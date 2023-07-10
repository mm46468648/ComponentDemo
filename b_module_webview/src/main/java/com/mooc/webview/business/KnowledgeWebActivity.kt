package com.mooc.webview.business

import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.route.Paths

/**
 * 知识点webview页面，需要对加载的url进行处理
 */
@Route(path = Paths.PAGE_WEB_KNOWLEDGE)
class KnowledgeWebActivity : BaseResourceWebviewActivity() {


    val splitString = "fragment/"

    /**
     * 对url进行处理
     * 在这个链接中http://v1-www.xuetangx.com/login/moocnd/?next=/fragment/641?plantform=moocnd
     * 提取中fragment 和 ？中间的值
     * 拼接成新的链接加载
     *
     */
    override fun loadUrl() {
//        if(loadUrl.isNotEmpty() && loadUrl.contains(splitString)){
//            val startIndex = loadUrl.indexOf(splitString) + splitString.length
//            val lastIndexOf = loadUrl.lastIndexOf("?")
//            val substring = loadUrl.substring(startIndex, lastIndexOf)
//            loadLinkUrl(String.format(UrlConstants.XUETANG_KNOWLEDGE_URL,substring))
//        }else{
//            loadLinkUrl(splitString)
//        }
        loadLinkUrl(loadUrl)
    }

    /**
     * 加载链接地址
     */
    private fun loadLinkUrl(realUrl: String) {
        webviewWrapper.loadUrl(realUrl)

    }


}