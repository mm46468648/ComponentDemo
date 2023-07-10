package com.mooc.webview.business

import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.utils.DateStyle
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.commonbusiness.viewmodel.CommonViewModel
import com.mooc.commonbusiness.model.privacy.PrivacyPolicyCheckBean
import com.mooc.webview.WebviewActivity

/**
 * 隐私策略页面
 */
@Route(path = Paths.PAGE_WEB_PRIVACY)
class PrivacyWebActivity : WebviewActivity() {

    private val commonViewModel by viewModels<CommonViewModel>()
    private var dialog: CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    /**
     * 请求隐私详情
     */
    private fun initData() {
        webviewWrapper.getView().setOnLongClickListener {
            true
        }
        dialog = CustomProgressDialog.createLoadingDialog(this, true)
        dialog?.show()
        commonViewModel.getPrivacyPolicyCheck("")

        commonViewModel.liveData.observe(this) {
            if (dialog != null) {
                dialog?.dismiss()
            }
            val privacyPolicyCheckBean: PrivacyPolicyCheckBean
            if (it != null) {
                privacyPolicyCheckBean = it.data
                if (privacyPolicyCheckBean != null) {
                    updateData(privacyPolicyCheckBean)
                } else {
                    webviewWrapper.loadUrl(UrlConstants.PRIVACY_POLICY_URL)
                }
            } else {
                webviewWrapper.loadUrl(UrlConstants.PRIVACY_POLICY_URL)
            }
        }
        commonViewModel.errorException.observe(this) {
            if (dialog != null) {
                dialog?.dismiss()
            }
            webviewWrapper.loadUrl(UrlConstants.PRIVACY_POLICY_URL)
        }
    }

    private fun updateData(privacyPolicyCheckBean: PrivacyPolicyCheckBean) {
        var describe = privacyPolicyCheckBean.describe
        var title = ""
        if (privacyPolicyCheckBean.title?.isNotEmpty() == true) {
            title = """<p style="text-align:center;font-size: 20px;">
                            ${privacyPolicyCheckBean.title} </p>"""
        }
        var usedTimeContent = ""
        if (privacyPolicyCheckBean.used_time?.isNotEmpty() == true) {
            val usedTime = DateUtil.StringToString(privacyPolicyCheckBean.used_time,
                    DateUtil.getDateStyle(privacyPolicyCheckBean.used_time),
                    DateStyle.YYYY_MM_DD_CN)

            usedTimeContent = """<p style="text-align:right;">
                            本隐私信息保护政策生效日期：
                            $usedTime </p>"""
        }
        var version = ""
        if (privacyPolicyCheckBean.version?.isNotEmpty() == true) {
            version = """<p style="text-align:right;">
                            本隐私信息保护政策版本：
                            ${privacyPolicyCheckBean.version} </p>"""
        }

        var privacyTop = ""
        if (describe?.isNotEmpty() == true) {
            if (title.isNotEmpty()) {
                privacyTop += title
            }
            if (usedTimeContent.isNotEmpty()) {
                privacyTop += usedTimeContent
            }
            if (version.isNotEmpty()) {
                privacyTop += version
            }
            if (privacyTop.isNotEmpty()) {
                describe = privacyTop + describe
            }
            loadLinkUrl(describe)
        } else {
            webviewWrapper.loadUrl(UrlConstants.PRIVACY_POLICY_URL)
        }
    }

    /**
     * 加载链接地址
     */
    private fun loadLinkUrl(realUrl: String) {
        val finalHtml = HtmlUtils.getHtml(realUrl)
        webviewWrapper.loadDataWithBaseURL(finalHtml)

    }


}