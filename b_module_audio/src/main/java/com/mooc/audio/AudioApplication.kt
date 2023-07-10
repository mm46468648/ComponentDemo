package com.mooc.audio

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseApplication
import com.mooc.audio.receiver.XMLYPlayerReceiver
import com.ximalaya.ting.android.opensdk.datatrasfer.DeviceInfoProviderDefault
import com.ximalaya.ting.android.opensdk.datatrasfer.IDeviceInfoProvider
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater
import com.ximalaya.ting.android.opensdk.util.BaseUtil

class AudioApplication : BaseApplication() {
    override fun init() {
//        ConstantsOpenSdk.isDebug = DebugUtil.debugMode
//        XMediaPlayerConstants.isDebug = DebugUtil.debugMode
//        x.Ext.init(BaseApplication.instance)
//        val mXimalaya = CommonRequest.getInstanse()
//        mXimalaya.setAppkey("7dda552f9900e8a4733f84df74fe83c4");
//        mXimalaya.setPackid("com.moocxuetang");
//        // 优先取oaid作为设备ID，如果获取不到再按照列表顺序优先级进行获取，如果出于用户隐私数据安全考虑，可以对得到的设备ID再进行MD5/SHA1/SHA256哈希，注意不要加盐，并请告知平台技术支持同学。
//        instance?.let {
//            if (BaseUtil.isMainProcess(it)){
//                mXimalaya.init(instance, "fbac060c0f0928e090422d581eab59ff", null)
//            }
            makeCustomNotifycation()
//        }
    }

    fun getDeviceInfoProvider(context: Context): IDeviceInfoProvider {
        return object : DeviceInfoProviderDefault(context) {
            override fun oaid(): String {
                // 合作方要尽量优先回传用户真实的oaid，使用oaid可以关联并打通喜马拉雅主app中记录的用户画像数据，对后续个性化推荐接口推荐给用户内容的准确性会有极大的提升！
                return ""
            }
        }
    }

    fun makeCustomNotifycation() {
        loge("XMLY make notifycation ${instance}  processName: ${BaseUtil.getCurProcessName(instance)}")
        if (instance == null) return
        if(!BaseUtil.getCurProcessName(instance).contains(":player")) return

        //自定义关闭通知
        val instanse = XmNotificationCreater.getInstanse(BaseApplication.instance)
        val actionName_close = XMLYPlayerReceiver.ACTION_CLOSE
        val intent = Intent(actionName_close)
        intent.setClass(
            BaseApplication.instance as Context,
            XMLYPlayerReceiver::class.java
        )
        val broadcast = PendingIntent.getBroadcast(
            BaseApplication.instance,
            0,
            intent,
            0
        )
        instanse.setClosePendingIntent(broadcast)
        val actionName_play = XMLYPlayerReceiver.ACTION_PLAY_PLAUSE
        val intent_play = Intent(actionName_play)
        intent_play.setClass(
            BaseApplication.instance as Context,
            XMLYPlayerReceiver::class.java
        )
        val broadcast_play = PendingIntent.getBroadcast(
            BaseApplication.instance as Context,
            0,
            intent_play,
            0
        )
        instanse.setStartOrPausePendingIntent(broadcast_play)
        val actionName_next = XMLYPlayerReceiver.ACTION_NEXT
        val intent_next = Intent(actionName_next)
        intent_next.setClass(
            BaseApplication.instance as Context,
            XMLYPlayerReceiver::class.java
        )
        val broadcast_next = PendingIntent.getBroadcast(
            BaseApplication.instance as Context,
            0,
            intent_next,
            0
        )
        instanse.setNextPendingIntent(broadcast_next)
        val actionName_pre = XMLYPlayerReceiver.ACTION_PRE
        val intent_pre = Intent(actionName_pre)
        intent_pre.setClass(
            BaseApplication.instance as Context,
            XMLYPlayerReceiver::class.java
        )
        val broadcast_pre = PendingIntent.getBroadcast(
            BaseApplication.instance as Context,
            0,
            intent_pre,
            0
        )
        instanse.setPrePendingIntent(broadcast_pre)
    }
}