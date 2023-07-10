package com.mooc.discover.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.mooc.common.ktextends.getColorRes
import com.mooc.discover.R
import com.mooc.common.ktextends.sp2px
import com.mooc.discover.databinding.LayoutTabMutualTaskBinding

/**
 * 互斥任务自定义TabLayout Tab
 */
class MutualTaskTabCustomView @JvmOverloads constructor(var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
        FrameLayout(mContext, attributeSet, defaultInt) {

    private var inflater: LayoutTabMutualTaskBinding =
        LayoutTabMutualTaskBinding.inflate(LayoutInflater.from(context),this,true)
    init {
//        LayoutInflater.from(mContext).inflate(R.layout.layout_tab_mutual_task, this)
    }

    var str = ""
    override fun setSelected(selected: Boolean) {
        onTabSelect(selected)
        super.setSelected(selected)
    }

    fun onTabSelect(selected: Boolean){
        if(selected){
            inflater.tvTabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 17.sp2px().toFloat())
            inflater.viewTabLine.visibility = View.VISIBLE
            inflater.tvTabTitle.setTextColor(mContext.getColorRes(R.color.color_2))
        }else{
            inflater.tvTabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13.sp2px().toFloat())
            inflater.tvTabTitle.setTextColor(mContext.getColorRes(R.color.color_6))
            inflater.viewTabLine.visibility = View.GONE
        }
    }

    fun setTitle(str:String){
        this.str = str
        inflater.tvTabTitle.setText(str)
    }

    fun updateSelect() {
        inflater.ivSelect.visibility = View.VISIBLE
    }
}