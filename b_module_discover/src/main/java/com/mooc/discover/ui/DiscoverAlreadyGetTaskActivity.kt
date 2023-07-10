package com.mooc.discover.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.fragment.AlreadyGetTaskFragment
import com.mooc.resource.widget.CommonTitleLayout

@Route(path = Paths.PAGE_DISCOVER_ALREADY_GET_TASK)
class DiscoverAlreadyGetTaskActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_task)

        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        commonTitle?.middle_text = "已领取的任务"
        commonTitle?.setOnLeftClickListener { finish() }
        commonTitle.right_text = getString(R.string.history_task)
        commonTitle.tv_right?.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_DISCOVER_HISTORY_GET_TASK).navigation() }
        supportFragmentManager.beginTransaction().add(R.id.flContainer, AlreadyGetTaskFragment()).commit()

    }
}