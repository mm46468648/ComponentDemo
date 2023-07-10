package com.mooc.audio.ui.pop

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.core.BottomPopupView
import com.mooc.audio.R
import com.mooc.audio.databinding.AudioPopPlayTimeingSetBinding
import com.mooc.audio.manager.XiMaUtile
import com.mooc.common.ktextends.getColorRes
import com.mooc.resource.ktextention.dp2px
//import kotlinx.android.synthetic.main.audio_pop_play_timeing_set.view.*

/**
 * 音频定时设置浮窗
 */
class AudioTimingPop(var mContext:Context):BottomPopupView(mContext) {

    override fun getImplLayoutId(): Int {
        return R.layout.audio_pop_play_timeing_set
    }

    val timeArray = arrayListOf(10,20,30,60,90)
    var onTimeSelect : ((t: Long)->Unit)? = null   //直接回调转换成秒的值

    lateinit var inflate : AudioPopPlayTimeingSetBinding

    override fun onCreate() {
        super.onCreate()

        inflate = AudioPopPlayTimeingSetBinding.bind(popupImplView)
        val timeAdapter = TimeAdapter(timeArray)
        timeAdapter.setOnItemClickListener { adapter, view, position ->

            val get = timeArray.get(position)
            val time = get * 60L
            onTimeSelect?.invoke(time)
            dismiss()
        }
        inflate.rvTime.layoutManager = LinearLayoutManager(mContext)
        inflate.rvTime.adapter = timeAdapter


        inflate.tvStop.setOnClickListener {
            onTimeSelect?.invoke(0L)
            val tvStopColor = mContext.getColorRes(R.color.colorPrimary)
            inflate.tvStop.setTextColor(tvStopColor)
//            toast("停止计时")
            dismiss()
        }
        inflate.tvClose.setOnClickListener {
            dismiss()
        }


        val tvStopColor = if(XiMaUtile.getInstance().currentCloseTimerIndex == -1) mContext.getColorRes(R.color.colorPrimary) else mContext.getColorRes(R.color.color_3)
        inflate.tvStop.setTextColor(tvStopColor)


    }


    class TimeAdapter(list:ArrayList<Int>) : BaseQuickAdapter<Int,BaseViewHolder>(0,list){

        override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

            val createTextView = createTextView(parent.context)
            return createBaseViewHolder(createTextView)
        }
        override fun convert(holder: BaseViewHolder, item: Int) {
            (holder.itemView as TextView).text = "${item}分钟"

            val textColor = if(holder.layoutPosition == XiMaUtile.getInstance().currentCloseTimerIndex) context.getColorRes(R.color.colorPrimary) else context.getColorRes(R.color.color_3)
            (holder.itemView as TextView).setTextColor(textColor)
        }


        private fun createTextView(context: Context): TextView {
            val textView = TextView(context)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setSingleLine()
            textView.setTextColor(Color.parseColor("#333333"))
            textView.ellipsize = TextUtils.TruncateAt.END
            val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 54.dp2px())
//            layoutParams.leftMargin = 32.dp2px()
            textView.layoutParams = layoutParams
            return textView
        }
    }

}