package com.mooc.commonbusiness.global

import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.utils.sp.SPUserUtils

object GlobalsUserManager {

    /**
     *  用户信息，
     *  先从内存中读取
     *  内存中没有从sp中获取
     */
    var userInfo: UserInfo? = null
        set(value) {
            field = value
            SPUserUtils.getInstance().saveUserInfo(value)
//                appToken = value.token?:""   //用户信息中的token字段是null，不能在用户信息里保存token
        }
        get() {
            if (field != null)
                return field
            field = SPUserUtils.getInstance().userInfo
            return field
        }

    /**
     *  appToken，
     *  先从内存中读取
     *  内存中没有从sp中获取
     *
     *  token只能从获取token接口中获取
     */
    var appToken: String = ""
        set(value) {
            field = value
            SPUserUtils.getInstance()
                .putString(SPUserUtils.KEY_USER_TOKEN, value)
        }
        get() {
            if (field.isNotEmpty()) {  //如果不为""，直接返回
                return field
            }        //否则从sp获取
            return SPUserUtils.getInstance()
                .getString(SPUserUtils.KEY_USER_TOKEN, "")
        }

    /**
     *  学堂Token，
     *  先从内存中读取
     *  内存中没有从sp中获取
     */
    @Deprecated("新版本不需要存")
    var xuetangToken: String = ""
        set(value) {
            field = value
//            PreferenceUtils.setPrefString(AppGlobals.getApplication(), SpConstants.XT_TOKEN, value);
            SpDefaultUtils.getInstance().putString(SpConstants.XT_TOKEN, value);
        }
        get() {
            if (field.isNotEmpty()) {
                return field
            }
//            return PreferenceUtils.getPrefString(AppGlobals.getApplication(), SpConstants.XT_TOKEN, "");
            return SpDefaultUtils.getInstance().getString(SpConstants.XT_TOKEN, "");
        }

    var uuid: String = ""
        get() {
            field = userInfo?.uuid ?: ""
            return field
        }
    var uid: String = ""
        get() {
            field = userInfo?.id ?: ""
            return field
        }

    fun isLogin(): Boolean {
//        return userInfo != null
        return SPUserUtils.getInstance().userInfo != null
    }

    /**
     * 退出登录，清空用户信息
     * token也置为空
     */
    @JvmStatic
   public fun clearUserInfo() {
        userInfo = null
        appToken = ""
        uuid = ""
        uid = ""
        SPUserUtils.getInstance().saveUserInfo(null)
    }

}