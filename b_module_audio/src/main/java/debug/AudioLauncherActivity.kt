package debug

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.commonbusiness.route.Paths


class AudioLauncherActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onResume() {
        super.onResume()

        runOnMainDelayed(2000) {
            ARouter.getInstance().build(Paths.PAGE_ALBUM).withString("params_albumid","246622").navigation()
        }

    }
}