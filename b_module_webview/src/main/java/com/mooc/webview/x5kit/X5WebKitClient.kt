package com.mooc.webview.x5kit

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.webview.OverrideUrlLoadingDelegete
import com.mooc.webview.stratage.WebViewInitStrategy
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

class X5WebKitClient(var startage: WebViewInitStrategy? = null) : WebViewClient() {


    private val overrideDelegate by lazy { OverrideUrlLoadingDelegete() }

    override fun onReceivedSslError(
            webView: WebView,
            handler: SslErrorHandler,
            p2: com.tencent.smtt.export.external.interfaces.SslError?
    ) {
        handler.proceed() // 接受所有网站的证书
    }

    override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
//        loge("shouldOverrideUrl: ${url}")
        overrideDelegate.shouldOverrideUrlLoadingX5(webView, url)
        startage?.recordeUrlHeight?.invoke(url ?: "")
        return true
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onPageStarted(webView: WebView, s: String?, bitmap: Bitmap?) {

    }


    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    override fun onPageFinished(webView: WebView, url: String?) {

        //恢复图片的加载
        webView.settings.blockNetworkImage = false
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(NormalConstants.JS_REMOVE_ADVERT_FUNCTION)
    }

    /**
     * 这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
     *
     * @param view
     */
    // 旧版本，会在新版本中也可能被调用，所以加上一个判断，防止重复显示
    override fun onReceivedError(
            p0: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
    ) {
        super.onReceivedError(p0, errorCode, description, failingUrl)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return
        }
        loge("errorcode: ${errorCode}", "description: ${description}")
    }

    // 新版本，只会在Android6及以上调用
    override fun onReceivedError(p0: WebView?, p1: WebResourceRequest?, p2: WebResourceError?) {
        super.onReceivedError(p0, p1, p2)
        if (p1?.isForMainFrame() == true) { // 或者： if(request.getUrl().toString() .equals(getUrl()))
            // 在这里显示自定义错误页
            loge("errorcode: ${p2?.errorCode}", "description: ${p2?.description}")
        }
    }
}