package com.mooc.discover.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.fragment.HistoryTaskFragment
import com.mooc.resource.widget.CommonTitleLayout

//import kotlinx.android.synthetic.main.activity_discover_task.*

@Route(path = Paths.PAGE_DISCOVER_HISTORY_GET_TASK)
class DiscoverHistoryTaskActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_task)

        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)

        commonTitle?.middle_text = getString(R.string.history_task)
        commonTitle?.setOnLeftClickListener { finish() }

        supportFragmentManager.beginTransaction().add(R.id.flContainer, HistoryTaskFragment()).commit()

    }
}