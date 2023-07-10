package com.mooc.studyproject.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.INTENT_COMMENT_ID
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.INTENT_IS_CAN_COMMENT
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.INTENT_PLANDETAILS_DATA
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.INTENT_PLAN_DYNAMIC_ID
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.INTENT_PLAN_DYNAMIC_IS_INITIATOR
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.StudyprojectActivityCommentListBinding
import com.mooc.studyproject.fragment.CommendListFragment
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
//import kotlinx.android.synthetic.main.studyproject_activity_comment_list.*

/**
 *评论列表页面
 * @author limeng
 * @date 2020/12/24
 */
@Route(path = Paths.PAGE_COMMENTLIST)
class CommentListActivity : BaseActivity() {
    private var mDynamicId = ""//动态id
    private var commentId = 0
    private var isCreate = false
    private var mToUserId = 0//被评论人的id
    private var mStudyPlanDetailBean: StudyPlanDetailBean? = null //学习相关数据
    private var mIsInitiator = false //是否是发起人
    private var isCanComment = false
    val PARAMS_COMMENT_REQUESTCODE = 104 //评论页面请求码

    var commendListFragment: CommendListFragment? = null

    private lateinit var inflater: StudyprojectActivityCommentListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyprojectActivityCommentListBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        getIntentData()
        initView()
        initData()
        initListener()
    }


    private fun getIntentData() {
        if (intent != null) {
            mDynamicId = intent.getStringExtra(INTENT_PLAN_DYNAMIC_ID) ?: ""
//            mToUserId = intent.getIntExtra(INTENT_PLAN_DYNAMIC_USER_ID, -1)
            mIsInitiator = intent.getBooleanExtra(INTENT_PLAN_DYNAMIC_IS_INITIATOR, false)
            commentId = intent.getIntExtra(INTENT_COMMENT_ID, -1)
            mStudyPlanDetailBean = intent.getParcelableExtra<StudyPlanDetailBean>(INTENT_PLANDETAILS_DATA)
            isCanComment = intent.getBooleanExtra(INTENT_IS_CAN_COMMENT, true)
            isCreate = true
        }
    }

    private fun initView() {
    }

    private fun initData() {
        if (mStudyPlanDetailBean != null) {
            if (mStudyPlanDetailBean?.is_join == 1) {
                if (isCanComment) {
                    inflater.viewSend.setVisibility(View.VISIBLE)
                } else {
                    inflater.viewSend.setVisibility(View.GONE)
                }
            } else {
                if (isCanComment) {
                    inflater.viewSend.setVisibility(View.VISIBLE)
                } else {
                    inflater.viewSend.setVisibility(View.GONE)
                }
            }
        } else {
            if (isCanComment) {
                inflater.viewSend.setVisibility(View.VISIBLE)
            } else {
                inflater.viewSend.setVisibility(View.GONE)
            }
        }
        inflater.commonTitle.middle_text = resources.getString(R.string.comment)
        commendListFragment = CommendListFragment()
        commendListFragment?.mStudyPlanDetailBean = mStudyPlanDetailBean
        commendListFragment?.mIsInitiator = mIsInitiator
        commendListFragment?.mDynamicId = mDynamicId
        commendListFragment?.isCanComment = isCanComment
        if (commendListFragment != null) {
            supportFragmentManager.beginTransaction().add(R.id.commendfragment, commendListFragment!!).commit()
        }


    }

    private fun initListener() {
        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.tvPublishComment.setOnClickListener {

            val planId = mStudyPlanDetailBean?.study_plan?.id
            ARouter.getInstance().build(Paths.PAGE_PUBLISHINGDYNAMICSCOMMEN)
                    .withBoolean(PublishingDynamicsCommentActivity.IS_PUBLISH_COMMENT, true)
                    .withString(PublishingDynamicsCommentActivity.STUDY_PLAN_ID, planId)
                    .withString(PublishingDynamicsCommentActivity.PARAMS_DYNAMICID_KEY, mDynamicId.toString())
                    .navigation(this, PARAMS_COMMENT_REQUESTCODE)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        commendListFragment?.onActivityResult(requestCode, resultCode, null)

    }

}