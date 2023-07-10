package com.mooc.course.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.course.R
import com.mooc.course.manager.VideoActionManager
import com.mooc.course.ui.adapter.CourseZHSPlayPageAdapter
//import kotlinx.android.synthetic.main.course_play_activity.*
import java.lang.ref.WeakReference

/**
 * 智慧树课程播放页面
 */
@Route(path = Paths.PAGE_COURSE_ZHS_PLAY)
class ZHSCoursePlayActivity : CoursePlayActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater.tvCourseName.text = courseTitle
        inflater.playerWrapper.getCustomLayoutView()?.findViewById<View>(R.id.ibDownload)?.apply {
            this.visibility = View.VISIBLE
            this.setOnClickListener {
                ARouter.getInstance().build(Paths.PAGE_COURSE_ZHS_DOWNLOAD)
                        .withString(IntentParamsConstants.COURSE_PARAMS_ID, courseId)
                        .withString(IntentParamsConstants.COURSE_PARAMS_TITLE, courseTitle)
                        .withParcelable(IntentParamsConstants.COURSE_PARAMS_DATA, courseBean)
                        .navigation()
            }
        }

        VideoActionManager.classId = courseBean.id
        VideoActionManager.context = WeakReference<Activity>(this)

        mViewModel.postSynchronizeZHSCourseProcess(courseBean.id)
    }

    /**
     * 初始化下面viewpager
     */
    override fun initViewPager() {
        inflater.stlTitle.setTabStrs(arrayOf("课件", "考核"))
        inflater.viewPager.adapter = CourseZHSPlayPageAdapter(courseId, this, courseBean)
        inflater.stlTitle.setViewPager2(inflater.viewPager)
    }

}