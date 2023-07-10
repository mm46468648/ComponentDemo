@file:Suppress("DEPRECATION")

package com.mooc.webview.business

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.webview.WebviewActivity
import com.mooc.webview.viewmodel.AnnouncementViewModel
//import kotlinx.android.synthetic.main.webview_activity_announcement.*

@Route(path = Paths.PAGE_ANNOUNCEMENT_DETAIL)
class AnnouncementActivity : WebviewActivity() {
    val mViewModel: AnnouncementViewModel by lazy {
        ViewModelProviders.of(this)[AnnouncementViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater.commonTitleLayout.middle_text = "公告详情"
        //设置返回健
        inflater.commonTitleLayout.setOnLeftClickListener {
            finish()
        }

        //隐藏加载进度条
        webviewWrapper.onLoadProgressChange = {}
        mViewModel.getData(resourceID)

        //内容
        mViewModel.commondata.observe(this, {
            var title = ""
            var describe = it.article
            if (it.title.isNotEmpty()) {
                title = """<p style="text-align:center;margin-top: 15px;font-size: 18px;color: #222222;">
                            ${it.title} </p>"""
            }
            if (title.isNotEmpty()) {
                describe = title + describe
            }
            val finalHtml = HtmlUtils.getHtmlMargin(describe)
            webviewWrapper.loadDataWithBaseURL(finalHtml)
        })
    }

}