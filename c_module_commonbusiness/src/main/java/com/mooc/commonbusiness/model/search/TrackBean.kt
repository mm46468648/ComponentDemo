package com.mooc.commonbusiness.model.search

import android.os.Parcelable
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import com.mooc.commonbusiness.model.audio.BaseAudioModle
import com.mooc.commonbusiness.model.db.TrackDB
import com.mooc.commonbusiness.utils.HashUtil
import kotlinx.android.parcel.Parcelize

/**

 * @Author limeng
 * @Date 2020/8/20-2:20 PM
 */
@Parcelize
data class TrackBean(
//    var  announcer:String?=null,

    var nickname: String = "",
    var playCount: Long = 0,
    var cover_url_small: String = "",
    var album: AlbumBean? = null,
    var subordinated_album: AlbumBean? = null,       //喜马拉雅用的字段
    var track_title: String = "",
    var play_count: Long = 0,
    var cover_url_middle: String = "",
    var liveRoomId: Long = 0, //上一次视频播放时常 (单位s)
    var duration: Long = 0, //视频总时常 (单位s)
    var play_url_32: String = "", //音频播放地址
    var position: Int = 0,     //当前音频在音频课列表中的位置，用户浮窗跳转回音频播放页面
    //用于同步Adapter中的状态字段
    var downloadSelect: Boolean = false,
    //喜马拉雅中的属性
    var coverUrlMiddle: String = "",
    var dataId: Long = 0,
    var orderNum: Long = 0,
    var downloadUrl: String = "",
    var enrolled: Boolean = false, //是否已加入学习室
    val resource_status: Int = 0,
    var asyncFromXimaData : Boolean = false, //是否是从喜马拉雅同步过来的数据
    var has_permission:String = ""   //"1"的时候拥有屏蔽资源的权限,在弹窗中显示屏蔽按钮

) : Parcelable, StudyResourceEditable, BaseResourceInterface, BaseAudioModle {

    override var id: String = ""
    override var albumId: String = subordinated_album?.id ?: ""
    override val playUrl: String
        get() = play_url_32
    override val coverUrl: String
        get() = cover_url_middle
    override val lastDuration: Long
        get() = liveRoomId
    override val totalDuration: Long
        get() = duration
    override val resourceType: Int
        get() = ResourceTypeConstans.TYPE_TRACK
    override var albumTitle: String = ""
    override val trackTitle: String
        get() {
            return track_title
        }

    /**
     * 生成下载数据库db存储的id
     */
    fun generateDownloadDBId(): Long {
        return HashUtil.longHash("${DownloadConfig.TYPE_AUDIO}_${albumId}_${id}")
    }

    fun generateTrackDB(): TrackDB {
        val trackDB = TrackDB()
        trackDB.id = id.toLong()
        trackDB.albumId = albumId
        trackDB.data = GsonManager.getInstance().toJson(this)
        return trackDB
    }


    override val _resourceId: String
        get() = id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_TRACK
    override val _resourceStatus: Int
        get() = resource_status

    override val _other: Map<String, String>?
        get(){
            return if(subordinated_album!=null){
                val s = subordinated_album?.id ?: ""
                hashMapOf(IntentParamsConstants.ALBUM_PARAMS_ID to s)
            }else null
        }


    override val resourceId: String
        get() = id

    override val sourceType: String
        get() = ResourceTypeConstans.TYPE_TRACK.toString()

}

