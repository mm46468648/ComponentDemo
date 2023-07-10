package com.mooc.common.utils.net

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.mooc.common.utils.NetUtils

/**
 * 网络变化监听管理类
 */
class NetMangaer(private var application: Application) {


    companion object {

        @Volatile
        private var INSTANCE: NetMangaer? = null

        @JvmStatic
        fun getInstance(application: Application): NetMangaer {
            val temp = INSTANCE
            if (null != temp) {
                return temp
            }
            synchronized(this) {
                val instance = NetMangaer(application)
                INSTANCE = instance
                return instance
            }
        }
    }


    // 网络状态广播监听
    private val receiver = NetStatusReceiver()

    // 网络状态
    private val netTypeLiveData: MutableLiveData<@NetType String> = MutableLiveData()

    // 获取状态
    fun getNetTypeLiveData() : MutableLiveData<@NetType String> = netTypeLiveData

    init {
        _init()
    }

    fun _init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val connectivityManager =
                application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            // 请注意这里会有一个版本适配bug，所以请在这里添加非空判断
            if (connectivityManager != null) {
                connectivityManager.registerNetworkCallback(
                    NetworkRequest.Builder().build(),
                    NetStatusCallBack(netTypeLiveData)
                )
                return
            }
        }
        registBroadcastReceiver()
    }


    /**
     * 5。0或者connectivityManager为空的时候
     * 注册网络监听广播
     */
    private fun registBroadcastReceiver() {
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        application.registerReceiver(receiver, filter)
    }


    inner class NetStatusReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            context ?: return
            intent ?: return
            val type = NetUtils.getNetworkType()
            if (type == netTypeLiveData.value) return
            post(type)
        }
    }

    // 执行
    private fun post(str: @NetType String) {
        netTypeLiveData.postValue(str)
    }

}