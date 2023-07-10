package com.mooc.resource.utils

import android.content.Context

class StatusBarUtil {

    companion object{
        /**
         * 获取状态栏高度
         * @param context
         * @return
         */
        fun getStatusBarHeight(context: Context): Int {
            var statusBarHeight = 0
            try {
                val resourceId = context.resources.getIdentifier("status_bar_height", "dimen",
                        "android")
                if (resourceId > 0) {
                    statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return statusBarHeight
        }
    }
}