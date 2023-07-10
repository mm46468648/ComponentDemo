package com.mooc.login.share

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.intelligent.reader.module.share.api.bean.BaseShareContent
import com.intelligent.reader.module.share.api.bean.ShareContentBitmap
import com.intelligent.reader.module.share.api.bean.ShareContentPicture
import com.intelligent.reader.module.share.api.constans.ShareConstant
import com.intelligent.reader.module.share.api.constans.ShareConstant.THUMB_SIZE
import com.mooc.commonbusiness.utils.IShare
import com.mooc.login.R
import com.mooc.login.manager.WechatManager
import com.mooc.common.excutor.DispatcherExecutor
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.commonbusiness.utils.IShare.Companion.SHARE_SITE_WX
import com.mooc.commonbusiness.utils.IShare.Companion.SHARE_SITE_WX_CIRCLE
import com.mooc.commonbusiness.utils.UMUtil
import com.mooc.commonbusiness.utils.format.HtmlFromatUtil
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.umeng.socialize.ShareContent
import com.umeng.socialize.bean.SHARE_MEDIA
import java.io.File


/**
 * 分享管理类
 * 示例：
 *  var shareManager = ShareManager()
var build = IShare.Builder()
.setSite(IShare.SHARE_SITE_WX)
.setWebUrl("https://www.baidu.com")
.setTitle("百度一下，你就知道")
.setMessage("赚钱养梦，音乐不死")
.setImageUrl(imageUrl).build()
shareManager.share(this,build)
 *
 *  todo 1.QQ分享如果要获取file类型图片,必须将图片复制到外部存储卡，才能使得QQ读取到文件
 *  可利用FileUtils。copyFile
 *  todo 2.当QQ分享完毕后，清空用户复制到外部存储卡的空间，
 *  todo 3.外部存储路径，通过统一管理类获取
 */

class ShareManager {

    lateinit var shareActivity: Activity

    //总分享回调 (0失败,1成功,2取消)
    var shareCallBack: ((status: Int) -> Unit)? = null
    var wxShareReceived = false // 微信分享是否已回调（防止接收多次回调）

    private val wxShareReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(activity: Context, intent: Intent) {
                val shareSuccess = intent.getBooleanExtra(IShare.KEY_WX_SHARE_CALL_BACK, false)

                shareCallBack?.apply {
                    if (!wxShareReceived) {
                        this.invoke(if (shareSuccess) 1 else 0)
                        wxShareReceived = true
                    }
                    shareComplete()
                }
            }
        }
    }

    /**
     * 前期由于BaseShareContent，不能暴露给其他module
     * 暂时使用的时候只能通过bundle传递基本类型，再组装成BaseShareContent
     * 但是分享纯图需要支持bitmap类型分享，不能通过Bundle传递，单起一个方法，
     * 改为建造者模式，支持纯bitmap类型分享
     * 后变慢慢移除调bundle传递参数方式
     */
    fun share(activity: Activity, builder: IShare.Builder, shareCallBack: ((status: Int) -> Unit)? = null) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) || !ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                toast("请在手机设置打开存储权限,才能进行分享")
                return
            }
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), IShare.CONSTANCE_REQUESTPERMESSION_CODE)
            return
        }
        shareActivity = activity
        wxShareReceived = false
        shareCallBack?.apply { this@ShareManager.shareCallBack = this }

        // 1.创建分享内容
        val makeShareContent = makeShareContent(builder)

        val shareSite = builder.site
        if (shareSite == IShare.SHARE_SITE_WX || shareSite == IShare.SHARE_SITE_WX_CIRCLE) {
            initWXShareReceiver()
        }
        when (shareSite) {
//            IShare.SHARE_SITE_QQ -> shareByQQ(shareActivity, makeShareContent, ShareConstant.SHARE_TYPE_QQ_SESSION)
//            IShare.SHARE_SITE_QZONE -> shareByQQ(shareActivity, makeShareContent, ShareConstant.SHARE_TYPE_QQ_QZONE)
            IShare.SHARE_SITE_WX -> shareByWx(shareActivity, makeShareContent, ShareConstant.SHARE_TYPE_WECHAT_SESSION)
            IShare.SHARE_SITE_WX_CIRCLE -> shareByWx(shareActivity, makeShareContent, ShareConstant.SHARE_TYPE_WECHAT_FRENDS_GROUP)
        }
    }


    /**
     * 根据Builder创建分享内容
     * @param builder
     */
    fun makeShareContent(builder: IShare.Builder): BaseShareContent {
        lateinit var shareContent: BaseShareContent


        val shareTitle = builder.title
        val shareMessage = builder.message
        val webUrl = builder.webUrl
        val imageUrl = builder.imageUrl
        val shareBitmap = builder.imageBitmap
        val thumbImageResource = if (builder.thumbImagePath != -1) builder.thumbImagePath else R.mipmap.ic_share_logo     //缩略图,暂时只支持本地资源

        val shareType = when {
            webUrl.isNotEmpty() -> IShare.SHARE_TYPE_DEFAULT
            imageUrl.isNotEmpty() -> IShare.SHARE_TYPE_IMAGE        //如果链接为空，但是图片不为空，则为纯图分享
            shareBitmap != null -> IShare.SHARE_TYPE_BITMAP
            else -> IShare.SHARE_TYPE_DEFAULT
        }
//        val thumbImageResource = params.getInt(IShare.SHARE_THUMB_IMAGE_PATH, R.mipmap.module_icon_share_default)       //缩略图,暂时只支持本地资源

        when (shareType) {
            IShare.SHARE_TYPE_DEFAULT -> {
                shareContent = ShareContentWebpage(shareTitle, shareMessage, webUrl, imageUrl, thumbImageResource)
            }
            IShare.SHARE_TYPE_IMAGE -> {
                shareContent = ShareContentPicture(imageUrl)
            }
            IShare.SHARE_TYPE_BITMAP -> {
                shareContent = shareBitmap?.let {
                    ShareContentBitmap(it)
                }!!
            }
            else -> {
            }
        }
        return shareContent
    }

    fun shareByWx(activity: Activity, shareContent: BaseShareContent, shareType: Int) {
        when (shareContent.shareWay) {
            ShareConstant.SHARE_WAY_PICTURE -> {
                if (shareContent is ShareContentBitmap) {
                    //如果是bitmap类型，直接分享，微信支持bitmap类型
                    sharePictureWX(activity, shareContent, shareType, null)
                    return
                }

                //使用Glide下载
                //如果是url类型，则需要下载成file文件，微信转成bitmap，QQ，复制到外部存储卡
                DispatcherExecutor.getIOExecutor().submit(Runnable {
                    AppGlobals.getApplication()?.let {
                        Glide.with(it).downloadOnly().load(shareContent.pictureResource).into(object : CustomTarget<File>() {
                            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                                sharePictureWX(activity, shareContent, shareType, resource)
                            }

                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                shareWebPageWX(activity, shareContent, shareType, null)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                    }
                })

            }
            ShareConstant.SHARE_WAY_WEBPAGE -> {
                //使用Glide下载
                //如果是url类型，则需要下载成file文件，微信转成bitmap，QQ，复制到外部存储卡
                DispatcherExecutor.getIOExecutor().submit(Runnable {
                    AppGlobals.getApplication()?.let {
                        Glide.with(it).downloadOnly().load(shareContent.pictureResource).into(object : CustomTarget<File>() {
                            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                                shareWebPageWX(activity, shareContent, shareType, resource)
                            }

                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                shareWebPageWX(activity, shareContent, shareType, null)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                    }
                })
            }
        }
    }

    /**
     * 分享微信链接
     */
    private fun shareWebPageWX(activity: Activity, shareContent: BaseShareContent, shareType: Int, file: File?) {
//        var mWXApi = WechatManager.api
//
//        val webpage = WXWebpageObject()
//        webpage.webpageUrl = shareContent.url
//        val msg = WXMediaMessage(webpage)
//        msg.title = shareContent.title
//        msg.description = shareContent.content
//        msg.thumbData = if (file == null) {      //缩略图
//            BitmapUtils.bmpToByteArray(getDefaultThumbnail(activity, R.mipmap.ic_share_launcher))
//        } else {        //网页类型的缩略图，需限制宽高
//            BitmapUtils.bmpToByteArray(ShareUtil.decodeSampledBitmapFromFile(file.absolutePath,THUMB_SIZE,THUMB_SIZE))
//            BitmapUtils.bmpToByteArray(BitmapUtils.compressBitmapFromFileByWidth(file.absolutePath, THUMB_SIZE, THUMB_SIZE))
//        }
//        val req = SendMessageToWX.Req()
//        req.transaction = buildTransaction("webpage")
//        req.message = msg
//        req.scene = shareType


        var platform: SHARE_MEDIA = SHARE_MEDIA.WEIXIN;
        if (shareType == SHARE_SITE_WX_CIRCLE) {
            platform = SHARE_MEDIA.WEIXIN_CIRCLE
        }

        val shareDesc = HtmlFromatUtil.html2Text(shareContent.content)
//            if(TextUtils.isEmpty(shareContent.content)){
//                "\r\n"
//            }else{
//                HtmlUtils.fromHtml(shareContent.content).toString()
//            }
//        mWXApi?.sendReq(req)
        UMUtil.shareWebPage(activity, shareContent.pictureResource, shareContent.url, shareContent.title, shareDesc, platform)
    }

    /**
     * 分享微信纯图片
     * @param file 之所以传文件，是为了兼容QQ分享时，分享的是文件在sdcard的路径
     */
    private fun sharePictureWX(activity: Activity, shareContent: BaseShareContent, shareType: Int, file: File?) {
//        val mWXApi = WechatManager.api

        val bitmap = when {
            shareContent is ShareContentBitmap -> shareContent.bitmap
//            file != null-> ShareUtil.decodeSampledBitmapFromFile(file.absolutePath, ShareConstant.THUMB_SIZE, ShareConstant.THUMB_SIZE)
            file != null -> BitmapFactory.decodeFile(file.absolutePath)
            else -> getDefaultThumbnail(activity, R.mipmap.ic_share_launcher)    //获取默认图片
        }
//        val imgObj = WXImageObject(bitmap)
//        val msg = WXMediaMessage()
//        msg.mediaObject = imgObj
        //纯图类型缩略图，需对质量进行压缩
//        msg.thumbData =  ShareUtil.decodeSampledBitmap(bitmap, ShareConstant.THUMB_SIZE_LIMIT)
//        msg.thumbData = BitmapUtils.compressPicturethumbData(bitmap)
//        val req = SendMessageToWX.Req()
//        req.transaction = buildTransaction("imgshareappdata")
//        req.message = msg
//        req.scene = shareType
        var platform: SHARE_MEDIA = SHARE_MEDIA.WEIXIN;
        if (shareType == SHARE_SITE_WX_CIRCLE) {
            platform = SHARE_MEDIA.WEIXIN_CIRCLE
        }

        UMUtil.shareImg(activity, bitmap, platform)
//        mWXApi.sendReq(req)
//        if (bitmap != null) {
//            if (!bitmap.isRecycled()) {
//                bitmap.recycle()
//            }
//        }
    }

    /**
     * 构建微信分享Tracnsaction字段
     * 每次都不能重复
     */
    fun buildTransaction(type: String = ""): String {
        return "$type + ${System.currentTimeMillis()}"
    }

    /**
     * 获取默认缩略图
     *
     * @return
     */
    fun getDefaultThumbnail(activity: Activity, defaultThumbnail: Int): Bitmap {
//        return (ContextCompat.getDrawable(activity,R.mipmap.ic_launcher) as BitmapDrawable).bitmap
        return BitmapFactory.decodeResource(activity.resources, defaultThumbnail)
    }

    /**
     * 初始化微信分享回调广播
     */
    private fun initWXShareReceiver() {
        LocalBroadcastManager.getInstance(shareActivity).registerReceiver(wxShareReceiver, IntentFilter(IShare.WX_SHARE_RECEIVER_ACTION))
    }

    fun shareComplete() {
        if (::shareActivity.isInitialized) {
            LocalBroadcastManager.getInstance(shareActivity).unregisterReceiver(wxShareReceiver)
        }
    }

    /**
     *  分享回调广播
     */
    companion object {
        fun sendShareBackBroadcast(success: Boolean) {
            val intent = Intent(IShare.WX_SHARE_RECEIVER_ACTION)
            intent.putExtra(IShare.KEY_WX_SHARE_CALL_BACK, success)
            AppGlobals.getApplication()?.let {
                LocalBroadcastManager.getInstance(it).sendBroadcast(intent)
            }
        }
    }

}