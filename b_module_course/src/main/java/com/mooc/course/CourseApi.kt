package com.mooc.course

import com.google.gson.JsonObject
import com.mooc.commonbusiness.model.FollowMemberBean
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.course.model.*
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

interface CourseApi {

    //获取课程详情
    @GET("api/web/courses/{courseId}/")
    fun getCourseDetail(@Path("courseId") courseId: String): Deferred<CourseDetail>

    //获取新学堂详情
    @GET("/api/web/courses_xt/{course_id}/")
    fun getNewXtCourseDetail(@Path("course_id") course_id: String): Deferred<HttpResponse<CourseDetail>>

    /***
     * 获取中国大学慕课详情（富文本格式）
     * @param courseId
     * @return
     */
    @GET("/api/web/courses_zh/{courseId}/")
    fun getMoocDetail(@Path("courseId") courseId: String): Deferred<HttpResponse<CourseDetail>>

    //获取课程章节列表
    @GET("api/v2/course/{strCourseId}/chapters")
    fun getCourseChapterList(@Path("strCourseId") strCourseId: String, @Query("show_sequentials") show_sequentials: Boolean = true, @Query("show_exams") show_exams: Boolean = false): Deferred<CourseChapterListResponse>

    //获取课程公告列表
    @GET("api/v2/course/{courseId}/updates")
    fun getCourseNotice(@Path("courseId") courseId: String, @Query("timestamp") timestamp: String = "0"): Deferred<CourseChapterListResponse>

    //获取课程考试列表
    @GET("api/v2/course/{courseId}/exams")
    fun getCourseExamsData(@Path("courseId") courseId: String): Deferred<CourseExamResponse>

    //同步课程
    @POST("/api/web/student/study/redis/course/{courseId}/")
    fun postRedisCourse(@Path("courseId") courseId: String): Deferred<JsonObject>


    //选课报名接口
    @POST("/api/web/student/enrollment/course/{courseId}/")
    fun selectionCourse(@Path("courseId") courseId: String): Deferred<HttpResponse<Any>>

    //新学堂课程加入学习室
    @POST("/api/web/student/enrollment/course/{courseId}/")
    fun addXtCourseToStudyRoom(@Path("courseId") courseId: String?, @Query("cid") cid: String?): Deferred<HttpResponse<Any>>

    /**
     *获取智慧树课程章节
     * 智慧树的渠道id 33
     */
    @GET("/api/partner/courseware/")
    fun getZHSChapters(@Query("course_id") course_id: String, @Query("platform_id") platform_id: String = "33"): Deferred<HttpResponse<ZHSChapterData>>

    //获取智慧树考试
    @GET("/api/partner/exam/")
    fun getZHSExamData(@Query("course_id") course_id: String, @Query("platform_id") platform_id: String = "33"): Deferred<HttpResponse<ZHSExam>>

    //上传课程得分 （课程详情页）
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/course/score/set/")
    fun postCourseScoreAsync(@Body body: RequestBody?): Deferred<PostCourseScore>

    //获取新学堂课程状态
    @GET("/api/web/courses_xt/{course_id}/{classroom_id}/status/")
    fun getXtCourseState(@Path("course_id") course_id: String?, @Path("classroom_id") classroom_id: String?): Deferred<HttpResponse<XtCourseStatus>>

    //获取中国大学mooc课程状态
    @GET("/api/web/courses_xt/{course_id}/{classroom_id}/status/")
    fun getMoocCourseState(@Path("course_id") course_id: String?, @Path("classroom_id") classroom_id: String?): Observable<HttpResponse<XtCourseStatus>>

    /**
     * 新学堂章节列表
     */
    @GET("/api/mobile/courses_xt/{classroom_id}/chapters/")
    fun getXtCourseDownloadList(@Path("classroom_id") classRoomId: String): Deferred<HttpResponse<List<ChaptersBean>>>

    /**
     * 获取新学堂课程视频下载地址
     */
    @GET("/api/mobile/courses_xt/{classroom_id}/video/download/{video_id}/")
    fun getXtCourseVideoMessage(@Path("classroom_id") classRoomId: String?, @Path("video_id") video_id: String): Deferred<HttpResponse<XtCourseVideoMessageBean>>

    /**
     * 智慧树视频打点
     */
    @GET("http://armystudy.zhihuishu.com/armystudy/moocnd/course/hearbeat")
    fun zhsVideoPlayHeartBeat(@QueryMap map: HashMap<String, String>): Call<ResponseBody?>?

    /**
     * 告诉后段同步智慧树课程进度
     * body 传课程id
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/partner/course/process/set/")
    suspend fun zhsCourseProcess(@Body body: RequestBody): HttpResponse<Any>

    /**
     * 视频用户行为打点
     */
    @GET("/api/mobile/course/{courseId}/action/")
    fun videoPlayAction(@Path("courseId") courseId: String, @QueryMap map: HashMap<String, String>): Observable<HttpResponse<Any>>

    /**
     * 获取课程视频播放提示
     */
    @GET("/api/mobile/course/{courseId}/remind/")
    fun courseVideoTip(@Path("courseId") courseId: String, @QueryMap map: HashMap<String, String>): Observable<HttpResponse<VideoTipBean>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/course/enroll/usersetting/")
    fun enrollUserSetting(@Body body: RequestBody): Deferred<HttpResponse<Any>>


    //获取推荐文章
    @GET("/api/mobile/similar/resource/recommend/")
    fun getRecommendCourse(
            @Query("resource_type") resource_type: Int = 2,
            @Query("resource_id") resource_id: String): Deferred<HttpResponse<MutableList<CourseBean>>?>

    //资源评价维度
    @GET("/api/mobile/resource/appraise/")
    fun getAppraise(@Query("resource_type") resource_type: String): Deferred<HttpResponse<ArrayList<CourseScoreBean>>>

    //用户资源评分
    @POST("/api/mobile/resource/appraise/")
    fun postCourseAppraise(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    //选课用户列表接口
    @GET("/api/mobile/course/sub/user/")
    fun getChoseCoursePeopleList(
            @Query("course_id") course_id: String?,
            @Query("sub_users") sub_users: String?,
            @Query("limit") limit: Int,
            @Query("offset") offset: Int,
    ): Deferred<HttpResponse<CourseChoseUserBean>>

    //获取中国大学mooc绑定提示信息
    @GET("/api/mobile/course/msg_box/")
    fun getMoocBindMsg() : Observable<HttpResponse<HotListBean?>>

    //发送绑定信息
    @POST("/api/mobile/course/msg_box/")
    fun postMoocBindMsg() : Observable<HttpResponse<Any>>

}