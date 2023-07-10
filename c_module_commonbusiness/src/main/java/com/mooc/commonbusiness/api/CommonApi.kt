package com.mooc.commonbusiness.api

import com.mooc.commonbusiness.model.*
import com.mooc.commonbusiness.model.external.LinkArticleBean
import com.mooc.commonbusiness.model.external.UserPermissionBean
import com.mooc.commonbusiness.model.folder.FolderBean
import com.mooc.commonbusiness.model.my.*
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.model.studyproject.HttpBean
import com.mooc.commonbusiness.model.studyproject.JoinStudyState
import com.mooc.commonbusiness.model.studyroom.FolderResourceDelBean
import com.mooc.commonbusiness.model.privacy.PrivacyPolicyCheckBean
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * 公共api
 */
interface CommonApi {

    //获取举报列表(举报弹窗）
    @GET("/api/mobile/report/get_report_choices/")
    fun getReportData(): Deferred<ReportChoicesBean>


    //获取学习项目动态举报列表(举报弹窗）
    @GET("api/mobile/studyplan/report/choices/")
    fun getDynamicReportData(): Deferred<ReportChoicesBean>

    //资源分享详细信息
    @GET("/api/mobile/resource/share/detail")
    fun getShareDetailData(@Query("resource_type") resourceType: String, @Query("resource_id") resourceId: String): Deferred<HttpResponse<ShareDetailModel>>

    /**
     * 获取分享详情
     * 分享地址包含拉新url
     * @param source_type
     *   (6,  u'分享资源邀请类型'),          支持的资源包括 电子书，自建音频，期刊，刊物，音频，单条，课程，问卷，测试卷，文章，专栏
    (7,  u'分享学习项目邀请类型'),
    (2,  u'分享App邀请类型'),
    (3,  u'分享勋章邀请类型'),
    (4,  u'分享个人数据邀请类型')
     */
    @GET("/api/mobile/resource/share/detail/new/")
    suspend fun getShareDetailDataNew(
            @Query("source_type") source_type: String,
            @Query("resource_type") resource_type: String,
            @Query("resource_id") resource_id: String
    ): HttpResponse<ShareDetailModel>


    /**
     * 资源获取加入学习空间状态
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/web/resource_enroll/")
    fun getResourceEnroll(@Body body: RequestBody): Deferred<HttpResponse<ShareDetailModel>>

    /**
     * 获取资源是否显示，还是显示学习计划提示
     * @param body  "{
     *      "resource_type":"XXX",
     *      "resource_url":"XXX",
     * }"
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/web/resource_show/")
    fun getResourceShow(@Body body: RequestBody): Observable<HttpResponse<JoinStudyState>>

    /**
     * 上传图片(所有上传图片都用这个)
     */
    @POST("/api/mobile/v2/student/upload_error_img/")
    fun postImageFile(@Body body: RequestBody?): Deferred<HttpResponse<UploadFileBean>>

    /**
     * 上传图片(动态上传和评论上传)
     */
    @POST("/api/mobile/v2/student/upload_activity_img/")
    fun postDynamicImageAsync(@Body body: RequestBody?): Deferred<HttpResponse<UploadFileBean>>

    //资源滑动上传记录
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/slide/record/")
    fun postResourceScroll(@Body body: RequestBody?): Deferred<HttpBean?>

    /**
     * 获取CMS配置分享邀请信息，二维码
     * @param requestBody source_type,source_id
     * 分享app   source_type  2      source_id为0
    分享勋章   source_type  3      source_id为0
    分享个人数据   source_type  4     source_id为0
    分享电子书   source_type  6      source_id为电子书ID
    分享学习计划 source_type  7       source_id为学习计划ID
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/share/get_invitation_letter/")
    suspend fun getCMSShareData(@Body requestBody: RequestBody): HttpResponse<CMSShareBean>

    //获取app分享信息  (首页我的）
    @GET("/api/mobile/app_share/")
    fun getAppShareData(): Deferred<GetAppShareDataBean?>

    //上传用户积分
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/student/user/study/everyday/normal/")
    fun postUserPoint(@Body body: RequestBody?): Deferred<HttpResponse<Any>>

    /**
     * /上传学习记录
     * @param body 格式
     *   ("type", getResTypeWithString(type));
    ("url", url);      url，资源的情况下是是id，链接情况下是链接的地址
    ("title", title);
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/student/statistics/")
    fun postStudyLog(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    //上传举报信息
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/report/post_report/")
    fun postReportData(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    //上传学习疾患动态举报信息
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/activity/report/")
    fun postDynamicReportData(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    //获取阅读增加积分
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/score_first/")
    fun postFirstStudyScoreRecord(@Body body: RequestBody): Deferred<ScoreRecordDataBean>

    //获取全局配置灰度
    @GET("/api/mobile/terms/globalConfig/")
    fun getPlatformGray(): Deferred<HttpResponse<GlobalConfigResponse>>

    //分享学友圈
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/school_circle/share/")
    fun postSchoolShare(@Body body: RequestBody?): Observable<ShareScoreResponse>

    //分享获得积分
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/share_new/")
    fun getShareScore(@Body body: RequestBody?): Observable<ShareScoreResponse>

    @GET("/api/resources/folder/new/{id}/tab/")
    fun getStudyRoomFolderMenu(@Path("id") id: String): Deferred<HttpResponse<FolderBean>>


    //更新掌阅进度
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/other_resource/study/")
    fun updateZYReaderProgress(@Body requestBody: RequestBody): Deferred<HttpResponse<Any>>

    //获取文章任务验证码
    @GET("/api/service/verify_code/")
    fun getArticleVrifyCode(@Query("source_id") id: String): Deferred<HttpResponse<VerifyCode>>


    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/task_system/finish/article/")
    fun postVerifyCode(@Body body: RequestBody): Deferred<HttpResponse<Boolean>>

    /**
     * 隐私政策弹窗查询
     */
    @GET("/api/cms/student/privacy_policy/check/")
    fun getPrivacyPolicyCheckAsync(@Query("version") version: String): Deferred<HttpResponse<PrivacyPolicyCheckBean>>

    /**
     * 更新已读
     */
    @POST("/api/web/recommend/read/{id}/")
    fun updateRead(@Path("id") id: String?): Observable<HttpResponse<Any>>

    //关注
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/school_circle/follow/user/")
    fun postFollowUser(@Body body: RequestBody): Deferred<ParserStatusBean>

    //获取学习清单删除的资源
    @GET("/api/resources/folder/resource/invalid/")
    fun getFolderResourceDelAsync(@Query("folder_id") folderId: String = ""): Deferred<HttpResponse<ArrayList<FolderResourceDelBean>>>

    /***
     * 上传正在运行的无障碍服务包名
     * @param body {
     * "app_info": ["test1", "test2"]
     * }
     * @return
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/app/info/")
    fun postRuningAcccessibility(@Body body: RequestBody?): Observable<HttpResponse<Any>>

    /**
     * 隐私政策弹窗查询
     */
    @GET("/api/mobile/kefu/link/")
    fun getWebUrl(): Deferred<HttpResponse<WebUrlBean>>

    //上传外链文章
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/web/article/")
    fun uploadArticelLink(@Body body: RequestBody): Deferred<HttpResponse<LinkArticleBean>>

    //获取用户权限
    @GET("/api/mobile/student/permissions/")
    fun getUserPermission(): Deferred<UserPermissionBean>
}