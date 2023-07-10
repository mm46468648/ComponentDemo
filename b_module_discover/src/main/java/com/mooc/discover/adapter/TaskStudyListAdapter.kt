package com.mooc.discover.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.discover.R
import com.mooc.discover.model.TaskBindStudyListBean

/**

 * @Author limeng
 * @Date 2/24/22-2:04 PM
 */
class TaskStudyListAdapter(list: ArrayList<TaskBindStudyListBean>?) :
        BaseQuickAdapter<TaskBindStudyListBean, BaseViewHolder>(R.layout.discover_item_study_list, list) {
    override fun convert(holder: BaseViewHolder, item: TaskBindStudyListBean) {
        holder.setText(R.id.tv_content, item.source_name)

    }
}