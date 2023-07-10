package com.mooc.commonbusiness.utils


import android.graphics.Bitmap

interface IShare {


    companion object {
        const val SHARE_TYPE = "share_type" //分享多媒体类型
        const val SHARE_TYPE_DEFAULT = "share_type_default" //图文(网页)类型
        const val SHARE_TYPE_IMAGE = "share_type_image" //纯图片类型
        const val SHARE_TYPE_BITMAP = "share_type_bitmap" //纯图片Bitmap类型

        const val SHARE_SITE = "share_site" //分享地方(QQ,QZone,Wx,Wx_Circle)
        const val SHARE_SITE_WX = 0
        const val SHARE_SITE_WX_CIRCLE = 1
        const val SHARE_SITE_QQ = 2
        const val SHARE_SITE_QZONE = 3

        const val SHARE_TITLE = "share_title"
        const val SHARE_DESCRIPTION = "share_description"
        const val SHARE_IMAGE_PATH = "share_image_path"   //纯图分享路径
        const val SHARE_THUMB_IMAGE_PATH = "share_thumb_image_path"   //缩略图分享路径 (url,本地资源)
        const val SHARE_WEB_URL = "share_web_url"   //纯图分享路径

        const val WX_SHARE_RECEIVER_ACTION = "wx_auth_receiver_action" //微信认证action
        const val KEY_WX_SHARE_CALL_BACK = "key_wx_share_call_back" //微信分享回调

        // 权限申请code
        const val CONSTANCE_REQUESTPERMESSION_CODE = 100

    }

    /**
     *
     * @ProjectName:
     * @Package:
     * @ClassName:
     * @Description:    分享内容建造者
     * @Author:         xym
     * @CreateDate:     2020/9/15 3:00 PM
     * @UpdateUser:     更新者
     * @UpdateDate:     2020/9/15 3:00 PM
     * @UpdateRemark:   更新内容
     * @Version:        1.0
     */
    class Builder{
        @JvmField var title:String = ""
        @JvmField var message:String = ""
        @JvmField var webUrl:String = ""
        @JvmField var site:Int = 0
        @JvmField var mode:Int = -1     //分享模式，暂时没用  0网页，1纯图，还可支持多媒体，纯文字，微信小程序...等
        @JvmField var imageUrl:String = ""
        @JvmField var imageRes:Int = -1
        @JvmField var imageBitmap:Bitmap?=null
        @JvmField var thumbImagePath:Int = -1     //缩略图

        fun setTitle(title:String): Builder {
            this.title = title
            return this
        }
        fun setMessage(message:String): Builder {
            this.message = message
            return this
        }

        /**
         * 分享地方（QQ，QZONE，WX，WXCICLE。。）
         */
        fun setSite(site:Int): Builder {
            this.site = site
            return this
        }

        fun setWebUrl(webUrl:String): Builder {
            this.webUrl = webUrl
            return this
        }

        fun setImageUrl(imageUrl:String): Builder {
            this.imageUrl = imageUrl
            return this
        }

        fun setImageRes(imageRes:Int): Builder {
            this.imageRes = imageRes
            return this
        }

        fun setImageBitmap(imageBitmap: Bitmap): Builder {
            this.imageBitmap = imageBitmap
            return this
        }

        /**
         * 缩略图，用于微信分享图片获取不到，默认缩略图
         * @param thumbImagePath 缩略图资源id
         */
        fun setThumbImagePath(thumbImagePath:Int): Builder {

            return this
        }
        fun build(): Builder {
            return this
        }
    }
}