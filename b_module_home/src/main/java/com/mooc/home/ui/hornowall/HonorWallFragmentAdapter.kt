package com.mooc.home.ui.hornowall

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.home.ui.hornowall.activityrank.RankListFragment
import com.mooc.home.ui.hornowall.talent.TalentListFragment
import com.mooc.home.ui.hornowall.platformcontribution.PlatformContributionFragment

class HonorWallFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments: SparseArray<Fragment> = SparseArray()

    companion object {
        const val PAGE_TALENT = 0
//        const val PLATFORM_CONTRIBUTION = 1
//        const val PAGE_RANK_LIST = 2
        const val PAGE_RANK_LIST = 1
    }

    init {
        fragments.put(PAGE_TALENT, TalentListFragment())
//        fragments.put(PLATFORM_CONTRIBUTION,PlatformContributionFragment())
        fragments.put(PAGE_RANK_LIST, RankListFragment.getInstance())
    }


    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }



}