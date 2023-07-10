package com.mooc.home.ui.todaystudy

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.home.ui.todaystudy.special.TodayStudySpecialFragment
import com.mooc.home.ui.todaystudy.complete.TodayStudyCompleteFragment
import com.mooc.home.ui.todaystudy.must.TodayStudyMustFragment
import com.mooc.home.ui.todaystudy.column.TodayStudySubscribeColumnFragment

class TodayStudyFragmentAdapter(fragment:Fragment): FragmentStateAdapter(fragment) {

    companion object {

        const val PAGE_MUST = 0
        const val PAGE_COLUMN = 1
        const val PAGE_SPECIAL = 2
        const val PAGE_COMPLETE = 3
    }

    private val fragments: SparseArray<Fragment> = SparseArray()


    init {
        fragments.put(PAGE_MUST, TodayStudyMustFragment())
        fragments.put(PAGE_COLUMN, TodayStudySubscribeColumnFragment())
        fragments.put(PAGE_SPECIAL, TodayStudySpecialFragment())
        fragments.put(PAGE_COMPLETE,TodayStudyCompleteFragment())
//
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}