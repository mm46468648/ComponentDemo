package com.mooc.commonbusiness.utils.graysetting

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.mooc.changeskin.SkinManager
import com.mooc.common.utils.sharepreference.SpUtils
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.constants.SpConstants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
设置灰度模式
 * @Author limeng
 * @Date 2021/4/13-11:12 AM
 */
class GrayModeSetting {
    companion object {
        var GRAY_MODE: Boolean = false
        var paint: Paint? = null

//        /**
//         * 获取是否是灰度模式
//         */
//        fun getMode(lifecycleOwner: LifecycleOwner, view: View) {
//            lifecycleOwner.lifecycle.addObserver(this)
//
//            launchJob = GlobalScope.launch {
//                try {
//                    val bean = HttpService.commonApi.getPlatformGray().await()
//                    if (bean.isSuccess) {
//                        if (bean.data != null) {
//                            GRAY_MODE = "true" == bean.data.results?.is_change_gray
////                            val skinName = SpUtils.get().getValue(SpConstants.SP_SKIN_Suffix, "")
//
//                            val skinName =
//                                when (bean.data.results?.topic_type) {
//                                    "2" -> "festival"//节日
//                                    "3" -> "pag"//党政
//                                    else -> ""//默认
//                                }
//                            SkinManager.getInstance().changeSkin(skinName)
//                        }
//                    }
//
//                    if (launchJob.isActive) {
//                        setGrayMode(view)
//
//                    }
//
//                } catch (e: Exception) {
//
//                }
//
//            }
//
//        }

        /**
         * 设置灰度模式
         */
        fun setGrayMode(view: View) {
            if (GRAY_MODE) {
                if (paint != null) {
                    view.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
                } else {
                    paint = Paint()
                    val cm = ColorMatrix()
                    cm.setSaturation(0f)
                    paint?.colorFilter = ColorMatrixColorFilter(cm)
                    view.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
                }

            }
        }
    }
}