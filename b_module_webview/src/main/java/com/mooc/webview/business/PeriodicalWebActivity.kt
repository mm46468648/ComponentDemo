package com.mooc.webview.business

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.runOnMain
import com.mooc.common.ktextends.toJSON
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.api.OtherApi
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.search.PeriodicalBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.commonbusiness.utils.format.RequestBodyUtil

/**
 * 期刊
 */
@Route(path = Paths.PAGE_WEB_PERIODICAL)
class PeriodicalWebActivity : BaseResourceWebviewActivity() {


    /**
     * 请求期刊网页内容
     */
    override fun loadUrl() {
        //如果有basicurl字段（后期为了更方便传递添加的参数，直接在期刊页面拼接，加载的地址，）
        if (intent.hasExtra(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL)) {
            val reloadUrl = intent.getStringExtra(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL)?:""
            if (reloadUrl.startsWith(UrlConstants.CHAOXING_BASE_URL)) {
                webviewWrapper.loadUrl(reloadUrl)
            } else {
                getWebContent(reloadUrl)
            }
            return
        }

        //兼容之前封装好url加载
        //如果是http的地址，直接加载
        if (loadUrl.contains(UrlConstants.CHAOXING_BASE_URL)) {
//            webviewWrapper.loadUrl(loadUrl)
            super.loadUrl()
        }else{
            webviewWrapper.loadUrl(UrlConstants.CHAOXING_BASE_URL + loadUrl)
        }


        //有的模型已经,重新封装了loadUrl,所以要判断一下
//        if (type == ResourceTypeConstans.TYPE_PERIODICAL) {
//            if (basic_url.isNotEmpty()) {
//                realUrl = basic_url
//            } else if (link.isNotEmpty() && !link.contains(UrlConstants.CHAOXING_BASE_URL)) {
//                realUrl = UrlConstants.CHAOXING_BASE_URL + link
//            }
//        }

//       getWebContent(loadUrl)

    }

    /**
     * 获取真正需要加载的内容
     */
    fun getWebContent(url: String) {

        //请求网页内容
        HttpService.otherApi.getPeiodicalDetail(url)
            .flatMap {
                //如果没有资源id，需要先调用入库，新版对接后，就不需要客户端主动调用入库了
                try {
//                    postTODb(it)
                    getShareInfo(it.data.other_resource_id)
                } catch (e: Exception) {

                }
                //这里不要动，一定要使用xtRetrofit,不然https证书校验无法通过
                ApiService.xtRetrofit.create(OtherApi::class.java)
                    .getPeiodicalContent(it.data.basic_title_url)
//                    HttpService.otherApi.getPeiodicalContent(it.data.basic_title_url)
            }
            .compose(RxUtils.applySchedulers())
            .subscribe(object : BaseObserver<HttpResponse<String>>(this) {
                override fun onSuccess(t: HttpResponse<String>) {
                    loadHtml(t.data)
                }

                override fun onFailure(code: Int, message: String?) {
                    super.onFailure(code, message)

                    loge(code,message?:"")
                }
            })
    }

    private fun postTODb(it: HttpResponse<PeriodicalBean>) {
        if (resourceID.isEmpty() || resourceID == "0") {
            runOnMain {
                mViewModel.postPeriodicalToDB(RequestBodyUtil.fromJsonStr(it.data.toJSON()))
                    .observe(this, Observer {
                        getShareInfo(it.id)
                    })
            }
        }
    }

    /**
     * 加载链接地址
     */
    private fun loadHtml(data: String) {
        var str: Array<String> = arrayOf("", "")
        if (data.contains("</head>")) {
            str = data.split("</head>".toRegex()).toTypedArray()
        }
        val finalHtml =
            """${str[0]} <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no">
                            </head>
                            <style type="text/css"> ${HtmlUtils.getStrCss()} </style>${str[1]}"""

        webviewWrapper.loadDataWithBaseURL(finalHtml)

    }


}