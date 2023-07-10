package com.mooc.home.ui.discover.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.studyroom.SortChild
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.resource.ktextention.dp2px
import com.mooc.home.R

/**
 * 发现页3级分类适配器
 */
class Child2SortAdapter(list:ArrayList<SortChild>) : BaseQuickAdapter<SortChild,BaseViewHolder>(0,list){
    var stateArr = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf())
    var colorArr = intArrayOf(Color.parseColor("#10955B"),Color.parseColor("#222222"))
    var colorStateList = ColorStateList(stateArr,colorArr)

    var selectPosition = 0 //默认选中第一个位置
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        //自定义容器
        val textView = TextView(parent.context)
        textView.gravity = Gravity.LEFT
        textView.minHeight = 40.dp2px()
        val layoutParams = LinearLayout.LayoutParams(80.dp2px(), LinearLayout.LayoutParams.WRAP_CONTENT)
        textView.setPadding(0,20.dp2px(),10.dp2px(),20.dp2px())
        textView.layoutParams = layoutParams
        textView.compoundDrawablePadding = 10.dp2px()
        textView.setTextColor(colorStateList)
        textView.setTextSize(4.dp2px().toFloat())
        textView.setBackgroundResource(R.drawable.home_selector_child2sort_innertext_bg)
        return createBaseViewHolder(textView)
    }


    override fun convert(holder: BaseViewHolder, item: SortChild) {
        holder.itemView.isSelected = holder.adapterPosition == selectPosition
        //drawableLeft
        val leftDrawable
                = if(holder.adapterPosition == selectPosition)
                    ContextCompat.getDrawable(context,R.drawable.home_shape_chil2sort_drawableleft)
                else ContextCompat.getDrawable(context,R.drawable.home_shape_chil2sort_drawableleft_unselect)
        (holder.itemView as TextView).setDrawLeft(leftDrawable)
        //title
        (holder.itemView as TextView).text = item.title
        //bg
//        val textBgColor = if(holder.adapterPosition == selectPosition) Color.WHITE else Color.TRANSPARENT
//        holder.itemView.setBackgroundColor(textBgColor)
    }
}