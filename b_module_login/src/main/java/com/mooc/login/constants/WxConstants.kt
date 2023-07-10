package com.mooc.login.constants

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    微信相关常量
 * @Author:         xym
 * @CreateDate:     2020/8/7 5:06 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/7 5:06 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class WxConstants {

    companion object{
        const val APP_ID = "wxd5eb28cd2aa4bfab"
        const val APP_SECRET = "e06bf5dcbb97ff468836a2242054cb53"
        const val WEIXIN_PROGRAM_ID = "gh_e6328c0d31b9" // 填小程序原始id"
        const val WEIXIN_SCOPE = "snsapi_userinfo,snsapi_friend,snsapi_message,snsapi_contact"
        const val WEIXIN_STATE = "none"    //建议随机数+session
    }
}