package com.mooc.setting.adapter

import android.webkit.WebView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.setting.R
import com.mooc.setting.model.UpdateLogItem

class UpdateLogAdapter(list : ArrayList<UpdateLogItem>)
    : BaseQuickAdapter<UpdateLogItem,BaseViewHolder>(R.layout.set_item_update_log,list),LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: UpdateLogItem) {
//        val formatYear = TimeFormatUtil.formatDate(item.year_str, "YYYY")
        holder.setText(R.id.tvYear,item.year_str)
        holder.setText(R.id.tvDay,item.time_str)
        val strVersion = "版本号: ${item.version_name} (build ${item.build})"
        holder.setText(R.id.tvVersion,strVersion)
//        val desStr = HtmlUtils.fromHtml(item.app_presentation)
//        holder.setText(R.id.tvDes,desStr)

        val formatHtml = HtmlUtils.getFormatHtml(item.app_presentation)
        holder.getView<WebView>(R.id.webView).loadDataWithBaseURL(null, formatHtml, "text/html", "UTF-8", null)
    }
}