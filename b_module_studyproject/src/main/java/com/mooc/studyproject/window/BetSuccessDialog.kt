package com.mooc.studyproject.window

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.mooc.commonbusiness.dialog.BaseDialog
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.StudyprojectLayoutBetSuccesssDialogBinding
//import kotlinx.android.synthetic.main.studyproject_layout_bet_successs_dialog.*

class BetSuccessDialog(context: Context, theme: Int) : BaseDialog(context, theme) {

     var strMsg: String? = null
     var strButton: String? = null
     var invitationScore: String? = null
     var chengxinScore: String? = null
     var getScore: String? = null
    var onButtonClick: (() -> Unit)? = null

    private lateinit var inflater: StudyprojectLayoutBetSuccesssDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyprojectLayoutBetSuccesssDialogBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initListener()
    }

    fun initListener() {
        inflater.tvButtonOne?.setOnClickListener {
            onButtonClick?.invoke()
            dismiss()
        }
    }

    override fun show() {
        super.show()
        if (!TextUtils.isEmpty(strMsg)) {
            inflater.tvMsg.text = strMsg
        }
        if (!TextUtils.isEmpty(strButton)) {
            inflater.tvButtonOne.visibility = View.VISIBLE
            inflater.tvButtonOne.text = strButton
        } else {
            inflater.tvButtonOne.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(chengxinScore)) {
            if (chengxinScore == "0") {
                inflater.rlCheng.visibility = View.GONE
            } else {
                inflater.chengxinTv.text = "+$chengxinScore"
                inflater.rlCheng.visibility = View.VISIBLE
            }
        } else {
            inflater.rlCheng.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(invitationScore)) {
            if (invitationScore == "0") {
                inflater.rlInvitation.visibility = View.GONE
            } else {
                inflater.invitationTv.text = "-$invitationScore"
                inflater.rlInvitation.visibility = View.VISIBLE
            }
        } else {
            inflater.rlInvitation.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(getScore)) {
            if (getScore == "0") {
                inflater.rlGet.visibility = View.GONE
            } else {
                inflater.getScoreTv.text = getScore
                inflater.rlGet.visibility = View.VISIBLE
            }
        } else {
            inflater.rlGet.visibility = View.GONE
        }
    }


   
}