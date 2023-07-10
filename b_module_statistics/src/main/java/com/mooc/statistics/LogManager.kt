package com.mooc.statistics

import com.mooc.common.global.AppGlobals
import com.mooc.common.utils.FileMgr
import com.mooc.common.utils.FileUtils
import com.mooc.commonbusiness.net.ApiService
import com.mooc.common.utils.ZipUtil
import com.mooc.common.utils.ZipUtilsJ
import com.mooc.commonbusiness.config.DownloadConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * 打点日志管理
 */
object LogManager {


    //logfile path,外部存储卡私有file目录(不需要权限即可访问) Android/data/com.moocxuetang/file/
    const val LOG_FILE_PATH = "logdata"    //存储目录
    const val LOG_CACHE_PATH = "cachelog"    //缓存日志目录（用于上传）
    const val LOG_FILE_NAME = "logdata.txt"   //存储文件名
    const val LOG_ZIP_FILE = "cachelog.zip"    //压缩文件名
    const val LOG_MAX_LENGTH = 30 * 1024 //log文件最大限制，超过就上传
    //将log日志，存储到外部私有file文件
    val cache_Log_Dir = AppGlobals.getApplication()?.getExternalFilesDir(LOG_FILE_PATH)

    /**
     * 上传日志文件
     */
    fun uploadLogFile(file: File) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val toRequestBody = file.asRequestBody("image/*; charset=utf-8".toMediaTypeOrNull())
        builder.addFormDataPart("file", file.name, toRequestBody)

        GlobalScope.launch {
            try {
                val response = ApiService.getRetrofit()
                        .create(UploadApi::class.java)
                        .postZipFile(builder.build()).await()

                if (response.isSuccess) {
                    //上传成功原打点文件
                    deleteLogFile()
                }
            } catch (e: Exception) {

            }
        }
    }

    /**
     * 检测打点文件大小
     */
    fun checkFileSize() {
        val file = File(cache_Log_Dir, LOG_FILE_NAME)
        val length = file.length()
//        loge("文件大小", length)

        if (length > LOG_MAX_LENGTH) {
            uploadFile()
        }
    }

    fun uploadFile(){
        val file = File(cache_Log_Dir, LOG_FILE_NAME)
        if(file.exists() && file.length() > 0){
            GlobalScope.launch {
                try {
                    //将日志文件，拷贝到cacaelog/xxxxx.log      xxxx是时间戳，再压缩上传
                    val copyFileDir = File(cache_Log_Dir, LOG_CACHE_PATH)
                    if (!copyFileDir.exists()) {
                        copyFileDir.mkdirs()
                    }

                    val copyToFile = File(copyFileDir, "${System.currentTimeMillis()}.log")
                    FileUtils.copyFile(file, copyToFile, false)

//                val zipFile = FileMgr.newFile(cache_Log_Dir?.absolutePath,LOG_ZIP_FILE)
                    val uploadFile = File(cache_Log_Dir, LOG_ZIP_FILE)

//                ZipUtil.ZipFolder(copyFileDir.absolutePath, "${copyFileDir.parent}.zip")
                    ZipUtilsJ.zipFile(copyFileDir.absolutePath, uploadFile)

//                val uploadFile = File(cache_Log_Dir,LOG_ZIP_FILE)
                    if (uploadFile.exists()) {
                        uploadLogFile(uploadFile)
                    }
                } catch (e: Exception) {

                }
            }
        }

    }

    /**
     * 删除打点文件
     * 还有copy文件
     */
    fun deleteLogFile() {
        FileUtils.deleteByParentPath(cache_Log_Dir)
    }


}