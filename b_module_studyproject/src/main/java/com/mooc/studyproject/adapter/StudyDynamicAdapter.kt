package com.mooc.studyproject.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.DateStyle
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.adapter.DynamicImagesAdapter
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.TYPE_FOLLOWUP_RESOURCE
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.model.studyproject.StudyPlan
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.getStrUserName
import com.mooc.commonbusiness.widget.HeadView
import com.mooc.commonbusiness.widget.VoicePlayerController
import com.mooc.resource.utils.PixUtil.Companion.dp2px
import com.mooc.resource.widget.expandabletextview.ExpandableTextView
import com.mooc.studyproject.R
import java.util.*
import kotlin.collections.ArrayList

/**

 * @Author limeng
 * @Date 2020/12/15-5:35 PM
 */
class StudyDynamicAdapter(data: ArrayList<StudyDynamic>?, layoutId: Int = R.layout.studyproject_item_dynamic_plan_study) : BaseQuickAdapter<StudyDynamic, BaseViewHolder>(layoutId, data), LoadMoreModule {
    companion object {
        val TYPE_DYNAMIC = 1
        val TYPE_DYNAMIC_FOLLOWUP = 2  //跟读打卡动态
    }


    var mStudyPlanData: StudyPlan? = null
    var onDyniaSucStrClick: ((isSuc: Boolean) -> Unit)? = null
    var onTitleDelClick: (() -> Unit)? = null
    var onTitleClick: ((title: String, isLoadMore: Boolean) -> Unit)? = null
    var title = ""
    var punch = ""
    var isSelf = false //是否是我的动态
    var mIsInitiator = false
    var isCanSendComment = false
    var mJoin = 0

    override fun convert(holder: BaseViewHolder, item: StudyDynamic) {
        var itemType = if (item.source_type_id == ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE) 2 else 1
        if (itemType == TYPE_DYNAMIC) {
            setItemView(holder, item)
        } else {
            setFollowupItemView(holder, item)
        }

    }

    private fun setFollowupItemView(holder: BaseViewHolder, dataBean: StudyDynamic) {

        //筛选标签相关
        if (holder.layoutPosition == 0) {
            if (!TextUtils.isEmpty(title)) {
                holder.setGone(R.id.llTitle, false)
                holder.setGone(R.id.titleDelTv, false)
                holder.setText(R.id.titleDelTv, title)
            } else {
                holder.setGone(R.id.titleDelTv, true)
            }
            if (!TextUtils.isEmpty(punch)) {
                holder.setGone(R.id.llTitle, false)
                holder.setGone(R.id.punchTv, false)
                holder.setText(R.id.punchTv, punch)

            } else {
                holder.setGone(R.id.punchTv, true)
            }
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(punch)) {
                holder.setGone(R.id.llTitle, true)

            }
        } else {
            holder.setGone(R.id.llTitle, true)
        }

        //是否是我的动态
        if (isSelf) {
            holder.setGone(R.id.ivAuditStatus, false)
            holder.setGone(R.id.flHead, true)
            holder.setGone(R.id.tvOwnTime, false)
            holder.setGone(R.id.tvName, true)
            holder.setGone(R.id.tvTime, true)
            holder.setGone(R.id.tvSelfRecommend, false)
            var b = holder.getView<LinearLayout>(R.id.llSign).getLayoutParams() as RelativeLayout.LayoutParams;
            b.rightMargin = dp2px(74.toFloat()).toInt()
            val auditStatus: Int = dataBean.review_status
            if (auditStatus == 0) {
                holder.setBackgroundResource(R.id.ivAuditStatus, R.mipmap.studyproject_iv_audit_progress)
            } else if (auditStatus == 1) {
                holder.setBackgroundResource(R.id.ivAuditStatus, R.mipmap.studyproject_iv_audit_failed)

            } else if (auditStatus == 2) {
                holder.setBackgroundResource(R.id.ivAuditStatus, R.mipmap.studyproject_iv_audit_approval)

            }
        } else {
            holder.setGone(R.id.ivAuditStatus, true)
            holder.setGone(R.id.flHead, false)
            holder.setGone(R.id.tvOwnTime, true)
            holder.setGone(R.id.tvName, false)
            holder.setGone(R.id.tvTime, false)
            holder.setGone(R.id.ivUser, false)
            holder.setGone(R.id.tvSelfRecommend, true)
        }

        //设置用户信息
        val user = dataBean.user
        if (user != null) {
            val name: String? = if (dataBean.is_studyplan_start_user) java.lang.String.format(context.getResources().getString(R.string.text_initiator), user.name?.let { getStrUserName(it) }) else user.name?.let { getStrUserName(it) }
            holder.setText(R.id.tvName, name)
            val headView = holder.getView<HeadView>(R.id.ivUser)
            headView.setHeadImage(user.avatar, user.avatar_identity)
//           Glide.with(context)
//                    .load(user.avatar)
//                    .circleCrop()
//                    .into(holder.getView<ImageView>(R.id.ivUser))
        }

        //设置点赞
        holder.setText(R.id.tvCommentCount, dataBean.comment_num.toString())
        holder.setText(R.id.tvFillCount, dataBean.like_num.toString())
        val isLikeImageRes: Int = if (dataBean?.is_like == true) R.mipmap.common_icon_red_like else R.mipmap.common_iv_plan_fill
        holder.setBackgroundResource(R.id.ivFill, isLikeImageRes)

        //设置时间
        if (!TextUtils.isEmpty(dataBean.publish_time)) {
            var time = dataBean.publish_time
            val str = time?.split(" ")?.toTypedArray()
            val date = str?.get(0)
            val dates = date?.split("-")?.toTypedArray()
            val t = str?.get(1)
            val ts = t?.split(":")?.toTypedArray()
            var m = dates?.get(1)
            if (m?.toInt() ?: 0 <= 9) {
                val monStrings = m?.split("0")?.toTypedArray()
                m = monStrings?.get(1)
            }
            time = m + "月" + (dates?.get(2) ?: "") + "日 " + (ts?.get(0) ?: "") + ":" + (ts?.get(1)
                    ?: "")
            holder.setText(R.id.tvTime, time)
            holder.setText(R.id.tvOwnTime, time)
        }
        //设置是否定时显示
        if (isHideDynamic(dataBean)) {
            setActivityTime(holder, dataBean) //时间activity_resource_time判断
            return
        }

        //设置动态的内容
        holder.setGone(R.id.tvActivityTime, true)
        holder.setGone(R.id.llBottom, false)

        if (dataBean.activity_checkin_type == 0) { //打卡动态
            holder.setGone(R.id.tvSignDynamic, false)
        } else {
            holder.setGone(R.id.tvSignDynamic, true)

        }
        if (dataBean.is_top == 0) {    //是否置顶
            holder.setGone(R.id.tvDynamicTop, true)
        } else {
            holder.setGone(R.id.tvDynamicTop, false)

        }
        if (TextUtils.isEmpty(dataBean.display_tag)) {
            if (!TextUtils.isEmpty(dataBean.studyplan_display_tag)) {
                holder.setGone(R.id.tv_highquality_dynamic, false)
                holder.setText(R.id.tv_highquality_dynamic, dataBean.studyplan_display_tag)
            } else {
                holder.setGone(R.id.tv_highquality_dynamic, true)
                holder.setText(R.id.tv_highquality_dynamic, "")
            }
        } else {
            holder.setGone(R.id.tv_highquality_dynamic, false)
            holder.setText(R.id.tv_highquality_dynamic, dataBean.display_tag)

        }
        val tvHighqualityDynamicVisible = if (holder.getView<TextView>(R.id.tv_highquality_dynamic).visibility == View.VISIBLE && isSelf) View.VISIBLE else View.GONE
        holder.getView<TextView>(R.id.tv_highquality_dynamic).visibility = tvHighqualityDynamicVisible

        if (mJoin == 1) {
            val userBean = GlobalsUserManager.userInfo
            if (userBean != null && user != null) {
                if (userBean.id == java.lang.String.valueOf(user.id)) {
                    if (isSelf) {//我的动态，审核通过的显示分享按钮
                        if (dataBean.review_status == 2) {
                            holder.setGone(R.id.llShare, false)
                        } else {
                            holder.setGone(R.id.llShare, true)
                        }
                    } else {
                        holder.setGone(R.id.llShare, false)
                    }
                    holder.setGone(R.id.llReport, true)
                } else {
                    holder.setGone(R.id.llShare, true)
                    holder.setGone(R.id.llReport, false)

                }
            } else {
                holder.setGone(R.id.llShare, true)
                holder.setGone(R.id.llReport, true)
            }
            if (mIsInitiator) {
                if (dataBean.publish_state == 0) {
                    holder.setText(R.id.tvStopDynamic, context.getResources().getString(R.string.study_plan_cancel_stop))
                } else {
                    holder.setText(R.id.tvStopDynamic, context.getResources().getString(R.string.study_plan_stop))
                }
                holder.setGone(R.id.llStop, false)

            } else {
                holder.setGone(R.id.llStop, true)

            }

            if (dataBean.is_activity_user == 1) {//动态发起人
                if (dataBean.is_time_out == 0) {//计划已结束
                    if (dataBean.activity_checkin_type == 0) {//打卡动态
                        holder.setGone(R.id.llDel, true)
                    } else {
                        holder.setGone(R.id.llDel, false)
                    }
                } else {
                    holder.setGone(R.id.llDel, false)
                }
            } else {
                holder.setGone(R.id.llDel, true)

            }


            if (mStudyPlanData != null) {
                if (mStudyPlanData?.comment_like_status == 0) {
                    holder.setGone(R.id.llComment, false)
                    isCanSendComment = true
                    holder.setGone(R.id.llFill, false)
                } else if (mStudyPlanData?.comment_like_status == 1) {
                    if (mIsInitiator) {
                        if (mStudyPlanData?.plan_starttime?.let { mStudyPlanData?.plan_endtime?.let { it1 -> isUnStartOrStop(DateUtil.getCurrentTime(), it, it1) } } != 0) {
                            holder.setGone(R.id.llComment, false)
                            isCanSendComment = true
                            holder.setGone(R.id.llFill, false)
                        } else {
                            holder.setGone(R.id.llComment, false)
                            isCanSendComment = false
                            holder.setGone(R.id.llFill, true)

                        }
                    } else {
                        val time = mStudyPlanData?.set_time
                        if (time?.let { isCanOperate(it) } == true) {
                            holder.setGone(R.id.llComment, false)
                            isCanSendComment = true
                            holder.setGone(R.id.llFill, false)
                        } else {
                            holder.setGone(R.id.llComment, false)
                            isCanSendComment = false
                            holder.setGone(R.id.llFill, true)
                        }
                    }
                } else if (mStudyPlanData?.comment_like_status == 3) {
                    if (mIsInitiator) {
                        if (mStudyPlanData?.plan_starttime?.let { mStudyPlanData?.plan_endtime?.let { it1 -> isUnStartOrStop(DateUtil.getCurrentTime(), it, it1) } } != 0) {
                            holder.setGone(R.id.llComment, false)
                            isCanSendComment = true
                            holder.setGone(R.id.llFill, false)
                        } else {
                            holder.setGone(R.id.llComment, false)
                            isCanSendComment = false
                            holder.setGone(R.id.llFill, true)
                        }
                    } else {
                        holder.setGone(R.id.llComment, false)
                        isCanSendComment = false
                        holder.setGone(R.id.llFill, true)
                    }
                }
            } else {
                holder.setGone(R.id.llComment, false)
                isCanSendComment = true
                holder.setGone(R.id.llFill, false)
            }
        } else {
            holder.setGone(R.id.llReport, false)
            if (isSelf) {//我的动态，审核通过的显示分享按钮
                if (dataBean.review_status == 2) {
                    holder.setGone(R.id.llShare, false)
                } else {
                    holder.setGone(R.id.llShare, true)
                }
            } else {
                holder.setGone(R.id.llShare, false)
            }
            holder.setGone(R.id.llStop, false)
            holder.setGone(R.id.llFill, false)
            holder.setGone(R.id.llDel, false)
            if (mStudyPlanData != null) {
                if (mStudyPlanData?.comment_like_status == 0) {
                    holder.setGone(R.id.llComment, false)
                    isCanSendComment = false
                } else if (mStudyPlanData?.comment_like_status == 1) {
                    if (mIsInitiator) {
                        isCanSendComment = false
                        holder.setGone(R.id.llComment, false)
                    } else {
                        val time = mStudyPlanData?.set_time
                        if (time?.let { isCanOperate(it) } == true) {
                            holder.setGone(R.id.llComment, false)
                            false
                        } else {
                            holder.setGone(R.id.llComment, false)
                            false
                        }
                    }
                } else if (mStudyPlanData?.comment_like_status == 3) {
                    isCanSendComment = false
                    holder.setGone(R.id.llComment, false)
                }
            } else {
                holder.setGone(R.id.llComment, false)
                isCanSendComment = false
            }
        }

        //现实打卡标签
        if (dataBean.activity_checkin_type == 0) { //打卡动态
            holder.setGone(R.id.tvDetail, true)
            holder.setGone(R.id.tvDetail2, false)

            setTvSpan(holder.getView(R.id.tvDetail2), dataBean)
        }

        //跟读打卡动态只显示跟读专门样式
        val layout = LinearLayoutManager(context)
        holder.getView<RecyclerView>(R.id.imgRcy).setLayoutManager(layout)
        val repeat_record = dataBean.extra_info?.repeat_record
        if (repeat_record != null) {
            val followUpVoiceDynamicAdapter = FollowUpVoiceDynamicAdapter(repeat_record)

//            boolean expandTvVisibale = itemViewHolder.tvFollowUpAudioExpand.getVisibility() == View.GONE;
            if (repeat_record.size > 4) {   //如果超过6条，显示4条，并且显示展开view
                holder.setText(R.id.tvFollowUpAudioExpand, "展开")
                holder.setGone(R.id.tvFollowUpAudioExpand, false)
                holder.getView<TextView>(R.id.tvFollowUpAudioExpand).setCompoundDrawables(null, null, getTvFollowupDrawableRight(holder), null)
                //                followUpVoiceDynamicAdapter.setExpand(false);
                val subList: ArrayList<StudyDynamic.RepeatRecordBean> = ArrayList<StudyDynamic.RepeatRecordBean>()
                for (i in 0..3) {   //截取前4个音频
                    subList.add(repeat_record[i])
                }
                followUpVoiceDynamicAdapter.setNewInstance(subList)
                holder.getView<TextView>(R.id.tvFollowUpAudioExpand).setOnClickListener(View.OnClickListener {
                    val equals = "展开" == holder.getView<TextView>(R.id.tvFollowUpAudioExpand).getText().toString()
                    val expandStr = if (equals) "收起" else "展开"
                    //                        int drawRight = equals? R.mipmap.ic_arrow_up_blue : R.mipmap.ic_arrow_down_blue;
                    //                        followUpVoiceDynamicAdapter.setExpand(equals);
                    if (equals) {
                        followUpVoiceDynamicAdapter.setNewData(repeat_record)
                    } else {
                        followUpVoiceDynamicAdapter.setNewData(subList)
                    }
                    holder.setText(R.id.tvFollowUpAudioExpand, expandStr)

                    val tvFollowupDrawableRight = getTvFollowupDrawableRight(holder)
                    holder.getView<TextView>(R.id.tvFollowUpAudioExpand).setCompoundDrawables(null, null, tvFollowupDrawableRight, null)
                })
            } else {
                followUpVoiceDynamicAdapter.setNewInstance(repeat_record)
                holder.setGone(R.id.tvFollowUpAudioExpand, true)

            }
            holder.getView<RecyclerView>(R.id.imgRcy).setAdapter(followUpVoiceDynamicAdapter)
            holder.setGone(R.id.imgRcy, false)
        } else {

            holder.setGone(R.id.imgRcy, true)
            holder.setGone(R.id.tvFollowUpAudioExpand, true)

        }

        setNominationStatus(holder, dataBean)
    }


    /**
     * 设置我是自荐文案
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    fun setNominationStatus(holder: BaseViewHolder, item: StudyDynamic) {
        val nomination: StudyDynamic.NominationBean? = item.activity_nomination
        var strText = ""
        var status = ""
        val tvSelfRecommend: TextView = holder.getView(R.id.tvSelfRecommend)

        if (nomination != null) {
            if (nomination.text?.isNotEmpty() == true) {
                strText = nomination.text ?: ""
                tvSelfRecommend.visibility = View.VISIBLE
            } else {
                tvSelfRecommend.visibility = View.GONE
            }
            if (nomination.status?.isNotEmpty() == true) {
                status = nomination.status ?: ""
                //1,我要自荐   2,自荐审核中  3,自荐未通过  4,自荐通过  0,""
                when (status) {
                    "1" -> {
                        tvSelfRecommend.setTextColor(context.resources.getColor(R.color.color_4A90E2))
                        tvSelfRecommend.background = context.resources.getDrawable(R.drawable.shape_line_4a90e2)
                    }
                    "4" -> {
                        tvSelfRecommend.setTextColor(context.resources.getColor(R.color.color_10955B))
                        tvSelfRecommend.background = context.resources.getDrawable(R.drawable.shape_bg_transparent)
                    }
                    else -> {
                        tvSelfRecommend.setTextColor(context.resources.getColor(R.color.color_BB))
                        tvSelfRecommend.background = context.resources.getDrawable(R.drawable.shape_bg_transparent)
                    }
                }
            } else {
                tvSelfRecommend.setTextColor(context.resources.getColor(R.color.color_BB))
                tvSelfRecommend.background = context.resources.getDrawable(R.drawable.shape_bg_transparent)
            }

        } else {
            tvSelfRecommend.visibility = View.GONE

        }
        tvSelfRecommend.text = strText
    }

    //普通模式加载数据
    fun setItemView(holder: BaseViewHolder, item: StudyDynamic) {
        val user = item.user
        if (holder.layoutPosition == 0) {
            if (!TextUtils.isEmpty(title)) {//筛选标签相关
                holder.setGone(R.id.llTitle, false)
                holder.setGone(R.id.titleDelTv, false)
                holder.setText(R.id.titleDelTv, title)
            } else {
                holder.setGone(R.id.titleDelTv, true)
            }
            if (!TextUtils.isEmpty(punch)) {
                holder.setGone(R.id.llTitle, false)
                holder.setGone(R.id.punchTv, false)
                holder.setText(R.id.punchTv, punch)

            } else {
                holder.setGone(R.id.punchTv, true)
            }
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(punch)) {
                holder.setGone(R.id.llTitle, true)
            }
        } else {
            holder.setGone(R.id.llTitle, true)
        }
        if (isSelf) {
            holder.setGone(R.id.ivAuditStatus, false)
            holder.setGone(R.id.flHead, true)
            holder.setGone(R.id.tvOwnTime, false)
            holder.setGone(R.id.tvName, true)
            holder.setGone(R.id.tvTime, true)
            holder.setGone(R.id.tvSelfRecommend, false)

            val b = holder.getView<LinearLayout>(R.id.llSign).getLayoutParams() as RelativeLayout.LayoutParams;
            b.rightMargin = dp2px(74.toFloat()).toInt()

            val auditStatus: Int = item.review_status
            if (auditStatus == 0) {
                holder.setBackgroundResource(R.id.ivAuditStatus, R.mipmap.studyproject_iv_audit_progress)
            } else if (auditStatus == 1) {
                holder.setBackgroundResource(R.id.ivAuditStatus, R.mipmap.studyproject_iv_audit_failed)
            } else if (auditStatus == 2) {
                holder.setBackgroundResource(R.id.ivAuditStatus, R.mipmap.studyproject_iv_audit_approval)
            }
        } else {
            holder.setGone(R.id.flHead, false)
            holder.setGone(R.id.ivAuditStatus, true)
            holder.setGone(R.id.tvOwnTime, true)
            holder.setGone(R.id.tvName, false)
            holder.setGone(R.id.tvTime, false)
            holder.setGone(R.id.tvSelfRecommend, true)
        }
        if (user != null) {
            var name: String? = null
            if (item.is_studyplan_start_user) {
                name = java.lang.String.format(context.getResources().getString(R.string.text_initiator), user.name?.let { getStrUserName(it) })
            } else {
                name = user.name?.let { getStrUserName(it) }
            }
            holder.setText(R.id.tvName, name)
            if (!TextUtils.isEmpty(user.avatar)) {
                holder.setGone(R.id.ivUser, false)
                val headView = holder.getView<HeadView>(R.id.ivUser)
                headView.setHeadImage(user.avatar, user.avatar_identity)
//                Glide.with(context)
//                        .load(user.avatar)
//                        .placeholder(R.mipmap.common_ic_user_head_normal)
//                        .error(R.mipmap.common_ic_user_head_normal)
//                        .circleCrop()
//                        .into(holder.getView(R.id.ivUser))

            } else {
                holder.setGone(R.id.ivUser, false)
            }
        }
        if (isHideDynamic(item)) {
            setActivityTime(holder, item) //时间activity_resource_time判断
        } else {
            holder.setGone(R.id.tvActivityTime, true)
            holder.setGone(R.id.llBottom, false)
            if (item.activity_checkin_type == 0) { //打卡动态
                holder.setGone(R.id.tvSignDynamic, false)
            } else {
                holder.setGone(R.id.tvSignDynamic, true)

            }
            if (item.is_top == 0) {    //是否置顶
                holder.setGone(R.id.tvDynamicTop, true)
            } else {
                holder.setGone(R.id.tvDynamicTop, false)
            }
            if (TextUtils.isEmpty(item.display_tag)) {
                if (!TextUtils.isEmpty(item.studyplan_display_tag)) {
                    holder.setGone(R.id.tv_highquality_dynamic, false)
                    holder.setText(R.id.tv_highquality_dynamic, item.studyplan_display_tag)
                } else {
                    holder.setGone(R.id.tv_highquality_dynamic, true)
                    holder.setText(R.id.tv_highquality_dynamic, "")
                }
            } else {
                holder.setGone(R.id.tv_highquality_dynamic, false)
                holder.setText(R.id.tv_highquality_dynamic, item.display_tag)
            }
            val tvHighqualityDynamicVisible = if (holder.getView<TextView>(R.id.tv_highquality_dynamic).visibility ==
                    View.VISIBLE && isSelf) View.VISIBLE else View.GONE
            holder.getView<TextView>(R.id.tv_highquality_dynamic).visibility = tvHighqualityDynamicVisible
            if (mJoin == 1) {//没加入？？？？？
                val userBean = GlobalsUserManager.userInfo
                if (userBean != null && user != null) {
                    if (userBean.id == user.id) {
                        if (isSelf) {//我的动态，审核通过的显示分享按钮
                            if (item.review_status == 2) {
                                holder.setGone(R.id.llShare, false)
                            } else {
                                holder.setGone(R.id.llShare, true)
                            }
                        } else {
                            holder.setGone(R.id.llShare, false)
                        }
                        holder.setGone(R.id.llReport, true)
                    } else {
                        holder.setGone(R.id.llShare, true)
                        holder.setGone(R.id.llReport, false)
                    }
                } else {
                    holder.setGone(R.id.llShare, true)
                    holder.setGone(R.id.llReport, true)
                }
                if (mIsInitiator) {
                    if (item.publish_state == 0) {
                        //显示取消屏蔽
                        holder.setText(R.id.tvStopDynamic, R.string.study_plan_cancel_stop)
                    } else {
                        // 显示屏蔽
                        holder.setText(R.id.tvStopDynamic, R.string.study_plan_stop)
                    }
                    holder.setGone(R.id.llStop, false)
                } else {
                    holder.setGone(R.id.llStop, true)
                }
                if (item.is_activity_user == 1) { //动态发起人
                    if (item.is_time_out == 0) { //计划已结束
                        if (item.activity_checkin_type == 0) { //打卡动态
                            holder.setGone(R.id.llDel, true)
                        } else {
                            holder.setGone(R.id.llDel, false)
                        }
                    } else {
                        holder.setGone(R.id.llDel, false)
                    }
                } else {
                    holder.setGone(R.id.llDel, true)
                }
                if (mStudyPlanData != null) {
                    if (mStudyPlanData?.comment_like_status == 0) {
                        holder.setGone(R.id.llComment, false)
                        holder.setGone(R.id.llFill, false)
                        isCanSendComment = true
                    } else if (mStudyPlanData?.comment_like_status == 1) {
                        if (mIsInitiator) {
                            if (mStudyPlanData?.plan_starttime?.let { mStudyPlanData?.plan_endtime?.let { it1 -> isUnStartOrStop(DateUtil.getCurrentTime(), it, it1) } } != 0) {
                                holder.setGone(R.id.llComment, false)
                                holder.setGone(R.id.llFill, false)
                                isCanSendComment = true
                            } else {
                                holder.setGone(R.id.llComment, false)
                                holder.setGone(R.id.llFill, true)
                                isCanSendComment = false
                            }
                        } else {
                            val time = mStudyPlanData?.set_time
                            if (time?.let { isCanOperate(it) } == true) {
                                holder.setGone(R.id.llComment, false)
                                holder.setGone(R.id.llFill, false)
                                isCanSendComment = true
                            } else {
                                holder.setGone(R.id.llComment, false)
                                holder.setGone(R.id.llFill, true)
                                isCanSendComment = false
                            }
                        }
                    } else if (mStudyPlanData?.comment_like_status == 3) {
                        if (mIsInitiator) {
                            if (mStudyPlanData?.plan_starttime?.let { mStudyPlanData?.plan_endtime?.let { it1 -> isUnStartOrStop(DateUtil.getCurrentTime(), it, it1) } } != 0) {
                                holder.setGone(R.id.llComment, false)
                                holder.setGone(R.id.llFill, false)
                                isCanSendComment = true
                            } else {
                                holder.setGone(R.id.llComment, false)
                                holder.setGone(R.id.llFill, true)
                                isCanSendComment = false
                            }
                        } else {
                            holder.setGone(R.id.llComment, false)
                            holder.setGone(R.id.llFill, true)
                            isCanSendComment = false
                        }
                    }
                } else {
                    holder.setGone(R.id.llComment, false)
                    holder.setGone(R.id.llFill, false)
                    isCanSendComment = true
                }
            } else {
                holder.setGone(R.id.llShare, true)
                holder.setGone(R.id.llReport, true)
                holder.setGone(R.id.llStop, true)
                holder.setGone(R.id.llDel, true)
                holder.setGone(R.id.llFill, true)

                if (mStudyPlanData != null) {
                    if (mStudyPlanData?.comment_like_status == 0) {
                        holder.setGone(R.id.llComment, false)

                        isCanSendComment = false
                    } else if (mStudyPlanData?.comment_like_status == 1) {
                        if (mIsInitiator) {
                            if (mStudyPlanData?.plan_starttime?.let { mStudyPlanData?.plan_endtime?.let { it1 -> isUnStartOrStop(DateUtil.getCurrentTime(), it, it1) } } != 0) {
                                holder.setGone(R.id.llComment, false)
                                isCanSendComment = true
                            } else {
                                holder.setGone(R.id.llComment, false)
                                isCanSendComment = false
                            }
                        } else {
                            val time = mStudyPlanData?.set_time
                            holder.setGone(R.id.llComment, false)
                            isCanSendComment = false
                        }
                    } else if (mStudyPlanData?.comment_like_status == 3) {
                        holder.setGone(R.id.llComment, false)
                        isCanSendComment = false

                    }
                } else {
                    holder.setGone(R.id.llComment, false)
                    isCanSendComment = false
                }
            }
            if (item.activity_type == 0) {//0为文本类型，1为语音类型
                holder.setGone(R.id.tvDetail, false)
                if (item.activity_checkin_type == 0) { //打卡动态
                    holder.setGone(R.id.tvDetail, true)
                    holder.setGone(R.id.tvDetail2, false)


                    setTvSpan(holder.getView(R.id.tvDetail2), item)
                } else {
                    holder.setGone(R.id.tvDetail2, true)
                    if (item.publish_content.isNullOrEmpty()) {
                        holder.setGone(R.id.tvDetail, true)
                    } else {
                        holder.setGone(R.id.tvDetail, false)
                        holder.getViewOrNull<com.mooc.resource.widget.expandabletextview.ExpandableTextView>(R.id.tvDetail)?.setContent(item.publish_content)

                    }
                }

                holder.setGone(R.id.viewVoice, true)
                //加载图片资源
                if (item.publish_img_list != null && (item.publish_img_list?.size ?: 0) > 0) {
                    var rcy = holder.getViewOrNull<RecyclerView>(R.id.imgRcy)

                    var textView = holder.getViewOrNull<com.mooc.resource.widget.expandabletextview.ExpandableTextView>(R.id.tvDetail)
                    if (textView?.isVisible == true) {
                        var lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        lp.setMargins(0, 20, 0, 0);
                        rcy?.setLayoutParams(lp);

                    } else {
                        var lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        lp.setMargins(0, 0, 0, 0);
                        rcy?.setLayoutParams(lp);
                    }
//                    if (dataBean.getPublish_img_list().size() === 1) {
//                            itemViewHolder.ivDynamic.setVisibility(View.VISIBLE)
//                            itemViewHolder.imgRcy.setVisibility(View.GONE)
//                            ImageLoader.getInstance().displayImage(dataBean.getPublish_img_list().get(0), itemViewHolder.ivDynamic,
//                                BaseOptions.getInstance().getCourseImgOptions())
//                    } else {
                    holder.setGone(R.id.imgRcy, false)
                    val adapter = DynamicImagesAdapter(item.publish_img_list)
                    val manager = GridLayoutManager(context, 3)

                    holder.getView<RecyclerView>(R.id.imgRcy).setLayoutManager(manager)
                    holder.getView<RecyclerView>(R.id.imgRcy).setAdapter(adapter)
                    holder.getView<RecyclerView>(R.id.imgRcy).setNestedScrollingEnabled(false)
//                    adapter.addChildClickViewIds(R.id.imageView)
//                    adapter.setOnItemChildClickListener { adapter, view, position ->
//
//                    }
//                    }
                } else {
                    holder.getView<RecyclerView>(R.id.imgRcy).setVisibility(View.GONE)
                }
            } else {
                //添加语音控件
                if (item.activity_checkin_type == 0) { //打卡动态
                    holder.setGone(R.id.tvDetail, true)
                    holder.setGone(R.id.tvDetail2, false)
                    setTvSpan(holder.getView(R.id.tvDetail2), item)
                } else {
                    holder.setGone(R.id.tvDetail, true)
                }
                holder.setGone(R.id.imgRcy, true)
                holder.setGone(R.id.viewVoice, false)

                val time = item.activity_content_long
                //   增加publish_content是否为空，如果为空则代表音频已被删除，音频控件隐藏
                (holder.getView<VoicePlayerController>(R.id.viewVoice) as VoicePlayerController).setPlayPath(item.publish_content)
                (holder.getView<VoicePlayerController>(R.id.viewVoice) as VoicePlayerController).setTotleTimeLength(item.activity_content_long)
                if (!item.publish_content.isNullOrEmpty()) {
                    holder.setGone(R.id.viewVoice, false)
                    holder.setGone(R.id.tvVoiceDel, true)
                } else {
                    holder.setGone(R.id.viewVoice, true)
                    holder.setGone(R.id.tvVoiceDel, false)
                }
            }
        }
        holder.setText(R.id.tvCommentCount, item.comment_num.toString())
        holder.setText(R.id.tvFillCount, item.like_num.toString())
        if (item?.is_like) {
            holder.setBackgroundResource(R.id.ivFill, R.mipmap.common_icon_red_like)
        } else {
            holder.setBackgroundResource(R.id.ivFill, R.mipmap.common_iv_plan_fill)

        }

        if (!TextUtils.isEmpty(item.publish_time)) {
            var time = item.publish_time
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
            time = m + "月" + (dates?.get(2) ?: "") + "日 " + (ts?.get(0) ?: "") + ":" + (ts?.get(1)
                    ?: "")
            holder.setText(R.id.tvTime, time)
            holder.setText(R.id.tvOwnTime, time)
        }

        holder.getView<TextView>(R.id.tvDetail).setOnLongClickListener(View.OnLongClickListener {
//            val txt: String = holder.getView<TextView>(R.id.tvDetail).getText().toString().trim({ it <= ' ' })
//            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            cm.text = if (TextUtils.isEmpty(txt)) "" else txt
//            Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
            toast(context.getString(R.string.dynamic_nocopy))
            false
        })

        setNominationStatus(holder, item)

    }

    fun isHideDynamic(dataBean: StudyDynamic?): Boolean {
        return if (dataBean != null) {
            var resource_time: Long = 0
            try {
                //过滤掉，需要定时显示，但是当前时间未到显示时间的动态，参考（TAPD 学习项目动态评论打卡优化需求文档）
                //后段返回的时间要1000，才能使用
                //如果为1的时候使用旧的字段判断，是否动态显示，0，用新的判断逻辑
                if ("1" == dataBean.studyplan_is_open_activity) {
                    val s = dataBean?.activity_resource_time
                    resource_time = if (s?.contains(".") == true) {
                        val f = s?.split("\\.".toRegex()).toTypedArray()
                        java.lang.Long.valueOf(f[0]) * 1000
                    } else {
                        java.lang.Long.valueOf(dataBean.activity_resource_time ?: "") * 1000
                    }
                } else {
                    if ("1" == dataBean.is_timing_show) {   //"1"的时候再获取，0的时候不获取
                        resource_time = dataBean.display_time * 1000
                    }
                }
            } catch (ignored: Exception) {
            }
            !isSelf && System.currentTimeMillis() < resource_time
        } else {
            false
        }
    }

    fun setActivityTime(holder: BaseViewHolder, dataBean: StudyDynamic) {
        var resource_time: Long = 0
        try {
            resource_time = if ("1" == dataBean.studyplan_is_open_activity) {
                val s = dataBean.activity_resource_time
                if (s?.contains(".") == true) {
                    val f = s.split("\\.".toRegex()).toTypedArray()
                    java.lang.Long.valueOf(f[0]) * 1000
                } else {
                    java.lang.Long.valueOf(dataBean.activity_resource_time ?: "") * 1000
                }
            } else {
                dataBean.display_time * 1000
            }
        } catch (ignored: java.lang.Exception) {
        }
        if (dataBean.activity_checkin_type == 0) { //打卡动态
            holder.setGone(R.id.tvSignDynamic, false)
        } else {
            holder.setGone(R.id.tvSignDynamic, true)
        }
        if (dataBean.is_top == 0) {
            holder.setGone(R.id.tvDynamicTop, true)
        } else {
            holder.setGone(R.id.tvDynamicTop, false)
        }
        holder.setGone(R.id.llBottom, true)
        holder.setGone(R.id.imgRcy, true)
        holder.setGone(R.id.viewVoice, true)

        if (TextUtils.isEmpty(dataBean.source_title)) {
            holder.setGone(R.id.tvDetail, true)
        } else {
            holder.setGone(R.id.tvDetail, true)
            holder.setGone(R.id.tvDetail2, false)

            val plan = StudyDynamic()
            plan.source_title = dataBean.source_title
            setTvSpan(holder.getView(R.id.tvDetail2), plan)
        }
        holder.setGone(R.id.tvActivityTime, false)
        val str: String
        if (dataBean.activity_num > 0) {
            val activityNum: Int = java.lang.String.valueOf(dataBean.activity_num).length
            str = "成功提交" + dataBean.activity_num.toString() + "字打卡动态，该动态内容可在" + DateUtil.getDate(Date(resource_time), DateStyle.YYYY_MM_DD_HH_MM_SS).toString() + "后查看，敬请关注！"
            val spannableString = SpannableString(str)
            spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_5)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 4, 4 + activityNum, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_5)), 4 + activityNum, 4 + activityNum + 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 4 + activityNum + 13, str.length - 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_5)), str.length - 9, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.setText(R.id.tvActivityTime, spannableString)
        } else {
            str = "成功提交打卡动态，该动态内容可在" + DateUtil.getDate(Date(resource_time), DateStyle.YYYY_MM_DD_HH_MM_SS).toString() + "后查看，敬请关注！"
            val spannableString = SpannableString(str)
            spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 16, str.length - 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.setText(R.id.tvActivityTime, spannableString)
        }
        holder.getView<TextView>(R.id.tvActivityTime).movementMethod = LinkMovementMethod.getInstance()

    }

    fun setTvSpan(tv: TextView, dataBean: StudyDynamic) {
        val spannableString: SpannableString
        var title = ""
        var detail = ""
        if (!TextUtils.isEmpty(dataBean.source_title)) {
            title = dataBean.source_title ?: ""
        }
        if (!TextUtils.isEmpty(dataBean.publish_content)) {
            //如果是普通文本，或者是跟读类型，要显示publish_content
            if (dataBean.activity_type == 0 || dataBean.source_type_id == TYPE_FOLLOWUP_RESOURCE) {
                detail = dataBean.publish_content.toString()
            }
        }
        var str = ""
        if (!TextUtils.isEmpty(title)) {
            tv.highlightColor = context.getResources().getColor(R.color.transparent)
            var isSuc = 1
            var SucStr = ""
            if (dataBean.review_status == 1) {
                SucStr = "#打卡失败#"
                isSuc = 1
            } else if (dataBean.review_status == 2) {
                SucStr = "#打卡成功#"
                isSuc = 2
            } else {
                isSuc = 3
            }
            var punchColor = R.color.color_F59B23
            if (isSuc == 2) {
                punchColor = R.color.color_00CF5A
            } else if (isSuc == 1) {
                punchColor = R.color.color_F59B23
            }
            if (!TextUtils.isEmpty(detail)) {
                if (isSuc == 1 || isSuc == 2) {
                    str = "$SucStr#$title#$detail"
                    spannableString = SpannableString(str)
                    val finalTitle = title
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        onTitleClick?.invoke(finalTitle, false)
                    }, context), 6, title.length + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    val finalIsSuc = isSuc
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        if (finalIsSuc == 1) {
                            onDyniaSucStrClick?.invoke(false)
                        } else {
                            onDyniaSucStrClick?.invoke(true)
                        }

                    }, context), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(punchColor)), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 6, title.length + 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), title.length + 8, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tv.text = spannableString
                    tv.movementMethod = LinkMovementMethod.getInstance()
                } else if (isSuc == 3) {
                    str = "#$title#$detail"
                    spannableString = SpannableString(str)
                    val finalTitle = title
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        onTitleClick?.invoke(finalTitle, false)

                    }, context), 0, title.length + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 0, title.length + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), title.length + 2, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tv.text = spannableString
                    tv.movementMethod = LinkMovementMethod.getInstance()
                }
            } else {
                if (isSuc == 1 || isSuc == 2) {
                    str = "$SucStr#$title#"
                    spannableString = SpannableString(str)
                    val finalTitle = title
                    spannableString.setSpan(Clickable(View.OnClickListener {

                        onTitleClick?.invoke(finalTitle, false)

                    }, context), 6, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    val finalIsSuc = isSuc
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        if (finalIsSuc == 1) {
                            onDyniaSucStrClick?.invoke(false)
                        } else {
                            onDyniaSucStrClick?.invoke(true)
                        }
                    }, context), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(punchColor)), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 6, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 6, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tv.text = spannableString
                    tv.movementMethod = LinkMovementMethod.getInstance()
                } else if (isSuc == 3) {
                    str = "#$title#"
                    spannableString = SpannableString(str)
                    val finalTitle = title
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        onTitleClick?.invoke(finalTitle, false)

                    }, context), 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tv.text = spannableString
                    tv.movementMethod = LinkMovementMethod.getInstance()
                }
            }
        } else {
            if (!TextUtils.isEmpty(detail)) {
                str = detail
                spannableString = SpannableString(str)
                spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv.text = spannableString
            } else {
                tv.visibility = View.GONE
            }
        }
    }

    fun isUnStartOrStop(time: Long, startTime: Long, stopTime: Long): Int {
        return if (time < startTime) { //尚未开始
            0
        } else if (time <= stopTime) { //可以加入计划
            1
        } else { //计划结束
            2
        }
    }


    internal class Clickable(val mListener: View.OnClickListener, var context: Context) : ClickableSpan() {
        override fun onClick(widget: View) {
            mListener.onClick(widget)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = context.getResources().getColor(R.color.color_1982FF)
        }
    }

    fun isCanOperate(time: Long): Boolean {
//        return time > 0 && DateUtil.getCurrentTime() <= time * 1000;
        return time <= 0 || DateUtil.getCurrentTime() <= time * 1000
    }

    fun getTvFollowupDrawableRight(holder: BaseViewHolder): Drawable? {
        val equals = "展开" == holder.getView<TextView>(R.id.tvFollowUpAudioExpand).getText().toString()
        val drawRight: Int = if (equals) R.mipmap.common_ic_arrow_down_blue else R.mipmap.studyproject_ic_arrow_up_blue
        val drawable: Drawable = context.getResources().getDrawable(drawRight)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        return drawable
    }

}