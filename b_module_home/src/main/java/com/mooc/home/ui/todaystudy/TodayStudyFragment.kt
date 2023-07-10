package com.mooc.home.ui.todaystudy

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.mooc.changeskin.SkinManager
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.resource.utils.AppBarStateChangeListener
import com.mooc.home.R
import com.mooc.home.model.TodayStudyData
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.eventbus.ScoreChangeNeedRefreshEvent
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.home.databinding.HomeFragmentHomeTodaystudyBinding
//import kotlinx.android.synthetic.main.home_fragment_home_todaystudy.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    今日学习Fragment
 * @Author:         xym
 * @CreateDate:     2020/8/21 3:07 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/21 3:07 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class TodayStudyFragment : Fragment() {

    val todayViewModel : TodayViewModel by viewModels()

    //fragment Adapter
    val todayStudyFragmentAdapter by lazy {
        TodayStudyFragmentAdapter(this)
    }

    val tabArray = arrayOf("最近在学","已订专栏","已订专题","已完成")

    private var _binding : HomeFragmentHomeTodaystudyBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        _binding = HomeFragmentHomeTodaystudyBinding.inflate(layoutInflater,container,false)
        SkinManager.getInstance().injectSkin(binding.root)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView()

        initDataObserver()

        if(!GlobalsUserManager.isLogin()){
            ResourceTurnManager.turnToLogin()
        }
    }

    private fun initView() {
        //设置点击调整目标
        binding.tvChangeTargetFold.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_ADJUST_TARGET).navigation()
        }
        binding.tvChangeTarget.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_ADJUST_TARGET).navigation()
        }

        //积分详情
        binding.viewAllClickRange.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_SCORE_DETAIL).navigation()
        }

        //换肤
        val skinColor = if(SkinManager.getInstance().needChangeSkin()){
            SkinManager.getInstance().resourceManager.getColor("colorPrimary")
        }else{
            requireActivity().getColorRes(com.mooc.commonbusiness.R.color.colorPrimary)
        }

        binding.toolbarLayout.setContentScrimColor(skinColor)
        //监听toolbar折叠
        binding.appBar.addOnOffsetChangedListener(object : com.mooc.resource.utils.AppBarStateChangeListener(){
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: Int) {
               when(state){
                    State.COLLAPSED->{
                        binding.tvChangeTarget.visibility = View.INVISIBLE
                        binding.groupToobal.visibility = View.VISIBLE
                        binding.clFold.setBackgroundColor(skinColor)
                    }
                    else->{
                        binding.tvChangeTarget.visibility = View.VISIBLE
                        binding.groupToobal.visibility = View.INVISIBLE
                        binding.clFold.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.color_transparent))

                    }
                }
                binding.groupToobal.requestLayout()
            }
        })

        //adpater
        binding.todayStudyVp2.adapter = todayStudyFragmentAdapter
//        todayStudyStl.setTabStrs(tabArray)
//        todayStudyStl.setViewPager2(todayStudyVp2)

        binding.mctTodayStudy.setUpWithViewPage2(binding.todayStudyVp2,tabArray.toList() as ArrayList<String>)
        binding.todayStudyVp2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                LogUtil.addClickLogNew(LogEventConstants2.P_TODAY,"${position+1}", LogEventConstants2.ET_TAB, tabArray[position])
            }
        })
    }

    /**
     * 初始化观察者
     */
    private fun initDataObserver() {
        todayViewModel.getTodayStudyData().observe(viewLifecycleOwner, Observer {
            setTodayData(it)
        })


        todayViewModel.getTodayStudyIconData().observe(viewLifecycleOwner, Observer {
            Glide.with(this).load(it).error(R.mipmap.home_icon_todaystudy_wang).into(binding.ivWang)
        })
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
//        loge("todayStudyFragment show : ${!hidden}")

        if(!hidden && !GlobalsUserManager.isLogin()){
            ResourceTurnManager.turnToLogin()
        }
    }

    /**
     * 退出登录重置数据
     */
    fun resetTodayData(){

        binding.tvAllScore.text = "0"
        //资源日学习目标
        val tvSourceDayStr = "资源日学习目标  条，您可通过\"调整目标\"修改"
        binding.tvSourceDay.text = tvSourceDayStr

        //打卡得分
        val signTxt = "打卡  分"
        binding.tvCheckinScore.setText(signTxt)

        //举报得分
        val jubaoTxt = "举报  分"
        binding.tvReportScore.text = jubaoTxt

        //学习得分
        val studyTxt = "学习  分"
        binding.tvStudyScore.text = studyTxt

        //分享得分
        val shareTxt = "分享  分"
        binding.tvShareScore.text = shareTxt

        //头部
        val todayScoreForldText = "今日积分"
        binding.tvTodayScoreFold.text = todayScoreForldText
    }
    /**
     * 设置头部今日数据
     */
    private fun setTodayData(todayStudyData: TodayStudyData?) {
        todayStudyData?.apply {
            binding.tvAllScore.text = this.today_score.toString()
            //资源日学习目标
            val tvSourceDayStr = "资源日学习目标 ${this.resource_num} 条，您可通过\"调整目标\"修改"
//            spannedStr = getSpannedStr(tvSourceDayStr)
            val start = tvSourceDayStr.indexOf(resource_num.toString())
            val spannedBottomStr = getSpannedStr(tvSourceDayStr, start,start + resource_num.toString().length)
            binding.tvSourceDay.text = spannedBottomStr

            //打卡得分
            val signTxt = "打卡  " + this.checkin_score.toString() + "  分"
            var spannedStr = getSpannedStr(signTxt)
            binding.tvCheckinScore.setText(spannedStr)

            //举报得分
            val jubaoTxt = "举报  " + this.report_resource_score.toString() + "  分"
            spannedStr = getSpannedStr(jubaoTxt)
            binding.tvReportScore.text = spannedStr

            //学习得分
            val studyTxt = "学习  " + this.every_day_score.toString() + "  分"
            spannedStr = getSpannedStr(studyTxt)
            binding.tvStudyScore.text = spannedStr

            //分享得分
            val shareTxt = "分享  " + this.share_score.toString() + "  分"
            val ShareStr = getSpannedStr(shareTxt)
            binding.tvShareScore.text = ShareStr

            val todayScoreForldText = "${this.today_score} 今日积分"
            val todayScoreForldStr = getSpannedStr(todayScoreForldText,0,this.today_score.toString().length)
            binding.tvTodayScoreFold.text = todayScoreForldStr
            binding.tvTodayScoreFold.setOnClickListener {
                ARouter.getInstance().build(Paths.PAGE_SCORE_DETAIL).navigation()
            }
        }
    }

    /**
     * 统一处理缩放富文本
     */
    private fun getSpannedStr(str: String,start:Int =3,end:Int = str.length-1): SpannableString {
        val dpSize = 14.dp2px()
        val signStr = SpannableString(str)
        signStr.setSpan(AbsoluteSizeSpan(dpSize), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signStr.setSpan(ForegroundColorSpan(requireContext().getColorRes(R.color.color_white)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return signStr
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(userInfo: UserLoginStateEvent){
//        loge("${this::class.java.simpleName}收到了登录事件")
        if(userInfo.userInfo == null){
            resetTodayData()
        }else{
            todayViewModel.loadData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserScoreEvent(userInfo: ScoreChangeNeedRefreshEvent){
        if(GlobalsUserManager.isLogin()){
            todayViewModel.loadData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)

    }

}