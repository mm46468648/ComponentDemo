package com.mooc.studyproject.api

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.commonbusiness.model.studyroom.ShareSchoolCircleBean
import com.mooc.studyproject.model.*
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.*


/**

 * @Author limeng
 * @Date 2020/11/25-6:52 PM
 */
interface UserApi {

    /***
     * 取消订阅  打卡消息
     * @param body
     * @return
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/subscribe_checkin_remind/")
    fun orderMessage(@Body body: RequestBody): Deferred<HttpResponse<Any>?>?


    //获取学习计划详情
    @GET("/api/mobile/studyplan/detail/new/{resId}/")
    fun getStudyPlanDetail(@Path("resId") resId: String): Deferred<StudyPlanDetailBean>

    //获取笔记列表
    @GET("/api/mobile/studyplan/note/{noteId}/")
    fun getStudyPlanNoticeList(@Path("noteId") noteId: String): Deferred<NoticeBean>

    //学习计划兑换码兑换
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/coupon/exchange/")
    fun exchangeCode(@Body body: RequestBody): Deferred<ExChangeBean>

    //报名学习计划
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/userjoin/")
    fun postAddStudyPlan(@Body body: RequestBody): Deferred<StudyPlanAddBean?>

    //获取学习计划未完成 （学习计划详情页）
    @GET("/api/mobile/studyplan/detail/not/finish/resource/{id}/")
    fun getLearnStudyNotFinish(@Path("id") id: String,
                               @Query("limit") limit: Int,
                               @Query("offset") offset: Int): Deferred<StudyPlansBean?>
    //获取选学列表
    @GET("/api/mobile/studyplan/detail/passed/resource/{pk}/")
    fun getChoseLearnFinish(@Path("pk") id: String,
                               @Query("limit") limit: Int,
                               @Query("offset") offset: Int): Deferred<StudyPlansBean?>

    //获取学习计划已完成 （学习计划详情页）
    @GET("/api/mobile/studyplan/detail/finish/resource/{id}/")
    fun getLearnStudyFinish(@Path("id") id: String,
                            @Query("limit") limit: Int,
                            @Query("offset") offset: Int): Deferred<StudyPlansBean?>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/resource/checkin/")
    fun postResourceClickStatus(@Body body: RequestBody): Deferred<ResultMsgBean?>

    //获取音频播放状态（音频）
    @GET("/api/mobile/studyplan/audio/info/{id}/")
    fun getResourceStatus(@Path("id") id: String): Deferred<ResourceStatusBean>

    //发布动态
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/cms/studyplan/activity/{studyPlan}/")
    fun publishDynamics(@Path("studyPlan") studyPlan: String, @Body body: RequestBody): Deferred<DynamicsBean?>

    //获取已发布动态用户
    @GET("/api/mobile/studyplan/activity/users/{id}/")
    fun getStudySourceMembers(@Path("id") id: String, @Query("checkin_source_id") checkin_source_id: String?)
            : Deferred<StudyMemberBean?>

    //动态点赞
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/userlike/")
    fun postFillStudyPlan(@Body body: RequestBody): Deferred<PostFillStudyPlanBean?>

    //获取动态学习计划详情
    @GET("/api/mobile/studyplan/detail/activity/{id}/")
    fun getStudyPlanDetailWithTitle(@Path("id") id: String,
                                    @QueryMap map: HashMap<String, String>): Deferred<StudyActivityBean?>

    //获取动态学习计划详情
    @GET("/api/mobile/studyplan/detail/activity/{id}/")
    fun getStudyPlanDetailActivity(@Path("id") id: String,
                                   @Query("limit") limit: Int,
                                   @Query("offset") offset: Int): Deferred<StudyActivityBean?>


    //获取我的动态详情
    @GET("/api/mobile/studyplan/my/activity/{id}/")
    fun getStudyPlanMyActivity(@Path("id") id: String,
                               @Query("limit") limt: Int,
                               @Query("offset") offset: Int): Deferred<StudyActivityBean?>

    //获取我的发布的动态
    @GET("/api/mobile/studyplan/my/activity/{id}/")
    fun getStudyDynamicWithTitleMy(@Path("id") id: String,
                                   @QueryMap map: HashMap<String, String>): Deferred<StudyActivityBean?>


    //删除动态
    @DELETE("/api/mobile/studyplan/activity/delete/{id}/")
    fun delDynamic(@Path("id") id: String): Deferred<ResultMsgBean>

    //获取举报选项
    @GET("/api/mobile/studyplan/report/choices/")
    fun getReportLearnData(): Deferred<ReportLearnBean?>

    //上传举报信息
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/activity/report/")
    fun postReportLearnData(@Body body: RequestBody): Deferred<ResultMsgBean?>

    //隐藏某条动态
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/change/activity/")
    fun postStopStudyPlan(@Body body: RequestBody): Deferred<PostFillStudyPlanBean?>

    //分享学友圈
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/school_circle/share/")
    fun postSchoolShare(@Body body: RequestBody): Deferred<ShareSchoolCircleBean?>

    //发表评论接口
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/cms/studyplan/comment/{study_activity}/")
    fun postCommentData(@Path("study_activity") study_activity: String, @Body body: RequestBody): Deferred<SendCommendBean?>

    //获取评论列表（评论列表页）
    @GET("/api/mobile/studyplan/comment/{id}/")
    fun getCommentList(@Path("id") id: String,
                       @Query("limit") limit: Int,
                       @Query("offset") offset: Int): Deferred<CommentList>



    //获取动态详情
    @GET("/api/mobile/studyplan/comment/activity/detail/{id}/")
    fun getCommentDynamic(@Path("id") id: String): Deferred<StudyDynamic?>

    //屏蔽某条动态
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/change/comment/")
    fun postStopCommentStudyPlan(@Body body: RequestBody): Deferred<PostFillStudyPlanBean?>

    //删除评论
    @DELETE("/api/mobile/studyplan/comment/delete/{comment_id}/")
    fun delComment(@Path("comment_id") comment_id: String): Deferred<PostFillStudyPlanBean?>

    //上传语音文件
    @POST("/api/cms/audio/upload/")
    fun publishVoice(@Body body: RequestBody): Deferred<UploadVoiceBean?>

    //上传语音文件,返回Observable
    @POST("/api/cms/audio/upload/")
    fun publishVoiceObserver(@Body body: RequestBody): Observable<UploadVoiceBean>

    //上传跟读音频链接
    @POST("/api/mobile/studyplan/repeat_book_context/")
    fun postFollowUpAudioUrl(@Body requestBody: RequestBody): Observable<HttpResponse<Any>>

    //上传跟读音频链接
    @POST("/api/mobile/resource/repeat_book_context/")
    fun postResourceAudioUrl(@Body requestBody: RequestBody): Observable<HttpResponse<Any>>

    /**
     * 获取跟读内容绑定资源进度（电子书阅读进度）
     * @param studyPlanId 学习项目绑定资源id
     */
    @GET("/api/mobile/studyplan/repeat_book/")
    fun getFollowupDetail(@Query("repeat_books_id") studyBindId: String): Observable<HttpResponse<FollowupResponse>>

    //获取掌阅电子书详情
    @GET("/api/resources/ebook/detail/{bookId}/")
    fun getEbookDetail(@Path("bookId") bookId: String): Observable<EBookBean>

    //获取跟读资源内容列表
    @GET("/api/mobile/studyplan/repeat_book_context/")
    fun getFollowupResourseList(@Query("repeat_books_id") studyPlanId: String, @Query("resource_id") sourceType: String): Observable<HttpResponse<FollowupResourse>>

    //获取跟读资源内容列表(适用于其他资源)
    @GET("/api/mobile/resource/repeat_book_context/")
    fun getFollowupList(@Query("repeat_books_id") repeat_books_id: String, @Query("resource_id") resource_id: String, @Query("resource_type") sourceType: String): Observable<HttpResponse<FollowupResourse>>

    //跟读资源时，弹出'您没读完整，再来一次吧'，上传跟读音频链接
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/repeat_book_context_not_pass/")
    fun postAudioUrl(@Body requestBody: RequestBody): Observable<HttpResponse<Any>>

    //跟读资源时，弹出'您没读完整，再来一次吧'，上传跟读音频链接
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/resource/repeat_book_context_not_pass/")
    fun postResourceUrl(@Body requestBody: RequestBody): Observable<HttpResponse<Any>>

    //检测是否需要排队
    @GET("api/mobile/studyplan/repeat_audio_num/")
    fun getRepeatAudioLimit(
            @Query("studyplan_id") studyplan_id: String,
            @Query("repeat_books_context_id") repeat_books_context_id: String,
            @Query("repeat_books_id") repeat_books_id: String
    ): Observable<HttpResponse<OverLimitBean>>

    //检测是否需要排队
    @GET("api/mobile/resource/repeat_audio_num/")
    fun getResourceAudioLimit(
        @Query("repeat_books_id") repeat_books_id: String,
        @Query("repeat_books_context_id") repeat_books_context_id: String,
        @Query("resource_id") studyplan_id: String,
        @Query("resource_type") resource_type: String
    ): Observable<HttpResponse<OverLimitBean>>

    //取消评测排队
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/studyplan/repeat_audio_num/")
    fun delRepeatAudioLimit(@Body body: RequestBody?): Observable<HttpResponse<Any>>


    //取消评测排队
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/resource/repeat_audio_num/")
    fun delResourceAudioLimit(@Body body: RequestBody?): Observable<HttpResponse<Any>>

    @Multipart
    @POST("/api/mobile/studyplan/upload_repeat_audio/")
    fun uploadRepeatAudio(
            @Part requestBody: List<MultipartBody.Part>, @Part("file\"; filename=\"test.wav") file: RequestBody?
    ): Observable<HttpResponse<FollowUrlBean>>

    @Multipart
    @POST("/api/mobile/resource/upload_repeat_audio/")
    fun uploadResourceAudio(
        @Part requestBody: List<MultipartBody.Part>, @Part("file\"; filename=\"test.wav") file: RequestBody?
    ): Observable<HttpResponse<FollowUrlBean>>

    @GET("/api/mobile/studyplan/repeat_book/check_status/")
    fun loopGetTestResult(@Query("resource_id") studyplan_id: String,
                          @Query("repeat_books_id") repeat_books_context_id: String,
                          @Query("context_id") repeat_books_id: String): Observable<HttpResponse<LoopBean>>

    @GET("/api/mobile/resource/repeat_book/check_status/")
    fun loopFollowupResult(@Query("resource_id") resource_id: String,
                           @Query("resource_type") resource_type: String,
                          @Query("repeat_books_id") repeat_books_context_id: String,
                          @Query("context_id") repeat_books_id: String): Observable<HttpResponse<LoopBean>>

    //自荐接口
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("api/mobile/studyplan/activity/user/nomination/")
    fun postSelfRecommendAsync(@Body body: RequestBody?): Deferred<HttpResponse<StudyDynamic>>
}
