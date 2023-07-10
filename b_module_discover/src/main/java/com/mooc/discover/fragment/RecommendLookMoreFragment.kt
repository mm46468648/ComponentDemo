package com.mooc.discover.fragment

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.discover.adapter.RecommendLookMoreAdapter
import com.mooc.discover.model.RecommendContentBean
import com.mooc.discover.viewmodel.RecommendLookMoreViewModel

class RecommendLookMoreFragment : BaseListFragment<RecommendContentBean.DataBean, RecommendLookMoreViewModel>(){

    override fun initAdapter(): BaseQuickAdapter<RecommendContentBean.DataBean, BaseViewHolder>? {
        val string = arguments?.getString(IntentParamsConstants.PARAMS_RESOURCE_ID) ?: ""
        (mViewModel as RecommendLookMoreViewModel).resId = string
        return mViewModel?.getPageData()?.value?.let {
            val recommendAdapter = RecommendLookMoreAdapter(it)
            recommendAdapter.setOnItemClickListener { adapter, view, position ->
                ResourceTurnManager.turnToResourcePage(it[position])
            }
            recommendAdapter
        }
    }
}