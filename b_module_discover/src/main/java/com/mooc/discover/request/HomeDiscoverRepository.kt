package com.mooc.discover.request

import com.mooc.discover.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseRepository

import com.mooc.discover.model.*
import io.reactivex.Observable
import okhttp3.RequestBody
import com.mooc.commonbusiness.model.HttpResponse


class HomeDiscoverRepository : BaseRepository() {



    suspend fun getDiscoverTab(tab: Int): HttpResponse<MutableList<TabSortBean>> {
        return HttpService.discoverApi.getDiscoverTabSort(tab)
    }


    suspend fun getRecommendSpecial(tab: String, limit: Int, offset: Int): RecommendZhuanlanBean {
        return HttpService.discoverApi.getRecommendDetailList(tab, limit, offset).await()
    }

    suspend fun getColumnDetail(resId : String ,page:Int ,pageSize:Int) : RecommendContentBean {
        val map = mapOf<String, String>("page" to page.toString(),
                "page_size " to pageSize.toString())
        return HttpService.discoverApi.getRecommendListWithId(resId,map).await()
    }


    suspend fun postTask(body: RequestBody): HttpResponse<Any> {
        return request {
            HttpService.discoverApi.postTaskToReceive(body).await()
        }
    }
    //            ApiService.retrofit.create(MyModelApi::class.java).postFollowUser(body).await()

}