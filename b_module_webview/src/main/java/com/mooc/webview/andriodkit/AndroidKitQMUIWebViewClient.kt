package com.mooc.webview.andriodkit

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.*
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.commonbusiness.utils.InjectJsManager
import com.mooc.webview.OverrideUrlLoadingDelegete
import com.mooc.webview.stratage.WebViewInitStrategy
import com.qmuiteam.qmui.widget.webview.QMUIWebViewClient

/**
 * Android系统webView内核的webViewClient
 */
class AndroidKitQMUIWebViewClient(var startage: WebViewInitStrategy? = null) : QMUIWebViewClient(false,false) {


    private val overrideDelegate by lazy { OverrideUrlLoadingDelegete() }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)

        loge("request: ${request} errorResponse: ${errorResponse} ")

    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)

        loge("error: ${error} request: ${request?.url} ")
    }
    override fun onReceivedSslError(webView: WebView, handler: SslErrorHandler, p2: SslError?) {
        handler.proceed() // 接受所有网站的证书

        loge("SslError: ${p2}")
    }

    override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {

        //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
        //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
        overrideDelegate.shouldOverrideUrlLoading(webView, url)
        startage?.recordeUrlHeight?.invoke(url ?: "")
        return true
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
//        loge("shouldInterceptRequest: ${request}")
        InjectJsManager.injectJs(view)
        return super.shouldInterceptRequest(view, request)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onPageStarted(webView: WebView, s: String?, bitmap: Bitmap?) {
        InjectJsManager.closeInject = true
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onPageFinished(webView: WebView, url: String?) {
        //恢复图片的加载
        webView.settings.blockNetworkImage = false
        //开启js
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(NormalConstants.JS_REMOVE_ADVERT_FUNCTION)
    }
}