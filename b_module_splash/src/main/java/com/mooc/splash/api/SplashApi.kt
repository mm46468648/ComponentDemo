package com.mooc.splash.api

import com.mooc.splash.model.LaunchBean
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface SplashApi {

    //获取launch信息（启动页）
    @GET("api/mobile/app_launch/")
    fun getLunch(): Deferred<LaunchBean>
}