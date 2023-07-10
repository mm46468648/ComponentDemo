package com.mooc.studyproject.window

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import com.mooc.commonbusiness.dialog.BaseDialog
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.StudyprojectLayoutDialogInputBinding
//import kotlinx.android.synthetic.main.studyproject_layout_dialog_input.*

class InputDialog : BaseDialog {
   
     var mContext: Context
     var strTitle: String? = null
     var strConfirm: String? = null
     var strCancel: String? = null
     var strCurrentName: String? = null
     var strHint: String? = null
     var visiable = View.VISIBLE
     val maxCount = 14

    constructor(context: Context, theme: Int) : super(context, theme) {
        mContext = context
    }


    lateinit var inflater: StudyprojectLayoutDialogInputBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyprojectLayoutDialogInputBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        inflater.btnConfirm.isClickable = false
        initListener()
    }

    fun initListener() {
        inflater.edtInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                inflater.tvCount.text = s.toString().trim { it <= ' ' }.length.toString() + "/" + maxCount
            }
        })
        inflater.btnConfirm.setOnClickListener {
            onCompleteListener?.invoke(inflater.edtInputText.text.toString().trim { it <= ' ' })
            dismiss()
        }
        inflater.btnCancel.setOnClickListener {
            
            dismiss()
        }
    }

    override fun show() {
        super.show()
        if (!TextUtils.isEmpty(strTitle)) {
            inflater.tvTitle.text = strTitle
        }
        if (!TextUtils.isEmpty(strConfirm)) {
            inflater.btnConfirm.text = strConfirm
        }
        if (!TextUtils.isEmpty(strCancel)) {
            inflater.btnCancel.text = strCancel
        }
        if (!TextUtils.isEmpty(strCurrentName)) {
            inflater.edtInputText.setText(strCurrentName)
            inflater.edtInputText.setSelection(strCurrentName?.length!!)
        }
        if (!TextUtils.isEmpty(strHint)) {
            inflater.edtInputText.hint = strHint
        }
        inflater.tvCount.visibility = visiable
    }


    var onCompleteListener:((inputText: String?)->Unit)?=null
}