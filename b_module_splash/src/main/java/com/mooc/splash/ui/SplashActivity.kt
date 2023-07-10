package com.mooc.splash.ui

//import kotlinx.android.synthetic.main.activity_splash.*
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.DebugUtil
import com.mooc.common.utils.PackageUtils
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.common.utils.statusbar.StatusBarUtils
import com.mooc.commonbusiness.base.BaseApplication
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.interfacewrapper.ARouterNavigationCallbackWrapper
import com.mooc.commonbusiness.manager.CrashManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.AudioPlayService
import com.mooc.commonbusiness.route.routeservice.EbookService
import com.mooc.commonbusiness.route.routeservice.LoginService
import com.mooc.commonbusiness.route.routeservice.WebService
import com.mooc.commonbusiness.scheme.SchemeUtils
import com.mooc.commonbusiness.utils.ServerTimeManager
import com.mooc.commonbusiness.utils.graysetting.GrayModeSetting
import com.mooc.splash.R
import com.mooc.splash.SplashViewModel
import com.mooc.splash.api.SplashApi
import com.mooc.splash.databinding.ActivitySplashBinding
import com.mooc.splash.model.LaunchBean
import com.mooc.statistics.LogUtil
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    启动页
 * @Author:         xym
 * @CreateDate:     2020/8/7 1:17 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/7 1:17 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class SplashActivity : AppCompatActivity() {

    private val DELAYED_TIME: Long = 1000
    private val START_APP_LINK = "moocnd://"  //外部跳转app

    var hasAgree: Boolean = false       //是否同意过隐私策略,默认false
    private lateinit var inflater: ActivitySplashBinding
    val viewModel by viewModels<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        inflater = ActivitySplashBinding.inflate(layoutInflater)
        ServerTimeManager.getInstance().serviceTime
//        GrayModeSetting.getMode(this, window.decorView)
        viewModel.getMode(window.decorView)
        setContentView(inflater.root)
        checkSign()
        hasAgree = SpDefaultUtils.getInstance().getBoolean(SpConstants.HAS_AGREE_MENT, false)

        if (hasAgree) {
            loadOpertionData()
            initThirdPartSdk()
        } else {
            showStatementDialog()
        }
        getIntentData()
        inflater.tvSkip.setOnClickListener { enterHomePage() }

    }

    //外部跳转App使用
    private fun getIntentData() {
        val action = intent.action
        val uri = intent.data
        if (Intent.ACTION_VIEW == action) {
            if (uri != null && uri.toString().contains(START_APP_LINK)) {  //可测试uri
                var strLinkUrl: String =
                    SchemeUtils.substringAfter(uri.toString(), START_APP_LINK)!!
                strLinkUrl = if (strLinkUrl.contains(IntentParamsConstants.INTENT_KEY_COURSE_ID)) {
                    "course://$strLinkUrl"
                } else {
                    uri.toString()
                }
                if (!TextUtils.isEmpty(strLinkUrl)) {
//                    enterHomePage()
                    SpDefaultUtils.getInstance().putString(SpConstants.KEY_LOCATION, strLinkUrl)
                } else {
                    SpDefaultUtils.getInstance().putString(SpConstants.KEY_LOCATION, "")
                }
                return
            }
            //  测试uri  http://192.168.9.104:11112/api/mobile?resource_type=31&resource_id=23&is_share=1
            if (uri != null && SchemeUtils.isHttpUrl(uri.toString())) {
                SpDefaultUtils.getInstance().putString(SpConstants.KEY_LOCATION, uri.toString())
//                enterHomePage()
            }
        }
    }

    /**
     * 检验安装的apk签名是否一致，不一致则直接退出应用
     */
    private fun checkSign() {
        if (!PackageUtils.checkSignature(this).equals(ApiService.SHA1)) {
            Toast.makeText(this, getString(R.string.str_sign_unanimous), Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    /**
     * 初始化第三方的sdk
     */
    fun initThirdPartSdk() {
        initWx()
        initUMShare()
        initBugly()
        initZySdk()
        initXimalaya()
    }

    private fun initWx() {
        ARouter.getInstance().navigation(LoginService::class.java).registerWx()
    }

    private fun initXimalaya() {
        ARouter.getInstance().navigation(AudioPlayService::class.java).initSdk()
    }


    /**
     * 加载运营信息
     */
    private fun loadOpertionData() {
        //开启5s倒计时
        startEnterTime(false)
        //下载启动图
        lifecycleScope.launch {
            try {
                val lunchResponse =
                    ApiService.getRetrofit().create(SplashApi::class.java).getLunch().await()

                Glide.with(this@SplashActivity)
                    .load(lunchResponse.launch_picture)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            inflater.ivLaunch.postDelayed({

                                StatusBarUtils.setTransparent(this@SplashActivity)
                                getWindow().setBackgroundDrawable(ColorDrawable(Color.WHITE));
                                inflater.ivLaunch.animate().alpha(1f).setDuration(250).start()
                                inflater.ivLaunch.setImageDrawable(resource)

                                initImageClickListener(lunchResponse)
                                startEnterTime()
                            }, 2000)
                        }


                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)

                            startEnterTime()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })

            } catch (e: Exception) {
                loge(e.toString())

//                startEnterTime(false)
            }
        }
    }


    /**
     * 注册点击事件
     */
    private fun initImageClickListener(lunchResponse: LaunchBean) {
        if (lunchResponse.resource_type != -1) {
            inflater.ivLaunch.setOnClickListener { //进入home页后再进入资源页面
                enterHomePage()
                ResourceTurnManager.turnToResourcePage(lunchResponse)
                LogUtil.addClickLogNew(
                    LogEventConstants2.P_ADV,
                    lunchResponse._resourceId,
                    lunchResponse._resourceType.toString(),
                    lunchResponse.title,
                    "${LogEventConstants2.typeLogPointMap[lunchResponse._resourceType]}#${lunchResponse._resourceId}"
                )
            }
        }
    }


    /**
     * 展示用户隐私政策保护弹窗
     */

    private fun showStatementDialog() {
        val agreeMentDialog = DialogAgreeMeant(
            this,
            R.style.DefaultDialogStyle,
            object : DialogAgreeMeant.InfoCallback {
                override fun onRight() {
                    hasAgree = true
                    initThirdPartSdk()
//                    initX5()
                    SpDefaultUtils.getInstance().putBoolean(SpConstants.HAS_AGREE_MENT, true)
                    showGuideActivity()
                }

                override fun onLeft() {
                    finish()
                }
            })
        agreeMentDialog.setStrLeft("取消")
        agreeMentDialog.setStrRight("同意")
        agreeMentDialog.setTvLeftColor(ContextCompat.getColor(this, R.color.color_616161))
        agreeMentDialog.setTvRightColor(ContextCompat.getColor(this, R.color.color_white))
        agreeMentDialog.show()
    }

    var subscribe: Disposable? = null
    private var mCountdownJob: Job? = null

    /**
     * 开启5s倒计时
     */
    private fun startEnterTime(show: Boolean = true) {
        subscribe?.dispose()
        mCountdownJob?.cancel()
        mCountdownJob = countDownCoroutines(5, lifecycleScope,
            onTick = { second ->
                if (second == 0) {
                    enterHomePage()
                } else {
                    inflater.tvSkip.text = "${second}s跳过"
                }
            }, onStart = {
                // 倒计时开始
                if (show) {
                    inflater.tvSkip.visibility = View.VISIBLE
                }
            }, onFinish = {
                // 倒计时结束，重置状态

            })

    }

    fun countDownCoroutines(
        total: Int,
        scope: CoroutineScope,
        onTick: (Int) -> Unit,
        onStart: (() -> Unit)? = null,
        onFinish: (() -> Unit)? = null,
    ): Job {
        return flow {
            for (i in total downTo 0) {
                emit(i)
                delay(1000)
            }
        }.flowOn(Dispatchers.Main)
            .onStart { onStart?.invoke() }
            .onCompletion { onFinish?.invoke() }
            .onEach { onTick.invoke(it) }
            .launchIn(scope)
    }

    private fun initUMShare() {
        UMConfigure.init(
            BaseApplication.instance,
            "5e4751db0feb474e621fb29c",
            "umeng",
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
        UMConfigure.setLogEnabled(false)
        PlatformConfig.setWeixin("wxd5eb28cd2aa4bfab", "e06bf5dcbb97ff468836a2242054cb53")
        PlatformConfig.setWXFileProvider("com.moocxuetang.fileProvider") //适配android 11
        PlatformConfig.setQQZone("1106296912", "NS3As2xN2fYvnQTq")

    }


    /**
     * 进入主页
     */
    private fun enterHomePage() {
        subscribe?.dispose()
        mCountdownJob?.cancel()

        ARouter.getInstance().build(Paths.PAGE_HOME)
            .navigation(this, object : ARouterNavigationCallbackWrapper() {
                override fun onArrival(postcard: Postcard?) {
                    finish()
                }
            })
    }

    /**
     * 进入引导页
     */
    private fun enterGuidePage() {
        inflater.ivLaunch.postDelayed({
            ARouter.getInstance().build(Paths.PAGE_GUIDE)
                .navigation(this, object : ARouterNavigationCallbackWrapper() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
        }, DELAYED_TIME)
    }

    /**
     * 是否显示过引导页
     */
    private fun showGuideActivity() {
        val lastBoot = SpDefaultUtils.getInstance().getLong(SpConstants.KEY_LAST_BOOT, -1)
        SpDefaultUtils.getInstance().putLong(SpConstants.KEY_LAST_BOOT, System.currentTimeMillis())
        if (lastBoot == -1L) {
            //进入引导页
            enterGuidePage()
        } else {
            //开启倒计时进入
            startEnterTime()
        }
    }


    /**
     * 配置bugly
     */
    private fun initBugly() {
        CrashReport.initCrashReport(getApplicationContext(), "db1d2e2ff7", DebugUtil.debugMode);
    }

    fun initZySdk() {
        val navigation = ARouter.getInstance().navigation(EbookService::class.java)
        navigation.setDisallowPrivacy(true)
        navigation.initSdk()
    }

    fun initCrashHandler() {
        //初始化 错误日子系统
        CrashManager.getInstance(this);
    }

    /**
     * 初始化X5 webview
     */
    private fun initX5() {
        ARouter.getInstance().navigation(WebService::class.java).initX5()
    }

    /**
     * @method  ondestory
     * @description 描述一下方法的作用
     * @date: 2020/8/7 1:14 PM
     * @author: xym
     * @param
     * @return unit
     */
    override fun onDestroy() {
        super.onDestroy()

        subscribe?.dispose()
    }
}
