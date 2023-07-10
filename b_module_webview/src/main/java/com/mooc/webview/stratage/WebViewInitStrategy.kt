package com.mooc.webview.stratage

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import org.json.JSONObject

/**
 * webView初始化策略
 */
interface WebViewInitStrategy {

    companion object {
        val CHOOSE_FILE_REQUEST_CODE = 0x9001
    }

    //加载进度回调
    var onLoadProgressChange: ((newProgress: Int) -> Unit)?

    var recordeUrlHeight: ((url: String) -> Unit)?


    fun initWebView()

    fun getView(): View

    fun addJavascriptInterface(`object`: Any, name: String)

    fun loadUrl(url: String)

    fun reload()

    fun loadDataWithBaseURL(txt: String)

    fun setJavaScriptEnable(b: Boolean)

    fun evaluateJavascript(s: String, nothing: Nothing?)

    fun dispatchTouchEvent(me: MotionEvent?)

    fun openCustomLongTextPop(json: JSONObject)

    fun getContentHeight(): Int

    fun goBack();

    fun release()
//    fun setNoteJsonBean(json:JSONObject)
    /**
     * 是否全屏播放状态
     */
    fun getFullScreenState(): Boolean


    /**
     * 上传文件回调
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    /**
     * 打开文件选择
     * 需要在使用的Activity中实现onActivityResoult
     * 展示所有文件
     */
    fun openFileChooseProcess(context: Context) {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "*/*"
        scanForActivity(context)?.startActivityForResult(
                Intent.createChooser(i, "Choose"), WebViewInitStrategy.CHOOSE_FILE_REQUEST_CODE
        )
    }

    fun scanForActivity(context: Context): Activity? {
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }

}