package com.mooc.discover.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mooc.column.constans.DiscoverConstants
import com.mooc.common.ktextends.put
import com.mooc.discover.fragment.MyOrderListFragment
import com.mooc.discover.fragment.RecommendAllFragment
import com.mooc.discover.fragment.RecommendSpecialFragment
import com.mooc.discover.fragment.StudyProjectFragment
import com.mooc.discover.model.TabSortBean

class DiscoverRecoomendAdapter(
    private val tabSortBeans: List<TabSortBean>,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        val tabSortBean = tabSortBeans[position]
        val fragment = when (tabSortBean.title) {
            "全部"->{
                RecommendAllFragment()
            }
            "我的订阅"->{
                MyOrderListFragment()
            }
            "学习项目"->{
                StudyProjectFragment.getInstance()
            }
            else->{
                RecommendSpecialFragment.getInstance(Bundle().put(DiscoverConstants.TAB_ID, tabSortBean.id))
            }
        }
        return fragment
    }

    override fun getCount(): Int {
        return tabSortBeans.size
    }
}