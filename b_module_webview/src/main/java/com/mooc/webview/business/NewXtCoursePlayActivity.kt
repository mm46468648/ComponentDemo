package com.mooc.webview.business

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.webview.R

@Route(path = Paths.PAGE_COURSE_NEWXT_PLAY)
class NewXtCoursePlayActivity : BaseResourceWebviewActivity(){

    val classRoomId by extraDelegate(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID,"")
    val courseBean by extraDelegate<CourseBean?>(IntentParamsConstants.COURSE_PARAMS_DATA,null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater.commonTitleLayout.setRightSecondIconRes(R.drawable.web_ic_xtcourse_download)
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }
        inflater.commonTitleLayout.tv_right?.visiable(false)
        inflater.commonTitleLayout.setOnSecondRightIconClickListener {
            ARouter.getInstance().build(Paths.PAGE_COURSE_NEWXT_DOWNLOAD)
                .withString(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID,classRoomId)
                .withParcelable(IntentParamsConstants.COURSE_PARAMS_DATA,courseBean)
                .navigation()
        }
    }


}