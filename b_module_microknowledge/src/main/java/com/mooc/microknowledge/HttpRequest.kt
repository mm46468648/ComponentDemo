package com.mooc.microknowledge

import com.mooc.commonbusiness.net.ApiService

class HttpRequest {

    companion object{
        val api = ApiService.getRetrofit().create(KnowledgeAPI::class.java)
    }
}