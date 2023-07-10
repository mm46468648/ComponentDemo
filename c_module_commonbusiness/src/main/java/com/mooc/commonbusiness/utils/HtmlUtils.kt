package com.mooc.commonbusiness.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.commonbusiness.module.web.MobileWebInterface

class HtmlUtils {

    companion object {
        fun fromHtml(string: String): Spanned {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(string)
            }
        }

        /**
         * 将字符串使用html格式包裹
         */
        fun getReplaceHtml(detail: String): String {
            return if (TextUtils.isEmpty(detail)) {
                ""
            } else """<html>
                     <head>
                     <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                     </head>
                     <body>
                    $detail </body>
                    </html>"""
        }

        /**
         * 对h5内容格式化
         * 使图片和视频空间，缩放至屏幕大小，这种会导致下方有留白
         */
//        fun getFormatHtml(detail: String): String {
//            return """<html>
//                        <head>
//                        <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
//                        </head>
//                        <style>img,video,table,iframe{width:100%!important;height:auto;}
//                        video,table,iframe{max-width:100%!important;min-height:200px!important;}
//                        </style>
//                        <body style="word-break: break-all;text-align: justify; font-size: 15px; margin-left: 0px;padding-left: 0px;line-height: 2; color: #222222;">
//                        $detail </body>
//                    </html>"""
//        }

        /**
         * 格式化html
         */
        fun getFormatHtml(detail: String): String {
            return """<html>
                        <head>
                        <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                        </head>
                        <style>
                        body {
                            margin: 0;
                            padding: 5px;
                            box-sizing: border-box;
                        }
                        img,video,table,iframe{
                            max-width:100%!important;
                            height:auto!important;
                            display: block;
                        }
                        ul {
                            max-width: 90% !important;
                        }
                        code {
                             display: block;
                             word-wrap: break-word;
                             word-break: break-all;
                             max-width: 100% !important;
                             overflow-x:scroll;
                        }
                            
                        .code-snippet_outer,
                        .code-snippet__string {
                            max-width: 100%;
                            word-wrap: break-word;
                            word-break: break-all;
                            display: inherit;
                        }
                        section,div {
                             width: inherit !important;
                        }
                        </style>
                        <body style="word-break: break-all;text-align: justify;font-size: 14px; line-height: 1.5; color: #666666;">
                        $detail </body>
                    </html>"""
        }

        /**
         * 字体4a
         */
        fun getHtml(detail: String): String {
            return """<html>
                        <head>
                        <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                        </head>
                        <style>
                        img,video,table,iframe{
                            max-width:100%!important;
                            height:auto!important;
                            display: block;
                        }
                        </style>
                        <body style="word-break: break-all;text-align: justify;font-size: 15px; line-height: 1.8; color: #4A4A4A;">
                        $detail </body>
                    </html>"""
        }

        fun getHtmlMargin25(detail: String): String {
            return """<html>
                   <head>
                   <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                   </head>
                   <style>
                   img,video,table,iframe{
                            max-width:100%!important;
                            height:auto!important;
                            display: block;
                        }
                    </style>
                   <body style="word-break: break-all; margin-left: 25px; margin-right: 25px; margin-bottom: 25px; text-align: justify;font-size: 14px; line-height: 2;">
                 $detail </body>
                 </html>"""
        }


        /**
         * 设置左右间距
         */
        fun getHtmlWithMargin(detail: String, margin: Int): String {
            return """<html>
                        <head>
                        <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                        </head>
                        <style>
                        img,video,table,iframe{
                            max-width:100%!important;
                            height:auto!important;
                            display: block;
                        }
                        </style>
                        <body style="word-break: break-all; margin-left: ${margin}px; margin-right: ${margin}px;text-align: justify;font-size: 15px; line-height: 1.8; color: #4A4A4A;">
                        $detail </body>
                    </html>"""
        }

        /**
         * 有Margin left right
         */
        fun getHtmlMargin(detail: String): String {
            return """<html>
                        <head>
                        <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                        </head>
                        <style>
                        img,video,table,iframe{
                            max-width:100%!important;
                            height:auto!important;
                            display: block;
                        }
                        </style>
                        <body style="word-break: break-all; margin-left: 25px; margin-right: 25px;text-align: justify;font-size: 15px; line-height: 1.8; color: #4A4A4A;">
                        $detail </body>
                    </html>"""
        }


        fun getStrCss(): String {
            return """@charset "utf-8";
                    html {
                      background: #fff;
                      -webkit-text-size-adjust: 100%;
                      -ms-text-size-adjust: 100%;
                      text-rendering: optimizelegibility;
                    
                      -moz-osx-font-smoothing: grayscale;
                      -webkit-font-smoothing: antialiased;
                      text-rendering: optimizeLegibility;
                      line-height: 1.8em;
                      box-sizing: border-box;
                      font-size:32px;
                    }

                    /* 如果你的项目仅支持 IE9+ | Chrome | Firefox 等，推荐在 <html> 中添加 .borderbox 这个 class */
                    html.borderbox *, html.borderbox *:before, html.borderbox *:after {
                      -moz-box-sizing: border-box;
                      -webkit-box-sizing: border-box;
                      box-sizing: border-box;
                    }
                    
                    /* 内外边距通常让各个浏览器样式的表现位置不同 */
                    body, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, pre, code, form, fieldset, legend, input, textarea, p, blockquote, th, td, hr, button, article, aside, details, figcaption, figure, footer, header, menu, nav, section {
                      margin: 0;
                      padding: 0;
                      color: #25272A;
                    }
                    
                    /* 重设 HTML5 标签, IE 需要在 js 中 createElement(TAG) */
                    article, aside, details, figcaption, figure, footer, header, menu, nav, section {
                      display: block;
                    }
                    
                    /* HTML5 媒体文件跟 img 保持一致 */
                    audio, canvas, video {
                      display: inline-block;
                    }
                    
                    /* 要注意表单元素并不继承父级 font 的问题 */
                    body, button, input, select, textarea {
                      font: 400 1em/1.8 PingFang SC, Lantinghei SC, Microsoft Yahei, Hiragino Sans GB, Microsoft Sans Serif, WenQuanYi Micro Hei, sans;
                    }
                    
                    button::-moz-focus-inner,
                    input::-moz-focus-inner {
                      padding: 0;
                      border: 0;
                    }
                    
                    /* 去掉各Table cell 的边距并让其边重合 */
                    table {
                      border-collapse: collapse;
                      border-spacing: 0;
                      table-layout: fixed;
                    }
                    
                    /* 去除默认边框 */
                    fieldset, img {
                      border: 0;
                    }
                    
                    /* 块/段落引用 */
                    blockquote {
                      position: relative;
                      color: #999;
                      font-weight: 400;
                      border-left: 1px solid #1abc9c;
                      padding-left: 1em;
                      margin: 1em 3em 1em 2em;
                    }
                    
                    @media only screen and ( max-width: 640px ) {
                      blockquote {
                        margin: 1em 0;
                      }
                    }
                    
                    /* Firefox 以外，元素没有下划线，需添加 */
                    acronym, abbr {
                      border-bottom: 1px dotted;
                      font-variant: normal;
                    }
                    
                    /* 添加鼠标问号，进一步确保应用的语义是正确的（要知道，交互他们也有洁癖，如果你不去掉，那得多花点口舌） */
                    abbr {
                      cursor: help;
                    }
                    
                    /* 一致的 del 样式 */
                    del {
                      text-decoration: line-through;
                    }
                    
                    address, caption, cite, code, dfn, em, th, var {
                      font-style: normal;
                      font-weight: 400;
                    }
                    
                    /* 去掉列表前的标识, li 会继承，大部分网站通常用列表来很多内容，所以应该当去 */
                    ul, ol {
                      list-style: none;
                    }
                    
                    /* 对齐是排版最重要的因素, 别让什么都居中 */
                    caption, th {
                      text-align: left;
                    }
                    
                    q:before, q:after {
                      content: '';
                    }
                    
                    /* 统一上标和下标 */
                    sub, sup {
                      font-size: 75%;
                      line-height: 0;
                      position: relative;
                    }
                    
                    :root sub, :root sup {
                      vertical-align: baseline; /* for ie9 and other modern browsers */
                    }
                    
                    sup {
                      top: -0.5em;
                    }
                    
                    sub {
                      bottom: -0.25em;
                    }
                    
                    /* 让链接在 hover 状态下显示下划线 */
                    a {
                      color: #1abc9c;
                    }
                    
                    a:hover {
                      text-decoration: underline;
                    }
                    
                    .typo a {
                      border-bottom: 1px solid #1abc9c;
                    }
                    
                    .typo a:hover {
                      border-bottom-color: #555;
                      color: #555;
                      text-decoration: none;
                    }
                    
                    /* 默认不显示下划线，保持页面简洁 */
                    ins, a {
                      text-decoration: none;
                    }
                    
                    /* 专名号：虽然 u 已经重回 html5 Draft，但在所有浏览器中都是可以使用的，
                     * 要做到更好，向后兼容的话，添加 class="typo-u" 来显示专名号
                     * 关于 <u> 标签：http://www.whatwg.org/specs/web-apps/current-work/multipage/text-level-semantics.html#the-u-element
                     * 被放弃的是 4，之前一直搞错 http://www.w3.org/TR/html401/appendix/changes.html#idx-deprecated
                     * 一篇关于 <u> 标签的很好文章：http://html5doctor.com/u-element/
                     */
                    u, .typo-u {
                      text-decoration: underline;
                    }
                    
                    /* 标记，类似于手写的荧光笔的作用 */
                    mark {
                      background: #fffdd1;
                      border-bottom: 1px solid #ffedce;
                      padding: 2px;
                      margin: 0 5px;
                    }
                    
                    /* 代码片断 */
                    pre, code, pre tt {
                      font-family: Courier, 'Courier New', monospace;
                    }
                    
                    pre {
                      background: #f8f8f8;
                      border: 1px solid #ddd;
                      padding: 1em 1.5em;
                      display: block;
                      -webkit-overflow-scrolling: touch;
                    }
                    
                    /* 一致化 horizontal rule */
                    hr {
                      border: none;
                      border-bottom: 1px solid #cfcfcf;
                      margin-bottom: 0.8em;
                      height: 10px;
                    }
                    
                    /* 底部印刷体、版本等标记 */
                    small, .typo-small,
                      /* 图片说明 */
                    figcaption {
                      font-size: 0.9em;
                      color: #888;
                    }
                    
                    strong, b {
                      font-weight: bold;
                      color: #000;
                    }
                    
                    /* 可拖动文件添加拖动手势 */
                    [draggable] {
                      cursor: move;
                    }
                    
                    .clearfix:before, .clearfix:after {
                      content: "";
                      display: table;
                    }
                    
                    .clearfix:after {
                      clear: both;
                    }
                    
                    .clearfix {
                      zoom: 1;
                    }
                    
                    /* 强制文本换行 */
                    .textwrap, .textwrap td, .textwrap th {
                      word-wrap: break-word;
                      word-break: break-all;
                    }
                    
                    .textwrap-table {
                      table-layout: fixed;
                    }
                    
                    /* 提供 serif 版本的字体设置: iOS 下中文自动 fallback 到 sans-serif */
                    .serif {
                      font-family: Palatino, Optima, Georgia, serif;
                    }
                    
                    /* 保证块/段落之间的空白隔行 */
                    .typo p, .typo pre, .typo ul, .typo ol, .typo dl, .typo form, .typo hr, .typo table,
                    .typo-p, .typo-pre, .typo-ul, .typo-ol, .typo-dl, .typo-form, .typo-hr, .typo-table, blockquote {
                      margin-bottom: 1.2em
                    }
                    
                    h1, h2, h3, h4, h5, h6 {
                      font-family: PingFang SC, Verdana, Helvetica Neue, Microsoft Yahei, Hiragino Sans GB, Microsoft Sans Serif, WenQuanYi Micro Hei, sans-serif;
                      font-weight: 600;
                      color: #000;
                      line-height: 1.35;
                    }
                    
                    /* 标题应该更贴紧内容，并与其他块区分，margin 值要相应做优化 */
                    .typo h1, .typo h2, .typo h3, .typo h4, .typo h5, .typo h6,
                    .typo-h1, .typo-h2, .typo-h3, .typo-h4, .typo-h5, .typo-h6 {
                      margin-top: 1.2em;
                      margin-bottom: 0.6em;
                      line-height: 1.35;
                      font-weight: 400;
                    }
                    
                    .typo h1, .typo-h1 {
                      font-size: 1.6em;
                    }
                    
                    .typo h2, .typo-h2 {
                      font-size: 1.6em;
                    }
                    
                    .typo h3, .typo-h3 {
                      font-size: 1.4em;
                    }
                    
                    .typo h4, .typo-h4 {
                      font-size: 1.2em;
                    }
                    
                    .typo h5, .typo h6, .typo-h5, .typo-h6 {
                      font-size: 1.2em;
                    }
                    
                    /* 在文章中，应该还原 ul 和 ol 的样式 */
                    .typo ul, .typo-ul {
                      margin-left: 1em;
                      list-style: disc;
                    }
                    
                    .typo ol, .typo-ol {
                      list-style: decimal;
                      margin-left: 1em;
                    }
                    
                    .typo li ul, .typo li ol, .typo-ul ul, .typo-ul ol, .typo-ol ul, .typo-ol ol {
                      margin-bottom: 0.8em;
                      margin-left: 1em;
                    }
                    
                    .typo li ul, .typo-ul ul, .typo-ol ul {
                      list-style: circle;
                    }
                    
                    /* 同 ul/ol，在文章中应用 table 基本格式 */
                    .typo table th, .typo table td, .typo-table th, .typo-table td, .typo table caption {
                      border: 1px solid #ddd;
                      padding: 0.5em 1em;
                      color: #666;
                    }
                    
                    .typo table th, .typo-table th {
                      background: #fbfbfb;
                    }
                    
                    .typo table thead th, .typo-table thead th {
                      background: #f1f1f1;
                    }
                    
                    .typo table caption {
                      border-bottom: none;
                    }
                    
                    /* 去除 webkit 中 input 和 textarea 的默认样式  */
                    .typo-input, .typo-textarea {
                      -webkit-appearance: none;
                      border-radius: 0;
                    }
                    
                    .typo-em, .typo em, legend, caption {
                      color: #000;
                      font-weight: inherit;
                    }
                    
                    /* 着重号，只能在少量（少于100个字符）且全是全角字符的情况下使用 */
                    .typo-em {
                      position: relative;
                    }
                    
                    .typo-em:after {
                      position: absolute;
                      top: 0.65em;
                      left: 0;
                      width: 100%;
                      overflow: hidden;
                      white-space: nowrap;
                      content: "・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・・";
                    }
                    
                    /* Responsive images */
                    .typo img,img ,p img,div img {
                      max-width: 100%;
                      margin: auto;
                      display: block;
                      margin-bottom: 20px;
                    }
                    
                    body{
                     padding: 10px 20px;
                        font-size: 16px;
                        line-height: 1.8em;word-break: break-word;    font-family: -apple-system, BlinkMacSystemFont, "PingFang SC","Helvetica Neue",STHeiti,"Microsoft Yahei",Tahoma,Simsun,sans-serif;
                    }
                    body h2,body h3,body h4{
                        margin: 20px 0;
                    }
                    .title {
                      font-size: 25px;
                      color: #25272A;
                      margin-top: 15px;
                      margin-bottom: 20px;
                      font-weight: 600;
                    }
                    
                    
                    video,audio {
                      width: 100%;
                      height: auto;
                      margin: auto;
                    }
                    
                    p {
                      text-align: justify;
                      margin-bottom: 1em;
                    }
                    h2.sect2{
                        font-size: 26px;
                        margin: 40px 0 20px;
                    }
                    .reference{
                        margin-top: 50px;
                    }
                    .abstract,.keywords{
                        margin-top: 20px;
                    }
                    .keywords{
                        margin-bottom: 20px;
                    }"""
        }


        @Suppress("DEPRECATION")
        @SuppressLint("SetJavaScriptEnabled")
        fun setWebView(webView: WebView, context: Context) {
            val webSettings: WebSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.useWideViewPort = true
            webSettings.loadWithOverviewMode = true
            //缩放操作
            webSettings.setSupportZoom(true)
            webSettings.builtInZoomControls = false
            webSettings.displayZoomControls = false
            //其他细节操作
            webSettings.cacheMode = WebSettings.LOAD_NORMAL
            webSettings.allowFileAccess = true //设置可以访问文件
            webSettings.javaScriptCanOpenWindowsAutomatically = true
            webSettings.loadsImagesAutomatically = true
            webSettings.defaultTextEncodingName = "utf-8"
            webSettings.savePassword = false
            setWebViewRemove(webView)
            webSettings.allowFileAccess = false
            webView.addJavascriptInterface(MobileWebInterface(context as Activity), "mobile")
            webView.webViewClient = NoAdWebViewClient()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }

        private fun setWebViewRemove(webView: WebView) {
            webView.removeJavascriptInterface("searchBoxJavaBridge_")
            webView.removeJavascriptInterface("accessibility")
            webView.removeJavascriptInterface("accessibilityTraversal")
        }

        private class NoAdWebViewClient : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return setUrlLoading(view, url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return setUrlLoading(view, request.url.toString())
            }

            @SuppressLint("SetJavaScriptEnabled")
            override fun onPageFinished(view: WebView, url: String) {
                //开启js
                view.settings.javaScriptEnabled = true
                view.loadUrl(NormalConstants.JS_FUNCTION)
            }

        }


        private fun setUrlLoading(view: WebView, url: String): Boolean {
            //通过连接跳转对应的app内资源
            if (url.startsWith("customize://") || url.startsWith("moocnd://")) {
                val urlOverrideUtil = UrlOverrideUtil()
                urlOverrideUtil.loadStudyResource(url)
            }
            if (url.startsWith("http://") || url.startsWith("https://")) {
                view.loadUrl(url)
            }
            return true
        }
    }


}