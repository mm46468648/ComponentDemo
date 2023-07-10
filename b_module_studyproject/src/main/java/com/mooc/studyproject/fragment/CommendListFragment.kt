package com.mooc.studyproject.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.visiable
import com.mooc.common.utils.TimeUtils.getCurrentTime
import com.mooc.commonbusiness.adapter.DynamicImagesAdapter
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.MY_USER_ID
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.TYPE_FOLLOWUP_RESOURCE
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.pop.IncreaseScorePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.getStrUserName
import com.mooc.commonbusiness.widget.VoicePlayerController
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.CommentListAdapter
import com.mooc.studyproject.adapter.FollowUpVoiceDynamicAdapter
import com.mooc.studyproject.databinding.StudyprojectItemCommentTopDynamicBinding
import com.mooc.studyproject.model.ItemComment
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.studyproject.adapter.CommentListNewAdapter
import com.mooc.studyproject.ui.PublishingDynamicsCommentActivity.Companion.IS_PUBLISH_COMMENT
import com.mooc.studyproject.ui.PublishingDynamicsCommentActivity.Companion.PARAMS_COMMENTID_KEY
import com.mooc.studyproject.ui.PublishingDynamicsCommentActivity.Companion.PARAMS_COMMENTUSERID_KEY
import com.mooc.studyproject.ui.PublishingDynamicsCommentActivity.Companion.PARAMS_DYNAMICID_KEY
import com.mooc.studyproject.ui.PublishingDynamicsCommentActivity.Companion.STUDY_PLAN_ID
import com.mooc.studyproject.viewmodel.CommendListViewModel
import com.mooc.studyproject.viewmodel.StudyProjectViewModel
import com.mooc.studyproject.window.DynamicPublishSucPop
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *评论notes
 * @author limeng
 * @date 2020/12/29
 */
class CommendListFragment : BaseListFragment<ItemComment, CommendListViewModel>() {
    val model: StudyProjectViewModel by lazy {
        ViewModelProviders.of(this).get(StudyProjectViewModel::class.java);
    }
    var headBinding: StudyprojectItemCommentTopDynamicBinding? = null
    var mStudyPlanDetailBean: StudyPlanDetailBean? = null //学习相关数据
    var mIsInitiator = false//是否是发起人
    var mDynamicId = ""
    var isCanComment: Boolean = false

    val PARAMS_COMMENT_REQUESTCODE = 104 //评论页面请求码
    var isHeadFill = true


    override fun initAdapter(): BaseQuickAdapter<ItemComment, BaseViewHolder> {
        mViewModel?.id = mDynamicId
        mViewModel?.headBinding = headBinding
        val list = mViewModel?.getPageData()?.value
        if ((list?.size ?: 0) > 0) {
            headBinding?.llCommentDynamic?.setVisibility(View.VISIBLE)
            headBinding?.tvCommentTitleCount?.setText("${list?.size.toString()}条")
        } else {
            headBinding?.llCommentDynamic?.setVisibility(View.GONE)
        }
        return list.let {
//            val mCommendAdapter = CommentListAdapter(it)
            val mCommendAdapter = CommentListNewAdapter(it)
            mCommendAdapter.isCanComment = isCanComment
            mCommendAdapter.addChildClickViewIds(R.id.tvShied, R.id.tvReply, R.id.tvAgree, R.id.tvDel, R.id.ivHeader)
            mCommendAdapter.setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.tvShied -> {
                        commendShied(mCommendAdapter.data.get(position))
                    }
                    R.id.tvReply -> {
                        commedReplay(mCommendAdapter.data.get(position))
                    }
                    R.id.tvAgree -> {
                        commedAgree(mCommendAdapter.data.get(position))
                    }
                    R.id.tvDel -> {
                        commendDeleted(mCommendAdapter.data.get(position))
                    }
                    R.id.ivHeader -> {
                        tohead(mCommendAdapter.data.get(position))
                    }
                    else -> {
                    }
                }
            }
            mCommendAdapter
        }
    }

    /**
     * 点击头像跳转他人信息页面
     */
    private fun tohead(dataBean: ItemComment) {
        if (dataBean.is_from_cms == 1) {
            val commentUserBean = dataBean.from_cms_user_name
            if (commentUserBean?.id != 0
                    && !TextUtils.isEmpty(commentUserBean?.name) &&
                    !commentUserBean?.name.equals(context?.getResources()?.getString(R.string.text_str_initiator))) {
                ARouter.getInstance()
                        .build(Paths.PAGE_USER_INFO)
                        .withString(MY_USER_ID, commentUserBean?.id.toString())
                        .navigation()

            }
        } else {
            val commentUserBean = dataBean.comment_user
            if (commentUserBean?.id != 0) {
                ARouter.getInstance()
                        .build(Paths.PAGE_USER_INFO)
                        .withString(MY_USER_ID, commentUserBean?.id.toString())
                        .navigation()
            }
        }
    }

    private fun commendDeleted(data: ItemComment) {

        context?.let { context ->
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = resources.getString(R.string.del_message)
            publicDialogBean.strLeft = resources.getString(R.string.text_cancel)
            publicDialogBean.strRight = resources.getString(R.string.text_ok)
            publicDialogBean.isLeftGreen = 0
            XPopup.Builder(context)
                    .asCustom(PublicDialog(context, publicDialogBean) {
                        if (it == 1) {
                            delComment(data)
                        }
                    })
                    .show()
        }
    }

    private fun delComment(data: ItemComment) {
        model.delComment(data.id.toString())
        model.detlteCommendBean.observe(this, Observer {
            mAdapter?.remove(data)
            mAdapter?.notifyDataSetChanged()
        })
    }

    var itemComment: ItemComment? = null
    var itemDynamic: StudyDynamic? = null

    /**
     * 点赞
     */
    private fun commedAgree(data: ItemComment) {
        isHeadFill = false
        val requestData = JSONObject()
        try {
            requestData.put("like_type", "2")
            requestData.put("type_id", data.id.toString())
            requestData.put("user", GlobalsUserManager.uid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        itemComment = data
        model.postFillStudyPlan(stringBody)

    }

    private fun commedReplay(data: ItemComment) {
        // 跳转发布评论页面
        val planId = mStudyPlanDetailBean?.study_plan?.id
        val receiverUserId = if (data.is_from_cms == 1) data.from_cms_user_name?.id else data.comment_user?.id
        ARouter.getInstance().build(Paths.PAGE_PUBLISHINGDYNAMICSCOMMEN)
                .withBoolean(IS_PUBLISH_COMMENT, true)
                .withString(PARAMS_DYNAMICID_KEY, mDynamicId.toString())
                .withString(PARAMS_COMMENTID_KEY, data.id.toString())
                .withString(STUDY_PLAN_ID, planId)
                .withString(PARAMS_COMMENTUSERID_KEY, receiverUserId.toString())
                .navigation()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PARAMS_COMMENT_REQUESTCODE && resultCode == Activity.RESULT_OK) {
            loadDataWithRrefresh()

            val dynamicPublishSucPop = activity?.let { DynamicPublishSucPop(it, flRoot, true) }
            dynamicPublishSucPop?.show()
        }
    }

    /**
     * 隐藏评论
     */
    private fun commendShied(data: ItemComment) {
        context?.let { context ->
            val message = if (data.comment_status == 1) {
                getString(R.string.comment_stop_message)
            } else {
                getString(R.string.comment_show_message)
            }
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = message
            publicDialogBean.strLeft = resources.getString(R.string.text_cancel)
            publicDialogBean.strRight = resources.getString(R.string.text_ok)
            publicDialogBean.isLeftGreen = 0
            XPopup.Builder(context)
                    .asCustom(PublicDialog(context, publicDialogBean) {
                        if (it == 1) {
                            shieldComment(data)
                        }
                    })
                    .show()
        }
    }

    private fun shieldComment(data: ItemComment) {
        val requestData = JSONObject()
        try {
            requestData.put("comment_id", data.id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        model.postStopCommentStudyPlan(stringBody)
        model.stopCpmmendBean.observe(this, Observer {
            toast(it?.message?.detail)
            val code = it?.code
            if (code == "200") {
                data.comment_status = 0
            } else if (code == "201") {
                data.comment_status = 1
            }
            mAdapter?.notifyDataSetChanged()

        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headBinding = DataBindingUtil.inflate<StudyprojectItemCommentTopDynamicBinding>(LayoutInflater.from(activity), R.layout.studyproject_item_comment_top_dynamic, null, false)
        headBinding?.root?.let { mAdapter?.addHeaderView(it) }
        initData()
        initDataListener()


    }

    private fun initDataListener() {
        mViewModel?.studyDynamicDetails?.observe(viewLifecycleOwner, Observer {
            headBinding?.tvCommentCount?.text = java.lang.String.valueOf(it?.comment_num)
            headBinding?.tvFillCount?.text = java.lang.String.valueOf(it?.like_num)
        })
        model.delDynamicBean.observe(viewLifecycleOwner, Observer {
            if (it?.code.equals("1")) {
                activity?.finish()
            }
        })
        model.postFillStudyPlanBean.observe(viewLifecycleOwner, Observer {
            if (isHeadFill) {
                toast(it?.message?.detail)
                if (it?.code.equals("200")) {
                    if (itemDynamic?.like_num != null) {
                        itemDynamic?.like_num = (itemDynamic?.like_num ?: 0) + 1

                    }
                    itemDynamic?.is_like = true
                } else if (it?.code.equals("201")) {
                    if (itemDynamic?.like_num != null) {
                        if ((itemDynamic?.like_num ?: 0) >= 1) {
                            itemDynamic?.like_num = (itemDynamic?.like_num ?: 0) - 1
                        }
                    }

                    itemDynamic?.is_like = false

                }
                headBinding?.tvFillCount?.setText(itemDynamic?.like_num.toString())
                if (itemDynamic?.is_like == true) {
                    headBinding?.ivFill?.setBackgroundResource(R.mipmap.common_icon_red_like)
                } else {
                    headBinding?.ivFill?.setBackgroundResource(R.mipmap.common_iv_plan_fill)
                }
                mAdapter?.notifyDataSetChanged()

            } else {
                val code = it?.code
                val msg = it?.message?.detail
                toast(msg)

                if (code == "200") {
                    if (itemComment?.like_num != null) {
                        itemComment?.like_num = (itemComment?.like_num ?: 0) + 1
                    }
                    itemComment?.is_like = true
                } else if (code == "201") {
                    if ((itemComment?.like_num ?: 0) >= 1) {
                        if (itemComment?.like_num != null) {
                            itemComment?.like_num = (itemComment?.like_num ?: 0) - 1
                        }
                        itemComment?.is_like = false
                    }
                }
                mAdapter?.notifyDataSetChanged()
            }
        })
        //设置head 数据
        model.studyDynamicDetails.observe(viewLifecycleOwner, Observer {
            initHead(it)

        })
    }

    private fun initData() {
        getSynamicDetials()

    }

    private fun getSynamicDetials() {
        model.getCommentDynamic(mDynamicId.toString())

    }

    private fun initHead(dataBean: StudyDynamic?) {
        val user = dataBean?.user
        itemDynamic = dataBean
        if (user != null) {
            val name: String
            if (dataBean.is_studyplan_start_user) {
                name = "发起人(${user.name?.let { getStrUserName(it) }})"
            } else {
                name = user.name?.let { getStrUserName(it) }.toString()
            }
            headBinding?.tvName?.setText(name)
            activity?.let {
                headBinding?.ivUser?.let { it1 ->
                    headBinding?.ivUser?.visiable(true)
                    headBinding?.ivUser?.setHeadImage(user.avatar, user.avatar_identity)
                }
            }

        }
        if (dataBean?.activity_checkin_type == 0) { //打卡动态
            headBinding?.tvSignDynamic?.setVisibility(View.VISIBLE)
        } else {
            headBinding?.tvSignDynamic?.setVisibility(View.GONE)
        }
        if (dataBean?.is_top == 0) {
            headBinding?.tvDynamicTop?.setVisibility(View.GONE)
        } else {
            headBinding?.tvDynamicTop?.setVisibility(View.VISIBLE)
        }
        if (mStudyPlanDetailBean != null) {
            val mStudyPlanData = mStudyPlanDetailBean?.study_plan
            if (mStudyPlanDetailBean?.is_join == 1) {
                val userBean = GlobalsUserManager.userInfo
                if (userBean != null && user != null) {
                    if (userBean.id.equals(user.id.toString())) {
                        headBinding?.llShare?.setVisibility(View.VISIBLE)
                        headBinding?.llReport?.setVisibility(View.GONE)
                    } else {
                        headBinding?.llShare?.setVisibility(View.GONE)
                        headBinding?.llReport?.setVisibility(View.VISIBLE)
                    }
                } else {
                    headBinding?.llShare?.setVisibility(View.GONE)
                    headBinding?.llReport?.setVisibility(View.GONE)
                }
                if (mIsInitiator) {
                    if (dataBean?.publish_state == 0) {
                        headBinding?.tvStopDynamic?.setText(activity?.getResources()?.getString(R.string.study_plan_cancel_stop))
                    } else {
                        headBinding?.tvStopDynamic?.setText(activity?.getResources()?.getString(R.string.study_plan_stop))
                    }
                    headBinding?.llStop?.setVisibility(View.VISIBLE)
                } else {
                    headBinding?.llStop?.setVisibility(View.GONE)
                }
                if (dataBean?.is_activity_user == 1) { //动态发起人
                    if (dataBean.is_time_out == 0) { //计划已结束
                        if (dataBean.activity_checkin_type == 0) { //打卡动态
                            headBinding?.llDel?.setVisibility(View.GONE)
                        } else {
                            headBinding?.llDel?.setVisibility(View.VISIBLE)
                        }
                    } else {
                        headBinding?.llDel?.setVisibility(View.VISIBLE)
                    }
                } else {
                    headBinding?.llDel?.setVisibility(View.GONE)
                }
                if (mStudyPlanData != null) {
                    if (mStudyPlanData.comment_like_status == 0) {
                        headBinding?.llComment?.setVisibility(View.VISIBLE)
                        headBinding?.llFill?.setVisibility(View.VISIBLE)
                    } else if (mStudyPlanData.comment_like_status == 1) {
                        if (mIsInitiator) {
                            if (isUnStartOrStop(getCurrentTime(), mStudyPlanData.plan_starttime, mStudyPlanData.plan_endtime) != 0) {
                                headBinding?.llComment?.setVisibility(View.VISIBLE)
                                headBinding?.llFill?.setVisibility(View.VISIBLE)
                            } else {
                                headBinding?.llComment?.setVisibility(View.GONE)
                                headBinding?.llFill?.setVisibility(View.GONE)
                            }
                        } else {
                            val time: Long = mStudyPlanData.set_time
                            if (isCanOperate(time)) {
                                headBinding?.llComment?.setVisibility(View.VISIBLE)
                                headBinding?.llFill?.setVisibility(View.VISIBLE)
                            } else {
                                headBinding?.llComment?.setVisibility(View.GONE)
                                headBinding?.llFill?.setVisibility(View.GONE)
                            }
                        }
                    } else if (mStudyPlanData.comment_like_status == 3) {
                        if (mIsInitiator) {
                            if (isUnStartOrStop(getCurrentTime(), mStudyPlanData.plan_starttime, mStudyPlanData.plan_endtime) != 0) {
                                headBinding?.llComment?.setVisibility(View.VISIBLE)
                                headBinding?.llFill?.setVisibility(View.VISIBLE)
                            } else {
                                headBinding?.llComment?.setVisibility(View.GONE)
                                headBinding?.llFill?.setVisibility(View.GONE)
                            }
                        } else {
                            headBinding?.llComment?.setVisibility(View.GONE)
                            headBinding?.llFill?.setVisibility(View.GONE)
                        }
                    }
                } else {
                    headBinding?.llComment?.setVisibility(View.VISIBLE)
                    headBinding?.llFill?.setVisibility(View.VISIBLE)
                }
            } else {
                headBinding?.llShare?.setVisibility(View.GONE)
                headBinding?.llReport?.setVisibility(View.GONE)
                headBinding?.llStop?.setVisibility(View.GONE)
                headBinding?.llDel?.setVisibility(View.GONE)
                if (mStudyPlanData != null) {
                    if (mStudyPlanData.comment_like_status == 0) {
                        headBinding?.llComment?.setVisibility(View.VISIBLE)
                    } else if (mStudyPlanData.comment_like_status == 1) {
                        if (mIsInitiator) {
                            if (isUnStartOrStop(getCurrentTime(), mStudyPlanData.plan_starttime, mStudyPlanData.plan_endtime) != 0) {
                                headBinding?.llComment?.setVisibility(View.VISIBLE)
                            } else {
                                headBinding?.llComment?.setVisibility(View.GONE)
                            }
                        } else {
                            val time: Long = mStudyPlanData.set_time
                            if (isCanOperate(time)) {
                                headBinding?.llComment?.setVisibility(View.VISIBLE)
                            } else {
                                headBinding?.llComment?.setVisibility(View.GONE)
                            }
                        }
                    } else if (mStudyPlanData.comment_like_status == 3) {
                        if (mIsInitiator) {
                            if (isUnStartOrStop(getCurrentTime(), mStudyPlanData.plan_starttime, mStudyPlanData.plan_endtime) != 0) {
                                headBinding?.llComment?.setVisibility(View.VISIBLE)
                            } else {
                                headBinding?.llComment?.setVisibility(View.GONE)
                            }
                        } else {
                            headBinding?.llComment?.setVisibility(View.GONE)
                        }
                    }
                } else {
                    headBinding?.llComment?.setVisibility(View.VISIBLE)
                }
                headBinding?.llFill?.setVisibility(View.GONE)
            }
        } else {
            val userBean = GlobalsUserManager.userInfo
            if (userBean != null && user != null) {
                if (userBean.id.equals(java.lang.String.valueOf(user.id))) {
                    headBinding?.llShare?.setVisibility(View.VISIBLE)
                    headBinding?.llReport?.setVisibility(View.GONE)
                } else {
                    headBinding?.llShare?.setVisibility(View.GONE)
                    headBinding?.llReport?.setVisibility(View.VISIBLE)
                }
            } else {
                headBinding?.llShare?.setVisibility(View.GONE)
                headBinding?.llReport?.setVisibility(View.GONE)
            }
            if (dataBean?.is_activity_user == 1) { //动态发起人
                if (dataBean.is_time_out == 0) { //计划已结束
                    if (dataBean.activity_checkin_type == 0) { //打卡动态
                        headBinding?.llDel?.setVisibility(View.GONE)
                    } else {
                        headBinding?.llDel?.setVisibility(View.VISIBLE)
                    }
                } else {
                    headBinding?.llDel?.setVisibility(View.VISIBLE)
                }
            } else {
                headBinding?.llDel?.setVisibility(View.GONE)
            }
            if (mIsInitiator) {
                if (dataBean?.publish_state == 0) {
                    headBinding?.tvStopDynamic?.setText(activity?.getResources()?.getString(R.string.study_plan_cancel_stop))
                } else {
                    headBinding?.tvStopDynamic?.setText(activity?.getResources()?.getString(R.string.study_plan_stop))
                }
                headBinding?.llStop?.setVisibility(View.VISIBLE)
            } else {
                headBinding?.llStop?.setVisibility(View.GONE)
            }

            headBinding?.llComment?.setVisibility(View.GONE)
            headBinding?.llFill?.setVisibility(View.VISIBLE)

        }
        if (!TextUtils.isEmpty(dataBean?.publish_time)) {
            var time = dataBean?.publish_time
            val str = time?.split(" ".toRegex())?.toTypedArray()
            val date = str?.get(0)
            val dates = date?.split("-".toRegex())?.toTypedArray()
            val t = str?.get(1)
            val ts = t?.split(":".toRegex())?.toTypedArray()
            var m = dates?.get(1)
            if ((m?.toInt() ?: 0) <= 9) {
                val monStrings = m?.split("0".toRegex())?.toTypedArray()
                m = monStrings?.get(1)
            }
            time = m + "月" + (dates?.get(2)) + "日 " + (ts?.get(0)) + ":" + (ts?.get(1))
            headBinding?.tvTime?.setText(time)
        }
        if (dataBean?.activity_type == 0) {
            headBinding?.tvDetail?.setVisibility(View.VISIBLE)
            if (dataBean.activity_checkin_type == 0) { //打卡动态
                headBinding?.tvDetail?.let { setTvSpan(it, dataBean) }
            } else {
                headBinding?.tvDetail?.setText(dataBean.publish_content)
            }
            headBinding?.viewVoice?.setVisibility(View.GONE)
            if (dataBean.publish_img_list != null && (dataBean.publish_img_list?.size ?: 0) > 0) {
                if ((dataBean.publish_img_list?.size ?: 0) > 0) {
                    headBinding?.imgRcy?.setVisibility(View.VISIBLE)
                    //                            headBinding?.ivDynamic.setVisibility(View.GONE);
                    val adapter = DynamicImagesAdapter(dataBean.publish_img_list)
                    val manager = GridLayoutManager(activity, 3)
                    headBinding?.imgRcy?.setLayoutManager(manager)
                    headBinding?.imgRcy?.setAdapter(adapter)
                    headBinding?.imgRcy?.setNestedScrollingEnabled(false)
                }
            } else {
                headBinding?.imgRcy?.setVisibility(View.GONE)
                //                        headBinding?.ivDynamic.setVisibility(View.GONE);
            }
        } else {
            //添加语音控件
            if (dataBean?.activity_checkin_type == 0) { //打卡动态
                headBinding?.tvDetail?.setVisibility(View.VISIBLE)
                headBinding?.tvDetail?.let { setTvSpan(it, dataBean) }
            } else {
                headBinding?.tvDetail?.setVisibility(View.GONE)
            }
            //语音动态又分为了 跟读语音和普通语音，跟读类型需显示，一整套跟读语音列表
            if (dataBean?.source_type_id == TYPE_FOLLOWUP_RESOURCE) {
                showFollowupTypeSpecialLayout(dataBean)
            } else {
                headBinding?.imgRcy?.setVisibility(View.GONE)
                //                    headBinding?.ivDynamic.setVisibility(View.GONE);
                headBinding?.viewVoice?.setVisibility(View.VISIBLE)
                //                    String time = dataBean.getActivity_content_long();
                (headBinding?.viewVoice as VoicePlayerController).setPlayPath(dataBean?.publish_content)
                (headBinding?.viewVoice as VoicePlayerController).setTotleTimeLength(dataBean?.activity_content_long)
            }
        }
        headBinding?.tvCommentCount?.setText(java.lang.String.valueOf(dataBean?.comment_num))
        headBinding?.tvFillCount?.setText(java.lang.String.valueOf(dataBean?.like_num))
        if (dataBean?.is_like == true) {
            headBinding?.ivFill?.setBackgroundResource(R.mipmap.common_icon_red_like)
        } else {
            headBinding?.ivFill?.setBackgroundResource(R.mipmap.common_iv_plan_fill)
        }
        //点赞
        headBinding?.llFill?.setOnClickListener(View.OnClickListener {
            fill(dataBean)
        })
        //屏蔽
        headBinding?.llStop?.setOnClickListener(View.OnClickListener {
            stopStudyPlanDialog(dataBean)

        })
        //删除
        headBinding?.llDel?.setOnClickListener(View.OnClickListener {
            deleteDialog(dataBean)
        })
        //分享
        headBinding?.llShare?.setOnClickListener(View.OnClickListener {
            share(dataBean)
        })
        //举报
        headBinding?.llReport?.setOnClickListener(View.OnClickListener {
            report(dataBean)
        })
        headBinding?.tvDetail?.setOnLongClickListener(OnLongClickListener {
//            val txt: String = headBinding?.tvDetail?.getText().toString().trim({ it <= ' ' })
//            val cm = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            cm.text = if (TextUtils.isEmpty(txt)) "" else txt
            toast(getString(R.string.dynamic_nocopy))
            false
        })
        headBinding?.llUser?.setOnClickListener(View.OnClickListener {
            if (user != null) {
                ARouter.getInstance().build(Paths.PAGE_USER_INFO)
                        .withString(MY_USER_ID, user.id.toString())
                        .navigation()

            }
        })


    }

    /**
     * 删除动态
     */
    @Suppress("DEPRECATION")
    private fun deleteDialog(dataBean: StudyDynamic?) {
        context?.let { context ->

            val publicDialogBean = PublicDialogBean()
            val message: String
            if (dataBean?.activity_checkin_type == 0) {
                message = getString(R.string.study_del_sign_dynamic)
                val spannableString = SpannableString(message)
                spannableString.setSpan(context.resources?.getColor(R.color.color_2)?.let { ForegroundColorSpan(it) }, 0, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(context.resources?.getColor(R.color.color_F8935D)?.let { ForegroundColorSpan(it) }, 26, message.length - 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(context.resources?.getColor(R.color.color_2)?.let { ForegroundColorSpan(it) }, message.length - 11, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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
            XPopup.Builder(context)
                    .asCustom(PublicDialog(context, publicDialogBean) {
                        if (it == 0) {
                            delDynamic(dataBean)
                        }
                    })
                    .show()
        }
    }

    private fun delDynamic(dataBean: StudyDynamic?) {
        /**
         * 删除动态
         */
        model.delDynamic(dataBean?.id.toString())

    }

    /**
     * 屏蔽功能
     */
    private fun stopStudyPlanDialog(dataBean: StudyDynamic?) {
        context?.let { context ->
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
            XPopup.Builder(context)
                    .asCustom(PublicDialog(context, publicDialogBean) {
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
            requestData.put("plan_id", dataBean?.study_plan.toString())
            requestData.put("activity_id", dataBean?.id.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        model.postStopStudyPlan(stringBody)
        model.stopStudyPlan.observe(viewLifecycleOwner, Observer {
            toast(it?.message?.detail)
            val code = it?.code
            if (code == "200") { //用户屏蔽成功
                dataBean?.publish_state = 0
            } else if (code == "201") {
                dataBean?.publish_state = 1
            }
            if (dataBean?.publish_state == 0) {
                headBinding?.tvStopDynamic?.text = activity?.resources?.getString(R.string.study_plan_cancel_stop)
            } else {
                headBinding?.tvStopDynamic?.text = activity?.resources?.getString(R.string.study_plan_stop)
            }
        })
    }

    private fun fill(dataBean: StudyDynamic?) {
        val requestData = JSONObject()
        try {
            requestData.put("like_type", "1")
            requestData.put("type_id", dataBean?.id.toString())
            requestData.put("user", GlobalsUserManager.uid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        isHeadFill = true
        model.postFillStudyPlan(stringBody)

    }

    private fun isUnStartOrStop(time: Long, startTime: Long, stopTime: Long): Int {
        return if (time < startTime) { //尚未开始
            0
        } else if (time <= stopTime) { //可以加入计划
            1
        } else { //计划结束
            2
        }
    }

    private fun isCanOperate(time: Long): Boolean {
//        return time > 0 && DateUtil.getCurrentTime() <= time * 1000;
        return time <= 0 || getCurrentTime() <= time * 1000
    }

    private fun setTvSpan(tv: TextView, dataBean: StudyDynamic) {
        val spannableString: SpannableString
        var title = ""
        var detail = ""
        if (!TextUtils.isEmpty(dataBean.source_title)) {
            title = dataBean.source_title.toString()
        }
        if (!TextUtils.isEmpty(dataBean.publish_content)) {
            if (dataBean.activity_type == 0) {
                detail = dataBean.publish_content.toString()
            }
        }
        var str = ""
        if (!TextUtils.isEmpty(title)) {
            tv.highlightColor = Color.TRANSPARENT
            if (!TextUtils.isEmpty(detail)) {
                str = "#$title#$detail"
                spannableString = SpannableString(str)
                spannableString.setSpan(activity?.getResources()?.getColor(R.color.color_4A90E2)?.let { ForegroundColorSpan(it) }, 0, title.length + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(activity?.getResources()?.getColor(R.color.color_6)?.let { ForegroundColorSpan(it) }, title.length + 2, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv.text = spannableString
                tv.movementMethod = LinkMovementMethod.getInstance()
            } else {
                str = "#$title#"
                spannableString = SpannableString(str)
                spannableString.setSpan(activity?.getResources()?.getColor(R.color.color_4A90E2)?.let { ForegroundColorSpan(it) }, 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv.text = spannableString
                tv.movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            if (!TextUtils.isEmpty(detail)) {
                str = detail
                spannableString = SpannableString(str)
                spannableString.setSpan(activity?.getResources()?.getColor(R.color.color_6)?.let { ForegroundColorSpan(it) }, 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv.text = spannableString
            }
        }

    }

    private fun getTvFollowupDrawableRight(): Drawable? {
        val equals = "展开" == headBinding?.tvFollowUpAudioExpand?.getText().toString()
        val drawRight: Int = if (equals) R.mipmap.common_ic_arrow_down_blue else R.mipmap.studyproject_ic_arrow_up_blue
        val drawable = activity?.resources?.let { ResourcesCompat.getDrawable(it, drawRight, null) }
        drawable?.minimumWidth?.let { drawable.setBounds(0, 0, it, drawable.minimumHeight) }
        return drawable
    }

    fun showFollowupTypeSpecialLayout(dataBean: StudyDynamic) {
        //跟读打卡动态只显示跟读专门样式
        val layout = LinearLayoutManager(activity)
        headBinding?.imgRcy?.setLayoutManager(layout)
        val repeat_record = dataBean.extra_info?.repeat_record
        if (repeat_record != null) {
            val followUpVoiceDynamicAdapter = FollowUpVoiceDynamicAdapter(repeat_record)

//            boolean expandTvVisibale = headBinding?.tvFollowUpAudioExpand.getVisibility() == View.GONE;
            if (repeat_record.size > 4) {   //如果超过6条，显示4条，并且显示展开view
                headBinding?.tvFollowUpAudioExpand?.text = "展开"
                headBinding?.tvFollowUpAudioExpand?.visibility = View.VISIBLE
                headBinding?.tvFollowUpAudioExpand?.setCompoundDrawables(null, null, getTvFollowupDrawableRight(), null)
                //                followUpVoiceDynamicAdapter.setExpand(false);
                val subList = ArrayList<StudyDynamic.RepeatRecordBean>()
                for (i in 0..3) {   //截取前4个音频
                    subList.add(repeat_record[i])
                }
                followUpVoiceDynamicAdapter.setNewData(subList)
                headBinding?.tvFollowUpAudioExpand?.setOnClickListener {
                    val equals = "展开" == headBinding?.tvFollowUpAudioExpand?.text.toString()
                    val expandStr = if (equals) "收起" else "展开"
                    if (equals) {
                        followUpVoiceDynamicAdapter.setNewData(repeat_record)
                    } else {
                        followUpVoiceDynamicAdapter.setNewData(subList)
                    }
                    headBinding?.tvFollowUpAudioExpand?.text = expandStr
                    val tvFollowupDrawableRight = getTvFollowupDrawableRight()
                    headBinding?.tvFollowUpAudioExpand?.setCompoundDrawables(null, null, tvFollowupDrawableRight, null)
                }
            } else {
                followUpVoiceDynamicAdapter.setNewData(repeat_record)
                headBinding?.tvFollowUpAudioExpand?.visibility = View.GONE
            }
            headBinding?.imgRcy?.adapter = followUpVoiceDynamicAdapter
            headBinding?.imgRcy?.visibility = View.VISIBLE
        } else {
            headBinding?.tvFollowUpAudioExpand?.visibility = View.GONE
            headBinding?.imgRcy?.visibility = View.GONE
        }
    }

    /**
     * 分享学友圈功能
     */
    private fun share(data: StudyDynamic?) {
        /**
         * 分享学友圈功能
         */
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
        model.postSchoolShare(stringBody)
        model.shareSchoolCircleBean.observe(viewLifecycleOwner, {
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

    }

    /**
     * 举报
     */
    private fun report(data: StudyDynamic?) {
//        model.getReportLearnData()
//        model.reportLearnBean.observe(this, Observer {

//            val reportLearnPopWindow = ReportLearnPopWindow(context, view)
//            reportLearnPopWindow.results = it?.results
//            reportLearnPopWindow.show()
//            reportLearnPopWindow.postReportListener = {
//                postReport(it, data)
//            }
//        })

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

        model.postReportLearnData(stringBody)
        model.reportDataResult.observe(this, Observer {
            toast(it?.message)

        })
    }
}

