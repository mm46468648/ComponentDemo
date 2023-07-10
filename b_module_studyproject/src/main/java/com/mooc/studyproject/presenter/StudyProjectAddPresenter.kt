package com.mooc.studyproject.presenter

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.dialog.PublicPromisedDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.R
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.studyproject.ui.BetPointActivity
import com.mooc.studyproject.ui.StudyProjectActivity
import com.mooc.studyproject.viewmodel.StudyProjectViewModel
import com.mooc.studyproject.window.ExchangeChooseDialog
import com.mooc.studyproject.window.InputDialog
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

/**

 * @Author limeng
 * @Date 2021/5/6-3:13 PM
 */
class StudyProjectAddPresenter {
    var context: Context? = null
    var planId: String? = null
    val model: StudyProjectViewModel by lazy {
        ViewModelProviders.of(context as StudyProjectActivity)
            .get(StudyProjectViewModel::class.java);
    }

    var mStudyPlanDetailBean: StudyPlanDetailBean? = null

    //        fun isToBet(): Boolean {//是否押注
//        var mStudyPlanData = mStudyPlanDetailBean?.study_plan
//        return mStudyPlanData?.is_bet == 1
//
//    }
//    var isToBet = {//是否押注
//        val mStudyPlanData = mStudyPlanDetailBean?.study_plan
//        mStudyPlanData?.is_bet == 1
//    }

    //是否押注
    fun isToBet(): Boolean = mStudyPlanDetailBean?.study_plan?.is_bet == 1

    fun initDataListener() {
        //处理 学习计划兑换码兑换
        model.exChangeBean.observe(context as FragmentActivity) {
            if (it.success) {
                when (getExchangeMode()) {
                    A ->
                        toBetPoint()
                    B, C -> postAddStudyPlan()
                    D, E -> {
                    }

                }
            } else {
                toast(it.msg)
            }
        }
    }

    private val A = "A"
    private val B = "B"
    private val C = "C"
    private val D = "D"
    private val E = "E"

    //学习项目报名 限制dialog
    fun restrictDialog(isDaShi: Boolean) {
        context?.let { context ->
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = "欢迎您再次参加该项目，此次学习无需预存积分，也不再进行积分奖励"
            publicDialogBean.strLeft = context.resources?.getString(R.string.text_ok)
            publicDialogBean.strRight = context.resources?.getString(R.string.text_cancel)
            publicDialogBean.isLeftGreen = 1
            XPopup.Builder(context)
                .asCustom(PublicDialog(context, publicDialogBean) {
                    if (it == 0) {
                        if (isDaShi) {//受限用户大师课模式
                            //大师课模式，受限用户
                            joinLearnMethod()
                        } else {
                            if (!isToBet()) { //不需要进入预存页面
                                postAddStudyPlan()
                            } else {
                                // 跳转到预存页面
                                toBetPoint()
                            }
                        }
                    }
                })
                .show()
        }
    }

    /**
     *大师课报名参加学习项目
     */
    fun integrityCommitmentDialog() {
        val mStudyPlanData = mStudyPlanDetailBean?.study_plan


        val strHtmlMsg: String?
        var strRight: String? = null

        val message =
            "维护公平、公正的学习环境是我们共同的责任。请承诺以诚信行为参加本学习项目，系统根据不同项目预先奖励一定诚信积分。同时，系统将不定期对涉嫌刷分情况进行检测，若在学习过程中有被系统判定有涉嫌刷分行为，将<span style=\"color:#D37C00;font-weight:700;\">双倍扣除</span>诚信积分，并在平台公示。"
        strHtmlMsg = if (mStudyPlanData != null) {
            if (!TextUtils.isEmpty(mStudyPlanData.chengxin_pop_info)) {
                mStudyPlanData.chengxin_pop_info
            } else {
                message
            }
        } else {
            message


        }
        if (TextUtils.isEmpty(mStudyPlanDetailBean?.coupon_used_status) || mStudyPlanDetailBean?.coupon_used_status.equals(
                "1"
            )
        ) {
            strRight = context?.resources?.getString(R.string.text_str_next_step)
        } else {
            when (getExchangeMode()) {
                A, B, C, D -> strRight = context?.resources?.getString(R.string.text_str_next_step)
                E -> strRight = (context?.resources?.getString(R.string.text_ok))
            }
        }
        val dialog = context?.let { PublicPromisedDialog(it, strHtmlMsg, strRight) }
        dialog?.onLeftListener = {
            dialog?.dismiss()

        }
        dialog?.onRightListener = {
            if (it != null && it) {
                joinLearnMethod()
                dialog?.dismiss()
            } else {
                toast(context?.resources?.getString(R.string.text_str_integrity_commitment_cb_remind))
            }
        }
        XPopup.Builder(context)
            .asCustom(dialog)
            .show()

    }

    //    挑战项目设置
    var toBetPoint = {
        val mStudyPlanData = mStudyPlanDetailBean?.study_plan

//        var isNewUser = false
//        isNewUser = mStudyPlanDetailBean?.user_join_date!! >= (mStudyPlanData?.join_start_time!! / 1000)
        ARouter.getInstance()
            .build(Paths.PAGE_BET_POINT)
            .withParcelable(BetPointActivity.INTENT_STUDY_PLAN_DATA, mStudyPlanData)
//                .withBoolean(BetPointActivity.INTENT_KEY_IS_NEW_USER, isNewUser)
            .withBoolean(
                BetPointActivity.INTENT_KEY_IS_RESTRICT_USER,
                mStudyPlanDetailBean!!.is_restrict
            )
            .navigation(
                context as StudyProjectActivity,
                StudyProjectActivity.REQUEST_CODE_JOIN_PLAN
            )
    }

    //点击加入学习项目的处理方法
    fun joinLearnMethod() {
        val mStudyPlanData = mStudyPlanDetailBean?.study_plan
        if (mStudyPlanData?.plan_mode_status == 2) {  //大师课模式
            if (mStudyPlanDetailBean != null && (mStudyPlanDetailBean?.coupon_used_status
                        != null) && mStudyPlanDetailBean?.coupon_used_status.equals("1")
            ) {
                toBetPoint()
                return
            }
            when (getExchangeMode()) {
                A, C -> showExchangeCodeDialog()
                B -> showExchangeChooseDialog()
                D -> toBetPoint()
                E -> postAddStudyPlan()
            }
        } else { //其他模式
            if (!isToBet()) { //不需要进入预存页面
                postAddStudyPlan()
            } else {
                // 跳转到预存页面
                toBetPoint()
            }
        }
    }

    /***
     * 获取模式A,B,C,D,E
     * plan_master_relation  兑换码与预存关系   0 没有  1  且    2 或
     * plan_master_is_code   是否使用兑换码   0  不使用   1 使用
     * is_bet  是否预存 0 不预存  1预存
     */
    private fun getExchangeMode(): String {
        val mStudyPlanData = mStudyPlanDetailBean?.study_plan

        if ("1" == mStudyPlanData?.plan_master_is_code && mStudyPlanData.is_bet == 1
            && "1" == mStudyPlanData.plan_master_relation
        ) {
            return A
        }
        if ("1" == mStudyPlanData?.plan_master_is_code && mStudyPlanData.is_bet == 1
            && "2" == mStudyPlanData.plan_master_relation
        ) {
            return B
        }
        if ("1" == mStudyPlanData?.plan_master_is_code && mStudyPlanData.is_bet == 0) {
            return C
        }
        if ("0" == mStudyPlanData?.plan_master_is_code && mStudyPlanData.is_bet == 1) {
            return D
        }
        return if ("0" == mStudyPlanData?.plan_master_is_code && mStudyPlanData.is_bet == 0) {
            E
        } else E
    }

    /**
     * 参与方式的选择
     */
    private fun showExchangeChooseDialog() {

        val dialog = context?.let { ExchangeChooseDialog(it, R.style.DefaultDialogStyle) }
        dialog?.onCompleteListener = {
            when (getExchangeMode()) {
                B -> {
                    if (it.equals("1")) {       //1：用兑换码参加  0：用积分参加
                        showExchangeCodeDialog()
                    }
                    if (it.equals("0")) {//用积分参加
                        toBetPoint()
                    }
                }
            }
        }
        dialog?.show()
    }

    /**
     * 学习计划兑换弹框
     */
    private fun showExchangeCodeDialog() {
        val inputDialog = context?.let { InputDialog(it, R.style.DefaultDialogStyle) }
        inputDialog?.onCompleteListener = {
            exchangeCode(it)

        }

        inputDialog?.strTitle = context?.resources?.getString(R.string.txt_input_exchange_code)
        inputDialog?.strHint = context?.resources?.getString(R.string.hint_input_exchange_code)
        inputDialog?.visiable = View.GONE
        inputDialog?.show()
    }

    /**
     * 学习计划兑换码兑换
     */
    private fun exchangeCode(code: String?) {
        val requestData = JSONObject()
        requestData.put("studyplan_id", planId.toString())
        requestData.put("code", code)

        val stringBody =
            requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

        model.exchangeCode(stringBody)

    }

    /**
     * 报名学习计划
     */
    private fun postAddStudyPlan() {
        val requestData = JSONObject()
        try {
            requestData.put("study_plan", planId)
            requestData.put("user", GlobalsUserManager.uid)
            requestData.put("start_score", "0")
            requestData.put("end_score", "0")
            requestData.put("receiver_user_id", "0")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody =
            requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        model.postAddStudyPlan(stringBody)

    }

    fun getPlanNum(): String? {
        val mStudyPlanData = mStudyPlanDetailBean?.study_plan
        var planNum: String? = ""
        when (isEnrolment(
            DateUtil.getCurrentTime(),
            mStudyPlanData?.join_start_time,
            mStudyPlanData?.join_end_time
        )) {
            0, 1 -> {
                planNum = if (mStudyPlanData?.is_set_limit_num == true) {//设置了限制人数
                    mStudyPlanData.plan_num.toString() + "/" + mStudyPlanData.limit_num
                } else {
                    mStudyPlanData?.plan_num.toString()
                }
            }
            2 -> planNum = java.lang.String.valueOf(mStudyPlanData?.plan_num)
        }
        return planNum
    }

    /**
     * 报名的类型
     */
    fun isEnrolment(time: Long, startTime: Long?, stopTime: Long?): Int {
        if (startTime != null && stopTime != null) {
            return if (time < startTime * 1000) { //尚未到报名期
                0
            } else if (time >= startTime * 1000 && time <= stopTime * 1000) { //报名期
                1
            } else { //报名期结束
                2
            }
        }
        return 2

    }

    //    /**
//     * 是否能够在 动态和我的动态底部 会显示能够"发布我的动态" 按钮
//     */
    fun isShowDynamic(): Boolean {
        val mStudyPlanData = mStudyPlanDetailBean?.study_plan

        return if (mStudyPlanData != null) {
            if (mStudyPlanData.activity_status == 0) {
                true
            } else if (mStudyPlanData.activity_status == 1) {
                if (mStudyPlanDetailBean?.is_start_user!!) {
                    true
                } else {
                    mStudyPlanData.activity_status <= 0 || DateUtil.getCurrentTime() < mStudyPlanData.set_activity_time * 1000
                }
            } else if (mStudyPlanData.activity_status == 2) {
                if (mStudyPlanDetailBean?.is_start_user!!) {
                    true
                } else {
                    mStudyPlanData.set_activity_time <= 0 || DateUtil.getCurrentTime() >= mStudyPlanData.set_activity_time * 1000
                }
            } else if (mStudyPlanData.activity_status == 3) {
                mStudyPlanDetailBean?.is_start_user!!// 是否发起人
            } else {
                true
            }
        } else {
            false
        }
    }

}