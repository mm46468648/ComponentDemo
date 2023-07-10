package com.mooc.microknowledge

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.TestVolume
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.microknowledge.model.KnowledgeResource
import com.mooc.microknowledge.model.MicroKnowledge
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KnowledgeAPI {

    /***
     * 点赞
     * @param like 1点赞 0取消点赞
     * @return
     */
    @GET("api/mobile/micro_knowledge/like/")
    suspend fun postLike(
        @Query("knowledge_id") id: String,
        @Query("like") like: String
    ): HttpResponse<Any>

    /***
     * 获取微知识统计数据
     * @param
     * @return
     */
    @GET("api/mobile/micro_knowledge/statistics/{PK}/")
    suspend fun getKnowLedgeStatistics(@Path("PK") id: String): HttpResponse<MicroKnowledge>


    /***
     * 获取微知识详情
     * @param
     * @return
     */
    @GET("api/mobile/micro_knowledge/{PK}/")
    suspend fun getKnowLedgeDetail(@Path("PK") id: String): HttpResponse<MicroKnowledge>

    /***
     * 获取微知识资源列表
     * @param
     * @return
     */
    @GET("api/mobile/micro_knowledge/resource/")
    suspend fun getKnowLedgeSources(@Query("knowledge_id") id: String): HttpResponse<List<KnowledgeResource>>

    /***
     * 获取微知识详情
     * @param
     * @return
     */
    @GET("api/mobile/micro_knowledge/exam/")
    suspend fun getKnowLedgeExam(@Query("knowledge_id") id: String): HttpResponse<List<TestVolume>>

    /**
     * @param 1 点赞 0 取消点赞
     */
    @GET("/api/mobile/micro_knowledge/like/")
    suspend fun postKnowLedgePrise(
        @Query("knowledge_id") id: String,
        @Query("like") like: Int
    ): HttpResponse<Any>


    /***
     * 获取微知识推荐
     * @param
     * @return
     */
    @GET("api/mobile/micro_knowledge/similar/{PK}/")
    suspend fun getKnowledgeMicroRecommend(@Path("PK") id: String): HttpResponse<List<MicroKnowBean>>
}