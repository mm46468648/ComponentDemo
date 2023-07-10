package com.mooc.studyroom


import com.mooc.commonbusiness.model.HttpListResponse
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.folder.FolderBean
import com.mooc.commonbusiness.model.home.NoteBean
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.model.studyroom.*
import com.mooc.studyroom.model.*
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*

interface StudyRoomApi {

    //获取学习室根目录文件 (加入学习室弹窗）
    @GET("/api/resources/folder/")
    fun getResourceRootFolder(@Query("type") type: String): Deferred<ResourceFolder>

    //获取学习室子目录文件  （学习室点击子目录)
    /**
     * @param type
     * 如果type = folder 返回文件目录列表
     * 如果type = "" 返回这个列表中的所有资源
     */
    @GET("/api/resources/folder/{id}/")
    fun getResourceChileFolder(@Path("id") id: String, @Query("type") type: String): Deferred<ResourceFolder>

    //获取新的学习清单详情
    /**
     * @param type
     * 如果type = folder 返回文件目录列表
     * 如果type = "" 返回这个列表中的所有资源
     */
    @GET("/api/resources/folder/new/{id}/")
    fun getNewResourceChileFolder(@Path("id") id: String, @Query("type") type: String): Deferred<HttpResponse<ResourceFolder>>

    //创建文件夹
    /**
     * @body
     * requestData.put("name", folderName);   文件夹名称
     * requestData.put("pid", pid);     父文件夹id
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/resources/folder/")
    fun createFolder(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    //删除文件夹、学习室中资源(除了课程,和百科资源)
    @DELETE("/api/web/other_resource/other_resource_enroll/{resId}/{type}/")
    fun deleteResFromFolder(@Path("resId") resId: String, @Path("type") tpye: String): Deferred<HttpResponse<Any>>


    //删除文件夹、学习室课程资源
    @DELETE("/api/web/student/enrollment/course/{courseId}/")
    fun deleteCourseFromFolder(@Path("courseId") courseId: String): Deferred<HttpResponse<Any>>

//    //删除文件夹，学习室百科资源
//    @Headers("Content-Type: application/json", "Accept: application/json")
//    @POST("/api/web/other_resource/other_resource_enroll_delete_by_url/")
//    fun deleteBaikeFromFolder(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    //删除文件夹、学习室新学堂课程资源，要传入班级id
    @DELETE("/api/web/student/enrollment/course/{courseId}/")
    fun delNewCourseFromFolder(@Path("courseId") courseId: String?, @Query("cid") classroom_id: String): Deferred<HttpResponse<Any>>

    /**
     * 上传学习清单文件夹新排序
     * @param 学习清单列表的id集合json
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/resources/folder/order/")
    fun postFolderNewSort(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    /**
     * 重命名文件夹
     */
    @FormUrlEncoded
    @PUT("/api/resources/folder/{folderId}/")
    fun reNameFolder(@Path("folderId") folderId: String, @Field("name") name: String): Deferred<HttpResponse<Any>>

    //删除整个folder
    @DELETE("/api/resources/folder/{folderId}/")
    fun delFolder(@Path("folderId") folderId: String): Deferred<HttpResponse<Any>>

//    /**
//     *移动文件夹到文件夹（文件夹移动）
//     */
//    @Headers("Content-Type: application/json", "Accept: application/json")
//    @POST("/api/resources/folder/archive/")
//    fun moveFolderToFolder(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    /**
     * 添加资源到文件夹下 (未添加过学习室的资源添加到学习室)
     * 跟目录传"0"
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/resources/resource/{folderId}/add/")
    fun addResToFolder(@Path("folderId") folderId: String, @Body body: RequestBody?): Deferred<AddFloderResultBean>

    //移动资源到文件夹下 (已经在学习室里的资源 ，进行资源移动) 根目录传 "0"
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/resources/folder/{newFolder}/add/")
    fun moveResToFolder(@Path("newFolder") folderId: String, @Body body: RequestBody): Deferred<HttpResponse<Any>>

    //获取学习记录
    @GET("/api/mobile/student_learn_detail/")
    fun getStudyLearnDetail(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<HttpListResponse<List<StudyRecordBean>>>

    //获取个人学习计划列表
    @GET("/api/mobile/studyplan/userinfo/")
    fun getUserStudyPlanData(): Deferred<HttpResponse<List<StudyProject>>>

    //删除学习记录
    @DELETE("/api/mobile/student_learn_detail/")
    fun deleteStudyRecord(): Deferred<HttpResponse<Any>>


    //获取积分规则(积分规则页面)
    @GET("/api/mobile/user/score/rule/")
    fun getScoreRule(): Observable<ScoreRule>


    /**
     *获取积分明细
     * @param add_time 可以设置从哪个月份开始查，默认""代表最新月份 格式 2020-11
     */
    @GET("/score/detail_v2/")
    fun getStudyScoreDetailList(@Query("offset") offset: Int, @Query("limit") limit: Int, @Query("add_time") add_time: String = ""): Deferred<ScoreDetailList>


    //已完成课程
    @GET("/api/mobile/course/user/enroll/finish/")
    fun getCompleteCourse(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<HttpResponse<List<CourseBean>>>

    //获取学堂证书列表(暂时没有分页，直接1请求到1000)
    @GET("/api/partner/oldxuetangx/honors/")
    suspend fun getCertificateListFromXt(@Query("offset") offset: Int, @Query("limit") limit: Int): HttpResponse<List<HonorDataBean>>

    //获取证书列表从我们的平台(暂时没有分页，直接1请求到1000)
    @GET("/api/mobile/certificate/my/")
    suspend fun getCertificateListFromOwn(@Query("offset") offset: Int, @Query("limit") limit: Int): HttpResponse<List<HonorDataBean>>

    //获取我的勋章
    @GET("/medal_new/")
    fun getMedalData(): Deferred<MedalBean>

    //获取数据看板信息
    @GET("/student/statistics/new/")
    fun getDataBoardData(): Deferred<StudyStatusBean?>

    //获取我的消息
    @GET("/api/web/message/message_index/new/")
    fun getMyMsg(@Query("limit") limit: Int, @Query("offset") offset: Int): Deferred<MsgRespose<MyMsgBean>>

    //获取互动消息列表
    @GET("/api/web/message/interaction_message/")
    fun getInteractionMsg(@Query("limit") limt: Int, @Query("offset") offset: Int): Deferred<MsgRespose<InteractionMsgBean>>


    //获取系统消息列表
    @GET("/api/web/message/system_message/")
    fun getSystemMessage(@Query("limit") limt: Int, @Query("offset") offset: Int): Deferred<MsgRespose<SystemMsgBean>>

    //获取课程公告列表
    @GET("/api/web/message/course_update_message_index/")
    fun getCourseMsg(@Query("limit") limt: Int, @Query("offset") offset: Int): Deferred<MsgRespose<CourseMsgBean>>

    //获取课程消息
    @GET("/api/web/message/course_update_message/")
    fun getCourseDetailMsg(@Query("course_id") course_id: String?,
                           @Query("message_type") message_type: String?,
                           @Query("limit") limit: Int,
                           @Query("offset") offset: Int): Deferred<MsgRespose<CourseMsgDetailBean>>

    //删除互动消息
    @DELETE("/api/web/message/interaction_detail/{id}/")
    fun delInteractionMsg(@Path("id") id: String): Deferred<HttpResponse<Any>>

    //删除课程消息
    @DELETE("/api/web/message/message_index/new/{id}/")
    fun delCourseMsg(@Path("id") id: String): Deferred<HttpResponse<Any>>

    //删除系统消息
    @DELETE("/api/web/message/system_message/{id}/")
    fun delSystemMsg(@Path("id") id: String): Deferred<HttpResponse<Any>>

    //获取笔记信息
    @GET("/other_resource/note/{resId}/")
    fun getNotInfo(@Path("resId") resId: String?): Deferred<NoteBean>

    //删除note
    @DELETE("/other_resource/note/{noteId}/")
    fun delNote(@Path("noteId") noteId: String?): Deferred<HttpResponse<Any>>

    //分享学友圈
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/school_circle/share/")
    fun postSchoolShare(@Body body: RequestBody?): Deferred<ShareSchoolCircleBean?>

//
//    //获取根目录文件 (加入学习室弹窗）
//    @GET("/api/resources/folder/")
//    fun getFolderContent(@Query("type") type: String?): Deferred<StudyFolderContentBean?>
//
//    //创建文件夹
//    @Headers("Content-Type: application/json", "Accept: application/json")
//    @POST("/api/resources/folder/")
//    fun createNewFolder(@Body body: RequestBody?): Deferred<NewFolderBean?>?

    //退出学习计划
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/studyplan/userexit/")
    fun postOutStudyPlan(@Body body: RequestBody?): Deferred<StudyPlanAddBean?>?

    /**
     * 获取贡献任务
     * @param type 1今日任务，2持续任务
     * tip 新接口不需要传递type
     * @return
     */
    @GET("/api/mobile/devote/tasks_list/")
    fun getContributionTask(): Deferred<HttpResponse<List<ContributionTaskBean>>>

    //获取贡献值规则/举报文案
    @GET("/api/mobile/devote/common_data/")
    fun getCommonData(@Query("type") type: String): Deferred<HttpResponse<UnderstandContributionBean>>

    /**
     * 获取邀请好友读书二维码
     * 与分享文案
     * @param ebookIds 书籍id字符串，以"，"拼接
     */
    @GET("/api/mobile/devote/share/ebook_letter/")
    fun getInviteFriendReadBookInfo(@Query("ebook_ids") ebookIds: String): Observable<HttpResponse<com.mooc.studyroom.model.ShareBookCodeModel>>

    /**
     * 获取收藏的学习清单
     */
    @GET("/api/resources/collect_folder/")
    fun getCollectStudyList(@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<HttpResponse<HttpResponse<List<CollectList>>>>

//    /**
//     * 获取公开学习清单弹出确认消息
//     * @param folderId 学习清单id
//     */
//    @GET("/api/resources/folder/show/info/{folder_id}/")
//    fun getPublicMessage(@Path("folder_id") folderId: String): Deferred<HttpResponse<PublicStudyListResponse>>
//
//    /**
//     * 确定公开学习清单
//     * @param folderId 学习清单id
//     */
//    @GET("/api/resources/folder/show/{folder_id}/")
//    fun publicStudyList(@Path("folder_id") folderId: String): Deferred<HttpResponse<Any>>
//
//    /**
//     * 取消公开学习清单
//     */
//    @GET("/api/resources/folder/hide/{folder_id}/")
//    fun canclePublicStudyList(@Path("folder_id") folderId: String): Deferred<HttpResponse<Any>>

    /**
     * 收藏到我的学习清单
     */
    @POST("/api/resources/folder/collect/{folder_id}/")
    fun collectToMyStudyList(@Path("folder_id") folderId: String): Deferred<HttpResponse<Any>>

    /**
     * 点赞学习清单
     * @param body
     * {
    "like":0, // 1 点赞 0 取消点赞
    "folder_id":132
    }
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/resources/folder/like/")
    fun pristStudyList(@Body body: RequestBody): Deferred<HttpResponse<Any>>

    /**
     * 删除收藏清单
     */
    @DELETE("/api/resources/collect_folder/delete/{folder_id}/")
    fun deleteCollectList(@Path("folder_id") folder_id: String): Deferred<HttpResponse<Any>>


    /**
     * 获取公开的学习清单详情
     * @param folderId 清单id
     * @param mId  # 获取自己公开的学习清单详情时传参  传自己的ID     其他时候不传
     * @param userId  # 获取他人公开的学习清单详情时传参 传他人的ID。 其他时候不传
     */
    @GET("/api/resources/collect_folder/detail/")
    fun publicListDetail(@Query("folder_id") folderId: String = "", @Query("user_id") mId: String = "", @Query("other_user_id") userId: String = ""): Deferred<HttpResponse<ResourceFolder>>

    /**
     * 获取运营推荐的公开的学习清单详情
     * @param folderId 清单id
     * @param mId  # 获取自己公开的学习清单详情时传参  传自己的ID     其他时候不传
     * @param userId  # 获取他人公开的学习清单详情时传参 传他人的ID。 其他时候不传
     */
    @GET("/api/resources/collect_folder/detail/")
    fun getRecommendStudyListDetail(@Query("folder_id") folderId: String = "", @Query("is_admin") mId: String = "1"): Deferred<HttpResponse<ResourceFolder>>

    /***
     * 学习清单--清单详情--显示tab
     */
    @GET("/api/resources/collect_folder/detail/tab/")
    fun getFolderTabs(@Query("folder_id") folderId: String,
                      @Query("user_id") userId: String, @Query("other_user_id") other_user_id: String,
                      @Query("share_user_id") share_user_id: String): Deferred<HttpResponse<FolderBean>>;

    /***
     * 学习清单运营推荐的清单详情--显示tab
     */
    @GET("/api/resources/collect_folder/detail/tab/")
    fun getRecommendFolderTabs(@Query("folder_id") folderId: String,
                               @Query("is_admin") mId: String = "1"): Deferred<HttpResponse<FolderBean>>;


    /**
     * 获取好友排行榜数据
     * @param type排行榜类型 0:总 1:day 2:week 3:month
     */
    @GET("/api/mobile/student/friend/rank/")
    suspend fun getFriendScoreRank(@Query("type") type : Int, @Query("offset") offset: Int,@Query("limit") limt: Int) : HttpResponse<FriendScoreRank>
}