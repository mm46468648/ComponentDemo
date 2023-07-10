package com.mooc.studyproject.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.model.studyproject.StudyPlan
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.timeToString
import com.mooc.studyproject.R
import com.mooc.studyproject.model.StudyClockState
import com.mooc.studyproject.model.StudyPlanSource

/**

 * @Author liM
 * @Date 2020/12/7-4:40 PM
 */
class StudySourceAdapter(data: ArrayList<StudyPlanSource>?) : BaseQuickAdapter<StudyPlanSource, BaseViewHolder>
(R.layout.studyproject_item_plan_study, data), LoadMoreModule {
    var mJoin = 0 //0未加入加入
    var isDaShiMode = false
    var mStudyPlanData: StudyPlan? = null

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun convert(holder: BaseViewHolder, item: StudyPlanSource) {

        //设置是否可看  mJoin:加入计划  0:未加入      is_lock_up：是否锁定  0：未加锁    set_is_listen_test：试听  0 没有 1 有 -1  该资源没有被加入学习计划
        setDaShiStatus(holder, item)
        mStudyPlanData?.let {
            if (isUnStartOrStop(DateUtil.getCurrentTime(), it.plan_starttime * 1000, it.plan_endtime * 1000) == 2) {
                item.is_lock_up = 0
            }
        }

        if (item.is_lock_up == 0) {// 未加锁
            holder.getView<TextView>(R.id.tvSign).setCompoundDrawables(null, null, null, null)
            if (mJoin == 0) { //未加入计划
                holder.setGone(R.id.tvSign, true)
                holder.setGone(R.id.tvTimeSign, true)
                holder.setGone(R.id.tvBeforeResource, true)
                setDaShiStatus(holder, item)
            } else {
                if (item.checkin_start_time > 0 && item.checkin_end_time > 0) {
                    holder.setGone(R.id.tvTimeSign, false)
                    holder.setText(R.id.tvTimeSign, String.format(context.resources.getString(R.string.study_sign_time_plan),
                            timeToString(item.checkin_start_time), timeToString(item.checkin_end_time)))

                } else {
                    holder.setGone(R.id.tvTimeSign, true)
                }


                val checkBool: Int = item.checkin_bool
                if (checkBool == 0) { //不需要打卡
                    holder.setGone(R.id.tvSign, true)
                    holder.setGone(R.id.tvBeforeResource, true)
                } else { //需要打卡
                    holder.setGone(R.id.tvSign, false)
                    holder.setGone(R.id.tvBeforeResource, true)

                    setBeforeCheck(holder, item)
                    val studyClockState: StudyClockState? = item.button
                    if (!studyClockState?.text.isNullOrEmpty()) {
                        if (!TextUtils.isEmpty(studyClockState?.text)) {
                            holder.setText(R.id.tvSign, studyClockState?.text)
                        }
                        if (!TextUtils.isEmpty(studyClockState?.click)) {
                            if (studyClockState?.click?.toBoolean() != null) {
                                holder.setEnabled(R.id.tvSign, studyClockState.click?.toBoolean()
                                        ?: false)

                            }
                        } else {
                            holder.setEnabled(R.id.tvSign, false)
                        }
                        if (!TextUtils.isEmpty(studyClockState?.title_color_code)) {
                            when (studyClockState?.title_color_code) {
                                "1" -> { //字体颜色为灰色
                                    holder.setTextColor(R.id.tvSign, context.resources.getColor(R.color.color_9))
                                }
                                "2" -> {
                                    holder.setTextColor(R.id.tvSign, context.resources.getColor(R.color.color_white))

                                }
                                "3" -> {
                                    holder.setTextColor(R.id.tvSign, context.resources.getColor(R.color.color_00CF5A))
                                }
                            }
                        } else {
                            holder.setTextColor(R.id.tvSign, context.resources.getColor(R.color.color_9))
                        }
                        if (!TextUtils.isEmpty(studyClockState?.background_color_code)) {
                            item.is_re_chick = false
                            when (studyClockState?.background_color_code) {
                                "1" -> { //背景颜色为灰色
                                    holder.setBackgroundResource(R.id.tvSign, R.mipmap.studyproject_iv_sign_gray)
                                }
                                "2" -> {
                                    holder.setBackgroundResource(R.id.tvSign, R.mipmap.studyproject_iv_sign_plan)

                                }
                                "3" -> {
                                    item.is_re_chick = true
                                    holder.setBackgroundResource(R.id.tvSign, R.mipmap.studyproject_iv_sign_red)

                                }
                                "4" -> {
                                    holder.setBackgroundResource(R.id.tvSign, R.drawable.studyproject_bg_success_sign)

                                }
                            }
                        } else {
                            item.is_re_chick = false
                            holder.setBackgroundResource(R.id.tvSign, R.mipmap.studyproject_iv_sign_gray)

                        }
                    } else {
                        holder.setGone(R.id.tvSign, true)
                        holder.setGone(R.id.tvTimeSign, true)
                        holder.setGone(R.id.tvBeforeResource, true)

                    }
                }
            }
        } else { //加锁
            setBeforeCheck(holder, item)
            setSign(holder, false, R.drawable.studyproject_bg_success_sign, R.color.color_00CF5A)
            holder.setText(R.id.tvSign, "")
            @Suppress("DEPRECATION")
            val drawable: Drawable = context.resources.getDrawable(R.mipmap.studyproject_iv_lock_up)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            holder.getView<TextView>(R.id.tvSign).setCompoundDrawables(drawable, null, null, null)
            if (item.set_resource_show_time > 0) {
                holder.setGone(R.id.tvSign, false)
                holder.setGone(R.id.tvTimeSign, false)
                holder.setText(R.id.tvTimeSign, java.lang.String.format(context.resources.getString(R.string.study_lock_time),
                        DateUtil.timeToString(item.set_resource_show_time * 1000, "yyyy.MM.dd HH:mm")))
            } else {
                holder.setGone(R.id.tvSign, true)
                holder.setGone(R.id.tvTimeSign, true)
            }
        }
        holder.setText(R.id.tvType, item.show_type)
        holder.setText(R.id.tvTitle, item.source_title)

    }

    private fun setDaShiStatus(holder: BaseViewHolder, item: StudyPlanSource) {
        if (isDaShiMode) {   //大师课模式
            if (mJoin != 0) {   //已报名
                if (item.is_lock_up == 0) {  //解锁
                    //试听否
                    setCanLook(holder, true, R.mipmap.studyproject_iv_sign_plan)

                    // TODO  点击事件
//                    itemViewHolder.tvSign.setOnClickListener(View.OnClickListener {
//                        if (mSourceListener != null) {
//                            mSourceListener.onSignClick(positon, dataBean)
//                        }
//                    })
                } else {//锁定
                    setCanLook(holder, false, R.mipmap.studyproject_iv_sign_gray)


                }
            } else {  //未报名
                if (item.is_lock_up == 0) {  //解锁
                    if ("0" == item.set_is_listen_test) {   //试听否
                        setCanLook(holder, false, R.mipmap.studyproject_iv_lock_up)

                    } else {
                        setCanLook(holder, true, R.mipmap.studyproject_iv_sign_plan_green)
                        holder.setText(R.id.tvSign, R.string.try_listen)
                        holder.setTextColorRes(R.id.tvSign, R.color.color_white)
                        // TODO 点击事件
//                        itemViewHolder.tvSign.setOnClickListener(View.OnClickListener {
//                            if (mSourceListener != null) {
//                                mSourceListener.onSignClick(positon, dataBean)
//                            }
//                        })
                    }
                } else {     //锁定
                    setCanLook(holder, false, R.mipmap.studyproject_iv_sign_gray)
                }
            }
        }
    }

    private fun setCanLook(holder: BaseViewHolder, b: Boolean, mipmap: Int) {
        holder.setEnabled(R.id.tvSign, b)
        holder.setBackgroundResource(R.id.tvSign, mipmap)

    }

    @Suppress("SameParameterValue")
    private fun setSign(holder: BaseViewHolder, b: Boolean, mipmap: Int, color: Int) {
        holder.setEnabled(R.id.tvSign, b)
        holder.setBackgroundResource(R.id.tvSign, mipmap)
        holder.setTextColorRes(R.id.tvSign, color)

    }

    private fun isUnStartOrStop(time: Long, startTime: Long?, stopTime: Long?): Int {
        if (startTime != null && stopTime != null) {
            return if (time < startTime * 1000) { //尚未开始
                0
            } else if (time >= startTime * 1000 && time <= stopTime * 1000) { //可以加入计划
                1
            } else { //计划结束
                2
            }
        }
        return 2

    }

    private fun setBeforeCheck(holder: BaseViewHolder, dataBean: StudyPlanSource) {
        if (!TextUtils.isEmpty(dataBean.before_resource_check_status)) {
            val beforeResourceCheck: String = dataBean.before_resource_check_status
            val beforeResourceInfo = dataBean.before_resource_info
            if (beforeResourceCheck == "0") {
                holder.setGone(R.id.tvBeforeResource, true)
            } else {
                if (beforeResourceInfo != null) {
                    if (beforeResourceInfo.code.isNotEmpty()) {
                        val code = beforeResourceInfo.code
                        if (code == "3") {
                            holder.setGone(R.id.tvBeforeResource, true)
                        } else {
                            if (!TextUtils.isEmpty(beforeResourceInfo.msg)) {
                                holder.setGone(R.id.tvBeforeResource, false)

                                holder.setText(R.id.tvBeforeResource, beforeResourceInfo.msg)
                            } else {
                                holder.setGone(R.id.tvBeforeResource, true)
                            }
                        }
                    } else {
                        holder.setGone(R.id.tvBeforeResource, true)

                    }
                } else {
                    holder.setGone(R.id.tvBeforeResource, true)
                }
            }
        } else {
            holder.setGone(R.id.tvBeforeResource, true)
        }
    }

}