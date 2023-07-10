package com.mooc.discover.fragment

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.discover.adapter.HotResourceAdapter
import com.mooc.discover.viewmodel.HotResourceViewModel
import com.mooc.statistics.LogUtil

/**
 * 最热资源，通过首页快捷入口展示
 * 只有最热课程和最热文章
 */
class HotResourceFragment : BaseListFragment2<Any, HotResourceViewModel>() {

    override fun initAdapter(): BaseQuickAdapter<Any, BaseViewHolder> {
        mViewModel?.resourceType =
            arguments?.getInt(IntentParamsConstants.PARAMS_RESOURCE_TYPE) ?: -1
        return mViewModel?.getPageData()?.value.let {
            val hotResourceAdapter = HotResourceAdapter(it)
            hotResourceAdapter.setOnItemClickListener { adapter, view, position ->
                val get = adapter.data.get(position)
                if (get is BaseResourceInterface){

                    ResourceTurnManager.turnToResourcePage(get as BaseResourceInterface)

                    LogUtil.addClickLogNew(
                        LogEventConstants2.P_RMKC,
                        get._resourceId,
                        "${get._resourceType}",
                        get._other?.get(IntentParamsConstants.WEB_PARAMS_TITLE)?:"",
                        "${LogEventConstants2.typeLogPointMap[get._resourceType]}#${get._resourceId}"
                    )
                }
            }
            hotResourceAdapter
        }
    }
}