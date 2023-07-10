package com.mooc.home.ui.todaystudy.adjusttarget

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.home.R
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.CommonTitleLayout

//import kotlinx.android.synthetic.main.home_activity_adjust_target.*

@Route(path = Paths.PAGE_ADJUST_TARGET)
class AdjustTargetActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.home_activity_adjust_target)

        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        commonTitle.setOnLeftClickListener { finish() }
    }
}