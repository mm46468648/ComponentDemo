package com.mooc.studyroom.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.CommonTitleLayout
import com.mooc.studyroom.R

/**
 * 积分明细页面
 */
@Route(path = Paths.PAGE_SCORE_DETAIL)
class ScoreDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.studyroom_activity_score_detail)

        initView()
    }

    private fun initView() {
        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        commonTitle.setOnLeftClickListener { finish() }
        commonTitle.setOnRightTextClickListener(View.OnClickListener {
            //跳转积分规则h5页面
            ARouter.getInstance().build(Paths.PAGE_SCORE_RULE).navigation()
        })


    }
}