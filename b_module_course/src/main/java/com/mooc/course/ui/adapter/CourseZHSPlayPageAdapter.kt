package com.mooc.course.ui.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.course.ui.fragment.ZHSCheckFragment
import com.mooc.course.ui.fragment.ZHSCourseWareFragment

/**
 * 智慧树课程播放fragmentAdapter
 */
class CourseZHSPlayPageAdapter(courseId: String, activity: FragmentActivity, var courseBean: CourseBean) : FragmentStateAdapter(activity) {
    private val fragments: SparseArray<Fragment> = SparseArray()

    companion object {
        const val PAGE_COURSE_WARE = 0
        const val PAGE_COURSE_EXAM = 1
    }

    init {
        fragments.put(PAGE_COURSE_WARE, ZHSCourseWareFragment.getInstance(courseId, courseBean))
        fragments.put(PAGE_COURSE_EXAM, ZHSCheckFragment.getInstance(courseId, courseBean))
    }


    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }

    fun getCheckFragment(): Fragment? {
        return fragments.get(PAGE_COURSE_EXAM)
    }
}