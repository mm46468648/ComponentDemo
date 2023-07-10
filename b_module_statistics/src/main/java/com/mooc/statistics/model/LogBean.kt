package com.mooc.statistics.model

import android.content.res.Configuration
import com.mooc.common.global.AppGlobals
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.common.utils.Md5Util
import com.mooc.common.utils.NetUtils
import com.mooc.common.utils.SystemUtils
import com.mooc.statistics.R
import java.util.*

/**
 * 行为打点模型
 */
class LogBean : Cloneable{
    var pageID: String = ""
    var event: String = ""         //日志类型，onClick
    var element: String = ""
    var block: String = ""       //模块
    var from: String = ""
    var to: String = ""
    var strFilter: String = ""  //搜索相关可能用到

    var uid: String = GlobalsUserManager.uid            //用户id
    var uuid: String = GlobalsUserManager.uuid           //唯一标识
    var appVersion: String = SystemUtils.getVersionName()     //app版本

    //屏幕方向
    var orientation: String =
            if (AppGlobals.getApplication()?.getResources()?.getConfiguration()?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                "landscape"
            } else {
                "portrait"
            }

    //每次应用程序启动时，生成一个唯一的id，由启动时时间+10000以内随机数，生成的MD5值
    var session: String = ""
        get() {
            if (field.isEmpty()) {
                field = Md5Util.getMD5Str((System.currentTimeMillis() + Random().nextInt(10000)).toString())
            }
            return field
        }

    //时区?,原生方法但是获取的不准，我在北京，显示的是 "Asia/Shanghai"
    var timeZone: String = TimeZone.getDefault().id
    //渠道 todo 接入美团打包动态替换
    var channel: String = AppGlobals.getApplication()?.getString(R.string.channel) ?: ""
    //uuid+时间戳字符串，不知道什么意义
    var sid: String = "$uuid${System.currentTimeMillis()}"
    var network: String = NetUtils.getNetworkType()
    var strIP: String = NetUtils.getNetworkType()  //ip
    var hasNetwork: String = NetUtils.isNetworkConnected().toString()
    var strOperatorName: String = SystemUtils.getOperatorName()     //运营商
    var host: String = "android"
    var model: String = SystemUtils.getDeviceModel()        //机型
    var timestamp: String = System.currentTimeMillis().toString()


    public override fun clone(): LogBean {
        return super.clone() as LogBean
    }
}