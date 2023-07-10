package com.mooc.studyproject.fragment

//import kotlinx.android.synthetic.main.studyproject_fragment_study_source.*
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.eventbus.StudyProjectRefresh
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.StudySourceAdapter
import com.mooc.studyproject.eventbus.ClickFinishEvent
import com.mooc.studyproject.eventbus.StudyDynamicChangeEvent
import com.mooc.studyproject.model.*
import com.mooc.studyproject.presenter.ItemClickPresenter
import com.mooc.studyproject.presenter.SomeUtilsPresenter
import com.mooc.studyproject.ui.FollowUpNewActivity
import com.mooc.studyproject.ui.PublishingDynamicsCommentActivity
import com.mooc.studyproject.viewmodel.MustLearnViewModel
import com.mooc.studyproject.window.LearnPlanMembersPop
import com.tencent.bugly.crashreport.CrashReport
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject

/**
 *必学项目列表
 * @author limeng
 * @date 3/9/22
 */
class MustLearnFragment(
    var isDaShiMode: Boolean = false,
    var mStudyPlanDetailBean: StudyPlanDetailBean? = null
) : BaseListFragment<StudyPlanSource, MustLearnViewModel>() {
    val utilsPresenter: SomeUtilsPresenter by lazy { SomeUtilsPresenter() }
    private var studyPlanSource: StudyPlanSource? = null
    protected var dialog: CustomProgressDialog? = null
    private val REQUEST_CODE_DYNAMIC = 100
    private var signPosition = 0
    val itemClickPresenter: ItemClickPresenter by lazy {
        ItemClickPresenter(
            requireActivity(),
            mStudyPlanDetailBean
        )
    }

//    fun upDateData() {
//        if (mAdapter != null) {
//            val adapter = mAdapter as StudySourceAdapter
//            adapter.mStudyPlanData = mStudyPlanDetailBean?.study_plan
//            adapter.mJoin = mStudyPlanDetailBean?.is_join ?: 0
//            loadDataWithRrefresh()
//        }
//
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(refresh: StudyProjectRefresh) {
        this.mStudyPlanDetailBean = refresh.detail
        val adapter = mAdapter as StudySourceAdapter
        adapter.mStudyPlanData = mStudyPlanDetailBean?.study_plan
        adapter.mJoin = mStudyPlanDetailBean?.is_join ?: 0
        loadDataWithRrefresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initAdapter(): BaseQuickAdapter<StudyPlanSource, BaseViewHolder>? {
        mViewModel?.id = mStudyPlanDetailBean?.study_plan?.id

        return mViewModel?.getPageData()?.value?.let {
            setEmpty()
            val studySourceAdapter = StudySourceAdapter(it)
            studySourceAdapter.isDaShiMode = isDaShiMode
            studySourceAdapter.mStudyPlanData = mStudyPlanDetailBean?.study_plan
            studySourceAdapter.mJoin = mStudyPlanDetailBean?.is_join ?: 0

            studySourceAdapter.addChildClickViewIds(R.id.tvSign)
            studySourceAdapter.setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.tvSign -> {
                        signPosition = position
                        studyPlanSource = studySourceAdapter.data.get(position)
                        //计划已结束点击打卡，不显示打卡转盘
                        if (mStudyPlanDetailBean?.study_plan?.let {
                                utilsPresenter.isUnStartOrStop(
                                    DateUtil.getCurrentTime(),
                                    it.plan_starttime,
                                    it.plan_endtime
                                )
                            } == 2) {
                            toast(resources.getString(R.string.study_plan_finish))
                            return@setOnItemChildClickListener
                        }
                        //  如果是跟读资源，点击打卡，也进入跟读页面
                        if (ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE == studyPlanSource?.source_type) {
                            enterFollowUpActivity(studyPlanSource)
                            return@setOnItemChildClickListener
                        }

                        if (mStudyPlanDetailBean?.study_plan != null) {
                            if (TextUtils.isEmpty(studyPlanSource?.before_resource_check_status)) {
                                studyPlanSource?.before_resource_check_status = "0"
                            }
                            postResourceClickStatus()
                        }
                    }
                }
            }
            studySourceAdapter.setOnItemClickListener { adapter, view, position ->

                val studyPlanSourceBean = studySourceAdapter.getItem(position);
                if (studyPlanSourceBean.source_type == ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE) {
                    if (studyPlanSourceBean.checkin_bool != 0) {
                        val view = studySourceAdapter.getViewByPosition(position, R.id.tvSign)
                        if (studyPlanSourceBean.is_click) {
                            if (view?.isEnabled == true) {
                                itemClickPresenter.ItemClick(studySourceAdapter, position)
                            } else {
                                return@setOnItemClickListener
                            }
                        } else {
                            return@setOnItemClickListener
                        }

                    }

                } else {
                    itemClickPresenter.ItemClick(studySourceAdapter, position)

                }

            }

            studySourceAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = CustomProgressDialog.createLoadingDialog(requireActivity())
        val adapter = mAdapter as StudySourceAdapter
        adapter.addFooterView(getFootView())
//        mViewModel?.audioInfoException?.observe(viewLifecycleOwner, Observer {
//            toPublicDynamic()
//        })
        //处理自建音频请求是否听完
        mViewModel?.resultMsgBean?.observe(viewLifecycleOwner, Observer {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }

            if (it != null) {
                if (!TextUtils.isEmpty(it.code)) {
                    if (it.code.equals("10009")) {
                        reDynamicDialog()
                    } else if (it.code.equals("10011")) {
                        tipDialog(it)
                    } else {
                        dialogOne(it)
                    }
                } else {
                    dialogOne(it)
                }
            }
        })
        //自建音频检查是否是 获取音频播放状态（音频）
        mViewModel?.resourceStatusBean?.observe(viewLifecycleOwner, Observer {
            dialog?.dismiss()
            if (it != null) {
                if (!TextUtils.isEmpty(it.is_complate)) {
                    studyPlanSource?.audio_is_complate = it.is_complate
                }
                toPublicDynamic()
            }
        })
        // 发布动态结果处理
//        mViewModel?.dynamicsBean?.observe(viewLifecycleOwner, Observer {
//            if (it != null) {
//                toast(it.msg)
//                if (it.error_code != 10004) {
//                    if (TextUtils.isEmpty(studyPlanSource?.review_checkin_status)) {
//                        studyPlanSource?.review_checkin_status = "0"
//                    }
//
//                    updateSourceItem(it)
//                    //获取动态发布者
//                    getMembers()
//                    //通知事件，刷新动态和我的动态列表
//                    EventBus.getDefault().post(StudyDynamicChangeEvent())
//
//                }
//            }
//        })
        //获取发布者的信息
//        mViewModel?.studyMemberBean?.observe(viewLifecycleOwner, Observer {
//            val memberList = it?.result
//            if (memberList != null && memberList.size > 0) {
//
//                val activity = requireActivity()
//                if(!activity.isFinishing){
//                    val pop = LearnPlanMembersPop(requireActivity(), recycler_view, memberList)
//                    pop.likeMemberListener =
//                        { textView: TextView?, imageView: ImageView?, memberListBean: MemberListBean ->
//                            likeMember(textView, imageView, memberListBean)
//                        }
//                    pop.show()
//                }
//            }
//        })
//        mViewModel?.postFillStudyPlanBean?.observe(viewLifecycleOwner, Observer {
//            likeView?.visibility = View.VISIBLE
//            likeMemberBean?.isLike = true
//            likeName?.setBackgroundResource(R.drawable.shape_corners10_stroke0_5_color3)
//            activity?.resources?.getColor(R.color.white)?.let { it1 -> likeName?.setTextColor(it1) }
//        })
    }

    /**
     * 跳转到跟读资源页面
     */
    private fun enterFollowUpActivity(dataBean: StudyPlanSource?) {

        val intent = Intent(activity, FollowUpNewActivity::class.java)
        intent.putExtra(
            FollowUpNewActivity.PARAMS_RESOURSE_ID,
            java.lang.String.valueOf(dataBean?.source_other_id)
        )
        intent.putExtra(
            FollowUpNewActivity.PARAMS_CHECKIN_SOURCE_ID,
            java.lang.String.valueOf(dataBean?.id)
        )
        intent.putExtra(
            FollowUpNewActivity.PARAMS_STUDYPLAN_ID,
            mStudyPlanDetailBean?.study_plan?.id
        )
        requireActivity().startActivityForResult(intent, IntelligentLearnListFragment.REFRESH_ALL)
    }

    // 检查
    private fun postResourceClickStatus() {
        if (dialog?.isShowing == false) {
            dialog?.show()
        }
        val requestData = JSONObject()
        try {
            requestData.put("study_plan", mStudyPlanDetailBean?.study_plan?.id)
            requestData.put("checkin_source", studyPlanSource?.id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody =
            requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        mViewModel?.postResourceClickStatus(stringBody)
    }

    /**
     * 处理动态重新操作
     */
    private fun reDynamicDialog() {
        studyPlanSource?.apply {
            activity?.let { activity ->
                val message: String = if (this.is_re_chick) {
                    val temp = if ((studyPlanSource?.need_score
                            ?: 0) <= (mStudyPlanDetailBean?.study_plan?.user_all_score ?: 0)
                    ) {
                        activity.resources?.getString(R.string.study_can_re_score)
                    } else {
                        activity.resources?.getString(R.string.study_re_score)
                    }

                    String.format(
                        temp ?: "",
                        this.need_score,
                        mStudyPlanDetailBean?.study_plan?.user_all_score ?: 0
                    )
                } else {
                    activity.resources?.getString(R.string.study_source_sign) ?: ""
                }
                val publicDialogBean = PublicDialogBean()
                publicDialogBean.strMsg = message
                publicDialogBean.strLeft = resources.getString(R.string.text_ok)
                publicDialogBean.strRight = resources.getString(R.string.text_cancel)
                publicDialogBean.isLeftGreen = 1
                XPopup.Builder(activity)
                    .asCustom(PublicDialog(activity, publicDialogBean) {
                        if (it == 0) {
                            if (studyPlanSource?.is_re_chick == true) { //是否需要补打卡
                                if ((studyPlanSource?.need_score
                                        ?: 0) <= (mStudyPlanDetailBean?.study_plan?.user_all_score
                                        ?: 0)
                                ) {
                                    resourceOrMusicDynamic()
                                }
                            } else {
                                resourceOrMusicDynamic()
                            }
                        }
                    })
                    .show()
            }
        }


    }

    private fun tipDialog(resultMsgBean: ResultMsgBean?) {
        activity?.let { activity ->
            val publicDialogBean = PublicDialogBean()
            val message = resultMsgBean?.message
            if (resultMsgBean?.code.equals("10011")) {
                //您的音频未听完，现在打卡后需要花50积分重新打卡，是否继续打卡？
                if (message?.contains("温馨提示") == true) {
                    val spannableString = SpannableString(message)
                    spannableString.setSpan(
                        activity.resources?.getColor(R.color.color_2)
                            ?.let { ForegroundColorSpan(it) },
                        0,
                        27,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        activity.resources?.getColor(R.color.color_F8935D)
                            ?.let { ForegroundColorSpan(it) },
                        27,
                        message.length - 5,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        activity.resources?.getColor(R.color.color_2)
                            ?.let { ForegroundColorSpan(it) },
                        message.length - 5,
                        message.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    publicDialogBean.strSpan = spannableString
                } else {
                    publicDialogBean.strMsg = message
                }
                publicDialogBean.strLeft = getString(R.string.text_continue_clock_in)
                publicDialogBean.strRight = getString(R.string.text_back_listen)
                publicDialogBean.isLeftGreen = 0
            } else {
                publicDialogBean.strMsg = message
                publicDialogBean.strLeft = getString(R.string.text_ok)
                publicDialogBean.strRight = getString(R.string.text_cancel)
                publicDialogBean.isLeftGreen = 1
            }
            XPopup.Builder(activity)
                .asCustom(PublicDialog(activity, publicDialogBean) {
                    if (it == 0) {
                        if (resultMsgBean != null) {
                            if ("10011" == resultMsgBean.code) {
                                reDynamicDialog()
                            }
                        }
                    }
                })
                .show()
        }
    }

    /**
     * 确定dialog
     */
    private fun dialogOne(resultMsgBean: ResultMsgBean) {
        activity?.let { activity ->
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = resultMsgBean.message
            publicDialogBean.strTv = resources.getString(R.string.text_ok)
            val dialog = PublicOneDialog(activity, publicDialogBean)
            XPopup.Builder(activity)
                .asCustom(dialog)
                .show()
        }
    }

    /**
     * 判断资源是否是自建音频
     */
    private fun resourceOrMusicDynamic() {
        if (studyPlanSource?.source_type == ResourceTypeConstans.TYPE_ONESELF_TRACK) {
            dialog?.show()
            mViewModel?.getResourceStatus(studyPlanSource?.source_other_id ?: "")
        } else {
            toPublicDynamic()
        }
    }

    //    var likeName: TextView? = null
//    var likeView: ImageView? = null
//    var likeMemberBean: MemberListBean? = null
    private fun likeMember(name: TextView?, like: ImageView?, dataBean: MemberListBean) {
        val requestData = JSONObject()
        try {
            requestData.put("like_type", "1")
            requestData.put("type_id", java.lang.String.valueOf(dataBean.activity_id))
            requestData.put("user", GlobalsUserManager.uid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody =
            requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

        mViewModel?.postFillStudyPlanNew(stringBody)?.observe(viewLifecycleOwner, Observer {
            like?.visibility = View.VISIBLE
            dataBean.isLike = true
            name?.setBackgroundResource(R.drawable.shape_corners10_stroke0_5_color3)
            name?.setTextColor(requireActivity().getColorRes(R.color.white))
        })
//        likeName = name
//        likeView = like
//        likeMemberBean = dataBean


    }

    private fun toPublicDynamic() {
        if (TextUtils.isEmpty(studyPlanSource?.review_checkin_status)) {
            studyPlanSource?.review_checkin_status = "0"
        }
        when (studyPlanSource?.review_checkin_status) {
            "0" -> { // 0 为不审核  直接请求发送动态接口
                publishDynamicsToNet()
            }
            "2" -> { //2为自动审核，直接请求发送动态接口
                publishDynamicsToNet()
            }
            else -> { // 1 为人工审核 review_checkin_page
                // 跳转发布动态页面
                mStudyPlanDetailBean?.study_plan?.id?.let {//音频 1 图片 2 文字 3
                    val dynamicType = studyPlanSource?.review_checkin_page?.joinToString(",")

                    mStudyPlanDetailBean?.study_plan?.num_activity_limit?.let { it1 ->
                        ARouter.getInstance().build(Paths.PAGE_PUBLISHINGDYNAMICSCOMMEN)
                            .withString(PublishingDynamicsCommentActivity.STUDY_PLAN_ID, it)
                            .withString(
                                PublishingDynamicsCommentActivity.INTENT_STUDY_PLAN_REVIEW_CHECKIN_PAGE,
                                dynamicType
                            )
                            .withParcelable(
                                PublishingDynamicsCommentActivity.INTENT_STUDY_PLAN_RESOURCE,
                                studyPlanSource
                            )
                            .withInt(
                                PublishingDynamicsCommentActivity.INTENT_STUDY_DYNAMIC_WORD_LIMIT,
                                it1
                            )
                            .navigation(activity, REQUEST_CODE_DYNAMIC)
                    }

                }

            }
        }

    }

    /**
     * 自动审核自动发送动态功能
     */
    private fun publishDynamicsToNet() {

        val requestData = JSONObject()


        requestData.put("study_plan", mStudyPlanDetailBean?.study_plan?.id)
        requestData.put("publish_content", "")
        requestData.put("publish_img", "")
        requestData.put("user_id", GlobalsUserManager.uid)
        requestData.put("publish_state", "1")
        requestData.put("activity_type", "0")
        requestData.put("activity_content_long", "")
        if (studyPlanSource != null) {
            requestData.put("activity_checkin_type", "0")
            requestData.put("checkin_source_id", java.lang.String.valueOf(studyPlanSource?.id))
            if (studyPlanSource?.is_re_chick == true) {
                requestData.put("is_new_checkin", "1")
                requestData.put("activity_bu_type", "1")
            }
        } else {
            requestData.put("activity_checkin_type", "1")
        }

        val stringBody =
            requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

        //发布动态结果处理
        mStudyPlanDetailBean?.study_plan?.id?.let { id ->
            mViewModel?.publishDynamicsNew(id, stringBody)?.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    toast(it.msg)
                    if (it.error_code != 10004) {
                        if (TextUtils.isEmpty(studyPlanSource?.review_checkin_status)) {
                            studyPlanSource?.review_checkin_status = "0"
                        }
                        updateSourceItem(it)
                        //获取动态发布者
                        getMembers()
                        //通知事件，刷新动态和我的动态列表
                        EventBus.getDefault().post(StudyDynamicChangeEvent())

                    }
                }
            })
        }
    }

    private fun updateSourceItem(bean: DynamicsBean?) {
        if (bean != null && mAdapter != null) {
            val studySourceAdapter = mAdapter as StudySourceAdapter
            studySourceAdapter.mJoin = mStudyPlanDetailBean?.is_join ?: 0
            val button: StudyClockState? = bean.button
            val nextButton: StudyClockState? = bean.next_button
            val sourceAfterBean: SourceAfterBean? = bean.after_resource_status
            if (button != null) {
                studyPlanSource?.button = button
            }
            val isSignSuccess = isSignSuccess()
            studyPlanSource?.let { studySourceAdapter.setData(signPosition, it) }
            studySourceAdapter.notifyItemChanged(signPosition, studyPlanSource)
            var nextSource: StudyPlanSource? = null
            if (nextButton != null && !nextButton.text.isNullOrEmpty()) {
                if (signPosition < (studySourceAdapter.data.size ?: 0) - 1) {
                    nextSource = studySourceAdapter.data.get(signPosition + 1)
                    val beforeResourceInfo = nextSource.before_resource_info
                    if (sourceAfterBean != null) {
                        beforeResourceInfo?.msg = sourceAfterBean.msg
                        beforeResourceInfo?.code = sourceAfterBean.code
                    }

                    nextSource.before_resource_info = beforeResourceInfo
                    nextSource.button = nextButton

                    studySourceAdapter.setData(signPosition + 1, nextSource)

                }
            }
            if (isSignSuccess) {// 已完成列表，可以发个通知
                studySourceAdapter.removeAt(signPosition)
                EventBus.getDefault().post(ClickFinishEvent())
            }
            if (studySourceAdapter.data.size == 0) {
                mAdapter?.setEmptyView(emptyView)
                initEmptyView()
                setEmpty()
            }
        }
    }

    private fun setEmpty() {
        emptyView.isShowEmptyIcon(false)
        if ("1" != mStudyPlanDetailBean?.study_plan?.is_bind_testpaper) {//未完成
            emptyView.setTitle(getString(R.string.text_str_finish_study_check))
            emptyView.setTitleViewBottom(120)
        } else {//智能导学  必学
            emptyView.setTitle(getString(R.string.text_str_must_learn_study_check))
            emptyView.setTitleViewBottom(120)
        }
    }

    private fun isSignSuccess(): Boolean {
        val isSignSuccess: Boolean
        val button: StudyClockState? = studyPlanSource?.button

        isSignSuccess = if (studyPlanSource != null && button != null) {
            (!TextUtils.isEmpty(button.background_color_code)
                    && "4" == button.background_color_code)
        } else {
            false
        }
        return isSignSuccess
    }

    /**
     * 获取发布动态的其他人
     */
    private fun getMembers() {
        mStudyPlanDetailBean?.study_plan?.id?.let { planId ->
            mViewModel?.getStudySourceMembersNew(planId, studyPlanSource?.id.toString())
                ?.observe(viewLifecycleOwner, Observer {
                    val memberList = it?.result
                    if (memberList != null && memberList.size > 0) {
                        val activity = requireActivity()
                        if (!activity.isFinishing) {
                            val pop = LearnPlanMembersPop(activity, recycler_view, memberList)
                            pop.likeMemberListener =
                                { textView: TextView?, imageView: ImageView?, memberListBean: MemberListBean ->
                                    likeMember(textView, imageView, memberListBean)
                                }
                            pop.show()
                        }
                    }
                })
        }
    }



    private fun getFootView(): View {
        val view = LayoutInflater.from(activity)
            .inflate(R.layout.studyproject_item_plan_foot, null, false);
        return view;
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //此请求，和activity中的REQUEST_CODE_JOIN_PLAN重复了
            if (requestCode == REQUEST_CODE_DYNAMIC) {
                if (data != null) {
                    val bean =
                        data.getParcelableExtra(IntelligentLearnListFragment.INTENT_STUDY_PLAN_DYNAMIC) as DynamicsBean?
                    updateSourceItem(bean)
                    getMembers()
                }

            }
        }
    }
}