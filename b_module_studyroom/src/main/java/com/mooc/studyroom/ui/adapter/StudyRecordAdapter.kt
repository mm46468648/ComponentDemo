package com.mooc.studyroom.ui.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.resource.widget.tagtext.TagTextView
import com.mooc.studyroom.R
import com.mooc.studyroom.model.StudyRecordBean
import java.util.*

class StudyRecordAdapter(list : ArrayList<StudyRecordBean>)
    : BaseQuickAdapter<StudyRecordBean,BaseViewHolder>(R.layout.studyroom_item_studyrecord,list),LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: StudyRecordBean) {
        val learnRecordBean = item
        val position = holder.adapterPosition

        val time_line_top = holder.getView<View>(R.id.time_line_top)
        val detaile_line_bottom = holder.getView<View>(R.id.detaile_line_bottom)
        val clTime = holder.getView<View>(R.id.clTime)
        val tvTime = holder.getView<TextView>(R.id.tv_time_record)
        val tvTitle = holder.getView<com.mooc.resource.widget.tagtext.TagTextView>(R.id.tv_title_record)

        //时间进度条顶部
        val timeTipVisibiable = if (position == 0) View.GONE else View.VISIBLE
        time_line_top.setVisibility(timeTipVisibiable)
        //详情进度条底部
        //详情进度条底部
        val detaliTipVisibable = if (position == data.size) View.GONE else View.VISIBLE
        detaile_line_bottom.setVisibility(detaliTipVisibable)

        if (position == 0) {
            var time = ""
            if (learnRecordBean.learn_date > 0) {
//                time = DateUtil.timeToString(learnRecordBean.getLearn_date() * 1000, "yyyy-MM-dd")
                time = TimeFormatUtil.formatDate(learnRecordBean.learn_date * 1000,TimeFormatUtil.yyyy_MM_dd)
            }
            tvTime.setVisibility(View.VISIBLE)
            tvTime.setText(time)
            clTime.setVisibility(View.VISIBLE)
        } else {     // 大于0的位置以后
            var time = ""
            if (learnRecordBean.learn_date > 0) {
//                time = DateUtil.timeToString(learnRecordBean.learn_date * 1000, "yyyy-MM-dd")
                time = TimeFormatUtil.formatDate(learnRecordBean.learn_date * 1000,TimeFormatUtil.yyyy_MM_dd)
            }
            var lastTime = ""
            val lastlearnRecordBean = data.get(position - 1)
            if (lastlearnRecordBean.learn_date > 0) {
//                lastTime = DateUtil.timeToString(currentDataList.get(position - 1).getLearn_date() * 1000, "yyyy-MM-dd")
                lastTime = TimeFormatUtil.formatDate(lastlearnRecordBean.learn_date * 1000,TimeFormatUtil.yyyy_MM_dd)

            }
            if (time == lastTime) {
                clTime.setVisibility(View.GONE)
            } else {
                clTime.setVisibility(View.VISIBLE)
                tvTime.setText(time)
            }
        }


        val type: Int = learnRecordBean.type

        val tags = arrayOf<String>(ResourceTypeConstans.typeStringMap[type]?:"")
//        tvTitle.setContentAndTag(learnRecordBean.getTitle(),tags);
        //        tvTitle.setContentAndTag(learnRecordBean.getTitle(),tags);
        tvTitle.setTagStart(Arrays.asList(*tags), learnRecordBean.title)

//        parent.setOnClickListener(View.OnClickListener { v: View? ->
//            if (mOnItemClickListener != null) {
//                mOnItemClickListener.OnItemClick(position, learnRecordBean)
//            }
//        })
    }
}