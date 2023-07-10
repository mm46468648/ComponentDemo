package com.mooc.common.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.mooc.common.ktextends.loge

/**
 * Webview包装类
 */
class WebviewWrapper @JvmOverloads constructor(var activity: AppCompatActivity):LifecycleObserver {

    private val mWebView by lazy {
        WebPools.getInstance().acquireWebView(activity)
    }

    init {
        activity.lifecycle.addObserver(this)
        setWebView(mWebView)
    }

    fun getWebView() = mWebView
    /**
     * 对webview做一些基本的配置
     * @param mWebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(mWebView: WebView) {
        val settings = mWebView.settings
//        settings.cacheMode = WebSettings.LOAD_NO_CACHE //设置 缓存模式(true);
        settings.setAppCacheEnabled(false)
        settings.setSupportZoom(false)
        settings.useWideViewPort = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.domStorageEnabled = true
        //阻塞图片的加载
        settings.blockNetworkImage = true


        mWebView.webViewClient = MwebViewClient()
        mWebView.webChromeClient = MwebChormeClient()

        //布局参数
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mWebView.layoutParams = layoutParams
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onActivityResume(){
       mWebView.settings.javaScriptEnabled = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onActivityPause(){
        mWebView.settings.javaScriptEnabled = false
    }

    /**
     * 加载链接
     */
    public fun loadUrl(url :String){
        mWebView.loadUrl(url)
    }

    /**
     * 加载html格式的字符串
     */
    public fun loadBaseUrl(htmlStr:String){
        mWebView.loadDataWithBaseURL("", htmlStr, "text/html", "utf-8", null)
    }

    /**
     * 回收webview
     */
    public fun recoveryWebview(){
        WebPools.getInstance().recycle(mWebView)
    }



    var onLoadProgressChange : ((newProgress:Int)->Unit)? = null

    inner class MwebChormeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            onLoadProgressChange?.invoke(newProgress)
        }
    }

    open inner class MwebViewClient : WebViewClient() {
        override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler, sslError: SslError?) {
            handler.proceed() // 接受所有网站的证书
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            if(url.startsWith("http")){
                webView.loadUrl(url)
            }
            return true
        }

        @SuppressLint("SetJavaScriptEnabled")
        override fun onPageStarted(webView: WebView, s: String?, bitmap: Bitmap?) {

        }


        @SuppressLint("SetJavaScriptEnabled")
        override fun onPageFinished(webView: WebView, url: String?) {
            //恢复图片的加载
            webView.settings.blockNetworkImage = false
        }
    }

    /**
     *处理重定向跳转
     */
    private fun handlerOverrideUrlload(view: WebView, url: String): Boolean {
        loge("TAG", "url:$url")
        try {
            if (url.contains("share://") || url.contains("jsbridge:") || url.contains("intent")) {
                return true
            }

            if (url.contains("http://static.moocnd.ykt.io/ucloud/moocnd/app/")
                    || url.contains("https://static.moocnd.ykt.io/ucloud/moocnd/app/")
                    || url.startsWith("alipays://platformapi/startApp")
                    || url.startsWith("https://mclient.alipay.com/h5Continue.htm?")) {
                val uri = Uri.parse(url) //url为你要链接的地址
                val intent = Intent(Intent.ACTION_VIEW, uri)
//                startActivity(intent)
                //            Utils.toResource(ActionWebViewActivity.this,url,strTitle);
                return true
            }
            if (url.startsWith("alipays://platformapi/startApp")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                startActivity(intent)
                return false
            }
            //网页上点击了"继续支付"
            if (url.startsWith("https://mclient.alipay.com/h5Continue.htm?")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                startActivity(intent)
                return false
            }

            if (url.startsWith("http://") || url.startsWith("https://")) {
                if (url.contains("weixin/official/openapp/exampaper")) {
                    return true
                } else if (url.contains("weixin/official/openapp/survey")) {
                    return true
                } else {
                    view.loadUrl(url)
                }
            }
            if (url.contains("emaphone:") && !url.startsWith("http")) {
                // 以下固定写法
                val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse(url))
                intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
            }

        } catch (e: Exception) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
            // 防止没有安装的情况
            e.printStackTrace()
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val content_url = Uri.parse("http://www.icourse163.org/mobile.htm#/mobile")
            intent.data = content_url
//            startActivity(intent)
            return true //没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
        }
        return true
    }
}