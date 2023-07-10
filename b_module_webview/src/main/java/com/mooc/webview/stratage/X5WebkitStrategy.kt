package com.mooc.webview.stratage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.mooc.webview.business.CustomLongClickPresenter
import com.mooc.webview.interfaces.ActionSelectInterface
import com.mooc.webview.x5kit.X5WebKitChormeClient
import com.mooc.webview.x5kit.X5WebKitClient
import com.mooc.webview.x5kit.X5kitWebView
import com.tencent.smtt.sdk.CookieSyncManager
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import org.json.JSONObject


class X5WebkitStrategy(var context: Context) : WebViewInitStrategy {

    val mView = X5kitWebView(context)
    private var x5WebKitChormeClient: X5WebKitChormeClient? = null

    //    val mView = WebView(context)
    override var onLoadProgressChange: ((newProgress: Int) -> Unit)? = null
    var onItemClickListener: ((json: JSONObject) -> Unit)? = null


    override var recordeUrlHeight: ((url: String) -> Unit)? = null


    override fun initWebView() {
        setWebView(mView)
    }

    override fun getView(): View {
        return mView
    }

    override fun addJavascriptInterface(`object`: Any, name: String) {
        mView.addJavascriptInterface(`object`, name)
    }

    override fun loadUrl(url: String) {
        mView.loadUrl(url)
    }

    override fun reload() {
        mView.reload()
    }

    override fun loadDataWithBaseURL(txt: String) {
        mView.loadDataWithBaseURL(null, txt, "text/html", "UTF-8", null)
    }

    override fun setJavaScriptEnable(b: Boolean) {
        mView.settings.javaScriptEnabled = b
    }

    override fun evaluateJavascript(s: String, nothing: Nothing?) {
        mView.evaluateJavascript(s, nothing)
    }

    override fun dispatchTouchEvent(me: MotionEvent?) {
        (mView as ViewGroup).dispatchTouchEvent(me)
    }

    override fun openCustomLongTextPop(json: JSONObject) {
        val mCustomLongClickPresenter = CustomLongClickPresenter(context, this)
        val actionSelectInterface = ActionSelectInterface(context, json) {
            mView.postDelayed(Runnable { mCustomLongClickPresenter.performClickEvent() }, 200)
        }
        mView.addJavascriptInterface(actionSelectInterface, "ActionSelectInterface")
        mView.setOnLongClickListener {
            mView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    mCustomLongClickPresenter.x5Webview = mView
                    mCustomLongClickPresenter.foreachView(mView)
                }
            })
            false
        }

    }

    override fun getContentHeight(): Int {
        return (mView.contentHeight * mView.scale).toInt()
    }

    override fun goBack() {

    }


    /**
     * 对webView做一些基本的配置
     * @param mWebView
     */
    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(mWebView: WebView) {
        val settings = mWebView.settings
//        settings.cacheMode = settingss.LOAD_NO_CACHE //设置 缓存模式(true);
        settings.useWideViewPort = true
        settings.domStorageEnabled = true
        //阻塞图片的加载
        settings.blockNetworkImage = true
        //去掉滚动条
        mWebView.isHorizontalScrollBarEnabled = false;//水平不显示
        mWebView.isVerticalScrollBarEnabled = false; //垂直不显示

        settings.allowFileAccess = true //设置可以访问文件
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.useWideViewPort = true
        //设置可以访问文件
        settings.setAllowFileAccess(true);
        settings.setSupportMultipleWindows(false)
        settings.setAppCacheEnabled(true)
        settings.domStorageEnabled = true
        settings.setGeolocationEnabled(true)
        settings.savePassword = false
        settings.setAppCacheMaxSize(Long.MAX_VALUE)
        settings.setAppCachePath(context.getDir("appcache", 0).path)
        settings.databasePath = context.getDir("databases", 0).path
        settings.setGeolocationDatabasePath(context.getDir("geolocation", 0).path)
        settings.pluginState = WebSettings.PluginState.ON_DEMAND
        CookieSyncManager.createInstance(context)
        CookieSyncManager.getInstance().sync()
        settings.cacheMode = WebSettings.LOAD_NORMAL
        settings.userAgentString = settings.userAgentString + "; App/com.moocxuetang"
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.loadsImagesAutomatically = true
        settings.defaultTextEncodingName = "utf-8"
//        settings.cacheMode=WebSettings.LOAD_DEFAULT

//        if (mWebView.getX5WebViewExtension() != null) {
////            Toast.makeText(this, "页面内全屏播放模式", Toast.LENGTH_LONG).show()
//            val data = Bundle()
//            data.putBoolean("standardFullScreen", true) // true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
//            data.putBoolean("supportLiteWnd", false) // false：关闭小窗；true：开启小窗；不设置默认true，
//            data.putInt("DefaultVideoScreen", 1) // 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
//            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data)
//        }
        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        mWebView.webViewClient = X5WebKitClient(this)

        x5WebKitChormeClient = X5WebKitChormeClient(this)
        mWebView.webChromeClient = x5WebKitChormeClient
//        mWebView.webChromeClientExtension = X5WebKitChromeClientExtension(this)
    }

    override fun release() {
        if (mView != null) {
            mView.stopLoading()
            mView.destroy()
        }
    }

    var videoLand = false //视频横屏
    override fun getFullScreenState(): Boolean {
        return videoLand
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        x5WebKitChormeClient?.onActivityResult(requestCode, resultCode, data)
    }
}