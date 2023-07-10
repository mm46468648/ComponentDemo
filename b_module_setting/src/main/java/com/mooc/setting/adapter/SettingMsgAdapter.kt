package com.mooc.setting.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.setting.R
import com.mooc.setting.model.SettingMsgBean

/**

 * @Author limeng
 * @Date 2021/2/3-2:07 PM
 */
class SettingMsgAdapter(data : MutableList<SettingMsgBean>?) :BaseMultiItemQuickAdapter<SettingMsgBean, BaseViewHolder>() {
    init {
        addItemType(1,R.layout.setting_item_setting_msg)
        addItemType(2,R.layout.setting_item_msg_notice)
    }
    override fun convert(holder: BaseViewHolder, item: SettingMsgBean) {
        when (item.itemType) {
            1 -> {//开关的item  statusItem 2课程 1公告的
                when (item.statusItem) {
                    "1" -> {

                        holder.setBackgroundResource(R.id.iv_setting_msg,R.mipmap.setting_iv_interaction_msg)

                        holder.setText(R.id.tv_interaction_setting_msg, item.title)
                        if (item.status == true) {
                            holder.setBackgroundResource(R.id.sw_setting_msg,R.mipmap.setting_iv_switch_open)
                        } else {
                            holder.setBackgroundResource(R.id.sw_setting_msg,R.mipmap.setting_iv_switch_close)
                        }
                    }
                    "2" -> {
                        holder.setText(R.id.tv_interaction_setting_msg, item.title)

                        holder.setBackgroundResource(R.id.iv_setting_msg,R.mipmap.setting_iv_course_msg)
                        if ("0" == item.notice_status) {
                            holder.setBackgroundResource(R.id.sw_setting_msg,R.mipmap.setting_iv_switch_open)
                        } else {
                            holder.setBackgroundResource(R.id.sw_setting_msg,R.mipmap.setting_iv_switch_close)
                        }
                    }
                    else -> {
                    }
                }
            }
            2 -> {//显示课程公告开关的提示item

            }
            else -> {

            }
        }
    }
}