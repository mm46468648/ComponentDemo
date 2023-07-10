package com.mooc.commonbusiness.module.web

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.webkit.JavascriptInterface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.runOnMain
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.Md5Util
import com.mooc.common.utils.aesencry.AesEncryptUtil
import com.mooc.common.utils.permission.PermissionRequestCallback
import com.mooc.common.utils.permission.PermissionUtil
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.BigImagePreview
import com.mooc.commonbusiness.net.EncryptBodyBean
import com.mooc.commonbusiness.net.EncryptParseData
import com.mooc.commonbusiness.net.EncryptToken
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


/**
 * 手机端web交互接口
 * 挂载在mobile对象下
 */
open class MobileWebInterface(var activity: Activity) {

    private var ts: Long = 0
    private var aesToken: String? = ""

    /**
     * 退出页面
     */
    @JavascriptInterface
    fun exit() {
        activity.finish()
    }

    /**
     * 测试卷通过按钮，跳转回学习项目页面
     */
    @JavascriptInterface
    fun jsToSign() {
        ARouter.getInstance().build(Paths.PAGE_STUDYPROJECT).navigation()
    }

    /**
     * 智能导学测试卷通过按钮，跳转回学习项目页面
     */
    @JavascriptInterface
    fun toIntelligentStudyProject(planId: String) {
        ARouter.getInstance()
                .build(Paths.PAGE_STUDYPROJECT)
                .withString(IntentParamsConstants.STUDYPROJECT_PARAMS_ID, planId)
                .navigation()
        activity.finish()
    }


    /**
     * 分享
     * @param shareJson
     * title : 值得回忆的不仅是成长
     * link : http://moocnd.t.esile.me?is_share=1
     * imgUrl : https://moocnd.oss-cn-shanghai.aliyuncs.com/moocnd-logo-big@3x.png
     * desc : 军职在线[时光机]即将起航，我是笫1位登机者，你也一起来吧
     */
    @JavascriptInterface
    fun shareData(shareJson: String) {

        val json = JSONObject(shareJson)
        var shareUrl = json.get("link") as String
        var shareTitle = json.get("title") as String
        var shareDesc = json.get("desc") as String
        val sharePicture = json.get("imgUrl") as String


        if(!TextUtils.isEmpty(shareUrl)){
            //拼接用户信息
            shareUrl =
                UrlUtils.appendParams(shareUrl, "user_id", GlobalsUserManager.uid, "is_app_share", "1")
            if (!TextUtils.isEmpty(shareTitle) && shareTitle.contains("\${user}")) {
                shareTitle = shareTitle.replace("\${user}", GlobalsUserManager.userInfo?.name ?: "")
            }
            if (!TextUtils.isEmpty(shareDesc) && shareDesc.contains("\${user}")) {
                shareDesc = shareDesc.replace("\${user}", GlobalsUserManager.userInfo?.name ?: "")
            }
        }


        this.runOnMain {
            XPopup.Builder(activity).asCustom(
                CommonBottomSharePop(activity, {
                    val shareService =
                        ARouter.getInstance().build(Paths.SERVICE_SHARE)
                            .navigation() as ShareSrevice
                    val build = IShare.Builder()
                        .setSite(it)
                        .setTitle(shareTitle)
                        .setMessage(shareDesc)
                        .setImageUrl(sharePicture)
                        .setWebUrl(shareUrl)
                        .build()
                    shareService.share(activity, build)
                })
            ).show()
        }
    }

    private fun buildAesToken(token: String, ts: Long): String {
        var tokenStr = token
        val encryptToken = EncryptToken()
        if (tokenStr.startsWith("JWT ")) {
            tokenStr = tokenStr.substring(3).trim { it <= ' ' }
        }
        encryptToken.jwt = tokenStr
        encryptToken.ts = ts
        val gson = Gson()
        return try {
            AesEncryptUtil.encrypt(gson.toJson(encryptToken))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @JavascriptInterface
    fun encryptHeaders(): String {
        ts = System.currentTimeMillis()
        aesToken = buildAesToken(getToken(), ts)
        return aesToken ?: ""
    }

    @JavascriptInterface
    fun encryptParams(str: String): String {
        val requestData: String
        val dataBeanX = EncryptBodyBean.DataBeanX()
        //加密数据赋值
        dataBeanX.data = str
        dataBeanX.client_type = 2 //APP是1  h5是2
        dataBeanX.token_check = aesToken?.let { signByMd5(it) }
        dataBeanX.ts = ts
        requestData = dataBeanX.toString().trim()
        var body: String? = null
        try {
            body = AesEncryptUtil.encrypt(requestData)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val encryptBodyBean = EncryptBodyBean()
        encryptBodyBean.data = body
        val gson = Gson()

        return gson.toJson(encryptBodyBean)
    }


    @JavascriptInterface
    fun decryptData(bodyStr: String): String {
        val gson = Gson()
        val parseData: EncryptParseData = gson.fromJson(bodyStr, EncryptParseData::class.java)
        val data: String = parseData.data
        return AesEncryptUtil.decrypt(data)
    }

    fun signByMd5(vararg str: String): String {
        val md: MessageDigest = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return ""
        }
        for (temp in str) {
            md.update(temp.toByteArray())
        }
        return String.format("%032x", BigInteger(1, md.digest()))
    }

    @JavascriptInterface
    fun getToken(): String {
        if (GlobalsUserManager.isLogin()) {
            var token: String = GlobalsUserManager.appToken
            if (token.startsWith("JWT ")) {
                token = token.substring(3).trim { it <= ' ' }
            }
            return token
        }
        return ""
    }

    /**
     * h5中跳转协议
     */
    @JavascriptInterface
    open fun linkTo(url: String) {
        if (TextUtils.isEmpty(url)) return
        if (url.startsWith("customize://") || url.startsWith("moocnd://")) {
            val urlOverrideUtil = UrlOverrideUtil()
            urlOverrideUtil.loadStudyResource(url)
        }
    }

    /**
     * 检测是否有录音权限
     */
    @JavascriptInterface
    open fun checkAudioPermisson(): Boolean {
        return PermissionUtil.hasPermissionRequest(activity, Manifest.permission.RECORD_AUDIO)
    }

    /**
     * h5申请录音权限
     */
    @JavascriptInterface
    open fun requsetAudioPermission() {
        PermissionApplyActivity.launchActivity(
                activity, arrayOf(Manifest.permission.RECORD_AUDIO), 0,
                object : PermissionRequestCallback {
                    override fun permissionSuccess() {

                    }

                    override fun permissionCanceled() {
                        
                    }

                    override fun permissionDenied() {

                    }
                })

    }

    /**
     * H5控制是否关闭大图注入
     * 因为有些页面不想要大图预览
     */
    @JavascriptInterface
    open fun closeBigImageInject(c : Boolean){
        InjectJsManager.closeInject = c
    }

    @JavascriptInterface
    open fun downloadFiles(json: Array<String?>?) {
        if (json != null && json.size > 0) {

            if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        ), PermissionApplyActivity.REQUEST_CODE_DEFAULT
                )
                return
            }
            val currentImgUrl = json.get(0) ?: ""
            DownloadImageUtil.download(activity,currentImgUrl)
//            //base64
//            if (currentImgUrl.startsWith("data:image")) {
//                val images = currentImgUrl.split(",".toRegex()).toTypedArray()
//                if (images.size > 1) {
//                    downloadBase64(images[1])
//                }
//                return
//            }
//            //http
////        val file: File? = FileMgr.newDownLoadImgFile(DIR_EVERY_DAY_READ_CACHE_PATH, Md5Util.getMD5Str(currentImgUrl).toString() + ".png")
//            val folder = File(DownloadConfig.imageLocation)
//            if (!folder.exists()) {
//                folder.mkdirs()
//            }
//
//            val file = File(
//                    DownloadConfig.imageLocation,
//                    Md5Util.getMD5Str(currentImgUrl).toString() + ".png"
//            )
//            if (file.exists()) {
//                toast("图片已保存至" + file.path)
//                return
//            }
//
//            //下载图片
//            GlobalScope.launch(Dispatchers.IO) {
//                Glide.with(activity).downloadOnly().load(json.get(0))
//                        .into(object : CustomTarget<File>() {
//                            override fun onResourceReady(
//                                    resource: File,
//                                    transition: Transition<in File>?
//                            ) {
//                                resource.let {
//                                    resource.copyTo(it, true)
//                                    sendBroadcast(activity, resource)
//                                    toast("图片已保存至" + resource.path)
//                                }
//                            }
//
//                            override fun onLoadCleared(placeholder: Drawable?) {
//
//                            }
//                        })
//            }

        }
    }


    /**
     * 下载base64
     */
    private fun downloadBase64(base64: String) {
//        val bitmap: Bitmap = BitmapTools.base64ToBitmap(base64)
//
//        val folder = File(DownloadConfig.imageLocation)
//        if (!folder.exists()) {
//            folder.mkdirs()
//        }
//        val file = File(DownloadConfig.imageLocation, Md5Util.getMD5Str(base64).toString() + ".png")
//
//        if (file.exists()) {
//            toast("图片已保存至" + file.path)
//            return
//        }
//        val path: String = BitmapTools.saveBitmapToSDCard(activity, bitmap)
//        val file1 = File(path)
//        toast("图片已保存至" + file.path)
//        sendBroadcast(activity, file1)
//        BitmapTools.deleteDir(BitmapTools.getDirectoryPath());
    }

    /**
     * 大图预览
     */
    @JavascriptInterface
    fun openImage(position: Int, images: Array<String>, baseUrl: String) {

        val currentUrl = images.get(position)
        //对图片地址进行转换
        val imageStrArrayList = images.map {
            getImageUrl(it, baseUrl)
        }.filter {
            it.isNotEmpty()
        }.filter { //对不是图片的地址进行过滤
            it.startsWith("data:image") || it.endsWith("jpg") || it.endsWith("png")
                    || it.endsWith("jpeg") || it.endsWith("JPG")
                    || it.endsWith("PNG") || it.endsWith("JPEG")
        }

        //选中当前图片位置
        var choosePosition = 0;
        imageStrArrayList.forEachIndexed { index, s ->
            if (currentUrl == s) {
                choosePosition = index
            }
        }

        BigImagePreview
                .setPosition(choosePosition)
                .setImageList(imageStrArrayList as ArrayList<String>)
                .start()


    }


    /**
     * 对url进行处理
     * 如果是base64，或者http开头直接返回
     */
    private fun getImageUrl(originUrl: String, strBaseUrl: String): String {
        if (originUrl.startsWith("data:image") || originUrl.startsWith("http")) return originUrl

        if (originUrl.startsWith("//")) {
            return "http:${originUrl}"
        }
        if (!TextUtils.isEmpty(strBaseUrl)) {
            return "${strBaseUrl}/${originUrl}"
        }
        return originUrl
    }

    /**
     * 跳转到证书申请页面
     */
    @JavascriptInterface
    open fun toCertificatePage(id:String){
        ARouter.getInstance().build(Paths.PAGE_APPLYCER_INPUT)
            .withString(IntentParamsConstants.INTENT_CERTIFICATE_ID, id)
            .withString(IntentParamsConstants.PARAMS_RESOURCE_TITLE, "").navigation()
    }
}