package com.mooc.resource.widget.tablayout

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.dp2px
import com.mooc.common.R;

/**
 * 首页发现页自定义TabLayout Tab
 */
class MCustomTabView @JvmOverloads constructor(var layoutId:Int,var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
        FrameLayout(mContext, attributeSet, defaultInt) {

    var tvTabTitle:TextView? = null
    var viewTabLine:View? = null
    var mNormalTextSize = 12.dp2px().toFloat()
    var mSelectTextSize = 12.dp2px().toFloat()
    var mSelectTextColor = 0
    var mUnselectTextColor = 0
    var selectBload = false

    init {
        LayoutInflater.from(mContext).inflate(layoutId, this)
        tvTabTitle = findViewById(R.id.tvTabTitle)
        viewTabLine = findViewById(R.id.viewTabLine)

        mSelectTextColor = ContextCompat.getColor(mContext,R.color.colorPrimary)
        mSelectTextColor = ContextCompat.getColor(mContext,R.color.color_6)

        SkinManager.getInstance().injectSkin(this)
    }

    override fun setSelected(selected: Boolean) {
        onTabSelect(selected)
        super.setSelected(selected)
    }

    fun onTabSelect(selected: Boolean){
        tvTabTitle?.isSelected = true
        if(selected){
            tvTabTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectTextSize)
//            tvTabTitle?.setTextColor(mSelectTextColor)
            viewTabLine?.visibility = View.VISIBLE

            if(selectBload){
                tvTabTitle?.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
            }
        }else{
            tvTabTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize)
//            tvTabTitle?.setTextColor(mUnselectTextColor)
            viewTabLine?.visibility = View.GONE

            if(selectBload){
                tvTabTitle?.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
            }
        }
    }

    fun setSelectTextSize(size: Float){
        mSelectTextSize = size
    }

    fun setNormalTextSize(size: Float){
        mNormalTextSize = size
    }

    fun setSelectTextColor(color: Int){
        mSelectTextColor = color
    }

    fun setNormalTextColor(color: Int){
        mUnselectTextColor = color
    }




    fun setTitle(str:String){
        tvTabTitle?.setText(str)
    }

    fun setTabTextColor(tabTextColors: ColorStateList?) {
        tvTabTitle?.setTextColor(tabTextColors)
    }
}