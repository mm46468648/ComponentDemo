package com.mooc.home.model

import java.io.Serializable

class GetUpgradeDataBean : Serializable {
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
    var app_url: String? = null
    var apk_md5: String? = null
    var app_name: String? = null
    var isApp_force_upgrade = false
    var app_presentation: String? = null
    var version_name: String? = null
    var version_code: String? = null
    var app_size: String? = null

    override fun toString(): String {
        return "GetUpgradeDataBean{" +
                "app_url='" + app_url + '\'' +
                ", apk_md5='" + apk_md5 + '\'' +
                ", app_name='" + app_name + '\'' +
                ", app_force_upgrade=" + isApp_force_upgrade +
                ", app_presentation='" + app_presentation + '\'' +
                ", version_name='" + version_name + '\'' +
                ", version_code='" + version_code + '\'' +
                ", app_size='" + app_size + '\'' +
                '}'
    }
}