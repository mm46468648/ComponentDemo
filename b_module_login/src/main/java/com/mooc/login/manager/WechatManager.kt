package com.mooc.login.manager

import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.login.constants.WxConstants
import com.mooc.login.share.ShareManager
import com.mooc.common.global.AppGlobals
import com.mooc.login.receiver.AppRegister
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory


/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    处理登录相关服务
 * @Author:         xym
 * @CreateDate:     2020/8/7 3:39 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/7 3:39 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
object WechatManager : IWXAPIEventHandler {

    const val TAG = "WechatManager"

    // IWXAPI 是第三方app和微信通信的openApi接口
    public val api: IWXAPI by lazy {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        WXAPIFactory.createWXAPI(AppGlobals.getApplication(), WxConstants.APP_ID, true);
    }

    /**
     * 要使你的程序启动后微信终端能响应你的程序，必须在代码中向微信终端注册你的id。
     * 可以在程序入口 Activity 的 onCreate 回调函数处，或其他合适的地方将你的应用 id 注册到微信。
     * 注册函数如下
     */
    fun regWx() {
        val myBroadcast = AppRegister()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConstantsAPI.ACTION_REFRESH_WXAPP)
        AppGlobals.getApplication()?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(myBroadcast, intentFilter)
        }
        // 将应用的appId注册到微信
        api.registerApp(WxConstants.APP_ID);
    }

    /**
     * 通过发送一个消息到微信
     * 消息范围-获取用户信息
     */
    fun loginWx() {
        // send oauth request
        val req = SendAuth.Req()
        req.scope = WxConstants.WEIXIN_SCOPE
        req.state = WxConstants.WEIXIN_STATE
        api.sendReq(req)
    }

    /**
     * 跳转小程序的方法
     */
    fun toWxLaunchMiniProgram() {
//        val appId = "wxd5eb28cd2aa4bfab" // 填移动应用(App)的 AppId，非小程序的 AppID
//        val api: IWXAPI = WXAPIFactory.createWXAPI(context, appId)
        val req = WXLaunchMiniProgram.Req()
        req.userName = WxConstants.WEIXIN_PROGRAM_ID // 填小程序原始id
        req.path =
            "pages/index?u=" + GlobalsUserManager.uid //拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW // 可选打开 开发版，体验版和正式版
        api.sendReq(req)
    }

    fun handleIntent(intent: Intent) {
        try {
            api.handleIntent(intent, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 发送的请求回调到这里
     * @param baseReq
     */
    override fun onReq(baseReq: BaseReq?) {}

    /**
     * 接受的响应回调到这里
     * @param baseResp
     */
    override fun onResp(resp: BaseResp) {
//        loge(TAG,"onResp :  ${resp?.errCode}, ${resp.javaClass?.simpleName}")
        if (resp.type == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
            //预埋分享回调
            ShareManager.sendShareBackBroadcast(resp.errCode == BaseResp.ErrCode.ERR_OK)
            return
        }

        //用户权限请求，并且用户同意
        if (resp.errCode == BaseResp.ErrCode.ERR_OK && resp is SendAuth.Resp) {
            UserInfoServiceImpl.onWxResp(resp.code)
        }
    }


}