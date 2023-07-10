package com.mooc.webview.reccommend

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.webview.R
import com.mooc.webview.WebViewWrapper
import com.mooc.webview.interfaces.OnPageFinish
import java.util.*

class RecommendArticleAdapter(data: MutableList<ArticleBean>?) : BaseQuickAdapter<ArticleBean, BaseViewHolder>(R.layout.item_article_or_recommend, data) {




    var count = 0;

    var url: String? = null;



    override fun convert(holder: BaseViewHolder, item: ArticleBean) {
        holder.setText(R.id.title, item.title)
//
        if (holder.layoutPosition == 0) {
            holder.setGone(R.id.tip, false)
            holder.setGone(R.id.diver, false)
        } else {
            holder.setGone(R.id.tip, true)
            holder.setGone(R.id.diver, true)
        }

        var sourceStr = item.platform_zh?:""
        if (!TextUtils.isEmpty(item.source)) {
            sourceStr += " | ${item.source}"
        }
        holder.setText(R.id.tv_platform, sourceStr)


    }



}