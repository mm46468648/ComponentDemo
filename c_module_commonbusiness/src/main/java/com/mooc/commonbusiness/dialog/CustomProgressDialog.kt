package com.mooc.commonbusiness.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mooc.common.R;

/**

 * @Author limeng
 * @Date 2020/8/24-10:45 AM
 */
class CustomProgressDialog : Dialog {
    private var dialogView: View? = null
    private var mContext: Context
    private var progressImageView: ImageView? = null
    private var res: Resources? = null


    constructor(context: Context, layoutID: Int = 0) : super(context, R.style.DefaultDialogStyle) {
        mContext = context
        res = context.resources
        dialogView = LayoutInflater.from(context).inflate(layoutID, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (dialogView != null) {
            setContentView(dialogView!!)
        }
    }

    fun getCustomView(): View? {
        return dialogView
    }

    override fun show() {
        if (!isShowing) {
            try {
                super.show()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun dismiss() {
        if (isShowing) {
            try {
                super.dismiss()
                progressImageView = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    companion object {
        /**
         * @param context
         * 如果不需要，置null
         * @param desc
         * @param isCancel
         * 返回键和点击外部是否可以取消
         */
        @JvmOverloads
        fun createLoadingDialog(context: Context, outsideClose: Boolean = true): CustomProgressDialog {
            val dialog = CustomProgressDialog(context, R.layout.common_dialog_progress)
            val view = dialog.getCustomView()
            val descView = view?.findViewById<View>(R.id.layout_dialog_progress_tv) as TextView
            val imageView = view?.findViewById<View>(R.id.iv_progress_loading) as ImageView
            descView.text = context.getString(R.string.loading)
            Glide.with(context).load(R.drawable.common_gif_loading).into(imageView)
            dialog.setCanceledOnTouchOutside(outsideClose)
            dialog.setCancelable(outsideClose)
            return dialog
        }
    }
}
