package com.mooc.studyproject.fragment

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.studyproject.adapter.NoticeAdapter
import com.mooc.studyproject.model.Notice
import com.mooc.studyproject.viewmodel.NoticeViewModel

class NoticeListFragment : BaseListFragment<Notice, NoticeViewModel>() {
    var planId:String = ""
    override fun initAdapter(): BaseQuickAdapter<Notice, BaseViewHolder>? {
        val model = mViewModel as NoticeViewModel
         model.planId=planId
        return model.getPageData().value?.let {
            val adapter = NoticeAdapter(it)
            adapter
        }
    }
}