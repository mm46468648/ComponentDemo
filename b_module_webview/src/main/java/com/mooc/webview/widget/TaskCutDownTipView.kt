package com.mooc.webview.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.webview.R
import com.mooc.webview.databinding.WebviewViewTaskCutdownBinding
import pl.droidsonroids.gif.GifImageView

/**
 * 任务倒计时提示控件
 */
class TaskCutDownTipView  @JvmOverloads constructor(var mContext: Context, attributes: AttributeSet? = null)
    : FrameLayout(mContext, attributes){

        var inflate = WebviewViewTaskCutdownBinding.inflate(LayoutInflater.from(context),this,true)


//    /**
//     * 设置当前进度百分比
//     * (最大100%)
//     */
//    fun setPercent(p : Int){
//        inflate.circleProgressView.setmCurrent(p)
//    }

    fun setCutdownTime(t:Int){
        //将毫秒值转换为时分秒
        val formatAudioPlayTime = TimeFormatUtil.formatAudioPlayTime(t.toLong())
        inflate.tvTime.setText(formatAudioPlayTime.toString())
    }
}