package com.mooc.webview.external

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.webview.R
import com.mooc.webview.WebViewWrapper
import com.mooc.webview.WebviewApplication
import com.mooc.webview.databinding.WebviewActivityWebviewBinding
import com.mooc.webview.util.ReLocationUrlManager
import com.mooc.webview.viewmodel.ResourceWebViewModel
import com.tencent.smtt.sdk.WebView

@Route(path = Paths.PAGE_WEB_EXTERNAL_WEB)
open class ExternalLikeWebViewActivity : BaseActivity() {

    val commonViewModel: ResourceWebViewModel by viewModels()


    val loadUrl by extraDelegate(IntentParamsConstants.WEB_PARAMS_URL, "")
    val title by extraDelegate(IntentParamsConstants.WEB_PARAMS_TITLE, "")

    var weChatTitle: String? = null;

    val resourceType by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_TYPE, -1)
    val resourceID by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_ID, "")


    val webviewWrapper by lazy { WebViewWrapper(this, userX5()) }

    open fun userX5(): Boolean = WebviewApplication.x5InitFinish

    lateinit var inflater: WebviewActivityWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ReLocationUrlManager.clearList()
        inflater = WebviewActivityWebviewBinding.inflate(layoutInflater)
        setContentView(inflater.root)


        loge("是否使用的X5${userX5()}")

        //将包装类中webview添加到布局
        getContentView()

        getUserPermission()

        //设置返回健
        inflater.commonTitleLayout.setOnLeftClickListener {
            onPressBack()
        }
        //设置标题
        inflater.commonTitleLayout.middle_text = title

        //设置网页链接
        loadUrl()

        obseverLiveData()

    }


    @Suppress("COMPATIBILITY_WARNING")
    private fun obseverLiveData() {
        commonViewModel.uploadArticleLiveData.observe(this) {
            if (it.data?.title != null) {
                toast("文章成功导入cms后台管理！")

            } else {
                toast("文章已存在！")
            }

        }

        commonViewModel.titleLiveData.observe(this) {
            weChatTitle = it
            inflater.commonTitleLayout.middle_text = it
        }


        commonViewModel.userPermissionLiveData.observe(this) {
            if (it.is_article) {
                inflater.commonTitleLayout.setRightFirstIconRes(R.mipmap.common_iv_upload)
                inflater.commonTitleLayout.setOnRightIconClickListener {
                    weChatTitle?.let { it1 -> uploadArticelLink(it1) }
                }

            }
        }
    }

    fun getUserPermission() {
        commonViewModel.getUserPermission(loadUrl);
    }

    fun uploadArticelLink(title: String) {
        commonViewModel.uploadArticleLink(title, loadUrl);
    }


    /**
     * 方便子类添加自定义的布局
     */
    protected open fun getContentView() {
        val mWebView = webviewWrapper.getView()
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mWebView.layoutParams = layoutParams
        inflater.flRoot.addView(mWebView, 0)

        //监听加载进度
        webviewWrapper.onLoadProgressChange = { newProgress ->
            inflater.progressBar.progress = newProgress
            if (newProgress >= 99) {
                inflater.progressBar.visibility = View.GONE
                loadPreviewImageAndFilterWxLinkJs()
            } else {
                if (inflater.progressBar.visibility == View.GONE) {
                    inflater.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }


    var hasAddJs = false

    /**
     * 注入图片预览和
     * 去除微信的链接js
     */
    fun loadPreviewImageAndFilterWxLinkJs() {
        //只有文章和专栏文章注入查看大图的方法
//        if (resourceType == ResourceTypeConstans.TYPE_ARTICLE
//            || resourceType == ResourceTypeConstans.TYPE_COLUMN_ARTICLE
//        ) {
//            if (hasAddJs) return
//            hasAddJs = true
        //加载完注入一个js方法
//            runOnMainDelayed(2000) {
//                webviewWrapper.strategy.loadUrl(NormalConstants.JS_FUNCTION)
//            }
//        }
    }

    open fun getLayoutId() = R.layout.webview_activity_webview


    @SuppressLint("CheckResult")
    open fun loadUrl() {
        if (loadUrl.isNotEmpty()) {
            webviewWrapper.loadUrl(loadUrl)
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onPressBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    /**
     * 点击返回事件
     */
    open fun onPressBack() {
        val view = webviewWrapper.getView()

        //如果是视频横屏，先旋转屏幕
        if (webviewWrapper.strategy.getFullScreenState()) {
            return
        }

        if (view is WebView) {     //判断是否能返回
            if (view.canGoBack()) {
                if (onRelocation()) {
                    view.goBack()
                }
                view.goBack()
                return
            }
        }

        if (view is android.webkit.WebView) {
            if (view.canGoBack()) {
                if (onRelocation()) {
                    view.goBack()
                }
                view.goBack()
                return
            }
        }

        onBackPressed()
    }

    //是否需要处理重定向
    fun onRelocation(): Boolean {
        var contain = false
        if (ReLocationUrlManager.historyUrl.size > 0) {
            ReLocationUrlManager.filterList.forEach { filter ->
                val iterator = ReLocationUrlManager.historyUrl.iterator()
                while (iterator.hasNext()) {
                    val item: String = iterator.next()
                    if (item.contains(filter)) {
                        contain = true
                        iterator.remove()
                    }
                }
                if (contain) {
                    //加载重定向之前的页
                    return true
                }
            }
        }
        return contain
    }

    override fun onDestroy() {
        try {
            ReLocationUrlManager.clearList()
            webviewWrapper.release()
        } catch (e: Exception) {
            loge("X5WebViewActivity", e.toString())
        }
        super.onDestroy()

    }


}