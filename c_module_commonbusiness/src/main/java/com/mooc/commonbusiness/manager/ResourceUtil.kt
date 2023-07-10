package com.mooc.commonbusiness.manager

import android.annotation.SuppressLint
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.model.HttpResponse
import io.reactivex.functions.Consumer

class ResourceUtil {

    companion object {

        /**
         * 接口请求已阅读
         */
        @SuppressLint("CheckResult")
        fun updateResourceRead(id: String) {
            //接口请求已阅读
            HttpService.Companion.commonApi.updateRead(id)
                .compose<HttpResponse<*>>(RxUtils.applySchedulers<HttpResponse<*>>())
                .subscribe(Consumer {

                },{

                })
        }
    }
}