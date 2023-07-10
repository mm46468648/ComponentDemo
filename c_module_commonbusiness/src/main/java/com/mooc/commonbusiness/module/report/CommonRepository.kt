package com.mooc.commonbusiness.module.report

import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.my.WebUrlBean
import com.mooc.commonbusiness.model.privacy.PrivacyPolicyCheckBean

class CommonRepository : BaseRepository() {


    /**
     * 获取隐藏内容
     */
    suspend fun getPrivacyPolicyCheck(version: String): HttpResponse<PrivacyPolicyCheckBean> {
        return request {
            HttpService.commonApi.getPrivacyPolicyCheckAsync(version).await()
        }
    }

    /**
     * 获取Web Url
     */
    suspend fun getWebUrl(): HttpResponse<WebUrlBean> {
        return request {
            HttpService.commonApi.getWebUrl().await()
        }
    }

}