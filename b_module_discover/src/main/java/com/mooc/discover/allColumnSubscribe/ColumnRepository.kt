package com.mooc.discover.allColumnSubscribe

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.RecommendBean
import com.mooc.discover.model.RecommendColumn
import com.mooc.discover.model.SubscribeAllResponse

import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.common.ktextends.loge
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ColumnRepository : BaseRepository() {

//    fun getData(): Observable<RecommendBean> {
//        return HttpService.discoverApi.getScoreRule();
//    }


//    fun getRecommendAllData(offset: Int, limit: Int): Deferred<HttpResponse<List<RecommendColumn>>> {
//        return HttpService.discoverApi.getRecommendColumn(offset, limit)
//    }


    /**
     * 获取全部栏目数据
     */
    suspend fun getAllColumnSubscribeData(): ArrayList<SubscribeAllResponse> {
        return HttpService.discoverApi.getSubscribeAllList().await();
    }



    /**
     * 上传订阅的栏目
     * 格式
     * {
    "ids": [134, 11989, 9971]
    }
     */
    suspend fun postSubscribeChangeData(ids: Array<String>) {
        val str = "{\"ids\":${ids.contentToString()}}"
        loge(str)
        val toRequestBody = str.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        HttpService.discoverApi.postColumnMenu(toRequestBody)
    }

}