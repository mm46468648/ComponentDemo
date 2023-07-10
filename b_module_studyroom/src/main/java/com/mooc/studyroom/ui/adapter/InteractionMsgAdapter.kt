package com.mooc.studyroom.ui.adapter

import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.R
import com.mooc.studyroom.model.AllClassBean
import com.mooc.studyroom.model.InteractionMsgBean
import com.mooc.studyroom.model.InteractionOtherBean

/**

 * @Author limeng
 * @Date 2021/3/5-11:00 AM
 */
class InteractionMsgAdapter(data: ArrayList<InteractionMsgBean>?) : BaseQuickAdapter<InteractionMsgBean, BaseViewHolder>(R.layout.studyromm_item_interaction_msg, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: InteractionMsgBean) {
        if (item.time > 0) {
            val time = DateUtil.timeToString(item.time * 1000, "yyyy-MM-dd HH:mm")
            holder.setText(R.id.tvTime, time)
        }
        var otherBean: InteractionOtherBean? = item.others
        if (otherBean == null) {
            holder.setGone(R.id.llDynamicNote, true)
            holder.setGone(R.id.llOthers, true)
        } else {


            if (item.like_type == 8) {
                var allClassBean = otherBean?.extra_dict
                holder.setGone(R.id.llDynamicNote, true)
                holder.setGone(R.id.llOthers, false)
                when (otherBean?.event_name) {

                    "22" -> {
                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, false)
                            holder.setText(R.id.tvTypeOthers, R.string.track)
                            Glide.with(context).load(allClassBean?.coverUrlSmall).into(holder.getView(R.id.ivImageOthers))
                            if (allClassBean?.trackTitle.isNullOrEmpty()) {
                                holder.setText(R.id.tvDetailOthers, allClassBean?.announcer?.nickname)
                            } else {
                                holder.setText(R.id.tvDetailOthers, allClassBean?.trackTitle)
                            }
                        }
                    }
                    "30" -> {

                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, false)


                            holder.setText(R.id.tvTypeOthers, R.string.study_plan_dynamic)
                            if (allClassBean?.activity_type == 0) {
                                if (allClassBean.activity_checkin_type == 0) {//打卡动态
                                    setTvSpan(holder.getView<TextView>(R.id.tvDetailDynamicNote), allClassBean)
                                } else {
                                    if (allClassBean.publish_content.isNullOrEmpty()) {
                                        holder.setGone(R.id.tvDetailDynamicNote, false)
                                        holder.setText(R.id.tvDetailDynamicNote, allClassBean.publish_content)
                                    } else {
                                        holder.setGone(R.id.tvDetailDynamicNote, true)

                                    }

                                }
                            } else {
                                holder.setGone(R.id.ivImageDynamic, true)
                            }
                        }

                    }
                    "2" -> {
                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, false)
                            holder.setText(R.id.tvTypeOthers, R.string.course)
                            holder.setText(R.id.tvDetailOthers, allClassBean?.title)
                            Glide.with(context).load(allClassBean?.picture).into(holder.getView(R.id.ivImageOthers))
                        }
                    }
                    "14" -> {
                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, false)
                            holder.setText(R.id.tvTypeOthers, R.string.article)
                            holder.setText(R.id.tvDetailOthers, allClassBean?.title)
                            Glide.with(context).load(allClassBean?.picture).into(holder.getView(R.id.ivImageOthers))
                        }
                    }
                    "10" -> {
                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, false)
                            holder.setGone(R.id.ivImageDynamic, true)
                            holder.setText(R.id.tvTypeDynamicNote, R.string.baike)
                            holder.setText(R.id.tvDetailDynamicNote, allClassBean?.content)
                        }
                    }
                    "26" -> {
                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, false)
                            holder.setGone(R.id.llOthers, true)
                            holder.setGone(R.id.ivImageDynamic, true)
                            holder.setText(R.id.tvTypeDynamicNote, R.string.note)
                            holder.setText(R.id.tvDetailDynamicNote, allClassBean?.content)
                        }
                    }
                    "21" -> {
                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, false)
                            holder.setText(R.id.tvTypeOthers, R.string.track_course)
                            if (allClassBean?.albumTitle.isNullOrEmpty()) {
                                holder.setText(R.id.tvDetailOthers, allClassBean?.lastUptrack?.trackTitle)
                            } else {
                                holder.setText(R.id.tvDetailOthers, allClassBean?.albumTitle)
                            }
                        }
                    }
                    "12" -> {
                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, false)
                            holder.setText(R.id.tvTypeOthers, R.string.konwledge)
                            if (!allClassBean?.title.isNullOrEmpty()) {
                                holder.setText(R.id.tvDetailOthers, Html.fromHtml(allClassBean?.title))
                            }
                            Glide.with(context).load(allClassBean?.cover_image).into(holder.getView(R.id.ivImageOthers))
                        }
                    }
                    "11" -> {
                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, false)
                            holder.setText(R.id.tvTypeOthers, R.string.periodical)
                            if (!allClassBean?.title.isNullOrEmpty()) {
                                holder.setText(R.id.tvDetailOthers, Html.fromHtml(allClassBean?.title))
                            }
                            Glide.with(context).load(allClassBean?.basic_cover_url).into(holder.getView(R.id.ivImageOthers))
                        }
                    }
                    "5" -> {
                        if (isNullResource(otherBean)) {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, true)
                        } else {
                            holder.setGone(R.id.llDynamicNote, true)
                            holder.setGone(R.id.llOthers, false)
                            holder.setText(R.id.tvTypeOthers, R.string.ebook)
                            if (!allClassBean?.title.isNullOrEmpty()) {
                                holder.setText(R.id.tvDetailOthers, Html.fromHtml(allClassBean?.title))
                            }
                            Glide.with(context).load(allClassBean?.picture).into(holder.getView(R.id.ivImageOthers))
                        }
                    }
                    else -> {
                        holder.setGone(R.id.llDynamicNote, true)
                        holder.setGone(R.id.llOthers, true)
                    }
                }
            } else {
                holder.setGone(R.id.llDynamicNote, true)
                holder.setGone(R.id.llOthers, true)
            }
        }
        setTvSpan(holder.getView(R.id.tvContent), item)


    }

    private fun isNullResource(data: InteractionOtherBean?): Boolean {
        return if (data != null) {
            val type: String? = data.event_name
            if (type == "2") {
                data.extra_dict?.id.isNullOrEmpty()
            } else if (type == "12") {
                data.extra_dict?.url.isNullOrEmpty()

            } else if (type == "10") {
                data.extra_dict?.url.isNullOrEmpty()
            } else if (type == "11") {
                data.extra_dict?.url.isNullOrEmpty()
            } else if (type == "26") {
                data.extra_dict?.id.isNullOrEmpty()

            } else if (type == "14") {
                data.extra_dict?.url.isNullOrEmpty()

            } else if (type == "21") {
                data.extra_dict?.id.isNullOrEmpty()

            } else if (type == "22") {
                data.extra_dict?.id.isNullOrEmpty()

            } else if (type == "30") {
                data.extra_dict?.id.isNullOrEmpty()

            } else if (type == "5") {
                data.extra_dict?.link.isNullOrEmpty()

            } else {
                true
            }
        } else {
            true
        }
    }

    fun setTvSpan(tv: TextView, dataBean: AllClassBean) {
        val spannableString: SpannableString
        var title: String? = null
        var detail: String? = null
        if (!TextUtils.isEmpty(dataBean.source_title)) {
            title = dataBean?.source_title
        }
        if (!TextUtils.isEmpty(dataBean.publish_content)) {
            if (dataBean.activity_type == 0) {
                detail = dataBean?.publish_content
            }
        }
        var str: String? = null
        if (!TextUtils.isEmpty(title)) {
            tv.highlightColor = context.getResources().getColor(R.color.color_transparent)
            if (!TextUtils.isEmpty(detail)) {
                str = "#$title#$detail"
                spannableString = SpannableString(str)
                spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 0, title?.length!! + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), title?.length + 2, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv.text = spannableString
                tv.movementMethod = LinkMovementMethod.getInstance()
            } else {
                str = "#$title#"
                spannableString = SpannableString(str)
                spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_4A90E2)), 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv.text = spannableString
                tv.movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            if (!TextUtils.isEmpty(detail)) {
                str = detail
                spannableString = SpannableString(str)
                str?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), 0, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                tv.text = spannableString
            }
        }
    }

    private fun setTvSpan(textView: TextView, data: InteractionMsgBean) {
        val content: String
        val spannableString: SpannableString
        val senderNameLength = data.sender_name?.length
        val eventNameLength = data.event_name?.length
        val totalLength: Int
        textView.highlightColor = context.getResources().getColor(R.color.transparent)
        when (data.like_type) {
            1 -> {
                content = String.format(context.getResources().getString(R.string.interaction_link_msg),
                        data.sender_name, data.event_name)
                totalLength = content.length
                spannableString = SpannableString(content)
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), it, senderNameLength + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), senderNameLength + 6, senderNameLength + eventNameLength + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                senderNameLength?.let {
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        if (!TextUtils.isEmpty(data.sender_name) &&
                                !data.sender_name.equals(context.getString(R.string.text_str_initiator))) {
                            toUserIndo(data.sender_id?.toString())
                        }
                    }, context), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(Clickable(View.OnClickListener {
                            toStudyProject(data.studyplan_id.toString())
                        }, context), senderNameLength + 6, senderNameLength + eventNameLength + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), senderNameLength + eventNameLength + 6, totalLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            2 -> {
                content = java.lang.String.format(context.getResources().getString(R.string.interaction_link_comment_msg), data.sender_name, data.event_name)
                totalLength = content.length
                spannableString = SpannableString(content)
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), it, senderNameLength + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), senderNameLength + 6, senderNameLength + eventNameLength + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                senderNameLength?.let {
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        if (!TextUtils.isEmpty(data.sender_name) && !data.sender_name.equals(context.getString(R.string.text_str_initiator))) {

                            toUserIndo(data.sender_id?.toString())

                        }
                    }, context), 0, senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(Clickable(View.OnClickListener {
                            toStudyProject(data.studyplan_id.toString())

                        }, context), senderNameLength + 6, senderNameLength + eventNameLength + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                senderNameLength?.let {
                    eventNameLength?.let {
                        spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), senderNameLength + eventNameLength + 6, totalLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            3 -> {
                content = java.lang.String.format(context.getResources().getString(R.string.interaction_comment_msg), data.sender_name, data.event_name)
                totalLength = content.length
                spannableString = SpannableString(content)
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), it, senderNameLength + 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), senderNameLength + 7, senderNameLength + eventNameLength + 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                senderNameLength?.let {
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        if (!TextUtils.isEmpty(data.sender_name) && !data.sender_name.equals(context.getString(R.string.text_str_initiator))) {

                            toUserIndo(data.sender_id?.toString())

                        }
                    }, context), 0, senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(Clickable(View.OnClickListener {
                            toStudyProject(data.studyplan_id.toString())

                        }, context), senderNameLength + 7, senderNameLength + eventNameLength + 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), senderNameLength + +7, totalLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            4 -> {
                content = java.lang.String.format(context.getResources().getString(R.string.interaction_fuel_msg), data.sender_name, data.event_name)
                totalLength = content.length
                spannableString = SpannableString(content)
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), it, senderNameLength + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), senderNameLength + 3, senderNameLength + eventNameLength + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                senderNameLength?.let {
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        if (!TextUtils.isEmpty(data.sender_name) && !data.sender_name.equals(context.getString(R.string.text_str_initiator))) {

                            toUserIndo(data.sender_id?.toString())

                        }
                    }, context), 0, senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(Clickable(View.OnClickListener {
                            toStudyProject(data.studyplan_id.toString())
                        }, context), senderNameLength + 3, senderNameLength + eventNameLength + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    eventNameLength?.let {
                        senderNameLength?.let {
                            spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), senderNameLength + eventNameLength + 3, totalLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            }
            5 -> {
                content = java.lang.String.format(context.getResources().getString(R.string.interaction_fuel_msg), data.sender_name, data.event_name)
                totalLength = content.length
                spannableString = SpannableString(content)
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), it, senderNameLength + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), senderNameLength + 3, senderNameLength + eventNameLength + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                eventNameLength?.let {
                    senderNameLength?.let {
                        spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), senderNameLength + eventNameLength + 3, totalLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                senderNameLength?.let {
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        if (!TextUtils.isEmpty(data.sender_name) && !data.sender_name.equals(context.getString(R.string.text_str_initiator))) {
                            toUserIndo(data.sender_id?.toString())
                        }
                    }, context), 0, senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            7 -> {
                content = java.lang.String.format(context.getResources().getString(R.string.interaction_follow_msg), data.sender_name)
                totalLength = content.length
                spannableString = SpannableString(content)
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), it, totalLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                senderNameLength?.let {
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        if (!TextUtils.isEmpty(data.sender_name)) {
                            toUserIndo(data.sender_id?.toString())

                        }
                    }, context), 0, senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            8 -> {
                content = java.lang.String.format(context.getResources().getString(R.string.interaction_like_school_resource_msg), data.sender_name)
                totalLength = content.length
                spannableString = SpannableString(content)
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_1982FF)), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                senderNameLength?.let { spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), it, totalLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
                senderNameLength?.let {
                    spannableString.setSpan(Clickable(View.OnClickListener {
                        if (!TextUtils.isEmpty(data.sender_name)) {
                            toUserIndo(data.sender_id?.toString())
                        }
                    }, context), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            else -> {
                content = data.content.toString()
                totalLength = content.length
                spannableString = SpannableString(content)
                spannableString.setSpan(ForegroundColorSpan(context.getResources().getColor(R.color.color_6)), 0, totalLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
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

    //跳转个人信息
    fun toUserIndo(id: String) {
        ARouter.getInstance()
                .build(Paths.PAGE_USER_INFO)
                .withString(IntentParamsConstants.MY_USER_ID, id)
                .navigation()

    }

    //跳转学习计划
    fun toStudyProject(id: String) {
        ARouter.getInstance()
                .build(Paths.PAGE_STUDYPROJECT)
                .withString(IntentParamsConstants.STUDYPROJECT_PARAMS_ID, id)
                .navigation()

    }
}