package com.mooc.setting

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.excutor.DispatcherExecutor
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toastMain
import com.mooc.common.utils.PackageUtils
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.ChannelConstants
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.net.SSLSocketFactoryCompat
import com.mooc.commonbusiness.net.SSLSocketFactoryEx
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.CerUtil
import com.mooc.common.utils.DebugUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection


class UpdateManager {

    companion object : LifecycleObserver {

        lateinit var launchJob: Job
        var ownerDestory: Boolean = false
        const val PARAMS_FROMHOME = "params_fromhome"
        const val PARAMS_UPDATEBEAN = "params_updatebean"

        //        val download_Apk_Dir = AppGlobals.getApplication()?.getExternalFilesDir("apk")
//        val download_Apk_Dir = DownloadConfig.apkLocation
        private const val bufferSize = 8192
        const val PROGRESS_FAIL = -1
        const val PROGRESS_FAIL_SSL = -2

        //有新版本，但是点击了忽略，要在我的版本更新中提示
        var hasUpdateButIgnor = false

        var isUpdatePageShow = false //更新页面是否正在显示

        /**
         * 检测是否有更新
         * @param fromHome 是否是主页发起的更新检测,默认是
         */
        fun checkApkUpdate(lifecycleOwner: LifecycleOwner, fromHome: Boolean = true) {

            if (isUpdatePageShow) {
                loge("升级页面正在显示。。。")
                return
            }
            lifecycleOwner.lifecycle.addObserver(this)
            launchJob = GlobalScope.launch {
                try {
                    val host = if (DebugUtil.isTestServeUrl) {
                        ApiService.TEST_BASE_IP_URL
                    } else {
                        if (ApiService.getUserNewUrl()) {
                            ApiService.NORMAL_BASE_URL
                        } else {
                            ApiService.OLD_BASE_URL
                        }
                    }
                    val upgradeData = ApiService.noVerifyRetrofit.create(SetApi::class.java)
                            .getUpgradeAsync("${host}/api/mobile/version/", ChannelConstants.channelName).await()
                    //判断是否点击过忽略此版本更新
                    val ignoreVersion = SpDefaultUtils.getInstance().getString(SpConstants.IGNORE_UPDATE_APK_VERSION, "")

                    if (upgradeData.version_code > PackageUtils.getVersionCode() && upgradeData.app_url.isNotEmpty()) {

                        hasUpdateButIgnor = true
                        if (fromHome && upgradeData.version_code.toString() == ignoreVersion) {
                            return@launch
                        }
                        //版本号大于目前版本，并且地址不为空，则进入更新提醒页面
                        if (!isActive) return@launch
                        ARouter.getInstance().build(Paths.PAGE_APK_UPDATE)
                                .with(Bundle().put(PARAMS_UPDATEBEAN, upgradeData).put(PARAMS_FROMHOME, fromHome.toString())).navigation()
                    } else {
                        if (!fromHome) {  //当前已经是最新，并且是在设置页点击的版本更新提示
                            toastMain("已经是最新版本")
                        }
                    }
                } catch (e: Exception) {

                }

            }
        }


        /**
         * 下载apk
         */
        fun downloadApk(versionCode: String, downloadUrl: String, downloadListener: (progress: Int) -> Unit) {


            val fileDir = File(DownloadConfig.apkLocation)
            if (!fileDir.exists()) { //文件夹不存在先创建
                fileDir.mkdirs()
            }
            //创建apk文件
            val downloadFileName = "app_moo_${versionCode}.apk"
            val downloadFile = File(fileDir, downloadFileName)
            val okHttpClient: OkHttpClient
            //是否校验证书 true 不校验证书，false 校验证书
            if (CerUtil.getInstance().isOrNoVerifyCertificate) {//不校验证书
                okHttpClient = OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .sslSocketFactory(SSLSocketFactoryCompat(SSLSocketFactoryCompat.TrustManager()), SSLSocketFactoryCompat.TrustManager())
                        .hostnameVerifier(HostnameVerifier { host, session -> true })
                        .build()
            } else {//校验证书
                //https ssl证书校验
                val sslSocketFactoryEx = SSLSocketFactoryEx()
                okHttpClient = OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .sslSocketFactory(sslSocketFactoryEx.sslSocketFactoryForOneWay, sslSocketFactoryEx.trustManager)//证书校验
                        .hostnameVerifier { host, session ->
                            if (host.contains("www.learning.mil.cn")
                                    || host.contains("static.learning.mil.cn")) {//如果是自己的下载地址通过
                                true
                            } else {
                                val hv = HttpsURLConnection.getDefaultHostnameVerifier()
                                hv.verify(host, session)
                            }
                        }
                        .build()

            }

            val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(ApiService.BASE_URL)
                    .client(okHttpClient)
                    .callbackExecutor(DispatcherExecutor.getIOExecutor())
                    .build()
            //下载方法
            retrofit.create(SetApi::class.java).download(downloadUrl).enqueue(object : Callback<ResponseBody?> {
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    if (t.toString().contains("SSLHandshakeException")) {
                        downloadListener.invoke(PROGRESS_FAIL_SSL)
                    } else {
                        downloadListener.invoke(PROGRESS_FAIL)
                    }
                }

                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    writeResponseToDisk(downloadFile, response, downloadListener)
                }
            })
        }

        /**
         * 将流写入文体
         */
        private fun writeResponseToDisk(file: File, response: Response<ResponseBody?>, downloadListener: (progress: Int) -> Unit) {
            val inputStream = response.body()?.byteStream()
            val totalLength = response.body()?.contentLength() ?: 0
            //从response获取输入流以及总大小
            if (response.body() != null) {
                var os: OutputStream? = null
                var currentLength: Long = 0
                try {
                    os = BufferedOutputStream(FileOutputStream(file))
                    val data = ByteArray(bufferSize)
                    var len: Int
                    while (inputStream?.read(data, 0, bufferSize).also { len = it!! } != -1) {
                        os.write(data, 0, len)
                        currentLength += len.toLong()
                        //计算当前下载进度
                        downloadListener.invoke((100 * currentLength / totalLength).toInt())
                    }
                    //下载完成，并返回保存的文件路径
                } catch (e: IOException) {
                    e.printStackTrace()
                    //如果下载错误传-1进度
                    downloadListener.invoke(PROGRESS_FAIL)
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    try {
                        os?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onOwnerDestory() {
            ownerDestory = true
            launchJob.cancel()
        }
    }

}
