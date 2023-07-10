package com.mooc.setting.ui.controller

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.runOnMain
import com.mooc.common.ktextends.toast
import com.mooc.setting.R
import java.util.*


class LogAdapter(var list : ArrayList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.common_item_console_log,list),Observer{
        override fun convert(holder: BaseViewHolder, item: String) {
            val textView = holder.itemView as TextView
            textView.setText(item)

            textView.setOnLongClickListener {
                copyContentToClipboard(item,context)
                return@setOnLongClickListener true
            }
        }

    var onUpdateCallback : (()->Unit)? = null
    override fun update(o: Observable?, arg: Any?) {
        runOnMain {
            notifyDataSetChanged()
            onUpdateCallback?.invoke()
        }
    }


    fun copyContentToClipboard(content: String, context: Context) {
        //获取剪贴板管理器：
        val cm: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("Label", content)
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)

        toast("内容已复制到剪切板")
    }
}