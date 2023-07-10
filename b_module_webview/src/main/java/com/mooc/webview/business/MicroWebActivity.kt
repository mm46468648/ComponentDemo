package com.mooc.webview.business

import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.search.MicroBean
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.route.Paths

/**
 * 微课页面要先查询微课的信息
 * 再加载信息中的链接
 */
@Route(path = Paths.PAGE_WEB_MICROCOURSE)
class MicroWebActivity : BaseResourceWebviewActivity() {


    /**
     * 请求微课详情
     */
    override fun loadUrl() {
        HttpService.otherApi.getMicroDetail(resourceID)
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<MicroBean>(this) {
                    override fun onSuccess(t: MicroBean) {
                        loadLinkUrl(t.url)
                    }
                })
    }

    /**
     * 加载链接地址
     */
    private fun loadLinkUrl(realUrl: String) {
        webviewWrapper.loadUrl(realUrl)

    }


}