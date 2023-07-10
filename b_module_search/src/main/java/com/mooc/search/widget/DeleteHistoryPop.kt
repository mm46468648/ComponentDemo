package com.mooc.search.widget

import android.content.Context
import android.view.View
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.search.R
//import kotlinx.android.synthetic.main.search_item_delete_history.view.*

/**

 * @Author limeng
 * @Date 2/8/22-5:36 PM
 */
class DeleteHistoryPop (mConText: Context):CenterPopupView(mConText) {
    override fun getImplLayoutId(): Int {
        return R.layout.search_item_delete_history
    }
    var onConfirm : (()->Unit)? = null

    override fun onCreate() {
        super.onCreate()

        val layout_left_dialog = popupImplView.findViewById<View>(R.id.layout_left_dialog)
        val layout_right_dialog = popupImplView.findViewById<View>(R.id.layout_right_dialog)
        layout_left_dialog.setOnClickListener { dismiss() }
        layout_right_dialog.setOnClickListener {
            dismiss()
            onConfirm?.invoke()
        }
    }
}