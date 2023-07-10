package com.mooc.studyroom

import com.mooc.studyroom.model.ExchangePointBean
import com.mooc.studyroom.model.IntegralBean
import com.mooc.studyroom.model.IntegralRecordBean
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*

interface IntegralApi {

    @GET("/api/mobile/scoremanage/prizes/")
    fun getIntegralExchangeList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int): Deferred<IntegralBean>;


    //兑换记录列表
    @GET("/api/mobile/scoremanage/userprizes/")
    fun getPrizeRecordList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Deferred<IntegralRecordBean>


    //兑换积分
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/scoremanage/usergetprizes/")
    fun exchangePoint(@Body body: RequestBody?): Observable<ExchangePointBean>
}