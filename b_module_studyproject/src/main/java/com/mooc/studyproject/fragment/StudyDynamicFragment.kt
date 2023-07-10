package com.mooc.studyproject.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.MY_USER_ID
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.model.studyproject.StudyPlan
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.pop.IncreaseScorePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.StudyDynamicAdapter
import com.mooc.studyproject.eventbus.StudyDynamicChangeEvent
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.studyproject.ui.NominationActivity.Companion.ACTIVITY_ID
import com.mooc.studyproject.viewmodel.StudyProjectViewModel
//import kotlinx.android.synthetic.main.studyproject_fragment_study_dynamic.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *动态列表页面
 * @author limeng
 * @date 2020/11/27
 */
class StudyDynamicFragment : Fragment() {
    private var mStudyPlanData: StudyPlan? = null

    var mStudyDynamicAdapter: StudyDynamicAdapter? = null
    private var param1: StudyPlanDetailBean? = null
    private var param2: String? = null
    var limit: Int = 10
    var titleOffSet: Int = 0
    var mStudyPlanDetailBean: StudyPlanDetailBean? = null
    var planId: String? = null
    var offset: Int = 0
    var title: String? = null
    var isTitleClick = false
    private val REQUEST_CODE_DYNAMIC_COMMENT = 101
    var mRecyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        arguments?.let {
            mStudyPlanDetailBean = it.getParcelable(ARG_PARAM1) as StudyPlanDetailBean?
            param2 = it.getString(ARG_PARAM2)
        }
    }

    /**
     * 增加脚布局，适配底部的view
     */
    private fun getFootView(): View {
        val view = LayoutInflater.from(activity).inflate(R.layout.studyproject_item_plan_foot, null, false);
        return view;
    }

    fun updateData(bean: StudyPlanDetailBean?) {
        mStudyPlanDetailBean = bean
        initView()

        offset = 0
        initData()
        initListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRecyclerView = view.findViewById(R.id.mRecyclerView)
        initView()
        initData()
        initListener()
        initDataListener()
    }

    //    val model: StudyProjectViewModel by lazy {
//        ViewModelProviders.of(this).get(StudyProjectViewModel::class.java);
//    }
    private var model: StudyProjectViewModel? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        model = ViewModelProviders.of(this).get(StudyProjectViewModel::class.java);

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.studyproject_fragment_study_dynamic, container, false)

        return view
    }


    private fun updateAdapterData() {
        mRecyclerView?.setVisibility(View.VISIBLE)
        mStudyDynamicAdapter?.mIsInitiator = mStudyPlanDetailBean?.is_start_user ?: false
        mStudyDynamicAdapter?.mStudyPlanData = mStudyPlanData
        if (dynamic.equals(param2)) {
            mStudyDynamicAdapter?.isSelf = false
        } else if (dynamicMy.equals(param2)) {
            mStudyDynamicAdapter?.isSelf = true
        }

        mStudyDynamicAdapter?.mJoin = mStudyPlanDetailBean?.is_join ?: 0
        mStudyDynamicAdapter?.notifyDataSetChanged()
    }

    private fun initDataListener() {
        //分享学友圈结果
        model?.shareSchoolCircleBean?.observe(viewLifecycleOwner, Observer {
            if (it?.code == 1) {
                if (it.score == 1) {
                    toast(it.message)
//                    val scorePop = ShowScorePop(requireContext(), it.score, 0)
                    val scorePop = IncreaseScorePop(requireContext(), it.score, 0)
                    XPopup.Builder(context)
                            .asCustom(scorePop)
                            .show()
                } else if (it.score == 0) {
                    toast(it.message + "," + it.share_message)
                }
            } else {
                toast(it?.message)
            }
        })
        //数据处理
        model?.studyActivityBean?.observe(viewLifecycleOwner, Observer {
            updateAdapterData()
            setData(it?.results)
        })
        //wo de title数据
        model?.studyActivityWithTitleBean?.observe(viewLifecycleOwner, Observer {
            updateAdapterData()
            isTitleClick = true
            setData(it?.results)


        })
        //动态列表点赞的监听
        model?.postFillStudyPlanBean?.observe(viewLifecycleOwner, Observer {

            toast(it?.message?.detail)
            if (it?.code.equals("200")) {
                if (clickStudyDynamic?.like_num != null) {
                    clickStudyDynamic?.like_num = (clickStudyDynamic?.like_num ?: 0) + 1

                }
                clickStudyDynamic?.is_like = true
                mStudyDynamicAdapter?.notifyDataSetChanged()
            } else if (it?.code.equals("201")) {
                if (clickStudyDynamic?.like_num != null && (clickStudyDynamic?.like_num
                                ?: 0) >= 1) {
                    clickStudyDynamic?.like_num = (clickStudyDynamic?.like_num ?: 0) - 1
                }

                clickStudyDynamic?.is_like = false
                mStudyDynamicAdapter?.notifyDataSetChanged()

            }

        })
        //屏蔽动态接口
        model?.stopStudyPlan?.observe(viewLifecycleOwner, Observer {
            toast(it?.message?.detail)
            val code = it?.code
            if (code == "200") { //用户屏蔽成功
                clickStudyDynamic?.publish_state = 0
                mStudyDynamicAdapter?.notifyDataSetChanged()
            } else if (code == "201") {
                clickStudyDynamic?.publish_state = 1
                mStudyDynamicAdapter?.notifyDataSetChanged()
            }
        })
        //删除动态接口
        model?.delDynamicBean?.observe(viewLifecycleOwner, Observer {
            if (it?.code.equals("1")) {
                mStudyDynamicAdapter?.data?.remove(clickStudyDynamic)
                mStudyDynamicAdapter?.notifyDataSetChanged()
            }
        })
        //举报的接口 ,统一由公共弹窗处理了
//        model?.reportLearnBean?.observe(viewLifecycleOwner, Observer {
//            val reportLearnPopWindow = ReportLearnPopWindow(context, view)
//            reportLearnPopWindow.results = it?.results
//            reportLearnPopWindow.show()
//            reportLearnPopWindow.postReportListener = {
//                reportLearnPopWindow.onDismiss()
//                postReport(it, clickStudyDynamic)
//            }

//        })
    }

    var clickStudyDynamic: StudyDynamic? = null
    private fun initListener() {
        mStudyDynamicAdapter?.loadMoreModule?.setOnLoadMoreListener {
            offset += 10
            getData()
        }
        mStudyDynamicAdapter?.onDyniaSucStrClick = {
            if (it) {
                mStudyDynamicAdapter?.punch = "打卡成功"
            } else {
                mStudyDynamicAdapter?.punch = "打卡失败"
            }
            mStudyDynamicAdapter?.title?.let { it1 -> getDataWithTitleActivity(it1) }
        }
        mStudyDynamicAdapter?.addChildClickViewIds(R.id.titleDelTv, R.id.punchTv, R.id.llComment,
                R.id.llFill, R.id.llStop, R.id.llDel, R.id.llReport, R.id.llShare, R.id.flHead, R.id.tvSelfRecommend)

        mStudyDynamicAdapter?.setOnItemChildClickListener { _, view, position ->
            clickStudyDynamic = mStudyDynamicAdapter?.data?.get(position)
            if (view.id == R.id.titleDelTv) {// 删除 titleonTitleDelClick()
                mStudyDynamicAdapter?.title = ""
                offset = 0
                isTitleClick = false
                if (TextUtils.isEmpty(mStudyDynamicAdapter?.punch)) {
                    getData()
                } else {
                    getDataWithTitleActivity(mStudyDynamicAdapter?.title ?: "")
                }

            } else if (view.id == R.id.punchTv) {//onPunchDelClick
                mStudyDynamicAdapter?.punch = ""
                offset = 0
                if (TextUtils.isEmpty(mStudyDynamicAdapter?.punch)) {
                    getData()
                } else {
                    getDataWithTitleActivity(mStudyDynamicAdapter?.title ?: "")
                }
            } else if (view.id == R.id.llComment) {//评论
                val StudyDynamic = mStudyDynamicAdapter?.data?.get(position)
//                mStudyDynamicComment = data as StudyDynamic
                if (mStudyDynamicAdapter?.getItem(position) != null) {
                    //  评论list 页面
                    StudyDynamic?.id?.let {
                        StudyDynamic.user?.id?.let { it1 ->
                            mStudyPlanDetailBean?.is_start_user?.let { it2 ->
                                mStudyDynamicAdapter?.isCanSendComment?.let { it3 ->
                                    ARouter.getInstance().build(Paths.PAGE_COMMENTLIST)
                                            .withString(IntentParamsConstants.INTENT_PLAN_DYNAMIC_ID, it)
//                                            .withString(IntentParamsConstants.INTENT_PLAN_DYNAMIC_USER_ID, it1)
                                            .withBoolean(IntentParamsConstants.INTENT_PLAN_DYNAMIC_IS_INITIATOR, it2)
                                            .withBoolean(IntentParamsConstants.INTENT_IS_CAN_COMMENT, it3)
                                            .withParcelable(IntentParamsConstants.INTENT_PLANDETAILS_DATA, mStudyPlanDetailBean)
                                            .navigation(activity, REQUEST_CODE_DYNAMIC_COMMENT)
                                }

                            }

                        }

                    }
                }

            } else if (view.id == R.id.llFill) {//点赞

                fill(mStudyDynamicAdapter?.data?.get(position))

            } else if (view.id == R.id.llStop) {//屏蔽
                stopStudyPlanDialog(mStudyDynamicAdapter?.data?.get(position))
            } else if (view.id == R.id.llDel) {//删除
                deleteDialog(mStudyDynamicAdapter?.data?.get(position))

            } else if (view.id == R.id.llReport) {//举报
                report(mStudyDynamicAdapter?.data?.get(position))

            } else if (view.id == R.id.llShare) {//分享
                share(mStudyDynamicAdapter?.data?.get(position))
            } else if (view.id == R.id.flHead) {//跳转用户信息
                ARouter.getInstance()
                        .build(Paths.PAGE_USER_INFO)
                        .withString(MY_USER_ID, mStudyDynamicAdapter?.data?.get(position)?.user?.id.toString())
                        .navigation()
            } else if (view.id == R.id.tvSelfRecommend) {//我要自荐
                val studyDynamic: StudyDynamic? = mStudyDynamicAdapter?.data?.get(position)
                if (studyDynamic != null) {
                    val nomination: StudyDynamic.NominationBean? = studyDynamic.activity_nomination
                    if (nomination != null) {
                        val status = nomination.status
                        if (status?.isNotEmpty() == true) {
                            if (status == "1") {
                                ARouter.getInstance()
                                        .build(Paths.PAGE_NOMINATION)
                                        .withString(ACTIVITY_ID, studyDynamic.id)
                                        .navigation()
                            }
                        }
                    }
                }
            }

        }
        mStudyDynamicAdapter?.onTitleClick = { title: String, b: Boolean ->
            mStudyDynamicAdapter?.title = title
            getDataWithTitleActivity(title)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == REQUEST_CODE_DYNAMIC_COMMENT) {
//                if (data != null) {
//                    val commentCount = data.getIntExtra(INTENT_PLAN_DYNAMIC_COMMENT_COUNT, 0)
//                    val isDel = data.getBooleanExtra(.INTENT_COMMENT_DYNAMIC_DEL, false)
//                    if (isDel) {
//                        dynamicArrayList.removeAt(mPositionComment)
//                        adapterDynamic.notifyDataSetChanged()
//                    } else {
//                        mStudyDynamicComment.setComment_num(commentCount)
//                        dynamicArrayList.set(mPositionComment, mStudyDynamicComment)
//                        //                    adapterDynamic.notifyItemChanged(mPositionComment, mStudyDynamicComment);
//                        if (mItemViewHolder != null) {
//                            mItemViewHolder.tvCommentCount.setText(java.lang.String.valueOf(mStudyDynamicComment.getComment_num()))
//                        }
//                    }
//                }
//            }
//        }
    }

    /**
     * 分享学友圈功能
     */
    private fun share(data: StudyDynamic?) {
        val commonBottomSharePop = CommonBottomSharePop(requireContext(), {
            if (it == CommonBottomSharePop.SHARE_TYPE_SCHOOL) {
                shareSchool(data)
            }
        }, false, true)
        XPopup.Builder(requireContext()).asCustom(commonBottomSharePop).show()
    }

    private fun shareSchool(data: StudyDynamic?) {
        val requestData = JSONObject()
        try {
            requestData.put("resource_type", ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC.toString())
            requestData.put("resource_id", data?.id.toString())
            requestData.put("other_data", data?.publish_img)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        model?.postSchoolShare(stringBody)


    }

    /**
     * 屏蔽功能
     */
    private fun stopStudyPlanDialog(dataBean: StudyDynamic?) {
        activity?.let { activity ->
            val message = if (dataBean?.publish_state == 1) {
                getString(R.string.study_plan_stop_message)
            } else {
                getString(R.string.study_plan_show_message)
            }
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = message
            publicDialogBean.strLeft = resources.getString(R.string.text_cancel)
            publicDialogBean.strRight = resources.getString(R.string.text_ok)
            publicDialogBean.isLeftGreen = 0
            XPopup.Builder(activity)
                    .asCustom(PublicDialog(activity, publicDialogBean) {
                        if (it == 1) {
                            postStopStudyPlan(dataBean)
                        }
                    })
                    .show()
        }
    }

    /**
     * 屏蔽接口
     */
    private fun postStopStudyPlan(dataBean: StudyDynamic?) {
        val requestData = JSONObject()

        try {
            requestData.put("plan_id", planId)
            requestData.put("activity_id", java.lang.String.valueOf(dataBean?.id))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        model?.postStopStudyPlan(stringBody)

    }

    /**
     * 举报
     */
    private fun report(data: StudyDynamic?) {
//        model?.getReportLearnData()

        val put = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_ID, data?.id ?: "")
                .put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC)

        ARouter.getInstance().build(Paths.PAGE_REPORT_DIALOG).with(put).navigation()

    }

    /**
     * 举报接口
     */
    private fun postReport(reportId: Int?, data: StudyDynamic?) {
        val requestData = JSONObject()
        try {
            requestData.put("report_reason_id", reportId?.toString())
            requestData.put("activity_id", data?.id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

        model?.postReportLearnData(stringBody)
        model?.reportDataResult?.observe(this, Observer {
            toast(it?.message)

        })
    }

    /**
     * 删除动态
     */
    private fun deleteDialog(dataBean: StudyDynamic?) {
        activity?.let { activity ->

            val publicDialogBean = PublicDialogBean()
            val message: String
            if (dataBean?.activity_checkin_type == 0) {
                message = resources.getString(R.string.study_del_sign_dynamic)
                val spannableString = SpannableString(message)
                spannableString.setSpan(ForegroundColorSpan(activity.getColorRes(R.color.color_2)), 0, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(activity.getColorRes(R.color.color_F8935D)), 26, message.length - 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(activity.getColorRes(R.color.color_2)), message.length - 11, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                publicDialogBean.strSpan = spannableString
                publicDialogBean.strLeft = getString(R.string.text_continue_del)
                publicDialogBean.isLeftGreen = 0
            } else {
                message = getString(R.string.del_message)
                publicDialogBean.strMsg = message
                publicDialogBean.strLeft = resources.getString(R.string.text_ok)
                publicDialogBean.isLeftGreen = 1
            }
            publicDialogBean.strRight = resources.getString(R.string.text_cancel)
            XPopup.Builder(activity)
                    .asCustom(PublicDialog(activity, publicDialogBean) {
                        if (it == 0) {
                            delDynamic(dataBean)
                        }
                    })
                    .show()
        }

    }

    private fun initView() {
        mRecyclerView?.layoutManager = LinearLayoutManager(activity)
        mStudyDynamicAdapter = StudyDynamicAdapter(null)
        mRecyclerView?.adapter = mStudyDynamicAdapter
        updateAdapterData()
    }

    private fun initData() {
        mStudyPlanData = mStudyPlanDetailBean?.study_plan
        planId = mStudyPlanDetailBean?.study_plan?.id
        getData()

    }

    fun getData() {
        if (dynamic.equals(param2)) {
            getStudyPlanDetailActivity()
        } else if (dynamicMy.equals(param2)) {
            getStudyPlanMyActivity()
        }
    }

    companion object {
        const val dynamic: String = "0"
        const val dynamicMy: String = "1"

        @JvmStatic
        fun newInstance(param1: StudyPlanDetailBean?, param2: String) =
                StudyDynamicFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    /**
     * 获取动态列表
     */
    fun getStudyPlanDetailActivity() {
        planId?.let { model?.getStudyPlanDetailActivity(it, limit, offset) }
    }

    /**
     * 获取我的动态
     */
    fun getStudyPlanMyActivity() {
        planId?.let { model?.getStudyPlanMyActivity(it, limit, offset) }
    }

    private fun getDataWithTitleActivity(sTitle: String) {
        title = sTitle
        titleOffSet = 0

        var punchstate = ""
        if (mStudyDynamicAdapter?.punch.equals("打卡成功")) {
            punchstate = "2"
        } else if (mStudyDynamicAdapter?.punch.equals("打卡失败")) {
            punchstate = "1"
        }

        val map = HashMap<String, String>()
        map["limit"] = "10"
        map["offset"] = titleOffSet.toString()
        map["resource_title"] = sTitle
        if (!TextUtils.isEmpty(punchstate)) {
            map["review_status"] = punchstate
        }
        if (dynamic.equals(param2)) {
            model?.getStudyPlanDetailWithTitle(planId.toString(), map)
        } else if (dynamicMy.equals(param2)) {
            model?.getStudyPlanMyActivity(planId.toString(), map)
        }


    }

    /**
     *数据
     */
    private fun setData(beans: ArrayList<StudyDynamic>?) {
        beans?.let {
            if (offset == 0) {
                if (beans.size < limit) {
                    mStudyDynamicAdapter?.setNewInstance(beans)
                    mStudyDynamicAdapter?.loadMoreModule?.isEnableLoadMore = false
                    mStudyDynamicAdapter?.loadMoreModule?.loadMoreComplete()
                } else {
                    mStudyDynamicAdapter?.setNewInstance(beans)
                    mStudyDynamicAdapter?.loadMoreModule?.isEnableLoadMore = true
                    mStudyDynamicAdapter?.loadMoreModule?.loadMoreComplete()

                }
            } else {
                if (beans.size < limit) {
                    mStudyDynamicAdapter?.addData(beans)
                    mStudyDynamicAdapter?.loadMoreModule?.isEnableLoadMore = false
                    mStudyDynamicAdapter?.loadMoreModule?.loadMoreEnd()

                } else {
                    mStudyDynamicAdapter?.addData(beans)
                    mStudyDynamicAdapter?.loadMoreModule?.isEnableLoadMore = true
                    mStudyDynamicAdapter?.loadMoreModule?.loadMoreComplete()
                }
            }
            mStudyDynamicAdapter?.addFooterView(getFootView())
        }

    }

    /**
     * 点赞功能
     */
    fun fill(bean: StudyDynamic?) {
        if (bean != null) {
            val requestData = JSONObject()
            try {
                requestData.put("like_type", "1")
                requestData.put("type_id", java.lang.String.valueOf(bean.id))
                requestData.put("user", GlobalsUserManager.uid)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

            model?.postFillStudyPlan(stringBody)

        }

    }

    /**
     * 删除动态
     */
    private fun delDynamic(dataBean: StudyDynamic?) {
        model?.delDynamic(dataBean?.id.toString())

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(value: StudyDynamicChangeEvent) {
        if (value.eventType == 0) {
            getData()
        }

    }

    /**
     * 自荐之后刷新我的动态页面，更改自荐按钮
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun selfRecommendEvent(value: StudyDynamic) {
        val beans: ArrayList<StudyDynamic>? = mStudyDynamicAdapter?.data as ArrayList<StudyDynamic>?
        var studyDynamic: StudyDynamic?
        var position = 0
        if (beans != null) {
            for (i in beans.indices) {
                studyDynamic = beans[i]
                if (value.id.isNotEmpty() && studyDynamic.id.isNotEmpty()) {
                    if (value.id == studyDynamic.id) {
                        position = i
                    }
                }
            }
            mStudyDynamicAdapter?.setData(position, value)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)

    }

}