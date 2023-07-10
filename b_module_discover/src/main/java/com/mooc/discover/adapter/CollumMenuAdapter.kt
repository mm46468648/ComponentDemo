package com.mooc.discover.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.R
import com.mooc.discover.model.MenuBean

/**

 * @Author limeng
 * @Date 2020/11/23-3:58 PM
 */
class CollumMenuAdapter(data: ArrayList<MenuBean>?) : BaseQuickAdapter<MenuBean, BaseViewHolder>(R.layout.item_collumn_menu, data) {

    override fun convert(holder: BaseViewHolder, item: MenuBean) {
        holder.setText(R.id.tv_name, item.name)
        if (holder.layoutPosition == 0) {
            holder.setGone(R.id.diver, false)
        } else {
            holder.setGone(R.id.diver, true)

        }

        if (item.isCheck) {
            holder.setTextColor(R.id.tv_name, context.resources.getColor(R.color.colorPrimary))
        } else {
            holder.setTextColor(R.id.tv_name, context.resources.getColor(R.color.color_3))

        }

    }


}