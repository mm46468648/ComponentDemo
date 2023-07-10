package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * 统计服务
 */
interface StatisticsService : IProvider {
    override fun init(context: Context?) {}

    fun addClickLog( page: String = "",
                     element: String = "",
                     etype: String = "",
                     name: String = "",
                     to: String = "",)

    fun addLoadLog(page: String = "",
                   element: String = "",
                   etype: String = "",)
}