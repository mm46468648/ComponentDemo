package com.mooc.studyroom.manager

import android.content.Context
import android.text.TextUtils
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.common.ktextends.toast

object CourseItemClickPresenter {
    @JvmStatic
    fun onClickCourse(context: Context, courseResource: CourseBean) {
        val courseStatus = courseResource.course_status
        if (!TextUtils.isEmpty(courseStatus) && "0" == courseStatus && "0" == courseResource.platform.toString()) {
            context.toast("资源已下线")
            return
        }
        ResourceTurnManager.turnToResourcePage(courseResource)

//        ResourceTurnManager.turnToResoursePage(recommendColumn)
//    ARouter.getInstance().build(Paths.PAGE_COURSE_DETAIL)
//                .withString(IntentParamsConstants.COURSE_PARAMS_ID,courseResource.getId())
//                .navigation();
    }
}