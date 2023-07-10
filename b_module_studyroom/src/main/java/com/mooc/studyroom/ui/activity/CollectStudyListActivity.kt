package com.mooc.studyroom.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.CommonTitleLayout
import com.mooc.studyroom.R

/**
 * 收藏的清单页面
 */
@Route(path = Paths.PAGE_COLLECT_STUDY_LIST)
class CollectStudyListActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.studyroom_activity_collect_studylist)
        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        commonTitle.setOnLeftClickListener { finish() }
    }

}