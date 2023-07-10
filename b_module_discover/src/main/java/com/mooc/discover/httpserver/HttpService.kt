package com.mooc.discover.httpserver

import com.mooc.discover.api.DiscoverApi
import com.mooc.commonbusiness.net.ApiService

class HttpService {

    companion object {
        val discoverApi: DiscoverApi
            get() = ApiService.getRetrofit().create(DiscoverApi::class.java)
    }
}