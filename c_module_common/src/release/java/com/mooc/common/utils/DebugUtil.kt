package com.mooc.common.utils

open class DebugUtil {

    companion object {
        @JvmField
        var isTestServeUrl = false  //是否是测试服务接口  true 测试地址 false 正式地址

        @JvmField
        var isNoEncrypt = false //是否不加密传输 true：不加密  false:加密

        @JvmField
        var isNoVerifyCertificate = false //true:不校验证书  false:校验证书

        @JvmField
        var debugMode = false //debug模式

    }
}