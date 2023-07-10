package com.mooc.column.ui.columnall

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.adapter.DiscoverRecommendColumnAdapter
import com.mooc.discover.model.RecommendColumn
import com.mooc.commonbusiness.base.BaseListFragment


class ColumnAllFragment : BaseListFragment<RecommendColumn, ColumnAllViewModel>() {


    override fun initAdapter(): BaseQuickAdapter<RecommendColumn, BaseViewHolder>? {

        return (mViewModel as ColumnAllViewModel).getPageData().value?.let {
            DiscoverRecommendColumnAdapter(it)
        }

    }


}