package com.mooc.webview.andriodkit

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import com.mooc.common.utils.permission.PermissionRequestCallback
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.webview.stratage.AndroidWebkitStrategy
import com.mooc.webview.stratage.WebViewInitStrategy

class AndroidKitWebChormeClient(var startage: WebViewInitStrategy? = null) : WebChromeClient() {

    var repleaceWeb: ViewGroup? = null
    var parentView: ViewGroup? = null
    var customViewCallback: CustomViewCallback? = null


    private var uploadFilesCallback: ValueCallback<Array<Uri>>? = null

    /**
     * 视频横屏
     */
    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {

        startage?.apply {
            customViewCallback = callback

            parentView = scanForActivity((startage as AndroidWebkitStrategy).context)?.getWindow()?.getDecorView() as ViewGroup

//            parentView = this.getView() as ViewGroup
            this.getView().visibility = View.GONE
//            parentView?.addView(view)

            //设置黑色背景
            repleaceWeb = FrameLayout((startage as AndroidWebkitStrategy).context)
            repleaceWeb?.setBackgroundColor(Color.BLACK)
            repleaceWeb?.addView(view)
            parentView?.addView(repleaceWeb)
//            repleaceWeb = view

            //设置横屏
            setOrientation(true)
            (startage as AndroidWebkitStrategy).videoLand = true
            //设置横屏主题
        }
    }

    fun setOrientation(landScape: Boolean) {
        val orientation = if (landScape) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        startage?.scanForActivity((startage as AndroidWebkitStrategy).context)?.requestedOrientation = orientation
    }

    /**
     * 取消视频横屏
     */
    override fun onHideCustomView() {
        parentView?.apply {
            this.removeView(repleaceWeb)
            repleaceWeb = null
            this@AndroidKitWebChormeClient.startage?.getView()?.visibility = View.VISIBLE
        }
        customViewCallback?.onCustomViewHidden()
        setOrientation(false)
        (startage as AndroidWebkitStrategy).videoLand = false
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)

        startage?.onLoadProgressChange?.invoke(newProgress)
    }

    /**
     * 权限申请
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPermissionRequest(request: PermissionRequest) {
        request.grant(request.resources)
//        request.resources.forEach {
//            if (it == Manifest.permission.RECORD_AUDIO) {
//                PermissionApplyActivity.launchActivity(
//                        (startage as AndroidWebkitStrategy).context, arrayOf(Manifest.permission.RECORD_AUDIO), 0,
//                        object : PermissionRequestCallback {
//                            override fun permissionSuccess() {
//
//                            }
//
//                            override fun permissionCanceled() {
//                            }
//
//                            override fun permissionDenied() {
//
//                            }
//                        })
//            }
//        }
    }

    override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
    ): Boolean {

        uploadFilesCallback = filePathCallback
        startage?.openFileChooseProcess((startage as AndroidWebkitStrategy).context)
        return true
    }


    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("requestCode===", "$requestCode====")
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == WebViewInitStrategy.CHOOSE_FILE_REQUEST_CODE) {
                if (null != uploadFilesCallback) {
                    data?.data?.let {
                        uploadFilesCallback?.onReceiveValue(arrayOf(it))
                    }
                    uploadFilesCallback = null
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (null != uploadFilesCallback) {
                uploadFilesCallback?.onReceiveValue(null)
                uploadFilesCallback = null
            }

        }
    }
}