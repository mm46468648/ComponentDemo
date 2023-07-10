package com.mooc.commonbusiness.api

import com.mooc.commonbusiness.model.ActivityTaskBean
import com.mooc.commonbusiness.model.AnnouncementBean
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.ResDataBean
import com.mooc.commonbusiness.model.search.MicroBean
import com.mooc.commonbusiness.model.search.PeriodicalBean
import com.mooc.commonbusiness.model.studyproject.NoticeInfoBean
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * 其他
 */
interface OtherApi {

    @GET("/api/mobile/micro_course/url/")
    fun getMicroDetail(@Query("course_id") id: String): Observable<MicroBean>

    //获取超星期刊详情
    @GET("/api/web/chaoxing/detail/")
    fun getPeiodicalDetail(@Query("basic_url") basic_url: String): Observable<HttpResponse<PeriodicalBean>>


    /**
     * 获取超星期刊html内容
     */
    @GET
    fun getPeiodicalContent(@Url url: String): Observable<HttpResponse<String>>

    //期刊资源入库，解决在学习记录打卡期刊资源样式错乱的问题
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/other_resource/")
    fun postPeiodicalToDB(@Body body: RequestBody): Deferred<PeriodicalBean>

    /**
     * 获取活动任务详情
     */
    @GET("/api/mobile/event/task/{resourceId}/")
    fun getActivityTaskDetail(@Path("resourceId") resourceId: String): Observable<HttpResponse<ActivityTaskBean>>

    //公告详情
    @GET("/api/mobile/announcement_detail/")
    fun getAnnouncementDetail(@Query("ann_id") id: String): Deferred<HttpResponse<AnnouncementBean>>

    @GET("/api/mobile/studyplan/note/detail/{noteId}/")
    fun getNoticeInfo(@Path("noteId") id: String?): Deferred<NoticeInfoBean>

    //获取资源分享数据
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/other_resource/")
    suspend fun getResData(@Body body: RequestBody): ResDataBean
}