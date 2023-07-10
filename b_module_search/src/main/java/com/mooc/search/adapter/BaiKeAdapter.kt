package com.mooc.search.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.search.R
import com.mooc.commonbusiness.model.search.BaiKeBean

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class BaiKeAdapter(data: MutableList<BaiKeBean>?, layoutResId: Int = R.layout.search_item_baike) : BaseQuickAdapter<BaiKeBean, BaseViewHolder>(layoutResId, data) , LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: BaiKeBean) {

        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_content, item.summary)


    }

}