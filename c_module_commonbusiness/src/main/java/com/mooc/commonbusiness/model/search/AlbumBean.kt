package com.mooc.commonbusiness.model.search

import android.annotation.SuppressLint
import android.os.Parcelable
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import kotlinx.android.parcel.Parcelize

/**

 * @Author limeng
 * @Date 2020/8/10-4:32 PM
 */

@SuppressLint("ParcelCreator")
@Parcelize
class AlbumBean: Parcelable, StudyResourceEditable,BaseResourceInterface {

    var id: String = ""
    var album_title: String = ""
    var source: String = ""
    var author: String = ""
    var picture: String = ""
    var playCount: Long = 0
    var includeTrackCount: Int = 0

    var cover_url_small: String = ""
    var cover_url_large: String = ""

    //喜马拉雅中的属性
    var coverUrlMiddle: String = ""
    var cover_url_middle: String = ""
    var short_intro: String = ""
    var albumTitle: String = ""
    var include_track_count :Long = 0
    var track_count :Long = 0
    var play_count: Long = 0

    //记录上次播放音频id
    var lastPlayAudioId :String=  ""
    var haveDownloadTrack :Boolean = false
    val resource_status: Int = 0

    override val _resourceId: String
        get() = id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_ALBUM
    override val _resourceStatus: Int
        get() = resource_status
    override val _other: Map<String, String>?
        get() = null


    override var resourceId: String
        get() = id
        set(value) {}
    override var sourceType: String
        get() = ResourceTypeConstans.TYPE_ALBUM.toString()
        set(value) {}


}