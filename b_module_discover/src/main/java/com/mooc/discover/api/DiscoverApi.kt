package com.mooc.discover.api

import com.google.gson.JsonObject
import com.mooc.commonbusiness.model.AnnouncementBean
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.MicroProfession
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.commonbusiness.model.studyproject.StudyProject
import com.mooc.discover.model.*
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.*

interface DiscoverApi {


    //获取发现页菜单分类
    @GET("/api/mobile/index/sort/")
    suspend fun getDiscoverTabSort(@Query("tab_id") tab_id: Int): HttpResponse<MutableList<TabSortBean>>


    //获取banner
    @GET("/api/mobile/v2/banners/")
    suspend fun getBannerData(): HttpResponse<BannerBean>


    //获取任务
    @GET("/api/mobile/task_system/img/")
    fun getTaskData(): Deferred<HttpResponse<TaskBean>>

    //获取公告data
    @GET("/api/mobile/announcement_list/")
    fun getNoticeData(): Deferred<HttpResponse<List<AnnouncementBean>>>

    //获取首页快捷入口
    @GET("/api/mobile/quick/entry/")
    fun getQuickEntry(): Deferred<HttpResponse<List<QuickEntry>>>

    //获取猜你喜欢
    @GET("/api/opdata/resource/recommend/new_like/v2/")
    fun getGuess(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<HttpResponse<GuessLikeBean>>


    //获取学习计划列表 （发现页学习计划）
    @GET("/api/mobile/recommend/studyplan/")
    suspend fun getDiscoverStudyPlan(@Query("offset") offset: Int, @Query("limit") limit: Int): HttpResponse<List<StudyProject>>

    //获取微知识列表
    @GET("/api/mobile/micro_knowledge/")
    suspend fun getMicroKnowledge(@Query("offset") offset: Int, @Query("limit") limit: Int): HttpResponse<List<MicroKnowBean>>

    //获取微专业列表
    @GET("/api/mobile/specialized/course/list/")
    suspend fun getMicroProfessions(@Query("limit") limit: Int, @Query("offset") offset: Int): HttpResponse<List<MicroProfession>>

    //获取我的订阅 （发现页我的订阅)
    @GET("/api/web/recommend/mysubscribe/")
    suspend fun getMyOrder(@Query("limit") limit: Int, @Query("offset") offset: Int): HttpResponse<List<MyOrderBean.ResultsBean>>


    //获取专栏分类列表 （发现页）
    @GET("/api/mobile/recommend/detail/list/{id}/")
    fun getRecommendDetailList(@Path("id") id: String, @Query("limit") limit: Int, @Query("offset") offset: Int): Deferred<RecommendZhuanlanBean>

    //获取任务列表
    @GET("/api/mobile/task_system/")
    fun getDiscoverTaskResult(@Query("limit") limit: Int, @Query("offset") offset: Int):
            Deferred<HttpResponse<HttpResponse<List<DiscoverTaskBean>>>>


    //获取我的订阅 （发现页我的订阅)
    @GET("/api/mobile/task_system/my/")
    fun getMyDiscoverTaskResult(@Query("status") status: Int,//新增参数 status=1 进行中任务 2历史任务
                                @Query("limit") limit: Int,
                                @Query("offset") offset: Int):
            Deferred<HttpResponse<HttpResponse<List<DiscoverTaskBean>>>>


    //获取某个专题下数据
    @GET("/api/web/recommend/{id}/")
    fun getRecommendListWithId(@Path("id") id: String?, @QueryMap map: Map<String, String>): Deferred<RecommendContentBean>


    //推荐列表中单个专栏订阅,专栏详情订阅(只能订阅不能取消)
    @POST("/api/web/recommend/subscribe/{resId}/")
    suspend fun postSpecialSubscribe(@Path("resId") resId: String): HttpResponse<Any>

    //获取专栏列表
    @GET("/api/web/recommend/{id}/")
    fun getColumnList(@Path("id") id: String, @Query("page") page: Int, @Query("page_size") page_size: Int): Observable<RecommendContentBean>

    //获取推荐查看更多列表
    @GET("/api/web/recommend/{id}/")
    fun getRecommendLookMoreList(@Path("id") id: String, @Query("page") page: Int, @Query("page_size") page_size: Int): Deferred<RecommendContentBean>

    /**
     * 发现页换一换
     * @param exchange 1 代表换一换
     */
    @GET("/api/web/recommend/{id}/")
    fun getExchangeRecommendListWithId(@Path("id") id: String, @Query("page_size") page_size: Int, @Query("exchange") exchange: Int = 1): Observable<RecommendColumn>

    //获取发现页专栏
    @GET("/api/web/recommend/subscribe/all/list/")
    fun getSubscribeAllList(): Deferred<ArrayList<SubscribeAllResponse>>


    //上传订阅栏目菜单(将订阅的栏目id，以数组方式{"ids":[]})上传
    @POST("/api/web/recommend/subscribe/change/")
    fun postColumnMenu(@Body body: RequestBody): Deferred<JsonObject>


    //获取更多专栏列表
    @GET("/api/web/recommend/type/{parentId}/")
    fun getRecommendResTypes(@Path("parentId") parentId: String?): Deferred<RecommendResTypeBean>


    //获取推荐栏目数据更多列表
    @GET("/api/web/recommend/index/")
    fun getRecommendColumn(@Query("offset") offset: Int, @Query("limit") limit: Int):
            Deferred<HttpResponse<List<RecommendColumn>>>

    //首页获取推荐栏目接口，根据cms设置返回栏目个数
    @GET("/api/web/recommend/new/index/")
    fun getRecommendDiscoverColumn(): Deferred<HttpResponse<List<RecommendColumn>>>

    //获取热门 课程和文章列表
    @GET("/api/opdata/resource/recommend/hot/")
    suspend fun getRecommendHotRes(@Query("resource_type") resource_type: Int,
                                   @Query("limit") limit: Int,
                                   @Query("offset") offset: Int): HttpResponse<List<Any>>

    //获取最新上线
    @GET("/api/opdata/resource/recommend/new/v2/")
    fun getNewOnline(@Query("limit") limit: Int, @Query("offset") offset: Int): Deferred<HttpResponse<ArrayList<ResultBean>>>

    //删除我的订阅
    @DELETE("/api/web/recommend/subscribe/{id}/")
    fun deleteSubscribeReq(@Path("id") id: String): Observable<HttpResponse<Any>>

    //推荐列表中单个专栏订阅,专栏详情订阅
    @POST("/api/web/recommend/subscribe/{resId}/")
    fun postSubscribeReq(@Path("resId") resId: String): Observable<HttpResponse<Any>>


//    //公告详情
//    @GET("/api/mobile/announcement_detail/{id}/")
//    fun getAnnouncementId(@Path("ann_id") type: String): Deferred<HttpResponse<AnnouncementBean>>

    //任务详情
    @GET("/api/mobile/task_system/{id}/")
    fun getTaskSystemId(@Path("id") type: String): Deferred<HttpResponse<TaskDetailsBean>>

    //任务详情
    @GET("/api/mobile/task_system/{id}/")
    fun getTaskSystemIdRx(@Path("id") type: String): Observable<HttpResponse<TaskDetailsBean>>

    //任务绑定清单资源
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/task_system/bind_source/")
    fun bindTaskSource(@Body body: RequestBody): Deferred<HttpResponse<TaskBindStudyListBean>>


    //推荐相似用户
    @GET("/api/mobile/student/label/")
    fun getRecommendUsers(): Observable<HttpResponse<ArrayList<RecommendUser>>>

    //关注用户
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/school_circle/follow/user/")
    fun postFollowUser(@Body body: RequestBody): Observable<HttpResponse<Any>>

    /**
     * 获取可以加入的活动
     */
    @GET("/api/activity/join/")
    fun getActivityJoin(): Observable<ActivityJoinBean>


    //领取任务
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/task_system/")
    fun postTaskToReceive(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    //更新已读
    @POST("/api/web/recommend/read/{id}/")
    fun updateRecommendRead(@Path("id") id: String): Observable<HttpResponse<Any>>?

    //获取快捷入口组合栏目数据
    @GET("/api/mobile/quick/entry/{id}/")
    fun getQuickColumn(@Path("id") type: String, @Query("offset") offset: Int, @Query("limit") limit: Int):
            Deferred<HttpResponse<QuickColumn>>

    @GET("/api/mobile/task_system/lp/")
    suspend fun getTaskGuideConfig() : HttpResponse<Boolean>

    @POST("/api/mobile/task_system/lp/")
    suspend fun postTaskGuideConfig() : HttpResponse<Any>


    @POST("/api/mobile/task_system/finish/early_checkin/")
    suspend fun postTaskEarlyCheckin(@Body body:RequestBody):HttpResponse<Any>


    @GET("/api/mobile/task_system/finish/early_checkin/")
    suspend fun getTaskEarlyContent():HttpResponse<EarlyContent>;
}