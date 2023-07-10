package com.mooc.webview.x5kit

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.permission.PermissionRequestCallback
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.webview.stratage.WebViewInitStrategy
import com.mooc.webview.stratage.X5WebkitStrategy
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

class X5WebKitChormeClient(var startage: WebViewInitStrategy? = null) : WebChromeClient() {

    //视频横屏相关Start
    var repleaceWeb: View? = null
    var parentView: ViewGroup? = null
    var customViewCallback: IX5WebChromeClient.CustomViewCallback? = null
    //视频横屏相关End

    //上传文件相关Start
    private var uploadFilesCallback: ValueCallback<Array<Uri>>? = null

    private var uploadFile //定义接受返回值
            : android.webkit.ValueCallback<Uri>? = null


    /**
     * 视频横屏
     */
    override fun onShowCustomView(p0: View?, p1: IX5WebChromeClient.CustomViewCallback?) {

        startage?.apply {
            customViewCallback = p1
            parentView = scanForActivity((startage as X5WebkitStrategy).context)?.getWindow()
                    ?.getDecorView() as ViewGroup

//            parentView = this.getView() as ViewGroup
            this.getView().visibility = View.GONE
            parentView?.addView(p0)
            repleaceWeb = p0

            //设置横屏
            setOrientation(true)
            (startage as X5WebkitStrategy).videoLand = true
            //设置横屏主题
        }
    }

    fun setOrientation(landScape: Boolean) {
        val orientation =
                if (landScape) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        startage?.scanForActivity((startage as X5WebkitStrategy).context)?.requestedOrientation = orientation
    }


    /**
     * 取消视频横屏
     */
    override fun onHideCustomView() {
        parentView?.apply {
            this.removeView(repleaceWeb)
            this@X5WebKitChormeClient.startage?.getView()?.visibility = View.VISIBLE
        }
        customViewCallback?.onCustomViewHidden()
        setOrientation(false)
        (startage as X5WebkitStrategy).videoLand = false
    }

    /**
     * 加载进度
     */
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)

        startage?.onLoadProgressChange?.invoke(newProgress)
    }

    // For Android  > 4.1.1
    override fun openFileChooser(valueCallback: ValueCallback<Uri?>?, s: String?, s1: String?) {
        this.uploadFile = uploadFile
        openFileChooseProcess()
    }

    // For Android  >= 5.0
    //        @Override
    /**
     * 文件选择
     */
    override fun onShowFileChooser(
            p0: WebView?, p1: ValueCallback<Array<Uri>>?, p2: FileChooserParams?
    ): Boolean {
        loge("chooseParams: ${p2.toString()}", "acceptType ${p2?.acceptTypes}")

        uploadFilesCallback = p1
        startage?.openFileChooseProcess((startage as X5WebkitStrategy).context)
        return true
    }


    /**
     * 权限申请
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun onPermissionRequest(request: PermissionRequest) {
        request.grant(request.resources)
//        request.resources.forEach {
//            if (it == Manifest.permission.RECORD_AUDIO) {
//                PermissionApplyActivity.launchActivity(
//                        (startage as X5WebkitStrategy).context, arrayOf(Manifest.permission.RECORD_AUDIO), 0,
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

    /**
     * 打开文件选择
     * 需要在使用的Activity中实现onActivityResoult
     */
    private fun openFileChooseProcess() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        startage?.scanForActivity((startage as X5WebkitStrategy).context)?.startActivityForResult(
                Intent.createChooser(i, "Choose"), WebViewInitStrategy.CHOOSE_FILE_REQUEST_CODE
        )
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == WebViewInitStrategy.CHOOSE_FILE_REQUEST_CODE) {

                if (null != uploadFile) {
                    val result =
                            if (data == null || resultCode != Activity.RESULT_OK) null else data.data
                    uploadFile?.onReceiveValue(result)
                    uploadFile = null
                }

                if (null != uploadFilesCallback) {
                    data?.data?.let {
                        uploadFilesCallback?.onReceiveValue(arrayOf(it))
                    }
                    uploadFilesCallback = null
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (null != uploadFile) {
                uploadFile?.onReceiveValue(null)
                uploadFile = null
            }
            if (null != uploadFilesCallback) {
                uploadFilesCallback?.onReceiveValue(null)
                uploadFilesCallback = null
            }

        }
    }
}