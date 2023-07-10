package com.mooc.audio.ui.pop

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.core.BottomPopupView
import com.mooc.audio.R
import com.mooc.audio.databinding.AudioPopPlayTimeingSetBinding
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.ktextention.setDrawRight
//import kotlinx.android.synthetic.main.audio_pop_play_timeing_set.view.*

/**
 * 音频倍速选择弹窗
 */
class AudioSpeedPop(var mContext: Context) : BottomPopupView(mContext) {

    val speendList = arrayListOf<Float>(0.7f, 1.0f, 1.25f, 1.5f, 2.0f)
    val timeAdapter = SpeedAdapter(speendList)
    var onItemtSelect: ((s: Float) -> Unit)? = null


    var currentSelectSpeed = 1.0f //当前选中速度
        set(value) {
            field = value
            timeAdapter.notifyDataSetChanged()
        }

    override fun getImplLayoutId(): Int {
        return R.layout.audio_pop_play_timeing_set
    }

    lateinit var inflate : AudioPopPlayTimeingSetBinding
    override fun onCreate() {
        super.onCreate()

        inflate = AudioPopPlayTimeingSetBinding.bind(getPopupImplView())

        inflate.tvStop.visibility = View.GONE
        inflate.tvClose.visibility = View.GONE

        timeAdapter.setOnItemClickListener { adapter, view, position ->
            onItemtSelect?.invoke(speendList.get(position))
            dismiss()
        }
        inflate.rvTime.layoutManager = LinearLayoutManager(mContext)
        inflate.rvTime.adapter = timeAdapter
    }

    inner class SpeedAdapter(var list: ArrayList<Float>) : BaseQuickAdapter<Float, BaseViewHolder>(0, list) {

        override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

            val createTextView = createTextView(parent.context)
            return createBaseViewHolder(createTextView)
        }


        private fun createTextView(context: Context): TextView {
            val textView = TextView(context)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setSingleLine()
            textView.ellipsize = TextUtils.TruncateAt.END
            textView.setTextColor(ContextCompat.getColor(context, R.color.color_3))
            val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 43.dp2px())
//            layoutParams.leftMargin = 32.dp2px()
            textView.layoutParams = layoutParams
            return textView
        }

        override fun convert(holder: BaseViewHolder, item: Float) {
            val textColor = if (item == currentSelectSpeed) ContextCompat.getColor(context, R.color.colorPrimary) else ContextCompat.getColor(context, R.color.color_3)
            val textView = holder.itemView as TextView
            textView.setTextColor(textColor)
            textView.setText("x${item}")

            if (item == currentSelectSpeed) {
                textView.setDrawRight(R.mipmap.audio_ic_green_select)
            } else {
                textView.setDrawRight(null)
            }
        }
    }
}