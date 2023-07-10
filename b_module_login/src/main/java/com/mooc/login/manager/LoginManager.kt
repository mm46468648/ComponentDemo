package com.mooc.login.manager


/**
 *
 * @ProjectName:请求登录管理类
 * @Package:
 * @ClassName:
 * @Description:    方便处理登录完后续操作
 * @Author:         xym
 * @CreateDate:     2020/8/10 6:59 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/10 6:59 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
object LoginManager {

    var onSuccess:(()->Unit)? = null

    /**
     * 去微信登录
     */
    fun toWxLogin(onSuccess:(()->Unit)? = null){
//        WechatManager.loginWx()
        this.onSuccess = onSuccess
    }

    /**
     * 登录成功回调
     */
    fun invokeSucessFunction(){
        onSuccess?.invoke()
        onSuccess = null
    }
}