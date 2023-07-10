package com.mooc.studyproject.presenter

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayout
import com.mooc.common.ktextends.sp2px
import com.mooc.studyproject.R
import me.devilsen.czxing.util.BarCodeUtil
import java.util.regex.Pattern

/**

 * @Author limeng
 * @Date 2021/5/7-3:17 PM
 */
class SomeUtilsPresenter {
    var context:Context?=null


    fun updateTabView(tab: TabLayout.Tab?, isSelect: Boolean) {
        //找到自定义视图的控件ID
//        assert(tab?.customView != null)
        val tv_tab: TextView
        val viewLint: View
        try {
            tv_tab = tab?.customView!!.findViewById(R.id.tvTabTitle)
            viewLint = tab.customView!!.findViewById(R.id.viewTabLine)
        } catch (e: java.lang.Exception) {
            return
        }
        if (isSelect) {
            //设置标签选中
            tv_tab.isSelected = true
            //选中后字体变大
            tv_tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, BarCodeUtil.sp2px(context, 15.toFloat()).toFloat())
            tv_tab.setTypeface(Typeface.DEFAULT_BOLD)
            viewLint.visibility = View.VISIBLE
        } else {
            //设置标签取消选中
            tv_tab.isSelected = false
            //恢复为默认字体大小
            tv_tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, BarCodeUtil.sp2px(context, 13.toFloat()).toFloat())
            tv_tab.setTypeface(Typeface.DEFAULT_BOLD)
            viewLint.visibility = View.GONE
        }
    }

    fun initStatusBar(color: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = (context as FragmentActivity).window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = color
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getRuleContent(plan_rule:String?):String{
      val content= delHtmlTag(Html.fromHtml(delBetRuleImg(plan_rule)).toString())
        return content.toString()
    }
    private fun delHtmlTag(content: String?): String? {
        return content?.replace("\\s*|(\r\n)".toRegex(), "")
    }

    private fun delBetRuleImg(str: String?): String? {
        var string: String?
        val FyqXwContent = str?.replace("\'", "\"")
        val pImg = Pattern.compile("(<img.*?>)", Pattern.CASE_INSENSITIVE)
        val mImg = pImg.matcher(FyqXwContent)
        //        mStudyPlanData.setPlan_rule(FyqXwContent);
        string = FyqXwContent
        while (mImg.find()) {
            val result = mImg.group()
            val p = Pattern.compile("src=\"([^\"]+)\"")
            val m = p.matcher(result)
            while (m.find()) {
                val LastResult = string?.replace(result, "")
                string = LastResult
            }
        }
        return string
    }
    fun getHtmlHeader(str: String): String {
        return kotlin.String.format(htmlHeader, str)
    }
    val htmlContent = "<p>%s</p>"

    val htmlHeader = """<div
      style="font-size: 14px;
      font-weight: 800;
      color: #222222;
      margin: 0px 0 20px;
      font-size: 14px;"
    ><span
        style="float: left;
        width: 4px;
        height: 12px;
        border-radius: 2px;
        background-color: #666;
        margin: 7px 10px 0 4px;"
        ></span>%s
    </div>"""

    fun getHtmlContent(str: String): String {
        return String.format(htmlContent, str)
    }

    fun getHtmlImage(html: String?): String? {
        return html?.replace("<img", "<img width=\"100%\"")
    }
    fun isUnStartOrStop(time: Long, startTime: Long?, stopTime: Long?): Int {
        if (startTime != null&& stopTime!=null) {
            return if (time < startTime*1000) { //尚未开始
                0
            } else if (time >= startTime*1000 && time <= stopTime*1000) { //可以加入计划
                1
            } else { //计划结束
                2
            }
        }
        return 2

    }

    fun initTabs(tabs: TabLayout) {
        for (i in 0 until tabs.getTabCount()) {
            //获取每一个tab对象
            val tabAt = tabs.getTabAt(i)
            val title: String = tabAt?.getText().toString()
//            assert(tabAt != null)
            val customView = tabAt?.getCustomView()
            if (customView != null) {
                val parent = customView.parent
                if (parent != null) {
                    (parent as ViewGroup).removeView(customView)
                }
            }
            tabAt?.setCustomView(R.layout.studyproject_layout_discover_tab)
//            assert(tabAt?.getCustomView() != null)
            val tvTitle = tabAt?.getCustomView()?.findViewById(R.id.tvTabTitle) as TextView
            val viewLint = tabAt.getCustomView()?.findViewById(R.id.viewTabLine) as View
            //默认选中第一个
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                tvTitle.isSelected = true //第一个tab被选中
                //设置选中标签的文字大小
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15.sp2px().toFloat())
                tvTitle.setTypeface(Typeface.DEFAULT_BOLD)
                viewLint.visibility = View.VISIBLE
            } else {
                // 设置第一个tab的TextView是被选择的样式
                tvTitle.isSelected = false //第一个tab被选中
                //设置选中标签的文字大小
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13.sp2px().toFloat())
                tvTitle.setTypeface(Typeface.DEFAULT_BOLD)
                viewLint.visibility = View.GONE
            }
            //通过tab对象找到自定义视图的ID
            tvTitle.text = title //设置tab上的文字
        }
    }
}