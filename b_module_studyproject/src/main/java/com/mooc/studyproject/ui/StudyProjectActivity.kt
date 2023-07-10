package com.mooc.studyproject.ui

//import kotlinx.android.synthetic.main.studyproject_activity_study_project.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.visiable
import com.mooc.common.utils.DateUtil
import com.mooc.common.utils.NetUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.studyproject.DynamicUser
import com.mooc.commonbusiness.model.studyproject.StudyPlan
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.pop.studyproject.MedalPop
import com.mooc.commonbusiness.pop.studyproject.ShowStudyProjectScorePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.AudioPlayService
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.utils.AppBarStateChangeListener.State.COLLAPSED
import com.mooc.resource.utils.AppBarStateChangeListener.State.EXPANDED
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.NoticeAdapter
import com.mooc.studyproject.adapter.TabAdapter
import com.mooc.studyproject.databinding.StudyprojectActivityStudyProjectBinding
import com.mooc.studyproject.fragment.DaShiIntroRuleFragment
import com.mooc.studyproject.fragment.IntelligentLearnListFragment
import com.mooc.studyproject.fragment.StudyDynamicFragment
import com.mooc.studyproject.model.DynamicsBean
import com.mooc.studyproject.model.Notice
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.studyproject.presenter.SomeUtilsPresenter
import com.mooc.studyproject.presenter.StudyProjectAddPresenter
import com.mooc.studyproject.presenter.StudyProjectSharePresenter
import com.mooc.studyproject.viewmodel.StudyProjectViewModel
import com.mooc.studyproject.window.BetSuccessDialog
import com.mooc.studyproject.window.ClockRemindPop
import com.mooc.studyproject.window.DynamicPublishSucPop
import jp.wasabeef.glide.transformations.BlurTransformation
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 *学习项目页面
 * @author limeng
 * @date 2020/11/26
 */
@Route(path = Paths.PAGE_STUDYPROJECT)
class StudyProjectActivity : BaseActivity() {
    var planId: String? = null
    var mStudyPlanData: StudyPlan? = null
    var mStudyPlanDetailBean: StudyPlanDetailBean? = null
    var oldPosition = 0
    var isDaShiIntroUi = false
    var isFirstLoadPage = true//是否是首页加载当前页面  fragment不重复加载
    var setDaShi = false
    var fragments: ArrayList<Fragment?>? = null
    var dashiFragments: ArrayList<Fragment?>? = null
    var notices = ArrayList<Notice>()
    var noticeAdapter: NoticeAdapter? = null
    var daShiIntroFragment: DaShiIntroRuleFragment? = null
    var daShiRuleFragment: DaShiIntroRuleFragment? = null
    var setFirstTab = false
    var isDynamic = false
    var dynamicPublishSucPop: DynamicPublishSucPop? = null
    private var mAppBarStatus = false//判断现在是折叠还是展开，true 折叠，false 展开

    //分享逻辑处理类
    val mPresenter: StudyProjectSharePresenter by lazy { StudyProjectSharePresenter() }
    val mAddPresenter: StudyProjectAddPresenter by lazy { StudyProjectAddPresenter() }
    val utilsPresenter: SomeUtilsPresenter by lazy { SomeUtilsPresenter() }


    companion object {
        const val REQUEST_CODE_DYNAMIC = 100
        const val REQUEST_CODE_BET = 102
        const val REQUEST_CODE_JOIN_PLAN = 103
        const val REQUEST_CODE_APPLY_VER = 104
    }


    //    val model: StudyProjectViewModel by lazy {
//        ViewModelProviders.of(this).get(StudyProjectViewModel::class.java);
//    }
    val model: StudyProjectViewModel by viewModels()

    var daShiOnlyShow = false


    var sourceFragment: IntelligentLearnListFragment? = null
    var dynamicFragment: StudyDynamicFragment? = null
    var myDynamicFragment: StudyDynamicFragment? = null
    var progressDialog: CustomProgressDialog? = null
    var isToOnlyUi = false

    private lateinit var inflater: StudyprojectActivityStudyProjectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyprojectActivityStudyProjectBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initData()
        initListener()
        initDataListener()
    }

    fun initView() {
        progressDialog = CustomProgressDialog.createLoadingDialog(this, true)

        planId = intent.getStringExtra(IntentParamsConstants.STUDYPROJECT_PARAMS_ID)
        noticeAdapter = NoticeAdapter(null)
        isDaShiIntroUi = intent.getBooleanExtra(IntentParamsConstants.PLAN_INTRO_UI, false)
        inflater.mRcyNotcie.layoutManager = LinearLayoutManager(this)
        inflater.mRcyNotcie.adapter = noticeAdapter
//        mTvMore.gone(true)
    }

    fun initData() {
        mAddPresenter.context = this
        utilsPresenter.context = this
        mAddPresenter.planId = planId
        mStudyPlanDetailBean = StudyPlanDetailBean()
        mStudyPlanData = StudyPlan()
        setActionBar()
        getData()
        inflater.commonTitleLayout.ib_right?.visiable(false)
    }

    private fun initDataListener() {
        mAddPresenter.initDataListener()
        // 订阅打卡的返回
        model.orderMessageResult.observe(this, Observer {
            if (it.isSuccess) {
                if (mStudyPlanData?.user_subscribe_checkin_remind != null) {
                    mStudyPlanData?.user_subscribe_checkin_remind =
                        !(mStudyPlanData?.user_subscribe_checkin_remind ?: false)
                    setClockReminderUI()

                }
            }


        })
        //处理报名结果
        model.studyPlanAddBean.observe(this, Observer {
            inflater.mViewStatus.isEnabled = true
            if (it != null) {
                if (it.code == 5) {
                    //报名积分展示
                    if ((it.integrity_score.isNullOrEmpty() || it.integrity_score.equals("0")) &&
                        (it.join_score.isNullOrEmpty() || it.join_score.equals("0"))
                    ) {//禁成长

                    } else {
                        val betSuccessDialog = BetSuccessDialog(this, R.style.DefaultDialogStyle)
                        betSuccessDialog.chengxinScore = it.integrity_score
                        betSuccessDialog.invitationScore = null
                        betSuccessDialog.getScore = it.join_score
                        betSuccessDialog.strButton = getString(R.string.text_ok)
                        betSuccessDialog.show()
                    }
                    if (mStudyPlanData?.plan_mode_status == 2) {  //大师课模式
                        toast("报名成功")
                        isFirstLoadPage = false
                        getData()
                        return@Observer
                    }
                    if (it.join_status == 1) {
                        toast(resources.getString(R.string.study_plan_join))
                        //  如果是智能导学学习项目则跳转过去
                        if ("1" == mStudyPlanDetailBean?.study_plan?.is_bind_testpaper && mStudyPlanDetailBean?.study_plan?.finished_intelligent_test == false) {// 绑定智能测试，并且未完成测试
                            //   跳转到智能导学
                            mStudyPlanDetailBean?.study_plan?.bind_info?.let {
                                ResourceTurnManager.turnToResourcePage(
                                    it
                                )
                            }
                            finish()
                        }

                        if (mStudyPlanDetailBean != null) {
                            mStudyPlanDetailBean?.is_join = 1
                            if (mStudyPlanData != null) {
                                mStudyPlanData?.plan_num = (mStudyPlanData?.plan_num ?: 0) + 1
                                inflater.mTvCount.text = java.lang.String.format(
                                    resources.getString(R.string.study_plan_count),
                                    mAddPresenter.getPlanNum()
                                )
                            }

                            showUIWithStauts()
                            setPlanJoin(mStudyPlanDetailBean, isDynamic)
                            isFirstLoadPage = true
                            sourceFragment?.updateData(mStudyPlanDetailBean)
                            dynamicFragment?.updateData(mStudyPlanDetailBean)
                            myDynamicFragment?.updateData(mStudyPlanDetailBean)
                        }
                    } else {
                        if (!it.message.isNullOrEmpty()) {
                            toast(it.message ?: "")
                        }
                    }
                } else {
                    if (!it.message.isNullOrEmpty()) {
                        toast(it.message ?: "")
                    }
                }
            } else {
                toast("请稍后再试")
            }

        })

        //处理计划详情数据
        model.studyPlanDetailBean.observe(this, Observer {
            if (progressDialog?.isShowing == true) {
                progressDialog?.dismiss()
            }
            inflater.mSwipeLayout.isRefreshing = false
            mStudyPlanDetailBean = it
            mAddPresenter.mStudyPlanDetailBean = mStudyPlanDetailBean
            mStudyPlanData = it.study_plan
            if (mStudyPlanDetailBean?.is_join == 1
                && "1".equals(mStudyPlanDetailBean?.study_plan?.is_bind_testpaper)
                && mStudyPlanDetailBean?.study_plan?.finished_intelligent_test == false
            ) {//已加入学习项目并且是智能导学且 没有完成测试  --》去测试
                if (mStudyPlanDetailBean?.study_plan?.bind_info != null) {
                    ResourceTurnManager.turnToResourcePage(mStudyPlanDetailBean?.study_plan?.bind_info!!)
                    finish()
                }
            }
            setshareVisable()
            updateUIData()
            showUIWithStauts()
            setShowInvitation()

            if (isFirstLoadPage) {//首次bu刷新

            } else {
                if (setFirstTab) {//判断是否手切为清单页面 true 切换
                    inflater.mViewPager.setCurrentItem(0)
                    setFirstTab = false
                }
                if (sourceFragment != null) {
                    sourceFragment?.updateData(mStudyPlanDetailBean)
                }
                if (dynamicFragment != null) {
                    dynamicFragment?.updateData(mStudyPlanDetailBean)
                }
                if (myDynamicFragment != null) {
                    myDynamicFragment?.updateData(mStudyPlanDetailBean)
                }
            }

            setTabLayoutOldPostion(oldPosition)


        })
        //处理大师课 笔记列表
        model.noticeBean.observe(this, Observer {
            if (it != null) {
                if (it.results != null && it.results.isNotEmpty()) {
                    inflater.mRcyNotcie.visibility = View.VISIBLE
                    val notice = Notice()
                    notice.type = 1
                    notices.clear()
                    notices.add(notice)
                    if (it.results.size > 4) {
                        notices.addAll(it.results.subList(0, 4))
                    } else {
                        notices.addAll(it.results)
                    }
                    noticeAdapter?.planId = planId
                    noticeAdapter?.setNewInstance(notices)

                } else {
                    inflater.mRcyNotcie.visibility = View.GONE
                }
            } else {
                inflater.mRcyNotcie.visibility = View.GONE
            }
        })
    }

    /**
     * 报名学习计划
     * 店家加入学习项目
     */
    private fun postAddStudyPlan() {
        val requestData = JSONObject()
        requestData.put("study_plan", planId)
        requestData.put("user", GlobalsUserManager.uid)
        requestData.put("start_score", "0")
        requestData.put("end_score", "0")
        requestData.put("receiver_user_id", "0")
        val stringBody =
            requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        model.postAddStudyPlan(stringBody)

    }

    /**
     * 设置是否显示邀请报名奖积分说明按钮
     * 2021 8 19  慧颖说没有了
     */
    fun setShowInvitation() {
        inflater.mTvInvitation.setVisibility(View.GONE)
    }

    /**
     * 是否大师课模式  2:是
     * 是大师课时是否参加  0：未参加  其它：参加
     */
    fun showUIWithStauts() {
        if (isDaShiIntroUi) {//是大师课UI
            if (isFirstLoadPage) {//是不是首页数据
                setupViewPagerOnlyShow(inflater.dashiViewPager)
                setupDaShiViewPager()
            }
            dashiShow()
            inflater.toDaShiIntro.setVisibility(View.GONE)
            inflater.daShiApplyRl.setVisibility(View.GONE)
            return
        }

        if (mStudyPlanData?.plan_mode_status == 2) {  //大师课模式
            getNoticeList()
            setupViewPagerOnlyShow(inflater.dashiViewPager)
            setupDaShiViewPager()
            if (isFirstLoadPage) {
                setupViewPager(inflater.mViewPager, true)
                setupViewPager()
            }
            if (mStudyPlanDetailBean?.is_join != 0) {  //参加了学习计划
                daShiOnlyShow()
                inflater.toDaShiIntro.setVisibility(View.VISIBLE)
            } else {                  //未参加了学习计划
                dashiShow()
                setDaShiPlanJoin()
                inflater.toDaShiIntro.setVisibility(View.GONE)
            }
        } else {    //正常模式
            inflater.mRlP.setVisibility(View.VISIBLE)
            inflater.daShiApplyRl.setVisibility(View.GONE)
            inflater.mRcyNotcie.setVisibility(View.GONE)
            if (isFirstLoadPage) {
                setupViewPager(inflater.mViewPager, false)
                setupViewPager()
            }
            inflater.mViewPager.setVisibility(View.VISIBLE)
            inflater.tabLayout.setVisibility(View.VISIBLE)
            inflater.dashiTabLayout.setVisibility(View.GONE)
            inflater.dashiViewPager.setVisibility(View.GONE)
            inflater.toDaShiIntro.setVisibility(View.GONE)
        }


    }


    // /获取笔记列表
    fun getNoticeList() {
        planId?.let { model.getStudyPlanNoticeList(it) }
    }

    fun setupViewPager(viewPager: ViewPager, isDaShiMode: Boolean) {
        if (fragments != null && (fragments?.size ?: 0) > 0) {

        } else {
            val titleList = ArrayList<String>()
            fragments = ArrayList()
            titleList.add("学习清单")
            titleList.add("动态")
            titleList.add("我的动态")
            //TODO  改了不用这了fragment 了
            sourceFragment = IntelligentLearnListFragment.newInstance(mStudyPlanDetailBean)
            dynamicFragment =
                StudyDynamicFragment.newInstance(mStudyPlanDetailBean, StudyDynamicFragment.dynamic)
            myDynamicFragment = StudyDynamicFragment.newInstance(
                mStudyPlanDetailBean,
                StudyDynamicFragment.dynamicMy
            )
            sourceFragment?.isDaShiMode = isDaShiMode
//            IntelligentLearnListFragment?.isDaShiMode = isDaShiMode

            fragments?.add(sourceFragment)
            fragments?.add(dynamicFragment)
            fragments?.add(myDynamicFragment)
            val tabAdapter = TabAdapter(supportFragmentManager, titleList, fragments)
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = 3
        }
    }

    /**
     * 更新页面数据
     */
    fun updateUIData() {
        if (!mStudyPlanData?.plan_rule.isNullOrEmpty()) {
            inflater.mTvMore.visiable(true)
        } else {
            inflater.mTvMore.visiable(false)
        }

        var imgUrl: String? = null
        if (TextUtils.isEmpty(mStudyPlanData?.head_img)) {
            imgUrl = mStudyPlanData?.plan_img
        } else {
            imgUrl = mStudyPlanData?.head_img
        }
        Glide.with(this)
            .load(imgUrl) //第二个参数是圆角半径，第三个是模糊程度，2-5之间个人感觉比较好。
            .transforms(CenterCrop(), BlurTransformation(20, 3))
            .into(inflater.mIvPlan)
        Glide.with(this)
            .load(imgUrl)
            .transform(GlideTransform.centerCropAndRounder2)
            .into(inflater.headerImg)
        val planName: String? = mStudyPlanData?.plan_name
        inflater.mTvTitleHead.setText(planName)
        inflater.mTvSubTitle.setText(mStudyPlanData?.plan_subtitle)
        // 设置订阅打开提醒的状态
        setClockReminderUI()
        //设置申请证书按钮状态
        setApplyCerticaficateState()
        setPlanJoin(mStudyPlanDetailBean, isDynamic)
        val dynamicUsers: ArrayList<DynamicUser>? = mStudyPlanData?.plan_start_users
        val pileUser: MutableList<String?> = ArrayList()
        if (dynamicUsers != null && dynamicUsers.size > 0) {
            inflater.tvUser.setVisibility(View.VISIBLE)
            for (i in dynamicUsers.indices) {
                val dynamicUser: DynamicUser = dynamicUsers[i]
                pileUser.add(dynamicUser.avatar)
            }
            //                pvIcon.setAvertImages(pileUser);
            val dynamicUser: DynamicUser = dynamicUsers[0]
            if (!isDestroyed && !isFinishing) {


                Glide.with(this)
                    .load(dynamicUser.avatar)
                    .error(R.mipmap.common_ic_user_head_default)
                    .placeholder(R.mipmap.common_ic_user_head_default)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(20.dp2px())))
                    .into(inflater.pvIcon)
            }
            val userName: String? = dynamicUser.name
            val userString: String?
            userString = if (dynamicUsers.size > 1 && userName != null) {
                java.lang.String.format(
                    getString(R.string.learn_user_name),
                    StringFormatUtil.getStrUserName(userName),
                    dynamicUsers.size
                )
            } else {
                userName
            }
            inflater.tvUser.setText(userString)
        } else {
            inflater.tvUser.setVisibility(View.GONE)
        }
        if (!mStudyPlanData?.plan_rule.isNullOrEmpty()) {
            inflater.mTvRule.setText(utilsPresenter.getRuleContent(mStudyPlanData?.plan_rule))
        } else {
            inflater.rlRule.visiable(false)
        }
        val planTime = java.lang.String.format(
            getString(R.string.text_time_to_time),
            DateUtil.timeToString((mStudyPlanData?.plan_starttime ?: 0) * 1000, "yyyy.MM.dd"),
            DateUtil.timeToString((mStudyPlanData?.plan_endtime ?: 0) * 1000, "yyyy.MM.dd")
        )
        val enrollTime = java.lang.String.format(
            getString(R.string.text_time_to_time),
            DateUtil.timeToString((mStudyPlanData?.join_start_time ?: 0) * 1000, "yyyy.MM.dd"),
            DateUtil.timeToString((mStudyPlanData?.join_end_time ?: 0) * 1000, "yyyy.MM.dd")
        )
        inflater.mTvPlanTime.text = planTime
        inflater.mTvEnrollTime.text = enrollTime
        if (mStudyPlanData?.time_mode == 1) {//时间永久
            inflater.llLearnTimeCount.visibility = View.GONE
        } else {
            inflater.llLearnTimeCount.visibility = View.VISIBLE
        }

        val planNum: String? = mAddPresenter.getPlanNum()
        inflater.mTvCount.text = java.lang.String.format(
            resources.getString(R.string.study_plan_count),
            planNum
        )

        setTvEnrollAndLearnShow()
        //设置不同类型弹框
        if (mStudyPlanData?.is_success == 1) {
            if (mStudyPlanData?.is_read == 0) {//未读
                if (mStudyPlanData?.plan_mode_status == 0 || mStudyPlanData?.is_calculate == 1) {//弹出来获得积分弹框
                    val centerPopupView = ShowStudyProjectScorePop(this, mStudyPlanData)
                    XPopup.Builder(this)
                        .setPopupCallback(object : SimpleCallback() {
                            override fun onDismiss(popupView: BasePopupView?) {
                                showMediaPop(this@StudyProjectActivity, mStudyPlanData)
                            }
                        })
                        .asCustom(centerPopupView)
                        .show()
                } else {
                    showMediaPop(this, mStudyPlanData)
                }

            } else {
                showMediaPop(this, mStudyPlanData)
            }
        }
    }

    private fun setApplyCerticaficateState() {
        // 设置申请证书按钮状态
        inflater.mTvApplyCertificate.setText(resources.getString(R.string.apply_certificate))
        if (mStudyPlanData?.verified_active == -1) {//-1 不可申请证书  0 可申请证书 1 已申请证书 -2 没有证书 不显示
            inflater.mTvApplyCertificate.visiable(true)
            inflater.mTvApplyCertificate.setBackgroundResource(R.drawable.shape_corner3_colorf1)
            inflater.mTvApplyCertificate.isEnabled = false
            inflater.mTvApplyCertificate.background?.alpha = 125
        } else if (mStudyPlanData?.verified_active == 0) {
            inflater.mTvApplyCertificate.visiable(true)
            inflater.mTvApplyCertificate.isEnabled = true
            inflater.mTvApplyCertificate.setBackgroundResource(R.drawable.shape_corner3_colordprimary)
            inflater.mTvApplyCertificate.background?.alpha = 255

        } else if (mStudyPlanData?.verified_active == 1) {
            inflater.mTvApplyCertificate.visiable(true)
            inflater.mTvApplyCertificate.background?.alpha = 255
            inflater.mTvApplyCertificate.setPadding(0, 0, 0, 0)
            inflater.mTvApplyCertificate.setText(resources.getString(R.string.apply_certificate_finish))
            inflater.mTvApplyCertificate.setTextColor(getColorRes(R.color.color_F_80))
            inflater.mTvApplyCertificate.isEnabled = false
            inflater.mTvApplyCertificate.background = null
        } else if (mStudyPlanData?.verified_active == -2) {
            inflater.mTvApplyCertificate.visiable(false)
        }
    }

    /***
     * 设置消息订阅状态
     */

    private fun setClockReminderUI() {
        if (mStudyPlanData?.is_checkin_remind == true) {
            inflater.commonTitleLayout.tv_right?.visiable(true)
        } else {
            inflater.commonTitleLayout.tv_right?.visiable(false)

        }


        if (mStudyPlanData?.user_subscribe_checkin_remind == true) {
            inflater.commonTitleLayout.tv_right?.setDrawLeft(
                R.mipmap.studyproject_clock_already,
                10
            )
            inflater.commonTitleLayout.tv_right?.setText(R.string.clock_reminder_already)
            inflater.commonTitleLayout.tv_right?.setTextColor(this.getColorRes(R.color.white))

        } else {
            inflater.commonTitleLayout.tv_right?.setDrawLeft(
                R.mipmap.studyprojrct_clock_reminder_green,
                10
            )
            inflater.commonTitleLayout.tv_right?.setText(R.string.clock_reminder)
            inflater.commonTitleLayout.tv_right?.setTextColor(this.getColorRes(R.color.color_green_msg_tip))
        }

    }

    /**
     * 弹出获得计划勋章动画
     */
    fun showMediaPop(context: Context, studyplan: StudyPlan?) {
        if (studyplan?.is_read_medal == 0) {//弹出获得计划勋章动画
            val medalpop = MedalPop(context, studyplan.medal_default_link)
            XPopup.Builder(context)
                .asCustom(medalpop)
                .show()
        }
    }


    fun initListener() {
//        mTvApplyCertificate.setOnClickListener {
//            ARouter.getInstance().build(Paths.PAGE_APPLYCER_INPUT)
//                .withString("id", mStudyPlanData?.certificate_id)
//                .withString("title", mStudyPlanData?.plan_name).navigation()
//        }
        // 打开打卡提醒或者取消
        inflater.commonTitleLayout.tv_right?.setOnClickListener {

            if (inflater.mTvPlanStatus.text.toString() == resources.getString(R.string.study_plan_finish)) {
                toast("学习项目已结束")
                return@setOnClickListener
            }
            if (mStudyPlanDetailBean?.is_join != 1) {
                toast("请先加入学习项目")
                return@setOnClickListener
            }

            val clockRemindPop = mStudyPlanData?.user_subscribe_checkin_remind?.let { it1 ->
                ClockRemindPop(
                    this,
                    it1
                )
            }
            clockRemindPop?.onSureClick = {
                //修改打卡提醒的参数
                val isActive = mStudyPlanData?.user_subscribe_checkin_remind != true

                //订阅或者取消订阅提醒
                val map = hashMapOf(
                    "studyplan_id" to planId,
                    "is_active" to isActive
                )
                val toJson = Gson().toJson(map)
                val stringBody =
                    toJson.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
                model.orderMessage(stringBody)
                clockRemindPop?.dismiss()
            }
            XPopup.Builder(this)
                .asCustom(clockRemindPop)
                .show()
        }
        //申请证书按钮
        inflater.mTvApplyCertificate.setOnClickListener {
            // 申请证书
            ARouter.getInstance()
                .build(Paths.PAGE_APPLYCER_INPUT)
                .withString(
                    IntentParamsConstants.INTENT_CERTIFICATE_ID,
                    mStudyPlanData?.certificate_id
                )
                .withString(IntentParamsConstants.PARAMS_RESOURCE_TITLE, mStudyPlanData?.plan_name)
                .navigation(this, REQUEST_CODE_APPLY_VER)
        }
        inflater.mSwipeLayout.setOnRefreshListener {
            if (NetUtils.isNetworkConnected()) {
                isFirstLoadPage = false
                getData()
            } else {
                isFirstLoadPage = true
                inflater.mSwipeLayout.isRefreshing = false
                toast(resources.getString(R.string.net_error))
            }
        }
        inflater.commonTitleLayout.setOnLeftClickListener { onBackPressed() }
        inflater.rlRule.setOnClickListener {
            //参与规则中的更多
            if (mStudyPlanData != null) {
                ARouter.getInstance().build(Paths.PAGE_WEB_STUDY)
                    .withString("resource_txt", mStudyPlanData?.plan_rule)
                    .withString("resource_type", "1")
                    .navigation()

            }
        }




        inflater.commonTitleLayout.ib_right?.setOnClickListener {
            if (mStudyPlanData != null) {
                //修改分享功能 1 调用接口
//                val shareDialog = ShareDialog.newInstance(Bundle().put(ShareDialog.PARAMS_SHOW_SCHOOL, false))
//                shareDialog.onItemClick
//                shareDialog.show(supportFragmentManager, "studyproject")
                val commonBottomSharePop = CommonBottomSharePop(this)
                commonBottomSharePop.onItemClick = {
                    // 调用两个接口 分别是获取app的分享信息和个人相关信息
                    mPresenter.shareAppMessage(this, mStudyPlanDetailBean, it, model, planId)
                }
                XPopup.Builder(this).asCustom(commonBottomSharePop).show()

            }
        }

        // 点击发起人跳转到   发起人简介
        inflater.mViewLook.setOnClickListener {
            if (mStudyPlanData != null) {
                ARouter.getInstance().build(Paths.PAGE_WEB_INITIORBRIEF)
                    .withString(
                        "intent_study_plan_rule",
                        mStudyPlanData?.plan_start_users_introduction
                    )
                    .withParcelableArrayList(
                        "intent_study_plan_list",
                        mStudyPlanData?.plan_start_users
                    )
                    .navigation()
            }
        }


        //  仅浏览
        inflater.mTvOnlyShow.setOnClickListener {
            if (mStudyPlanDetailBean?.is_join == 0) {  //未报名
                daShiOnlyShow()
            }
        }


        //   大师课模式   报名参加兑换积分 coupon_used_status是否兑换过:0  没有用  1用过
        // TODO  防止连续点击
        inflater.tvApply.setOnClickListener {
            if (mStudyPlanData != null) {//大师课的报名参加
                if (mStudyPlanDetailBean?.is_restrict == true) {//学习项目报名受限用户
                    mAddPresenter.restrictDialog(true)
                } else {// 不是受限用户 判断是否显示诚信积分
                    if (mStudyPlanData?.is_open_integrity == true) {//弹诚信积分的弹框
                        mAddPresenter.integrityCommitmentDialog()
                    } else {
                        mAddPresenter.joinLearnMethod()
                    }
                }
            }
        }

        //  要显示大师课的学习计划
        inflater.toDaShiIntro.setOnClickListener {
            ARouter.getInstance()
                .build(Paths.PAGE_STUDYPROJECT)
                .withString(IntentParamsConstants.STUDYPROJECT_PARAMS_ID, planId)
                .withBoolean(IntentParamsConstants.PLAN_INTRO_UI, true)
                .navigation()

        }


        inflater.appBarLayout.addOnOffsetChangedListener(object :
            com.mooc.resource.utils.AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: Int) {
                when (state) {
                    EXPANDED -> {
                        //展开状态
                        inflater.mViewShadow.visibility = View.VISIBLE
                        mAppBarStatus = false
                        inflater.commonTitleLayout.middle_text = ""
                        inflater.commonTitleLayout.leftIcon = R.mipmap.common_ic_back_white
                        inflater.commonTitleLayout.ib_right?.setImageResource(R.mipmap.common_ic_right_share_white)
                        inflater.mSwipeLayout.isEnabled = true
                        if (mStudyPlanData?.is_checkin_remind == true) {//非滑动状态该显示显示
                            inflater.commonTitleLayout.tv_right?.visiable(true)
                        }

                    }
                    COLLAPSED -> {
                        //折叠状态
                        inflater.mViewShadow.visibility = View.GONE
                        mAppBarStatus = true
                        if (mStudyPlanData != null && !TextUtils.isEmpty(mStudyPlanData?.plan_name)) {
                            inflater.commonTitleLayout.middle_text = mStudyPlanData?.plan_name
                        }
                        inflater.commonTitleLayout.leftIcon = R.mipmap.common_ic_back_black
                        inflater.commonTitleLayout.ib_right?.setImageResource(R.mipmap.common_ic_right_share_gray)
                        inflater.mSwipeLayout.isEnabled = false
                        if (mStudyPlanData?.is_checkin_remind == true) {//滑动状态隐藏
                            inflater.commonTitleLayout.tv_right?.visiable(false)
                        }
                    }
                    else -> {
                        //中间状态
                        inflater.mViewShadow.visibility = View.VISIBLE
                        inflater.commonTitleLayout.middle_text = ""
                        inflater.commonTitleLayout.leftIcon = R.mipmap.common_ic_back_white
                        inflater.commonTitleLayout.ib_right?.setImageResource(R.mipmap.common_ic_right_share_white)
                        inflater.mSwipeLayout.isEnabled = false
                        if (mStudyPlanData?.is_checkin_remind == true) {//滑动状态隐藏
                            inflater.commonTitleLayout.tv_right?.visiable(false)
                        }
                    }
                }
            }
        })
        inflater.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                oldPosition = tab.position
                setTabLayoutOldPostion(tab.position)
                utilsPresenter.updateTabView(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                utilsPresenter.updateTabView(tab, false)

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        inflater.dashiTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                utilsPresenter.updateTabView(tab, true)

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                utilsPresenter.updateTabView(tab, false)

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        //TODO 相对 这个按钮进行防止 多次点击
        inflater.mViewStatus.setOnClickListener {
            //  加入学习项目
            if (mStudyPlanDetailBean != null) {
                if (mStudyPlanDetailBean?.is_join == 1) {
                    //已经加入项目 显示发布动态
                    if (isDynamic) {
                        mStudyPlanData?.num_activity_limit?.let { it1 ->
                            planId?.let { it2 ->
                                ARouter.getInstance().build(Paths.PAGE_PUBLISHINGDYNAMICSCOMMEN)
                                    .withBoolean(
                                        PublishingDynamicsCommentActivity.IS_PUBLISH_COMMENT,
                                        false
                                    )
                                    .withString(
                                        PublishingDynamicsCommentActivity.STUDY_PLAN_ID,
                                        it2
                                    )
                                    .withInt(
                                        PublishingDynamicsCommentActivity.INTENT_STUDY_DYNAMIC_WORD_LIMIT,
                                        it1
                                    )
                                    .navigation(this, REQUEST_CODE_DYNAMIC)
                            }
                        }
                    }
                } else {
                    if (mStudyPlanDetailBean?.is_restrict == true) {//学习项目报名限制
                        mAddPresenter.restrictDialog(false)
                    } else {
                        if (mStudyPlanData?.is_open_integrity == true) {
                            mAddPresenter.integrityCommitmentDialog()
                        } else {
                            if (!mAddPresenter.isToBet()) { //不需要进入预存页面
                                postAddStudyPlan()
                            } else {
                                // 跳转到预存页面
                                mAddPresenter.toBetPoint()
                            }
                        }

                    }
                }
            }
        }
    }


    /**
     * 获取计划详情数据
     */
    fun getData() {

        planId?.let {
            if (progressDialog?.isShowing == false) {
                progressDialog?.show()
            }
            model.getData(it)
        }
    }

    fun setActionBar() {
        setSupportActionBar(inflater.mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayUseLogoEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    /**
     * 设置分享按钮是否显示
     */
    fun setshareVisable() {
        if (mStudyPlanData != null) {
            if (mStudyPlanData?.share_status == 1) {
                inflater.commonTitleLayout.ib_right?.visiable(true)
            } else {
                inflater.commonTitleLayout.ib_right?.visiable(false)
            }
        }
    }


    /**
     * 设置计划时间显示与否
     */
    fun setTvEnrollAndLearnShow() {
        if (mStudyPlanData?.is_enroll_daterange == 0) {
            inflater.llEnrollTime.setVisibility(View.GONE)
        } else {
            when (mAddPresenter.isEnrolment(
                DateUtil.getCurrentTime(),
                mStudyPlanData?.join_start_time,
                mStudyPlanData?.join_end_time
            )) {
                0, 1 -> inflater.llEnrollTime.setVisibility(View.VISIBLE)
                2 -> inflater.llEnrollTime.setVisibility(View.GONE)
            }
        }
        if (mStudyPlanData?.is_studyplan_daterange == 0) {
            inflater.llLearnTime.visibility = View.GONE
        } else {
            inflater.llLearnTime.visibility = View.VISIBLE
        }
    }

    private fun setupViewPagerOnlyShow(viewPager: ViewPager) {
        if (setDaShi) {
            return
        }
        val titleList = ArrayList<String>()
        dashiFragments = ArrayList()
        titleList.add("项目介绍")
        titleList.add("参与规则")

        val planTime = java.lang.String.format(
            getString(R.string.text_time_to_time), DateUtil.timeToString(
                (mStudyPlanData?.plan_starttime
                    ?: 0) * 1000,
                "yyyy.MM.dd"
            ), DateUtil.timeToString(
                (mStudyPlanData?.plan_endtime
                    ?: 0) * 1000, "yyyy.MM.dd"
            )
        )
        val enrollTime = java.lang.String.format(
            getString(R.string.text_time_to_time),
            DateUtil.timeToString((mStudyPlanData?.join_start_time ?: 0) * 1000, "yyyy.MM.dd"),
            DateUtil.timeToString((mStudyPlanData?.join_end_time ?: 0) * 1000, "yyyy.MM.dd")
        )
        val sb = StringBuffer()
        if (!TextUtils.isEmpty(mStudyPlanData?.plan_master_speaker)) {
            sb.append(
                utilsPresenter.getHtmlHeader("主讲人介绍") + mStudyPlanData?.plan_master_speaker
            )
        }
        if (!TextUtils.isEmpty(mStudyPlanData?.plan_master_content)) {
            sb.append(
                utilsPresenter.getHtmlHeader("内容介绍") + mStudyPlanData?.plan_master_content
            )
        }
        if (!TextUtils.isEmpty(mStudyPlanData?.plan_master_join_method)) {
            sb.append(
                utilsPresenter.getHtmlHeader("参与方式") + mStudyPlanData?.plan_master_join_method
            )
        }
        val infoDesc: String = if (mStudyPlanData?.time_mode == 1) {//时间永久
            sb.toString()
        } else {
            sb.toString() + utilsPresenter.getHtmlHeader("报名时间") + utilsPresenter.getHtmlContent(
                enrollTime
            ) + utilsPresenter.getHtmlHeader("起止时间") + utilsPresenter.getHtmlContent(planTime)
        }
        val ruleDesc = mStudyPlanData?.plan_rule
        daShiIntroFragment = DaShiIntroRuleFragment.newInstance(infoDesc)
        daShiRuleFragment = DaShiIntroRuleFragment.newInstance(ruleDesc)
        dashiFragments?.add(daShiIntroFragment)
        dashiFragments?.add(daShiRuleFragment)
        val tabAdapter = TabAdapter(supportFragmentManager, titleList, dashiFragments)
        viewPager.adapter = tabAdapter
        setDaShi = true
    }

    private fun setupDaShiViewPager() {
        inflater.dashiTabLayout.setupWithViewPager(inflater.dashiViewPager)
        utilsPresenter.initTabs(inflater.dashiTabLayout)
    }

    private fun dashiShow() {
        isToOnlyUi = false
        inflater.rlRule.setVisibility(View.GONE)
        inflater.mRlP.setVisibility(View.VISIBLE)
        inflater.daShiApplyRl.setVisibility(View.VISIBLE)
        inflater.mRcyNotcie.setVisibility(View.GONE)
        inflater.mViewPager.setVisibility(View.GONE)
        inflater.tabLayout.setVisibility(View.GONE)
        inflater.dashiTabLayout.visibility = View.VISIBLE
        inflater.dashiViewPager.visibility = View.VISIBLE
        inflater.mViewStatus.setVisibility(View.VISIBLE)
    }

    private fun setupViewPager() {
        inflater.tabLayout.setupWithViewPager(inflater.mViewPager)
        utilsPresenter.initTabs(inflater.tabLayout)
    }

    fun daShiOnlyShow() {
        daShiOnlyShow = true
        isToOnlyUi = false
        inflater.rlRule.visibility = View.GONE
        inflater.mRlP.visibility = View.VISIBLE
        inflater.daShiApplyRl.visibility = View.GONE
        inflater.mRcyNotcie.visibility = View.VISIBLE
        inflater.mViewPager.visibility = View.VISIBLE
        inflater.tabLayout.visibility = View.VISIBLE
        inflater.dashiTabLayout.visibility = View.GONE
        inflater.dashiViewPager.visibility = View.GONE
        inflater.mViewStatus.visibility = View.GONE
    }

    private fun setDaShiPlanJoin() {

        // 未加入学习项目
        if (mStudyPlanData != null) {
            if (mStudyPlanData?.time_mode == 1) {//时间永久
                inflater.tvApply.text = "报名参加"
                inflater.tvApply.isEnabled = true
                inflater.tvApply.setTextColor(this.getColorRes(R.color.color_white))
                inflater.tvApply.setBackgroundResource(R.color.colorPrimary)
            } else {
                when (mAddPresenter.isEnrolment(
                    DateUtil.getCurrentTime(), mStudyPlanData?.join_start_time,
                    mStudyPlanData?.join_end_time
                )) {
                    0 -> {
                        inflater.tvApply.text = "未开放报名"
                        inflater.tvApply.isEnabled = false
                        inflater.tvApply.setTextColor(resources.getColor(R.color.color_9))
                        inflater.tvApply.setBackgroundResource(R.color.color_64e0)
                    }
                    //不限制人数
                    1 -> if (mStudyPlanData?.is_set_limit_num == false || (mStudyPlanData?.plan_num
                            ?: 0) < (mStudyPlanData?.limit_num ?: 0)
                    ) {
                        inflater.tvApply.text = "报名参加"
                        inflater.tvApply.isEnabled = true
                        inflater.tvApply.setTextColor(this.getColorRes(R.color.color_white))
                        inflater.tvApply.setBackgroundResource(R.color.colorPrimary)
                    } else {
                        inflater.tvApply.text = getString(R.string.study_plan_limit_num)
                        inflater.tvApply.setTextColor(this.getColorRes(R.color.color_9))
                        inflater.tvApply.isEnabled = false
                        inflater.tvApply.setBackgroundResource(R.color.color_64e0)
                    }
                    2 -> if (utilsPresenter.isUnStartOrStop(
                            DateUtil.getCurrentTime(),
                            mStudyPlanData?.plan_starttime,
                            mStudyPlanData?.plan_endtime
                        ) == 2
                    ) {
                        inflater.tvApply.text = "项目已结束"
                        inflater.tvApply.isEnabled = false
                        inflater.tvApply.setTextColor(this.getColorRes(R.color.color_9))
                        inflater.tvApply.setBackgroundResource(R.color.color_64e0)
                    } else {
                        inflater.tvApply.text = "报名已结束"
                        inflater.tvApply.setTextColor(this.getColorRes(R.color.color_9))
                        inflater.tvApply.isEnabled = false
                        inflater.tvApply.setBackgroundResource(R.color.color_64e0)
                    }
                }
            }

        }
    }


    fun setTabLayoutOldPostion(oldPosition: Int) {
        when (oldPosition) {
            0 -> {
                isDynamic = false
                setPlanJoin(mStudyPlanDetailBean, false)
            }
            1 -> {
                isDynamic = true
                setPlanJoin(mStudyPlanDetailBean, true)
            }
            2 -> {
                isDynamic = true
                setPlanJoin(mStudyPlanDetailBean, true)
            }
        }
    }

    /**
     * 判断底部按钮状态，
     */
    private fun setPlanJoin(studyPlanDetailBean: StudyPlanDetailBean?, isDynamic: Boolean) {
        if (mStudyPlanData?.plan_mode_status == 2 && mStudyPlanDetailBean?.is_join == 0) {  //大师课模式  没参加学习计划
            inflater.mTvPlanStatus.visibility = View.GONE
            return
        }

        if (studyPlanDetailBean?.is_join == 0) {
            // 未加入学习项目
            if (mStudyPlanData != null) {
                inflater.mViewStatus.visibility = View.VISIBLE
                inflater.mTvPlanStatus.visibility = View.VISIBLE
                if (mStudyPlanData?.time_mode == 1) {//时间永久
                    inflater.mTvPlanStatus.text =
                        resources.getString(R.string.study_plan_start)
                    inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.colorPrimary))
                    inflater.mViewStatus.isEnabled = true
                } else {
                    when (mAddPresenter.isEnrolment(
                        DateUtil.getCurrentTime(), mStudyPlanData?.join_start_time,
                        mStudyPlanData?.join_end_time
                    )) {
                        0 -> {
                            inflater.mTvPlanStatus.text =
                                resources.getString(R.string.study_plan_enrolment_un_start)
                            inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.color_9))
                            inflater.mViewStatus.isEnabled = false
                        }
                        1 -> if (mStudyPlanData?.is_set_limit_num == false || (mStudyPlanData?.plan_num
                                ?: 0) < (mStudyPlanData?.limit_num ?: 0)
                        ) {
                            inflater.mTvPlanStatus.text =
                                resources.getString(R.string.study_plan_start)
                            inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.colorPrimary))
                            inflater.mViewStatus.isEnabled = true
                        } else {
                            inflater.mTvPlanStatus.text =
                                resources.getString(R.string.study_plan_limit_num)
                            inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.color_9))
                            inflater.mViewStatus.isEnabled = false
                        }
                        2 -> if (utilsPresenter.isUnStartOrStop(
                                DateUtil.getCurrentTime(), mStudyPlanData?.plan_starttime,
                                mStudyPlanData?.plan_endtime
                            ) == 2
                        ) {
                            inflater.mTvPlanStatus.text =
                                resources.getString(R.string.study_plan_finish)
                            inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.color_9))
                            inflater.mViewStatus.isEnabled = false
                        } else {
                            inflater.mTvPlanStatus.text =
                                resources.getString(R.string.study_plan_enrolment_finish)
                            inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.color_9))
                            inflater.mViewStatus.isEnabled = false
                        }
                    }
                }

            } else {
                inflater.mViewStatus.visibility = View.GONE
            }
        } else {
            when (utilsPresenter.isUnStartOrStop(
                DateUtil.getCurrentTime(), mStudyPlanData?.plan_starttime,
                mStudyPlanData?.plan_endtime
            )) {
                0 -> {
                    inflater.mViewStatus.visibility = View.VISIBLE
                    inflater.mTvPlanStatus.setVisibility(View.VISIBLE)
                    if (isDynamic) {
                        if (mAddPresenter.isShowDynamic()) {
                            inflater.mTvPlanStatus.text =
                                resources.getString(R.string.study_plan_comment)
                            inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.colorPrimary))
                            inflater.mViewStatus.isEnabled = true
                        } else {
                            inflater.mViewStatus.visibility = View.GONE
                        }
                    } else {
                        inflater.mTvPlanStatus.text =
                            resources.getString(R.string.study_plan_un_start)
                        inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.color_9))
                        inflater.mViewStatus.isEnabled = false
                    }
                }
                1 -> if (isDynamic) {//起止时间内
                    if (mAddPresenter.isShowDynamic()) {
                        inflater.mViewStatus.visibility = View.VISIBLE
                        inflater.mTvPlanStatus.visibility = View.VISIBLE
                        inflater.mTvPlanStatus.text =
                            resources.getString(R.string.study_plan_comment)
                        inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.colorPrimary))
                        inflater.mViewStatus.isEnabled = true
                    } else {
                        inflater.mViewStatus.visibility = View.GONE
                    }
                } else {
                    inflater.mViewStatus.visibility = View.GONE
                }
                2 -> {//结束
                    inflater.mViewStatus.visibility = View.VISIBLE
                    inflater.mTvPlanStatus.visibility = View.VISIBLE
                    if (isDynamic) {
                        if (mAddPresenter.isShowDynamic()) {
                            inflater.mTvPlanStatus.text =
                                resources.getString(R.string.study_plan_comment)
                            inflater.mTvPlanStatus.setTextColor(resources.getColor(R.color.colorPrimary))
                            inflater.mViewStatus.isEnabled = true
                        } else {
                            inflater.mViewStatus.visibility = View.GONE
                        }
                    } else {
                        inflater.mTvPlanStatus.text =
                            resources.getString(R.string.study_plan_finish)
                        inflater.mTvPlanStatus.setTextColor(this.getColorRes(R.color.color_9))
                        inflater.mViewStatus.isEnabled = false
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        inflater.mViewStatus.isEnabled = true
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_DYNAMIC) {
                if (data != null) {
                    val bean =
                        data.getParcelableExtra(IntelligentLearnListFragment.INTENT_STUDY_PLAN_DYNAMIC) as DynamicsBean?
                    val studyDynamic = bean?.activity
                    if (studyDynamic != null) {
                        if (dynamicPublishSucPop == null) {
                            dynamicPublishSucPop = DynamicPublishSucPop(this, inflater.mViewStatus)
                        }
                        dynamicPublishSucPop?.show()
                    }
                }
                isFirstLoadPage = false
                setFirstTab = true
                getData()
            } else if (requestCode == REQUEST_CODE_JOIN_PLAN || requestCode == IntelligentLearnListFragment.REFRESH_ALL) {
                if ("1" == mStudyPlanDetailBean?.study_plan?.is_bind_testpaper && mStudyPlanDetailBean?.study_plan?.finished_intelligent_test == false) {// 绑定智能测试，并且未完成测试
                    //   跳转到智能导学
                    mStudyPlanDetailBean?.study_plan?.bind_info?.let {
                        ResourceTurnManager.turnToResourcePage(
                            it
                        )
                    }
                    finish()
                } else {
                    isFirstLoadPage = false
                    setFirstTab = true
                    getData()
                }

            } else if (requestCode == REQUEST_CODE_APPLY_VER) {
                isFirstLoadPage = false
                setFirstTab = true
                getData()
            }
            sourceFragment?.onActivityResult(requestCode, resultCode, null)


        }
    }

    override fun onResume() {
        super.onResume()

        //上传喜马拉雅失败的打点
        try {
            val navigation = ARouter.getInstance().navigation(AudioPlayService::class.java)
            navigation.postErrorPoint()
        } catch (e: Exception) {
        }

    }

    override fun onBackPressed() {
//        if (fromLaunch) {
//            toHome()
//        }
        if (daShiOnlyShow && mStudyPlanDetailBean?.is_join == 0) {
            daShiOnlyShow = false
            dashiShow()
            return
        }
        finish()
    }

}
