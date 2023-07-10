package com.mooc.audio.ui.pop

import android.content.Context
import android.view.View
import com.mooc.audio.R
import com.mooc.commonbusiness.pop.CommonMenuPopupW
import java.util.ArrayList

/**
 * 音频设置弹窗
 * @param isOwnBuildAudio 是否是自建音频的弹窗
 */
class AudioSettingNewPop(context: Context, setItems: ArrayList<String>, parent: View, var isOwnBuildAudio : Boolean = false)
    : CommonMenuPopupW(context,setItems,parent){

    override val imgResMap = hashMapOf<String, Int>(
        TYPE_SHARE_STR to R.mipmap.common_ic_menu_share,
        TYPE_SHIELD_STR to R.mipmap.common_ic_menu_shield,
        TYPE_CUTDOWN_TIME_STR to R.mipmap.audio_ic_menu_timing,
        TYPE_DOWNLOAD_STR to R.mipmap.audio_ic_menu_download,
        TYPE_SPEED_STR to R.mipmap.audio_ic_menu_speed,
        TYPE_REPORT_STR to R.mipmap.audio_ic_menu_report)

}