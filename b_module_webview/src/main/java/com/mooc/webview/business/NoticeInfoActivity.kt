package com.mooc.webview.business

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.webview.R
import com.mooc.webview.WebViewWrapper
import com.mooc.webview.WebviewActivity
import com.mooc.webview.databinding.WebviewActivityNoticeInfoBinding
import com.mooc.webview.viewmodel.AnnouncementViewModel
//import kotlinx.android.synthetic.main.webview_activity_initior_brief.*

@Route(path = Paths.PAGE_NOTICE_INFO)
class NoticeInfoActivity : BaseActivity() {
    val webviewWrapper by lazy { WebViewWrapper(this) }
    var planId: String? = null
    val mViewModel: AnnouncementViewModel by viewModels()
    private lateinit var inflater: WebviewActivityNoticeInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = WebviewActivityNoticeInfoBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initData()
        initListener()
    }


    private fun initView() {
        //将包装类中webview添加到布局
        val mWebView = webviewWrapper.getView()
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mWebView.layoutParams = layoutParams
        inflater.flRoot.addView(mWebView, 0)
    }

    private fun initListener() {
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }
        mViewModel.noticeInfoBean.observe(this, Observer {
            if (!TextUtils.isEmpty(it.content)) {

                val html = it.content?.replace("<img", "<img width=\"100%\"")?.let { it1 -> HtmlUtils.getReplaceHtml(it1) }

                html?.let { it1 -> webviewWrapper.loadDataWithBaseURL(it1) }
            }
        })
    }


    private fun initData() {
        val intent = intent
        if (intent != null) {
            planId = intent.getStringExtra("id")
        }
        mViewModel . getNoticeInfoData (planId)
    }

}