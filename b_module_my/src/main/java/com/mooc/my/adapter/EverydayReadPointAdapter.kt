package com.mooc.my.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R
import com.mooc.my.model.ReadBean


/**
 *点的适配器
 * @author limeng
 * @date 2020/9/2
 */
class EverydayReadPointAdapter(data: ArrayList<ReadBean>, layoutResId: Int = R.layout.my_item_read_history_point) :
        BaseQuickAdapter<ReadBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: ReadBean) {

        if (holder.adapterPosition==currentPosition) {
            holder.getView<View>(R.id.iv_read_point).setBackgroundResource(R.mipmap.my_ic_everyday_point_selected)
        }else{
            holder.getView<View>(R.id.iv_read_point).setBackgroundResource(R.mipmap.my_ic_everyday_point_normal)
        }
    }
    var currentPosition = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }


}