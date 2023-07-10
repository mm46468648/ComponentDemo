package com.mooc.commonbusiness.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.interfaces.AudioBottomPlayable
import com.mooc.commonbusiness.interfaces.StatusBarDarkable
import com.mooc.commonbusiness.manager.ShowAudioLayoutObserver
import com.mooc.commonbusiness.utils.graysetting.GrayModeSetting
import com.mooc.common.utils.statusbar.StatusBarUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.routeservice.StatisticsService

/**
 * Activity基类
 * 统一实现沉浸式状态栏
 * 统一实现底部音频播放浮窗
 *
 */
abstract class BaseActivity : AppCompatActivity(), AudioBottomPlayable, StatusBarDarkable {




    override fun onCreate(savedInstanceState: Bundle?) {

        StatusBarUtils.setTransparent(this)
        StatusBarUtils.setTextDark(this,darkMode())
        super.onCreate(savedInstanceState)
        GrayModeSetting.setGrayMode(window.decorView)
        lifecycle.addObserver(ShowAudioLayoutObserver(this))

    }




}