package com.mooc.my.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.R
import com.mooc.my.databinding.ActivityCustomerServiceBinding
//import kotlinx.android.synthetic.main.activity_customer_service.*

/**
 *联系客服
 * @author limeng
 * @date 2020/10/14
 */
@Route(path = Paths.PAGE_CUSTOMER_SERVICE)
class CustomerServiceActivity : BaseActivity() {
    private lateinit var inflate : ActivityCustomerServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate = ActivityCustomerServiceBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initListener()
    }

    private fun initListener() {
        inflate.commonTitle.setOnLeftClickListener { finish() }
        val phoneNum = inflate.tvPhone.text.toString().trim()
        inflate.tvPhone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val data = Uri.parse("tel:$phoneNum")
            intent.data = data
            startActivity(intent)
        }
    }
}