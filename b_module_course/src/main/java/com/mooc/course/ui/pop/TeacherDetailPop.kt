package com.mooc.course.ui.pop

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.mooc.course.R
import com.mooc.course.model.StaffInfo
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.resource.widget.MoocImageView

class TeacherDetailPop(var mContext:Context,var teacher:StaffInfo) : CenterPopupView(mContext){

    override fun getImplLayoutId(): Int {
        return R.layout.course_pop_detail_teacher
    }

    override fun onCreate() {
        super.onCreate()

        findViewById<MoocImageView>(R.id.mivTeacherHead).setImageUrl(teacher.avatar,true)

        findViewById<TextView>(R.id.tvName).setText(teacher.name)
        findViewById<TextView>(R.id.tvDesc).setText(teacher.about)
        findViewById<ImageView>(R.id.iv_close).setOnClickListener { dismiss() }
    }
}