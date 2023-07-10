package com.mooc.discover.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.discover.R
import com.mooc.discover.model.RecommendContentBean


/**
 * 专栏列表适配器
 */
class ColumnListAdapter(list: ArrayList<RecommendContentBean.DataBean>)
    : BaseQuickAdapter<RecommendContentBean.DataBean, BaseViewHolder>(
        R.layout.item_column_graph_new, list

), LoadMoreModule {
    var simpleMode = false  //简洁模式
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    var listener: OnItemClickListener? = null;
    override fun convert(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {

        holder.setGone(R.id.llSimple, !simpleMode)
        holder.setGone(R.id.llComplete, simpleMode)

        holder.setGone(R.id.tvVideoSource, true)

        if (item.is_read()) {
            holder.setTextColor(R.id.tvTitle, context.resources.getColor(R.color.color_9));
        } else {
            holder.setTextColor(R.id.tvTitle, context.resources.getColor(R.color.color_3));
        }

        if (simpleMode) {   //只显示时间和标题

            holder.setText(R.id.tvTitleSimple, item.getTitle())
            holder.setText(R.id.tvTimeSimple, item.getPublish_time())

        } else {    //显示所有信息
            holder.setText(R.id.tvTitle, item.title)
            holder.setText(R.id.tvTime, item.getPublish_time())


            //如果视频信息不为空，显示视频控件
            if (item.video_data != null && !TextUtils.isEmpty(item.video_data.video_link)) {

                val videoLink = item.getVideo_data().getVideo_link()
                loge("videoLink: ${videoLink}")
                holder.setGone(R.id.webVideo, false)
                holder.setGone(R.id.ivImg, true)

                holder.setGone(R.id.tvVideoSource, false)
                holder.setText(R.id.tvVideoSource, "平台: ${item.getVideo_data().getVideo_source()}")
                //设置视频播放地址
                //eg: "<iframe frameborder=\"0\" width=100% height=\"546\" src=\"https://v.qq.com/iframe/player.html?vid=d0389uyr07l&tiny=0&auto=0\" allowfullscreen=\"true\"></iframe>";
                val html = " <title>+" + item.title + "</title>" + item.video_data.video_link
                val webVideo = holder.getView<WebView>(R.id.webVideo)
                loge("loadHtml: ${html}")
                setWebView(webVideo, html)
                webVideo.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
                //取出视频播放地址
//                val regex = "src=\"(.*?)\""
//                val pattern = Pattern.compile(regex)
//                val matcher: Matcher = pattern.matcher(videoLink)
//                if(matcher.find()){
//                    val readVideoPath = matcher.group(1)
//                    loge ("convert: ${readVideoPath}" )
////                    val listPlayView = holder.getView<ListPlayerView>(R.id.listPlayView)
////                    listPlayView.bindData(item.id.toString(),ScreenUtil.getScreenWidth(context),180.dp2px(),"",readVideoPath)
//                }

            } else {               //否则显示其他
                holder.setGone(R.id.webVideo, true)
                holder.setGone(R.id.tvVideoSource, true)
                val ivImg = holder.getView<ImageView>(R.id.ivImg)
                if (item.big_image.isNotEmpty()) {
                    Glide.with(context)
                            .load(item.big_image)
                            .transform(MultiTransformation(CenterCrop(), RoundedCorners(2f.dp2px())))
                            .into(ivImg)
                    holder.setGone(R.id.ivImg, false)
                } else {
                    holder.setGone(R.id.ivImg, true)
                }

                ivImg.setOnClickListener {
                    if (listener != null) {
                        listener?.onItemClick(holder.layoutPosition, item)
                    }
                }

            }

            val webDesc = holder.getView<android.webkit.WebView>(R.id.webDesc)
            setWebView(webDesc)
            //展示描述（用TextView展示富文本类型，有局限性，用webview又比较耗费性能）
            var detail: String = item.detail
            if (!TextUtils.isEmpty(detail)) {
                detail = HtmlUtils.getFormatHtml(detail)
            }
            webDesc.loadDataWithBaseURL(null, detail, "text/html", "utf-8", null)


            webDesc.setOnTouchListener { v, event ->
                if (event.getAction() === MotionEvent.ACTION_UP) {
                    if (listener != null) {
                        listener?.onItemClick(holder.layoutPosition, item)
                    }
                }
                false
            }

//            holder.setGone(R.id.webDesc, TextUtils.isEmpty(detail))
            //是否有外链，展示查看详情
            holder.setGone(R.id.tvLookDetail, item.link.isEmpty())
            //刊物要显示详情，不管地址是否为空
            if (item.type == ResourceTypeConstans.TYPE_PUBLICATION) {
                holder.setVisible(R.id.tvLookDetail, true)
            }

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setWebView(webView: WebView, html: String) {
//        webView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS)
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
        //其他细节操作
        webSettings.layoutAlgorithm =
                WebSettings.LayoutAlgorithm.NARROW_COLUMNS //这句话将图片缩放至屏幕宽度，非常好嘛

        webSettings.cacheMode = android.webkit.WebSettings.LOAD_NORMAL
        webSettings.allowFileAccess = true //设置可以访问文件

        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        webSettings.defaultTextEncodingName = "utf-8"
//        webSettings.pluginsEnabled = true
        webSettings.pluginState = WebSettings.PluginState.ON
//        webView.addJavascriptInterface(JsAnnotation(context as Activity), "mobile")
        webView.setWebChromeClient(WebChromeClient())
        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(webView: WebView, s: String) {
                super.onPageFinished(webView, s)
                val lp = webView.layoutParams
                lp.height = getHtmlHeight(html)
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setWebView(webView: android.webkit.WebView) {
        val webSettings: android.webkit.WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true
        //其他细节操作
        //其他细节操作
        webSettings.layoutAlgorithm =
                android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN //这句话将图片缩放至屏幕宽度，非常好嘛

        webSettings.cacheMode = android.webkit.WebSettings.LOAD_NORMAL
        webSettings.allowFileAccess = true //设置可以访问文件

        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        webSettings.defaultTextEncodingName = "utf-8"

//        webView.addJavascriptInterface(JsAnnotation(context as Activity), "mobile")
        webSettings.pluginState = android.webkit.WebSettings.PluginState.ON
        webView.setWebChromeClient(android.webkit.WebChromeClient())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }


    fun getHtmlHeight(html: String): Int {
        return try {
            var str = ""
            if (html.contains("height=")) {
                str = html.substring(html.lastIndexOf("height=") + 8, html.lastIndexOf("src") - 2)
            }
            Integer.valueOf(str)
        } catch (e: Exception) {
            545
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, data: Any?)
        fun onWebClick(position: Int, data: Any?, textView: TextView?)
    }
}