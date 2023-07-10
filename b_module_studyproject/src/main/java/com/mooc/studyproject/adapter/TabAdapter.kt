package com.mooc.studyproject.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

class TabAdapter(fragmentManager: FragmentManager?, private val titleList: ArrayList<String>, fragments: ArrayList<Fragment?>?) : FragmentStatePagerAdapter(fragmentManager!!) {
    private val fragments: ArrayList<Fragment?>?
    override fun getItem(position: Int): Fragment {
        return fragments?.get(position)!!
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

    override fun getCount(): Int {
        return titleList.size
    }

    init {
        this.fragments = fragments
    }
}