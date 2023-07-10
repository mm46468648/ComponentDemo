package com.mooc.my.adapter

import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R
import com.mooc.my.model.FeedTypeBean

/**

 * @Author limeng
 * @Date 2020/10/21-6:16 PM
 */
class FeedBackTypeAdapter(data: ArrayList<FeedTypeBean>?, layoutId: Int = R.layout.my_item_feed_type)
    :BaseQuickAdapter<FeedTypeBean, BaseViewHolder>(layoutId, data) {
    override fun convert(holder: BaseViewHolder, item: FeedTypeBean) {

        holder.getView<CheckBox>(R.id.cb_feed_type).isChecked = item.isCheck
        holder.getView<CheckBox>(R.id.cb_feed_type).setText(item.feedback_type_content)
    }
}