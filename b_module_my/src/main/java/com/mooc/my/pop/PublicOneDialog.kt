package com.mooc.my.pop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.my.R
import com.mooc.my.databinding.LayoutPublicOneDialogBinding

//import kotlinx.android.synthetic.main.layout_public_one_dialog.view.*

class PublicOneDialog(mContext: Context, var msg: String) : CenterPopupView(mContext) {
    var callback: ((view: View) -> Unit)? = null
    private lateinit var inflater: LayoutPublicOneDialogBinding
    override fun getImplLayoutId(): Int {
        return R.layout.layout_public_one_dialog
    }

    override fun onCreate() {
        super.onCreate()

        inflater = LayoutPublicOneDialogBinding.bind(popupImplView)
        inflater.tvMsg.text = msg
        inflater.tvButton.setOnClickListener {
            callback?.invoke(it)
            dismiss()
        }
    }
}