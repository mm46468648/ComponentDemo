package com.mooc.studyroom.ui.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.studyroom.ui.fragment.record.StudyRecordFragment
import com.mooc.studyroom.ui.fragment.myhonor.MyHonorFragment

/**
 * 学习档案viewpager适配器
 * 包含学习记录，我的荣誉
 */
class StudyArchiveVpAdapter(activity : FragmentActivity) : FragmentStateAdapter(activity){

    private val fragments: SparseArray<Fragment> = SparseArray()

    companion object {
        const val PAGE_STUDY_RECORD = 0
        const val PAGE_MY_HONOR = 1


    }

    init {
        fragments.put(PAGE_STUDY_RECORD, StudyRecordFragment())
        fragments.put(PAGE_MY_HONOR, MyHonorFragment())
    }
    override fun getItemCount(): Int {
        return fragments.size()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}