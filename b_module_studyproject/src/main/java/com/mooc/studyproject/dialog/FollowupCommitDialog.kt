package com.mooc.studyproject.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mooc.studyproject.R
import com.mooc.common.ktextends.dp2px
import java.lang.reflect.Field


/**
 * 跟读完成是否提交弹窗
 *
 */
class FollowupCommitDialog : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // 设置宽度为屏宽、位置在屏幕底部
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER    
        wlp.width = 300.dp2px()
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = wlp
        return dialog
    }

    var onConfirm : (()->Unit)? = null
    var onCancle : (()->Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.studyproject_dialog_followup_commit,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvConfirm).setOnClickListener {
            dismissAllowingStateLoss()
            //点击提交
            onConfirm?.invoke()
        }
        view.findViewById<TextView>(R.id.tvCancle).setOnClickListener {
            dismissAllowingStateLoss()
            //点击重新朗读
            onCancle?.invoke()
        }

    }

    override fun show(manager: FragmentManager, tag: String?) {
//        super.show(manager, tag);
        try {
            val c = Class.forName("android.support.v4.app.DialogFragment")
            val con = c.getConstructor()
            val obj: Any = con.newInstance()
            val dismissed: Field = c.getDeclaredField(" mDismissed")
            dismissed.setAccessible(true)
            dismissed.set(obj, false)
            val shownByMe: Field = c.getDeclaredField("mShownByMe")
            shownByMe.setAccessible(true)
            shownByMe.set(obj, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }
}