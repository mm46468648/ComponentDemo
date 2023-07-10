package com.mooc.commonbusiness.module.studyroom.collect.mutiprovider

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.utils.format.TimeFormatUtil

class TrackProvider(
    override val itemViewType: Int = ResourceTypeConstans.TYPE_TRACK,
    override val layoutId: Int = R.layout.home_item_studyroom_collect_track
) : BaseItemProvider<Any>() {
    override fun convert(helper: BaseViewHolder, item: Any) {
        if (item is TrackBean) {

            helper.setText(R.id.title, item.track_title)

            helper.setText(
                R.id.tv_track_play_count,
                TimeFormatUtil.formatPlayCount(item.play_count)
            )
            Glide.with(context)
                .load(item.cover_url_small)
                .error(R.mipmap.common_bg_cover_square_default)
                .placeholder(R.mipmap.common_bg_cover_square_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(helper.getView(R.id.cover))

            helper.setText(R.id.play_duration, TimeFormatUtil.timeParse(item.duration))


        }
    }

    override fun onChildClick(helper: BaseViewHolder, view: View, data: Any, position: Int) {
        when (view.id) {
//            R.id.progress_view -> {
//                toast("开始下载")
//            }
        }
    }
}