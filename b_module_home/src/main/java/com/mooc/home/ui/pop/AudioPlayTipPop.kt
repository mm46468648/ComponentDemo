package com.mooc.home.ui.pop

import android.content.Context
import android.view.LayoutInflater
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.ktextends.dp2px
import com.mooc.home.R
import com.mooc.home.databinding.HomePopAudioPlayTipBinding
//import kotlinx.android.synthetic.main.home_pop_audio_play_tip.view.*

/**
 * 音频正在播放提示弹窗
 */
class AudioPlayTipPop(var mContext:Context,var dissmissCallBack : ((i:Int)->Unit)? = null) : CenterPopupView(mContext){

    var select = -1;         //点击关闭的类型  0最小化，1直接退出
    override fun getImplLayoutId(): Int {
        return R.layout.home_pop_audio_play_tip
    }

    private lateinit var inflater: HomePopAudioPlayTipBinding
    override fun getMaxWidth(): Int {
        return 263.dp2px()
    }
    override fun onCreate() {
        super.onCreate()

        inflater = HomePopAudioPlayTipBinding.bind(popupImplView)
        inflater.layoutTitleMore.setText(mContext.getString(R.string.track_dialog_quit))
        inflater.layoutCancelMore.setOnClickListener { dismiss() }
        inflater.layoutMinimizeMore.setOnClickListener {
            select = 0
            dismiss()
        }
        inflater.layoutQuitMore.setOnClickListener {
            select = 1
            dismiss()
        }



    }
    override fun onDismiss() {
        super.onDismiss()

        dissmissCallBack?.invoke(select)
    }
}