package com.mooc.home

import com.mooc.commonbusiness.net.ApiService
import com.mooc.home.api.HomeApi

class HttpService {

    companion object {
        //        var homeApi :HomeApi = ApiService.getRetrofit().create(HomeApi::class.java)
//        var noEncyrptApi:NoEncryptApi = ApiService.getRetrofit().create(NoEncryptApi::class.java)
        val homeApi: HomeApi
            get() = ApiService.getRetrofit().create(HomeApi::class.java)

    }


}