package com.mooc.studyroom.ui.adapter

import android.text.Html
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.utils.format.TimeFormatUtil.formatDateISO4YMMDD
import com.mooc.studyroom.R
import com.mooc.studyroom.model.CourseMsgDetailBean

/**

 * @Author limeng
 * @Date 2021/3/11-3:50 PM
 */
class CourseMsgDetailAdapter(data: ArrayList<CourseMsgDetailBean>) : BaseQuickAdapter<CourseMsgDetailBean, BaseViewHolder>(R.layout.studyroom_item_course_detail_msg, data) {
    override fun convert(holder: BaseViewHolder, data: CourseMsgDetailBean) {
        holder.setText(R.id.msgTitle, data.course_title)
        holder.setText(R.id.msgTime, formatDateISO4YMMDD(data.created_time))
        holder.setText(R.id.msgContent, Html.fromHtml(data.content))
    }
}