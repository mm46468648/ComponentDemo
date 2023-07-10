package com.mooc.home.ui.pop

import android.content.Context
import android.widget.FrameLayout
import androidx.core.view.marginLeft
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.PartShadowPopupView
import com.mooc.common.ktextends.dp2px
import com.mooc.home.R
import com.mooc.home.databinding.HomePopMoocCourseStateBinding
//import kotlinx.android.synthetic.main.home_pop_mooc_course_state.view.*

class CourseStatePop(var mContext: Context) : PartShadowPopupView(mContext) {

    var process = "2"     //默认2全部，0即将开课，1已开课，-1已结束
    var processArray = arrayOf("2","1","0","-1")
    var onStateSelectCallback : ((process:String)->Unit)? = null
    override fun getImplLayoutId(): Int {
        return R.layout.home_pop_mooc_course_state
    }

    private lateinit var inflater : HomePopMoocCourseStateBinding
    override fun onCreate() {
        super.onCreate()
        inflater = HomePopMoocCourseStateBinding.bind(popupImplView)
        for (i in 0 until  inflater.llContainer.childCount){
            inflater.llContainer.getChildAt(i).setOnClickListener {
                onStateSelectCallback?.invoke(processArray[i])
                dismiss()
            }
        }
    }


    /**
     * 为左边加一个边距展示
     */
    fun showWithMargin(margin:Int){
        if(popupContentView!=null && popupContentView.parent!=null){
//            var layoutParams = (popupContentView.parent as FrameLayout).layoutParams  as FrameLayout.LayoutParams
            val frameLayout = popupContentView.parent as FrameLayout
            (frameLayout).setPadding(margin,0,0,0)
        }
        super.show()
    }
}