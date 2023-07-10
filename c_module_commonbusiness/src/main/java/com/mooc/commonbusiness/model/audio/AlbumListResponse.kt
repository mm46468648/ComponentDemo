package com.mooc.commonbusiness.model.audio

import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.model.db.AlbumDB
import com.mooc.commonbusiness.model.search.TrackBean

class AlbumListResponse{
    val tracks: ArrayList<TrackBean>? = null
    var tracks_natural_ordered: Boolean = false
    var album_id: Int = 0
    val free_track_count: Int = 0
    var cover_url_large: String = ""
    val updated_at: Long = 0
    val can_download: Boolean = false
    val play_count: Long = 0
    val is_finished: Int = 0
    val cover_url_middle: String = ""
    val total_page: Int = 0
    var album_title: String = ""
    var id: String = ""
    val favorite_count: Int = 0
    val subscribe_count: Int = 0
    val has_sample: Boolean = false
    val total_count: Int = 0
    val is_paid: Boolean = false
    val based_relative_album_id: Int = 0
    val album_intro: String = ""
    val page_size: Int = 0
    val page: Int = 0
    val cover_url_small: String = ""
    val composed_price_type: Int = 0
    val soundLastListenId: Int = 0
    val album_tags: String = ""
    val share_count: String = ""
    val category_id: Int = 0
    val created_at: Long = 0
    val estimated_track_count: Int = 0
    val include_track_count: Int = 0
    val has_permission: String = ""
    var latest_page: Int = 1   //应该从哪个page开始加载
    var latest_track_id: String = "0"   //上次在听的音频id ， 默认是0
    var params :ParamsBean? = null
    val announcer: AnnouncerBean? = null
    var oldDownloadTrackCount = 0      //已下载的音频数量,为了兼容老版本数据库数据
    var oldDownloadTrackSize = 0L      //已下载的音频大小,为了兼容老版本数据库数据


    fun generateDbId():Long{
        return album_id.toLong()
    }
    /**
     * 转换成AlbumDB
     */
    fun converToAlbumDB():AlbumDB{
        val albumBean = AlbumDB()
        albumBean.id = generateDbId()
        albumBean.data = GsonManager.getInstance().toJson(this)
        return albumBean
    }
}

data class ParamsBean(
        var sort:String = "" //排序 1
)
data class AnnouncerBean(
        var nickname: String = ""
)
