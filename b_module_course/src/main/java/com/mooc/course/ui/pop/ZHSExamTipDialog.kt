package com.mooc.course.ui.pop

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.mooc.course.R
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.dp2px
import com.mooc.course.databinding.CoursePopZhsExamTipBinding
//import kotlinx.android.synthetic.main.course_pop_zhs_exam_tip.view.*

class ZHSExamTipDialog(mContext : Context): CenterPopupView(mContext) {

    override fun getImplLayoutId(): Int {
        return R.layout.course_pop_zhs_exam_tip
    }

    var onConfirm: (()->Unit)? = null
    var type : String = "1"    //提示类型，0未提交 ,1已提交
    var hasTip = "您有 1 次考试机会，开始后将进入倒计时，退出考试页面计时不会暂停，请确认是否参加考试？"
    var tip = "本课程只有一次考试机会，您已参加过考试。"

    private lateinit var inflater: CoursePopZhsExamTipBinding
    override fun onCreate() {
        super.onCreate()

        inflater = CoursePopZhsExamTipBinding.bind(popupImplView)
        if("0" == type){
            val spannableString = spannableString {
                str = hasTip
                colorSpan {
                    start = hasTip.indexOf("1")
                    end = hasTip.indexOf("1") + 1
                    color = Color.parseColor("#FA3A3A")
                }
                absoluteSpan {
                    size = 17.dp2px()
                    start = hasTip.indexOf("1")
                    end = hasTip.indexOf("1") + 1
                }
            }
            inflater.tvMessage.text = spannableString
        }else{
            inflater.btSingleConfirm.visibility = View.VISIBLE
            inflater.btCancle.visibility = View.GONE
            inflater.btConfirm.visibility = View.GONE
            inflater.tvMessage.text = tip
        }


        inflater.btConfirm.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }

        inflater.btSingleConfirm.setOnClickListener { dismiss() }
        inflater.btCancle.setOnClickListener { dismiss() }
    }
}