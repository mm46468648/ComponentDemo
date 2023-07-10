package com.mooc.studyproject.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.getDrawableRes
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.FragmentAdapter
import com.mooc.studyproject.databinding.StudyprojectActivityPublishingDynamicsCommentBinding
import com.mooc.studyproject.fragment.PublishDynamicsFragment
import com.mooc.studyproject.fragment.PublishVoiceFragment
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
//@Route(path = Paths.PAGE_PUBLISHINGDYNAMICSCOMMEN)
class PublishingDynamicsCommentActivity : BaseActivity() {

    companion object {
        val IS_PUBLISH_COMMENT = "is_publish_comment"
        val PARAMS_DYNAMICID_KEY = "params_dynamicid_key"//评论动态的id
        val PARAMS_COMMENTID_KEY = "params_commentid_key"
        val PARAMS_COMMENTUSERID_KEY = "params_commentuserid_key"
        val STUDY_PLAN_ID = "study_plan_id"
        val PARAMS_ONLY_IMAGE = "params_only_image"
        val PARAMS_ONLY_TEXT = "params_only_text"
        val INTENT_STUDY_PLAN_RESOURCE = "intent_study_plan_resource"
        val INTENT_STUDY_PLAN_REVIEW_CHECKIN_PAGE = "intent_study_plan_review_checkin_page"
        val INTENT_STUDY_DYNAMIC_WORD_LIMIT = "intent_study_dynamic_word_limit"
    }

    var publishDynamicsFragment: PublishDynamicsFragment? = null
    var publishVoiceFragment: PublishVoiceFragment? = null
    var views: ArrayList<Fragment> = ArrayList<Fragment>()
    var titles: ArrayList<String> = ArrayList()
    private var studyPlanSource: StudyPlanSource? = null
    private var planId: String? = null
    private var numLimit = 0

    private var isPublishComment = false //是否是发表评论 true是发表评论  false是发表动态

    private var dynamicId: String? = null //评论的动态id
    private var dynamicType: String? = null //发表动态的type leixing //音频 1 图片 2 文字 3

    private var commendid: String? = null //评论的id

    private var commendUserId: String? = null //评论用户的id
    private var hasAudio: Boolean = true
    private var hasImage: Boolean = true
    private var hasText: Boolean = true

    private lateinit var inflater: StudyprojectActivityPublishingDynamicsCommentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyprojectActivityPublishingDynamicsCommentBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        getIntentData()
        initView()
        initData()
        initListener()
    }


    private fun getIntentData() {
        val intentData = intent
        if (intentData != null) {
            //是否是评论页面
            isPublishComment = intentData.getBooleanExtra(IS_PUBLISH_COMMENT, false)



            if (isPublishComment) {//是评论的页面
                dynamicId = intentData.getStringExtra(PARAMS_DYNAMICID_KEY)
                commendid = intentData.getStringExtra(PARAMS_COMMENTID_KEY)
                commendUserId = intentData.getStringExtra(PARAMS_COMMENTUSERID_KEY)
                //学习项目id
                planId = intentData.getStringExtra(STUDY_PLAN_ID)
            } else {
                //动态字数限制
                numLimit = intentData.getIntExtra(INTENT_STUDY_DYNAMIC_WORD_LIMIT, 0)

                //学习项目id
                planId = intentData.getStringExtra(STUDY_PLAN_ID)

                // 学习清单 可能不传 ？
                studyPlanSource = intentData.getParcelableExtra(INTENT_STUDY_PLAN_RESOURCE)
//                studyPlanSource?.let {
                dynamicType = intentData.getStringExtra(INTENT_STUDY_PLAN_REVIEW_CHECKIN_PAGE)
                ///音频 1 图片 2 文字 3
                if (dynamicType?.contains("1") == true && dynamicType?.contains("2") == true &&
                        dynamicType?.contains("3") == true) {//保持现有逻辑不变
                    hasAudio = true
                    hasImage = true
                    hasText = true
                } else if (dynamicType?.contains("2") == true && dynamicType?.contains("3") == true) {
                    hasAudio = false
                    hasImage = true
                    hasText = true
                } else if (dynamicType?.contains("1") == true && dynamicType?.contains("2") == true) {
                    hasAudio = true
                    hasImage = true
                    hasText = false
                } else if (dynamicType?.contains("1") == true && dynamicType?.contains("3") == true) {
                    hasAudio = true
                    hasImage = false
                    hasText = true
                } else if (dynamicType?.contains("1") == true) {
                    hasAudio = true
                    hasImage = false
                    hasText = false

                } else if (dynamicType?.contains("2") == true) {
                    hasAudio = false
                    hasImage = true
                    hasText = false


                } else if (dynamicType?.contains("3") == true) {
                    hasAudio = false
                    hasImage = false
                    hasText = true

                }

            }

        }

    }

    private fun initView() {
        if (isPublishComment) {//发表评论
            inflater.commonTitleLayout.middle_text = resources.getString(R.string.text_publishing_comment)
        } else {//发表动态
            inflater.commonTitleLayout.middle_text = resources.getString(R.string.text_publishing_dynamics)


        }
    }

    private fun initData() {
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inflater.commonTitleLayout.getWindowToken(), 0)
        initItemViewPager()
        if (studyPlanSource != null) {
            if ("1".equals(studyPlanSource?.set_is_complate)) {
                inflater.completeTv.setVisibility(View.VISIBLE)
                if ("1".equals(studyPlanSource?.audio_is_complate)) {
                    setDrawable(1)
                    inflater.completeTv.setText(getString(R.string.text_has_complete_track))
                } else {
                    setDrawable(0)
                    inflater.completeTv.setText(getString(R.string.text_no_complete_track))
                }
            } else {
                inflater.completeTv.setVisibility(View.GONE)
            }
        } else {
            inflater.completeTv.setVisibility(View.GONE)
        }
    }

    private fun setDrawable(state: Int) {
        val drawable: Drawable? = if (state == 1) {
            this.getDrawableRes(R.mipmap.common_ic_complete_track)
        } else {
            this.getDrawableRes(R.mipmap.common_ic_warning_track)
        }
        inflater.completeTv.setDrawLeft(drawable)
    }

    private fun initItemViewPager() {
        //传递动态评论数据
        val bundle = Bundle()
        bundle.putBoolean(IS_PUBLISH_COMMENT, isPublishComment)
        bundle.putString(PARAMS_DYNAMICID_KEY, dynamicId)
        bundle.putString(PARAMS_COMMENTID_KEY, commendid)
        bundle.putString(PARAMS_COMMENTUSERID_KEY, commendUserId)
        bundle.putInt(INTENT_STUDY_DYNAMIC_WORD_LIMIT, numLimit)
        publishDynamicsFragment = PublishDynamicsFragment()
        publishDynamicsFragment?.setArguments(bundle)

        publishVoiceFragment = PublishVoiceFragment()
        publishVoiceFragment?.setArguments(bundle)

        publishDynamicsFragment?.planId = planId
        studyPlanSource?.let {
            publishDynamicsFragment?.studyPlanSource = studyPlanSource
        }
        publishVoiceFragment?.planId = planId
        studyPlanSource?.let {
            publishVoiceFragment?.studyPlanSource = studyPlanSource
        }
        if (publishDynamicsFragment == null || publishVoiceFragment == null) {
            return
        }
        if (true == hasAudio && true == hasImage && true == hasText) {
            publishDynamicsFragment?.numLimit = numLimit

            views.add(publishDynamicsFragment!!)
            views.add(publishVoiceFragment!!)
            titles.add("图文动态")
            titles.add("语音动态")
            inflater.tabs.addTab(inflater.tabs.newTab())
            inflater.tabs.addTab(inflater.tabs.newTab())
            val viewPagerAdapter = FragmentAdapter(supportFragmentManager, views, titles)
            inflater.viewPager.adapter = viewPagerAdapter
            inflater.tabs.setupWithViewPager(inflater.viewPager)
        } else if (false == hasAudio && true == hasImage && true == hasText) {
            publishDynamicsFragment?.numLimit = numLimit

            views.add(publishDynamicsFragment!!)
            titles.add("图文动态")
            inflater.tabs.addTab(inflater.tabs.newTab())
            val viewPagerAdapter = FragmentAdapter(supportFragmentManager, views, titles)
            inflater.viewPager.adapter = viewPagerAdapter
            inflater.tabs.setupWithViewPager(inflater.viewPager)
            inflater.tabs.visiable(false)
        } else {
            if (hasText) {
                publishDynamicsFragment?.numLimit = numLimit
                views.add(publishDynamicsFragment!!)
                publishDynamicsFragment?.onlyShowText = true
                titles.add("文字动态")
            }
            if (hasImage) {
                views.add(publishDynamicsFragment!!)
                publishDynamicsFragment?.onlyShowPic = true
                titles.add("图片动态")
            }
            if (hasAudio) {
                views.add(publishVoiceFragment!!)
                titles.add("语音动态")
            }

            if (titles.size <= 1) {
                inflater.tabs.visiable(false)
            }
            titles.forEach {
                inflater.tabs.addTab(inflater.tabs.newTab())
            }

            val viewPagerAdapter = FragmentAdapter(supportFragmentManager, views, titles)
            inflater.viewPager.adapter = viewPagerAdapter
            inflater.tabs.setupWithViewPager(inflater.viewPager)
        }

    }


    private fun initListener() {
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }
    }
}