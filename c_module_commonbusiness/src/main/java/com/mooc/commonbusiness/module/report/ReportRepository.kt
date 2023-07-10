package com.mooc.commonbusiness.module.report

import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.model.ReportChoicesBean

class ReportRepository : BaseRepository() {

    suspend fun loadReportChoices() : ReportChoicesBean{
        return request {
            HttpService.commonApi.getReportData().await()
        }
    }

    /**
     * 动态举报选项
     */
    suspend fun loadDynamicReportChoices() : ReportChoicesBean{
        return request {
            HttpService.commonApi.getDynamicReportData().await()
        }
    }


}