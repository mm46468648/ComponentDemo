package com.mooc.studyproject.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.common.webview.WebviewWrapper
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.studyproject.StudyPlan
import com.mooc.commonbusiness.pop.IncreaseScorePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils.Companion.getReplaceHtml
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.BetPointAdapter
import com.mooc.studyproject.databinding.StudyprojectActivityBetPointBinding
import com.mooc.studyproject.model.BetPoint
import com.mooc.studyproject.model.StudyPlanAddBean
import com.mooc.studyproject.viewmodel.StudyProjectViewModel
import com.mooc.studyproject.window.BetSuccessDialog
import com.mooc.studyproject.window.SplitPatternDialog
//import kotlinx.android.synthetic.main.studyproject_activity_bet_point.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

/**
 *挑战项目设置页面
 * @author limeng
 * @date 2021/3/12
 */

@Route(path = Paths.PAGE_BET_POINT)
class BetPointActivity : BaseActivity() {


    companion object {
        const val INTENT_KEY_IS_RESTRICT_USER = "intent_key_is_restrict"
        const val INTENT_STUDY_PLAN_DATA = "study_plan"

    }

    private var mBetPointAdapter: BetPointAdapter? = null

    private var studyPlan: StudyPlan? = null
    private var isRestrict: Boolean? = false//学习项目报名限制
    private var planId: String? = null
    private var userScore: Int = 0
    private var getScore: String? = null

//    val model: StudyProjectViewModel by lazy {
//        ViewModelProviders.of(this).get(StudyProjectViewModel::class.java);
//    }
    val model: StudyProjectViewModel by viewModels()
    private lateinit var inflater: StudyprojectActivityBetPointBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = StudyprojectActivityBetPointBinding.inflate(layoutInflater)

        setContentView(inflater.root)
        getIntentData()
        initView()
        initData()
        initListener()
        initDataListener()
    }

    private fun initDataListener() {
        model.getError().observe(this, Observer {
            toast(it.message)
        })
        model.studyPlanAddBean.observe(this, Observer {
            if (!TextUtils.isEmpty(it?.join_score)) {
                getScore = it?.join_score
                val score = it?.join_score?.toInt()
                if (score != null) {
                    if (score > 0) {//展示获得的积分 禁成长
//                        val scorePop = ShowScorePop(this, score, 0)
                        val scorePop = IncreaseScorePop(this, score, 0)
                        XPopup.Builder(this)
                                .asCustom(scorePop)
                                .show()

                    }
                }
            }
            if (it?.join_status == 1) {  //已经加入学习计划
                if ((it.integrity_score.isNullOrEmpty() || it.integrity_score.equals("0")) &&
                        (currentBean?.key.isNullOrEmpty() || currentBean?.key.equals("0")) &&
                        (getScore.isNullOrEmpty() || getScore.equals("0"))) {//禁成长
                    intent.putExtra(IntentParamsConstants.INTENT_KEY_JOIN, 1)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    showSuccessDialog(it, getScore)
                }

            } else {
                toast(it?.message)
            }
        })
    }

    val webviewWrapper by lazy { WebviewWrapper(this) }

    private fun getIntentData() {
        studyPlan = intent.getParcelableExtra(INTENT_STUDY_PLAN_DATA)
//        isNewUser = intent.getBooleanExtra(INTENT_KEY_IS_NEW_USER, false)
        isRestrict = intent.getBooleanExtra(INTENT_KEY_IS_RESTRICT_USER, false)
    }

    private fun initView() {
        mBetPointAdapter = BetPointAdapter(null)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        inflater.rcyBetPoint.setLayoutManager(staggeredGridLayoutManager)
        inflater.rcyBetPoint.setAdapter(mBetPointAdapter)
        addWebView()

    }

    private fun addWebView() {
        //将包装类中webview添加到布局
        val mWebView = webviewWrapper.getWebView()
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mWebView.layoutParams = layoutParams
        inflater.flRoot.addView(mWebView, 0)

    }

    private fun initData() {
        if (studyPlan != null) {
            if (studyPlan?.is_open_integrity == true) {//设置了诚信积分文案
                inflater.joinBtn.text = getString(R.string.join_study_sure)
            } else {
                inflater.joinBtn.text = getString(R.string.str_enroll)
            }
            val rules = studyPlan?.bet_rules
            if (!rules.isNullOrEmpty()) {
                val betPoints = getPointList(rules)
                currentBean = betPoints[0]
                mBetPointAdapter?.setNewInstance(betPoints)

            }
            planId = studyPlan?.id
            if (studyPlan?.user_all_score != null) {
                userScore = (studyPlan?.user_all_score ?: 0) + 500
            }
            if (!studyPlan?.pop_desc.isNullOrEmpty()) {
                splitDialog(studyPlan?.pop_desc ?: "")
            }
//            if (isNewUser!!) {
//                //新用户
//                invitationEdt.setVisibility(View.VISIBLE)
//                invitationTip.setVisibility(View.VISIBLE)
//            } else {
            inflater.invitationEdt.visibility = View.GONE
            inflater.invitationTip.visibility = View.GONE
//            }
            if (studyPlan?.plan_mode_status == 2) {    // 大师课模式可能会有积分预存说明
                inflater.betIntroTv.visibility = View.GONE
                var content = studyPlan?.bet_introduction
                if (!TextUtils.isEmpty(content)) {
                    content = content?.replace("<img", "<img width=\"100%\"")
                    inflater.betIntroTitle.visibility = View.VISIBLE
                    inflater.flRoot.visibility = View.VISIBLE
                } else {
                    inflater.betIntroTitle.visibility = View.GONE
                    inflater.flRoot.visibility = View.GONE
                }
                val html = content?.let { getReplaceHtml(it) }
                html?.let { webviewWrapper.loadBaseUrl(it) }
            } else {
                inflater.flRoot.visibility = View.VISIBLE
                inflater.betIntroTv.visibility = View.VISIBLE
                if (studyPlan?.plan_mode_status == 1) {
                    inflater.betIntroTv.text = getString(R.string.bet_point_split)
                } else {
                    inflater.betIntroTv.text = getString(R.string.bet_point_tip)
                }
            }

        }
    }

    private fun splitDialog(desc: String) {
        val dialog = SplitPatternDialog(this@BetPointActivity, R.style.DefaultDialogStyle)
        dialog.desc = desc
        dialog.show()
    }

    private var currentBean: BetPoint? = null
    private fun initListener() {
        mBetPointAdapter?.setOnItemClickListener { _, _, position ->
            currentBean = mBetPointAdapter?.data?.get(position)
            if (isRestrict == true) {//学习项目报名限制,只能选中不压积分参加学习项目
                if (position == 0) {
                    //选中一个
                    mBetPointAdapter?.data?.forEach {
                        it.selected = false
                    }
                    currentBean?.selected = true
                    mBetPointAdapter?.notifyDataSetChanged()
                } else {
                    //不能被选中
                    mBetPointAdapter?.data?.forEach {
                        it.selected = false
                    }
                    mBetPointAdapter?.data?.get(0)?.selected = true
                    currentBean = mBetPointAdapter?.data?.get(0)

                    currentBean?.selected = true
                    mBetPointAdapter?.notifyDataSetChanged()
                }
            } else {
                //选中一个
                mBetPointAdapter?.data?.forEach {
                    it.selected = false
                }
                currentBean?.selected = true
                mBetPointAdapter?.notifyDataSetChanged()
            }
        }
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }
        inflater.joinBtn.setOnClickListener {
            postAddStudyDialog()
        }
    }

    /**
     * 加入学习项目弹框
     */
    @Suppress("DEPRECATION")
    fun postAddStudyDialog() {
        val betScore = currentBean?.key?.toInt()
        var message = ""
        if (betScore != null) {
            message = if (betScore > 0) {
                if (betScore <= userScore) {
                    String.format(resources.getString(R.string.study_bet_score), betScore)
                } else {
                    String.format(resources.getString(R.string.study_bet_add), betScore)
                }
            } else {
                resources.getString(R.string.study_no_bet_add)
            }
        }

        val publicDialogBean = PublicDialogBean()
        publicDialogBean.strMsg = message
        publicDialogBean.strLeft = resources?.getString(R.string.text_cancel)
        publicDialogBean.strRight = resources?.getString(R.string.text_ok)
        publicDialogBean.isLeftGreen = 0
        XPopup.Builder(this)
                .asCustom(PublicDialog(this, publicDialogBean) {
                    if (it == 1) {
                        if (betScore != null) {
                            if (betScore <= userScore) {
                                postAddStudyPlan()
                            }
                        }
                    }
                })
                .show()
    }

    /**
     * 报名学习计划
     */
    private fun postAddStudyPlan() {

        var receiver_user_id = inflater.invitationEdt.text.toString().trim()
        if (TextUtils.isEmpty(receiver_user_id)) {
            receiver_user_id = ""
        }

        if (GlobalsUserManager.uid == receiver_user_id) {
            toast("用户不能邀请自己")
            return
        }

        val requestData = JSONObject()
        try {
            requestData.put("study_plan", planId)
            requestData.put("user", GlobalsUserManager.uid)
            requestData.put("start_score", currentBean?.key)
            requestData.put("end_score", currentBean?.value)
            requestData.put("receiver_user_id", receiver_user_id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        model.postAddStudyPlan(stringBody)

    }

    /**
     * 获取积分获取和预存的列表
     */
    private fun getPointList(rules: String): MutableList<BetPoint> {
        val betPoints: MutableList<BetPoint> = ArrayList()
        if (!TextUtils.isEmpty(rules)) {
            val points = rules.split("],\\[".toRegex()).toTypedArray()
            for (i in points.indices) {
                var item = points[i]
                val betPoint = BetPoint()
                if (i == 0) {
                    item = if (points.size == 1) {
                        item.substring(2, item.length - 2)
                    } else {
                        item.substring(2, item.length)
                    }
                    val point = item.split(",".toRegex()).toTypedArray()
                    betPoint.selected = true
                    betPoint.key = point[0]
                    betPoint.value = point[1]
                    betPoints.add(betPoint)
                } else if (i == points.size - 1) {
                    item = item.substring(0, item.length - 2)
                    val point = item.split(",".toRegex()).toTypedArray()
                    betPoint.key = point[0]
                    betPoint.value = point[1]
                    betPoints.add(betPoint)
                } else {
                    val point = item.split(",".toRegex()).toTypedArray()
                    betPoint.key = point[0]
                    betPoint.value = point[1]
                    betPoints.add(betPoint)
                }
            }
        } else {
            val betPoint1 = BetPoint()
            betPoint1.selected = true
            betPoint1.key = "0"
            betPoint1.value = "0"
            betPoints.add(betPoint1)
        }
        return betPoints
    }

    private fun showSuccessDialog(studyPlanAddBean: StudyPlanAddBean, getScore: String?) {
        val betSuccessDialog = BetSuccessDialog(this, R.style.DefaultDialogStyle)
        betSuccessDialog.onButtonClick = {
            intent.putExtra(IntentParamsConstants.INTENT_KEY_JOIN, 1)
            setResult(RESULT_OK, intent)
            finish()
        }
        betSuccessDialog.chengxinScore = studyPlanAddBean.integrity_score
        betSuccessDialog.invitationScore = currentBean?.key
        betSuccessDialog.getScore = getScore
        betSuccessDialog.strButton = getString(R.string.text_ok)
        betSuccessDialog.show()
    }

}


