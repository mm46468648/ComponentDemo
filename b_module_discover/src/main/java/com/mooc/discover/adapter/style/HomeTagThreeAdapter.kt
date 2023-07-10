package com.mooc.discover.adapter.style

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.column.ui.columnall.delegate.ColumnTagThreeDelegate
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.typeStringMap
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.formatPlayCount
import com.mooc.discover.R
import com.mooc.discover.model.RecommendColumn

class HomeTagThreeAdapter(data: ArrayList<RecommendColumn>?) :
    BaseDelegateMultiAdapter<RecommendColumn, BaseViewHolder>(data) {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    init {
        setMultiTypeDelegate(ColumnTagThreeDelegate())
    }

    override fun convert(holder: BaseViewHolder, item: RecommendColumn) {
        val itemViewType = holder.itemViewType
        if (itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) return
        holder.setGone(R.id.icon_play, true)
        val value = typeStringMap[item.type]
        holder.setText(R.id.tv_identify, value)
        holder.setGone(R.id.tv_identify, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeHomeThree, value)
        holder.setGone(R.id.tvTypeHomeThree, true)
        holder.setText(R.id.title, item.title)
        Glide.with(context)
            .load(item.small_image)
            .placeholder(R.mipmap.common_bg_cover_big_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into((holder.getView<View>(R.id.cover) as ImageView))
        when (item.type) {
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tv_identify, item.identity_name)
                }
                holder.setText(R.id.org, item.resource_info?.updated_time + "更新")
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> if (!TextUtils.isEmpty(item.video_duration)) {
                val video_duration: Long
                video_duration = try {
                    item.video_duration.toLong()
                } catch (e: NumberFormatException) {
                    0
                }
                holder.setText(R.id.org, TimeUtils.timeParse(video_duration))
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setText(R.id.org, item.org)
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tvTypeHomeThree, item.identity_name)
                    holder.setGone(R.id.tvTypeHomeThree, false)
                    holder.setGone(R.id.tv_identify, true)
                } else {
                    holder.setGone(R.id.tvTypeHomeThree, true)
                    holder.setGone(R.id.tv_identify, false)
                }
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.icon_play, false)
                val (_, _, _, _, subordinated_album) = item.track_data
                if (subordinated_album != null) {
                    holder.setText(R.id.org, subordinated_album.album_title)
                }
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.icon_play, false)
                val album = item.album_data
                val num = String.format(
                    context.resources.getString(R.string.recommend_album_count),
                    album.include_track_count.toString()
                )
                holder.setText(R.id.org, num)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> holder.setText(R.id.org, item.staff)
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> holder.setText(
                R.id.org,
                item.source
            )
            ResourceTypeConstans.TYPE_COLUMN -> holder.setText(R.id.org, item.update_time)
            ResourceTypeConstans.TYPE_E_BOOK -> holder.setText(R.id.org, item.writer)
            ResourceTypeConstans.TYPE_STUDY_PLAN -> holder.setText(R.id.org, item.staff)
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.icon_play, false)
                val musicBean = item.audio_data
                try {
                    holder.setText(
                        R.id.org,
                        String.format("播放 %s次", formatPlayCount(musicBean.audio_play_num))
                    )
                } catch (e: Exception) {
                    holder.setText(R.id.org, String.format("播放 %s次", 0))
                }
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                val updateStr =
                    "" + item.periodicals_data.year + "年 第" + item.periodicals_data.term + "期"
                holder.setText(R.id.org, updateStr)
            }
            ResourceTypeConstans.TYPE_TASK -> holder.setText(R.id.org, item.join_num + "人参与")
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> holder.setText(
                R.id.org,
                item.resource_info?.updated_time + "更新"
            )
        }
        setBottomGone(holder)
    }


    private fun setBottomGone(helper: BaseViewHolder) {
        val tvOrg = helper.getViewOrNull<View>(R.id.org)
        if (tvOrg is TextView) {
            val org = tvOrg.text.toString()
            helper.setGone(R.id.org, TextUtils.isEmpty(org))
        }
    }
}