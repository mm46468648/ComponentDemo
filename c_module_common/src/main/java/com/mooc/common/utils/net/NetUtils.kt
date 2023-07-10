package com.mooc.common.utils.net

import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

object NetUtils {
    /**
     * 获取网络状态
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getNetStatus(ties: NetworkCapabilities) : @NetType String {
        return if (!ties.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            NetType.NONE
        } else {
            if (ties.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                ties.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                // 使用WI-FI
                NetType.WIFI
            } else if (ties.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                ties.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ) {
                // 使用蜂窝网络
                NetType.NET
            } else{
                // 未知网络，包括蓝牙、VPN、LoWPAN
                NetType.NET_UNKNOWN
            }
        }
    }
}