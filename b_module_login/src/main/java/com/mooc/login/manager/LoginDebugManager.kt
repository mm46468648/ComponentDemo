package com.mooc.login.manager

import com.google.gson.Gson
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.net.ApiService
import com.mooc.login.api.LoginApi
import com.mooc.login.model.AppTokenResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus

/**
 * 登录调试类
 * 只在开发环境中才能初始化
 * 或者暗门开启
 */
object LoginDebugManager {
    /**
     * 去测试登录
     */
    fun toTestLogin() {

        //仿照微信的返回格式，去请求用户信息，可以把自己的账号添加上
        //liuhuiying 421
//         val wxResponseMap = hashMapOf(
//                "openid" to "ovPuTwZTOB1hKU7xDP86NYjD2y-4s",
//                "unionid" to "oPWZdxHJ8p-0BJ0e568dMZBcsRak",
//                "access_token" to "mllYbwyb5Gh1ba88R5wnlQns8he7DbBN5R9Wp0ylcuf4hMgcJnUB0_7tq4j_uTgs8QoEWFaQ8ck7IYVuHiBHEA",
//                "event_name" to "android")
        //mingjuan
//        val wxResponseMap = hashMapOf(
//                "openid" to "ovPuTwS891U2BkL_8YASWL49GRC8",
//                "unionid" to "oPWZdxLe5bpe8Q1alpFl39tWyjh8",
//                "access_token" to "sk78SRCZ6_OnKNgcQYNldQ_ayquY4-DCADASTGjLZzolxFcbtpPcNzBbKA0tba_Zx8UJFbKl3GRx7nbgaKTL-Op76NMMvApv1h-2ZMyxx0o",
//                "event_name" to "android"
//        )

        //xieyouming
//        val wxResponseMap = hashMapOf(
//                "openid" to "osCoowEcu8RSIaeDz3zAGkJKPBuU",
//                "unionid" to "oPWZdxDjhk7a1V-TLkOXqlxTIjRI",
//                "access_token" to "37_srL4TK1hH2uJTs_WelquSKuEdVDwyNKimsYlpV1P_WKGh1h6n7eXZWBTtFaNOdKNnPSXPikFTfvjC5jUZJiQ6cakxrMwJyMIO3Z47duT9t8",
//                "event_name" to "android")


        //学军
//        val wxResponseMap = hashMapOf(
//                "openid" to "oTc6RwH0P3Nn_JJb9A2gciewdmT4",
//                "unionid" to "oPWZdxN7ERhO1cjFb_BYny5y9tKA",
//                "access_token" to "42_lknh8zG9JeZRtpYzSU3TyvOALgKHElWXhJcMT8xQrtFWtV0RFLc-J--NjT5sRdpbzWz_iWn7T6aELGcDxscBP23xn2IiwvHi1qM86CfulHI",
//                "event_name" to "android")

        //学君小号
//        val wxResponseMap = hashMapOf(
//                "openid" to "oN3_v0gy1H64s6k-VJhJNQ9T1xik",
//                "unionid" to "oPWZdxOwXiKaP9lCF9MkPJr3rFp8",
//                "access_token" to "42_lknh8zG9JeZRtpYzSU3TyvOALgKHElWXhJcMT8xQrtFWtV0RFLc-J--NjT5sRdpbzWz_iWn7T6aELGcDxscBP23xn2IiwvHi1qM86CfulHI",
//                "event_name" to "android")

//        未知测试账号1?
//        val wxResponseMap = hashMapOf(
//                "openid" to "osCoowM-_nzFkv3TZT2Qh8Uj5bMM",
//                "unionid" to "oPWZdxGYGA4hx_hMtc6ru1tfH_vE",
//                "access_token" to "mllYbwyb5Gh1ba88R5wnlQns8he7DbBN5R9Wp0ylcuf4hMgcJnUB0_7tq4j_uTgs8QoEWFaQ8ck7IYVuHiBHEA",
//                "event_name" to "android"
//        )


//        未知测试账号2?
//        val wxResponseMap = hashMapOf(
//                "openid" to "oq75wwL5RZM3u-0NoPVGMBHL4jVM",
//                "unionid" to "oPWZdxH52baW5-VyJ01ll2VJ9v8I",
//                "access_token" to "gf1e8B7b2XYkhLYGE1AHlCb10j347Ck7kb8blYwqcw_lGigBLaJ5ygwwfY5Q8IX9XvFRngtwRztPC-hlpJsg_4Pgzloxu_3h_ivHX3gq7Lo",
//                "event_name" to "android"
//        )

//        测试账号67   （酉鸣使用中。。）
//        val wxResponseMap = hashMapOf(
//                "openid" to "ovzCbxKSeFDCKmiVBAIjENwnFCvU",
//                "unionid" to "oPWZdxDrJSi98IxDymigD4UEDn_w",
//                "access_token" to "gf1e8B7b2XYkhLYGE1AHlCb10j347Ck7kb8blYwqcw_lGigBLaJ5ygwwfY5Q8IX9XvFRngtwRztPC-hlpJsg_4Pgzloxu_3h_ivHX3gq7Lo",
//                "event_name" to "android"
//        )
        

//        博学使用中
//        val wxResponseMap = hashMapOf(
//                "openid" to "ovPuTwfgccjLJdtNN1FH_PJCVkvA",
//                "unionid" to "oPWZdxPJr_C90yW9LuWHCyGO45uY",
//                "access_token" to "gf1e8B7b2XYkhLYGE1AHlCb10j347Ck7kb8blYwqcw_lGigBLaJ5ygwwfY5Q8IX9XvFRngtwRztPC-hlpJsg_4Pgzloxu_3h_ivHX3gq7Lo",
//                "event_name" to "android"
//        )

//        博学使用中
        val wxResponseMap = hashMapOf(
                "openid" to "oWaCDweNvynat7iaG9J9jmh2yCkg",
                "unionid" to "oPWZdxHfPgGCcQRmKLWboVTTxJnI",
                "access_token" to "gf1e8B7b2XYkhLYGE1AHlCb10j347Ck7kb8blYwqcw_lGigBLaJ5ygwwfY5Q8IX9XvFRngtwRztPC-hlpJsg_4Pgzloxu_3h_ivHX3gq7Lo",
                "event_name" to "android"
        )
//        // 阿梦的号
//        val wxResponseMap = hashMapOf(
//                "openid" to "osCoowL1K1EWnfEgIFjKUr-g6zS4",
//                "unionid" to "oPWZdxN3PzqIdfSkWetpSrzth3os",
//                "access_token" to "gf1e8B7b2XYkhLYGE1AHlCb10j347Ck7kb8blYwqcw_lGigBLaJ5ygwwfY5Q8IX9XvFRngtwRztPC-hlpJsg_4Pgzloxu_3h_ivHX3gq7Lo",
//                "event_name" to "android"
//        )
//        // 阿梦的号
//        val wxResponseMap = hashMapOf(
//                "openid" to "osCoowL1K1EWnfEgIFjKUr-g6zS4",
//                "unionid" to "oPWZdxN3PzqIdfSkWetpSrzth3os",
//                "access_token" to "gf1e8B7b2XYkhLYGE1AHlCb10j347Ck7kb8blYwqcw_lGigBLaJ5ygwwfY5Q8IX9XvFRngtwRztPC-hlpJsg_4Pgzloxu_3h_ivHX3gq7Lo",
//                "event_name" to "android"
//        )
//        // 阿梦的号
//        val wxResponseMap = hashMapOf(
//                "openid" to "osCoowL1K1EWnfEgIFjKUr-g6zS4",
//                "unionid" to "oPWZdxN3PzqIdfSkWetpSrzth3os",
//                "access_token" to "gf1e8B7b2XYkhLYGE1AHlCb10j347Ck7kb8blYwqcw_lGigBLaJ5ygwwfY5Q8IX9XvFRngtwRztPC-hlpJsg_4Pgzloxu_3h_ivHX3gq7Lo",
//                "event_name" to "android"
//        )


        postTestLogin(wxResponseMap)
    }


    /**
     * 发送测试登录信息
     */
    fun postTestLogin(wxResponseMap: HashMap<String, String>) {
        GlobalScope.launch {
            //1.调用微信接口，获取access_token
//            val wxAccessTokenResponse = getWxAccessToken(code)
//            wxAccessTokenResponse.enent_name = "android"
//            loge("wxAccessTokenResponse: ${wxAccessTokenResponse}}")
            //2.根据response的json请求后端获取用户token
            val requestData = Gson().toJson(wxResponseMap)

            try {
                val appTokenResponse = getAppAccessToken(requestData)
                //3.设置到userManager
                GlobalsUserManager.appToken = appTokenResponse.token ?: ""

//            //4.同时请求用户信息
                val userInfoFromServer = getUserInfoFromServer()
//            //5.同时请求学堂token
//                val xtTokenFromServer = getXtTokenFromServer()

                GlobalsUserManager.userInfo = userInfoFromServer?.await()
                LoginManager.invokeSucessFunction()
//            //6.同步设置到userManager
//                GlobalsUserManager.xuetangToken = xtTokenFromServer?.await()?.token ?: ""
//                loge("userInfo: ${userInfoFromServer.toString()}")
                EventBus.getDefault().post(UserLoginStateEvent(GlobalsUserManager.userInfo))
            } catch (e: Exception) {
                loge("httpException: ${e.toString()}")
            }
        }
    }


    /**
     * 获取应用token
     * @param wxAccessTokenResponse的json类型字符串
     */
    private suspend fun getAppAccessToken(requestData: String): AppTokenResponse {
        val toRequestBody = requestData.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return ApiService.getRetrofit().create(LoginApi::class.java).getAppAccessToken(toRequestBody).await()
    }

    /**
     * 获取用户信息
     */
     fun getUserInfoFromServer() = ApiService.getRetrofit().create(LoginApi::class.java).getUserInfo()

    /**
     * 获取学堂Token
     */
     fun getXtTokenFromServer() = ApiService.getRetrofit().create(LoginApi::class.java).getXuetangToken()


}