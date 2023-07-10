package com.mooc.search.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.common.utils.DateStyle
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.model.studyproject.StudyProject
import com.mooc.search.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class StudyProAdapter(
    data: MutableList<StudyProject>?,
    layoutResId: Int = R.layout.search_item_study
) : BaseQuickAdapter<StudyProject, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: StudyProject) {
        holder.setText(R.id.tv_title, item.plan_name)
        holder.setText(R.id.tv_people_num, String.format("%s人", item.plan_num))

        if (item.plan_start_users.size != null) {
            if (item.plan_start_users.size > 1) {
                holder.setText(
                    R.id.tv_source,
                    String.format("发起人:%s", item.plan_start_users[0].name)
                )
            } else if (item.plan_start_users.size == 1) {
                holder.setText(
                    R.id.tv_source,
                    String.format("发起人:%s等", item.plan_start_users[0].name)
                )
            } else {
                holder.setText(R.id.tv_source, "发起人:无")
            }
        } else {
            holder.setText(R.id.tv_source, "发起人:无")
        }
        if (item.time_mode == 1) {//时间永久
            holder.setText(
                R.id.tv_date,
                context.resources.getString(R.string.study_time_permanent_opening)
            )
        } else {
            holder.setText(
                R.id.tv_date,
                String.format(
                    "%s-%s",
                    DateUtil.StringToString(item.plan_starttime, DateStyle.YYYY_MM_DD_SPOT),
                    DateUtil.StringToString(item.plan_endtime, DateStyle.YYYY_MM_DD_SPOT)
                )
            )
        }


        var start: Date? = null
        var end: Date? = null
        var join_start: Date? = null
        var join_end: Date? = null
        var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            start = simpleDateFormat.parse(item.plan_starttime)
            end = simpleDateFormat.parse(item.plan_endtime)
            join_start = simpleDateFormat.parse(item.join_start_time)
            join_end = simpleDateFormat.parse(item.join_end_time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (start != null && end != null && join_start != null && join_end != null) {
            when (isEnrolment(DateUtil.getCurrentTime(), start.time, end.time)) {
                0 -> {
                    when (isEnrolment(DateUtil.getCurrentTime(), join_start.time, join_end.time)) {
                        0 -> holder.setGone(R.id.state, true)
                        1 -> {
                            holder.setGone(R.id.state, false)
                            holder.setText(R.id.state, "报名中")
                        }
                        2 -> {
                            holder.setGone(R.id.state, false)
                            holder.setText(R.id.state, "进行中")
                        }
                    }
                    holder.setBackgroundResource(
                        R.id.state,
                        R.drawable.shape_radius1_5_colora310955b
                    )
                }
                1 -> {
                    holder.setGone(R.id.state, false)
                    holder.setText(R.id.state, "进行中")
                    holder.setBackgroundResource(
                        R.id.state,
                        R.drawable.shape_radius1_5_colora310955b
                    )
                }
                2 -> {
                    holder.setGone(R.id.state, false)
                    holder.setText(R.id.state, "已结束")
                    holder.setBackgroundResource(
                        R.id.state,
                        R.drawable.shape_radius20_10percent_black
                    )
                }
            }
        }

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.plan_img)
            .placeholder(R.mipmap.common_bg_cover_default)
            .error(R.mipmap.common_bg_cover_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)

    }
}

private fun isEnrolment(time: Long, startTime: Long, stopTime: Long): Int {
    return if (time < startTime) { //尚未到报名期
        0
    } else if (time in startTime..stopTime) { //报名期
        1
    } else { //报名期结束
        2
    }
}