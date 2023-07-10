package com.mooc.discover.fragment


import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.adapter.DiscoverTaskAdapter
import com.mooc.discover.model.DiscoverTaskBean
import com.mooc.discover.viewmodel.DiscoverTaskViewModel

/**
 * 任务列表Fragment 修改4.24
 */
class DiscoverTaskFragment : BaseListFragment<DiscoverTaskBean, DiscoverTaskViewModel>() {

    override fun onResume() {
        super.onResume()
        loadDataWithRrefresh()
    }

    @Suppress("NAME_SHADOWING")
    override fun initAdapter(): BaseQuickAdapter<DiscoverTaskBean, BaseViewHolder>? {
        val discoverTaskViewModel = mViewModel as DiscoverTaskViewModel
        return discoverTaskViewModel.getPageData().value?.let {
            val adapter = DiscoverTaskAdapter(it)
            adapter.addChildClickViewIds(R.id.tvTaskStatus)
            adapter.setOnItemClickListener { adapter, view, position ->
                val bean = adapter.data[position] as DiscoverTaskBean// 0 1 2 3 可以查看详情 目前4 5 不能进详情
                ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS).withString(
                        IntentParamsConstants.PARAMS_TASK_ID, bean.id)
                        .navigation()

            }
            adapter
        }
    }

}