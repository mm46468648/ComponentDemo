package com.mooc.webview.api

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.VerifyCode
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.webview.model.CompetitionManageData
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*

interface RecommendApi {

    //获取推荐文章
    @GET("/api/mobile/similar/resource/recommend/")
    fun getRecommendArticle(
            @Query("resource_type") resource_type: Int = 14,
            @Query("resource_id") resource_id: String): Deferred<HttpResponse<MutableList<ArticleBean>>?>



    //竞赛规则
    @GET("api/mobile/bs/battle/competition/{id}/")
    fun getParticipateRuleDetail(@Path("id") id: String?): Observable<HttpResponse<CompetitionManageData>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/task_system/finish/article/")
    suspend fun postVerifyCode(@Body body: RequestBody): HttpResponse<Boolean>


    //获取文章任务验证码
    @GET("/api/service/verify_code/")
    suspend fun getArticleVerifyCode(@Query("source_id") id: String): HttpResponse<VerifyCode>
}