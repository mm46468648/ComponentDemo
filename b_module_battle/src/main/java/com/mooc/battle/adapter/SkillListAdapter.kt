package com.mooc.battle.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.battle.fragment.SkillMyCreateFragment
import com.mooc.battle.fragment.SkillOtherCreateFragment
import com.mooc.commonbusiness.base.EmptyFragment

class SkillListAdapter(fragment: FragmentActivity)
    : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->SkillMyCreateFragment()
            1->SkillOtherCreateFragment()
            else->EmptyFragment()
        }
    }

}