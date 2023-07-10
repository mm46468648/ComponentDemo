package com.mooc.home.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.mooc.resource.ktextention.dp2px
import com.mooc.home.R

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    首页发现子布局导航View
 * @Author:         xym
 * @CreateDate:     2020/8/12 3:43 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/12 3:43 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class HorizontalTabLayout @JvmOverloads constructor(var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
        HorizontalScrollView(mContext, attributeSet, defaultInt) {

    private var tabsContainer: LinearLayout
    private var tabStrs: Array<String> = arrayOf()
    private var tabCount = 0
    private var currentSelectPosition = 0;

    var stateArr = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf())
    var colorArr = intArrayOf(Color.parseColor("#222222"),Color.parseColor("#989898"))
    var colorStateList = ColorStateList(stateArr,colorArr)

    init {
        //使ScrollView的内容充满视图
        isFillViewport = true

        tabsContainer = LinearLayout(mContext)
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL)
        tabsContainer.gravity = Gravity.CENTER_VERTICAL
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        tabsContainer.setLayoutParams(layoutParams)
        addView(tabsContainer)

    }

    fun setTabStrs(tabStrs: Array<String>) {
        this.tabStrs = tabStrs

        initTabView()
    }

    fun initTabView() {
        if (tabStrs.size > 0) {     //兼容ViewPage2
            tabsContainer.removeAllViews()
            tabCount = tabStrs.size
            for (i in 0 until tabCount) {
                addTextTab(i, tabStrs[i])
            }
            updateTabStyles()
        }
    }

    fun setCurrentSelectTab(position: Int){
        currentSelectPosition = position
        updateTabStyles()
    }

    fun getCurrentSelectPosition() = currentSelectPosition
    /**
     * 更新Tab样式
     */
    private fun updateTabStyles() {
        for (i in 0 until tabsContainer.childCount){
            val childAt = tabsContainer.getChildAt(i)
            childAt.isSelected = i == currentSelectPosition
        }
        invalidate()
    }

    var onTabSelectCallback : ((position:Int)->Unit)? = null


    /**
     * 添加子tab
     */
    private fun addTextTab(position: Int, title: String) {
        val tab = makeTabView()
        tab.text = title
        tab.setOnClickListener {
            setCurrentSelectTab(position)
            onTabSelectCallback?.invoke(position)
        }
        tabsContainer.addView(tab, position)
    }

    /**
     * 构建子tab
     */
    fun makeTabView(): TextView {
        val textView = TextView(mContext)
        textView.gravity = Gravity.CENTER
        textView.setSingleLine()
        textView.setPadding(8.dp2px(),0,8.dp2px(),0)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 26.dp2px())
        layoutParams.leftMargin = 10.dp2px()
        textView.layoutParams = layoutParams
        textView.setTextColor(colorStateList)
//        textView.setTextColor(ContextCompat.getColor(mContext, R.color.home_selector_customtablayout_innertab_text))
        textView.setBackgroundResource(R.drawable.home_selector_customtablayout_innertab_bg)
        return textView
    }

    /**
     * 请求父类不拦截子类触摸事件
     * todo，判断滑到头,以及上下托滑动可以拦截
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }
}