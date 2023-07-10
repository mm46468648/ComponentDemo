package com.mooc.studyroom.ui.activity

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.common.ktextends.sp2px
import com.mooc.common.utils.TimeUtils.formatTimeWithMin
import com.mooc.common.utils.TimeUtils.formatUnitWithMin
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityDataboardBinding
import com.mooc.studyroom.model.StudyStatusBean
import com.mooc.studyroom.ui.pop.ShareChooseDialog
import com.mooc.studyroom.viewmodel.DataBoardViewModel

/**
 *数据看板页面
 * @author limeng
 * @date 2021/2/24
 */
@Route(path = Paths.PAGE_DATA_BOARD)
class DataBoardActivity : BaseActivity() {
    private var mStatusBean: StudyStatusBean? = null
    var checks: BooleanArray? = null
    var shareSite: Int = 0
//    val mViewModel: DataBoardViewModel by lazy {
//        ViewModelProviders.of(this)[DataBoardViewModel::class.java]
//    }
    val mViewModel: DataBoardViewModel by viewModels()
    private lateinit var inflater: StudyroomActivityDataboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyroomActivityDataboardBinding.inflate(layoutInflater)
        setContentView(inflater.root)

//        LogUtil.addLoadLog(LogPageConstants.PID_DATA)
        iniView()


        inflater.swipeRefreshLayout.isRefreshing = true
        getData()
        initListener()
        initDataListener()
    }



    private fun getData() {
        mViewModel.getDataBordData()
    }

    private fun initListener() {
        //去分享
        inflater.commonTitle.setOnRightIconClickListener(View.OnClickListener {
            showChoseDialog()
        })

        inflater.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getData()
        })
    }

    /**
     * 弹出来选择弹框
     */
    private fun showChoseDialog() {
        val dialog = ShareChooseDialog(this, R.style.DefaultDialogStyle)
        dialog.show()
        dialog.toshareClick = {
            checks = it
//            val shareDialog = ShareDialog.newInstance(Bundle().put(ShareDialog.PARAMS_SHOW_SCHOOL, false))
//            shareDialog.onItemClick = {
//                shareSite = it
//                mViewModel.getShareData(mStatusBean)
//
////                it?.let { it1 -> mViewModel.getShareData( mStatusBean) }
//            }
//            shareDialog.show(supportFragmentManager, "dataBoard")

            val shareDialog = CommonBottomSharePop(this, {site->
                shareSite = site
                mViewModel.getShareData(mStatusBean)
            })
            XPopup.Builder(this)
                .asCustom(shareDialog)
                .show()
        }

    }

    private fun initDataListener() {
        //处理返回的数据
        mViewModel.result.observe(this, Observer {
            inflater.swipeRefreshLayout.isRefreshing = false
            it?.let { it1 -> setData(it1) }
        })
        mViewModel.mUrl.observe(this, Observer {
            val todayScore: String
            val totalScore: String
            var rank: String
            val medal: String
            var contentCenter = ""
            if (checks?.get(0) == true) {
                todayScore = String.format(getString(R.string.study_share_content_today_score), mStatusBean?.today_score)
                contentCenter += todayScore
            }
            if (checks?.get(1) == true) {
                totalScore = String.format(getString(R.string.study_share_content_total_score), mStatusBean?.total_score)
                contentCenter += totalScore
            }


            if (checks?.get(2) == true) {
                if (mStatusBean?.share_medal_count?.toInt() != null && mStatusBean?.check_medal_count?.toInt() != null
                        && mStatusBean?.specail_medal_count?.toInt() != null) {
                    val medalNum: Int = mStatusBean?.check_medal_count?.toInt()!!
                    +mStatusBean?.share_medal_count?.toInt()!!
                    +mStatusBean?.specail_medal_count?.toInt()!!
                    medal = java.lang.String.format(getString(R.string.study_share_content_medal), medalNum)
                    contentCenter += medal
                }
            }
            val content = String.format(getString(R.string.study_share_content), contentCenter)
            val strTitle: String = getString(R.string.share_study_title)

            val build = IShare.Builder()
                    .setSite(shareSite)
                    .setTitle(strTitle)
                    .setWebUrl(it)
                    .setMessage(content)
                    .build()
            shareAddScore.shareAddScore(ShareTypeConstants.TYPE_DATA,this, build)

        })
    }

    val shareAddScore: ShareSrevice by lazy {
        ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
    }

    /**
     * 刷新数据
     */
    private fun setData(bean: StudyStatusBean) {
        mStatusBean = bean
        val userBean = GlobalsUserManager.userInfo
        if (userBean != null) {
            if (!TextUtils.isEmpty(userBean.id)) {
                val userNum = "${getString(R.string.my_user_num)}${userBean.id}"
                inflater.tvStudyId.setText(userNum)
            }
        }
        inflater.tvStudyTime.setText(bean.today)
        inflater.tvTodayScore.setText(bean.today_score)

        val allScoreSpannableString = spannableString {
            str = "总积分\n\n${bean.total_score}"
            absoluteSpan {
                size = 18.dp2px()
                start = 5
                end = str.length
            }
        }

        val checkInSpannableString = spannableString {
            str = "累计打卡\n\n${bean.user_check_count}天"
            absoluteSpan {
                size = 18.dp2px()
                start = 6
                end = str.indexOf("天")
            }
        }

        val honorNumSpannableString = spannableString {
            str = "获得勋章\n\n${bean.check_medal_count}个"
            absoluteSpan {
                size = 18.dp2px()
                start = 6
                end = str.indexOf("个")
            }
        }

//        tvTotalScore.setText("总积分\n\n${bean.total_score}")
        inflater.tvTotalScore.setText(allScoreSpannableString)
//        tvCheckinCount.setText("累计打卡\n\n${bean.user_check_count}天")
        inflater.tvCheckinCount.setText(checkInSpannableString)
//        tvMedalCount.setText("获得勋章\n\n${bean.check_medal_count}个")
        inflater.tvMedalCount.setText(honorNumSpannableString)

        bean.course_cnt?.let { setSpanTextView(inflater.detailInclude.tvCourseNum, it, 2) }
        bean.learn_all_time?.let { setSpanTextView(inflater.detailInclude.tvCourseMin, it, 3) }
        bean.periodical?.let { setSpanTextView(inflater.detailInclude.tvPeriodicalStudy, it, 4) }
        bean.article?.let { setSpanTextView(inflater.detailInclude.tvArticleStudy, it, 5) }
        bean.knowledge?.let { setSpanTextView(inflater.detailInclude.tvKnowStudy, it, 6) }
        bean.baike?.let { setSpanTextView(inflater.detailInclude.tvBaiStudy, it, 7) }
        bean.track_count?.let { setSpanTextView(inflater.detailInclude.tvTrackStudy, it, 8) }
        bean.ebook?.let { setSpanTextView(inflater.detailInclude.tvBookStudy, it, 9) }
        bean.study_continue_count?.let { setSpanTextView(inflater.detailInclude.tvContinuousLearn, it, 10) }
        bean.study_total_count?.let { setSpanTextView(inflater.detailInclude.tvAccumulatingLearn, it, 11) }
        bean.micro_course?.let { setSpanTextView(inflater.detailInclude.tvMicroNum, it, 14) }

    }

    private fun iniView() {
        inflater.commonTitle.setOnLeftClickListener { finish() }

        //积分明细
        inflater.tvLookScoreDetail.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_SCORE_DETAIL).navigation()
        }

        //积分规则
        inflater.tvLookScoreRule.setOnClickListener {
//            LogUtil.addClickLog(LogPageConstants.PID_DATA,"",LogEventConstants.BID_TAB, LogPageConstants.PID_DATA, LogPageConstants.PID_INTEGRALRULE)
            ARouter.getInstance().build(Paths.PAGE_SCORE_RULE).navigation()
        }

    }

    private fun setSpanTextView(textView: TextView, str: String, type: Int) {
        val spannableString: SpannableString
        when (type) {
            2 -> {
                val courseNum = resources.getString(R.string.study_course_num) + str
                spannableString = SpannableString(courseNum)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 3, courseNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(23.sp2px()), 3, courseNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


                textView.text = spannableString
            }
            3 -> {
                val cunt: String = formatUnitWithMin(str.toInt())
                val time: String = formatTimeWithMin(str.toDouble())
                val courseMin = resources.getString(R.string.study_course_min) + time + cunt
                spannableString = SpannableString(courseMin)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 5, courseMin.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(25.sp2px()), 5, courseMin.length - cunt.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), courseMin.length - cunt.length, courseMin.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.text = spannableString
            }
            4 -> {
                val perNum = resources.getString(R.string.study_periodical_count) + str
                spannableString = SpannableString(perNum)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 3, perNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(23.sp2px()), 3, perNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                textView.text = spannableString
            }
            5 -> {
                val articleNum = resources.getString(R.string.study_article_count) + str
                spannableString = SpannableString(articleNum)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 3, articleNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(23.sp2px()), 3, articleNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                textView.text = spannableString
            }
            6 -> {
                val knowNum = resources.getString(R.string.study_know_count) + str
                spannableString = SpannableString(knowNum)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 4, knowNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(23.sp2px()), 4, knowNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                textView.text = spannableString
            }
            7 -> {
                val baiNum = resources.getString(R.string.study_bai_count) + str
                spannableString = SpannableString(baiNum)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 3, baiNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(23.sp2px()), 3, baiNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                textView.text = spannableString
            }
            8 -> {
                val trackNum = resources.getString(R.string.study_track_count) + str
                spannableString = SpannableString(trackNum)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 3, trackNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(23.sp2px()), 3, trackNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                textView.text = spannableString
            }
            9 -> {
                val bookNum = resources.getString(R.string.study_book_count) + str
                spannableString = SpannableString(bookNum)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 3, bookNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(23.sp2px()), 3, bookNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                textView.text = spannableString
            }
            10 -> {
                val continuousLearn = resources.getString(R.string.text_continuous_learn) + str + "天"
                spannableString = SpannableString(continuousLearn)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 5, continuousLearn.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(25.sp2px()), 5, continuousLearn.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), continuousLearn.length - 1, continuousLearn.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.text = spannableString
            }
            11 -> {
                val accumulatingLearn = resources.getString(R.string.text_accumulating_learn) + str + "天"
                spannableString = SpannableString(accumulatingLearn)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 5, accumulatingLearn.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(25.sp2px()), 5, accumulatingLearn.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), accumulatingLearn.length - 1, accumulatingLearn.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.text = spannableString
            }
            14 -> {
                val microNum = resources.getString(R.string.study_micro_count) + str
                spannableString = SpannableString(microNum)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_3)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_28A_FFF)), 3, microNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(12.sp2px()), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AbsoluteSizeSpan(23.sp2px()), 3, microNum.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                textView.text = spannableString
            }
        }
    }
}