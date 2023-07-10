package com.mooc.login.manager

import androidx.lifecycle.MutableLiveData
import com.mooc.login.api.LoginApi
import com.mooc.login.model.AppTokenResponse
import com.mooc.login.model.WxAccessTokenResponse
import com.google.gson.Gson
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.net.CustomNetCoroutineExceptionHandler
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.route.routeservice.UserInfoService
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    全局用户信息管理类
 * @Author:         xym
 * @CreateDate:     2020/8/10 2:36 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/10 2:36 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
object UserInfoServiceImpl : UserInfoService {

    var userInfomation: UserInfo? = null
    var appToken: String? = ""
    var xuetangToken: String? = ""
    var uid: String? = ""
    var uuid: String? = ""


    fun isLogin(): Boolean {
        return false
    }

    fun clearUserInfo() {

    }

//    override fun getUserInfo(): UserInfo? {
//        return userInfomation;
//    }

//    override fun getAppAccessToken(): String? = appToken

    //    override fun getXtToken(): String? = xuetangToken
    private val error by lazy { MutableLiveData<Exception>() }

    val coroutineExceptionHandler = CustomNetCoroutineExceptionHandler(error)

    /**
     * 当微信授权响应
     */
    fun onWxResp(code: String) {
        GlobalScope.launch(coroutineExceptionHandler) {
            //1.调用微信接口，获取access_token
//            try {
            val wxAccessTokenResponse = getWxAccessToken(code)
            wxAccessTokenResponse.enent_name = "android"

            //2.根据response的json请求后端获取用户token
            val requestData = Gson().toJson(wxAccessTokenResponse)


            val appTokenResponse = getAppAccessToken(requestData)
            //3.设置到userManager
            GlobalsUserManager.appToken = appTokenResponse.token ?: ""
            //4.同时请求用户信息
            val userInfoFromServer = getUserInfoFromServer()
            //5.同时请求学堂token
//                val xtTokenFromServer = getXtTokenFromServer()
            val userInfo = userInfoFromServer?.await()
            GlobalsUserManager.userInfo = userInfo
            //同步发送用户登录信息改变事件
            EventBus.getDefault().post(UserLoginStateEvent(userInfo))
            LoginManager.invokeSucessFunction()
            //6.同步设置到userManager
//                GlobalsUserManager.xuetangToken = xtTokenFromServer?.await()?.token ?: ""

        }
    }


    /**
     * 获取微信AccessToken
     */
    private suspend fun getWxAccessToken(code: String): WxAccessTokenResponse {
        return ApiService.xtRetrofit.create(LoginApi::class.java).getAccessToken(code).await()
    }

    /**
     * 获取应用token
     * @param wxAccessTokenResponse的json类型字符串
     */
    private suspend fun getAppAccessToken(requestData: String): AppTokenResponse {
        val toRequestBody =
            requestData.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return ApiService.getRetrofit().create(LoginApi::class.java)
            .getAppAccessToken(toRequestBody)
            .await()
    }

    /**
     * 获取用户信息
     */
    fun getUserInfoFromServer() =
        ApiService.getRetrofit().create(LoginApi::class.java).getUserInfo()

    /**
     * 获取学堂Token
     */
    suspend fun getXtTokenFromServer() =
        ApiService.getRetrofit().create(LoginApi::class.java).getXuetangToken()


}