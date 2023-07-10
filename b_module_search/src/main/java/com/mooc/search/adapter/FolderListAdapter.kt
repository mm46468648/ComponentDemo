package com.mooc.search.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.search.FolderBean
import com.mooc.search.R

/**
 * 搜索中学习清单
 */
class FolderListAdapter(data: MutableList<FolderBean>?, layoutResId: Int = R.layout.search_item_study_forder) : BaseQuickAdapter<FolderBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: FolderBean) {
        holder.setText(R.id.tvTitle, item.name)
        holder.setText(R.id.tvCreateName, item.username)
        holder.setText(R.id.tvCreateID, "ID " + item.user_id)
        holder.setText(R.id.tvResourceNum, item.resource_count.toString() + "个资源")
    }


}