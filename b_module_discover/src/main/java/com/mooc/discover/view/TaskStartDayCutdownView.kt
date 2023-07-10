package com.mooc.discover.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.mooc.discover.R
import com.mooc.discover.databinding.LayoutTabMutualTaskBinding
import com.mooc.discover.databinding.LayoutTaskStartdayCutdownBinding
//import kotlinx.android.synthetic.main.layout_task_startday_cutdown.view.*

/**
 * 距离任务开始还有多长时间
 * 倒计时控件
 */
class TaskStartDayCutdownView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var heighLightBgRes = R.drawable.shape_radius2_white
        set(value) {
            field = value
            _initBg()
        }

    private var inflater: LayoutTaskStartdayCutdownBinding =
        LayoutTaskStartdayCutdownBinding.inflate(LayoutInflater.from(context),this,true)

    init {
//        LayoutInflater.from(context).inflate(R.layout.layout_task_startday_cutdown,this)
    }

    private fun _initBg() {
        inflater.tvDay1.setBackgroundResource(heighLightBgRes)
        inflater.tvDay2.setBackgroundResource(heighLightBgRes)
        inflater.tvHour1.setBackgroundResource(heighLightBgRes)
        inflater.tvHour2.setBackgroundResource(heighLightBgRes)
        inflater.tvMin1.setBackgroundResource(heighLightBgRes)
        inflater.tvMin2.setBackgroundResource(heighLightBgRes)
    }

    /**
     * 设置倒计时时间
     */
    fun bindCutDownTime(timedeltaRemain : String) {
        val split = timedeltaRemain.split("-")
        if (split.size == 3) {     //3个才展示
            val day = split[0]
            //0天全部隐藏天相关控件
            if ("00".equals(day) || "0".equals(day)) {
                inflater.tvDay1.visibility = View.GONE
                inflater.tvDay2.visibility = View.GONE
                inflater.tvDay3.visibility = View.GONE
            } else if (day.length >= 2) {
                inflater.tvDay1.visibility = View.VISIBLE
                inflater.tvDay1.setText(day[0].toString())
                inflater.tvDay2.setText(day[1].toString())
            } else {
                inflater.tvDay1.visibility = View.GONE
                inflater.tvDay2.setText(day)
            }

            //0小时全部隐藏小时相关控件
            val hour = split[1]
            if ("00".equals(hour) || "0".equals(hour)) {
                inflater.tvHour1.visibility = View.GONE
                inflater.tvHour2.visibility = View.GONE
                inflater.tvHour3.visibility = View.GONE
            } else if (hour.length <= 1) {
                inflater.tvHour1.setText("0")
                inflater.tvHour2.setText(hour)
            } else {
                inflater.tvHour1.setText(hour[0].toString())
                inflater.tvHour2.setText(hour[1].toString())
            }

            val minute = split[2]
            if (minute.length <= 1) {
                inflater.tvMin1.setText("0")
                inflater.tvMin2.setText(minute)
            } else {
                inflater.tvMin1.setText(minute[0].toString())
                inflater.tvMin2.setText(minute[1].toString())
            }
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
        }
    }
}