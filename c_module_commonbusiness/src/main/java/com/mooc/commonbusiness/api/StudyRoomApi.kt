package com.mooc.commonbusiness.api

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.home.PublicFolder
import com.mooc.commonbusiness.model.home.UserStudyResourceBean
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.model.studyroom.PublicStudyListResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StudyRoomApi {

    //获取学习室资源
    @GET("/api/resources/folder/")
    fun getUserStudyData(@Query("type") type: String?="",@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<UserStudyResourceBean>

    //删除整个folder
    @DELETE("/api/resources/folder/{folderId}/")
    fun delFolder(@Path("folderId") folderId: String?): Deferred<HttpResponse<Any>>

    /**
     * 获取子目录文件 (点击学习室子目录)
     * @param type
     * 如果type = folder 返回文件目录列表
     * 如果type = "" 返回这个列表中的所有资源
     * 如果type = "resourceType" 则查询具体资源类型数据
     *
     * 3.5.5支持分页
     */
    @GET("api/resources/folder/{id}/")
    fun getFolderContentWithId(@Path("id") id: String?, @Query("type") type: String?,@Query("offset") offset: Int, @Query("limit") limit: Int): Deferred<UserStudyResourceBean>

    //学习室课程资源数据
    @GET("/api/mobile/course/user/enroll/list/")
    fun getStudyRoomCourse( @Query("offset") offset: Int, @Query("limit") limit: Int,@Query("type") type: String = "2"): Deferred<HttpResponse<List<CourseBean>>>


    /**
     * 获取公开的学习清单分类列表
     * @param folderId 清单id
     * @param type 资源类型
     * @param mId  # 获取自己公开的学习清单详情时传参  传自己的ID     其他时候不传
     * @param userId  # 获取他人公开的学习清单详情时传参 传他人的ID。 其他时候不传
     */
    @GET("api/resources/collect_folder/detail/")
    fun getPublicResourceList(@Query("folder_id") folderId:String,@Query("type") type:String,@Query("user_id") mId:String="",@Query("other_user_id") userId:String="",@Query("offset") offset: Int, @Query("limit") limit: Int) : Deferred<HttpResponse<PublicFolder>>

    /**
     * 获取运营推荐的公开的资源列表
     */
    @GET("api/resources/collect_folder/detail/")
    fun getRecommendPublicResourceList(@Query("folder_id") folderId:String,@Query("type") type:String,@Query("from") from: String = "",@Query("task_id") task_id: String = "",@Query("is_admin") mId: String = "1",@Query("offset") offset: Int, @Query("limit") limit: Int) : Deferred<HttpResponse<PublicFolder>>

    /**
     * 获取公开学习清单弹出确认消息
     * @param folderId 学习清单id
     */
    @GET("api/resources/folder/show/info/{folder_id}/")
    fun getPublicMessage(@Path("folder_id") folderId: String) : Deferred<HttpResponse<PublicStudyListResponse>>

    /**
     * 确定公开学习清单
     * @param folderId 学习清单id
     */
    @GET("api/resources/folder/show/{folder_id}/")
    fun publicStudyList(@Path("folder_id") folderId: String) : Deferred<HttpResponse<Any>>

    /**
     * 取消公开学习清单
     */
    @GET("api/resources/folder/hide/{folder_id}/")
    fun canclePublicStudyList(@Path("folder_id") folderId: String) : Deferred<HttpResponse<Any>>
}