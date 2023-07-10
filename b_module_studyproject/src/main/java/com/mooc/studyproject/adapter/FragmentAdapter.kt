package com.mooc.studyproject.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by huangzuoliang on 2017/10/30.
 */
class FragmentAdapter(fm: FragmentManager?, //fragment列表
                      private val list_fragment: List<Fragment>, private val list_Title: List<String>) : FragmentStatePagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        return list_fragment[position]
    }

    override fun getCount(): Int {
        return list_fragment.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list_Title[position]
    }
}