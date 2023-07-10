@file:Suppress("DEPRECATION")

package com.mooc.commonbusiness.module.studyroom.course

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.interfaces.BaseEditableAdapter
import com.mooc.commonbusiness.interfaces.EditableAdapterInterface
import com.mooc.commonbusiness.model.search.CourseBean
import java.text.DecimalFormat

/**
 * 实现公共可编辑接口，统一实现显示编辑弹窗逻辑
 */
class CourseAdapter(list: ArrayList<CourseBean>) :
    BaseEditableAdapter<CourseBean>(R.layout.home_item_studyroom_course, list),
    EditableAdapterInterface, LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: CourseBean) {
        holder.setText(R.id.tvTitle, item.title)
        Glide.with(context)
            .load(item.picture)
            .error(R.mipmap.common_bg_cover_default)
            .placeholder(R.mipmap.common_bg_cover_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(holder.getView(R.id.ivCover))

//        helper.getView<MoocImageView>(R.id.ivCover).setImageUrl(detailBean.picture, 2)
        holder.setText(R.id.tvOrg, item.org)
        holder.setText(R.id.tvPlatform, item.platform_zh)


//        val platFrom: Int = detailBean.platform
        //不同平台显示不同数据（得分，已学，作业）
        when (item.platform) {
            //1、显示已学:  好大学在线、八一学院
            CoursePlatFormConstants.COURSE_GOOD_COLLEGE, CoursePlatFormConstants.COURSE_EIGHT_ONE -> {
                holder.setGone(R.id.tvGet, false)
                holder.setGone(R.id.tv_task, true)
                holder.setGone(R.id.tvScore, true)

                setSpan(holder.getView(R.id.tvGet), getProces(item.learned_process))
            }
            //2、显示得分、已学:  高校邦、重走长征路 、工信部、中国大学mooc
            CoursePlatFormConstants.COURSE_HIGHT_SCHOOL_UNIN, CoursePlatFormConstants.COURSE_EXPEDITION, CoursePlatFormConstants.COURSE_PLATFORM_MOOC -> {
                holder.setGone(R.id.tvGet, false)
                holder.setGone(R.id.tv_task, true)
                holder.setGone(R.id.tvScore, false)

                setSpan(holder.getView(R.id.tvGet), getProces(item.learned_process))
                val score = java.lang.String.format("得分: %s", item.score)
                setSpan(holder.getView(R.id.tvScore), score)
                //得分为0的时候还需要隐藏
                holder.setGone(R.id.tvScore, "0" == item.score || "0.0" == item.score)
            }
            //3、显示得分、已学、作业得分/作业总分:  学堂（旧）、学堂在线、智慧树
            CoursePlatFormConstants.COURSE_PLATFORM_XTZX, CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT, CoursePlatFormConstants.COURSE_PLATFORM_ZHS -> {
                holder.setGone(R.id.tvGet, false)
                holder.setGone(R.id.tv_task, false)
                holder.setGone(R.id.tvScore, false)

                setSpan(holder.getView(R.id.tvGet), getProces(item.learned_process))
                val task = java.lang.String.format(
                    "作业: %s",
                    if (TextUtils.isEmpty(item.exercise_process)) "0" else item.exercise_process
                )
                setSpan(holder.getView(R.id.tv_task), task)

                val score = java.lang.String.format("得分: %s", item.score)
                setSpan(holder.getView(R.id.tvScore), score)

                //得分为0的时候还需要隐藏
                holder.setGone(R.id.tvScore, "0" == item.score || "0.0" == item.score)
            }
            //4.工信部的课区分免费付费
            CoursePlatFormConstants.COURSE_PUBLIC_MESSAGE -> {
                holder.setGone(R.id.tv_task, true)
                if (item.is_free == "true") {
                    //免费，在右边显示已学
                    holder.setGone(R.id.tvScore, true)
                } else {
                    //付费，显示已学和得分
                    if (item.is_show_score == true) {//显示得分
                        holder.setGone(R.id.tvScore, false)
                    } else {
                        holder.setGone(R.id.tvScore, true)
                    }
                }
                holder.setGone(R.id.tvGet, false)
                val score = java.lang.String.format("得分: %s", item.score)
                setSpan(holder.getView(R.id.tvScore), score)
                setSpan(holder.getView(R.id.tvGet), getProces(item.learned_process))
//
//                //得分为0的时候还需要隐藏
//                helper.setGone(R.id.tvScore, "0" == detailBean.score || "0.0" == detailBean.score)

            }
            //其他都隐藏
            else -> {
                holder.setGone(R.id.tvGet, true)
                holder.setGone(R.id.tv_task, true)
                holder.setGone(R.id.tvScore, true)
            }
        }
    }

    /**
     * 获取已学百分比字段
     */
    private fun getProces(learned_process: String): String {
        var process = 0.0
        try {
            process = learned_process.toDouble()
        } catch (e: Exception) {

        }

        return if (process == 0.0) {
            "已学: 0%"
        } else {
            "已学: ${getNoMoreThanTwoDigits(process)}"
//            "已学: ${getNoMoreThanTwoDigits(process)} %"
        }
    }

    fun getNoMoreThanTwoDigits(number: Double): String {
        return DecimalFormat("#0.00%").format(number)
    }


    private fun setSpan(textView: TextView, str: String) {

        //换肤
        val skinColor = if (SkinManager.getInstance().needChangeSkin()) {
            SkinManager.getInstance().resourceManager.getColor("colorPrimary")
        } else {
            context.getColorRes(R.color.colorPrimary)
        }

        val spannableString = SpannableString(str)
        spannableString.setSpan(
            ForegroundColorSpan(
                context.resources.getColor(R.color.color_5D5D5D)
            ), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(
                skinColor
            ), 3, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableString
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        //课程不需要
        removeAllFooterView()
    }
}

