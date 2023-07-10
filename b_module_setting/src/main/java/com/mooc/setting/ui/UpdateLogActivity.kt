package com.mooc.setting.ui

import android.os.Bundle
import android.view.LayoutInflater
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.setting.databinding.SetActivityUpdateLogBinding
import retrofit2.http.Path

@Route(path = Paths.SERVICE_SETTING_UPDATE_LOG)
class UpdateLogActivity : BaseActivity(){

    lateinit var mbindingLayout : SetActivityUpdateLogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mbindingLayout =  SetActivityUpdateLogBinding.inflate(layoutInflater)
        setContentView(mbindingLayout.root)

        mbindingLayout.commonTitle.setOnLeftClickListener { finish() }
    }
}