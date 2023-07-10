package com.mooc.my.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.databinding.ActivityFillCardRulesBinding
import com.mooc.my.viewmodel.MakeUpCheckinViewModel
//import kotlinx.android.synthetic.main.activity_fill_card_rules.*
//import kotlinx.android.synthetic.main.activity_fill_card_rules.commonTitle
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

/**
 * 补签规则
 */
@Route(path = Paths.PAGE_CHECKIN_REPAIRS)
class CheckInRepairRulesActivity : BaseActivity() {

    companion object{
        const val KEY_MAKE_UPDATE = "sign_make_update"
    }

    private lateinit var inflate : ActivityFillCardRulesBinding

    val repairDate by extraDelegate(KEY_MAKE_UPDATE,"")
    val mViewModel: MakeUpCheckinViewModel by lazy {
        ViewModelProviders.of(this)[MakeUpCheckinViewModel::class.java]
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = ActivityFillCardRulesBinding.inflate(layoutInflater)
        setContentView(inflate.root)

        inflate.commonTitle.middle_text = "补打卡规则"
        //设置返回健
        inflate.commonTitle.setOnLeftClickListener {
            finish()
        }

        inflate.checkInBtn.setOnClickListener {

            val requestData = JSONObject()
            requestData.put("make_up_date", repairDate)
            val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
            mViewModel.postData(stringBody)

        }

        mViewModel.commondata.observe(this, Observer {
            if (it.isSuccess){
                setResult(RESULT_OK)
                finish()
            }else{
                toast(it.msg)
            }
        })
    }
}