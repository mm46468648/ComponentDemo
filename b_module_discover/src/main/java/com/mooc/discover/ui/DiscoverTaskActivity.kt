package com.mooc.discover.ui

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.utils.statusbar.StatusBarUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.fragment.DiscoverTaskFragment
import com.mooc.discover.viewmodel.TaskDetailViewModel
import com.mooc.resource.widget.CommonTitleLayout

@Route(path = Paths.PAGE_DISCOVER_TASK)
class DiscoverTaskActivity : BaseActivity() {

    val mViewModle by viewModels<TaskDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_task)

        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        commonTitle?.middle_text = getString(R.string.task_list)
        commonTitle?.setOnLeftClickListener { finish() }
        commonTitle.right_text = getString(R.string.already_get_task)
        commonTitle.tv_right?.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_DISCOVER_ALREADY_GET_TASK).navigation() }

        supportFragmentManager.beginTransaction().add(R.id.flContainer, DiscoverTaskFragment()).commit()


        mViewModle.getGuideConfig().observe(this, Observer {
            if(!it.data){
                showTaskGuide()
            }
        })
    }


    fun showTaskGuide(){
//        val firstGuide = SpUtils.get().getValue(SpConstants.SP_TASK_FIRST_GUIDE, true)
//        if(!firstGuide) return
//        SpUtils.get().putValue(SpConstants.SP_TASK_FIRST_GUIDE, false)

        val inflate = findViewById<ViewStub>(R.id.vsTaskGuide).inflate()
        inflate.setOnClickListener {  }
        val llGuide1 = inflate.findViewById<LinearLayout>(R.id.llGuide1)
        val llGuide2 = inflate.findViewById<LinearLayout>(R.id.llGuide2)
        val llGuide3 = inflate.findViewById<LinearLayout>(R.id.llGuide3)

        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0,StatusBarUtils.getStatusBarHeight(this),0,0)
        llGuide3.layoutParams = layoutParams

        inflate.findViewById<View>(R.id.tvNext1).setOnClickListener {
            llGuide1.visibility = View.GONE
            llGuide2.visibility = View.VISIBLE
        }
        inflate.findViewById<View>(R.id.tvNext2).setOnClickListener {
            llGuide2.visibility = View.GONE
            llGuide3.visibility = View.VISIBLE
        }
        inflate.findViewById<View>(R.id.tvNext3).setOnClickListener {
            inflate.visibility = View.GONE
            mViewModle.postGuideConfig()
        }
    }
}