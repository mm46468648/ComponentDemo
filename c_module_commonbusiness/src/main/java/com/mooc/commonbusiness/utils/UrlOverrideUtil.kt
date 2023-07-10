package com.mooc.commonbusiness.utils

import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import java.util.HashMap

/**
 * 处理webview中url重定向
 */
class UrlOverrideUtil {

    /**
     *  跟前端约定好的格式
     *
     *  customize:// 协议由三部分构成
     *  app端的地址为app@moocnd://resource_type=1&resource_id=1&resource_link=http://moocnd.com/
     *  小程序为app@moocnd://resource_type=100&resource_id=&resource_link=pages/index
     *  wechat端的服务号地址为wechat@http://www.arcut.com，
     *  小程序为xcx://pages/index
     *  web端的地址为web@http://www.baidu.com
     *  三者之间用管道符“|”线连接起来
     *  每一个协议分俩部分，格式如：“客户端@真正的地址”
     *  在【app】端，resource_type代表唤醒的资源类型，resource_id代表唤醒的资源ID，resource_link代表如果是h5的话，h5的链接
     *  resource_id需要自己去资源列表中查看
     *  resource_type 类型如下：
     *  resource_type: 2 => '课程'
     *  resource_type: 5 => '电子书'
     *  resource_type: 15 => '专题'
     *  resource_type: 100 => '小程序'
     *  resource_type: 20 => '学习项目'
     *  这是一个错误的示例（由于一些原因不应该出现customize://app@）：customize://app@moocnd://resource_type=14&resource_id=1120&resource_link=http://moocnd.com/|wechat@http://www.arcut.com|web@http://www.baidu.com
     *  正常的示例是：moocnd://resource_type=14&resource_id=1120&resource_link=http://moocnd.com/
     */
    open fun loadStudyResource(url: String) {
        var androidUse = url
        //先通过管道分割符号 " ｜ " 截取安卓客户端的内容
        if (!TextUtils.isEmpty(url) && url.contains("|")) {
            val split = url.split("|")
            androidUse = split.find {
                it.contains("moocnd://")
            }.toString()
        }
        //去掉customize://
        var replace = androidUse.replace("customize://", "")
        //去掉app@moocnd://
        replace = replace.replace("app@moocnd://", "")
        //去掉moocnd://
        replace = replace.replace("moocnd://", "")
        //获取键值对
        val split = replace.split("&")
        if (split.isNotEmpty()) {
            val hashMap = HashMap<String, String>()
            split.forEach {
                if (it.contains("=")) {
                    val kv = it.split("=")
                    hashMap.put(kv.get(0), kv.get(1))
                }
            }

            //获取resourId,resourceType
            val resourceType = hashMap.get("resource_type") ?: ""
            val resourceId = hashMap.get("resource_id") ?: ""
            val resourceLink = hashMap.get("resource_link") ?: ""

            //微信小程序,直接跳转不用判断id
            if (ResourceTypeConstans.TYPE_WX_PROGRAM.toString().equals(resourceType)) {
                ARouter.getInstance().build(Paths.PAGE_TO_WX_PROGRAM_DIALOG).navigation()
                return
            }
            //对战,直接跳转不用判断id
            if (ResourceTypeConstans.TYPE_BATTLE.toString().equals(resourceType)) {
                ARouter.getInstance().build(Paths.PAGE_BATTLE_MAIN).navigation()
                return
            }
            //其他资源
            if (resourceType.isNotEmpty() && resourceId.isNotEmpty()) {
                try {
                    ResourceTurnManager.turnToResourcePage(object : BaseResourceInterface {
                        override val _resourceId: String
                            get() = resourceId
                        override val _resourceType: Int
                            get() = resourceType.toInt()
                        override val _other: Map<String, String>?
                            get() {
                                val hashMapOf = hashMapOf(IntentParamsConstants.WEB_PARAMS_URL to resourceLink)
                                if (_resourceType == ResourceTypeConstans.TYPE_PERIODICAL) {
                                    //如果是期刊资源，需要传递baseurl
                                    hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL, resourceLink)
                                }
                                return hashMapOf
                            }
                    })
                } catch (e: Exception) {

                }

            }
        }


    }
}