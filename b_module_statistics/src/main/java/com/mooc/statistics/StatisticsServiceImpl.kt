package com.mooc.statistics

import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.StatisticsService

@Route(path = Paths.SERVICE_LOG)
class StatisticsServiceImpl : StatisticsService {

    override fun addClickLog( page: String , element: String, etype: String, name: String, to: String,) {
        LogUtil.addClickLogNew(page,element,etype,name,to)
    }

    override fun addLoadLog(page: String, element: String, etype: String) {
        LogUtil.addLoadLog(page,element,etype)
    }
}