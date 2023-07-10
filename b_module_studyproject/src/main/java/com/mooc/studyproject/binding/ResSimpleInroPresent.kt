package com.mooc.studyproject.binding

import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths

class ResSimpleInroPresent {

    companion object{

        /**
         * tiaozhuandaoxuexixiangmu
         */
        @JvmStatic
        open fun toStudyProject(planId : String){
            ARouter.getInstance().build(Paths.PAGE_STUDYPROJECT).withString(IntentParamsConstants.STUDYPROJECT_PARAMS_ID,planId).navigation()
        }
    }

}