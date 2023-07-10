package com.mooc.studyproject.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.CommonTitleLayout
import com.mooc.studyproject.R
import com.mooc.studyproject.fragment.NoticeListFragment

@Route(path = Paths.PAGE_NOTICE_LIST)
class NoticeMoreListActivity : BaseActivity() {
    var planId: String = ""
    var fragment:NoticeListFragment?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.studyproject_activity_notice_more_list)
//        if (intent.getStringExtra("id") != null) { }
        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        planId=intent.getStringExtra("id")?:""
        fragment = NoticeListFragment()
       fragment?.planId=planId
        if (fragment != null) {
            supportFragmentManager.beginTransaction().add(R.id.commendfragment, fragment!!).commit()
        }
        commonTitle.middle_text=resources.getString(R.string.notice_more)
        commonTitle.setOnLeftClickListener { finish() }

    }
}