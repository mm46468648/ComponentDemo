package com.mooc.studyproject.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.commonbusiness.base.EmptyFragment
import com.mooc.studyproject.fragment.FinishLearnFragment
import com.mooc.studyproject.fragment.MustLearnFragment
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean

/**
 * 学习记录viewpager适配器
 * 包含（学习历史，已完成课程，参加的学习项目）
 */
class StudyNotIntelligentVpAdapter(activity : FragmentActivity,
                                   var isDaShiMode:Boolean = false,
                                   var mStudyPlanDetailBean: StudyPlanDetailBean? = null) : FragmentStateAdapter(activity){


//     val fragments: SparseArray<Fragment> = SparseArray()

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->MustLearnFragment(isDaShiMode,mStudyPlanDetailBean)
            1->FinishLearnFragment(isDaShiMode,mStudyPlanDetailBean)
            else->EmptyFragment()
        }
    }
}