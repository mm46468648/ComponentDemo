package com.mooc.discover.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.model.studyproject.StudyProject
import com.mooc.discover.R
import com.mooc.resource.widget.MoocImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StudyProjectAdapter(list: ArrayList<StudyProject>) :
    BaseQuickAdapter<StudyProject, BaseViewHolder>(R.layout.home_item_discover_studyproject, list),
    LoadMoreModule {
    private var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override fun convert(holder: BaseViewHolder, item: StudyProject) {
        //封面图
        val ivCouver = holder.getView<MoocImageView>(R.id.ivCover)
//        view.setImageUrl(item.plan_img, 2.dp2px())
//        Glide.with(context).load(item.plan_img).centerCrop().transform(RoundedCorners(2.dp2px())).into(ivCouver)
        Glide.with(context).load(item.plan_img).transform(GlideTransform.centerCropAndRounder2)
            .into(ivCouver)

        //计划名称
        holder.setText(R.id.tvTitle, item.plan_name)

        //人数,时间
        holder.setText(R.id.tvNum, "${item.plan_num}人")

        if (item.time_mode == 1) {//时间永久
            holder.setText(
                R.id.tvTime,
                context.resources.getString(R.string.study_time_permanent_opening)
            )
        } else {
            val startTimeStr = if (item.plan_starttime.length >= 10) item.plan_starttime.substring(
                0,
                10
            ) else item.plan_starttime
            val endTimeStr = if (item.plan_starttime.length >= 10) item.plan_endtime.substring(
                0,
                10
            ) else item.plan_endtime
            holder.setText(R.id.tvTime, "${startTimeStr} - ${endTimeStr}")
        }

        //发起人
        var startUserStr = "无"
        if (item.plan_start_users.isNotEmpty()) {
            val stringBuffer = StringBuffer(item.plan_start_users[0].name ?: "")
            if (item.plan_start_users.size > 1) {
                stringBuffer.append("等")
//                stringBuffer.append("、")
//                stringBuffer.append(item.plan_start_users[1].name ?: "")
            }
//            if (item.plan_start_users.size > 2) {
//                stringBuffer.append(item.plan_start_users[1].name ?: "")
//            }
            startUserStr = stringBuffer.toString()
        }
        holder.setText(R.id.tvOrg, "发起人: $startUserStr")

        var start: Date? = null
        var end: Date? = null
        var join_start: Date? = null
        var join_end: Date? = null
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
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
                        0 -> holder.setGone(R.id.tv_status, true)
                        1 -> {
                            holder.setGone(R.id.tv_status, false)
                            holder.setText(R.id.tv_status, "报名中")
                        }
                        2 -> {
                            holder.setGone(R.id.tv_status, false)
                            holder.setText(R.id.tv_status, "进行中")
                        }
                    }
                    holder.setBackgroundResource(R.id.tv_status, R.drawable.bg_plan_light_green)
                }
                1 -> {
                    holder.setGone(R.id.tv_status, false)
                    holder.setText(R.id.tv_status, "进行中")
                    holder.setBackgroundResource(R.id.tv_status, R.drawable.bg_plan_light_green)
                }
                2 -> {
                    holder.setGone(R.id.tv_status, false)
                    holder.setText(R.id.tv_status, "已结束")
                    holder.setBackgroundResource(R.id.tv_status, R.drawable.bg_plan_gray)
                }
            }
        }

    }

    /**
     * 报名的类型
     */
    fun isEnrolment(time: Long, startTime: Long?, stopTime: Long?): Int {
        if (startTime != null && stopTime != null) {
            return if (time < startTime) { //尚未到报名期
                0
            } else if (time >= startTime && time <= stopTime) { //报名期
                1
            } else { //报名期结束
                2
            }
        }
        return 2

    }
}