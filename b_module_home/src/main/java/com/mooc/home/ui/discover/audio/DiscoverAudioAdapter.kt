package com.mooc.home.ui.discover.audio

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.resource.widget.MoocImageView
import com.mooc.home.R


class DiscoverAudioAdapter(list:ArrayList<AlbumBean>)
    : BaseQuickAdapter<AlbumBean,BaseViewHolder>(R.layout.home_item_discover_audio,list),LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: AlbumBean) {
        holder.setText(R.id.tvTitle,item.album_title)
        holder.getView<MoocImageView>(R.id.ivCover).setImageUrl(item.cover_url_small,2.dp2px())
    }
}