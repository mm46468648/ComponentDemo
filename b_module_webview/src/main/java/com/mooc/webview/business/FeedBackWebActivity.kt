package com.mooc.webview.business

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.model.my.WebUrlBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.viewmodel.CommonViewModel
import com.mooc.webview.WebviewActivity
import com.mooc.webview.stratage.WebViewInitStrategy

/**
 * 意见反馈WebView
 */
//@Route(path = Paths.PAGE_WEB_FEED_BACK)
class FeedBackWebActivity : WebviewActivity() {

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
        commonViewModel.getWebUrl()

        commonViewModel.liveDataWebUrl.observe(this) {
            if (dialog != null) {
                dialog?.dismiss()
            }
            val webUrlBean: WebUrlBean
            if (it != null) {
                webUrlBean = it.data
                if (webUrlBean != null) {
                    updateData(webUrlBean)
                } else {
                    webviewWrapper.loadUrl(UrlConstants.FEEDBACK_URL)
                }
            } else {
                webviewWrapper.loadUrl(UrlConstants.FEEDBACK_URL)
            }
        }
        commonViewModel.errorExceptionWebUrl.observe(this) {
            if (dialog != null) {
                dialog?.dismiss()
            }
            webviewWrapper.loadUrl(UrlConstants.FEEDBACK_URL)
        }
    }

    private fun updateData(webUrlBean: WebUrlBean) {

        if (!TextUtils.isEmpty(webUrlBean.url)) {
            webviewWrapper.loadUrl(webUrlBean.url)
        } else {
            webviewWrapper.loadUrl(UrlConstants.FEEDBACK_URL)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //选择图片
        if (requestCode == WebViewInitStrategy.CHOOSE_FILE_REQUEST_CODE) {
            webviewWrapper.strategy.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)


    }

}