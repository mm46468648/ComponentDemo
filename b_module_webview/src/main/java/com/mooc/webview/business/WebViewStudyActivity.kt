package com.mooc.webview.business

import android.os.Bundle
import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.WEB_RESOURCE_TXT
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.WEB_RESOURCE_TYPE
import com.mooc.commonbusiness.route.Paths
import com.mooc.webview.WebviewActivity
//import kotlinx.android.synthetic.main.webview_activity_webview.*


/**
 *参与规则中的更多 发起人简介 邀请报名奖积分说明 页面
 * 想把这三个webView 和为一个页面
 * @author limeng
 * @date 2020/12/29
 */
@Route(path = Paths.PAGE_WEB_STUDY)
class WebViewStudyActivity : WebviewActivity() {

    var text: String? = null
    var type: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initListener()


    }

    private fun initListener() {
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }
    }

    private fun initData() {
        text = intent.getStringExtra(WEB_RESOURCE_TXT)
        type = intent.getStringExtra(WEB_RESOURCE_TYPE) //1 参与规则中的更多  3 邀请报名奖积分说明 页面
        when (type) {
            "1" -> {
                //设置中间标题为
                inflater.commonTitleLayout.middle_text = "参与规则"
                if (!TextUtils.isEmpty(text)) {
                    text = text?.replace("<img", "<img width=\"100%\"")
                }
                val html = text?.let { getReplaceRuleHtml(it) }
                html?.let { webviewWrapper.loadDataWithBaseURL(it) }
            }
            "3" -> {
                inflater.commonTitleLayout.middle_text = "邀请报名奖积分说明"
                if (!TextUtils.isEmpty(text)) {
                    text = text?.let { getChallengeHtml(it) }
                    text?.let { webviewWrapper.loadDataWithBaseURL(it) }
                }

            }
            else -> {
            }
        }
    }

    private fun getReplaceRuleHtml(detail: String): String? {
        return """<html>
                  <head>
                  <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                  </head>
                  <body style="word-break: break-all; margin: 25px 25px; font-size: 14px; line-height: 2; color: #555555;">
                 $detail </body>
                  </html>"""
    }

    private fun getChallengeHtml(detail: String): String? {
        return """<html>
                  <head>
                  <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                 </head>
                  <body>
                  <style> 
                   body {text-align: justify;} 
                   img,video {max-width: 100%;} 
                   </style>
                   $detail </body>
                 </html>"""
    }

//    private fun replaceImage(html: String): String? {
//        val parse: Document = Jsoup.parseBodyFragment(html)
//        val imgs: Elements = parse.getElementsByTag("img")
//        if (imgs != null && imgs.size() > 0) {
//            var e: Element
//            for (img in imgs) {
//                e = img
//                e.attr("style", "")
//            }
//        }
//        return parse.toString()
//    }

}