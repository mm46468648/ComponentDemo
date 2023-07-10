package com.mooc.home.api

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.model.search.SearchResultBean
import com.mooc.commonbusiness.model.studyroom.DiscoverTab
import com.mooc.commonbusiness.model.studyroom.SortChild
import com.mooc.home.model.*
import com.mooc.home.model.todaystudy.TodayStudyMust
import com.mooc.home.model.todaystudy.TodaySuscribe
import com.mooc.home.model.todaystudy.TodayTask
import com.mooc.home.model.honorwall.MyContributionBean
import com.mooc.home.model.honorwall.PlatformContributionBean
import com.mooc.home.model.todaystudy.TargetAdjustBean
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*


interface HomeApi {


    //获取今日阅读（首页弹窗）
    @GET("/api/mobile/weixin/official/daily_read/new/")
    fun getTodayReadAsync(): Deferred<HttpResponse<DailyReadBean>>

    //上传首页资源弹窗信息
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST
    fun postAlertMsgRead(@Url url: String, @Body body: RequestBody): Deferred<HttpResponse<Any>>

    //获取发现页Tab菜单数据
    @GET("/api/mobile/index/tab/")
    suspend fun getDiscoverTabSort(): HttpResponse<List<DiscoverTab>>

    //获取发现页Tab菜单子分类数据
    @GET("/api/mobile/index/sort/")
    suspend fun getDiscoverChildTabSort(@Query("tab_id") tab_id: Int): HttpResponse<ArrayList<DiscoverTab>>

    //获取发现页Tab菜单子分类第3级分类数据
    @GET("/api/mobile/index/children/sort/")
    fun getDiscoverChild2Sort(@Query("sort_id") sout_id: Int): Deferred<HttpResponse<ArrayList<SortChild>>>


    //获取荣耀列表（学习项目达人）
    @GET("/api/mobile/studyplan/honor/roll/")
    fun getHonorRoll(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<HttpResponse<List<HonorRollResponse>>>

    //获取荣耀详情列表
    @GET("/api/mobile/studyplan/honor/roll/{planId}/")
    suspend fun getStudyPlanHonorDetial(@Path("planId") planId: String, @Query("offset") offset: Int, @Query("limit") limit: Int): HonorRollResponse

    //获取活动排行列表
    @GET("/api/mobile/activity/list/")
    fun getActivityList(): Deferred<ActivityRankResponse>

    //获取今日学习数据
    @GET("/api/mobile/today/task/data/")
    fun getTodaySource(): Deferred<TodayStudyData>

    //获取今日学习人物形象Bean
    @GET("/api/mobile/search/img/")
    suspend fun getTodayStudyIcon(): TodayStudyIconBean

//    //获取积分明细
//    @GET("/score/detail/")
//    fun getScoreDetail(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<ScoreDetailResponse>


    //获取我的订阅 （发现页我的订阅)
//    @GET("/api/web/recommend/mysubscribe/")
//    fun getSubscribe(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<HttpResponse<List<ContentResponse>>>

    //获取专栏分类列表 （发现页，推荐中的专题，专栏都请求这个接口）
//    @GET("/api/mobile/recommend/detail/list/{id}/")
//    fun getRecommedColumnList(@Path("id") id: String, @Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<HttpResponse<List<ContentResponse>>>


    //获取学习计划列表 （发现页学习计划）
//    @GET("/api/mobile/recommend/studyplan/")
//    fun getDiscoverStudyPlan(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<HttpResponse<List<StudyProject>>>

    //获取搜索资源结果 （发现页中的 幕课，音频，电子书等都是搜索接口）
    @GET("/weixin/official/search/")
    suspend fun getSearchResult(@QueryMap map: HashMap<String, String>): SearchResultBean


    //获取平台菜单
    @GET("/api/mobile/new_course_cates/")
    fun getPlatFormCates(): Deferred<CourseCateResponse>

    //获取今日任务订阅数据
    @GET("/api/mobile/today/task/column/tab/")
    fun getTodayTaskSubscribe(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<TodaySuscribe>

    //获取今日任务专题数据
    @GET("/api/mobile/today/task/special/tab/")
    fun getTodayTaskSpecial(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<TodaySuscribe>

    //获取今日任务已完成数据
    @GET("/api/mobile/today/task/finish/")
    fun getTodayFinishTask(): Deferred<TodayTask>


    /**
     * 获取我的贡献值
     * type 1为日榜，2为月榜
     */
    @GET("api/mobile/devote/ranking/my_devote/")
    fun getMyContribution(@Query("type") type: String): Deferred<HttpResponse<MyContributionBean>>


    //获取榜单
    @GET("/api/mobile/devote/ranking/")
    fun getDevoteRankingResult(@Query("type") type: String, @Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<HttpResponse<ArrayList<PlatformContributionBean>>>

    //获取今日学习必看数据
    @GET("/api/mobile/today/task/common/tab/")
    suspend fun getTodayMust(): TodayStudyMust

    /**
     * 上传今日学习最近在学排序
     * json ：{
    "order_list": [2, 5, 10, 11, 12] //用户调整完顺序的类型列表，不能为空
    }
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/today/task/common/tab/order/")
    fun postTodayStudyTabOrder(@Body body: RequestBody): Deferred<HttpResponse<Any>>
    //上传今日任务单项点击
    /**
     * body中需包含
     * "resource_id"
     * "resource_info"
     * task_type
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/student/today_task/")
    fun postTodayTask(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    //获取今日任务目标调整条目
    @GET("/api/mobile/today/task/setting/")
    fun getTargetAdajustList(): Deferred<HttpResponse<TargetAdjustBean>>


    /**
     *上传更改目标调整
     * TragetDetailBean
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/today/task/setting/")
    fun postTargetAdajust(@Body body: RequestBody): Deferred<HttpResponse<Any>>


//    /**
//     * 获取可以加入的活动
//     */
//    @GET("/api/activity/join/")
//    fun getActivityUserJoin(): Deferred<ActivityJoinBean>

    //检查时间
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/check_ts/")
    fun postCheckTime(@Body body: RequestBody?): Deferred<HttpResponse<Any>>

    //获取获得学习计划勋章（首页弹窗）弹出获得积分框  弹出获得计划勋章
    @GET("/api/mobile/studyplan/user/completion/")
    fun getStudyPlanCompletion(): Deferred<StudyCompletion>

    //获取勋章（首页弹窗）
    @GET("/special_medal/")
    fun getSpecialMedal(): Deferred<MedalBean>

    /**
     * /获取用户信息
     */
    @GET("/api/mobile/student/info/")
    suspend fun getUserInfo(): UserInfo


    //获取首页资源展示信息
    @GET()
    fun getAlertMsg(@Url url: String): Deferred<AlertMsgBean>

}