package com.mooc.column.ui.columnall

import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.column.constans.DiscoverConstants
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.discover.adapter.DiscoverRecommendColumnAdapter
import com.mooc.discover.model.RecommendColumn


class ColumnQuickFragment : BaseListFragment<RecommendColumn, ColumnQuickViewModel>() {

    lateinit var quickId: String

    companion object {
        fun getInstance(bundle: Bundle? = null): ColumnQuickFragment {
            val fragment = ColumnQuickFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quickId = arguments?.getString(DiscoverConstants.QUICK_ID, "").toString()

    }

    override fun initAdapter(): BaseQuickAdapter<RecommendColumn, BaseViewHolder>? {

        mViewModel?.quickId=quickId
        return (mViewModel as ColumnQuickViewModel).getPageData()?.value?.let {
            DiscoverRecommendColumnAdapter(it)
        }

    }


}