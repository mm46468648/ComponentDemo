package com.mooc.login.api

import com.mooc.login.constants.WxConstants
import com.mooc.login.model.AppTokenResponse
import com.mooc.login.model.WxAccessTokenResponse
import com.mooc.commonbusiness.model.UserInfo
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*

interface LoginApi {

    /**
     * 获取微信授权access_token
     */
    @GET("https://api.weixin.qq.com/sns/oauth2/access_token")
    fun getAccessToken(
            @Query("code") code:String
            ,@Query("appid") appid:String = WxConstants.APP_ID
            ,@Query("secret") secret:String = WxConstants.APP_SECRET
            ,@Query("grant_type") grant_type:String = "authorization_code"
            ):Deferred<WxAccessTokenResponse>


    /**
     * 获取应用accessToken
     */
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/mobile/api-token-auth/")
    fun getAppAccessToken(@Body requestBody: RequestBody?): Deferred<AppTokenResponse>


    /**
     * 获取学堂Token   此接口可以不用了
     */
    @GET("/api/mobile/exchange/token/")
    fun getXuetangToken(): Deferred<AppTokenResponse>?

    /**
     * /获取用户信息
     */
    @GET("/api/mobile/student/info/")
    fun getUserInfo(): Deferred<UserInfo>?
}