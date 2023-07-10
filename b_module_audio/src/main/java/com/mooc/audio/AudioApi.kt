package com.mooc.audio

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.model.studyproject.MusicBean
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AudioApi {

    /**
     * @param sort 默认传 "" 通过接口返回上次的排序
     */
    @GET("api/web/ximalaya/album_detail/{albumId}/")
    fun getAlbumDetail(@Path("albumId") albumId: String, @Query("sort") sort: String = "", @Query("page_size") page_size: Int = 10): Deferred<HttpResponse<AlbumListResponse>>


    /**
     * @param sort 默认传 "" 通过接口返回上次的排序
     * @param trackid 新增trackid,方便查询所在专辑的位置
     */
    @GET("api/web/ximalaya/album_detail/{albumId}/")
    fun getAlbumDetailNew(@Path("albumId") albumId: String, @Query("sort") sort: String = "", @Query("track_id") trackId: String = "",  @Query("page_size") page_size: Int = 10): Deferred<HttpResponse<AlbumListResponse>>
    /**
     * 获取音频课列表
     * @param sort 1正序 0倒序
     */
    @GET("/api/web/ximalaya/album/{albumId}/")
    fun getTrackList(@Path("albumId") albumId: String, @Query("page") page: Int, @Query("sort") sort: String = "", @Query("page_size") page_size: Int = 10): Deferred<AlbumListResponse>

    /**
     * 获取该专辑下所有的音频课
     * @param sort 1正序 0倒序
     */
    @GET("/api/web/ximalaya/album/{albumId}/")
    suspend fun  getAllTrackList(@Path("albumId") albumId: String, @Query("page") page: Int, @Query("sort") sort: String = "", @Query("page_size") page_size: Int = 100,@Query("bd") bd:String = "zjxq"): AlbumListResponse

    //获取音频信息
    @GET("/api/web/ximalaya/track/{trackId}/")
    fun getTrackInfo(@Path("trackId") trackId: String): Deferred<TrackBean>

    //获取音频信息,第二种方式
    @GET("/api/web/ximalaya/track/{trackId}/")
    fun getTrackInfo2(@Path("trackId") trackId: String): Observable<TrackBean>

    /**
     * 获取自建音频信息
     */
    @GET("/api/mobile/audio/click/detail/{id}/")
    fun getOwnBuildTrackInfo(@Path("id") id: String): Deferred<MusicBean>


    /**
     * 新域名音频打点("http://log.learning.mil.cn/heartbeat")
     *
     */
    @GET("http://log.learning.mil.cn/heartbeat")
    fun audioPlayHeat(@QueryMap map: HashMap<String, String>): Call<ResponseBody>


    /**
     * 旧域名音频打点("http://dd.jz.xuetangx.com/heartbeat")
     */
    @GET("http://dd.jz.xuetangx.com/heartbeat")
    fun audioPlayHeatOld(@QueryMap map: HashMap<String, String>): Call<ResponseBody>

    /**
     * 上传音频播放位置，下次进入直接续播
     * @body
     *  track_id
     *  duration
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/web/ximalaya/audio_history/")
    fun postAudioHistory(@Body body: RequestBody): Observable<HttpResponse<Any>>

    //音频屏蔽接口(音频播放页）
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/web/ximalaya/shield/")
    fun postResourceShield(@Body body: RequestBody): Observable<HttpResponse<Any>>
}