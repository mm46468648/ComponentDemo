package com.mooc.setting.ui

import android.os.Bundle
import android.text.Editable
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.toastMain
import com.mooc.common.utils.GsonManager
import com.mooc.common.utils.sharepreference.SpUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.setting.databinding.SetActivityResourceTestBinding

@Route(path = Paths.PAGE_CONTROLLER_TEST_RESOURCE)
class ResourceTurnTestActivity : BaseActivity(){

    lateinit var inflate : SetActivityResourceTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = SetActivityResourceTestBinding.inflate(layoutInflater)
        setContentView(inflate.root)

        //从缓存中取
        val value = SpUtils.get().getValue(SpConstants.SP_LAST_TEST_RESOURCE, "")
        if(value.isNotEmpty()){
            val hashMap = GsonManager.getInstance().fromJson<HashMap<String, String>>(value, HashMap::class.java)
            inflate.etRID.setText(hashMap.get("resourceId")?:"")
            inflate.etRType.setText(hashMap.get("resourceType")?:"")
            inflate.etRLink.setText(hashMap.get("resourceLink")?:"")
        }
    }

    fun turnTo(v: View){
        val etRID = inflate.etRID.text.toString()
        val etRType = inflate.etRType.text.toString()
        var etRLink = inflate.etRLink.text.toString()

        if(etRID.isEmpty() || etRType.isEmpty()){
            toast("资源id或type为空")
            return
        }

        ResourceTurnManager.turnToResourcePage(object : BaseResourceInterface {
            override val _resourceId: String
                get() = etRID
            override val _resourceType: Int
                get() = etRType.toInt()
            override val _other: Map<String, String>
                get() = hashMapOf(IntentParamsConstants.WEB_PARAMS_URL to etRLink)
        })

//        etRLink = "http://wx.npou.net/mobile/?vconsole=1&xuetangTest=-1"

        val hashMapOf = hashMapOf<String, String>(
            "resourceId" to etRID,
            "resourceType" to etRType,
            "resourceLink" to etRLink
        )

        val toJson = GsonManager.getInstance().toJson(hashMapOf)
        SpUtils.get().putValue(SpConstants.SP_LAST_TEST_RESOURCE, toJson)
    }

}