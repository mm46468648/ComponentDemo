package com.mooc.splash.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.splash.R
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogPageConstants
import com.mooc.commonbusiness.interfacewrapper.ARouterNavigationCallbackWrapper
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.LoginService
import com.mooc.commonbusiness.utils.UMUtil
import com.mooc.splash.databinding.SplashActivityGuideBinding
import com.umeng.socialize.UMShareAPI
//import kotlinx.android.synthetic.main.splash_activity_guide.*

/**
 * 引导页
 */
@Route(path = Paths.PAGE_GUIDE)
class GuideActivity : AppCompatActivity() {

    val imgResArray = intArrayOf(R.mipmap.splash_bg_guide_1, R.mipmap.splash_bg_guide_2, R.mipmap.splash_bg_guide_3)

    private lateinit var inflater : SplashActivityGuideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = SplashActivityGuideBinding.inflate(layoutInflater)
        //页面载入打点
//        LogUtil.addLoadLog(LogPageConstants.PID_WELCOME2)
        setContentView(inflater.root)

        inflater.vpGuide.adapter = MAdapter()
        inflater.vpGuide.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                showLoginButton(position)
            }
        })
        inflater.guidePageIndicator.setUpWithViewPager(inflater.vpGuide)
        inflater.btVisitorMode.setOnClickListener {
//            LogUtil.addClickLog(LogPageConstants.PID_WELCOME2,from = LogPageConstants.PID_WELCOME2,to = LogPageConstants.PID_HOMEPAGE)
            enterHomePage()
        }
        inflater.btWxLogin.setOnClickListener {
//            LogUtil.addClickLog(LogPageConstants.PID_WELCOME2,from = LogPageConstants.PID_WELCOME2,to = LogPageConstants.PID_WEI_CHAT_LOGIN)
//            ARouter.getInstance().build(Paths.PAGE_LOGIN).withInt("PARAMS_REQUEST_CODE", 0).navigation(this,0)

            ARouter.getInstance().navigation(LoginService::class.java).toLogin {
                enterHomePage()
            }
            UMUtil.toAuthWeiXin(this)

        }
    }

    fun enterHomePage() {
        ARouter.getInstance().build(Paths.PAGE_HOME).navigation(this, object : ARouterNavigationCallbackWrapper() {
            override fun onArrival(postcard: Postcard?) {
                finish()
            }
        })
    }

    /**
     * 展示登录弹窗
     */
    fun showLoginButton(position: Int) {
        val visibility = if (position == imgResArray.size - 1) View.VISIBLE else View.GONE
        inflater.btWxLogin.visibility = visibility
        inflater.btVisitorMode.visibility = visibility
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            enterHomePage()
        }
    }

    /**
     * adapter
     */
    inner class MAdapter : RecyclerView.Adapter<MViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
            val imageView = ImageView(parent.context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
            return MViewHolder(imageView)
        }

        override fun getItemCount(): Int = imgResArray.size


        override fun onBindViewHolder(holder: MViewHolder, position: Int) {
            holder.imageView.setImageResource(imgResArray[position])
        }

    }

    inner class MViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}