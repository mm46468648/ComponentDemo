package com.mooc.studyroom.ui.adapter

import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.studyroom.ui.fragment.FriendScoreRankFragment
import com.mooc.studyroom.ui.fragment.ScoreDetailFragment

class ScoreRankViewpagerAdapter(activity:AppCompatActivity) : FragmentStateAdapter(activity) {

    companion object {

        const val PAGE_MUST = 0
        const val PAGE_COLUMN = 1
        const val PAGE_SPECIAL = 2
        const val PAGE_COMPLETE = 3
    }

    private val fragments: SparseArray<Fragment> = SparseArray()


    init {
        fragments.put(PAGE_MUST, FriendScoreRankFragment.getInstence(FriendScoreRankFragment.RANK_TYPE_ALL))
        fragments.put(PAGE_COLUMN, FriendScoreRankFragment.getInstence(FriendScoreRankFragment.RANK_TYPE_DAY))
        fragments.put(PAGE_SPECIAL, FriendScoreRankFragment.getInstence(FriendScoreRankFragment.RANK_TYPE_WEEK))
        fragments.put(PAGE_COMPLETE,FriendScoreRankFragment.getInstence(FriendScoreRankFragment.RANK_TYPE_MONTH))
//
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}