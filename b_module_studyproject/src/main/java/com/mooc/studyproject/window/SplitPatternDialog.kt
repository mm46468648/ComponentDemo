package com.mooc.studyproject.window

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mooc.commonbusiness.dialog.BaseDialog
import com.mooc.studyproject.R
import com.mooc.common.webview.WebviewWrapper

class SplitPatternDialog(context: Activity, theme: Int) : BaseDialog(context, theme) {
    private var viewClose: View? = null
    private var tvDesc: TextView? = null
    var desc: String? = null
    var flRoot: FrameLayout? = null

    val webviewWrapper by lazy { WebviewWrapper(context as AppCompatActivity) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.studyproject_layout_split_pattern_dialog)
        tvDesc = findViewById<TextView>(R.id.tv_desc_split_pattern)
        viewClose = findViewById<View>(R.id.ll_close_split_pattern)
        flRoot = findViewById(R.id.flRoot)
        addWebView()
        initListener()
    }

    private fun addWebView() {
        //将包装类中webview添加到布局
        val mWebView = webviewWrapper.getWebView()
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mWebView.layoutParams = layoutParams
        flRoot?.addView(mWebView, 0)

    }

    fun initListener() {
        viewClose?.setOnClickListener {

            dismiss()
        }
    }

    override fun show() {
        super.show()
        if (!TextUtils.isEmpty(desc)) {
            desc = desc!!.replace("<img", "<img width=\"100%\"")
            val html = getReplaceHtml(desc!!)
            webviewWrapper.loadBaseUrl(html)
        }

    }

    private fun getReplaceHtml(detail: String): String {
        return """<html>
                 <head>
                 <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                 </head>
                 <body style="word-break: break-all; font-size: 14px; line-height: 2; color: #99999;">
                 $detail </body>
                 </html>"""
    }

}