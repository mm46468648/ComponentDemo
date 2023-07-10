package com.mooc.setting.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 安装包升级数据
 */
@Parcelize
data class ApkUpgradeData(
        var app_url: String = "",   //安装包地址
        var apk_md5: String = "",
        var app_name: String = "",
        var app_force_upgrade : Boolean = false, //是否强制更新
        var app_presentation: String = "", //更新说明
        var version_name: String = "",
        var version_code: Int = -1,
        var app_size: String = ""   //安装包大小
) : Parcelable {
    /**
     * app_url : http://oopo3e0ye.bkt.clouddn.com/app-release.apk
     * apk_md5 : 69bf8573b69d31deb9c70c8a9e0bc692
     * app_name : 梦课学堂
     * app_force_upgrade : true
     * app_presentation : 1.支持题型：选择题（单选和多选）、填空题、判断题 <br></br><br></br>   2.网页、移动端同步完成习题  <br></br><br></br> 3. 已完成习题，一件保存提交 <br></br><br></br>   4.  新增认证课程专属标识，答题时拍照认证功能<br></br>
     * version_name : 1.0
     * version_code : 88
     * app_size : 11.2
     */
    
}