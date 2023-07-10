package com.mooc.home.ui.discover

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.discover.fragment.DiscoverRecommendFragment
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.EmptyFragment
import com.mooc.home.ui.discover.article.ArticleFragment
import com.mooc.home.ui.discover.audio.DiscoverAudioFragment
import com.mooc.home.ui.discover.ebook.EbookFragment
import com.mooc.home.ui.discover.mooc.DiscoverMoocFragment
import com.mooc.home.ui.discover.slightcourse.SlightCourseFragment
import com.mooc.discover.fragment.StudyProjectFragment
import com.mooc.commonbusiness.model.studyroom.DiscoverTab
import com.mooc.home.ui.discover.micrlprofession.MicroProfessionFragment
import com.mooc.home.ui.discover.microknow.MicroKnowChildFragment
import com.mooc.home.ui.discover.publication.PublicationFragment

class DiscoverFragmentAdapter(var discoverTabs: List<DiscoverTab>, fragment: Fragment)
    : FragmentStateAdapter(fragment) {


    companion object {
        const val PARAM_TABID = "param_tabid"
        const val PARAM_RESOURCE_TYPE = "param_resource_type"
        const val PARAM_RELATION_TYPE = "param_relation_type"

        const val PAGE_RECOMMEND = -1         //推荐
        const val PAGE_STUDY_PROJECT = 0     //学习计划
        const val PAGE_MOOC = 1               //慕课
        const val PAGE_AUDIO = 2              //音频
        const val PAGE_ARTICLE = 3            //文章
        const val PAGE_EBOOD = 4               //电子书
        const val PAGE_SLIGHT_COURESE = 5    //微课
        const val PAGE_PUBLICATION = 6    //刊物
        const val PAGE_MICRO_KNOW = 8    //微知识
        const val PAGE_MICRO_PROFESSION = 9    //微专业
    }

    override fun createFragment(position: Int): Fragment {
        val discoverTab = discoverTabs[position]
        //返回对应类型fragment
//        val fragment = if (discoverTabs[position].id in 0 until fragments.size()) fragments.get(discoverTabs[position].id) else EmptyFragment()
        val fragment: Fragment = when(discoverTab.relation_type){
            PAGE_RECOMMEND -> DiscoverRecommendFragment()
            PAGE_STUDY_PROJECT -> StudyProjectFragment.getInstance(Bundle().put(PARAM_TABID,discoverTab.id))
            PAGE_MOOC ->DiscoverMoocFragment.getInstance(Bundle().put(PARAM_TABID,discoverTab.id).put(PARAM_RESOURCE_TYPE,discoverTab.resource_type).put(PARAM_RELATION_TYPE,discoverTab.relation_type))
            PAGE_AUDIO -> DiscoverAudioFragment.getInstance(Bundle().put(PARAM_TABID,discoverTab.id).put(PARAM_RESOURCE_TYPE,discoverTab.resource_type).put(PARAM_RELATION_TYPE,discoverTab.relation_type))
            PAGE_ARTICLE ->ArticleFragment.getInstance(Bundle().put(PARAM_TABID,discoverTab.id).put(PARAM_RESOURCE_TYPE,discoverTab.resource_type).put(PARAM_RELATION_TYPE,discoverTab.relation_type))
            PAGE_EBOOD ->EbookFragment.getInstance(Bundle().put(PARAM_TABID,discoverTab.id).put(PARAM_RESOURCE_TYPE,discoverTab.resource_type).put(PARAM_RELATION_TYPE,discoverTab.relation_type))
            PAGE_SLIGHT_COURESE -> SlightCourseFragment.getInstance(Bundle().put(PARAM_TABID,discoverTab.id).put(PARAM_RESOURCE_TYPE,discoverTab.resource_type).put(PARAM_RELATION_TYPE,discoverTab.relation_type))
            PAGE_PUBLICATION -> PublicationFragment.getInstance(Bundle().put(PARAM_TABID,discoverTab.id).put(PARAM_RESOURCE_TYPE,discoverTab.resource_type).put(PARAM_RELATION_TYPE,discoverTab.relation_type))
            PAGE_MICRO_KNOW -> MicroKnowChildFragment.getInstance()
            PAGE_MICRO_PROFESSION -> MicroProfessionFragment.getInstance()
            else -> EmptyFragment()
        }
        return fragment
    }

    override fun getItemCount(): Int {
        return discoverTabs.size
    }



}