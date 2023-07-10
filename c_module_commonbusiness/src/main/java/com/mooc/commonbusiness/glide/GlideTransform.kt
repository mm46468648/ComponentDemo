package com.mooc.commonbusiness.glide

import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.mooc.common.ktextends.dp2px

object GlideTransform {

    @JvmField
    val centerCropAndRounder2 =
        MultiTransformation(CenterCrop(), RoundedCorners(2.dp2px()))
}