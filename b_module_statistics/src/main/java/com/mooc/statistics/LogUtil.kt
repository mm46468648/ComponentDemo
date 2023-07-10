package com.mooc.statistics

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.net.ApiService
import com.mooc.common.utils.DebugUtil
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.utils.ServerTimeManager
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.statistics.model.LogBean2
import java.io.File

object LogUtil {

    //    var logBean = LogBean()
    var logBean2 = LogBean2()


    @JvmOverloads
    @Synchronized
    fun addClickLogNew(
            page: String = "",
            element: String = "",
            etype: String = "",
            name: String = "",
            to: String = "",
    ) {
        //封装打点信息
        val cloneLog = logBean2.clone()
        cloneLog.page = page
        cloneLog.to = to
        cloneLog.element = element
        cloneLog.event = LogEventConstants2.E_CLICK
        cloneLog.etype = etype
        cloneLog.name = name
        cloneLog.timestamp = System.currentTimeMillis()
        saveLogBean2(cloneLog)
    }

    /**
     * 添加载入页面事件
     * @param page 页面名字
     */
    fun addLoadLog(
            page: String = "",
            element: String = "",
            etype: String = "",
    ) {
        //封装打点信息
        val cloneLog = logBean2.clone()
        cloneLog.page = page
        cloneLog.element = element
        cloneLog.etype = etype
        cloneLog.event = LogEventConstants2.E_LOAD
        cloneLog.timestamp = System.currentTimeMillis()
        saveLogBean2(cloneLog)
    }

    /**
     * 将打点信息，存储到文件
     */
    private fun saveLogBean2(cloneLog: LogBean2) {
        saveToFile(Gson().toJson(cloneLog))

    }

    /**
     * 将打点信息，存储到文件
     * @param toJson logJson
     */
    private fun saveToFile(toJson: String) {
        try {
            if (DebugUtil.debugMode) {
                loge(toJson)
//                testPost(toJson)
                return
            }
            //创建文件夹
            val file = File(DownloadConfig.defaultLocation, LogManager.LOG_FILE_PATH)
            if (!file.exists()) {
                file.mkdirs()
            }
            //将json字符串写入文件
            File(file, LogManager.LOG_FILE_NAME).apply {
                this.appendText(toJson)
                this.appendText("\n")
            }
//            val path = "${LogManager.cache_Log_Dir} + ${File.separator} + ${LogManager.LOG_FILE_NAME}"
//            val fw = FileWriter(File(file,LogManager.LOG_FILE_NAME), true)
//            fw.write(toJson)
//            fw.close()
//
//            Files.write(LogManager.cache_Log_Dir, toJson.toByteArray(), StandardOpenOption.APPEND)

            //检测文件大小
            LogManager.checkFileSize()
        } catch (e: Exception) {
            loge(e.toString())
        }

    }


    /**
     * 测试上传打点
     */
    @SuppressLint("CheckResult")
    fun testPost(json: String) {
        ApiService.getRetrofit().create(UploadApi::class.java)
                .testUploadLog(RequestBodyUtil.fromJsonStr(json))
                .compose(RxUtils.applySchedulers())
                .subscribe({

                }, {

                })
    }


    /**
     * 新版通过接口打点
     */
    fun addClick(page:Int, hrn:String, hrt:Int, hrid:String,hprid:String){
        val map = hashMapOf<String,Any>("page" to page,"hrn" to hrn
        ,"hrt" to hrt,"hrid" to hrid,"hprid" to hprid,"ts" to ServerTimeManager.getInstance().serviceTime)
        postServerLog(GsonManager.getInstance().toJson(map))
    }

    /**
     * 新版通过接口打点
     * @param heid 页面元素id
     */
    fun addClick(page:Int,heid:Int,hprid:String){
        val map = hashMapOf<String,Any>("page" to page,"heid" to heid
           ,"hprid" to hprid,"ts" to ServerTimeManager.getInstance().serviceTime)
        postServerLog(GsonManager.getInstance().toJson(map))
    }

    /**
     * 新版打点直接上传服务器
     */
    @SuppressLint("CheckResult")
    fun postServerLog(json: String) {
        ApiService.getRetrofit().create(UploadApi::class.java)
            .uploadServerLog(RequestBodyUtil.fromJsonStr(json))
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .subscribe({

            }, {

            })
    }
}