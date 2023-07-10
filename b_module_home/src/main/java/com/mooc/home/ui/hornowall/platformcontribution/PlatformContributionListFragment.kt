package com.mooc.home.ui.hornowall.platformcontribution

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.home.model.honorwall.PlatformContributionBean
import com.mooc.resource.widget.Space120LoadMoreView

class PlatformContributionListFragment : BaseListFragment<PlatformContributionBean, PlatfromContributionViewModel>() {
    var type: String = "1"

    override fun initAdapter(): BaseQuickAdapter<PlatformContributionBean, BaseViewHolder>? {
        val platfromContributionViewModel = mViewModel as PlatfromContributionViewModel
        platfromContributionViewModel.type = type
        return platfromContributionViewModel.getPageData()?.value?.let {
            PlatfromContributionAdapter(it)
        }

    }

    fun uodateType(type: String){
        val platfromContributionViewModel = mViewModel as PlatfromContributionViewModel
        platfromContributionViewModel.type = type
        loadDataWithRrefresh()
    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }

}