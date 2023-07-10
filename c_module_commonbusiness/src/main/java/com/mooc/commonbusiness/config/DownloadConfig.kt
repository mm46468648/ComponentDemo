package com.mooc.commonbusiness.config

import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.model.set.PathStoreBean
import com.mooc.commonbusiness.utils.sp.SPUserUtils
import com.google.gson.Gson
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import java.io.File

/**
 * 下载配置
 */
object DownloadConfig {

    //默认存储位置 （默认是外部存储卡私有file目录，这个目录不需要申请权限）

    var defaultLocation = AppGlobals.getApplication()?.getExternalFilesDir(null)?.absolutePath?:""

    //不同下载内容存储位置
    var audioLocation = "${defaultLocation}${File.separator}audio"
    var courseLocation = "${defaultLocation}${File.separator}course"
    var imageLocation = "${defaultLocation}${File.separator}image"
    var apkLocation = "${defaultLocation}${File.separator}apk"
    var h5DownloadLocation = "${defaultLocation}${File.separator}h5Download"
    var errorLogLoccation = "${defaultLocation}${File.separator}error"
    var certificateLoccation = "${defaultLocation}${File.separator}certificate" //下载证书

    //下载类型
    var TYPE_COURSE = "C"
    var TYPE_AUDIO = "A"

    val DOWNLOAD_DIR_NAME = "moocnd" //meidaStore document 文件夹名字

    init {
        //初始化默认下载地址
        defaultLocation = AppGlobals.getApplication()?.getExternalFilesDir(null)?.absolutePath?:""
        val defaultJson = SpDefaultUtils.getInstance().getString(SpConstants.DEFAULT_DOWNLOAD_LOCATION_JSON, "")
        val gson = Gson()
        val pathStoreBean = gson.fromJson(defaultJson, PathStoreBean::class.java)
        if (pathStoreBean?.storageBeans != null && pathStoreBean.storageBeans.size > 1) {
            val patshStore = pathStoreBean.storageBeans[1]
            if(patshStore.checked){
                //如果用户设置过自定义的路径，重新赋值
                defaultLocation  = patshStore.path
            }
        }

        resetDefault(defaultLocation)
    }

    fun resetDefault(path:String){
        defaultLocation = path
        loge("修改存储地址: ${defaultLocation}")

        audioLocation = "${defaultLocation}${File.separator}audio"
        courseLocation = "${defaultLocation}${File.separator}course"
        imageLocation = "${defaultLocation}${File.separator}image"
        apkLocation = "${defaultLocation}${File.separator}apk"
        h5DownloadLocation = "${defaultLocation}${File.separator}h5Download"
        errorLogLoccation = "${defaultLocation}${File.separator}error"
        certificateLoccation = "${defaultLocation}${File.separator}certificate" //下载证书
    }
}