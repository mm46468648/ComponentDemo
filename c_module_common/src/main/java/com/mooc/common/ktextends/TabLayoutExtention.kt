package com.mooc.common.ktextends

import com.google.android.material.tabs.TabLayout

/**

 * @Author limeng
 * @Date 2020/9/23-5:36 PM
 */
/**
 *tablyout 的监听扩展
 * @author limeng
 * @date 2020/9/23
 */
fun TabLayout.onTabSelected(tabSelect: TabSelect.() -> Unit) {
    tabSelect.invoke(TabSelect(this))
}
class TabSelect(tab: TabLayout) {
    private var tabReselected: ((tab: TabLayout.Tab) -> Unit)? = null
    private var tabUnselected: ((tab: TabLayout.Tab) -> Unit)? = null
    private var tabSelected: ((tab: TabLayout.Tab) -> Unit)? = null

    fun onTabReselected(tabReselected: (TabLayout.Tab.() -> Unit)) {
        this.tabReselected = tabReselected
    }

    fun onTabUnselected(tabUnselected: (TabLayout.Tab.() -> Unit)) {
        this.tabUnselected = tabUnselected
    }

    fun onTabSelected(tabSelected: (TabLayout.Tab.() -> Unit)) {
        this.tabSelected = tabSelected
    }

    init {
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.apply { tabReselected?.invoke(tab) }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.apply { tabUnselected?.invoke(tab) }
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply { tabSelected?.invoke(tab) }
            }

        })
    }
}