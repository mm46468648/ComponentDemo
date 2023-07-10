package com.mooc.home.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.text.TextUtils
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.changeskin.SkinManager
import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.annotation.ExecuteTime
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.interfaces.StatusBarDarkable
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.model.eventbus.UserOnClickMyEvent
import com.mooc.commonbusiness.model.privacy.PrivacyPolicyCheckBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ApkUpdateService
import com.mooc.commonbusiness.route.routeservice.AudioFloatService
import com.mooc.commonbusiness.route.routeservice.AudioPlayService
import com.mooc.commonbusiness.scheme.*
import com.mooc.commonbusiness.utils.CheckAccessibilityUtil
import com.mooc.commonbusiness.viewmodel.CommonViewModel
import com.mooc.home.HttpService
import com.mooc.home.R
import com.mooc.home.constans.TagConstants
import com.mooc.home.databinding.HomeActivityHomeBinding
import com.mooc.home.ui.discover.DiscoverFragment
import com.mooc.home.ui.hornowall.HonorFragment
import com.mooc.home.ui.my.MyFragment
import com.mooc.home.ui.pop.AudioPlayTipPop
import com.mooc.home.ui.studyroom.StudyRoomFragment
import com.mooc.home.ui.todaystudy.TodayStudyFragment
import com.mooc.statistics.LogManager
import com.mooc.statistics.LogUtil
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.socialize.UMShareAPI
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 首页页面
 */
@Route(path = Paths.PAGE_HOME)
class HomeActivity : BaseActivity(), StatusBarDarkable {

    //fragment数组
    private val mFragmentArray: Array<Fragment?> = arrayOfNulls<Fragment>(5)

    //fragment tag数组
    private val fragmentTagArray = arrayOf<String>(
        TagConstants.TAG_HOME_DISCOVER,
        TagConstants.TAG_HOME_TODAYSTUDY,
        TagConstants.TAG_HOME_STUDYROOM,
        TagConstants.TAG_HOME_HORNER,
        TagConstants.TAG_HOME_MY
    )

    //当前fragment pageIndex
    private var currentPage = 0
    val CURRENT_INDEX = "current_index"

    //apk更新服务
    val apkApkUpdate: ApkUpdateService by lazy {
        ARouter.getInstance().navigation(ApkUpdateService::class.java)
    }
    val mViewModel by viewModels<HomeViewModel>()
    val commonViewModel by viewModels<CommonViewModel>()

    val mPresenter by lazy {
        HomePresenter(this)
    }
    private var mMenuItemEvent //推送获取的数据
            : MenuItemEvent? = null

    private lateinit var inflater: HomeActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SkinManager.getInstance().register(this)
        inflater = HomeActivityHomeBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        //恢复数据
        onRestoreFragment(savedInstanceState)

        //eventBus
        EventBus.getDefault().register(this)
        //初始化5个fragment
        initFragment()

        //检查sp中是否将学习室置为首页
        checkFirstPage()
        //选择默认选中fragment
        selectFragment(currentPage)
        //初始化view
        initView()

        onCreateEvent()

        getPrivacyPolicyCheck()

        updaloadLogImmediate()

        CheckAccessibilityUtil.getInstance().startCheckAccessibilityService()

    }

    /**
     * 启动时上传下打点日志
     */
    fun updaloadLogImmediate() {
        Looper.myQueue().addIdleHandler(MessageQueue.IdleHandler {
            LogManager.uploadFile()
            return@IdleHandler false
        })
    }

    override fun onNewIntent(intent: Intent?) {
        setIntent(intent)

        intent?.let {
            onIntentOpenPosition(it)
        }

        super.onNewIntent(intent)


    }

    /**
     * 通过intent切换page
     */
    private fun onIntentOpenPosition(intent: Intent) {
        if (intent.hasExtra(IntentParamsConstants.HOME_SELECT_POSITION)) {
            val selectPosition =
                intent.getIntExtra(IntentParamsConstants.HOME_SELECT_POSITION, currentPage)
            selectFragment(selectPosition)

            if (intent.hasExtra(IntentParamsConstants.HOME_SELECT_CHILD_POSITION)) {
                val childSelectPosition =
                    intent.getIntExtra(IntentParamsConstants.HOME_SELECT_CHILD_POSITION, 0)
                LiveDataBus.get()
                    .with(LiveDataBusEventConstants.EVENT_CHILDE_FRAGMENT_SELECTPOSITION)
                    .postStickyData(Pair<Int, Int>(selectPosition, childSelectPosition))
            }
        }
    }

    private fun getSpecialMedal() {
        mPresenter.getMedia()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

    }

    //检查用户昵称是否违规
    fun checkUserInfo() {
        lifecycleScope.launchWhenCreated {
            flow<UserInfo> {
                emit(HttpService.homeApi.getUserInfo())
            }.catch {

            }.collect {
                GlobalsUserManager.userInfo = it
                it.check_user_info?.let { checkUserInfo ->
                    if (!checkUserInfo.check_avatar_status || !checkUserInfo.check_name_status) {
                        toast(checkUserInfo.check_info)
                        ARouter.getInstance()
                            .build(Paths.PAGE_USERINFO_EDIT)
                            .navigation()
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPushEvent(event: MenuItemEvent?) {
        mMenuItemEvent = event
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun showPushPage(event: MenuItemEvent) {
        val bundle = event.bundle
        if (bundle != null) {
            val pageCode = bundle.getInt(MyPushKey.CODE_KEY, -1)
            if (pageCode == SchemasBlockList.TYPE_DEFAULT) {
                if (inflater.tabHome.selectedTabPosition !== 0) {
                    selectFragment(0)
                }
            } else if (pageCode == SchemasBlockList.TYPE_RECOMMEND) {
                if (inflater.tabHome.selectedTabPosition !== 1) {
                    selectFragment(1)
                }
            } else if (pageCode == SchemasBlockList.TYPE_STUDY_ROOM) {
                if (inflater.tabHome.selectedTabPosition !== 2) {
                    selectFragment(2)
                }
            } else if (pageCode == SchemasBlockList.TYPE_RESOURCE) {
                if (inflater.tabHome.selectedTabPosition !== 3) {
                    selectFragment(3)
                }
            } else if (pageCode == SchemasBlockList.TYPE_MY) {
                if (inflater.tabHome.selectedTabPosition !== 4) {
                    selectFragment(4)
                }
            } else if (pageCode == SchemasBlockList.TYPE_COURSE) {
                val data = bundle.getSerializable(MyPushKey.DATA_KEY) as SchemasData?
                if (data != null) {
                    SchemeUtils.turnToPage(
                        data.getStrValue(MyPushKey.LINK_KEY),
                        data.getStrValue(MyPushKey.TYPE_KEY).toInt(), null, null
                    )
                }

            } else {
                SchemeUtils.toOtherPage(bundle)
            }
        } else {
            if (inflater.tabHome.selectedTabPosition !== 0) {
                selectFragment(0)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        SchemeUtils.appLinkActivity(this@HomeActivity);
        if (mMenuItemEvent != null && mMenuItemEvent!!.isPush) {
            mMenuItemEvent!!.isPush = false
            showPushPage(mMenuItemEvent!!)
        }
        Looper.myQueue().addIdleHandler(MessageQueue.IdleHandler {
            //检查更新
            apkApkUpdate.checkApkUpdate(this)

            if (GlobalsUserManager.isLogin()) {
                checkUserInfo()
                getSpecialMedal()
                getStudyPlanCompetion()
                getDialogMessage()
            }

            return@IdleHandler false
        })

    }

    /**
     * 获取完成学习计划的弹框
     */
    private fun getStudyPlanCompetion() {
        mPresenter.getStudyPlanCompletion()
    }

    fun onCreateEvent() {
        //上传时间
        mPresenter.checkTime()
        //请求存储权限
        mPresenter.requestPermission()
        onUserEvent(UserLoginStateEvent(GlobalsUserManager.userInfo))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(userInfo: UserLoginStateEvent) {
//        loge("${this::class.java.simpleName}收到了登录事件")
        if (userInfo.userInfo != null) {
            getDailyRead()
            CrashReport.setUserId(GlobalsUserManager.uid)
        }
    }


    /**
     * 获取每日一读弹窗
     */
    private fun getDailyRead() {
        mViewModel.getDailyReading()?.observe(this, {
            if (it != null) {
                mPresenter.showDailyRead(it)
            }
        })

//        val dailyReadBean = DailyReadBean()
//        dailyReadBean.image_url = "https://static.moocnd.ykt.io/moocnd/img/0df01a8d473719b66eac5d2384625808.jpg"
//        mPresenter.showDailyRead(dailyReadBean)
    }

    /**
     * 隐私政策弹窗查询
     */
    private fun getPrivacyPolicyCheck() {

        val strVersionName =
            SpDefaultUtils.getInstance().getString(SpConstants.SP_PRIVACY_VERSION, "")
        commonViewModel.getPrivacyPolicyCheck(strVersionName).observe(this, Observer {

            var version = ""
            var privacyPolicyCheckBean: PrivacyPolicyCheckBean? = null
            if (it != null) {
                privacyPolicyCheckBean = it.data
                if (privacyPolicyCheckBean != null) {
                    if (privacyPolicyCheckBean.version?.isNotEmpty() == true) {
                        version = privacyPolicyCheckBean.version!!
                    }
                }
            }

            if (TextUtils.isEmpty(strVersionName)) {  //第一次空的时候不弹  存下版本号
                SpDefaultUtils.getInstance().putString(SpConstants.SP_PRIVACY_VERSION, version)
                return@Observer
            }

            if (version.isNotEmpty()) {
                mPresenter.showPrivacyPolicy(privacyPolicyCheckBean)
            }

        })
    }

    fun getDialogMessage() {
        //获取弹窗消息
        mViewModel.getAlertMsg().observe(this, Observer {
            mPresenter.showAlertDialog(it)
        })
    }

    /**
     * 检查是否将学习室置为首页
     * 必须是登录状态下
     */
    private fun checkFirstPage() {
        if (!GlobalsUserManager.isLogin()) return
        val boolean = SpDefaultUtils.getInstance().getBoolean(SpConstants.STUDY_ROOM_FIRST, false)
        if (boolean) {
            currentPage = 2
        }
    }

    /**
     * 初始化View
     */
    private fun initView() {
        inflater.tabHome.setOnItemSelectListener { position -> selectFragment(position) }
    }

    /**
     * 初始化5个Fragment
     */
    @ExecuteTime
    private fun initFragment() {
        if (mFragmentArray[0] == null) {
            mFragmentArray[0] = DiscoverFragment()
        }
        if (mFragmentArray[1] == null) {
            mFragmentArray[1] = TodayStudyFragment()
        }
        if (mFragmentArray[2] == null) {
            mFragmentArray[2] = StudyRoomFragment()
        }
        if (mFragmentArray[3] == null) {
            mFragmentArray[3] = HonorFragment()
        }
        if (mFragmentArray[4] == null) {
            mFragmentArray[4] = MyFragment()
        }
    }

    private var currentFragment: Fragment? = null

    /**
     * 选择显示Fragment
     * @param position 位置
     */
    private fun selectFragment(position: Int) {
        //切换fragment的显示
        val fragment: Fragment? = mFragmentArray[position]
        fragment?.apply {
            if (currentFragment !== this) { //  判断传入的fragment是不是当前的currentFragmentgit
                val transaction = supportFragmentManager.beginTransaction()
                if (currentFragment != null) transaction.hide(currentFragment!!) //  不是则隐藏
                currentFragment = this //  然后将传入的fragment赋值给currentFragment
                if (!this.isAdded) { //  判断传入的fragment是否已经被add()过
                    transaction.add(R.id.fragmentContainer, this).show(this)
                        .commitAllowingStateLoss()
                } else {
                    transaction.show(this).commitAllowingStateLoss()
                }
            }
        }

        //赋值当前选中位置
        currentPage = position

        //行为打点
        statisticsAction(position)

//        showAudioLayout()
        //底部导航同步显示
        inflater.tabHome.select(position)
        if (currentPage == 4) {
            EventBus.getDefault().post(UserOnClickMyEvent(1))
        }
    }


    /**
     * 统计行为
     */
    private fun statisticsAction(position: Int) {
        LogUtil.addClickLogNew(
            LogEventConstants2.P_HOME, "${position + 1}",
            LogEventConstants2.ET_NAV,
            LogEventConstants2.NavStringMap[position] ?: ""
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        /*fragment不为空时 保存*/
        for (i in mFragmentArray.indices) {
            //确保fragment是否已经加入到fragment manager中
            val fragment: Fragment? = mFragmentArray[i]
            if (fragment != null && fragment.isAdded) {
                //保存已加载的Fragment
                supportFragmentManager.putFragment(
                    outState, fragmentTagArray[i],
                    fragment
                )
            }
        }
        //传入当前选中的tab值，在销毁重启后再定向到该tab
        outState.putInt(CURRENT_INDEX, currentPage)
        super.onSaveInstanceState(outState)
    }

    /**
     * 恢复因为Activity在后台回收时保存的Fragment
     * @param savedInstanceState
     */
    private fun onRestoreFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            /*获取保存的fragment  没有的话返回null*/
            for (i in 0..4) {
                val fragment =
                    supportFragmentManager.getFragment(savedInstanceState, fragmentTagArray.get(i))
                if (fragment != null) {
                    mFragmentArray[i] = fragment
                }
            }
            currentPage = savedInstanceState.getInt(CURRENT_INDEX, 1)
        }
    }


    private var lastExitTime: Long = 0 //上一次按返回键时间
    private var exitDuration = 3000 //两次按返回间隔

    /**
     * ------------处理键盘事件-------------
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN) {
            exitApp()
            return true
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 退出App
     */
    private fun exitApp() {
        //如果有音频是否正在播放,则弹出最小化的提示框
        val navigation = ARouter.getInstance().navigation(AudioPlayService::class.java)
        if (navigation.isPlaying()) {
            showAudioPlayTip()
            return
        }

        if ((System.currentTimeMillis() - lastExitTime) > exitDuration) {
            toast(getString(R.string.text_exit_tip))
            lastExitTime = System.currentTimeMillis()
        } else {
            onBackPressed()
        }


    }


    private fun showAudioPlayTip() {
        XPopup.Builder(this)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .asCustom(AudioPlayTipPop(this) {
                if (it == 0) {
                    pageToHome()
                } else if (it == 1) {
                    ARouter.getInstance().navigation(AudioFloatService::class.java).release()
                    onBackPressed()
                }
            })
            .show()
    }

    /**
     * 将主页退到后台
     */
    private fun pageToHome() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    /**
     * 释放资源
     */
    override fun onDestroy() {
        super.onDestroy()
        SkinManager.getInstance().unregister(this)
        EventBus.getDefault().unregister(this)
        mPresenter.release()
        ARouter.getInstance().navigation(AudioFloatService::class.java).release()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {     //我的页面发起的登录请求
            (mFragmentArray[4] as MyFragment).onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun darkMode(): Boolean = false
}