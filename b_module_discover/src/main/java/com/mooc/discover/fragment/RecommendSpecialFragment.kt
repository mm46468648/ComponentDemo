package com.mooc.discover.fragment

import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.adapter.RecommendSpecialAdapter
import com.mooc.discover.model.RecommendContentBean
import com.mooc.discover.viewmodel.RecommendSpecialViewModel
import com.mooc.resource.widget.Space120LoadMoreView
import com.mooc.column.constans.DiscoverConstants
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.manager.ResourceUtil
import com.mooc.discover.view.DiscoverAcitivtyFloatView
import com.mooc.statistics.LogUtil

/**
 * 发现页推荐tab
 * 其他专题fragment
 */
class RecommendSpecialFragment : BaseListFragment<RecommendContentBean.DataBean, RecommendSpecialViewModel>(),
    DiscoverAcitivtyFloatView.FloatViewVisibale {


    lateinit var mTab: String

    companion object {
        fun getInstance(bundle: Bundle? = null): RecommendSpecialFragment {
            val fragment = RecommendSpecialFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mTab = arguments?.getInt(DiscoverConstants.TAB_ID, -1).toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachToScroll(recycler_view,requireActivity())
    }
    override fun initAdapter(): BaseQuickAdapter<RecommendContentBean.DataBean, BaseViewHolder>? {
        val recommendSpecialViewModel = mViewModel as RecommendSpecialViewModel
        recommendSpecialViewModel.mTab = mTab

        return recommendSpecialViewModel.getPageData().value?.let {
            val recommendSpecialAdapter = RecommendSpecialAdapter(it)
            recommendSpecialAdapter.setOnItemClickListener { adapter, view, position ->
                val item = recommendSpecialAdapter.data.get(position)

                LogUtil.addClickLogNew(
                    LogEventConstants2.P_DISCOVER,
                    item._resourceId,
                    "${item._resourceType}",
                    item.title,
                    "${LogEventConstants2.typeLogPointMap[item._resourceType]}#${item._resourceId}"
                )
                ResourceTurnManager.turnToResourcePage(item)
                ResourceUtil.updateResourceRead(item.id.toString())

            }
            recommendSpecialAdapter
        }

    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }


}