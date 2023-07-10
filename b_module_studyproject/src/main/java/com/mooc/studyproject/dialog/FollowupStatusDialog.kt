package com.mooc.dialog.followup

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mooc.studyproject.R
import com.mooc.resource.ktextention.dp2px
import java.lang.reflect.Field


/**
 * 跟读完成是否提交弹窗
 */
class FollowupStatusDialog : DialogFragment() {

    companion object {
        const val followUpState_Success = 0    //朗读成功
        const val followUpState_fill = 1    //朗读失败
        const val followUpState_all_complete = 2    //全部朗读完成
    }

    var followUpState = -1
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 设置宽度为屏宽、位置在屏幕底部
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 100.dp2px())
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER    //底部弹出
//        wlp.width = Utils.dp2px(300f).toInt()
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = wlp
        return dialog
    }

    var onConfirm: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.studyproject_dialog_followup_status, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        //设置中间文案
        val tvStatusStr = when (followUpState) {
            followUpState_Success -> "恭喜您，已完成朗读"
            followUpState_fill -> "语音评测反馈未读完整，可能读得快了，请放慢速度再读一次？"
            followUpState_all_complete -> "恭喜您完成本条资源的学习，又进步了一小步噢"
            else -> ""
        }
        tvStatus.text = tvStatusStr

        //设置背景
        if (followUpState == followUpState_fill) {
            view.findViewById<View>(R.id.flRoot).setBackgroundResource(R.mipmap.studyproject_bg_followup_fill)
        }
        view.findViewById<TextView>(R.id.tvButton).setOnClickListener {
            //点击提交
            onConfirm?.invoke()
            dismiss()
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