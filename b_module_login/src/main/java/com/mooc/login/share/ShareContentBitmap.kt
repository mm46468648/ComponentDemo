package com.intelligent.reader.module.share.api.bean


import android.graphics.Bitmap
import com.intelligent.reader.module.share.api.constans.ShareConstant

/**
 * bitmap图片分享
 * Created by Administrator on 2019/11/22.
 */

class ShareContentBitmap(var bitmap: Bitmap) : BaseShareContent() {

    override val shareWay: Int
        get() = ShareConstant.SHARE_WAY_PICTURE

    override val content: String = ""

    override val title: String = ""

    override val url: String = ""
    override var pictureResource = ""

    override fun miniProgramPagePath(): String = ""


}
