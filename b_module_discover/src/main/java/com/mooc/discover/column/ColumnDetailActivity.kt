package com.mooc.discover.column

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mooc.common.ktextends.extraDelegate
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.discover.R
import com.mooc.discover.databinding.ActivityColumnDetailBinding
import com.mooc.discover.model.RecommendContentBean
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
//import kotlinx.android.synthetic.main.activity_column_detail.*
//import kotlinx.android.synthetic.main.item_column_graph_new.*

/**
 * 单个条目的专栏详情
 */
@Route(path = Paths.PAGE_COLUMN_DETAIL)
class ColumnDetailActivity : BaseActivity() {


    private val columnDetailStr: String by extraDelegate(IntentParamsConstants.INTENT_COLUMN_DATA, "")
    private lateinit var inflater: ActivityColumnDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivityColumnDetailBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        inflater.commonTitle.setOnLeftClickListener { finish() }
//        LogUtil.addLoadLog(LogPageConstants.PID_COLUMN_DETAIL)
        initData()
    }

    private fun initData() {
        val gson = Gson()
        val columnDetail = gson.fromJson(columnDetailStr, RecommendContentBean.DataBean::class.java)
        columnDetail?.apply {
            inflater.graphNewItem.tvTitle.text = this.title
            inflater.graphNewItem.tvTime.text = this.publish_time

            if (this.is_read) {
                inflater.graphNewItem.tvTitle.setTextColor(this@ColumnDetailActivity.resources.getColor(R.color.color_9))
            } else {
                inflater.graphNewItem.tvTitle.setTextColor(this@ColumnDetailActivity.resources.getColor(R.color.color_3))
            }

            inflater.graphNewItem.llComplete.setOnClickListener {
                //跳转资源详情
                ResourceTurnManager.turnToResourcePage(this)
            }
            //如果视频信息不为空，显示视频控件
            if (this.video_data != null && this.video_data.video_link.isNotEmpty()) {
                inflater.graphNewItem.webVideo.visibility = View.VISIBLE
                inflater.graphNewItem.tvVideoSource.visibility = View.VISIBLE
                inflater.graphNewItem.ivImg.visibility = View.GONE

                setWebView(inflater.graphNewItem.webVideo)
                //取出视频播放地址
                //eg: "<iframe frameborder=\"0\" width=100% height=\"546\" src=\"https://v.qq.com/iframe/player.html?vid=d0389uyr07l&tiny=0&auto=0\" allowfullscreen=\"true\"></iframe>";
                val html = " <title>+" + this.title + "</title>" + this.video_data.video_link
                inflater.graphNewItem.webVideo.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            } else {//否则显示其他
                inflater.graphNewItem.webVideo.visibility = View.GONE
                inflater.graphNewItem.tvVideoSource.visibility = View.GONE

                if (this.big_image.isNotEmpty()) {
                    Glide.with(this@ColumnDetailActivity).load(this.big_image).into(inflater.graphNewItem.ivImg)
                    inflater.graphNewItem.ivImg.visibility = View.VISIBLE
                } else {
                    inflater.graphNewItem.ivImg.visibility = View.GONE
                }

            }

            setWebView(inflater.graphNewItem.webDesc)
            //展示描述（富文本类型）
            var detail: String = this.detail
            if (!TextUtils.isEmpty(detail)) {
                inflater.graphNewItem.webDesc.visibility = View.VISIBLE
                detail = HtmlUtils.getFormatHtml(detail)
                inflater.graphNewItem.webDesc.loadDataWithBaseURL(null, detail, "text/html", "utf-8", null)
            } else {
                inflater.graphNewItem.webDesc.visibility = View.GONE
            }

            if (this.link.isNotEmpty()) {
                //是否有外链，展示查看详情
                inflater.graphNewItem.tvLookDetail.visibility = View.VISIBLE
            } else {
                inflater.graphNewItem.tvLookDetail.visibility = View.GONE
            }

        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun setWebView(webView: WebView) {
        webView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS)
        val webSettings: WebSettings = webView.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true
        if (false) {
            //缩放操作
            webSettings.setSupportZoom(true)
            webSettings.builtInZoomControls = true
            webSettings.displayZoomControls = false
        }
        //其他细节操作
        webSettings.layoutAlgorithm =
                WebSettings.LayoutAlgorithm.NARROW_COLUMNS //这句话将图片缩放至屏幕宽度，非常好嘛

        webSettings.cacheMode = android.webkit.WebSettings.LOAD_NORMAL
        webSettings.allowFileAccess = true //设置可以访问文件

        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.pluginsEnabled = true
        webSettings.pluginState = WebSettings.PluginState.ON
//        webView.addJavascriptInterface(JsAnnotation(context as Activity), "mobile")
        webView.setWebChromeClient(WebChromeClient())
        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(webView: WebView, s: String) {
                super.onPageFinished(webView, s)
//                com.moocxuetang.util.Utils.lp = webView.layoutParams
//                com.moocxuetang.util.Utils.lp.height =
//                    com.moocxuetang.util.Utils.getHtmlHeight(html)
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setWebView(webView: android.webkit.WebView) {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        if (false) {
            //缩放操作
            webSettings.setSupportZoom(true)
            webSettings.builtInZoomControls = false
            webSettings.displayZoomControls = false
        }
        webSettings.domStorageEnabled = true
        //其他细节操作
        webSettings.layoutAlgorithm =
                android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN //这句话将图片缩放至屏幕宽度，非常好嘛
        webSettings.cacheMode = android.webkit.WebSettings.LOAD_NORMAL
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        webSettings.defaultTextEncodingName = "utf-8"
//        webView.addJavascriptInterface(JsAnnotation(context as Activity?), "mobile")
        webSettings.pluginState = android.webkit.WebSettings.PluginState.ON
        webView.setWebChromeClient(android.webkit.WebChromeClient())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

}