package com.mooc.studyproject.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.commonbusiness.base.EmptyFragment
import com.mooc.studyproject.fragment.ChoseLearnFragment
import com.mooc.studyproject.fragment.FinishLearnFragment
import com.mooc.studyproject.fragment.MustLearnFragment
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean

/**
 * 学习记录viewpager适配器
 * 包含（学习历史，已完成课程，参加的学习项目）
 */
class StudyListVpAdapter(activity : FragmentActivity,
                         var isDaShiMode:Boolean = false,
                         var mStudyPlanDetailBean: StudyPlanDetailBean? = null) : FragmentStateAdapter(activity){


//     val fragments: SparseArray<Fragment> = SparseArray()
//
//    companion object {
//        const val PAGE_MUST_LEARN = 0
//        const val PAGE_CHOSE_LEARN = 1
//        const val PAGE_FINISH_LEARN = 2
//    }
//
//    init {
//        fragments.put(PAGE_MUST_LEARN, MustLearnFragment(isDaShiMode,mStudyPlanDetailBean))
//        fragments.put(PAGE_CHOSE_LEARN, ChoseLearnFragment(isDaShiMode,mStudyPlanDetailBean))
//        fragments.put(PAGE_FINISH_LEARN, FinishLearnFragment(isDaShiMode,mStudyPlanDetailBean))
//
//    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> MustLearnFragment(isDaShiMode,mStudyPlanDetailBean)
            1-> ChoseLearnFragment(isDaShiMode,mStudyPlanDetailBean)
            2-> FinishLearnFragment(isDaShiMode,mStudyPlanDetailBean)
            else-> EmptyFragment()
        }
    }
}