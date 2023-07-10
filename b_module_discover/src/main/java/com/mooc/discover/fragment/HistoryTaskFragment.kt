package com.mooc.discover.fragment


import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.adapter.DiscoverTaskHistoryAdapter
import com.mooc.discover.model.DiscoverTaskBean
import com.mooc.discover.viewmodel.DiscoverHistoryTaskViewModel


/**
 *历史任务
 * @author limeng
 * @date 2022/6/13
 */
class HistoryTaskFragment : BaseListFragment<DiscoverTaskBean, DiscoverHistoryTaskViewModel>() {
    override fun onResume() {
        super.onResume()
        loadDataWithRrefresh()
    }
    @Suppress("NAME_SHADOWING")
    override fun initAdapter(): BaseQuickAdapter<DiscoverTaskBean, BaseViewHolder>? {
        val discoverTaskViewModel = mViewModel as DiscoverHistoryTaskViewModel
        return discoverTaskViewModel.getPageData().value?.let {
            val adapter = DiscoverTaskHistoryAdapter(it)
            adapter.setOnItemClickListener { adapter, view, position ->
                val bean = adapter.data[position] as DiscoverTaskBean
                    ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS).withString(
                            IntentParamsConstants.PARAMS_TASK_ID, bean.id)
                            .navigation()
            }
            adapter
        }
    }


}