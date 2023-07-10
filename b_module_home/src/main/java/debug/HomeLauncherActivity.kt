package debug

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.route.Paths
import com.mooc.home.R
import com.mooc.commonbusiness.route.routeservice.StudyRoomService

class HomeLauncherActivity : AppCompatActivity() {
    val TAG = "LauncherActivity"

    @JvmField
    @Autowired(name = Paths.SERVICE_STUDYROOM)
    var studyRoomService: StudyRoomService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_test)
//        ARouter.getInstance().inject(this)
//                findViewById<View>(R.id.tvFirst).setOnClickListener { ARouter.getInstance().build(Paths.PAGE_INNERCOMPONENT).navigation() }
        findViewById<View>(R.id.tvSecond).setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_WEB)
                    .withString("params_url", "https://www.baidu.com")
                    .withString("params_title", "百度一下，你就知道")
                    .navigation()
        }
        findViewById<View>(R.id.tvThrid).setOnClickListener { studyRoomService?.addNote("幕课学堂") }

        val homeViewModel = ViewModelProviders.of(this)[HomeLauncherViewModel::class.java]
//        val homeViewModel = ViewModelProviders.of(this, HomeViewModelFactory("姓名李四，年龄18")).get(HomeViewModel::class.java)
        homeViewModel.testStr.observe(this, Observer {
            Log.e(TAG,it.toString())
        })
        findViewById<View>(R.id.tvForth).setOnClickListener {
            homeViewModel.getTestUrl()
        }
    }
}