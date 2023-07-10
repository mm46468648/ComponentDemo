package com.mooc.my.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R
import com.mooc.my.model.FeedUserBean
import com.mooc.common.utils.DateUtil

/**
反馈类列表页面
 * @Author limeng
 * @Date 2020/10/16-4:22 PM
 */
class FeedListAdapter(data: ArrayList<FeedUserBean>?, layoutId: Int = R.layout.my_item_feed_list)
    : BaseQuickAdapter<FeedUserBean, BaseViewHolder>(layoutId,data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: FeedUserBean) {
        setItemView(getItemPosition(item), holder, item)
    }

    private fun setItemView(position: Int, holder: BaseViewHolder, item: FeedUserBean) {
        if (item.is_read_message.equals("0")) {
            holder.setVisible(R.id.iv_feed_read, false);
        } else {
            holder.setVisible(R.id.iv_feed_read, true);
        }
        val type: String? = item.feedback_type_content
        if (!TextUtils.isEmpty(type)) {
            holder.setText(R.id.tv_feed_list_type, String.format(context.getString(R.string.text_feed_type), type))
        } else {
            holder.setText(R.id.tv_feed_list_type, "")
        }
        if (item.descriptionFeedUserBean.isNullOrEmpty()) {
            holder.setGone(R.id.tv_feed_list_content, true)
        } else {
            holder.setVisible(R.id.tv_feed_list_content, true)
            holder.setText(R.id.tv_feed_list_content, item.descriptionFeedUserBean)
        }

        if (item.feedback_time>0) {
            holder.setVisible(R.id.tv_feed_list_time, true)
            val time: String = DateUtil.timeToString(item.feedback_time * 1000, "yyyy年MM月dd日 HH:mm")
            holder.setText(R.id.tv_feed_list_time, time)
        } else {
            if (!"0".equals(item.local_time)) {
                holder.setVisible(R.id.tv_feed_list_time, true)
                val time: String = DateUtil.timeToString(item.local_time * 1000, "yyyy年MM月dd日 HH:mm")
                holder.setText(R.id.tv_feed_list_time, time)
            } else {
                holder.setGone(R.id.tv_feed_list_time, true)
            }
        }

//        holder.parent.setOnClickListener(View.OnClickListener {
//            if (listener != null) {
//                listener.onItemClick(position, item, holder)
//            }
//        })
    }

}