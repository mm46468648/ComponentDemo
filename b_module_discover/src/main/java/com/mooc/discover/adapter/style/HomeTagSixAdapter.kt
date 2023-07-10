package com.mooc.discover.adapter.style

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.column.ui.columnall.delegate.ColumnTagSixDelegate
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.typeStringMap
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.utils.TaskScoreUtils.Companion.getSpaString
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.formatPlayCount
import com.mooc.discover.R
import com.mooc.discover.model.RecommendColumn
import com.qmuiteam.qmui.util.QMUIDisplayHelper

class HomeTagSixAdapter(data: ArrayList<RecommendColumn>?) :
    BaseDelegateMultiAdapter<RecommendColumn, BaseViewHolder>(data) {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    init {
        setMultiTypeDelegate(ColumnTagSixDelegate())
    }

    override fun convert(holder: BaseViewHolder, item: RecommendColumn) {
        val itemViewType: Int = holder.getItemViewType()
        if (itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) return

        holder.setGone(R.id.icon_play, true)

        //资源类型
        val value = typeStringMap[item.type]
        holder.setText(R.id.tv_identify, value)
        holder.setGone(R.id.tv_identify, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeHomeSix, value)
        holder.setGone(R.id.tvTypeHomeSix, true)

        //标题
        holder.setText(R.id.title, item.title)

        //封面图
        Glide.with(context)
            .load(item.small_image)
            .placeholder(R.mipmap.common_bg_cover_big_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into((holder.getView<View>(R.id.cover) as ImageView)!!)

        //二三行初始化
        holder.setText(R.id.org, "")
        holder.setText(R.id.time, "")

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
                    val video_duration: Long
                    video_duration = try {
                        item.video_duration.toLong()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    holder.setText(R.id.time, TimeUtils.timeParse(video_duration))
                }
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setText(R.id.org, item.org)
                holder.setText(R.id.time, item.start_time)
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tvTypeHomeSix, item.identity_name)
                    holder.setGone(R.id.tvTypeHomeSix, false)
                    holder.setGone(R.id.tv_identify, true)
                } else {
                    holder.setGone(R.id.tvTypeHomeSix, true)
                    holder.setGone(R.id.tv_identify, false)
                }
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.icon_play, false)
                val (_, _, _, _, subordinated_album, _, _, _, _, duration) = item.track_data
                if (subordinated_album != null) {
                    holder.setText(R.id.org, subordinated_album!!.album_title)
                }
                holder.setText(R.id.time, TimeUtils.timeParse(duration))
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
                var staff = item.staff
                val strs = staff.split(",").toTypedArray()
                if (strs.size > 1) {
                    staff = strs[0] + "等"
                }
                holder.setText(R.id.org, staff)
                holder.setText(R.id.time, item.source)
            }
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {
                var artStaff = item.staff
                val artStaffstrs = artStaff.split(",").toTypedArray()
                if (artStaffstrs.size > 1) {
                    artStaff = artStaffstrs[0] + "等"
                }
                holder.setText(R.id.org, artStaff)
                holder.setText(R.id.time, item.source)
            }
            ResourceTypeConstans.TYPE_COLUMN -> {
                holder.setText(R.id.org, item.desc)
                holder.setText(R.id.time, item.update_time)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.org, item.writer)
                holder.setText(R.id.time, item.platform)
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                var studyPlanstaff = item.staff
                val studyPlanstaffstrs = studyPlanstaff.split(",").toTypedArray()
                if (studyPlanstaffstrs.size > 1) {
                    studyPlanstaff = studyPlanstaffstrs[0] + "等"
                }
                holder.setText(R.id.org, studyPlanstaff)
                holder.setText(R.id.time, item.student_num.toString() + "人")
            }
            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
//                holder.setText(R.id.org, item.about)
//                holder.setText(R.id.time, item.publish_time)
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
                    holder.setText(R.id.time, TimeUtils.timeParse(musicBean.audio_time))
                } catch (e: Exception) {
                    holder.setText(R.id.time, "")
                }
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                val updateStr =
                    "更新至" + item.periodicals_data.year + "年 第" + item.periodicals_data.term + "期"
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
                holder.setText(R.id.org, item.resource_info?.title)
                holder.setText(R.id.time, item.resource_info?.updated_time + "更新")
            }
        }

        val tvOrg = holder.getView<TextView>(R.id.org)
        if(TextUtils.isEmpty(tvOrg.text)){
            tvOrg.visibility = View.GONE
        }else{
            tvOrg.visibility = View.VISIBLE
        }

        val tvTime = holder.getView<TextView>(R.id.time)
        if(TextUtils.isEmpty(tvTime.text)){
            tvTime.visibility = View.GONE
        }else{
            tvTime.visibility = View.VISIBLE
        }

    }
}