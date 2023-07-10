package com.mooc.studyroom.ui.pop

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.dialog.BaseDialog
import com.mooc.studyroom.R

class ShareChooseDialog(context: Context, theme: Int) : BaseDialog(context, theme) {
    private var btnConfirm: Button? = null
    private var cbOne: CheckBox? = null
    private var cbTwo: CheckBox? = null
    private var cbThree: CheckBox? = null
    private var cbFour: CheckBox? = null
    private var strTitle: String? = null
    private var tvTitle: TextView? = null
    private var strConfirm: String? = null
    private var strCancel: String? = null
    private var folderName: String? = null
    private var mContext: Context = context
    var type = 0
    private val checks = booleanArrayOf(true, true, true)
    public var toshareClick: ((checks: BooleanArray?) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.studyroom_dialog_share_choose)
        tvTitle = findViewById<TextView>(R.id.layout_dialog_confirm_title)
        btnConfirm = findViewById<Button>(R.id.to_share)
        cbOne = findViewById<CheckBox>(R.id.cb_one)
        cbTwo = findViewById<CheckBox>(R.id.cb_two)
        cbThree = findViewById<CheckBox>(R.id.cb_three)
        cbFour = findViewById<CheckBox>(R.id.cb_four)
        initListener()
    }


    fun initListener() {
        setCbStatus()
        cbOne?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checks[0] = true
                setBtnCanShare(true)
            } else {
                checks[0] = false
                if (!(checks[0] || checks[1] || checks[2])) {
                    setBtnCanShare(false)
                }
            }
        }
        cbTwo?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checks[1] = true
                setBtnCanShare(true)
            } else {
                checks[1] = false
                if (!(checks[0] || checks[1] || checks[2])) {
                    setBtnCanShare(false)
                }
            }
        }


        cbFour?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checks[2] = true
                setBtnCanShare(true)
            } else {
                checks[2] = false
                if (!(checks[0] || checks[1] || checks[2])) {
                    setBtnCanShare(false)
                }
            }
        }
        btnConfirm?.setOnClickListener {
            // Intent intent = new Intent(mContext, Change)
            btnConfirm?.isEnabled = false
            toshareClick?.invoke(checks)

            dismiss()
        }
    }

    private fun setCbStatus() {
        cbOne?.isChecked = true
        cbTwo?.isChecked = true
        //        cbThree.setChecked(true);
        cbFour?.isChecked = true
        setBtnCanShare(true)
    }

    private fun setBtnCanShare(canClick: Boolean) {
        if (canClick) {
            btnConfirm?.setBackgroundResource(R.color.color_10955B)
            mContext.getColorRes(R.color.white).let { btnConfirm?.setTextColor(it) }
            btnConfirm?.isEnabled = true
        } else {
            btnConfirm?.setBackgroundResource(R.color.color_E0E0E0)
            mContext.getColorRes(R.color.color_9).let { btnConfirm?.setTextColor(it) }
            btnConfirm?.isEnabled = false
        }
    }


}