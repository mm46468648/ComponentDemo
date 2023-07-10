package com.mooc.course.ui.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.course.ui.fragment.CourseCheckFragment
import com.mooc.course.ui.fragment.CourseNoticeFragment
import com.mooc.course.ui.fragment.CourseWareFragment

/**
 * 课程播放fragmentAdapter
 */
class CoursePlayPageAdapter(xtCourseId : String, activity : FragmentActivity) : FragmentStateAdapter(activity){
    private val fragments: SparseArray<Fragment> = SparseArray()

    companion object {
        const val PAGE_COURSE_WARE = 0
        const val PAGE_COURSE_NOTICE = 1
        const val PAGE_COURSE_EXAM = 2
    }

    init {
        fragments.put(PAGE_COURSE_WARE, CourseWareFragment.getInstence(xtCourseId))
        fragments.put(PAGE_COURSE_NOTICE, CourseNoticeFragment.getInstence(xtCourseId))
        fragments.put(PAGE_COURSE_EXAM, CourseCheckFragment.getInstence(xtCourseId))
    }


    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }

}