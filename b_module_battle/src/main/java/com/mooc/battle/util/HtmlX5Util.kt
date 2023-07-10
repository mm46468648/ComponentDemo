package com.mooc.battle.util

import android.annotation.SuppressLint
import android.content.Context
import com.mooc.commonbusiness.utils.UserAgentUtils
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

object HtmlX5Util {

    /**
     * 格式化html
     */
    fun getFormatHtml(detail: String): String {
        return """<html>
                        <head>
                        <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                        </head>
                        <style>
                        body {
                            margin: 0;
                            padding: 5px;
                            box-sizing: border-box;
                        }
                        img,video,table,iframe{
                            max-width:100%!important;
                            height:auto!important;
                            display: block;
                        }
                        ul {
                            max-width: 90% !important;
                        }
                        code {
                             display: block;
                             word-wrap: break-word;
                             word-break: break-all;
                             max-width: 100% !important;
                             overflow-x:scroll;
                        }
                            
                        .code-snippet_outer,
                        .code-snippet__string {
                            max-width: 100%;
                            word-wrap: break-word;
                            word-break: break-all;
                            display: inherit;
                        }
                        section,div {
                             width: inherit !important;
                        }
                        </style>
                        <body style="word-break: break-all;text-align: justify;font-size: 14px; line-height: 1.5; color: #666666;">
                        $detail </body>
                    </html>"""
    }

    fun getReplaceHtml(detail: String): String {
        return """<html>
                        <head>
                        <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                        </head>
                        <style>
                        img,video,table,iframe{
                            max-width:100%!important;
                            height:auto!important;
                        }
                        </style>
                        <body style="word-break: break-all;text-align: justify; font-size: 14px; line-height: 1.5; color: #666666;">
                        $detail </body>
                        </html>"""
    }

    /**
     * @param detail  内容
     * @param color   颜色
     * @param spValue 字体大小
     */
    fun getReplaceHtml(detail: String, color: String, spValue: String): String {
        return """<html>
                        <head>
                        <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                        </head>
                        <style>
                        img,video,table,iframe{
                            max-width:100%!important;
                            height:auto!important;
                        }
                        </style>
                        <body style="word-break: break-all;text-align: justify; line-height: 1.5;font-size:$spValue; color: $color;">
                        $detail </body>
                        </html>"""
    }

    /**
     * 文字增加标签
     * detail 内容
     * marginTop  上部距离
     * color 颜色
     * spValue  字体大小
     */
    fun getAddLabel(detail: String, color: String, spValue: String): String {
        return """
             <p style="text-align: center;font-size: $spValue; line-height: 1.5; color: $color;">
             $detail</p>
             """
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    fun setWebView(webView: WebView, context: Context) {
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        //缩放操作
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = false
        webSettings.displayZoomControls = false
        //其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_NORMAL
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.savePassword = false
        webSettings.allowFileAccess = false
        // 修改ua使得web端正确判断
        val ua: String = webSettings.userAgentString
//        webSettings.userAgentString = ua + SystemUtil.getInstance().userAgent
        webSettings.userAgentString = ua + UserAgentUtils.getUserAgent()

//        webView.addJavascriptInterface(JsAnnotation(context as Activity), "mobile")
        webView.webViewClient = NoAdWebViewClient()
        webView.webChromeClient = WebChromeClient()
        setWebViewRemove(webView)

    }

    private fun setWebViewRemove(webView: WebView) {
        webView.removeJavascriptInterface("searchBoxJavaBridge_")
        webView.removeJavascriptInterface("accessibility")
        webView.removeJavascriptInterface("accessibilityTraversal")
    }

    private class NoAdWebViewClient : WebViewClient() {
        override fun onReceivedSslError(p0: WebView, p1: SslErrorHandler, p2: SslError) {
            p1.proceed() // 接受所有网站的证书
        }

        override fun shouldOverrideUrlLoading(p0: WebView, p1: String): Boolean {
            return setUrlLoading(p0, p1)
        }

        override fun shouldOverrideUrlLoading(p0: WebView, p1: WebResourceRequest): Boolean {
            return setUrlLoading(p0, p1.url.toString())
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Suppress("DEPRECATION")
        override fun onPageFinished(p0: WebView, p1: String?) {
            //开启js
            p0.settings.javaScriptEnabled = true
        }

    }


    fun setUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            view.loadUrl(url)
        }
        return true
    }
}