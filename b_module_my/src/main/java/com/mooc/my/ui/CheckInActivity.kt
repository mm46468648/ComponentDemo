package com.mooc.my.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.*
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.pop.studyproject.MedalPop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.my.R
import com.mooc.my.adapter.CheckInPeopleListAdapter
import com.mooc.my.model.CheckInDataBean
import com.mooc.my.databinding.ViewPointShareBinding
import com.mooc.my.pop.CheckInScorePop
import com.mooc.my.pop.ComeonOtherPop
import com.mooc.my.viewmodel.CheckInViewModel
import com.mooc.my.widget.PieView
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.my.databinding.MyActivityCheckinBinding
import com.mooc.resource.ktextention.dp2px
import java.util.*
import kotlin.collections.ArrayList

/**
 * 签到打卡抽积分
 */
@Route(path = Paths.PAGE_CHECKIN)
class CheckInActivity : BaseActivity() {

    companion object {
        const val REQUSET_CODE_REPAIR_CHECKIN = 0 //请求补签
    }

    val mViewModel by viewModels<CheckInViewModel>()
    var timer: Timer? = null

    private var checkInDataBean: CheckInDataBean? = null
    private lateinit var inflate : MyActivityCheckinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate = MyActivityCheckinBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initView()


        mViewModel.mCheckInData.observe(this, Observer {
            inflate.moocSwipeRefresh.isRefreshing = false
            checkInDataBean = it
            bindData(it)
        })

        initData()
    }


    private fun initView() {

        //打卡按钮，默认不可以点击
        inflate.tvStart.isEnabled = false

        inflate.commonTitleLayout.setOnLeftClickListener {
            finish()
        } //点击关闭
        inflate.commonTitleLayout.setOnRightIconClickListener(View.OnClickListener { share() }) //点击分享

        inflate.mivHead.setHeadImage(GlobalsUserManager.userInfo?.avatar,GlobalsUserManager.userInfo?.avatar_identity)
        inflate.tvStart.setOnClickListener {
            inflate.pieView.setListener(object : PieView.RotateListener {
                override fun value(s: String) {
                    setCheckState(true)
//                    toast("恭喜获得${s}积分")
                }
            })

//            LogUtil.addClickLog(LogPageConstants.PID_CLOCK, LogEventConstants.EID_TO_CHECKIN)
            //开始循转动画
            inflate.pieView.startAnimation()
            postCheckIn()
            //不可重复点击
            inflate.tvStart.isEnabled = false
        }

        inflate.moocSwipeRefresh.setOnRefreshListener {
            initData()
        }

    }

    /**
     * 请求签到奖励
     */
    fun postCheckIn() {
        mViewModel.postCheckInNew().observe(this, Observer {
            inflate.pieView.stopAnimation()
            if(it == null){
                setCheckState(false)
                toast("网络异常")
                return@Observer
            }
            if(it.success){
                inflate.pieView.rotate(it.random_score.toString())
                showSuccessDialog(it)

                runOnMainDelayed(1000) {
                    initData()
                }
            }else{
                toast(it.msg)
            }

            setCheckState(it.success)
        })
    }

    /**
     * 设置签到状态
     * @param success 是否成功
     * @param speicalTip 网络异常特殊提示
     */
    fun setCheckState(success:Boolean,speicalTip:String? = null){
        //打卡成功
        if(success){
            inflate.tvStart.text = "已打卡"
            inflate.tvStart.isEnabled = false
            inflate.tvStart.setTextColor(getColorRes(R.color.color_F4ABA6))
        }else{
            inflate.tvStart.text = "点击\r\n打卡"
            inflate.tvStart.isEnabled = true
            inflate.tvStart.setTextColor(getColorRes(R.color.white))

            speicalTip?.let {
                inflate.tvStart.text = speicalTip
            }

        }
    }

    /**
     * 签到成功展示弹窗
     */
    private fun showSuccessDialog(it: CheckInDataBean) {
        val continueDayArray =
            arrayOf("3", "5", "7", "14", "21", "30", "50", "100", "200", "500", "1000")
        val continueDays = it.continue_days
        if (continueDayArray.contains(continueDays)) {
            //如果连续签到天数是数组中的天数，显示勋章弹窗
            showMedalPop(it.checkin_medal_img)
        } else {
            //显示获取积分提示   禁成长
            val checkInScore = it.random_score + it.extra_score + it.special_score
            if (checkInScore > 0) {
                showScorePop(it)
            }
        }
    }

    /**
     * 积分弹窗
     */
    private fun showScorePop(it: CheckInDataBean) {
        XPopup.Builder(this)
            .asCustom(CheckInScorePop(this, it) { showComeonDialog() })
            .show()
    }

    /**
     * 勋章弹窗
     */
    private fun showMedalPop(imgUrl: String) {

        XPopup.Builder(this)
            .asCustom(MedalPop(this, imgUrl) {
                showComeonDialog()
            })
            .show()
    }

    /**
     * 为他人加油弹窗
     */
    private fun showComeonDialog() {
        mViewModel.getSignMembers().observe(this, Observer {
            if (it.data?.size ?: 0 > 0) {
                XPopup.Builder(this)
                    .asCustom(ComeonOtherPop(this, it))
                    .show()
            }
        })

    }

////    /**
////     * 为他人加油弹窗
////     */
//    private fun showComeonDialog() {
//        val comeonOtherResponse = ComeonOtherResponse()
//
//
//        val userList = arrayListOf<UserInfo>()
//        userList.add(GlobalsUserManager.userInfo!!)
//        userList.add(GlobalsUserManager.userInfo!!)
//        userList.add(GlobalsUserManager.userInfo!!)
//        userList.add(GlobalsUserManager.userInfo!!)
//        userList.add(GlobalsUserManager.userInfo!!)
//        userList.add(GlobalsUserManager.userInfo!!)
//        userList.add(GlobalsUserManager.userInfo!!)
//        userList.add(GlobalsUserManager.userInfo!!)
//        userList.add(GlobalsUserManager.userInfo!!)
//        comeonOtherResponse.data = userList
//
////        val musicBean = MusicBean()
////        musicBean.id = "710"
////        musicBean.audio_name = "参与疫情防控阻击战护士的心理防疫要点及对策"
////        musicBean.resource_link =
////            "https://static.moocnd.ykt.io/ucloud/moocnd/cms/audio/mp3/mp3/1583460704S-7M-3.4参与疫情防控阻击战护士的心理防疫要点及对策-静默期/3.4参与疫情防控阻击战护士的心理防疫要点及对策-静默期.mp3"
////        comeonOtherResponse.audio = arrayListOf<MusicBean>(musicBean)
//
//
//        XPopup.Builder(this)
//            .asCustom(ComeonOtherPop(this, comeonOtherResponse))
//            .show()
//
//    }

    /**
     * 初始化获取签到数据
     */
    private fun initData() {

        mViewModel.getCheckInData {
            inflate.moocSwipeRefresh.isRefreshing = false
            setCheckState(false,"请刷新")
        }

    }


    private fun bindData(checkInData: CheckInDataBean) {
        setCheckState(checkInData.has_checkin)

        setTvSpan(checkInData)

        setCheckInList(checkInData)

        //设置签到天,服务端返回的数据倒叙，并格式化
        val singDayStringList = checkInData.days_list.map {
            TimeFormatUtil.formatDate(it * 1000, "MM.dd")
        }
        loge(singDayStringList)
        Collections.reverse(singDayStringList)
        inflate.checkinDaysView.signDayList = singDayStringList as ArrayList<String>
    }


    private val scrollDelay : Long = 2 * 1000
    /**
     *
     * 设置签到人数列表
     */
    private fun setCheckInList(checkInData: CheckInDataBean) {
        inflate.revSign.layoutManager = LinearLayoutManager(this)
        inflate.revSign.adapter = CheckInPeopleListAdapter(checkInData.checkins)

        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        if (checkInData.checkins.count() > 2) {
            timer = Timer()
            timer?.schedule(object : TimerTask() {
                override fun run() {
                    runOnMain {
//                        revSign.smoothScrollToPosition(index)
                        inflate.revSign.smoothScrollBy(0, 31.dp2px())
                    }
                }
            }, scrollDelay, scrollDelay)
        }
    }

    /**
     * 设置一些富文本字段
     */
    @SuppressLint("StringFormatMatches")
    private fun setTvSpan(checkInData: CheckInDataBean) {
        var spannableString: SpannableString
        val currentScore = "当前获得 ${checkInData.score} 积分"
        spannableString = SpannableString(currentScore)
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_9)),
            0,
            5,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(14.dp2px()),
            0,
            5,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_3)),
            5,
            currentScore.length - 3,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(60.dp2px()),
            5,
            currentScore.length - 3,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_9)),
            currentScore.length - 3,
            currentScore.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(14.dp2px()),
            currentScore.length - 3,
            currentScore.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        inflate.tvScore.setText(spannableString)
        val continuityDay = "已连续签到 ${checkInData.continue_days} 天"
        spannableString = SpannableString(continuityDay)
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_6)),
            0,
            6,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(14.dp2px()),
            0,
            6,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_1982FF)),
            6,
            continuityDay.length - 2,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(20.dp2px()),
            6,
            continuityDay.length - 2,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_6)),
            continuityDay.length - 2,
            continuityDay.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(14.dp2px()),
            continuityDay.length - 2,
            continuityDay.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        inflate.tvSignDays.setText(spannableString)


        val signPeopleNum = "今日有 ${checkInData.today_checkin_count} 位小伙伴打卡"
        spannableString = SpannableString(signPeopleNum)
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_3)),
            0,
            4,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(15.dp2px()),
            0,
            4,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_1982FF)),
            4,
            signPeopleNum.length - 7,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(20.dp2px()),
            4,
            signPeopleNum.length - 7,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_3)),
            signPeopleNum.length - 7,
            signPeopleNum.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(15.dp2px()),
            signPeopleNum.length - 7,
            signPeopleNum.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        inflate.tvSignCount.setText(spannableString)


        //补签状态
        when (checkInData.task_status) {
            "0" -> {
                inflate.adhereSignDays.visibility = View.VISIBLE
                inflate.carSignDaysBtn.visibility = View.VISIBLE
                inflate.adhereSignDays.text = "您 ${checkInData.make_up_date} 未打卡"
                inflate.carSignDaysBtn.setOnClickListener {
                    ARouter.getInstance().build(Paths.PAGE_CHECKIN_REPAIRS).withString(
                        CheckInRepairRulesActivity.KEY_MAKE_UPDATE,
                        checkInData.make_up_date
                    )
                        .navigation(this, REQUSET_CODE_REPAIR_CHECKIN)
                }
            }
            "1" -> {
                inflate.adhereSignDays.visibility = View.VISIBLE
                inflate.carSignDaysBtn.visibility = View.GONE
                val adhereSignDay = "再坚持打卡${checkInData.hold_on_days}天，即可完成上次补打卡任务"
                val spannableString1 = com.mooc.common.dsl.spannableString {
                    str = adhereSignDay
                    colorSpan {
                        start = adhereSignDay.indexOf(checkInData.hold_on_days)
                        end =
                            adhereSignDay.indexOf(checkInData.hold_on_days) + checkInData.hold_on_days.length
                        color = this@CheckInActivity.getColorRes(R.color.colorPrimary)
                    }
                    absoluteSpan {
                        start = adhereSignDay.indexOf(checkInData.hold_on_days)
                        end =
                            adhereSignDay.indexOf(checkInData.hold_on_days) + checkInData.hold_on_days.length
                        size = 18.dp2px()
                    }
                }
                inflate.adhereSignDays.text = spannableString1
            }
            "2" -> {
                inflate.adhereSignDays.visibility = View.VISIBLE
                inflate.carSignDaysBtn.visibility = View.GONE
                val adhereSignDay = if (checkInData.old_task_success) {
                    "补打卡任务成功，离获得新的补打卡机会还有${checkInData.new_chance_days}天"
                } else {
                    "补打卡任务失败，离获得新的补打卡机会还有${checkInData.new_chance_days}天"
                }
                val spannableString1 = com.mooc.common.dsl.spannableString {
                    str = adhereSignDay
                    colorSpan {
                        start = adhereSignDay.indexOf(checkInData.new_chance_days)
                        end =
                            adhereSignDay.indexOf(checkInData.new_chance_days) + checkInData.new_chance_days.length
                        color = this@CheckInActivity.getColorRes(R.color.colorPrimary)
                    }
                    absoluteSpan {
                        start = adhereSignDay.indexOf(checkInData.new_chance_days)
                        end =
                            adhereSignDay.indexOf(checkInData.new_chance_days) + checkInData.new_chance_days.length
                        size = 18.dp2px()
                    }
                }
                inflate.adhereSignDays.text = spannableString1
            }
            else -> {
                inflate.adhereSignDays.visibility = View.GONE
                inflate.carSignDaysBtn.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        inflate.pieView.stopAnimation()
        timer?.cancel()
        timer = null

    }

    /**
     * 打卡分享
     */
    private fun share() {


        val binding: ViewPointShareBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.view_point_share,
            null,
            false
        )
//        binding.total_point
        binding.totalDay.setText(checkInDataBean?.continue_days)
        val userName = GlobalsUserManager.userInfo?.name ?: ""
        binding.name.setText(userName)
        binding.totalPoint.setText(checkInDataBean?.score?.toString()?:"0")

        val userAvatar = GlobalsUserManager.userInfo?.avatar ?: ""

        Glide.with(this@CheckInActivity).asBitmap()
            .load(userAvatar)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.sharePointHeader.setImageBitmap(resource)
                    onAvaterLoad(binding)

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)

                    binding.sharePointHeader.setImageResource(R.mipmap.common_ic_user_head_normal)
                    onAvaterLoad(binding)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

    }


    fun onAvaterLoad(binding: ViewPointShareBinding) {
        runOnMain {
            val shareAddScore = ARouter.getInstance().build(Paths.SERVICE_SHARE)
                .navigation() as ShareSrevice
            val shareBitmap = BitmapUtils.createUnShowBitmapFromLayout(binding.root)
            val commonBottomSharePop = CommonBottomSharePop(this,
                {
                    shareAddScore.shareAddScore(
                        ShareTypeConstants.TYPE_CHECKIN,
                        this@CheckInActivity,
                        IShare.Builder()
                            .setSite(it)
                            .setTitle("")
                            .setMessage("")
                            .setImageBitmap(shareBitmap)
                            .build()
                    )
                })
            XPopup.Builder(this)
                .asCustom(commonBottomSharePop)
                .show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUSET_CODE_REPAIR_CHECKIN) {
            //确定补签，刷新接口数据
            initData()
        }
    }

}


