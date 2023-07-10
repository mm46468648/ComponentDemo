package com.mooc.battle

import com.mooc.battle.model.*
import com.mooc.commonbusiness.model.HttpResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface BattleApi {

    @GET("api/mobile/bs/tournament/my/")
    suspend fun getMyCreateSkillList(@Query("offset") offset : Int,@Query("limit") limit : Int) : HttpResponse<SkillListResponse>

    @GET("api/mobile/bs/tournament/ongoing/")
    suspend fun getOtherCreateSkillList(@Query("offset") offset : Int,@Query("limit") limit : Int) : HttpResponse<SkillListResponse>
    //比武排行
    @GET("api/mobile/bs/tournament/rank/detail/")
    suspend fun getGameRankList(
        @Query("tournament_id") tournament_id: String?,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): HttpResponse<RankDeatilsBean>

    //创建比武描述信息
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/bs/tournament/play/")
    fun getCreateTipInfo(@Body body: RequestBody): Observable<HttpResponse<SkillDesc>>

    //加入比武描述信息
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/bs/tournament/play/")
    fun getJoinTipInfo(@Body body: RequestBody): Observable<HttpResponse<SkillDesc>>

    //创建比武
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/bs/tournament/play/")
    fun createSkill(@Body body: RequestBody) : Observable<HttpResponse<SkillInfo>>

    //获取比武问题
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/bs/tournament/play/")
    fun getQuestions(@Body body: RequestBody) : Observable<HttpResponse<SkillQuestionInfo>>

    //提交答案
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/bs/tournament/play/")
    fun submitAnswer(@Body body: RequestBody) : Observable<HttpResponse<GameViewAnswer>>

    //告诉后端离开
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/bs/tournament/play/")
    fun postLeave(@Body body: RequestBody) : Observable<HttpResponse<Object>>

    //获取比武结算页
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/bs/tournament/play/")
    fun getSkillResult(@Body body: RequestBody) : Observable<HttpResponse<SkillResult>>

    /**
     * 本局回顾接口
     *
     * @param body
     * @return
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/bs/tournament/play/")
    fun getSkillReview(@Body body: RequestBody): Observable<HttpResponse<SkillReviewQuestions>>
}