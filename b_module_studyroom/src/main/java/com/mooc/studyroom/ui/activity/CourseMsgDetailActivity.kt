package com.mooc.studyroom.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.CommonTitleLayout
import com.mooc.studyroom.R
import com.mooc.studyroom.ui.fragment.mymsg.CourseMsgDetailsFragment

/**
 *课程消息详情页面
 * @author limeng
 * @date 2021/3/11
 */
@Route(path = Paths.PAGE_COURSE_MSG_DETAIL)
class CourseMsgDetailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.studyroom_activity_course_msg_detail)

        val commonTitle = findViewById<CommonTitleLayout>(R.id.commonTitle)
        val title=intent.getStringExtra(IntentParamsConstants.COURSE_PARAMS_TITLE)
        val course_id=intent.getStringExtra(IntentParamsConstants.COURSE_PARAMS_ID) ?: ""
        intent.getStringExtra(IntentParamsConstants.COURSE_PARAMS_TITLE)
        commonTitle?.middle_text = title

        commonTitle?.setOnLeftClickListener { finish() }



        supportFragmentManager.beginTransaction().add(R.id.fragment, CourseMsgDetailsFragment.newInstance(course_id,"")).commit()
    }
}