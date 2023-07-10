package com.mooc.setting.ui.fragment

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.setting.adapter.UpdateLogAdapter
import com.mooc.setting.model.UpdateLogItem
import com.mooc.setting.viewmodel.UpdateLogViewModel

class UpdateLogsFramgent : BaseListFragment2<UpdateLogItem, UpdateLogViewModel>() {
    override fun initAdapter(): BaseQuickAdapter<UpdateLogItem, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let {
            UpdateLogAdapter(it)
        }
    }
}