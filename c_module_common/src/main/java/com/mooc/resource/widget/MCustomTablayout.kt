package com.mooc.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.mooc.common.R;
import com.mooc.resource.listener.ViewPager2OnTabSelectedListener
import com.mooc.resource.widget.tablayout.MCustomTabView

/**
 * 简化TabLayout自定义样式的操作
 * 支持自定义tablayou的布局，支持加粗，缩放，等
 */
class MCustomTablayout@JvmOverloads constructor(var mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : TabLayout(mContext,attrs,defStyleAttr) {


    var mCustomLayout = -1
    var unselectTextSize = 12
    var selectTextSize = 12
    var selectTextBlod = false


    init {

        //
        val ta = getContext().obtainStyledAttributes(attrs, R.styleable.MCustomTablayout)
        mCustomLayout = ta.getResourceId(R.styleable.MCustomTablayout_mct_tab_layout,-1)
        unselectTextSize = ta.getDimensionPixelSize(R.styleable.MCustomTablayout_mct_unselect_text_size,unselectTextSize)
        selectTextSize = ta.getDimensionPixelSize(R.styleable.MCustomTablayout_mct_select_text_size,unselectTextSize)
        selectTextBlod = ta.getBoolean(R.styleable.MCustomTablayout_mct_select_text_blod,false)
        ta.recycle()




//        if(mCustomLayout != -1){
//            LayoutInflater.from(mContext).inflate(mCustomLayout,this)
//        }
    }

    fun setUpWithViewPage2(viewPage2:ViewPager2,title:ArrayList<String>){
        removeAllTabs()

        for (i in 0 until title.size) {
            this.addTab(this.newTab())
        }

        for (i in 0 until title.size) {
            this.getTabAt(i)?.setCustomView(makeTabView(title[i]))
        }

        this.getTabAt(0)?.select()

        //adapter建立关联
//        vpDiscover.addOnPageChangeListener(TabLayoutOnPageChangeListener(mTabLayout))
        viewPage2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                this@MCustomTablayout.selectTab(this@MCustomTablayout.getTabAt(position), true)
            }
        })
        this.addOnTabSelectedListener(
            com.mooc.resource.listener.ViewPager2OnTabSelectedListener(
                viewPage2
            )
        )
    }
    /**
     * 设置tablayout tab 自定义样式
     * @param position
     * @return
     */
    private fun makeTabView(str: String): View {
        val tabView: MCustomTabView = MCustomTabView(mCustomLayout,mContext)
        tabView.setTitle(str)
        tabView.selectBload = this.selectTextBlod
        tabView.setTabTextColor(tabTextColors)
        tabView.setSelectTextSize(selectTextSize.toFloat())
        tabView.setNormalTextSize(unselectTextSize.toFloat())
        return tabView
    }
}