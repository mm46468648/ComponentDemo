package com.mooc.studyproject.window

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.StudyprojectItemClockReminderBinding
//import kotlinx.android.synthetic.main.studyproject_item_clock_reminder.view.*

/**

 * @Author limeng
 * @Date 2021/7/16-2:50 PM
 */
@SuppressLint("ViewConstructor")
class ClockRemindPop(mContext: Context, private val isOrder: Boolean = false) : CenterPopupView(mContext) {
    var onSureClick: (() -> Unit)? = null
    override fun getImplLayoutId(): Int {
        return R.layout.studyproject_item_clock_reminder
    }

    lateinit var inflater: StudyprojectItemClockReminderBinding
    override fun onCreate() {
        super.onCreate()

        inflater = StudyprojectItemClockReminderBinding.bind(popupImplView)
        inflater.tvMiddle.text = if (isOrder) "取消订阅打卡提醒" else "订阅打卡提醒"
        inflater.tvTip.text = if (isOrder) "是否取消订阅打卡提醒?" else "是否确认订阅打卡提醒？"
        inflater.tvOk.setOnClickListener {
            onSureClick?.invoke()
        }
        inflater.tvCancel.setOnClickListener {
            dismiss()
        }

    }
}