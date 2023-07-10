package com.mooc.my.fragment

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.my.ui.UserInfoActivity

class UserInfoFragmentAdapter(userId : String,fragment: UserInfoActivity) : FragmentStateAdapter(fragment) {

     val fragments: SparseArray<Fragment> = SparseArray()

    companion object {
        const val MY_SHARE = 0                //分享
        const val LEARNING_LISTING = 1         //公开的学习清单
    }

    init {
        fragments.put(MY_SHARE, UserShareListFragment.getInstence(userId))
        fragments.put(LEARNING_LISTING, UserLearningListFragment.getInstence(userId))
    }


    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }



}