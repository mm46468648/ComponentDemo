package com.mooc.commonbusiness.utils

import android.content.Context
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.model.PublicDialogBean

/**

 * @Author limeng
 * @Date 2023/3/24-10:01 上午
 */
class ShowDialogUtils {
    companion object {

        fun showDialog(context: Context) {
            context.let { context1 ->
                val publicDialogBean = PublicDialogBean()
                publicDialogBean.strMsg =
                    context1.resources.getString(R.string.text_str_column_hint)
                publicDialogBean.strTv = context1.resources.getString(R.string.text_ok)
                val dialog = PublicOneDialog(context1, publicDialogBean)
                XPopup.Builder(context1)
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asCustom(dialog)
                    .show()

            }
        }
    }
}