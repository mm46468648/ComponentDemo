package com.mooc.commonbusiness.module.studyroom.collect.mutiprovider

import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.home.OwnTrackBean
import com.mooc.commonbusiness.utils.format.TimeFormatUtil

/**
 * 自建音频Provider
 */
class OwnTrackProvider(
    override val itemViewType: Int = ResourceTypeConstans.TYPE_ONESELF_TRACK,
    override val layoutId: Int = R.layout.home_item_studyroom_collect_owntrack
) : BaseItemProvider<Any>() {

    override fun convert(helper: BaseViewHolder, item: Any) {
        if (item is OwnTrackBean) {

            Glide.with(context)
                .load(item.audio_bg_img)
                .error(R.mipmap.common_bg_cover_square_default)
                .placeholder(R.mipmap.common_bg_cover_square_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(helper.getView(R.id.cover))

            helper.setText(R.id.title, item.audio_name)

            helper.setText(R.id.tv_track_play_count, item.audio_play_num)
            val times: String = if (TextUtils.isEmpty(item.audio_time)) {
                "0"
            } else {
                item.audio_time
            }

            helper.setText(R.id.play_duration, TimeFormatUtil.timeParse(times.toLong()))
            helper.setText(R.id.tv_source, "军职在线")

        }

    }
}