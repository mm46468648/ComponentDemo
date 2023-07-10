package com.mooc.search.api

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.my.ParserStatusBean
import com.mooc.commonbusiness.model.my.UploadFileBean
import com.mooc.commonbusiness.model.studyroom.HonorDataBean
import com.mooc.my.model.*
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*

/**

 * @Author limeng
 * @Date 2020/8/10-11:31 AM
 */
interface MyModelApi {
    //获取搜索资源结果
    @GET("/resource/daily_read/app/")
    suspend fun getEverydayReadData(@Query("limit") limit: Int, @Query("offset") offset: Int): EverydayReadBean

    /**
     *通过日期获取每一读当天内容
     *@param date 日期字符串  2022-03-01
     */
    @GET("api/mobile/weixin/official/daily_read/{date}/")
    suspend fun getEverydayReadDataByDate(@Path("date") date: String): HttpResponse<ReadBean>

    //获取打卡信息
    @GET("/weixin/official/checkin/")
    fun getCheckInData(): Deferred<CheckInDataBean>


    @POST("/weixin/official/checkin/")
    suspend fun postCheckInData(): CheckInDataBean


    //我的证书列表2
    @GET("/api/mobile/certificate/my/")
    fun getMyCertificateList(@Query("limit") limit: Int, @Query("offset") offset: Int): Deferred<HttpResponse<List<HonorDataBean>>>

    //证书申请列表
    @GET("/api/mobile/certificate/apply/")
    fun getCertificateList(@Query("limit") limit: Int, @Query("offset") offset: Int): Deferred<CertificateBean>

    //申请学堂证书
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/certificate/download/")
    fun applyMoocCertificate(@Body body: RequestBody?): Deferred<HttpResponse<Any>>

    //获取常见问题列表
    @GET("/api/mobile/question/list/")
    fun getQuestionList(): Deferred<List<QuestionListBean>>

    //获取热门问题
    @GET("/api/mobile/question/list/more/")
    fun getQuestionListMore(@QueryMap map: Map<String, String>): Deferred<QuestionMoreBean>

    //获取反馈问题列表
    @GET("/api/mobile/feedback/user/list/")
    fun getFeedList(@QueryMap map: Map<String, String>): Deferred<FeedListBean>



    //获取反馈类型
    @GET("/api/web/student/feedback/type/")
    fun getFeedType(): Deferred<FeedBackBean>

    //获取意见反馈列表
    @GET("/api/mobile/feedback/user/talk/list/{feedId}/")
    fun getFeedBackList(@Path("feedId") feedId: String?): Deferred<FeedBackListBean>

    //发送意见反馈
    @FormUrlEncoded
    @POST("/api/mobile/feedback/send/message/")
    fun sendFeedMsg(
            @Field("id") id: String?,
            @Field("content") content: String,
            @Field("reply_id") reply_id: String?,
            @Field("img_attachment") img_attachment: String,
    ): Deferred<SendFeedMsgBean>

    //上传意见反馈
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/web/student/feedback/app/")
    fun postFeedback(@Body body: RequestBody): Deferred<FeedBean>

    //获取我的（我的信息）
    @GET("/api/mobile/school_circle/user/info/{userId}/")
    fun getUserSchoolCircle(@Path("userId") userId: String): Deferred<SchoolCircleBean>

    //用户主页分享（点赞或取消）
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/school_circle/userhome/like/")
    fun postUserLikeAndDis(@Body body: RequestBody?): Deferred<ParserStatusBean>

    //我的分享点赞（我的信息）
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/school_circle/user/like/")
    fun postLikeSchoolResource(@Body body: RequestBody): Deferred<ParserStatusBean>


    //补打卡
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/weixin/official/make_up_checkin/")
    fun postMakeUpCheckinResource(@Body body: RequestBody): Deferred<HttpResponse<Any>>



    //我的分享删除学友圈
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/school_circle/share/delete/")
    fun delSchoolResource(@Body body: RequestBody?): Deferred<ParserStatusBean>

    //上传用户头像
    @POST("/api/mobile/v2/student/upload_avatar/")
    fun postUserHeader(@Body body: RequestBody?): Deferred<HttpResponse<UploadFileBean>>

    //更改昵称
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/student/info/")
    fun updateUserNickname(@Body body: RequestBody?): Deferred<HttpResponse<Any>>

    //获取关注用户列表
    @GET("/api/mobile/school_circle/user/followerlist/{userId}/")
    fun getFollowList(
            @Path("userId") userId: String?,
            @Query("limit") limit: Int,
            @Query("offset") offset: Int,
    ): Deferred<UserFollowBean>

    //我/他公开的学习清单
    @GET("/api/resources/folder/my_show_folder/")
    fun getShowFolderList(
        @Query("user_id") userId: String?,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): Deferred<HttpResponse<HttpResponse<ArrayList<LearingListBean>>>>



    //获取粉丝列表
    @GET("/api/mobile/school_circle/user/befollowerlist/{userId}/")
    fun getFansList(
            @Path("userId") userId: String?,
            @Query("limit") limit: Int,
            @Query("offset") offset: Int,
    ): Deferred<UserFollowBean>

    //获取打卡成功用户(更准确的是为他人加油数据，包括需要播放的音频)
    @GET("/weixin/official/checkin_cheer/")
    fun getCheckInMembers() : Deferred<ComeonOtherResponse>

    /**
     *学友圈
     */
    @GET("/api/mobile/school_circle/all/follow/user/info/")
    fun getSchoolCircle(@Query("limit") limit: Int, @Query("offset") offset: Int):
            Deferred<SchoolCircleBean>

    //鼓励他人弹窗,点赞他人
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/weixin/official/checkin_cheer/")
    fun postcheckinCheer(@Body body: RequestBody): Observable<HttpResponse<Any>>
}