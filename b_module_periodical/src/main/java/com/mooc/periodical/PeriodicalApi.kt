package com.mooc.periodical

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.periodical.model.PeriodicalDetail
import com.mooc.periodical.model.PeriodicalList
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PeriodicalApi {

    /***
     * 获取期刊详情
     * @param
     * @return
     */
    @GET("api/mobile/periodical/{id}/")
    fun getPeriodicalDetail(@Path("id") id: String): Deferred<HttpResponse<PeriodicalDetail>>

    /**
     * 期刊列表
     */
    @GET("api/mobile/periodical/{id}/literature/")
    fun getPeriodicalList(@Path("id") id: String,@Query("year") year:Int,@Query("term") term:String,@Query("offset") offset:Int,@Query("limit") limit:Int) : Deferred<HttpResponse<PeriodicalList>>
}