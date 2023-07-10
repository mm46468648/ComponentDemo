package com.mooc.discover.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.discover.R
import com.mooc.discover.adapter.delegate.RecommendSpecilDelegeta
import com.mooc.discover.binding.Bindings
import com.mooc.discover.model.RecommendContentBean
import com.mooc.resource.widget.MoocImageView

class RecommendSpecialAdapter(data: ArrayList<RecommendContentBean.DataBean>) :
    BaseDelegateMultiAdapter<RecommendContentBean.DataBean, BaseViewHolder>(data), LoadMoreModule {


    init {
        setMultiTypeDelegate(RecommendSpecilDelegeta())
    }


    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        if (holder.itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) {
            return
        }
        val type: Int = item.type
        when (type) {
            ResourceTypeConstans.TYPE_ARTICLE,
            ResourceTypeConstans.TYPE_SPECIAL,
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE,
            ResourceTypeConstans.TYPE_KNOWLEDGE,
            ResourceTypeConstans.TYPE_NOTE -> {
                setArticleItem(holder, item, type)
            }


            ResourceTypeConstans.TYPE_ACTIVITY_TASK, ResourceTypeConstans.TYPE_ACTIVITY -> {
                setActivityItem(holder, item, type)
            }

            ResourceTypeConstans.TYPE_NO_IMAGE,
            ResourceTypeConstans.TYPE_BAIKE,
            ResourceTypeConstans.TYPE_COLUMN,
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK,
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                setNoImageItem(holder, item)
            }

            ResourceTypeConstans.TYPE_COURSE -> {

                holder.setText(R.id.title, item.title)

                //设置课程type
                holder.setText(R.id.type_name, R.string.course)
                holder.setText(R.id.tvTypeRecommendChild, R.string.course)
                if (!TextUtils.isEmpty(item.identity_name)) {
                    holder.setText(R.id.tvTypeRecommendChild, item.identity_name)
                    holder.setGone(R.id.tvTypeRecommendChild, false)
                    holder.setGone(R.id.type_name, true)
                } else {
                    holder.setGone(R.id.tvTypeRecommendChild, true)
                    holder.setGone(R.id.type_name, false)
                }

                holder.setText(R.id.org, item.org)

                holder.setText(R.id.platform, item.source)
                holder.setText(R.id.time, item.start_time)


                if (TextUtils.isEmpty(item.source)) {
                    holder.setGone(R.id.platform, true)
                } else {
                    holder.setGone(R.id.platform, false)
                }

                val isHaveExam: kotlin.String = item.is_have_exam
                val tv = holder.getView<TextView>(R.id.tvExamType)

                if (isHaveExam == "1" || isHaveExam == "true") {
                    tv.text =
                        tv.context.resources.getString(R.string.course_str_have_exam) + Bindings.dort
                    tv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
                } else {
                    tv.text =
                        tv.context.resources.getString(R.string.course_str_no_exam) + Bindings.dort
                    tv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
                }
                if (!TextUtils.isEmpty(item.is_have_exam_info)) {
                    tv.setText(item.is_have_exam_info + Bindings.dort)
                }


                val vertificateTv = holder.getView<TextView>(R.id.tvCertificateType)

                val isVerified: kotlin.String = item.verified_active
                if (isVerified == "1" || isVerified == "true") {
                    vertificateTv.text =
                        tv.context.resources.getString(R.string.course_str_have_certificate) + Bindings.dort
                    vertificateTv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
                } else {
                    vertificateTv.text =
                        tv.context.resources.getString(R.string.course_str_no_certificate) + Bindings.dort
                    vertificateTv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
                }
                if (!TextUtils.isEmpty(item.verified_active_info)) {
                    vertificateTv.text = item.verified_active_info + Bindings.dort
                }

                val isFreeTv = holder.getView<TextView>(R.id.tvPriceType)

                val isFree: kotlin.String = item.is_free
                if (isFree == "1" || isFree == "true") {
                    isFreeTv.text = tv.context.resources.getString(R.string.course_str_free)
                    isFreeTv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
                } else {
                    isFreeTv.text = tv.context.resources.getString(R.string.course_str_pay)
                    isFreeTv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
                }
                if (!TextUtils.isEmpty(item.is_free_info)) {
                    isFreeTv.setText(item.is_free_info)
                }

                Glide.with(context).load(item.cover_url)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(holder.getView(R.id.cover));
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                holder.setText(R.id.title, item.title)
                holder.setText(R.id.type_name, ResourceTypeConstans.typeStringMap.get(type))
                holder.setText(R.id.tv_source, item.source)

                holder.setText(R.id.time, TimeUtils.timeParse(item.video_duration.toLong()))

                Glide.with(context)
                    .load(item.cover_url)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(holder.getView<ImageView>(R.id.cover));
            }


            ResourceTypeConstans.TYPE_ALBUM -> {

                holder.setText(R.id.title, item.title)
                holder.setText(R.id.type_name, ResourceTypeConstans.typeStringMap.get(type))
                holder.setText(R.id.date, item.publish_time)

                val album: AlbumBean? = item.album_data
                album?.apply {
                    holder.setText(R.id.count, album.include_track_count.toString() + "集")
                    holder.setText(
                        R.id.play_count,
                        String.format(
                            context.resources.getString(R.string.text_str_play_count),
                            album.playCount.toString()
                        )
                    )
                    Glide.with(context)
                        .load(album.cover_url_middle)
                        .transform(GlideTransform.centerCropAndRounder2)
                        .placeholder(R.mipmap.common_bg_cover_default)
                        .into(holder.getView(R.id.cover));
                }
            }

            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setText(R.id.title, item.title)
                holder.setText(R.id.type_name, ResourceTypeConstans.typeStringMap.get(type))
                holder.setText(R.id.date, item.publish_time)

                val track: TrackBean? = item.track_data
                if (track != null) {
                    holder.setText(R.id.platform, track.subordinated_album?.album_title);

                    holder.setText(
                        R.id.count,
                        StringFormatUtil.formatPlayCount(track.play_count)
                    )


                    holder.setText(R.id.time, TimeUtils.timeParse(track.duration));

                    Glide.with(context)
                        .load(track.cover_url_small)
                        .transform(GlideTransform.centerCropAndRounder2)
                        .placeholder(R.mipmap.common_bg_cover_default)
                        .into(holder.getView(R.id.cover));

                }
            }


            ResourceTypeConstans.TYPE_QUESTIONNAIRE, ResourceTypeConstans.TYPE_TEST_VOLUME -> {
                setTestQuesionItem(holder, item, type)
            }

            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.title, item.title)
                holder.setText(R.id.type_name, ResourceTypeConstans.typeStringMap.get(type))
                holder.setText(R.id.time, item.basic_date_time)
                holder.setText(R.id.publish_time, item.publish_time)

                holder.setText(R.id.staff, item.staff)

                Glide.with(context)
                    .load(item.cover_url)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .into(holder.getView<ImageView>(R.id.cover));

            }

            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.title, item.title)
                holder.setText(R.id.type_name, ResourceTypeConstans.typeStringMap.get(type))
                holder.setText(R.id.platform, item.writer)
                holder.setText(R.id.date, item.publish_time)



                holder.setText(
                    R.id.count,
                    StringFormatUtil.formatPlayCount(item.word_count.toInt().toLong())
                        .toString() + "字"
                )


                val org = if (TextUtils.isEmpty(item.press)) "" else item.press
                if (TextUtils.isEmpty(item.platform_zh)) {
                    holder.setText(R.id.org, org)
                } else {
                    holder.setText(R.id.org, item.platform_zh.toString() + " | " + org)
                }

                Glide.with(context)
                    .load(item.cover_url)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .into(holder.getView<ImageView>(R.id.cover));
            }

            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setText(R.id.title, item.title)
                holder.setText(R.id.tag, ResourceTypeConstans.typeStringMap.get(type))
                holder.setText(R.id.orgs, "发起人: ${item.staff}")
                Bindings.setPlanState(holder.getView(R.id.state), item)
                if (item.time_mode.equals("1")) {//时间永久
                    holder.setText(
                        R.id.before_time,
                        context.resources.getString(R.string.study_time_permanent_opening)
                    )
                } else {
                    holder.setText(R.id.before_time, item.planDurition)
                }
                holder.setText(R.id.num, item.student_num.toString() + "人")
                holder.setText(R.id.column_name, item.parent_name)

                Glide.with(context)
                    .load(item.cover_url)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(holder.getView<ImageView>(R.id.cover));

            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setText(R.id.title, item.title)
                holder.setText(R.id.type_name, ResourceTypeConstans.typeStringMap.get(type))

                holder.setText(
                    R.id.count,
                    StringFormatUtil.formatPlayCount(item.audio_data.audio_play_num.toLong())
                )

                holder.setText(
                    R.id.play_time,
                    item.audio_data.audio_time.let { TimeUtils.timeParse(it.toLong()) })
                holder.setText(R.id.time, item.publish_time)

                Glide.with(context)
                    .load(item.cover_url)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(holder.getView<ImageView>(R.id.cover));


            }

            ResourceTypeConstans.TYPE_MASTER_TALK -> {
                holder.setText(R.id.title, item.title)
                holder.setText(R.id.column_name, item.parent_name)

                if (item.master_info.author_info != null) {
                    if (!TextUtils.isEmpty(item.master_info.author_info?.name)) {
                        (holder.getView<TextView>(R.id.about)).text =
                            item.master_info.author_info?.name;
                    }
                }

//                Bindings.setPrice(helper.getView(R.id.price), bean.price)
                holder.setText(R.id.desc, item.desc)
                Glide.with(context)
                    .load(item.cover_url)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(holder.getView<ImageView>(R.id.cover));
            }

            ResourceTypeConstans.TYPE_WX_PROGRAM -> {
                setWxProgram(holder, item, type)
            }
            ResourceTypeConstans.TYPE_BATTLE -> {
                setBattle(holder, item)
            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {
                holder.setGone(R.id.tag, false)
                holder.setText(R.id.tag, ResourceTypeConstans.typeStringMap[type])

                holder.setText(R.id.title, item.title)
                holder.setText(R.id.orgs, item.resource_info?.title)
                holder.setText(R.id.time, item.resource_info?.updated_time + "更新")

                Glide.with(context)
                    .load(item.cover_url)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(holder.getView<ImageView>(R.id.cover));
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                holder.setText(
                    R.id.tag,
                    ResourceTypeConstans.typeStringMap[item.type]
                )
                val imagePublication = holder.getView<MoocImageView>(R.id.cover)
                imagePublication.setImageUrl(item.cover_url, 2)


                holder.setText(R.id.title, item.title)
                val updateStr =
                    "更新至" + item.periodicals_data.year + "年 第" + item.periodicals_data.term + "期"
                holder.setText(R.id.device, updateStr)
                holder.setText(R.id.subtitle, item.periodicals_data.unit)
//                helper.setText(R.id.typename, bean.periodicals_data.unit)

            }
            ResourceTypeConstans.TYPE_TASK -> {
                setTask(holder, item, type)
            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {
                setMicroKowledge(holder, item, type)
            }
        }

    }

    private fun setMicroKowledge(
        holder: BaseViewHolder,
        item: RecommendContentBean.DataBean,
        type: Int
    ) {
        holder.setGone(R.id.tag, false)
        holder.setText(R.id.tag, ResourceTypeConstans.typeStringMap[type])

        holder.setText(R.id.title, item.title)
        holder.setText(R.id.orgs, item.resource_info?.title)
        holder.setText(R.id.time, item.resource_info?.updated_time + "更新")

        Glide.with(context)
            .load(item.cover_url)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_default)
            .into(holder.getView<ImageView>(R.id.cover));
    }

    private fun sp2px(spValue: Float): Int {
        val fontScale: Float = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    private fun setTask(
        holder: BaseViewHolder,
        item: RecommendContentBean.DataBean,
        type: Int
    ) {
        holder.setText(R.id.tvTitleTask, item.title)

        var peoNum = "${item.join_num}人参与"
        if (item.task_data.is_limit_num) { //限制人数
            peoNum = "${item.join_num}/${item.task_data.limit_num}人参与"
        }
        holder.setText(R.id.tvPeopleNumTask, peoNum)
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_default)
            .placeholder(R.mipmap.common_bg_cover_default)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(2f.dp2px())))
            .into(holder.getView(R.id.ivImgTask))


        if (item.time_mode == "1") {
            holder.setText(R.id.tvTimeTask, "任务时间：永久开放")
            holder.setText(R.id.tvGetTimeTask, "领取时间：永久开放")
        } else {
            holder.setText(
                R.id.tvTimeTask,
                "任务时间：" + item.task_start_date + "-" + item.task_end_date
            )
            if (item.start_time.isEmpty() && item.end_time.isEmpty()) {
                holder.setText(R.id.tvGetTimeTask, "永久开放")
            } else {
                holder.setText(R.id.tvGetTimeTask, "领取时间：" + item.start_time + "-" + item.end_time)
            }
        }

        if (!TextUtils.isEmpty(item.task_data.score?.success_score)) {
            holder.setGone(R.id.tvRewardTask, false)
            holder.setGone(R.id.tvRewardScoreTask, false)
            holder.setText(R.id.tvRewardScoreTask, item.task_data.score?.success_score)
        } else {
            holder.setGone(R.id.tvRewardTask, true)
            holder.setGone(R.id.tvRewardScoreTask, true)
        }

        holder.setBackgroundColor(R.id.tvStatusTask, Color.TRANSPARENT)
        val buttonText = holder.getViewOrNull<TextView>(R.id.tvStatusTask)
        buttonText?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15F)

        holder.getViewOrNull<TextView>(R.id.tvStatusTask)
            ?.setCompoundDrawables(null, null, null, null)


        // 1进行中
//            2已完成
//            3失败
//            4未领取已过期
//            5 未开始领取
//            6 任务已报名未开始 （新增）
        when (item.task_data.status) {
            TaskConstants.TASK_STATUS_DOING -> {//进行中
                holder.setText(R.id.tvStatusTask, "进行中")
                holder.setTextColor(
                    R.id.tvStatusTask,
                    ContextCompat.getColor(context, R.color.color_10955B)
                )

            }
            TaskConstants.TASK_STATUS_SUCCESS -> {// 任务成功
                holder.setText(R.id.tvStatusTask, "已完成")// 加了一个图片
                holder.setTextColor(
                    R.id.tvStatusTask,
                    ContextCompat.getColor(context, R.color.color_white)
                )
                val drawable: Drawable? =
                    ContextCompat.getDrawable(context, R.mipmap.common_icon_task_finish)
                drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                holder.getViewOrNull<TextView>(R.id.tvStatusTask)
                    ?.setCompoundDrawables(drawable, null, null, null)
            }
            TaskConstants.TASK_STATUS_FAIL -> {// 任务失败
                holder.setText(R.id.tvStatusTask, "任务失败")
                holder.setTextColor(
                    R.id.tvStatusTask,
                    ContextCompat.getColor(context, R.color.color_F35600)
                )

            }
            TaskConstants.TASK_STATUS_EXPIRED -> {//已过期
                holder.setText(R.id.tvStatusTask, "已过期")
                holder.setTextColor(
                    R.id.tvStatusTask,
                    ContextCompat.getColor(context, R.color.color_white)
                )
            }
            TaskConstants.TASK_STATUS_CANNOT_GET -> {//未开始领取
                holder.setText(R.id.tvStatusTask, "领取未开始")
                holder.setTextColor(
                    R.id.tvStatusTask,
                    ContextCompat.getColor(context, R.color.color_white)
                )
            }
            TaskConstants.TASK_STATUS_UNSTART -> {//任务已报名未开始
                holder.setText(R.id.tvStatusTask, "任务未开始")
                holder.setTextColor(
                    R.id.tvStatusTask,
                    ContextCompat.getColor(context, R.color.color_white)
                )
            }
            else -> {
                holder.setText(R.id.tvStatusTask, "查看详情")
                holder.setBackgroundResource(
                    R.id.tvStatusTask,
                    R.drawable.shape_radius15_color_primary
                )
                holder.setTextColor(
                    R.id.tvStatusTask,
                    ContextCompat.getColor(context, R.color.color_white)
                )
                buttonText?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13F)

            }
        }
    }

    private fun setActivityItem(
        helper: BaseViewHolder,
        bean: RecommendContentBean.DataBean,
        type: Int
    ) {
        helper.setText(R.id.title, bean.title)
        helper.setText(R.id.orgs, bean.event_task.applyDuration)
        helper.setText(R.id.time, bean.publish_time)
        helper.setText(R.id.tag, ResourceTypeConstans.typeStringMap.get(type))
        helper.setText(R.id.column_name, bean.parent_name)

        Glide.with(context)
            .load(bean.cover_url)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_default)
            .into(helper.getView<ImageView>(R.id.cover));
    }

    private fun setTestQuesionItem(
        helper: BaseViewHolder,
        bean: RecommendContentBean.DataBean,
        type: Int
    ) {
        helper.setText(R.id.title, bean.title)
        helper.setText(R.id.tag, ResourceTypeConstans.typeStringMap.get(type))
//        helper.setText(R.id.orgs, bean.quesintTime)
        //如果是测试卷就隐藏，orgs
        helper.setGone(R.id.orgs, true)
        helper.setText(R.id.orgs, bean.start_time + "-" + bean.end_time)
        helper.setText(R.id.column_name, bean.parent_name)

        helper.setText(R.id.time, bean.publish_time)
    }

    private fun setNoImageItem(helper: BaseViewHolder, bean: RecommendContentBean.DataBean) {
        helper.setText(R.id.title, bean.title)

        if (TextUtils.isEmpty(bean.platform_zh)) {
            helper.setText(R.id.platform, bean.org);
        } else {
            if (TextUtils.isEmpty(bean.org)) {
                helper.setText(R.id.platform, bean.platform_zh);
            } else {
                helper.setText(R.id.platform, bean.platform_zh + " | " + bean.org);
            }
        }
        helper.setText(R.id.time, bean.publish_time)
    }

    private fun setArticleItem(
        helper: BaseViewHolder,
        bean: RecommendContentBean.DataBean,
        type: Int
    ) {
        helper.setText(R.id.title, bean.title)
        helper.setText(R.id.type_name, ResourceTypeConstans.typeStringMap.get(type))



        if (TextUtils.isEmpty(bean.platform_zh)) {
            helper.setText(R.id.org_platform, bean.org);
        } else {
            if (TextUtils.isEmpty(bean.org)) {
                helper.setText(R.id.org_platform, bean.platform_zh);
            } else {
                helper.setText(R.id.org_platform, bean.platform_zh + " | " + bean.org);
            }
        }


        helper.setText(R.id.time, bean.publish_time)


        Glide.with(context).load(bean.cover_url).placeholder(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into(helper.getView(R.id.cover));
    }

    fun setBattle( helper: BaseViewHolder,
                   bean: RecommendContentBean.DataBean, ){
        helper.setText(R.id.title, bean.title)
    }

    private fun setWxProgram(
        helper: BaseViewHolder,
        bean: RecommendContentBean.DataBean,
        type: Int
    ) {
        helper.setText(R.id.title, bean.title)
        helper.setText(R.id.type_name, ResourceTypeConstans.typeStringMap.get(type))
        Glide.with(context)
            .load(bean.cover_url)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_default)
            .into(helper.getView(R.id.cover));
    }


}