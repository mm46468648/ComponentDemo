package com.mooc.webview.business

import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.dialog.CustomProgressDialog.Companion.createLoadingDialog
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils.Companion.getHtml
import com.mooc.webview.WebviewActivity
import com.mooc.webview.api.RecommendApi
import com.mooc.webview.model.CompetitionManageData

/**
 * 竞赛参与规则WebView详情
 */
@Route(path = Paths.PAGE_WEB_MATCH_RULE)
class WebViewParticipateRuleActivity : WebviewActivity() {
    override fun loadUrl() {
        super.loadUrl()
        if (!TextUtils.isEmpty(resourceID)) {
            dataFromNet()
        }
    }

    fun dataFromNet() {
        val dialog = createLoadingDialog(this, true)
        dialog.show()
        ApiService.getRetrofit().create(RecommendApi::class.java)
            .getParticipateRuleDetail(resourceID)
            .compose(RxUtils.applySchedulers())
            .subscribe(object : BaseObserver<HttpResponse<CompetitionManageData>>(this) {
                override fun onSuccess(competitionManageDataHttpResponse: HttpResponse<CompetitionManageData>) {
                    dialog.dismiss()
                    if (competitionManageDataHttpResponse != null) {
                        if (competitionManageDataHttpResponse.isSuccess) {
                            if (competitionManageDataHttpResponse.data != null) {
                                showContent(competitionManageDataHttpResponse.data)
                            }
                        }
                    }
                }

                override fun onFailure(code: Int, message: String) {
                    super.onFailure(code, message)
                    dialog.dismiss()
                }
            })
    }


    //设置内容
    private fun showContent(data: CompetitionManageData?) {
        val detail = data!!.content
        webviewWrapper.loadDataWithBaseURL(getHtml(detail))
    }
}