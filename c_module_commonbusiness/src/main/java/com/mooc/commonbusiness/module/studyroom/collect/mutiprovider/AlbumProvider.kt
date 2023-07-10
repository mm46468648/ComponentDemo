package com.mooc.commonbusiness.module.studyroom.collect.mutiprovider

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.utils.format.TimeFormatUtil

class AlbumProvider(
    override val itemViewType: Int = ResourceTypeConstans.TYPE_ALBUM,
    override val layoutId: Int = R.layout.home_item_studyroom_collect_album
) : BaseItemProvider<Any>() {
    override fun convert(helper: BaseViewHolder, item: Any) {
        if (item is AlbumBean) {

            helper.setText(R.id.title, item.album_title)

            helper.setText(R.id.tv_play_count, TimeFormatUtil.formatPlayCount(item.play_count))
            helper.setText(R.id.album_total, item.include_track_count.toString() + "集")


            Glide.with(context)
                .load(item.cover_url_small)
                .error(R.mipmap.common_bg_cover_square_default)
                .placeholder(R.mipmap.common_bg_cover_square_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(helper.getView(R.id.cover))

        }
    }
}