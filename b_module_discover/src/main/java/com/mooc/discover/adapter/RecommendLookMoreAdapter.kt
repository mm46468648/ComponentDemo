package com.mooc.discover.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.R
import com.mooc.discover.model.RecommendContentBean
import com.mooc.resource.widget.MoocImageView

class RecommendLookMoreAdapter(data:ArrayList<RecommendContentBean.DataBean>)
    : BaseQuickAdapter<RecommendContentBean.DataBean, BaseViewHolder>( R.layout.discover_item_recommend_lookmore,data) {

    override fun convert(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        holder.getView<MoocImageView>(R.id.ivBg).setImageUrl(item.big_image)
        holder.setText(R.id.tv_title,item.title)
        holder.setText(R.id.tv_sub_title,item.about)
    }
}