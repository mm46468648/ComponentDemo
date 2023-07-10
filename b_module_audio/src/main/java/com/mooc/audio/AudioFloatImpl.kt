package com.mooc.audio

import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.audio.ui.XimaAudioPlayBottomLayout
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.interfaces.AudioBottomPlayable
import com.mooc.commonbusiness.route.routeservice.AudioFloatService

/**
 * 音频播放底部浮窗
 */
@Route(path = Paths.SERVICE_AUDIO_FLOAT)
class AudioFloatImpl : AudioFloatService {
    override fun showAudioFloat(page: AudioBottomPlayable) {
        if(page is AppCompatActivity){
            XimaAudioPlayBottomLayout.show(page)
        }
    }

    override fun hide(page: AudioBottomPlayable, hide:Boolean) {
        if(page is AppCompatActivity){
            XimaAudioPlayBottomLayout.hide(page,hide)
        }
    }


    /**
     * 释放资源
     */
    override fun release() {
        XimaAudioPlayBottomLayout.release()
    }
}