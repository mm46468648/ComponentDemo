package com.mooc.studyroom.ui.pop

import android.content.Context
import android.view.LayoutInflater
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomPopRecordClearBinding

//import kotlinx.android.synthetic.main.studyroom_pop_record_clear.view.*

/**
 * 清除记录弹窗
 */
class ClearRecordPop(context:Context) : CenterPopupView(context) {

    override fun getImplLayoutId(): Int = R.layout.studyroom_pop_record_clear

    var onClickRight : (()->Unit)? = null

    lateinit var inflater: StudyroomPopRecordClearBinding
    override fun onCreate() {
        super.onCreate()

        inflater = StudyroomPopRecordClearBinding.bind(popupImplView)
        inflater.tvMessage.setText("是否清空历史记录")
        inflater.tvLeft.setText("取消")
        inflater.tvLeft.setOnClickListener { dismiss() }
        inflater.tvRight.setText("清空")
        inflater.tvRight.setOnClickListener {
            onClickRight?.invoke()
            dismiss()
        }
    }
}