package com.mooc.b_module_studyproject.httpserver

import com.mooc.studyproject.api.UserApi
import com.mooc.commonbusiness.net.ApiService

class HttpService {

    companion object {
        val userApi: UserApi
            get() = ApiService.getRetrofit().create(UserApi::class.java)
    }
}