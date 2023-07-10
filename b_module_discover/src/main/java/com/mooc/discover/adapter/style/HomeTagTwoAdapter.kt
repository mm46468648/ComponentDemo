package com.mooc.discover.adapter.style

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.column.ui.columnall.delegate.ColumnTagTwoDelegate
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.typeStringMap
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.utils.TaskScoreUtils.Companion.getSpaString
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.formatPlayCount
import com.mooc.discover.R
import com.mooc.discover.model.RecommendColumn
import com.qmuiteam.qmui.util.QMUIDisplayHelper

class HomeTagTwoAdapter(data: ArrayList<RecommendColumn>?) :
    BaseDelegateMultiAdapter<RecommendColumn, BaseViewHolder>(data) {

    init {
        setMultiTypeDelegate(ColumnTagTwoDelegate())
    }

    override fun convert(holder: BaseViewHolder, item: RecommendColumn) {
        val itemViewType = holder.itemViewType
        if (itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) return

        //默认值防止复用

        //默认值防止复用
        holder.setGone(R.id.icon_play, true)
        val value = typeStringMap[item.type]
        holder.setText(R.id.tv_identify, value)
        holder.setGone(R.id.tv_identify, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeHomeTwo, value)
        holder.setGone(R.id.tvTypeHomeTwo, true)

        holder.setText(R.id.org, "")
        holder.setText(R.id.tvFrom2, "")


        holder.setGone(R.id.time, false)
        holder.setGone(R.id.tvFrom2, false)

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
                holder.setText(R.id.time, item.resource_info?.title)
                holder.setText(R.id.tvFrom2, item.resource_info?.updated_time + "更新")
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                holder.setText(R.id.time, item.source)
                if (!TextUtils.isEmpty(item.video_duration)) {
                    val video_duration: Long
                    video_duration = try {
                        item.video_duration.toLong()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    holder.setText(R.id.tvFrom2, TimeUtils.timeParse(video_duration))
                }
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setText(R.id.time, item.org)
                holder.setText(R.id.tvFrom2, item.start_time)
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tvTypeHomeTwo, item.identity_name)
                    holder.setGone(R.id.tvTypeHomeTwo, false)
                    holder.setGone(R.id.tv_identify, true)
                } else {
                    holder.setGone(R.id.tvTypeHomeTwo, true)
                    holder.setGone(R.id.tv_identify, false)
                }
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.icon_play, false)
                val (_, _, _, _, subordinated_album, _, _, _, _, duration) = item.track_data
                if (subordinated_album != null) {
                    holder.setGone(R.id.time, false)
                    holder.setText(R.id.time, subordinated_album.album_title)
                } else {
                    holder.setGone(R.id.time, true)
                }
                holder.setText(R.id.tvFrom2, TimeUtils.timeParse(duration))
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.icon_play, false)
                val album = item.album_data
                val num = String.format(
                    context.resources.getString(R.string.recommend_album_count),
                    album.include_track_count.toString()
                )
                holder.setText(R.id.time, num)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.time, item.staff)
                holder.setText(R.id.tvFrom2, item.source)
            }
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {
                holder.setText(R.id.time, item.staff)
                holder.setText(R.id.tvFrom2, item.source)
            }
            ResourceTypeConstans.TYPE_COLUMN -> {
                holder.setText(R.id.org, item.desc)
                holder.setText(R.id.time, item.update_time)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.time, item.writer)
                holder.setText(R.id.tvFrom2, item.platform)
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setText(R.id.org, item.staff)
                holder.setText(R.id.time, item.student_num.toString() + "人")
                if (item.time_mode == "1") { //时间永久
                    holder.setText(
                        R.id.tvFrom2,
                        context.resources.getString(R.string.study_time_permanent_opening)
                    )
                } else {
                    holder.setText(R.id.tvFrom2, item.start_time)
                }
            }
            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
//                holder.setText(R.id.time, item.publish_time)
//                holder.setText(R.id.tvFrom2, item.publish_time)
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.icon_play, false)
                val musicBean = item.audio_data
                try {
                    holder.setText(
                        R.id.time,
                        String.format("播放 %s次", formatPlayCount(musicBean.audio_play_num))
                    )
                } catch (e: Exception) {
                    holder.setText(R.id.time, String.format("播放 %s次", 0))
                }
                try {
                    holder.setText(R.id.tvFrom2, TimeUtils.timeParse(musicBean.audio_time))
                } catch (e: Exception) {
                    holder.setText(R.id.tvFrom2, "")
                }
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                val updateStr =
                    "更新至" + item.periodicals_data.year + "年第" + item.periodicals_data.term + "期"
                holder.setText(R.id.org, updateStr)
                holder.setText(R.id.time, item.periodicals_data.unit)
            }
            ResourceTypeConstans.TYPE_TASK -> {
                val score = getSpaString(
                    item.success_score,
                    ContextCompat.getColor(context, R.color.color_5D5D5D),
                    ContextCompat.getColor(context, R.color.color_5D5D5D),
                    QMUIDisplayHelper.sp2px(context, 13),
                    QMUIDisplayHelper.sp2px(context, 15)
                )
                holder.setText(R.id.org, score)
                holder.setText(R.id.time, item.join_num + "人参与")
            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {
                holder.setText(R.id.time, item.resource_info?.title)
                holder.setText(R.id.tvFrom2, item.resource_info?.updated_time + "更新")
            }
            else -> {
                holder.setGone(R.id.time, true)
                holder.setGone(R.id.tvFrom2, true)
            }
        }
        setBottomGone(holder)

        val time = holder.getViewOrNull<TextView>(R.id.time)
        val tvFrom = holder.getViewOrNull<TextView>(R.id.tvFrom2)
        if (TextUtils.isEmpty(time?.text) && TextUtils.isEmpty(
                tvFrom!!.text
            )
        ) {
            holder.setGone(R.id.time, true)
            holder.setGone(R.id.tvFrom2, true)
        } else {
            holder.setGone(R.id.time, false)
            holder.setGone(R.id.tvFrom2, false)
        }
    }

    private fun setBottomGone(helper: BaseViewHolder) {
        val tvOrg = helper.getViewOrNull<View>(R.id.org)!!
        if (tvOrg is TextView) {
            val org = tvOrg.text.toString()
            helper.setGone(R.id.org, TextUtils.isEmpty(org))
        }
    }
}