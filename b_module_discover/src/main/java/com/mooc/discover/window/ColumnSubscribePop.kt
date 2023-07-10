package com.mooc.discover.window

import android.content.Context
import android.view.LayoutInflater
import com.mooc.discover.R
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.discover.databinding.ColumnPopSubscribeConfirmBinding
//import kotlinx.android.synthetic.main.column_pop_subscribe_confirm.view.*

class ColumnSubscribePop(context:Context) : CenterPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.column_pop_subscribe_confirm;
    }
    var onConfrim : (()->Unit)? = null
    var message : String  = ""
    private lateinit var inflater: ColumnPopSubscribeConfirmBinding
    override fun onCreate() {
        super.onCreate()

        inflater = ColumnPopSubscribeConfirmBinding.bind(popupImplView)
        inflater.tvConfirm.setOnClickListener {
            onConfrim?.invoke()
            dismiss()
        }
        inflater.tvCancel.setOnClickListener { dismiss() }
        inflater.tvMessage.setText(message)

    }


}