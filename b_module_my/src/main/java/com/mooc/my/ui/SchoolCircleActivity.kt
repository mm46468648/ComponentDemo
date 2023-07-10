package com.mooc.my.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.R
import com.mooc.resource.widget.CommonTitleLayout

@Route(path = Paths.PAGE_SCHOOL_CIRCLE)
class SchoolCircleActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_circle)
        findViewById<CommonTitleLayout>(R.id.common_title_layout).setOnLeftClickListener { finish() }
    }
}