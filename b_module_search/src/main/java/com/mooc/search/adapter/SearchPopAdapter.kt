package com.mooc.search.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.search.R
import com.mooc.search.model.SearchPopData

class SearchPopAdapter(data: MutableList<SearchPopData>) :
        BaseQuickAdapter<SearchPopData, BaseViewHolder>(R.layout.search_item_pop_recycleview, data) {
    override fun convert(helper: BaseViewHolder, item: SearchPopData) {
        val layoutPosition = helper.layoutPosition
        if (layoutPosition == 0) {
            helper.setText(R.id.tv_title, item.title)
        } else {
            helper.setText(R.id.tv_title, String.format("%s（%s）", item.title, item.count))
        }
        if (item.isChecked) {
            helper.setVisible(R.id.iv_select, true)
        } else {
            helper.setVisible(R.id.iv_select, false)
        }
    }
}