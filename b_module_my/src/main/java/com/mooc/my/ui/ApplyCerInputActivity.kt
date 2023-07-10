package com.mooc.my.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.R
import com.mooc.my.databinding.ActivityApplyCerInputBinding
import com.mooc.my.pop.PublicOneDialog
import com.mooc.my.viewmodel.CerificationViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

/**
 *证书申请页面
 * @author limeng
 * @date 2020/10/9
 */
@Route(path = Paths.PAGE_APPLYCER_INPUT)
class ApplyCerInputActivity : BaseActivity() {
    var id: String? = null
//    var title: String? = null
//    val mViewModel: CerificationViewModel by lazy {
//        ViewModelProviders.of(this)[CerificationViewModel::class.java]
//    }

    val mViewModel: CerificationViewModel by viewModels()
    private lateinit var inflate : ActivityApplyCerInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = ActivityApplyCerInputBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        id = intent.getStringExtra(IntentParamsConstants.INTENT_CERTIFICATE_ID)
//        title = intent.getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_TITLE)
        initView()
        initListener()
    }

    private fun initListener() {
        inflate.rlDeleteEdit.setOnClickListener {
            inflate.editNickName.setText("")
            inflate.rlDeleteEdit.visiable(false)

        }
        inflate.editNickName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.toString().isEmpty()) {
                    inflate.rlDeleteEdit.visiable(false)
                }else{
                    inflate.rlDeleteEdit.visiable(true)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    inflate.rlDeleteEdit.visiable(false)
                }else{
                    inflate.rlDeleteEdit.visiable(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    inflate.rlDeleteEdit.visiable(false)
                }else{
                    inflate.rlDeleteEdit.visiable(true)
                }
            }
        })
        inflate.btnCancel.setOnClickListener { finish() }
        inflate.commonTitleLayout.setOnLeftClickListener { finish() }
        inflate.btnOk.setOnClickListener {
            if (inflate.checkBox.isChecked) {
                val name = if (inflate.editNickName.text.trim().isEmpty()) "" else inflate.editNickName.text.trim()
                if (id.equals("-1")) return@setOnClickListener
                val requestData = JSONObject()
                try {
                    requestData.put("certificate_id", id)
                    requestData.put("name", name)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
                mViewModel.loadData(stringBody)
            }
        }
        mViewModel.cerificationBean.observe(this, Observer {
            if (it.isSuccess) {
                showTipDialog()
            } else {
                toast(it.message)
            }

        })
        inflate.checkBox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                inflate.btnOk.setBackgroundResource(R.drawable.shape_radius5_colorprimary)
            } else {
                inflate.btnOk.setBackgroundResource(R.drawable.shape_radius5_colorb6ebd4)
            }


            inflate.editNickName.setText(if (inflate.editNickName.getText().toString().trim().isEmpty())
                GlobalsUserManager.userInfo?.name else inflate.editNickName.getText().toString().trim())

        }
        inflate.llCheckBox.setOnClickListener {
            val checked: Boolean = !inflate.checkBox.isChecked()
            inflate.checkBox.setChecked(checked)
        }
    }

    private fun showTipDialog() {
        val onPop=PublicOneDialog(this,getString(R.string.tip_certificate_applly_suc))
        XPopup.Builder(this)
                .asCustom(onPop)
                .show()
        onPop.callback={
            setResult(RESULT_OK, intent)
            finish()
        }
//        val dialog = PublicOneDialog(this, R.style.DefaultDialogStyle)
//        dialog.setStrButton("确定")
//        dialog.setStrMsg(getString(R.string.tip_certificate_applly_suc))
//        dialog.show()
//        dialog.callback = {
//            finish()
//        }
    }

    private fun initView() {
        inflate.editNickName.setText(if (GlobalsUserManager.userInfo?.name.isNullOrEmpty()) "姓名" else GlobalsUserManager.userInfo?.name)
        if (!inflate.editNickName.text?.trim().isNullOrEmpty()) {
            inflate.rlDeleteEdit.visiable(true)
        }
    }
}