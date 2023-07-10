package com.mooc.discover.adapter.delegate

import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.discover.R
import com.mooc.discover.model.RecommendContentBean

/**
 * 推荐中的栏目代理
 */
class RecommendSpecilDelegeta : BaseColumnTagDelegeta<RecommendContentBean.DataBean>() {

    init {
        addItemType(ResourceTypeConstans.TYPE_ARTICLE, R.layout.item_binding_article)
        addItemType(ResourceTypeConstans.TYPE_SPECIAL, R.layout.item_binding_article)
        addItemType(ResourceTypeConstans.TYPE_COLUMN_ARTICLE, R.layout.item_binding_article)
        addItemType(ResourceTypeConstans.TYPE_KNOWLEDGE, R.layout.item_binding_article)
        addItemType(ResourceTypeConstans.TYPE_NOTE, R.layout.item_binding_article)
        addItemType(
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK,
            R.layout.item_binding_article_no_image
        )
        addItemType(ResourceTypeConstans.TYPE_NO_IMAGE, R.layout.item_binding_article_no_image)
        addItemType(ResourceTypeConstans.TYPE_BAIKE, R.layout.item_binding_article_no_image)
        addItemType(ResourceTypeConstans.TYPE_COLUMN, R.layout.item_binding_article_no_image)
        addItemType(
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE,
            R.layout.item_binding_article_no_image
        )
        addItemType(ResourceTypeConstans.TYPE_COURSE, R.layout.item_binding_course)
        addItemType(ResourceTypeConstans.TYPE_MICRO_LESSON, R.layout.item_binding_micro)
        addItemType(ResourceTypeConstans.TYPE_ALBUM, R.layout.item_binding_album)
        addItemType(ResourceTypeConstans.TYPE_TRACK, R.layout.item_binding_track)
        addItemType(ResourceTypeConstans.TYPE_PERIODICAL, R.layout.item_binding_periodical)
        addItemType(ResourceTypeConstans.TYPE_E_BOOK, R.layout.item_binding_e_book)
        addItemType(ResourceTypeConstans.TYPE_TEST_VOLUME, R.layout.item_binding_test_question)
        addItemType(ResourceTypeConstans.TYPE_QUESTIONNAIRE, R.layout.item_binding_test_question)
        addItemType(ResourceTypeConstans.TYPE_STUDY_PLAN, R.layout.item_binding_study_plan)
        addItemType(ResourceTypeConstans.TYPE_ACTIVITY, R.layout.item_binding_activity)
        addItemType(ResourceTypeConstans.TYPE_ACTIVITY_TASK, R.layout.item_binding_activity)
        addItemType(ResourceTypeConstans.TYPE_ONESELF_TRACK, R.layout.item_binding_self_music)
        addItemType(ResourceTypeConstans.TYPE_MASTER_TALK, R.layout.item_binding_mast_talk)
        addItemType(ResourceTypeConstans.TYPE_WX_PROGRAM, R.layout.item_binding_wx_program)
        addItemType(ResourceTypeConstans.TYPE_BATTLE, R.layout.item_binding_battle)
        addItemType(ResourceTypeConstans.TYPE_UNDEFINE, R.layout.item_recommend_empty)
        addItemType(ResourceTypeConstans.TYPE_PUBLICATION, R.layout.item_my_order_publication_two)
        addItemType(ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL, R.layout.item_binding_micro_professional)
        addItemType(ResourceTypeConstans.TYPE_TASK, R.layout.item_binding_task)
        addItemType(ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE, R.layout.item_binding_micro_professional)


    }
}