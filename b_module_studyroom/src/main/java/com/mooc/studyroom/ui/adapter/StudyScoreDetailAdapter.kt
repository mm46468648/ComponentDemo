package com.mooc.studyroom.ui.adapter

import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.studyroom.R
import com.mooc.studyroom.model.ScoreDetail

class StudyScoreDetailAdapter(list: ArrayList<ScoreDetail>)
    : BaseQuickAdapter<ScoreDetail, BaseViewHolder>(R.layout.studyroom_item_score_detail, list), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: ScoreDetail) {

        //自己插入的一条年积分数据，隐藏掉，暂时去掉
//        if(item.studyplan_name == Constans.SCORE_TOTAL_STR){
//            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
//            holder.itemView.layoutParams = layoutParams
//        }else{
//            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//            holder.itemView.layoutParams = layoutParams
//        }


        val llYearScore: LinearLayout = holder.getView(R.id.llYearScore)
        llYearScore.visibility = View.GONE
        //跨年隔断去掉
//        //当前position
//        val position = holder.layoutPosition
//        //年积分view
//        var year = 0//当前年
//        if (item.year_date.isNotEmpty()) {
//            year = item.year_date.toInt()
//        }
//        if (position == 0) {
//            llYearScore.visibility = View.GONE
//        } else {
//            if (position < data.size - 1) {
//                val scoreDetail: ScoreDetail = data[position + 1]
//                var yearBefore = 0//下个位置的年份
//                if (scoreDetail.year_date.isNotEmpty()) {
//                    yearBefore = scoreDetail.year_date.toInt()
//                    //当前位置显示下一年的年和年积分
//                    holder.setText(R.id.tvYear, String.format(context.resources.getString(R.string.str_year), scoreDetail.year_date))
//                    holder.setText(R.id.tvYearScore, String.format(context.resources.getString(R.string.str_year_score), scoreDetail.year_score))
//                } else {
//                    holder.setText(R.id.tvYear, "")
//                    holder.setText(R.id.tvYearScore, "")
//                }
//                if (year == yearBefore) {//和之前年份相同则不显示年份
//                    llYearScore.visibility = View.GONE
//                } else {
//                    llYearScore.visibility = View.VISIBLE
//                }
//            } else if (position == data.size - 1) {
//                llYearScore.visibility = View.GONE
//            }
//
//        }
        holder.setText(R.id.tvSource, item.source)
        val formatDate = TimeFormatUtil.formatDate(item.add_time * 1000, TimeFormatUtil.yyyy_MM_dd_HH_mm_ss)
        holder.setText(R.id.tvTime, formatDate)



        when {
            item.score == 0 -> {
                holder.setText(R.id.tvScoreDetail, "-${item.score}")
                holder.setTextColor(R.id.tvScoreDetail, context.getColorRes(R.color.color_2))
            }
            item.score > 0 -> {
                holder.setText(R.id.tvScoreDetail, "+${item.score}")
                holder.setTextColor(R.id.tvScoreDetail, context.getColorRes(R.color.colorPrimary))
            }
            item.score < 0 -> {
                holder.setText(R.id.tvScoreDetail, "${item.score}")
                holder.setTextColor(R.id.tvScoreDetail, context.getColorRes(R.color.color_2))
            }

        }
        holder.setText(R.id.tvAllScore, "总积分: ${item.current_score}")

        //等后台返回数据
        if (!TextUtils.isEmpty(item.studyplan_name)) {
            holder.setGone(R.id.tvSubtitle, false)
            holder.setText(R.id.tvSubtitle, item.studyplan_name)
        } else {
            holder.setGone(R.id.tvSubtitle, true)
            holder.setText(R.id.tvSubtitle, "")
        }
    }
}


