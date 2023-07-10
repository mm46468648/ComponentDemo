package com.mooc.home.ui.pop

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.lxj.xpopup.impl.PartShadowPopupView
import com.mooc.home.R
import com.mooc.home.databinding.HomePopMoocCourseFilterBinding
//import kotlinx.android.synthetic.main.home_pop_mooc_course_filter.view.*

class CourseFilterAttachPop(var mContext: Context) : PartShadowPopupView(mContext) {

    /*
   * is_free 是否免费  0 付费 1 免费
   *verified_active 是否有证书 0 无证书 1 有证书
   *is_have_exam 是否有考试 0 无考试 1 有考试
   *什么都不选为 ""
   * */

    var is_free = ""
    var verified_active = ""
    var is_have_exam = ""

    private lateinit var inflater: HomePopMoocCourseFilterBinding
    override fun getImplLayoutId(): Int {
        return R.layout.home_pop_mooc_course_filter
    }


    override fun onCreate() {
        super.onCreate()
        inflater = HomePopMoocCourseFilterBinding.bind(popupImplView)
        inflater.tvHaveExam.setOnClickListener {
            it.isSelected = !it.isSelected
            is_have_exam = if(it.isSelected) "1" else ""
            if(it.isSelected){
                inflater.tvNoExam.isSelected = false
            }
        }

        inflater.tvNoExam.setOnClickListener {
            it.isSelected = !it.isSelected
            is_have_exam = if(it.isSelected) "0" else ""
            if(it.isSelected){
                inflater.tvHaveExam.isSelected = false
            }
        }

        inflater.tvHaveCert.setOnClickListener {
            it.isSelected = !it.isSelected
            verified_active = if(it.isSelected) "1" else ""
            if(it.isSelected){
                inflater.tvNoCert.isSelected = false
            }
        }

        inflater.tvNoCert.setOnClickListener {
            it.isSelected = !it.isSelected
            verified_active = if(it.isSelected) "0" else ""
            if(it.isSelected){
                inflater.tvHaveCert.isSelected = false
            }
        }

        inflater.tvHaveCost.setOnClickListener {
            it.isSelected = !it.isSelected
            is_free = if(it.isSelected) "0" else ""
            if(it.isSelected){
                inflater.tvNoCost.isSelected = false
            }
        }

        inflater.tvNoCost.setOnClickListener {
            it.isSelected = !it.isSelected
            is_free = if(it.isSelected) "1" else ""
            if(it.isSelected){
                inflater.tvHaveCost.isSelected = false
            }
        }
    }

    /**
     * 为左边加一个边距展示
     */
    fun showWithMargin(margin:Int){


        if(popupContentView!=null && popupContentView.parent!=null){
            val frameLayout = popupContentView.parent as FrameLayout
            (frameLayout).setPadding(margin,0,0,0)
        }
        super.show()
    }
}