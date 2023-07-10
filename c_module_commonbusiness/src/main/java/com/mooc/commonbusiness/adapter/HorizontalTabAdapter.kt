package com.mooc.commonbusiness.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.model.studyroom.DiscoverTab
import com.mooc.resource.ktextention.dp2px

class HorizontalTabAdapter(list:ArrayList<DiscoverTab>?) : BaseQuickAdapter<DiscoverTab,BaseViewHolder>(0,list) {


    var stateArr = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf())
    var colorArr = intArrayOf(Color.parseColor("#222222"), Color.parseColor("#989898"))
    var colorStateList = ColorStateList(stateArr,colorArr)

    var selectPosition = -1 //默认什么都不选中
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val textView = TextView(parent.context)
        textView.gravity = Gravity.CENTER
        textView.setSingleLine()
        textView.setPadding(8.dp2px(),0,8.dp2px(),0)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 26.dp2px())
        layoutParams.leftMargin = 10.dp2px()
        textView.layoutParams = layoutParams
        textView.setTextColor(colorStateList)
//        textView.setBackgroundResource(R.drawable.home_selector_customtablayout_innertab_bg)
        return createBaseViewHolder(textView)
    }

    override fun convert(holder: BaseViewHolder, item: DiscoverTab) {
        holder.itemView.isSelected = holder.adapterPosition == selectPosition
        (holder.itemView as TextView).text = item.title

        if(holder.layoutPosition == selectPosition){
            //换肤
                val drawableBg = SkinManager.getInstance().resourceManager.getDrawableByName("common_bg_rv_horizantal_tab")
            holder.itemView.setBackground(drawableBg)
        }else{
            holder.itemView.setBackgroundResource(R.drawable.home_shape_solideefefef_corner2)
        }
    }


}