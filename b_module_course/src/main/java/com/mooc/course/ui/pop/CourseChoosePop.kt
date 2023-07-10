package com.mooc.course.ui.pop

import android.content.Context
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import com.mooc.course.R
import com.lxj.xpopup.core.CenterPopupView

class CourseChoosePop(var mContext : Context) : CenterPopupView(mContext) {

    override fun getImplLayoutId(): Int {
        return R.layout.course_pop_confirm_course
    }

    var onConfirm : ((switchCompatSelect : Boolean)->Unit)? = null
    override fun onCreate() {
        super.onCreate()


        val switchCompat = findViewById<SwitchCompat>(R.id.switchButton)
        findViewById<Button>(R.id.btConfirm).setOnClickListener {
            onConfirm?.invoke(switchCompat.isChecked)
            dismiss()
        }

        findViewById<Button>(R.id.btCancle).setOnClickListener {
            dismiss()
        }
    }
}