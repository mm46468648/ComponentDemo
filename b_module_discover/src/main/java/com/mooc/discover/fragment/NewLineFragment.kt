package com.mooc.discover.fragment


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.adapter.RecommendGuessAdapter
import com.mooc.discover.model.ResultBean
import com.mooc.discover.viewmodel.NewLineViewModel
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.manager.ResourceUtil
import com.mooc.statistics.LogUtil

/**
 *新鲜上线的列表页面
 * @author limeng
 * @date 2020/11/17
 */
class NewLineFragment : BaseListFragment<ResultBean, NewLineViewModel>() {
    override fun initAdapter(): BaseQuickAdapter<ResultBean, BaseViewHolder>? {
        return (mViewModel as NewLineViewModel ).getPageData().value?.let {
            val recommendGuessAdapter = RecommendGuessAdapter(it)
            recommendGuessAdapter.setOnItemClickListener { adapter, view, position ->
                val resource = it[position]


                ResourceTurnManager.turnToResourcePage(resource)

                LogUtil.addClickLogNew(
                    LogEventConstants2.P_XXSX,
                    resource._resourceId,
                    "${resource._resourceType}",
                    resource.title,
                    "${LogEventConstants2.typeLogPointMap[resource._resourceType]}#${resource._resourceId}"
                )

                ResourceUtil.updateResourceRead(resource.id)

            }
            recommendGuessAdapter
        }

    }

}