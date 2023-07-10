package com.mooc.studyroom.window

import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.toast
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomStudyplanCheckDialogBinding

//import kotlinx.android.synthetic.main.studyroom_studyplan_check_dialog.view.*

/**

 * @Author limeng
 * @Date 2021/4/26-5:17 PM
 */
class StudyPlanChekDialog(context: Context) : CenterPopupView(context) {
    var onOkClickListener:((b:Boolean)->Unit)?=null
    override fun getImplLayoutId(): Int {
        return R.layout.studyroom_studyplan_check_dialog
    }
    private lateinit var inflater: StudyroomStudyplanCheckDialogBinding

    override fun onCreate() {
        super.onCreate()

        inflater = StudyroomStudyplanCheckDialogBinding.bind(popupImplView)
        setContent()
    }

    private fun setContent() {
        val message = resources.getString(R.string.study_exit_dialog_title)
        val spannableString = spannableString {
            str = message
            colorSpan {
                color = ContextCompat.getColor(context, R.color.color_2)
                start = 1
                end = 23
            }
            colorSpan {
                color = ContextCompat.getColor(context, R.color.color_D37C00)
                start =24
                end = 28
            }
            colorSpan {
                color = ContextCompat.getColor(context, R.color.color_2)
                start = 29
                end = 42
            }
            colorSpan {
                color = ContextCompat.getColor(context, R.color.color_D37C00)
                start = 43
                end = message.length - 9
            }
            colorSpan {
                color = ContextCompat.getColor(context, R.color.color_2)
                start = message.length - 8
                end = message.length
            }
        }
        inflater.tvMsg.setText(spannableString)
        inflater.tvLeft.setOnClickListener {
            if (inflater.cbText.isChecked) {
                dismiss()
                onOkClickListener?.invoke(inflater.cbText.isChecked)
            } else {
                toast(context?.resources?.getString(R.string.text_str_cb_remind))
            }

        }
        inflater.tvRight.setOnClickListener {
            dismiss()
        }
    }
}