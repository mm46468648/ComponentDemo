package com.mooc.commonbusiness.module.studyroom.publication

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.interfaces.BaseEditableAdapter
import com.mooc.commonbusiness.interfaces.EditableAdapterInterface
import com.mooc.commonbusiness.model.search.PublicationBean
import com.mooc.resource.widget.MoocImageView

/**
 * 实现公共可编辑接口，统一实现显示编辑弹窗逻辑
 */
@Suppress("UselessCallOnNotNull")
class PublicationAdapter(var list: ArrayList<PublicationBean>, needEdit: Boolean = true) :
    BaseEditableAdapter<PublicationBean>(R.layout.home_item_studyroom_publication, list, needEdit),
    EditableAdapterInterface, LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: PublicationBean) {
        holder.setText(R.id.tvTitle, item.magname)
        val ivCover = holder.getView<MoocImageView>(R.id.ivCover)

        Glide.with(context).load(item.coverurl)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(ivCover)

        holder.setText(R.id.tvOrg, item.unit)
        if (item.year.isNullOrBlank()) {
            item.year = ""
        }
        if (item.term.isNullOrBlank()) {
            item.term = ""
        }
        holder.setText(R.id.tvDes, "更新至${item.year}年第${item.term}期")

    }


}

