package com.mooc.studyproject.window

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import com.mooc.commonbusiness.dialog.BaseDialog
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.StudyprojectLayoutExchangeChooseBinding
//import kotlinx.android.synthetic.main.studyproject_layout_exchange_choose.*

class ExchangeChooseDialog : BaseDialog {
    private var mContext: Context
    private var strTitle: String? = null
    private var strConfirm: String? = null
    private var strCancel: String? = null
    private var strCurrentName: String? = null
    private var strHint: String? = null
    private var cbExchangeCode: CheckBox? = null
    private var cbPoint: CheckBox? = null
    private var visiable = View.VISIBLE
    private val maxCount = 14

    constructor(context: Context, theme: Int) : super(context, theme) {
        mContext = context
    }


    private lateinit var inflater: StudyprojectLayoutExchangeChooseBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyprojectLayoutExchangeChooseBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        inflater.btnConfirm.isClickable = false
        initListener()
    }

    fun initListener() {
        inflater.btnConfirm.setOnClickListener { // TODO Auto-generated method stub
            // Intent intent = new Intent(mContext, Change)
                if (cbExchangeCode?.isChecked == true) {
                    onCompleteListener?.invoke("1")
                }
                if (cbPoint?.isChecked == true) {
                    onCompleteListener?.invoke("0")

                }
            dismiss()
        }
        cbExchangeCode?.setOnCheckedChangeListener { _, b ->
            if (b) {
                cbPoint?.isChecked = false
            }
        }
        cbPoint?.setOnCheckedChangeListener { _, b ->
            if (b) {
                cbExchangeCode?.isChecked = false
            }
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
    }


    var onCompleteListener:((inputText: String?)->Unit)?=null
}