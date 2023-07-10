package com.mooc.commonbusiness.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.model.PublicDialogBean
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * 容联云相关初始化
 */
class RlyUtils {
    companion object {
        @SuppressLint("CheckResult")
        fun postSdkPermission(activity: Activity) {
            val rxPermissions = RxPermissions(activity as FragmentActivity)

            //初始化SDK
            if (rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && rxPermissions.isGranted(Manifest.permission.READ_PHONE_STATE)
            ) {
                initSdk(activity)
            } else {
                rxPermissions.request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
                )
                    ?.subscribe {
                        if (it) {
                            initSdk(activity)
                        } else {
                            showPermissionDialog(activity)
                        }
                    }
            }

        }

        fun showPermissionDialog(activity: Activity) {
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = activity.resources.getString(R.string.permission_file_read)
            publicDialogBean.strTv = activity.resources.getString(R.string.text_ok)
            val dialog = PublicOneDialog(activity, publicDialogBean)
            XPopup.Builder(activity)
                .asCustom(dialog)
                .show()
        }

        fun initSdk(activity: Activity) {
//            //设置sdk 显示语言版本
////        initLanguage("en");
//            /*
//              第一步:初始化help
//             */
//            val helper = KfStartHelper(activity)
//
//
//            /*
//              商品信息实例，若有需要，请参照此方法；
//            */
////        handleCardInfo(helper);
//
//            /*
//              新卡片类型示例，若有需要，请参照此方法；
//             */
////        handleNewCardInfo(helper);
//            /**
//             * 第二步：设置服务环境
//             * setRequestUrl() 与 setRequestBasic()，为二选一调用
//             * 腾讯云，阿里云用户只使用setRequestBasic即可。
//             * 私有云用户只使用setRequestUrl即可。
//             */
//            /**2:
//             * 开放给私有云客户 设置地址的接口
//             * 要在helper.initSdkChat()之前设置
//             */
//            RequestUrl.setRequestUrl(
//                0,
//                "",
//                "https://kefu.learning.mil.cn/sdkChat",
//                "https://kefu.learning.mil.cn/sdkChat",
//                "kefu.learning.mil.cn/sdkSocket"
//            )
//            /**2.1
//             * 开放给私有云客户 设置文件服务地址的接口，如果私有云后端配置了文件服务器则需要调用。
//             * 要在helper.initSdkChat()之前设置
//             * 示例：RequestUrl.setFileUrl( "https://im-fileserver:8000/",new String[]{"im-fileserver:8000"},true)
//             */
//            RequestUrl.setFileUrl(
//                "https://kefu.learning.mil.cn/fileserver/",
//                arrayOf("kefu.learning.mil.cn/fileserver/"),
//                true
//            )
//
//            /*
//              第三步:设置参数
//              初始化sdk方法，必须先调用该方法进行初始化后才能使用IM相关功能
//              @param accessId       接入id（需后台配置获取）
//              @param userName       用户名
//              @param userId         用户id
//             */
//
//            val userBean: UserInfo? = SPUserUtils.getInstance().getUserInfo()
//            if (userBean != null) {
//                val userName: String? = userBean.name
//                val userId: String = userBean.id
//                if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userName)) {
//                    helper.initSdkChat("0039e770-46c1-11ec-8c7b-653fc8f2b508", userName, userId)
//                }
//            }
        }
    }
}