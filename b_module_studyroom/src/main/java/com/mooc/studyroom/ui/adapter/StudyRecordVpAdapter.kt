package com.mooc.studyroom.ui.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.studyroom.ui.fragment.record.StudyProjectFragment
import com.mooc.studyroom.ui.fragment.record.StudyHistoryFragment
import com.mooc.studyroom.ui.fragment.record.StudyCompleteCourseFragment

/**
 * 学习记录viewpager适配器
 * 包含（学习历史，已完成课程，参加的学习项目）
 */
class StudyRecordVpAdapter(activity : FragmentActivity) : FragmentStateAdapter(activity){

    private val fragments: SparseArray<Fragment> = SparseArray()

    companion object {
        const val PAGE_STUDY_HiSTORY = 0
        const val PAGE_COMPLETE_COURSE = 1
        const val PAGE_STUDY_PROJECT = 2
    }

    init {
        fragments.put(PAGE_STUDY_HiSTORY, StudyHistoryFragment())
        fragments.put(PAGE_COMPLETE_COURSE, StudyCompleteCourseFragment())
        fragments.put(PAGE_STUDY_PROJECT, StudyProjectFragment())

    }
    override fun getItemCount(): Int {
        return fragments.size()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}