package com.mooc.home.widget

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.mooc.home.R
import com.mooc.home.databinding.HomeViewDiscoverCommonHeadBinding
//import kotlinx.android.synthetic.main.home_view_discover_common_head.view.*


class DiscoverCommonListHeadView @JvmOverloads constructor(var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
        FrameLayout(mContext, attributeSet, defaultInt) {

    companion object {
        //字符串
        const val sortStr = "排序：综合       最新       最热"
        const val operationStr = "排序：综合"
        const val allStr = "综合"
        const val newStr = "最新"
        const val hotStr = "最热"
    }


    var listNum = "0" //条目
    val foregroundColor = ContextCompat.getColor(mContext, R.color.colorPrimary)
    val textColor = ContextCompat.getColor(mContext, R.color.color_5D5D5D)
    /**
     * 点击排序事件回调
     * @param str 具体查询时需要的字符串
     */
    var onClickSortCallBack: ((str: String) -> Unit)? = null

    private var inflater : HomeViewDiscoverCommonHeadBinding =
        HomeViewDiscoverCommonHeadBinding.inflate(LayoutInflater.from(context),this,true)

    init {
//        LayoutInflater.from(mContext).inflate(R.layout.home_view_discover_common_head, this)
        //默认选中综合
        updateStyle(allStr)

    }


    /**
     * 富文本点击事件
     */
    inner class CustomClickableSpan(val str: String) : ClickableSpan() {

        override fun onClick(p0: View) {
            val orderingMap = hashMapOf<String, String>("综合" to "-sort_top", "最新" to "-publish_time", "最热" to "-view_count")
            updateStyle(str)
            onClickSortCallBack?.invoke(orderingMap[str]?:"-sort_top")
        }

        //去除连接下划线
        override fun updateDrawState(ds: TextPaint) {
            /**set textColor */
            ds.color = textColor
            /**Remove the underline */
            ds.isUnderlineText = false
        }

    }

    /**
     * 更新富文本样式
     */
    private fun updateStyle(str: String) {
        //为sortStr，增加富文本样式与点击事件
        val string = SpannableString(sortStr)
        string.setSpan(CustomClickableSpan(allStr), sortStr.indexOf(allStr), sortStr.indexOf(allStr) + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        string.setSpan(CustomClickableSpan(newStr), sortStr.indexOf(newStr), sortStr.indexOf(newStr) + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        string.setSpan(CustomClickableSpan(hotStr), sortStr.indexOf(hotStr), sortStr.indexOf(hotStr) + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        string.setSpan(ForegroundColorSpan(foregroundColor), sortStr.indexOf(str), sortStr.indexOf(str) + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        inflater.tvSort.text = string
        //使点击生效
        inflater.tvSort.movementMethod = LinkMovementMethod.getInstance()
        inflater.tvSort.highlightColor = Color.TRANSPARENT

//        setNumText(listNum)
    }

    /**
     * 设置多少条
     */
    fun setNumText(num: String) {
        if(inflater.tvNum == null){
            listNum = num
            return
        }
        inflater.tvNum.text = "${num}条"
    }

    fun resetState() {

    }

    var lastOperation = 1 //默认1
    /**
     * 设置自运营参数
     * @param ownOperation 0 只显示综合排序，1，显示全部分类
     *
     */
    fun setOwnOperation(ownOperation: Int, currentSort: String) {
        if(lastOperation == ownOperation) return

        lastOperation = ownOperation
        if(ownOperation == 0){
            val string = SpannableString(operationStr)
            string.setSpan(CustomClickableSpan(allStr), operationStr.indexOf(allStr), operationStr.indexOf(allStr) + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            string.setSpan(ForegroundColorSpan(foregroundColor), operationStr.indexOf(allStr), operationStr.indexOf(allStr) + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

            inflater.tvSort.text = string
            //使点击生效
            inflater.tvSort.movementMethod = LinkMovementMethod.getInstance()
            inflater.tvSort.highlightColor = Color.TRANSPARENT
            return
        }

        val orderingMap = hashMapOf<String, String>("-sort_top" to "综合" , "-publish_time" to "最新"  , "-view_count" to "最热")

        //1.显示当前排序
        updateStyle(orderingMap[currentSort]?:"综合")
    }
}