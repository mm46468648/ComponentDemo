package com.mooc.commonbusiness.utils

import java.util.*

/**
 *防止点击
 */
class RepeatedClickUtils {
    companion object {

        /**
         * 防止重复点击
         *
         * @param target 目标view
         * @param listener 监听器
         */
//        fun preventRepeatedClick(target: View?, listener: View.OnClickListener) {
//            RxView.clicks(target).throttleFirst(1, TimeUnit.SECONDS)
//                .subscribe(object : Observer<Any?>() {
//                    fun onCompleted() {}
//                    fun onError(e: Throwable?) {}
//                    fun onNext(`object`: Any?) {
//                        listener.onClick(target)
//                    }
//                })
//        }


        private val MIN_DELAY_TIME: Int = 1000 // 两次点击间隔不能少于1000ms
        private var lastClickTime: Long = 0

        /**
         * 用于判断是否快速点击
         */
        fun isFastClick(): Boolean {

            var flag = true
            val currentClickTime = System.currentTimeMillis()

            if (currentClickTime - lastClickTime < MIN_DELAY_TIME) {
                flag = false
            }
            lastClickTime = currentClickTime
            return flag
        }

    }
}