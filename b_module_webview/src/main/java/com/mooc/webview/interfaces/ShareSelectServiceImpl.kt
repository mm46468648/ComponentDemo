package com.mooc.webview.interfaces

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.mooc.common.ktextends.runOnMain
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSelectTextService
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.share.ShareSchoolUtil
import com.mooc.resource.widget.MoocImageView
import com.mooc.webview.R
import com.mooc.webview.viewmodel.ResourceWebViewModel
import org.json.JSONObject

/**
分享选中文字
 * @Author limeng
 * @Date 2021/5/14-11:17 AM
 */
@Route(path = Paths.SERVICE_SHARE_TEXT)
class ShareSelectServiceImpl : ShareSelectTextService {
    val shareService: ShareSrevice by lazy {
        ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
    }

    override fun shareSelectText(
        context: Context,
        json: JSONObject,
        callback: ((success: Boolean) -> Unit)?
    ) {
        val mViewModel =
            ViewModelProviders.of(context as FragmentActivity)[ResourceWebViewModel::class.java]
//
        //分享之前，要将内容，添加到学习室中
        //点击加入到学习室 AddToStudyZoneBottomDialog中逻辑
//        job = GlobalScope.launch(Dispatchers.Main) {
        runOnMain {
            mViewModel.addToFolder("0", json)
                .observe(context as AppCompatActivity, Observer { addfolderBean ->
                    if (!addfolderBean.success) { //失败，弹出失败提示
                        toast(addfolderBean.msg)
                        callback?.invoke(false)
                    } else {
                        val commonBottomSharePop = CommonBottomSharePop(context)
                        commonBottomSharePop.hideSchoolItem = false
                        commonBottomSharePop.onItemClick = { platform ->
                            if (platform == CommonBottomSharePop.SHARE_TYPE_SCHOOL) {//添加到学友圈
                                mViewModel.getNodeData(addfolderBean.resource_id)
                                    .observe(context as FragmentActivity, Observer {
                                        ShareSchoolUtil.postSchoolShare(
                                            context,
                                            ResourceTypeConstans.TYPE_NOTE.toString(),
                                            it?.id.toString(),
                                            it?.other_resource_id.toString()
                                        )

                                    })
                            } else {

                                Glide.with(context)
                                    .load(GlobalsUserManager.userInfo?.avatar)
                                    .transform(GlideTransform.centerCropAndRounder2)
                                    .into(object : CustomTarget<Drawable>() {
                                        override fun onResourceReady(
                                            resource: Drawable,
                                            transition: Transition<in Drawable>?
                                        ) {
                                            val build = IShare.Builder()
                                                .setSite(platform)
                                                .setTitle("")
                                                .setMessage("")
                                                .setImageBitmap(
                                                    getShareBitmap(
                                                        json.getString("content"),
                                                        context,
                                                        json.getString("title"),
                                                        resource.toBitmap()
                                                    )
                                                )
                                                .build()
                                            shareService.shareAddScore(
                                                ShareTypeConstants.TYPE_NOTE,
                                                context as FragmentActivity,
                                                build
                                            ) { score ->
                                                callback?.invoke(score == 1)
                                            }
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) {

                                        }

                                        override fun onLoadFailed(errorDrawable: Drawable?) {
                                            super.onLoadFailed(errorDrawable)

                                        }
                                    })


                            }
                        }
                        XPopup.Builder(context)
                            .setPopupCallback(object : SimpleCallback() {
                                override fun onDismiss(popupView: BasePopupView?) {
                                    super.onDismiss(popupView)
                                    callback?.invoke(true)
                                }
                            })
                            .asCustom(commonBottomSharePop)
                            .show()

                    }

                })
        }

//        }
    }


    override fun init(context: Context?) {

    }

    /**
     * 生成图片并分享
     */
    @SuppressLint("SetTextI18n", "InflateParams")
    private fun getShareBitmap(
        copyText: String,
        context: Context,
        titleStr: String,
        bitmap: Bitmap
    ): Bitmap {

        val shareView = LayoutInflater.from(context as FragmentActivity)
            .inflate(R.layout.webview_note_share, null)
        val title = shareView.findViewById(R.id.note_title) as TextView
        val content = shareView.findViewById(R.id.note_content) as TextView
        val belong = shareView.findViewById(R.id.note_belong) as TextView
        val name = shareView.findViewById(R.id.name) as TextView

        val header = shareView.findViewById(R.id.head) as MoocImageView
        header.setImageBitmap(BitmapUtils.makeRoundCorner(bitmap))

        name.setText(GlobalsUserManager.userInfo?.name + "  的笔记")
        belong.setText("")
        title.setText(titleStr)
        content.setText(copyText)
        //专栏文章有这个值
//        belong.text = if (TextUtils.isEmpty(bean?.recommend_title)) "" else "|  引自" + bean?.recommend_title.toString()

        val shareBitmap = BitmapUtils.createUnShowBitmapFromLayout(shareView)
        return shareBitmap
    }
}