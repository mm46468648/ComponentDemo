package com.mooc.discover.adapter.style

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.column.ui.columnall.delegate.ColumnTagOneDelegate
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.typeStringMap
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.utils.TaskScoreUtils.Companion.getSpaString
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.formatPlayCount
import com.mooc.discover.R
import com.mooc.discover.model.RecommendColumn
import com.qmuiteam.qmui.util.QMUIDisplayHelper

class HomeTagOneAdapter(data: ArrayList<RecommendColumn>?) :
    BaseDelegateMultiAdapter<RecommendColumn, BaseViewHolder>(data) {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    init {
        setMultiTypeDelegate(ColumnTagOneDelegate())
    }

    override fun convert(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setGone(R.id.icon_play, true)
        holder.setText(R.id.tvFrom, "")
        holder.setText(R.id.org, "")
        holder.setText(R.id.time, "")
        holder.setText(R.id.title, "")
        holder.setGone(R.id.time, true)

        val type = typeStringMap[item.type]
        holder.setText(R.id.tv_identify, type)
        holder.setGone(R.id.tv_identify, TextUtils.isEmpty(type))
        holder.setText(R.id.tvTypeHomeOne, type)
        holder.setGone(R.id.tvTypeHomeOne, true)

        //默认一个图片一个标题

        //默认一个图片一个标题
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
                holder.setText(R.id.org, item.resource_info?.title)
                holder.setText(R.id.time, item.resource_info?.updated_time + "更新")
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                holder.setText(R.id.org, item.source)
                if (!TextUtils.isEmpty(item.video_duration)) {
                    val videoDuration: Long = try {
                        item.video_duration.toLong()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    holder.setText(R.id.tvFrom, TimeUtils.timeParse(videoDuration))
                }
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setText(R.id.org, item.org)
                holder.setText(R.id.tvFrom, item.start_time)
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setGone(R.id.tvTypeHomeOne, false)
                    holder.setGone(R.id.tv_identify, true)
                    holder.setText(R.id.tvTypeHomeOne, item.identity_name)
                } else {
                    holder.setGone(R.id.tvTypeHomeOne, true)
                    holder.setGone(R.id.tv_identify, false)
                }
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.icon_play, false)
                val (_, _, _, _, subordinated_album, _, _, _, _, duration) = item.track_data
                if (subordinated_album != null) {
                    holder.setText(R.id.org, subordinated_album!!.album_title)
                }
                holder.setText(R.id.tvFrom, TimeUtils.timeParse(duration))
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
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.org, item.staff)
                holder.setText(R.id.tvFrom, item.source)
            }
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {
                holder.setText(R.id.org, item.staff)
                holder.setText(R.id.tvFrom, item.source)
            }
            ResourceTypeConstans.TYPE_COLUMN -> {
                holder.setText(R.id.org, item.desc)
                holder.setText(R.id.time, item.update_time)
                holder.setGone(R.id.time, false)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.org, item.writer)
                holder.setText(R.id.tvFrom, item.platform)
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setText(R.id.org, item.staff)
                holder.setText(R.id.time, item.student_num.toString() + "人")
                holder.setGone(R.id.time, false)
            }
            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
//                holder.setText(R.id.org, item.about)
//                holder.setText(R.id.time, item.publish_time)
//                holder.setGone(R.id.time, false)
            }
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
                try {
                    holder.setText(R.id.tvFrom, TimeUtils.timeParse(musicBean.audio_time))
                } catch (e: Exception) {
                    holder.setText(R.id.tvFrom, "")
                }
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                val updateStr =
                    "更新至" + item.periodicals_data.year + "年 第" + item.periodicals_data.term + "期"
                holder.setText(R.id.org, updateStr)
                holder.setText(R.id.time, item.periodicals_data.unit)
                holder.setGone(R.id.time, false)
            }
            ResourceTypeConstans.TYPE_TASK -> {
                val succesScore = getSpaString(
                    item.success_score,
                    ContextCompat.getColor(context, R.color.color_5D5D5D),
                    ContextCompat.getColor(context, R.color.color_5D5D5D),
                    QMUIDisplayHelper.sp2px(context, 11),
                    QMUIDisplayHelper.sp2px(context, 15)
                )
                holder.setText(R.id.org, succesScore)
                holder.setText(R.id.time, item.join_num + "人参与")
                holder.setGone(R.id.time, false)
            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {
                holder.setText(R.id.org, item.resource_info?.title)
                holder.setText(R.id.time, item.resource_info?.updated_time + "更新")
                holder.setGone(R.id.time, false)
            }
        }
        val tvOrg = holder.getViewOrNull<TextView>(R.id.org)
        val tvFrom = holder.getViewOrNull<TextView>(R.id.tvFrom)

        val layoutParams = tvOrg?.layoutParams as ConstraintLayout.LayoutParams
        if (item.type == ResourceTypeConstans.TYPE_ARTICLE || item.type == ResourceTypeConstans.TYPE_COLUMN_ARTICLE) {
            layoutParams.matchConstraintPercentWidth = 0.5f
        } else {
            layoutParams.matchConstraintPercentWidth = 0.7f
        }
        tvOrg.layoutParams = layoutParams


        if (TextUtils.isEmpty(tvOrg.text) && TextUtils.isEmpty(tvFrom?.text)) {
            holder.setGone(R.id.org, true)
            holder.setGone(R.id.tvFrom, true)
        } else {
            holder.setGone(R.id.org, false)
            holder.setGone(R.id.tvFrom, false)
        }

    }
}