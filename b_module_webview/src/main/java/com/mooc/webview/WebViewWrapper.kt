package com.mooc.webview

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.mooc.commonbusiness.module.web.MobileWebInterface
import com.mooc.webview.business.BaseResourceWebviewActivity
//import com.moocxuetang.interfaces.BitImagePreviewInterface
import com.mooc.webview.stratage.AndroidWebkitStrategy
import com.mooc.webview.stratage.X5WebkitStrategy
import com.tencent.smtt.sdk.WebView
import org.json.JSONObject

class WebViewWrapper constructor(var mContext: Context,userX5:Boolean = WebviewApplication.x5InitFinish) : LifecycleObserver {

    //初始化策略
    var strategy = if (userX5) X5WebkitStrategy(mContext) else AndroidWebkitStrategy(mContext)
        set(value) {
            field = value
        }

    //加载进度回调
    var onLoadProgressChange: ((newProgress: Int) -> Unit)? = null
        set(value) {
            strategy.onLoadProgressChange = value
            field = value
        }

    init {
        _init()
    }

    fun _init(){
        //初始化相应的weView
        strategy.initWebView()
        //如果是Lifecycle的子类（activity 或者 fragment）
        if (mContext is LifecycleOwner) {
            (mContext as LifecycleOwner).lifecycle.addObserver(this)
        }

        //注入js方法
        if (mContext is Activity) {
            strategy.addJavascriptInterface(MobileWebInterface(mContext as Activity), "mobile")
        }

        //设置自定义长按事件
        if (mContext is BaseResourceWebviewActivity) {
            val baseResourceWebviewActivity = mContext as BaseResourceWebviewActivity
            val json = JSONObject()
            json.put("url", baseResourceWebviewActivity.loadUrl)
            json.put("other_resource_id", baseResourceWebviewActivity.resourceID)
            json.put("source_resource_type", baseResourceWebviewActivity.resourceType)
            json.put("title", baseResourceWebviewActivity.title)
            strategy.openCustomLongTextPop(json)
        }
    }

    fun openCustomLong(url: String, resourceId: String, type: Int, title: String) {
        val json = JSONObject()
        json.put("url", url)
        json.put("other_resource_id", resourceId)
        json.put("source_resource_type", type)
        json.put("title", title)
        strategy.openCustomLongTextPop(json)
    }

    /**
     * 代理url加载
     */
    fun loadUrl(url: String) {
        strategy.loadUrl(url)
    }

    fun reload(){
        strategy.reload()
    }

    /**
     * 代理 加载富文本
     */
    fun loadDataWithBaseURL(text: String) {
        strategy.loadDataWithBaseURL(text)
    }

    /**
     * 获取weviewview
     */
    fun getView() = strategy.getView()

    fun getContentHeight() = strategy.getContentHeight()

    /**
     * 可见时设置js enable
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onUiResume() {
        strategy.setJavaScriptEnable(true)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onUiPause() {
//        strategy.setJavaScriptEnable(false)
    }

    fun release() {
        strategy.release()
    }
}