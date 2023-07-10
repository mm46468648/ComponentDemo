package com.mooc.discover.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.mooc.changeskin.SkinManager
import com.mooc.discover.R
import com.mooc.common.ktextends.sp2px
import com.mooc.discover.databinding.LayoutTabHomeDiscoverBinding


/**
 * 首页发现页自定义TabLayout Tab
 */
class HomeTabCustomView @JvmOverloads constructor(var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
        FrameLayout(mContext, attributeSet, defaultInt) {

    private var inflater: LayoutTabHomeDiscoverBinding =
        LayoutTabHomeDiscoverBinding.inflate(LayoutInflater.from(context),this,true)

    init {
//        LayoutInflater.from(mContext).inflate(R.layout.layout_tab_home_discover, this)
        SkinManager.getInstance().injectSkin(this)
    }

    override fun setSelected(selected: Boolean) {
        onTabSelect(selected)
        super.setSelected(selected)
    }

    fun onTabSelect(selected: Boolean){
        if(selected){
            inflater.tvTabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 19.sp2px().toFloat())
            inflater.viewTabLine.visibility = View.VISIBLE
        }else{
            inflater.tvTabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15.sp2px().toFloat())
            inflater.viewTabLine.visibility = View.GONE
        }
    }

    fun setTitle(str:String){
        inflater.tvTabTitle.setText(str)
    }
}