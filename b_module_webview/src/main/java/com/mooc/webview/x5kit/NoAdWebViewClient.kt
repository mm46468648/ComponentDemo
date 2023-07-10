package com.mooc.webview.x5kit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**

 * @Author limeng
 * @Date 2021/1/4-5:05 PM
 */
class NoAdWebViewClient(var activity: Activity) : WebViewClient() {
    override fun onReceivedSslError(webView: WebView, handler: SslErrorHandler, p2: com.tencent.smtt.export.external.interfaces.SslError?) {
        handler.proceed() // 接受所有网站的证书
    }

    override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
        return setUrlLoading(webView, url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onPageStarted(webView: WebView, s: String?, bitmap: Bitmap?) {

    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun onPageFinished(webView: WebView, url: String?) {
        //恢复图片的加载
        webView.settings.blockNetworkImage = false
    }
    private fun setUrlLoading(view: WebView, url: String): Boolean {
        if (url.contains("share://") || url.contains("jsbridge:") || url.contains("intent")) {
            return true
        }
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                view.loadUrl(url)
                //            }
            } else {
                //打开合作方 app
                return if (url.contains("emaphone:") && !url.startsWith("http")) {
                    try {
                        // 以下固定写法
                        val intent = Intent(Intent.ACTION_VIEW,
                                Uri.parse(url))
                        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        activity. startActivity(intent)
                    } catch (e: Exception) {
                        // 防止没有安装的情况
                        e.printStackTrace()
                        val intent = Intent()
                        intent.action = "android.intent.action.VIEW"
                        val content_url = Uri.parse("http://www.icourse163.org/mobile.htm#/mobile")
                        intent.data = content_url
                        activity.startActivity(intent)
                    }
                    true
                } else {

                    //                    }
                    true
                }
            }
        } catch (e: Exception) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
            return true //没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
        }
        return true
    }
}