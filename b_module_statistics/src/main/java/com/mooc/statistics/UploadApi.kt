package com.mooc.statistics

import com.mooc.commonbusiness.model.HttpResponse
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface UploadApi {

    @POST("/api/mobile/logs/")
    fun postZipFile(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/web/logs/")
    fun testUploadLog(@Body body: RequestBody) : Observable<Any>


    /**
     * 新版打点接口
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/click/log/")
    fun uploadServerLog(@Body body: RequestBody) : Observable<Any>
}