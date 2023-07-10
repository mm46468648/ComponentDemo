package com.mooc.commonbusiness.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.LoginService
//import kotlinx.android.synthetic.main.common_pop_toweixn_sure.*

/**
    跳转小程序的
    dialog类型Activity
 * @Author limeng
 * @Date 2021/5/14-4:30 PM
 */
@Route(path = Paths.PAGE_TO_WX_PROGRAM_DIALOG)
class ToWeixinProgramDialog : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.common_pop_toweixn_sure)
        val tvCancle = findViewById<View>(R.id.tvCancle)
        val tvConfirm = findViewById<View>(R.id.tvConfirm)
        tvCancle.setOnClickListener { finish()}
        tvConfirm.setOnClickListener {// 跳转到小程序吧
            val service=ARouter.getInstance().navigation(LoginService::class.java)
            service.toWeixinProgram()
            tvConfirm.postDelayed({
                finish()
            },500)
        }
    }

}