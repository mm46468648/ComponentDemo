package com.mooc.commonbusiness.api

import com.mooc.commonbusiness.net.ApiService

class HttpService {

    companion object {
        val xtApi: XtApi = ApiService.xtRetrofit.create(XtApi::class.java)
        val studyRoomApi: StudyRoomApi get() = ApiService.getRetrofit().create(StudyRoomApi::class.java)
        val commonApi: CommonApi get() =  ApiService.getRetrofit().create(CommonApi::class.java)
        val otherApi: OtherApi get() = ApiService.getRetrofit().create(OtherApi::class.java)

    }
}