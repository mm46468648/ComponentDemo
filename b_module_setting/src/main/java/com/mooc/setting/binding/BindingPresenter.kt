package com.mooc.setting.binding

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.setting.R

/**
 * 布局绑定主持类
 */
class BindingPresenter {

    companion object {
        //跳转用户服务协议
        @JvmStatic
        fun turnToUserServiceAgreement() {
            ARouter.getInstance().build(Paths.PAGE_WEB)
                    .with(Bundle().put("params_title", AppGlobals.getApplication()?.resources?.getString(R.string.text_str_user_service_intro))
                            .put("params_url", UrlConstants.USER_SERVICE_AGREDDMENT_URL))
                    .navigation()

        }

        //跳转隐私政策
        @JvmStatic
        fun turnToPrivacyPolicy() {
            ARouter.getInstance().build(Paths.PAGE_WEB_PRIVACY)
                    .with(Bundle().put("params_title", AppGlobals.getApplication()?.resources?.getString(R.string.text_str_privacy_policy)))
                    .navigation()
        }
    }
}