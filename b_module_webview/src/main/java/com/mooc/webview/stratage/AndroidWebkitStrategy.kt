package com.mooc.webview.stratage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.MutableContextWrapper
import android.view.MotionEvent
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import com.mooc.webview.andriodkit.AndroidKitQMUIWebViewClient
import com.mooc.webview.andriodkit.AndroidKitWebChormeClient
import com.mooc.webview.andriodkit.AndroidKitWebView
import com.mooc.webview.andriodkit.AndroidKitWebViewClient
import com.mooc.webview.business.CustomLongClickPresenter
import com.mooc.webview.interfaces.ActionSelectInterface
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedTopWebView
import org.json.JSONObject

/**
 * 安卓系统内核的初始化方法
 */
class AndroidWebkitStrategy(
    var context: Context,
    var mView: AndroidKitWebView = AndroidKitWebView(
        MutableContextWrapper(context)
    )
) : WebViewInitStrategy {


    override var onLoadProgressChange: ((newProgress: Int) -> Unit)? = null

    override var recordeUrlHeight: ((url: String) -> Unit)? = null

    private var androidKitWebChormeClient: AndroidKitWebChormeClient? = null


    override fun initWebView() {
        setWebView(mView)
    }

    override fun getView(): View {
        return mView
    }

    @SuppressLint("JavascriptInterface")
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
        mView.dispatchTouchEvent(me)
    }

    override fun openCustomLongTextPop(json: JSONObject) {
        //添加一个javaScript接口，接收点击长按回调事件
        mView.mCustomLongClickPresenter = CustomLongClickPresenter(context, this)
        mView.addJavascriptInterface(ActionSelectInterface(context, json), "ActionSelectInterface")
    }

    override fun getContentHeight(): Int {
        return (mView.contentHeight * mView.scale).toInt()
    }

    override fun goBack() {

    }

    override fun release() {
        mView.stopLoading()
        mView.destroy()
    }

    var videoLand = false
    override fun getFullScreenState(): Boolean {
        return videoLand
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        androidKitWebChormeClient?.onActivityResult(requestCode, resultCode, data)

    }


    /**
     * 对webview做一些基本的配置
     * @param mWebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(mWebView: WebView) {
        val settings = mWebView.settings
//        settings.cacheMode = settings.LOAD_NO_CACHE //设置 缓存模式(true);
        settings.setAppCacheEnabled(false)
        settings.setSupportZoom(false)
        settings.useWideViewPort = true
        settings.domStorageEnabled = true
        //阻塞图片的加载
        settings.blockNetworkImage = true
        //去掉滚动条
        mWebView.isHorizontalScrollBarEnabled = false;//水平不显示
        mWebView.isVerticalScrollBarEnabled = false; //垂直不显示

        settings.loadWithOverviewMode = true
        //其他细节操作
        settings.allowFileAccess = true //设置可以访问文件
        settings.defaultTextEncodingName = "utf-8"
        settings.javaScriptEnabled = true
        settings.userAgentString = settings.userAgentString + "; App/com.moocxuetang"
        settings.pluginState = WebSettings.PluginState.ON_DEMAND
        //settings.cacheMode=WebSettings.LOAD_DEFAULT

        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);

        if (mWebView is QMUIContinuousNestedTopWebView) {
            mWebView.webViewClient = AndroidKitQMUIWebViewClient();
        } else {
            mWebView.webViewClient = AndroidKitWebViewClient(this)
        }

        androidKitWebChormeClient = AndroidKitWebChormeClient(this)
        mWebView.webChromeClient = androidKitWebChormeClient


    }

}