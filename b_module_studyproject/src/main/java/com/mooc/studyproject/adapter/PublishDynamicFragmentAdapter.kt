package com.mooc.studyproject.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.commonbusiness.base.EmptyFragment
import com.mooc.studyproject.fragment.PublishDynamicsFragment
import com.mooc.studyproject.fragment.PublishVoiceFragment

/**
 * Created by xym
 */
class PublishDynamicFragmentAdapter(var discoverTabs: List<String>, activity: FragmentActivity, val bundle : Bundle)
    : FragmentStateAdapter(activity) {


    companion object {
        const val STR_TAB_IMAGE = "图片动态"
        const val STR_TAB_TEXT = "文字动态"
        const val STR_TAB_IMAGE_TEXT = "图文动态"
        const val STR_TAB_VOICE = "语音动态"
        const val STR_TAB_VIDEO = "视频动态"
    }

    override fun createFragment(position: Int): Fragment {
        val discoverTab = discoverTabs[position]
        //返回对应类型fragment
//        val fragment = if (discoverTabs[position].id in 0 until fragments.size()) fragments.get(discoverTabs[position].id) else EmptyFragment()
        val fragment: Fragment = when (discoverTab) {
            STR_TAB_IMAGE, STR_TAB_TEXT, STR_TAB_IMAGE_TEXT -> {
                PublishDynamicsFragment()
            }
            STR_TAB_VOICE -> PublishVoiceFragment()
//            STR_TAB_VIDEO -> PublishVideoDynamicsFragment()
            else -> EmptyFragment()
        }
        fragment.arguments = bundle
        return fragment
    }

    override fun getItemCount(): Int {
        return discoverTabs.size
    }
}