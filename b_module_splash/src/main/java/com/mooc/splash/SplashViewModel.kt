package com.mooc.splash

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.mooc.changeskin.SkinManager
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.utils.graysetting.GrayModeSetting
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel() {

    /**
     * 获取是否是灰度模式
     */
    fun getMode(view: View) {

        launchUI {
            val bean = HttpService.commonApi.getPlatformGray().await()
            if (bean.isSuccess) {
                if (bean.data != null) {
                    GrayModeSetting.GRAY_MODE = "true" == bean.data.results?.is_change_gray
//                            val skinName = SpUtils.get().getValue(SpConstants.SP_SKIN_Suffix, "")

                    val skinName =
                        when (bean.data.results?.topic_type) {
                            "2" -> "festival"//节日
                            "3" -> "pag"//党政
                            else -> ""//默认
                        }
                    SkinManager.getInstance().changeSkin(skinName)
                }
            }

            if (view!=null) {
                GrayModeSetting.setGrayMode(view)
            }
        }
    }
}