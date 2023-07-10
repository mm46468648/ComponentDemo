package com.mooc.studyroom.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.CommonTitleLayout
import com.mooc.studyroom.R
//import kotlinx.android.synthetic.main.studyroom_activity_contribute_task.*

/**
 * 贡献值任务
 */
@Route(path = Paths.PAGE_CONTRIBUTE_TASK)
class ContributeTaskActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.studyroom_activity_contribute_task)

        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        commonTitle.setOnLeftClickListener { finish() }
        commonTitle.setOnRightTextClickListener(View.OnClickListener {
            //跳转home，荣誉墙到贡献值任务
            ARouter.getInstance().build(Paths.PAGE_HOME)
                    .withInt(IntentParamsConstants.HOME_SELECT_POSITION,3)
                    .withInt(IntentParamsConstants.HOME_SELECT_CHILD_POSITION,1)
                    .navigation()
        })
    }
}