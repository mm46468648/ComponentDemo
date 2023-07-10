package com.mooc.studyroom.ui.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.studyroom.R
import com.mooc.studyroom.model.MedalDataBean
import com.mooc.studyroom.model.MedalTypeBean
import com.mooc.studyroom.ui.fragment.myhonor.MyMedalFragment

/**
适配器
 * @Author limeng
 * @Date 2020/9/23-5:43 PM
 */

class MyMedalAdapter(data: ArrayList<MedalTypeBean>?, layoutId: Int = R.layout.studyroom_item_public_recycler)
    : BaseQuickAdapter<MedalTypeBean, BaseViewHolder>(layoutId, data) {
    var onItemClick: ((bean: MedalDataBean) -> Unit)? = null

    override fun convert(holder: BaseViewHolder, item: MedalTypeBean) {
        holder.setText(R.id.tv_type_medal, item.title)
        holder.setText(R.id.tv_count_medal, kotlin.String.format(context.getResources().getString(R.string.medal_str_count), item.count))
        val recyclerView = holder.getView<RecyclerView>(R.id.rcv_public_recycler)
        val tvNoSpecial = holder.getView<TextView>(R.id.tv_no_special)
        if (item.itemType == MyMedalFragment.TYPE_SPECIAL_MEDAL && !item.count.equals("0")) {
            recyclerView.setVisibility(View.GONE)
            tvNoSpecial.setVisibility(View.VISIBLE)
        }
        holder.setVisible(R.id.rcv_public_recycler, true)
        tvNoSpecial.setVisibility(View.GONE)

        recyclerView.setLayoutManager(GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false))
        val adapter = MedalAdapter(item.medalList)
        recyclerView.setAdapter(adapter)
        adapter.addChildClickViewIds(R.id.iv_medal_layout)
        adapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_medal_layout) {
                onItemClick?.invoke(adapter.getItem(position) as MedalDataBean)
            }
        }
        if(item.medalList.size>0){//自己写的 list为空 也隐藏这个按钮了
            holder.setGone(R.id.tv_type_chose,false)
        }else{
            holder.setGone(R.id.tv_type_chose,true)
        }
        if (item.itemType==MyMedalFragment.TYPE_SPECIAL_MEDAL) {//神秘勋章需要隐藏这个按钮
            holder.setGone(R.id.tv_type_chose,true)
        }
        var choseAllView = holder.getViewOrNull<TextView>(R.id.tv_type_chose);
        choseAllView?.setOnClickListener {
            if (item.isAll) {//已经全部显示
                //点击显示只点亮的
                choseAllView.setText(context.getText(R.string.medal_all_share))
                item.isAll=false
                adapter.isAll=item.isAll
                adapter.notifyDataSetChanged()
            }else{//只显示了点亮的勋章
                //点击显示全部
                choseAllView.setText(context.getText(R.string.medal_obtain_share))
                item.isAll=true
                adapter.isAll=item.isAll
                adapter.notifyDataSetChanged()

            }

        }

    }
}