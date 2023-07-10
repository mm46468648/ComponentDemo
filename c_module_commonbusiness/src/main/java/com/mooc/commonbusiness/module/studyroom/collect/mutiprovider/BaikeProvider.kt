package com.mooc.commonbusiness.module.studyroom.collect.mutiprovider

import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.model.search.BaiKeBean
import com.mooc.commonbusiness.constants.ResourceTypeConstans

class BaikeProvider(override val itemViewType: Int = ResourceTypeConstans.TYPE_BAIKE
                    , override val layoutId: Int = R.layout.home_item_studyroom_collect_baike) : BaseItemProvider<Any>() {
    override fun convert(helper: BaseViewHolder, item: Any) {
        if(item is BaiKeBean){
            helper.setText(R.id.tvBaikeTitle,item.title)
            helper.setText(R.id.tvBaikeContent,item.content)
        }
    }
}