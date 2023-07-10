package com.mooc.ebook

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.search.EBookBean

class RecommendEbookAdapter(data: MutableList<EBookBean>?) : BaseQuickAdapter<EBookBean, BaseViewHolder>(R.layout.item_recommend_ebook, data) {


    override fun convert(holder: BaseViewHolder, item: EBookBean) {

        Glide.with(context)
                .load(item.picture)
                .placeholder(R.mipmap.my_iv_vertical_option)
                .error(R.mipmap.my_iv_vertical_option)
                .into(holder.getView(R.id.cover))
        holder.setText(R.id.title, item.title)
        holder.setText(R.id.tv_author, item.writer)

    }
}