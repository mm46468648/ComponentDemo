package com.mooc.commonbusiness.utils

import android.app.Activity
import android.graphics.Bitmap
import android.text.TextUtils
import com.mooc.commonbusiness.R
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareConfig
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import java.io.File

class UMUtil {

    companion object {
        fun toAuthWeiXin(activity: Activity) {
            val config = UMShareConfig()
            config.isNeedAuthOnGetUserInfo(true)
            UMShareAPI.get(activity).setShareConfig(config)

            UMShareAPI.get(activity).getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, object : UMAuthListener {
                override fun onStart(p0: SHARE_MEDIA?) {

                }

                override fun onComplete(p0: SHARE_MEDIA?, p1: Int, p2: MutableMap<String, String>?) {

                }

                override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {

                }

                override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {

                }


            })
        }

        fun shareImg(context: Activity, bitmap: Bitmap, platform: SHARE_MEDIA) {
            val umImageI = UMImage(context, bitmap)

            //设置缩略图缩放
            val thum = UMImage(context, bitmap);
            thum.compressStyle = UMImage.CompressStyle.SCALE;
            umImageI.setThumb(thum)

            ShareAction(context).setPlatform(platform).withMedia(umImageI)
                    .setCallback(null).share()
        }


        fun shareWebPage(context: Activity, thumbId: String, url: String, title: String, des: String, platForm: SHARE_MEDIA) {
            var image = UMImage(context, thumbId)
            if(TextUtils.isEmpty(thumbId)){
                image = UMImage(context, R.mipmap.common_ic_share_logo)
            }
            val web = UMWeb(url)
            web.title = title //标题

            web.setThumb(image) //缩略图

            web.description = des //描述

            ShareAction(context)
                    .setPlatform(platForm)
                    .withMedia(web)
                    .setCallback(null)
                    .share()
        }


    }
}