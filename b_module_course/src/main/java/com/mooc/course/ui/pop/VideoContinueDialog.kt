package com.mooc.course.ui.pop

import android.content.Context
import android.view.LayoutInflater
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.course.R
import com.mooc.course.databinding.CoursePopConfirmCourseBinding
import com.mooc.course.databinding.CoursePopVideoContinueTipBinding
import com.mooc.course.model.VideoTipBean
//import kotlinx.android.synthetic.main.course_pop_video_continue_tip.view.*

class VideoContinueDialog(mContext:Context,var videoTipBean: VideoTipBean?
,var onCancleBack:(()->Unit)? = null,var  onContinueBack:(()->Unit)? = null) : CenterPopupView(mContext) {

    private lateinit var inflater: CoursePopVideoContinueTipBinding
    override fun getImplLayoutId(): Int {
        return R.layout.course_pop_video_continue_tip
    }

    override fun onCreate() {
        super.onCreate()
        inflater = CoursePopVideoContinueTipBinding.bind(popupImplView)
        videoTipBean?.let {
            inflater.tvMessage.text = it.message
            inflater.btCancle.text =  it.break_word
            inflater.btConfirm.text      = it.continue_word
        }


        inflater.btCancle.setOnClickListener {
            onCancleBack?.invoke()
            dismiss()
        }

        inflater.btConfirm.setOnClickListener {
            onContinueBack?.invoke()
            dismiss()
        }
    }
}