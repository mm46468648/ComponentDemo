package com.mooc.login.model

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    微信AccessToken响应体
 * @Author:         xym
 * @CreateDate:     2020/8/7 5:40 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/7 5:40 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
data class WxAccessTokenResponse(
    var access_token : String,
    var expires_in : String,
    var refresh_token : String,
    var openid : String,
    var scope : String,
    var unionid : String,
    var enent_name:String = "android"    //传给后台需要这个参数，不是微信返回的
)
