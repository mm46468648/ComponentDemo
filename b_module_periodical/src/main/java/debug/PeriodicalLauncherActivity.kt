package debug

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.commonbusiness.route.Paths


class PeriodicalLauncherActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        GlobalsUserManager.xuetangToken = "d99250b15da99cf52717c5c36bcc65936a96e052"
        GlobalsUserManager.appToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6Ildjb21tYW5kZXIiLCJvcmlnX2lhdCI6MTYzNDE5NjQ4NSwidXNlcl9pZCI6NjcsImVtYWlsIjoiIiwiZXhwIjoxNjY1NzMyNDg1fQ.QX2nI_w8m2s5oevabElmd-9oOhrNHOlxgl6bMpnds6k"
    }

    override fun onResume() {
        super.onResume()


        runOnMainDelayed(2000) {
            //多级列表测试id 2023174
//            ARouter.getInstance().build(Paths.PAGE_COURSE_PLAY).withString(IntentParamsConstants.COURSE_XT_PARAMS_ID,"course-v1:TsinghuaX+THU201605X+sp").navigation()
            ARouter.getInstance().build(Paths.PAGE_PUBLICATION_DETAIL).withString(IntentParamsConstants.PARAMS_RESOURCE_ID,"6").greenChannel().navigation()
        }

    }
}