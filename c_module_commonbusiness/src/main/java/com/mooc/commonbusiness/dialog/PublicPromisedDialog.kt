package com.mooc.commonbusiness.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.databinding.CommonLayoutPublicPromiseDialogBinding
import com.mooc.commonbusiness.utils.HtmlUtils
//import kotlinx.android.synthetic.main.common_layout_public_promise_dialog.view.*

@SuppressLint("ViewConstructor")
class PublicPromisedDialog(var mContext: Context, var strHtmlMsg: String?, var strRight: String?) : BasePopupDialog(mContext) {
    var onLeftListener: ((isCheck: Boolean?) -> Unit)? = null
    var onRightListener: ((isCheck: Boolean?) -> Unit)? = null
    lateinit var inflater: CommonLayoutPublicPromiseDialogBinding
    override fun getImplLayoutId(): Int {
        return R.layout.common_layout_public_promise_dialog
    }

    override fun getMaxWidth(): Int {
        return 300.dp2px()
    }

    override fun onCreate() {
        super.onCreate()
        inflater = CommonLayoutPublicPromiseDialogBinding.bind(popupImplView)
        inflater.tvTitle.text = mContext.getString(R.string.text_str_integrity_commitment_title)

        if (!TextUtils.isEmpty(strHtmlMsg)) {
            inflater.webView.visibility = View.VISIBLE
            inflater.tvMsg.visibility = View.GONE
            strHtmlMsg = strHtmlMsg?.let { HtmlUtils.getFormatHtml(it) }
            inflater.webView.loadDataWithBaseURL("", strHtmlMsg, "text/html", "utf-8", null)
        }

        inflater.tvLeft.text = mContext.getString(R.string.text_think_again)
        inflater.tvRight.text = strRight
        inflater.tvLeft.setOnClickListener {
            onLeftListener?.invoke(inflater.cbText?.isChecked)
        }
        inflater.tvRight.setOnClickListener {
            onRightListener?.invoke(inflater.cbText?.isChecked)
        }
    }
}