package com.mooc.commonbusiness.net;

import android.annotation.SuppressLint;

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory;
import com.mooc.common.utils.DebugUtil;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class ApiService {

    //xtRetrofit实际用于不带https校验的接口请求
    public static Retrofit retrofit, xtRetrofit, noVerifyRetrofit, oldRetrofit;
    public static OkHttpClient okHttpClient;

    public static final String XT_ROOT_URL = "http://v1-www.xuetangx.com";//学堂的域名，基本可以不用了
    public static final String NORMAL_BASE_URL = "https://www.learning.mil.cn";    //正式域名
    public static final String OLD_BASE_URL = "https://moocnd.ykt.io";    //以前的域名（在新域名访问不同的时候使用）
    public static final String TEST_BASE_IP_URL = "http://192.168.9.104:11112";//测试ip 直连
//    public static final String TEST_BASE_IP_URL = "http://192.168.191.244:11112/";//测试ip 长城网
    public static final String BASE_URL = DebugUtil.isTestServeUrl ? TEST_BASE_IP_URL : NORMAL_BASE_URL;     //37
    public static final String SHA1 = "2DD5C636F3FF91902C89E99C24A27A8F52E374F7";


    static volatile boolean userNewUrl = true;       //是否使用新的域名


    public synchronized static void setUserNewUrl(boolean bool) {
        userNewUrl = bool;
    }

    public static boolean getUserNewUrl() {
        return userNewUrl;
    }


    public static Retrofit getRetrofit() {
        if (!userNewUrl) {
            return getOldRetrofit();
        }
        return retrofit;
    }


    static {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        RequestEncryptInterceptor requestEncryptInterceptor = new RequestEncryptInterceptor();
        SSLSocketFactoryEx sslSocketFactoryEx = new SSLSocketFactoryEx();

        if (DebugUtil.isNoVerifyCertificate) {

            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(requestEncryptInterceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(22, TimeUnit.SECONDS)
                    .readTimeout(33, TimeUnit.SECONDS)
                    .sslSocketFactory(new SSLSocketFactoryCompat(new SSLSocketFactoryCompat.TrustManager()), new SSLSocketFactoryCompat.TrustManager())
                    .hostnameVerifier(new HostnameVerifier() {
                        @SuppressLint("BadHostnameVerifier")
                        @Override
                        public boolean verify(String host, SSLSession session) {
                            return true;
                        }
                    })
                    .build();
        } else {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(requestEncryptInterceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(22, TimeUnit.SECONDS)
                    .readTimeout(33, TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactoryEx.getSSLSocketFactoryForOneWay(), sslSocketFactoryEx.getTrustManager())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String host, SSLSession session) {
                            if (host.contains("www.learning.mil.cn")) {
                                return true;
                            } else {
                                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                                return hv.verify(host, session);
                            }
                        }
                    })
                    .build();
        }


        //初始化Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient.newBuilder().build())
                .addConverterFactory(LenientGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.create())
                .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(22, TimeUnit.SECONDS)
                .readTimeout(33, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(requestEncryptInterceptor)
                .retryOnConnectionFailure(true)
                .sslSocketFactory(new SSLSocketFactoryCompat(new SSLSocketFactoryCompat.TrustManager()), new SSLSocketFactoryCompat.TrustManager())
                .hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String host, SSLSession session) {
                        return true;
                    }
                })
                .build();

        //不需要验证证书，但是需要加解密数据，所以单独配置
        noVerifyRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.create())
                .build();


        //学堂接口不需要验证证书，并且不需要加解密数据，所以单独配置
        xtRetrofit = new Retrofit.Builder()
                .baseUrl(XT_ROOT_URL)
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.create())
                .build();
    }

    public static String getXtRootUrl() {
        return XT_ROOT_URL;
    }


    public static Retrofit getOldRetrofit() {
        if (oldRetrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            RequestEncryptInterceptor requestEncryptInterceptor = new RequestEncryptInterceptor();
            //moocnd.ykt.io 不进行ssl校验
//            SSLSocketFactoryExOld sslSocketFactoryEx = new SSLSocketFactoryExOld();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(requestEncryptInterceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(22, TimeUnit.SECONDS)
                    .readTimeout(33, TimeUnit.SECONDS)
                    .sslSocketFactory(new SSLSocketFactoryCompat(new SSLSocketFactoryCompat.TrustManager()), new SSLSocketFactoryCompat.TrustManager())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String host, SSLSession session) {
                            return true;
                        }
                    })
                    .build();


            //初始化Retrofit
            oldRetrofit = new Retrofit.Builder()
                    .baseUrl(OLD_BASE_URL)
                    .client(okHttpClient.newBuilder().build())
                    .addConverterFactory(LenientGsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory.create())
                    .build();
        }
        return oldRetrofit;
    }
}
