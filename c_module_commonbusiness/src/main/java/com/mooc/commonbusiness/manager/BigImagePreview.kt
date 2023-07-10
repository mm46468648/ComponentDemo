package com.mooc.commonbusiness.manager

import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths

object BigImagePreview {

    var imageStringList = arrayListOf<String>()
    var currentPotion = 0
    var flag = 0 //是否显示分享
    fun setImageList(imageList: ArrayList<String>): BigImagePreview {
        imageStringList.clear()
        imageStringList.addAll(imageList)
        imageStringList.remove("")
        return this
    }


    fun setPosition(position: Int): BigImagePreview {
        this.currentPotion = position
        return this
    }

    fun setFlag(flag: Int): BigImagePreview {
        this.flag = flag
        return this
    }

    fun start() {
        //如果列表为空,则不展示
        if(imageStringList.isEmpty()) return

        ARouter.getInstance()
                .build(Paths.PAGE_BIG_IMAGE_PREVIEW)
                .withStringArrayList(IntentParamsConstants.COMMON_IMAGE_PREVIEW_LIST, imageStringList)
                .withInt(IntentParamsConstants.COMMON_IMAGE_PREVIEW_POSITION, currentPotion)
                .withInt(IntentParamsConstants.COMMON_IMAGE_PREVIEW_FLAG, flag)
                .navigation()

    }
}