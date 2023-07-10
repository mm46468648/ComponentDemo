package com.mooc.home.ui.my

//import kotlinx.android.synthetic.main.home_fragment_home_my.*
import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.changeskin.SkinManager
import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.sp2px
import com.mooc.common.utils.PackageUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.MY_USER_ID
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.model.eventbus.UserOnClickMyEvent
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ApkUpdateService
import com.mooc.commonbusiness.utils.RlyUtils
import com.mooc.commonbusiness.utils.UMUtil
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.home.HttpService
import com.mooc.home.R
import com.mooc.home.databinding.HomeFragmentHomeMyBinding
import com.mooc.statistics.LogUtil
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MyFragment : Fragment() {

    //逻辑处理类
    val mPresenter: MyFragmentPresenter by lazy { MyFragmentPresenter() }

    val apkApkUpdate: ApkUpdateService by lazy {
        ARouter.getInstance().navigation(ApkUpdateService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    private var _binding: HomeFragmentHomeMyBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = HomeFragmentHomeMyBinding.inflate(layoutInflater, container, false)
        SkinManager.getInstance().injectSkin(_binding?.root)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        LogUtil.addLoadLog(LogPageConstants.PID_MY)
        updateUserInfo(GlobalsUserManager.userInfo)
        initView()
        initListener()

        //监听用户信息改变
        LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_USERINFO_CHANGE)
            .observe(this, Observer<UserInfo> {
                loge("${this::class.java.simpleName}收到了用户信息改变")
                updateUserInfo(it)
            })
    }

    private fun initView() {
        _binding?.csiVersionUpdate?.rightText = "V ${PackageUtils.getVersionName()}"

        //意见反馈添加摇一摇
        _binding?.feedBack?.findViewById<TextView>(R.id.tvRight)?.setText("摇一摇")


        //换肤
        val skinColor = if (SkinManager.getInstance().needChangeSkin()) {
            SkinManager.getInstance().resourceManager.getColor("colorPrimary")
        } else {
            requireContext().getColorRes(R.color.colorPrimary)
        }
        _binding?.feedBack?.findViewById<TextView>(R.id.tvRight)
            ?.setTextColor(skinColor)

        val skinDrawable = if (SkinManager.getInstance().needChangeSkin()) {
            SkinManager.getInstance().resourceManager.getDrawableByName("home_ic_my_shake")
        } else {
            ContextCompat.getDrawable(requireContext(), R.mipmap.home_ic_my_shake)
        }

        _binding?.feedBack?.findViewById<TextView>(R.id.tvRight)?.compoundDrawablePadding =
            6.dp2px()
        _binding?.feedBack?.findViewById<TextView>(R.id.tvRight)
            ?.setCompoundDrawablesWithIntrinsicBounds(
                skinDrawable,
                null,
                ContextCompat.getDrawable(requireContext(), R.mipmap.home_ic_my_right_arrow),
                null
            )

        //有版本更新要显示
        if (apkApkUpdate.getHasNewVersion()) {
            _binding?.csiVersionUpdate?.addExtraLayout(
                View.inflate(
                    requireContext(),
                    R.layout.home_my_layout_update_tip,
                    null
                )
            )
        }
    }


    private fun initListener() {
        if (_binding == null) return
        _binding?.tvLogin?.setOnClickListener {
            //点击登录
//            val navigation = ARouter.getInstance().navigation(LoginService::class.java)
//            navigation.toLogin()
//                if (WeChatHelper.mApi.isWXAppInstalled()) {
//                    WeChatHelper.weixinSdkLogin();
//                    return;
//                }
            UMUtil.toAuthWeiXin(activity as Activity)
        }

        _binding?.mivDefaultHead?.setOnClickListener {
            //通过eventbus传递消息，不需要判断请求码了
            ResourceTurnManager.turnToLogin()
        }

        _binding?.tvLogout?.setOnClickListener { //点击退出登录
            mPresenter.showLogoutDialog(requireContext(), _binding!!.nestedScrollView)
        }

        _binding?.tvReadOnceDay?.setOnClickListener {//点击进入每日一读
            LogUtil.addClickLogNew(
                LogEventConstants2.P_MY,
                "3",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.my_daliy_read)
            )

            ARouter.getInstance().build(Paths.PAGE_EVERYDAY_READ).navigation()
        }
        _binding?.tvFollow?.setOnClickListener {//点击进入关注的人
            ARouter.getInstance()
                .build(Paths.PAGE_USERFOLLOW)
                .withInt(IntentParamsConstants.Follow_Fans, 1)
                .withString(IntentParamsConstants.MY_USER_ID, GlobalsUserManager.uid)

                .navigation()
        }
        _binding?.tvFans?.setOnClickListener {//点击进入粉丝
            ARouter.getInstance().build(Paths.PAGE_USERFOLLOW)
                .withInt(IntentParamsConstants.Follow_Fans, 0)
                .withString(IntentParamsConstants.MY_USER_ID, GlobalsUserManager.uid)
                .navigation()
        }
        _binding?.tvContributeTask?.setOnClickListener {//点击进入贡献任务
            ARouter.getInstance().build(Paths.PAGE_CONTRIBUTE_TASK).navigation()
        }

        _binding?.csiSetting?.setOnClickListener { //进入设置页面
            LogUtil.addClickLogNew(
                LogEventConstants2.P_MY,
                "9",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.my_update_log)
            )
            ARouter.getInstance().build(Paths.PAGE_SETTING).navigation()
        }
        _binding?.csQuestion?.setOnClickListener { //进入常见问题页面
            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                return@setOnClickListener
            }
            LogUtil.addClickLogNew(
                LogEventConstants2.P_MY,
                "4",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.my_str_question)
            )
            ARouter.getInstance().build(Paths.PAGE_QUESTION_LIST).navigation()
        }
        _binding?.tvSchoolCircle?.setOnClickListener { //进入学友圈
            LogUtil.addClickLogNew(
                LogEventConstants2.P_MY,
                "2",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.my_friend_circle)
            )
            ARouter.getInstance().build(Paths.PAGE_SCHOOL_CIRCLE).navigation()
        }

        _binding?.tvCheckIn?.setOnClickListener {  //进入签到打卡页面
            LogUtil.addClickLogNew(
                LogEventConstants2.P_MY,
                "1",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.my_str_checkin)
            )
            ARouter.getInstance().build(Paths.PAGE_CHECKIN).navigation()
        }

        _binding?.csiUpdateLog?.setOnClickListener { //进入更新日志页面

            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                return@setOnClickListener
            }

            LogUtil.addClickLogNew(
                LogEventConstants2.P_MY,
                "8",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.my_update_log)
            )

//            ARouter.getInstance().build(Paths.PAGE_WEB)
//                .with(
//                    Bundle().put("params_title", "更新日志")
//                        .put("params_url", UrlConstants.UPDATE_LOG_URL)
//                ).navigation()

            ARouter.getInstance().build(Paths.SERVICE_SETTING_UPDATE_LOG).navigation()
        }
        _binding?.feedBack?.setOnClickListener {//进入意见反馈页面

            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                return@setOnClickListener
            }

//            ARouter.getInstance().build(Paths.PAGE_FEED_LIST).navigation()
            LogUtil.addClickLogNew(
                LogEventConstants2.P_MY,
                "5",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.my_str_feedback)
            )
            //跳转到反馈详情页面 (目前是一个h5页面)
            ARouter.getInstance().build(Paths.PAGE_WEB_FEED_BACK)
                .withString(IntentParamsConstants.WEB_PARAMS_TITLE, "意见反馈")
                .navigation()

            //进入客服
//            RlyUtils.postSdkPermission(activity as Activity)
        }


        //点击分享
        _binding?.csiShare?.setOnClickListener {
            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                return@setOnClickListener
            }

            LogUtil.addClickLogNew(
                LogEventConstants2.P_MY,
                "6",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.my_str_share)
            )
            showShareDialog()
        }
        //我的个人中心页面
        _binding?.mivAvatar?.setOnClickListener {
            ARouter.getInstance()
                .build(Paths.PAGE_USER_INFO)
                .withString(MY_USER_ID, GlobalsUserManager.uid)
                .navigation()
        }

        //点击检测版本更新
        _binding?.csiVersionUpdate?.setOnClickListener {
            LogUtil.addClickLogNew(
                LogEventConstants2.P_MY,
                "7",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.my_version_update)
            )

            //apk更新服务
            val apkApkUpdate: ApkUpdateService =
                ARouter.getInstance().navigation(ApkUpdateService::class.java)
            apkApkUpdate.checkApkUpdate(this, false)
        }

        //点击编辑用户信息
        _binding?.tvUserName?.setOnClickListener {
//            LogUtil.addClickLog(LogPageConstants.PID_MY, LogEventConstants.EID_NAV_SETTING, LogEventConstants.BID_FORM, LogPageConstants.PID_MY, LogPageConstants.PID_SETTING)
            ARouter.getInstance().build(Paths.PAGE_USERINFO_EDIT).navigation()
        }
        _binding?.ibEdit?.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_USERINFO_EDIT).navigation()
        }

    }


    override fun onResume() {
        super.onResume()

        //获取最新用户粉丝信息
        if (GlobalsUserManager.isLogin()) {

            if (GlobalsUserManager.userInfo != null) {
                updateUserInfo(GlobalsUserManager.userInfo)
                return
            }

            lifecycleScope.launchWhenCreated {
                flow<UserInfo> {
                    emit(HttpService.homeApi.getUserInfo())
                }.catch {

                }.collect {
                    GlobalsUserManager.userInfo = it
                    updateUserInfo(GlobalsUserManager.userInfo)
                }
            }
        }
    }


    private fun showShareDialog() {
        val commonBottomSharePop = CommonBottomSharePop(requireContext())
        commonBottomSharePop.onItemClick = { shareAppMessage(it) }
        XPopup.Builder(requireContext())
            .asCustom(commonBottomSharePop)
            .show()

    }

    private fun shareAppMessage(it: Int) {
        // 调用两个接口 分别是获取app的分享信息和个人相关信息
        mPresenter.shareAppMessage(requireActivity(), it)
    }


    /**
     * 更新用户信息
     */
    fun updateUserInfo(userInfo: UserInfo?) {
        if (userInfo == null) {
            _binding?.groupLogin?.visibility = View.GONE
            _binding?.groupLogin2?.visibility = View.INVISIBLE
            _binding?.tvLogout?.visibility = View.GONE
            _binding?.mivAvatar?.visibility = View.GONE
//          _binding?.  mivViewAvatar.visibility = View.GONE
            _binding?.tvLogin?.visibility = View.VISIBLE
            _binding?.mivDefaultHead?.visibility = View.VISIBLE
            return
        }

        userInfo.apply {
            _binding?.tvLogin?.visibility = View.GONE
            _binding?.mivDefaultHead?.visibility = View.GONE
            _binding?.groupLogin?.visibility = View.VISIBLE
            _binding?.groupLogin2?.visibility = View.VISIBLE
            _binding?.tvLogout?.visibility = View.VISIBLE
            _binding?.mivAvatar?.visibility = View.VISIBLE
//          _binding?.  mivViewAvatar.visibility = View.VISIBLE
            _binding?.tvUserName?.visibility = View.VISIBLE
            _binding?.mivAvatar?.setHeadImage(this.avatar, this.avatar_identity)
            _binding?.tvUserName?.text = this.name
            _binding?.tvID?.text = "ID ${this.id}"
            _binding?.tvBindCode?.text = "绑定码:\n${this.uuid}"
            setFansAndFollow(this)
        }


    }

    /**
     * 设置粉丝和关注数量
     */
    private fun setFansAndFollow(userInfo: UserInfo) {
        userInfo.apply {
            val fanStr = "粉丝\r\n${StringFormatUtil.formatFansNum(this.user_be_followed_count)}"
            val followStr = "关注\r\n${StringFormatUtil.formatFansNum(this.user_follow_count)}"
            val fanSsb = SpannableStringBuilder(fanStr)
            val followSsb = SpannableStringBuilder(followStr)
            fanSsb.setSpan(
                RelativeSizeSpan(1.4f),
                fanStr.length - StringFormatUtil.formatFansNum(user_be_followed_count).length,
                fanStr.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            fanSsb.setSpan(
                StyleSpan(Typeface.BOLD),
                fanStr.length - StringFormatUtil.formatFansNum(user_be_followed_count).length,
                fanStr.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            fanSsb.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.color_2)),
                2,
                fanStr.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            fanSsb.setSpan(
                AbsoluteSizeSpan(15.sp2px()),
                3,
                fanStr.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            followSsb.setSpan(
                RelativeSizeSpan(1.4f),
                followSsb.length - StringFormatUtil.formatFansNum(user_follow_count).length,
                followStr.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            followSsb.setSpan(
                StyleSpan(Typeface.BOLD),
                followSsb.length - StringFormatUtil.formatFansNum(user_follow_count).length,
                followStr.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            followSsb.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.color_2)),
                2,
                followSsb.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            followSsb.setSpan(
                AbsoluteSizeSpan(15.sp2px()),
                3,
                followSsb.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            _binding?.tvFans?.setText(fanSsb)
            _binding?.tvFollow?.setText(followSsb)
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(userInfo: UserLoginStateEvent) {
//        loge("${this::class.java.simpleName}收到了登录事件")
        updateUserInfo(userInfo.userInfo)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(userInfo: UserOnClickMyEvent) {
//        loge("${this::class.java.simpleName}收到了点击我的事件")
        //获取最新用户粉丝信息
        if (GlobalsUserManager.isLogin()) {
            updateUserInfo(GlobalsUserManager.userInfo)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.release()
        EventBus.getDefault().unregister(this)

    }
}