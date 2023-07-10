package com.mooc.common.utils.net

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.NetUtils

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NetStatusCallBack(var netTypeLiveData: MutableLiveData<@NetType String>) : ConnectivityManager.NetworkCallback()  {

    /**
     * 网络可用的回调
     * 当livedata的版本大于1，低于目前版本会先回调一次，后期可以考虑第一次不分发
     */
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
//        loge("lzp", "onAvailable")

        val type = NetUtils.getNetworkType()
        if (type == netTypeLiveData.value) return
        post(type)
    }

    /**
     * 网络丢失的回调
     * 断开wifi一瞬间会回调NONE，后期可以考虑延迟一会儿发送
     */
    override fun onLost(network: Network) {
        super.onLost(network)
        loge("lzp", "onLost")
        val type = NetUtils.getNetworkType()
        if (type == netTypeLiveData.value) return
        post(type)
    }

    // 执行
    private fun post(str: @NetType String) {
        netTypeLiveData.postValue(str)
    }

    /**
     * 按照官方的字面意思是，当我们的网络的某个能力发生了变化回调，
     * 可能会回调多次
     */
    override fun onCapabilitiesChanged(
        network: Network,
        networkCapabilities: NetworkCapabilities
    ) {
        super.onCapabilitiesChanged(network, networkCapabilities)
//        loge("lzp", "onCapabilitiesChanged")
    }

}