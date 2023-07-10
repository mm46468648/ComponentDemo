package com.mooc.discover.adapter.delegate

import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.mooc.discover.R
import com.mooc.discover.model.RecommendContentBean
import com.mooc.discover.model.RecommendColumn
import com.mooc.commonbusiness.constants.ResourceTypeConstans

open class BaseColumnTagDelegeta<T> : BaseMultiTypeDelegate<T>() {

    var normalResType = 1

    //展示资源数组（仅限数组中的资源可以展示，多余要移除）
    var displayTypes = arrayOf(
        ResourceTypeConstans.TYPE_MICRO_LESSON,
        ResourceTypeConstans.TYPE_COURSE,
        ResourceTypeConstans.TYPE_TRACK,
        ResourceTypeConstans.TYPE_ALBUM,
        ResourceTypeConstans.TYPE_PERIODICAL,
        ResourceTypeConstans.TYPE_ARTICLE,
        ResourceTypeConstans.TYPE_E_BOOK,
        ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK,
        ResourceTypeConstans.TYPE_KNOWLEDGE,
        ResourceTypeConstans.TYPE_ACTIVITY_TASK,
        ResourceTypeConstans.TYPE_COLUMN_ARTICLE,
        ResourceTypeConstans.TYPE_NOTE,
        ResourceTypeConstans.TYPE_MASTER_TALK,
        ResourceTypeConstans.TYPE_SPECIAL,
        ResourceTypeConstans.TYPE_BAIKE,
        ResourceTypeConstans.TYPE_TEST_VOLUME,
        ResourceTypeConstans.TYPE_QUESTIONNAIRE,
        ResourceTypeConstans.TYPE_ONESELF_TRACK,
        ResourceTypeConstans.TYPE_ACTIVITY,
        ResourceTypeConstans.TYPE_COLUMN,
        ResourceTypeConstans.TYPE_STUDY_PLAN,
        ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL,
        ResourceTypeConstans.TYPE_WX_PROGRAM,
        ResourceTypeConstans.TYPE_BATTLE,
        ResourceTypeConstans.TYPE_PUBLICATION,
        ResourceTypeConstans.TYPE_TASK,
        ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE,
        ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE
    )

    init {
        addItemType(ResourceTypeConstans.TYPE_UNDEFINE, R.layout.common_item_empty)
    }

    override fun getItemType(data: List<T>, position: Int): Int {

        val get = data.get(position)
        if (get is RecommendColumn && displayTypes.contains(get.type)) {
            return normalResType
        }
        if (get is RecommendContentBean.DataBean && displayTypes.contains(get.type)) {
            return get.type
        }
        return ResourceTypeConstans.TYPE_UNDEFINE
    }
}