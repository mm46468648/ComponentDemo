package com.mooc.commonbusiness.net

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mooc.common.utils.DebugUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection

object HttpUtil {

    var BASE_URL = ApiService.NORMAL_BASE_URL
    var mRetrofit: Retrofit? = null
    var noVerifyRetrofit: Retrofit? = null      //不校验https证书的


    init {
        _init()
    }

    private fun _init() {
        BASE_URL = if (DebugUtil.isTestServeUrl) ApiService.TEST_BASE_IP_URL else ApiService.NORMAL_BASE_URL //37
        //检查域名是否可以访问通
        GlobalScope.launch {
            val getTimeUrl = ApiService.NORMAL_BASE_URL + "/api/mobile/studyplan/checkin/timestamp/"
            val request: Request = Request.Builder().url(getTimeUrl).build()
            val okHttpClient = OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS).build()

            var needChangeHost = false
            try {
                val execute = okHttpClient.newCall(request).execute()
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    needChangeHost = true
                }
            } finally {
                initRetrofit(needChangeHost)
            }
        }
    }


    private fun initRetrofit(needChangeHost: Boolean) {


        if (needChangeHost) {
            BASE_URL = ApiService.OLD_BASE_URL
        }

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        val requestEncryptInterceptor = RequestEncryptInterceptor()
        val sslSocketFactoryEx = SSLSocketFactoryEx()

        val okHttpClient: OkHttpClient =
                if (DebugUtil.isNoVerifyCertificate) {
                    OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)
                            .addInterceptor(requestEncryptInterceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(22, TimeUnit.SECONDS)
                            .readTimeout(33, TimeUnit.SECONDS)
                            .build()
                } else {
                    OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)
                            .addInterceptor(requestEncryptInterceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(22, TimeUnit.SECONDS)
                            .readTimeout(33, TimeUnit.SECONDS)
                            .sslSocketFactory(sslSocketFactoryEx.sslSocketFactoryForOneWay, sslSocketFactoryEx.trustManager)
                            .hostnameVerifier(HostnameVerifier { host, session ->
                                if (host.contains("www.learning.mil.cn")) {
                                    true
                                } else {
                                    val hv = HttpsURLConnection.getDefaultHostnameVerifier()
                                    hv.verify(host, session)
                                }
                            })
                            .build()
                }

        //初始化Retrofit

        mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .build()

        val normalOkHttpClient: OkHttpClient = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(22, TimeUnit.SECONDS)
                .readTimeout(33, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(requestEncryptInterceptor)
                .retryOnConnectionFailure(true)
                .build()


        //学堂接口不需要验证证书，所以单独配置
        noVerifyRetrofit = Retrofit.Builder()
                .baseUrl(ApiService.XT_ROOT_URL)
                .client(normalOkHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .build()
    }


}