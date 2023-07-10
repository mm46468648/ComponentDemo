package com.mooc.commonbusiness.api

import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.model.xuetang.*
import com.mooc.commonbusiness.model.HttpResponse
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.*

/**
 * 学堂相关API接口
 */
interface XtApi {




    //获取学堂课程信息
    @GET("api/v2/course/{courseId}/enroll")
    fun getCourseEnroll(@Path("courseId") courseId: String, @Query("with_mode") with_mode: String, @Query("with_verify_status") with_verify_status: String): Observable<GetCourseEnrollDataBean>


    //获取学堂课程登记信息（就是课程详情接口,仅限于学堂的接口）
    @GET("api/v2/course/enroll/{courseId}/")
    fun getCourseEnrollDetail(@Path("courseId") courseId: String?): Deferred<CourseBean>

    //上传学堂课程信息
    @POST("api/v2/course/{courseId}/enroll/")
    fun postCourseEnroll(@Path("courseId") courseId: String, @Body requestBody: RequestBody): Observable<GetCourseEnrollDataBean>

    //删除学堂课程信息
    @DELETE("api/v2/course/{courseId}/enroll/")
    fun delCourseEnroll(@Path("courseId") courseId: String): Observable<GetCourseEnrollDataBean>


    //同步课程
    @FormUrlEncoded
    @POST("api/v2/course/{courseId}/sync")
    fun syncCourse(@Path("courseId") courseId: String, @Field("chapter_id") chapter_id: String, @Field("sequential_id") sequential_id: String
                   , @Field("timestamp") timestamp: String, @Field("video_id") video_id: String, @Field("saved_video_position") saved_video_position: String): Observable<SyncMoreCourseDataBean>


    //发送订阅课程
    @POST("api/v2/course/{courseId}/enroll")
    fun postCourseEnroll(@Path("courseId") courseId: String?, @Query("with_mode") with_mode: Int = 1, @Query("with_verify_status") with_verify_status: Int = 1): Deferred<GetCourseEnrollDataBean>
    //获取课程认证状态
    @GET("api/v2/cachenode/course/{xtCourseId}/cert_status")
    fun getCourseVerifyStatus(@Path("xtCourseId") xtCourseId: String): Deferred<HttpResponse<VerifyStatusBean>>

//    //获取课程公告列表
//    @GET("api/v2/course/{courseId}/updates")
//    fun getCourseUpdates(@Path("courseId") courseId: String, @Query("timestamp") timestamp: String): Observable<Any>
//
//    //获取课程考试列表
//    @GET("api/v2/course/{courseId}/exams")
//    fun getCourseExamsData(@Path("courseId") courseId: String): Observable<GetCourseExamsDataBean>

    //申请学堂课程证书
    @Multipart
    @POST("api/v2/credential/electric_moocnd/apply/")
    fun applyXtCourseVerify(@PartMap requestBodyMap: Map<String, RequestBody>): Observable<ApplyVerifyBean>



    //获取证书列表
//    @GET("api/web/honors")
//    fun getHonorList(@Query("limit") limit: Int, @Query("offset") offset: Int): Observable<HonorDataBean>

//    //获取
//    @GET("api/v2/course/{strCourseId}/chapter/{strChapterId}/quizs")
//    fun getVideoQuizList(@Path("strCourseId") strCourseId: String, @Path("strChapterId") strChapterId: String): Observable<Any>
//
//    //获取
//    @GET("api/v2/course/{strCourseId}/sequential/{sequential}/verticals/problem")
//    fun getSequentialProblemList(@Path("strCourseId") strCourseId: String, @Path("sequential") sequential: String): Observable<Any>
//
//
//    //获取课程章节信息
//    @GET("api/v2/course/{strCourseId}/chapters")
//    fun getCourseChapterList(@Path("strCourseId") strCourseId: String, @QueryMap nap: HashMap<String, String>): Observable<Any>

    //获取课程小节详情  里面通过source才能查询单节视频地址信息
    @GET("api/v2/course/{strCourseId}/sequential/{strSequentialID}/verticals")
    fun getSequenceDetail(@Path("strCourseId") strCourseId: String, @Path("strSequentialID") strSequentialID: String): Deferred<SequentialPlayList>



    /**
     *获取课程的视频播放地址
     * @param sequentialsID xt课程课件列表中小章节id
     * @param quality 视频质量
     */
    @GET("api/v2/video/{sequentialsSource}/{quality}")
    fun getVideoUrl(@Path("sequentialsSource") sequentialsSource: String, @Path("quality") quality: String = NormalConstants.VideoQuality20.toString()): Deferred<VideoUrl>

}