package com.mooc.discover.fragment


import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.adapter.DiscoverTaskHistoryAdapter
import com.mooc.discover.model.DiscoverTaskBean
import com.mooc.discover.viewmodel.DiscoverAlreadygetTaskViewModel

/**
 * 已领取任务列表Fragment
 * 4.24
 */
class AlreadyGetTaskFragment : BaseListFragment<DiscoverTaskBean, DiscoverAlreadygetTaskViewModel>() {
    override fun onResume() {
        super.onResume()
        loadDataWithRrefresh()
    }
    @Suppress("NAME_SHADOWING")
    override fun initAdapter(): BaseQuickAdapter<DiscoverTaskBean, BaseViewHolder>? {
        val discoverTaskViewModel = mViewModel as DiscoverAlreadygetTaskViewModel
        return discoverTaskViewModel.getPageData().value?.let {
            val adapter = DiscoverTaskHistoryAdapter(it)
            adapter.addChildClickViewIds(R.id.tvTaskStatus)
            adapter.setOnItemChildClickListener { adapter, view, position ->
                val bean = adapter.data[position] as DiscoverTaskBean
                if (view.id == R.id.tvTaskStatus) {
                    ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS).withString(
                            IntentParamsConstants.PARAMS_TASK_ID, bean.id)
                            .navigation()
                }
            }
            adapter.setOnItemClickListener { adapter, view, position ->
                val bean = adapter.data[position] as DiscoverTaskBean
//                if ("0".equals(bean.status) || "1".equals(bean.status) || "2".equals(bean.status) || "3".equals(bean.status)) {
                    ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS).withString(
                            IntentParamsConstants.PARAMS_TASK_ID, bean.id)
                            .navigation()
//                }

            }
            adapter
        }
    }


}