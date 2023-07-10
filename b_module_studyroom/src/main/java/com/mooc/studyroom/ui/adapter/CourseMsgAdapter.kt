package com.mooc.studyroom.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.studyroom.R
import com.mooc.studyroom.model.CourseMsgBean

/**

 * @Author limeng
 * @Date 2021/3/5-11:00 AM
 */
class CourseMsgAdapter(data: ArrayList<CourseMsgBean>?) : BaseQuickAdapter<CourseMsgBean, BaseViewHolder>(R.layout.studyroom_item_course_msg,data) ,LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: CourseMsgBean) {
        holder.setText(R.id.tv_course_msg, item.title)
        if (item.message_type == 5) {
            holder.setBackgroundResource(R.id.iv_course_msg, R.mipmap.setting_iv_course_msg)
        }


        if (item.unread_num > 0) {
            holder.setGone(R.id.iv_course_oval_remind, false)
        } else {
            holder.setGone(R.id.iv_course_oval_remind, true)

        }
    }
}