package com.mooc.setting

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.setting.UserSettingBean
import com.mooc.setting.model.*
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface SetApi {

    @GET
    fun getUpgradeAsync(@Url url: String, @Query("channel") channel: String = "Moocnd"): Deferred<ApkUpgradeData>

    @Streaming
    @GET
    fun download(@Url url: String?): Call<ResponseBody>

    //获取设置课程消息
    @GET("/api/mobile/course/enroll/usersetting/")
    fun getSettingCourseMsg(): Deferred<ArrayList<SettingCourseMsgBean>>

    //上传课程消息设置
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/course/enroll/usersetting/")
    fun postSettingCourseMsg(@Body body: RequestBody?): Deferred<SettingCourseMsgBean>

    //获取公告消息开关
    @GET("/api/web/message/interaction_setting/")
    fun getSettingInteractionMsg(): Deferred<SettingInteractionMsgBean>

    //上传公告消息开关
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/web/message/interaction_setting/")
    fun postSettingInteractionMsg(@Body body: RequestBody?): Deferred<HttpResponse<Any>?>


    //用户设置
    @GET("/api/mobile/user_setting/")
    fun getUserSettingDataAsync(): Deferred<HttpResponse<UserSettingBean>>


    //修改用户设置
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/user_setting/")
    fun postUserSettingDataAsync(@Body body: RequestBody?): Deferred<HttpResponse<UserSettingBean>>

    @GET("/api/mobile/version/log/")
    suspend fun getUpdateLogs(@Query("offset") offset:Int,@Query("limit") limit:Int = 10) : HttpResponse<List<UpdateLogItem>>

    @GET("/api/mobile/problem/user/openid/")
    suspend fun getSet(@Query("user_id") i:String) : HttpResponse<TestAccountBean>

}