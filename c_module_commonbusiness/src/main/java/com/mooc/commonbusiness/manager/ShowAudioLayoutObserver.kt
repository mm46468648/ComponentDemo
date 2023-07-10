package com.mooc.commonbusiness.manager

import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.interfaces.AudioBottomPlayable
import com.mooc.commonbusiness.route.routeservice.AudioFloatService
import java.lang.ref.WeakReference

/**
 * 展示底部音频服务的观察者
 */
class ShowAudioLayoutObserver : BaseActivityLifeObserver {

    var weakAudioBottomRef : WeakReference<AudioBottomPlayable>? = null
    constructor(activity: AudioBottomPlayable){
        weakAudioBottomRef = WeakReference(activity)
    }

    override fun onDestory() {

    }

    /**
     * onResume的时候
     * 通过音频模块的服务显示底部音频播放布局
     */
    override fun onResume() {
        val audioFloatService : AudioFloatService? = ARouter.getInstance().navigation(AudioFloatService::class.java)
        weakAudioBottomRef?.get()?.let {
            audioFloatService?.showAudioFloat(it)
        }
    }

}