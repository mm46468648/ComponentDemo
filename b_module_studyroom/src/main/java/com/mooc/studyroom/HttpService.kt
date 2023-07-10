package com.mooc.studyroom

import com.mooc.commonbusiness.net.ApiService

class HttpService {
    
    companion object{
        val studyRoomApi get() = ApiService.getRetrofit().create(StudyRoomApi::class.java)
    }
}