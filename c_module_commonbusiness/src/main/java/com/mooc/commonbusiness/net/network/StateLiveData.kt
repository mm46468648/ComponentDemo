package com.mooc.commonbusiness.net.network

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.model.HttpResponse


/**
 * @date：2021/6/11
 * @author fuusy
 * @instruction：MutableLiveData,用于将请求状态分发给UI
 */
class StateLiveData<T> : MutableLiveData<HttpResponse<T>>() {
}