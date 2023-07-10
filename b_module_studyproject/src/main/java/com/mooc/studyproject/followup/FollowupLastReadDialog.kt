package com.mooc.studyproject.followup

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mooc.common.ktextends.dp2px
import com.mooc.studyproject.R
import java.lang.reflect.Constructor
import java.lang.reflect.Field


/**
 * 跟读完成是否提交弹窗
 */
class FollowupLastReadDialog : DialogFragment() {


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

    var onConfirm: (() -> Unit)? = null
    var onCancle: (() -> Unit)? = null

    lateinit var btnConfirm: TextView;
    lateinit var btnCancel: TextView;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.studyproject_dialog_followup_commit, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnConfirm = view.findViewById<TextView>(R.id.tvConfirm);
        btnConfirm.text = "上传"
        btnConfirm.setOnClickListener {
            dismissAllowingStateLoss()
            //点击提交
            onConfirm?.invoke()
        }

        btnCancel = view.findViewById(R.id.tvCancle);
        btnCancel.text = "重新朗读"
        btnCancel.setOnClickListener {
            dismissAllowingStateLoss()
            //点击重新朗读
            onCancle?.invoke()
        }

        view.findViewById<TextView>(R.id.tip).text = "由于网络波动等原因上次的朗读音频上传失败，是否再次上传？"
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