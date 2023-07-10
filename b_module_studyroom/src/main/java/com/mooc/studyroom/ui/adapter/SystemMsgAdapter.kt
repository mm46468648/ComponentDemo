package com.mooc.studyroom.ui.adapter

import android.text.Html
import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.TYPE_RECOMMEND_OUT_LINK
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.studyroom.R
import com.mooc.studyroom.model.SystemMsgBean
import com.mooc.studyroom.model.SystemResourceBean

/**
系统消息适配器
 * @Author limeng
 * @Date 2021/3/5-11:00 AMstudyroom_item_course_msg
 */
class SystemMsgAdapter(data: ArrayList<SystemMsgBean>?) : BaseQuickAdapter<SystemMsgBean, BaseViewHolder>(R.layout.studyroom_item_system_msg, data),LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: SystemMsgBean) {

        holder.setText(R.id.msgTime, TimeFormatUtil.formatDateISO4YMMDD(item.created_time))
        holder.setText(R.id.msgContent, Html.fromHtml(item.content.toString().trim()))
        var otherBean = item.others
        if (!isHasLinkOrWay(otherBean)) {// 有链接和way
            holder.setGone(R.id.llResource, false)
            var way=otherBean?.resource_way
            var resourceType=otherBean?.type
            var type: Int
            type = if (!TextUtils.isEmpty(resourceType)) {
               if( resourceType?.toInt()==null) 0 else resourceType?.toInt()!!
            } else {
                TYPE_RECOMMEND_OUT_LINK
            }
            var title: String?=null
            if (way == 1) { //外部链接
                title = String.format(context.getResources().getString(R.string.resource_link), otherBean?.link)
            } else {
                when (type) {
                    ResourceTypeConstans.TYPE_COURSE -> title = String.format(context.getResources().getString(R.string.resource_course), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_KNOWLEDGE -> title = String.format(context.getResources().getString(R.string.resource_know), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_PERIODICAL -> title = String.format(context.getResources().getString(R.string.resource_periodical), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> title = String.format(context.getResources().getString(R.string.resource_article), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> title = String.format(context.getResources().getString(R.string.resource_link), otherBean?.link)
                    ResourceTypeConstans.TYPE_E_BOOK -> title = String.format(context.getResources().getString(R.string.resource_ebook), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_STUDY_PLAN -> title = String.format(context.getResources().getString(R.string.resource_study_plan), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_SPECIAL -> title = String.format(context.getResources().getString(R.string.resource_special), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_COLUMN -> title = String.format(context.getResources().getString(R.string.resource_column), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_ACTIVITY -> title = String.format(context.getResources().getString(R.string.resource_activity), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_ACTIVITY_TASK -> title = String.format(context.getResources().getString(R.string.resource_activity_task), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_BAIKE -> title = String.format(context.getResources().getString(R.string.resource_baike), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_TEST_VOLUME -> title = String.format(context.getResources().getString(R.string.resource_test_volume), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_QUESTIONNAIRE -> title = String.format(context.getResources().getString(R.string.resource_questionnaire), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_TRACK -> title = String.format(context.getResources().getString(R.string.resource_track), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_ALBUM -> title = String.format(context.getResources().getString(R.string.resource_album), otherBean?.resource_title)
                    ResourceTypeConstans.TYPE_NOTE -> title = String.format(context.getResources().getString(R.string.resource_note), otherBean?.resource_title)
                    25 -> title = String.format(context.getResources().getString(R.string.resource_master), otherBean?.resource_title)
                    else -> title = String.format(context.getResources().getString(R.string.resource_other), otherBean?.resource_title)
                }
            }
            holder.setText(R.id.tvResource,title)

        }else{
            holder.setGone(R.id.llResource, true)

        }



    }

    /**
     * 是否有链接等配置
     */
    private fun isHasLinkOrWay(resourceBean: SystemResourceBean?): Boolean {
        return if (resourceBean != null) {
            val way: Int = resourceBean.resource_way
            val type = resourceBean.type
            if (way == 0) {
                TextUtils.isEmpty(type)
            } else {
                TextUtils.isEmpty(resourceBean.link)
            }
        } else {
            true
        }
    }

}