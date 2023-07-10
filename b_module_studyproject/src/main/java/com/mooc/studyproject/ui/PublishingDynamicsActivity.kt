package com.mooc.studyproject.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.PublishDynamicFragmentAdapter
import com.mooc.studyproject.databinding.StudyprojectActivityPublishingDynamicsBinding
import com.mooc.studyproject.model.StudyPlanSource
//import kotlinx.android.synthetic.main.studyproject_activity_publishing_dynamics_comment.*
import java.util.*

/**
 *发表动态或者评论 评论页面
 * // TODO limeng 区分都有哪几种跳进来并传参的参数
 * 1、评论类表页面中对单个评论  CommendListFragment  发表对评论的评论
 * ithBoolean(IS_PUBLISH_COMMENT, true)
.withString(PARAMS_DYNAMICID_KEY, mDynamicId.toString())
.withString(PARAMS_COMMENTID_KEY, data.id.toString())
.withString(PARAMS_COMMENTUSERID_KEY, receiverUserId.toString())
 * 2、学习清单类表页面 StudySourceFragment  学习清单打卡后，自动跳转发表动态
 * .withString(STUDY_PLAN_ID, it)
.withParcelable(INTENT_STUDY_PLAN_RESOURCE, studyPlanSource)
.withInt(INTENT_STUDY_DYNAMIC_WORD_LIMIT, it1)
3、评论列表 发表评论 CommentListActivity
.withBoolean(PublishingDynamicsCommentActivity.IS_PUBLISH_COMMENT, true)
.withString(PublishingDynamicsCommentActivity.PARAMS_DYNAMICID_KEY, mDynamicId.toString())
.navigation(this, PARAMS_COMMENT_REQUESTCODE)
4、StudyProjectActivity  学习项目底部的完成音频的发表动态
.withString(PublishingDynamicsCommentActivity.STUDY_PLAN_ID, it2)
.withInt(PublishingDynamicsCommentActivity.INTENT_STUDY_DYNAMIC_WORD_LIMIT, it1)
.navigation(this, REQUEST_CODE_DYNAMIC)
 * @author limeng
 * @date 2020/12/22
 */
@Route(path = Paths.PAGE_PUBLISHINGDYNAMICSCOMMEN)
class PublishingDynamicsActivity : BaseActivity() {

    companion object {
        val IS_PUBLISH_COMMENT = "is_publish_comment"
        val PARAMS_DYNAMICID_KEY = "params_dynamicid_key"//评论动态的id
        val PARAMS_COMMENTID_KEY = "params_commentid_key"
        val PARAMS_COMMENTUSERID_KEY = "params_commentuserid_key"
        val PARAMS_ONLY_IMAGE = "params_only_image"
        val PARAMS_ONLY_TEXT = "params_only_text"
        val STUDY_PLAN_ID = "study_plan_id"
        val INTENT_STUDY_PLAN_RESOURCE = "intent_study_plan_resource"
        val INTENT_STUDY_PLAN_REVIEW_CHECKIN_PAGE = "intent_study_plan_review_checkin_page"
        val INTENT_STUDY_DYNAMIC_WORD_LIMIT = "intent_study_dynamic_word_limit"
    }

    var titles: ArrayList<String> = ArrayList()
    private var studyPlanSource: StudyPlanSource? = null
    private var planId: String? = null
    private var numLimit = 0
    private var isPublishComment = false //是否是发表评论 true是发表评论  false是发表动态
    private var dynamicId: String = "" //评论的动态id
    private var dynamicType: String = "" //发表动态的type leixing //音频 1 图片 2 文字 3
    private var commendid: String = "" //评论的id
    private var commendUserId: String = "" //评论用户的id


    private lateinit var inflater: StudyprojectActivityPublishingDynamicsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyprojectActivityPublishingDynamicsBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        getIntentData()
        initView()
        initData()
        initListener()
    }


    private fun getIntentData() {
        val intentData = intent ?: return
        //是否是评论页面
        isPublishComment = intentData.getBooleanExtra(IS_PUBLISH_COMMENT, false)
        if (isPublishComment) {//是评论的页面
            dynamicId = intentData.getStringExtra(PARAMS_DYNAMICID_KEY) ?: ""
            commendid = intentData.getStringExtra(PARAMS_COMMENTID_KEY) ?: ""
            commendUserId = intentData.getStringExtra(PARAMS_COMMENTUSERID_KEY) ?: ""
            //学习项目id
            planId = intentData.getStringExtra(STUDY_PLAN_ID)
        } else {
            //动态字数限制
            numLimit = intentData.getIntExtra(INTENT_STUDY_DYNAMIC_WORD_LIMIT, 0)
            //学习项目id
            planId = intentData.getStringExtra(STUDY_PLAN_ID)
            // 学习项目数据
            studyPlanSource = intentData.getParcelableExtra(INTENT_STUDY_PLAN_RESOURCE)
            dynamicType = intentData.getStringExtra(INTENT_STUDY_PLAN_REVIEW_CHECKIN_PAGE) ?: ""
        }
    }

    private fun initView() {
        if (isPublishComment) {//发表评论
            inflater.commonTitleLayout.middle_text =
                resources.getString(R.string.text_publishing_comment)
        } else {//发表动态
            inflater.commonTitleLayout.middle_text =
                resources.getString(R.string.text_publishing_dynamics)
        }
    }

    private fun initData() {
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inflater.commonTitleLayout.getWindowToken(), 0)

        //如果是发布动态,并且需要提示(是否完成音频学习)
        if (studyPlanSource != null && "1".equals(studyPlanSource?.set_is_complate)) {
            inflater.completeTv.setVisibility(View.VISIBLE)
            if ("1".equals(studyPlanSource?.audio_is_complate)) {
                inflater.completeTv.setDrawLeft(R.mipmap.common_ic_complete_track)
                inflater.completeTv.setText(getString(R.string.text_has_complete_track))
            } else {
                inflater.completeTv.setDrawLeft(R.mipmap.common_ic_warning_track)
                inflater.completeTv.setText(getString(R.string.text_no_complete_track))
            }
        } else {
            inflater.completeTv.setVisibility(View.GONE)
        }

        initItemViewPager()
    }

    private fun initItemViewPager() {
        //传递动态评论数据
        val bundle = Bundle()
        bundle.putBoolean(IS_PUBLISH_COMMENT, isPublishComment)
        bundle.putString(PARAMS_DYNAMICID_KEY, dynamicId)
        bundle.putString(PARAMS_COMMENTID_KEY, commendid)
        bundle.putString(PARAMS_COMMENTUSERID_KEY, commendUserId)
        bundle.putString(STUDY_PLAN_ID, planId)
        bundle.putParcelable(INTENT_STUDY_PLAN_RESOURCE, studyPlanSource)
        bundle.putInt(INTENT_STUDY_DYNAMIC_WORD_LIMIT, numLimit)
        titles.clear()

        if (dynamicType.contains("2") && !dynamicType.contains("3")) {
            bundle.putBoolean(PARAMS_ONLY_IMAGE, true)
            titles.add("图片动态")
        }

        if (dynamicType.contains("3") && !dynamicType.contains("2")) {
            bundle.putBoolean(PARAMS_ONLY_TEXT, true)
            titles.add("文字动态")
        }

        if (dynamicType.contains("2") && dynamicType.contains("3")) {
            titles.add("图文动态")
        }

        if (dynamicType.contains("4")) {
            titles.add("视频动态")
        }

        if (dynamicType.contains("1")) {
            titles.add("语音动态")
        }



        //默认,显示图文,和音频
        if(dynamicType.isEmpty()){
            titles.add("图文动态")

//            //非评论添加视频动态
//            if(!isPublishComment){
//                titles.add("视频动态")
//            }
            titles.add("语音动态")
        }

        //初始化适配器
        val viewPagerAdapter = PublishDynamicFragmentAdapter(titles, this, bundle)
        inflater.viewPager.adapter = viewPagerAdapter

        //只有一个tab不显示
        if (titles.size <= 1) {
            inflater.tabs.visiable(false)
            return
        }

        //绑定tab
        val tabLayoutMediator =
            TabLayoutMediator(inflater.tabs, inflater.viewPager, true) { tab, position ->
                tab.text = titles[position]
            }
        tabLayoutMediator.attach()

    }


    private fun initListener() {
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }
    }
}