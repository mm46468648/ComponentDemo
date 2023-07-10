package com.intelligent.reader.module.share.api.bean


import com.intelligent.reader.module.share.api.constans.ShareConstant

/**
 * 图片分享
 * Created by Administrator on 2019/11/22.
 */

class ShareContentPicture(override var pictureResource: String) : BaseShareContent() {

    override val shareWay: Int
        get() = ShareConstant.SHARE_WAY_PICTURE

    override val content: String = ""

    override val title: String = ""

    override val url: String = ""

    override fun miniProgramPagePath(): String = ""
}
