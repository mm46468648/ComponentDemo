package com.mooc.my.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R
import com.mooc.my.model.FeedBackListBean
import com.mooc.my.model.FeedBackListItemBean
import com.mooc.common.utils.DateUtil

/**
反馈对话列表页面
 * @Author limeng
 * @Date 2020/10/26-5:00 PM
 */
class FeedBackListAdapter(data: ArrayList<FeedBackListItemBean>?) : BaseMultiItemQuickAdapter<FeedBackListItemBean, BaseViewHolder>() {

    companion object {
        val TYPE_SENDER = 1//发送反馈信息
        val TYPE_RECIEVER = 2//回复
    }

    var backListBean: FeedBackListBean? = null

    init {
        addItemType(TYPE_SENDER, R.layout.my_item_feed_back_list_sender)
        addItemType(TYPE_RECIEVER, R.layout.my_item_feed_back_list_reciever)
    }

    override fun convert(holder: BaseViewHolder, item: FeedBackListItemBean) {
        var lastData: String? = null
        if (getItemPosition(item) != 0) {
            if (getItemPosition(item) == 1) {
                val feedbackBean = backListBean?.feedback?.get(0)
                if (feedbackBean?.local_time != null) {
                    lastData = DateUtil.timeToString(feedbackBean.local_time * 1000, "MM月dd日")

                }
            } else {
                val replyBean = backListBean?.reply?.get(getItemPosition(item) - 2)
                if (replyBean?.local_time != null) {
                    lastData = DateUtil.timeToString(replyBean.local_time * 1000, "MM月dd日")
                }
            }
        }

        when (item.itemType) {
            TYPE_SENDER -> {//发送反馈信息
                if (getItemPosition(item) == 0) {
                    holder.setGone(R.id.des, false)
                    holder.setGone(R.id.type, false)
                    holder.setGone(R.id.phone, false)
                    val feedbackBean = backListBean?.feedback?.get(0)
                    if (feedbackBean != null) {


                        Glide.with(context).load(feedbackBean.user_avatar).placeholder(R.mipmap.common_ic_user_head_default)
                                .error(R.mipmap.common_ic_user_head_default).into(holder.getView(R.id.sender_head))
                        holder.setText(R.id.type, "问题类型: " + feedbackBean.feedback_type_content)
                        holder.setText(R.id.des, "问题类型: " + feedbackBean.description)
                        if (!feedbackBean.contact.isNullOrEmpty()) {
                            holder.setGone(R.id.phone, false)
                            holder.setText(R.id.phone, "联系电话: " + feedbackBean.contact)
                        } else {
                            holder.setGone(R.id.phone, true)
                        }
                        holder.setGone(R.id.date, false)
                        holder.setText(R.id.date, DateUtil.timeToString((feedbackBean.local_time) * 1000, "MM月dd日"))
                        holder.setText(R.id.time, DateUtil.timeToString((feedbackBean.local_time) * 1000, "HH:mm"))

                        val mFeedBackListImageAdapter = FeedBackListImageAdapter(feedbackBean.attachment)
                        val recycler_view = holder.getView<RecyclerView>(R.id.recycler_view)
                        if (feedbackBean.attachment?.size ?: 0 <= 1) {
                            recycler_view.layoutManager = GridLayoutManager(context, 1)
                        } else if (feedbackBean.attachment?.size ?: 0 <= 2) {
                            recycler_view.layoutManager = GridLayoutManager(context, 2)
                        } else if (feedbackBean.attachment?.size ?: 0 >= 3) {
                            recycler_view.layoutManager = GridLayoutManager(context, 3)

                        }
                        recycler_view.adapter = mFeedBackListImageAdapter
                    }
//                    showImages(feedbackBean.getAttachment(), holder)
                } else {
                    val replyBean = backListBean?.reply?.get(getItemPosition(item) - 1)
                    Glide.with(context).load(replyBean?.user_avatar).placeholder(R.mipmap.common_ic_user_head_default)
                            .error(R.mipmap.common_ic_user_head_default).into(holder.getView(R.id.sender_head))
                    if (!replyBean?.content.isNullOrEmpty()) {
                        holder.setGone(R.id.des, true)
                        holder.setGone(R.id.phone, true)
                        holder.setGone(R.id.type, false)
                        holder.setText(R.id.type, replyBean?.content)
                    } else {
                        holder.setGone(R.id.des, true)
                        holder.setGone(R.id.phone, true)
                        holder.setGone(R.id.type, true)
                    }
                    if (replyBean?.local_time != null) {
                        val data: String = DateUtil.timeToString(replyBean.local_time * 1000, "MM月dd日")

                        if (data == lastData) {
                            holder.setVisible(R.id.date, false)
                        } else {
                            holder.setVisible(R.id.date, true)
                            holder.setText(R.id.date, data)
                        }
                        holder.setText(R.id.time, DateUtil.timeToString(replyBean.local_time * 1000, "HH:mm"))

                    }

                    val mFeedBackListImageAdapter = FeedBackListImageAdapter(replyBean?.img_attachment)
                    val recycler_view = holder.getView<RecyclerView>(R.id.recycler_view)
                    if (replyBean?.img_attachment?.size ?: 0 <= 1) {
                        recycler_view.layoutManager = GridLayoutManager(context, 1)
                    } else if (replyBean?.img_attachment?.size ?: 0 <= 2) {
                        recycler_view.layoutManager = GridLayoutManager(context, 2)
                    } else if (replyBean?.img_attachment?.size ?: 0 >= 3) {
                        recycler_view.layoutManager = GridLayoutManager(context, 3)

                    }
                    recycler_view.adapter = mFeedBackListImageAdapter
                }
            }
            TYPE_RECIEVER -> {//回复信息
                val replyBean = backListBean?.reply?.get(getItemPosition(item) - 1)
                Glide.with(context).load(replyBean?.user_avatar).placeholder(R.mipmap.common_ic_user_head_default)
                        .error(R.mipmap.common_ic_user_head_default).into(holder.getView(R.id.sender_head))
                holder.setText(R.id.reply, replyBean?.content)
                if (replyBean?.local_time != null) {

                    val data = DateUtil.timeToString(replyBean.local_time * 1000, "MM月dd日")
                    if (data == lastData) {
                        holder.setVisible(R.id.date, false)
                    } else {
                        holder.setVisible(R.id.date, true)
                        holder.setText(R.id.date, data)
                    }
                    holder.setText(R.id.time, DateUtil.timeToString(replyBean.local_time * 1000, "HH:mm"))
                }

                val mFeedBackListImageAdapter = FeedBackListImageAdapter(replyBean?.img_attachment)
                val recycler_view = holder.getView<RecyclerView>(R.id.recycler_view)
                if (replyBean?.img_attachment?.size ?: 0 <= 1) {
                    recycler_view.layoutManager = GridLayoutManager(context, 1)
                } else if (replyBean?.img_attachment?.size ?: 0 <= 2) {
                    recycler_view.layoutManager = GridLayoutManager(context, 2)
                } else if (replyBean?.img_attachment?.size ?: 0 >= 3) {
                    recycler_view.layoutManager = GridLayoutManager(context, 3)

                }
                recycler_view.adapter = mFeedBackListImageAdapter
            }
            else -> {
            }
        }

    }

}