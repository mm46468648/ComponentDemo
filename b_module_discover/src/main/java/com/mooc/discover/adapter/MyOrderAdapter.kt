package com.mooc.discover.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.typeStringMap
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.formatPlayCount

import com.mooc.discover.model.MyOrderBean
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.mooc.discover.model.RecommendColumn
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.resource.widget.MoocImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.resource.utils.PixUtil
import java.lang.Exception
import java.lang.NumberFormatException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/*
 * 发现页我的订阅adapter
 * */
class MyOrderAdapter(data: ArrayList<MyOrderBean.ResultsBean>) :
    BaseDelegateMultiAdapter<MyOrderBean.ResultsBean, BaseViewHolder>(data), LoadMoreModule {
    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd")

    //展示资源数组（仅限数组中的资源可以展示，多余要移除）
    var displayTypes = intArrayOf(
        ResourceTypeConstans.TYPE_ARTICLE,
        ResourceTypeConstans.TYPE_COURSE,
        ResourceTypeConstans.TYPE_TRACK,
        ResourceTypeConstans.TYPE_ALBUM,
        ResourceTypeConstans.TYPE_E_BOOK,
        ResourceTypeConstans.TYPE_MICRO_LESSON,
        ResourceTypeConstans.TYPE_PERIODICAL,
        ResourceTypeConstans.TYPE_NO_IMAGE,
        ResourceTypeConstans.TYPE_COLUMN_ARTICLE,
        ResourceTypeConstans.TYPE_ONESELF_TRACK,
        ResourceTypeConstans.TYPE_STUDY_PLAN,
        ResourceTypeConstans.TYPE_MASTER_TALK,
        ResourceTypeConstans.TYPE_TEST_VOLUME,
        ResourceTypeConstans.TYPE_QUESTIONNAIRE,
        ResourceTypeConstans.TYPE_ACTIVITY,
        ResourceTypeConstans.TYPE_ACTIVITY_TASK,
        ResourceTypeConstans.TYPE_PUBLICATION,
        ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL,
        ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE,
        ResourceTypeConstans.TYPE_BATTLE,
        ResourceTypeConstans.TYPE_TASK
    )

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<MyOrderBean.ResultsBean>() {
            override fun getItemType(data: List<MyOrderBean.ResultsBean>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                val resultBean = data[position]
                val type = resultBean.type
                var typeContain = false
                for (displayType in displayTypes) {
                    if (displayType == type) {
                        typeContain = true
                        break
                    }
                }
                //不包含
                if (!typeContain) {
                    return ResourceTypeConstans.TYPE_UNDEFINE
                }
                //无图类型
                return if ((type == ResourceTypeConstans.TYPE_ARTICLE || type == ResourceTypeConstans.TYPE_COLUMN_ARTICLE)
                    && TextUtils.isEmpty(resultBean.small_image)
                ) {
                    ResourceTypeConstans.TYPE_NO_IMAGE
                } else type
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
            ?.addItemType(ResourceTypeConstans.TYPE_ARTICLE, R.layout.item_my_order_article)
            ?.addItemType(ResourceTypeConstans.TYPE_COLUMN_ARTICLE, R.layout.item_my_order_article)
            ?.addItemType(ResourceTypeConstans.TYPE_COURSE, R.layout.item_my_order_course)
            ?.addItemType(ResourceTypeConstans.TYPE_TRACK, R.layout.item_my_order_track)
            ?.addItemType(ResourceTypeConstans.TYPE_ALBUM, R.layout.item_my_order_album)
            ?.addItemType(ResourceTypeConstans.TYPE_E_BOOK, R.layout.item_my_order_e_book)
            ?.addItemType(
                ResourceTypeConstans.TYPE_MICRO_LESSON,
                R.layout.item_my_order_micro_course
            )
            ?.addItemType(ResourceTypeConstans.TYPE_PERIODICAL, R.layout.item_my_order_period)
            ?.addItemType(ResourceTypeConstans.TYPE_NO_IMAGE, R.layout.item_my_order_no_img)
            ?.addItemType(
                ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE,
                R.layout.item_order_followup_resource
            )
            ?.addItemType(ResourceTypeConstans.TYPE_ONESELF_TRACK, R.layout.item_my_order_track)
            ?.addItemType(ResourceTypeConstans.TYPE_STUDY_PLAN, R.layout.item_my_order_study_plan)
            ?.addItemType(ResourceTypeConstans.TYPE_MASTER_TALK, R.layout.item_my_order_master)
            ?.addItemType(ResourceTypeConstans.TYPE_UNDEFINE, R.layout.item_recommend_empty)
            ?.addItemType(ResourceTypeConstans.TYPE_ACTIVITY_TASK, R.layout.item_my_order_activity)
            ?.addItemType(ResourceTypeConstans.TYPE_ACTIVITY, R.layout.item_my_order_activity)
            ?.addItemType(ResourceTypeConstans.TYPE_BATTLE, R.layout.item_my_order_battle)
            ?.addItemType(
                ResourceTypeConstans.TYPE_TEST_VOLUME,
                R.layout.item_my_order_test_question
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_QUESTIONNAIRE,
                R.layout.item_my_order_test_question
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_PUBLICATION,
                R.layout.item_my_order_publication_one
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL,
                R.layout.item_order_micro_professional
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE,
                R.layout.item_order_micro_professional
            )
            ?.addItemType(ResourceTypeConstans.TYPE_TASK, R.layout.item_my_order_task)
    }

    protected override fun convert(helper: BaseViewHolder, item: MyOrderBean.ResultsBean) {
        val dataBean = item.data[0] ?: return
        if (helper.itemViewType === ResourceTypeConstans.TYPE_UNDEFINE) {
            return
        }

        //点击进入专栏或者专题
        if (helper.getViewOrNull<View>(R.id.column_name) != null) {
            helper.getView<View>(R.id.column_name).setOnClickListener { v: View? ->
                if (item.parent_type == ResourceTypeConstans.TYPE_SPECIAL) {
                    ARouter.getInstance().build(Paths.PAGE_RECOMMEND_SPECIAL)
                        .withString(
                            IntentParamsConstants.PARAMS_RESOURCE_ID,
                            item.parent_id.toString()
                        )
                        .navigation()
                }
                if (item.parent_type == ResourceTypeConstans.TYPE_COLUMN) {
                    ARouter.getInstance().build(Paths.PAGE_COLUMN_LIST).withString(
                        IntentParamsConstants.COLUMN_PARAMS_ID,
                        item.parent_id.toString()
                    ).navigation()
                }
            }
        }
        val typeStr = typeStringMap[item.type]
//        helper.setText(R.id.tag, typeStr)
//        helper.setGone(R.id.tag, TextUtils.isEmpty(typeStr))
        val tagText = helper.getViewOrNull<TextView>(R.id.tag)
        if(tagText != null){
            tagText.setText(typeStr)
            tagText.visibility = if(TextUtils.isEmpty(typeStr))View.GONE else View.VISIBLE
        }
        when (helper.itemViewType) {
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {
                helper.setText(R.id.tag, "文章")
                val imageArticle = helper.getView<MoocImageView>(R.id.cover)

//                Glide.with(getContext()).load(dataBean.getCover_url()).placeholder(R.mipmap.common_bg_cover_default).into(imageArticle);
                imageArticle.setImageUrl(dataBean.cover_url, 2)
                helper.setText(R.id.title, dataBean.title)
                if (TextUtils.isEmpty(dataBean.staff)) {
                    helper.setText(R.id.orgs, dataBean.platform_zh)
                } else {
                    helper.setText(R.id.orgs, dataBean.platform_zh + " | " + dataBean.staff)
                }
                helper.setText(R.id.column_name, dataBean.parent_name)
                helper.setText(R.id.before_time, dataBean.publish_time)
            }
            ResourceTypeConstans.TYPE_NO_IMAGE -> {
                helper.setGone(R.id.tag, false)
                helper.setText(R.id.tag, "文章")
                helper.setText(R.id.title, dataBean.title)
                val orgTv = helper.getView<TextView>(R.id.orgs)
                if (TextUtils.isEmpty(dataBean.staff)) {
                    orgTv.text = dataBean.platform_zh
                } else {
                    if (TextUtils.isEmpty(dataBean.platform_zh)) {
                        orgTv.text = dataBean.staff
                    } else {
                        orgTv.text = dataBean.platform_zh + " | " + dataBean.staff
                    }
                }
                if (TextUtils.isEmpty(orgTv.text.toString().trim { it <= ' ' })) {
                    orgTv.visibility = View.GONE
                } else {
                    orgTv.visibility = View.VISIBLE
                }
                helper.setText(R.id.column_name, dataBean.parent_name)
                helper.setText(R.id.time, dataBean.publish_time)
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                val imageView = helper.getView<MoocImageView>(R.id.cover)

//                Glide.with(getContext()).load(dataBean.getCover_url()).placeholder(R.mipmap.common_bg_cover_default).into(imageView);
                imageView.setImageUrl(dataBean.cover_url, 2)

                //设置type
                helper.setText(R.id.tag, R.string.course)
                helper.setText(R.id.tvTypeOrderCourse, R.string.course)
                if (!TextUtils.isEmpty(dataBean.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    helper.setText(R.id.tvTypeOrderCourse, dataBean.identity_name)
                    helper.setGone(R.id.tvTypeOrderCourse, false)
                    helper.setGone(R.id.tag, true)
                } else {
                    helper.setGone(R.id.tvTypeOrderCourse, true)
                    helper.setGone(R.id.tag, false)
                }
                helper.setText(R.id.title, dataBean.title)
                helper.setText(R.id.orgs, dataBean.platform_zh)
                helper.setText(R.id.column_name, dataBean.parent_name)
                helper.setText(R.id.before_time, dataBean.start_time)
                setResourceType(
                    helper,
                    dataBean,
                    helper.getView(R.id.tvExamType),
                    helper.getView(R.id.tvCertificateType),
                    helper.getView(
                        R.id.tvPriceType
                    )
                )
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                val imageMicro = helper.getView<MoocImageView>(R.id.cover)
                val tvSource = helper.getView<TextView>(R.id.orgs)

//                Glide.with(getContext()).load(dataBean.getCover_url()).placeholder(R.mipmap.common_bg_cover_default).into(imageMicro);
                imageMicro.setImageUrl(dataBean.cover_url, 2)
                helper.setText(
                    R.id.tag,
                    context.resources.getString(R.string.recommend_micro_lesson)
                )
                helper.setText(R.id.title, dataBean.title)
                tvSource.text = dataBean.source
                helper.setText(R.id.column_name, dataBean.parent_name)
                if (!TextUtils.isEmpty(dataBean.video_duration)) {
                    val video_duration: Long
                    video_duration = try {
                        dataBean.video_duration.toLong()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    helper.setText(R.id.before_time, TimeUtils.timeParse(video_duration))
                }
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                helper.setGone(R.id.tag, true)
                val cover = helper.getView<MoocImageView>(R.id.cover)
                val tvOrg = helper.getView<TextView>(R.id.orgs)


//                Glide.with(getContext()).load(dataBean.getCover_url()).placeholder(R.mipmap.common_bg_cover_default).into(cover);
                cover.setImageUrl(dataBean.cover_url, 2)
                helper.setText(R.id.title, dataBean.title)
                helper.setText(R.id.column_name, dataBean.parent_name)
                if (TextUtils.isEmpty(dataBean.staff)) {
                    helper.setText(R.id.orgs, "发起人：无")
                } else {
                    helper.setText(R.id.orgs, String.format("发起人：%s等", dataBean.staff))
                }
                if ("1" == dataBean.time_mode) {
                    helper.setText(
                        R.id.before_time,
                        context.resources.getString(R.string.study_time_permanent_opening)
                    )
                } else {
                    helper.setText(
                        R.id.before_time,
                        dataBean.plan_starttime + "－" + dataBean.plan_endtime
                    )
                }
                helper.setText(R.id.num, dataBean.student_num.toString() + "人")
                var start: Date? = null
                var end: Date? = null
                var join_start: Date? = null
                var join_end: Date? = null
                try {
                    start = simpleDateFormat.parse(dataBean.plan_starttime)
                    end = simpleDateFormat.parse(dataBean.plan_endtime)
                    join_start = simpleDateFormat.parse(dataBean.join_start_time)
                    join_end = simpleDateFormat.parse(dataBean.join_end_time)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                if (start != null && end != null && join_start != null && join_end != null) {
                    helper.setGone(R.id.state, false)
                    when (isEnrolment(TimeUtils.getCurrentTime(), start.time, end.time)) {
                        0 -> {
                            when (isEnrolment(
                                TimeUtils.getCurrentTime(),
                                join_start.time,
                                join_end.time
                            )) {
                                0 -> helper.setGone(
                                    R.id.state, true
                                )
                                1 -> helper.setText(R.id.state, "报名中")
                                2 -> helper.setText(R.id.state, "进行中")
                            }
                            helper.setBackgroundResource(R.id.state, R.drawable.bg_plan_light_green)
                        }
                        1 -> {
                            helper.setText(R.id.state, "进行中")
                            helper.setBackgroundResource(R.id.state, R.drawable.bg_plan_light_green)
                        }
                        2 -> {
                            helper.setText(R.id.state, "已结束")
                            helper.setBackgroundResource(
                                R.id.state,
                                R.drawable.shape_color9e0_corners2
                            )
                        }
                    }
                }
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                helper.setText(R.id.tag, "音频")
                if (dataBean.track_data != null) {
                    val (_, _, _, _, _, track_title, play_count, cover_url_middle, _, duration) = dataBean.track_data
                    val moocImageView = helper.getView<MoocImageView>(R.id.cover)
                    //                    Glide.with(getContext()).load(track.getCoverUrlMiddle()).placeholder(R.mipmap.common_bg_cover_default).into((ImageView) view);
                    moocImageView.setImageUrl(cover_url_middle, 2)
                    helper.setText(R.id.title, track_title)
                    helper.setText(R.id.platform, "喜马拉雅")
                    helper.setText(
                        R.id.count, formatPlayCount(
                            play_count
                        )
                    )
                    helper.setText(
                        R.id.time, TimeUtils.timeParse(
                            duration
                        )
                    )
                    helper.setText(R.id.column_name, item.title)
                    helper.setText(R.id.before_time, dataBean.publish_time)
                }
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                val musicBean = dataBean.audio_data
                val moocImageView = helper.getView<MoocImageView>(R.id.cover)
                //                Glide.with(getContext()).load(musicBean.getAudio_bg_img()).placeholder(R.mipmap.common_bg_cover_default).into((ImageView) view);
                moocImageView.setImageUrl(musicBean.audio_bg_img, 2)
                helper.setText(R.id.tag, "自建音频")
                helper.setText(R.id.title, dataBean.title)
                helper.setText(R.id.count, formatPlayCount(musicBean.audio_play_num))
                helper.setText(R.id.time, TimeUtils.timeParse(musicBean.audio_time))
                helper.setText(R.id.before_time, dataBean.publish_time)
                helper.setText(R.id.column_name, dataBean.parent_name)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                helper.setText(R.id.tag, "音频课")
                if (dataBean.album_data != null) {
                    val album = dataBean.album_data
                    val moocImageView1 = helper.getView<MoocImageView>(R.id.cover)
                    //                    Glide.with(getContext()).load(album.getCover_url_middle()).placeholder(R.mipmap.common_bg_cover_default).into((ImageView) view);
                    moocImageView1.setImageUrl(album.cover_url_middle, 2)
                    helper.setText(R.id.title, album.album_title)
                    helper.setText(R.id.platform, dataBean.platform_zh)
                    helper.setText(R.id.count, formatPlayCount(album.play_count.toInt()))
                    helper.setText(R.id.num, album.include_track_count.toString() + "集")
                    helper.setText(R.id.column_name, item.title)
                    helper.setText(R.id.before_time, dataBean.publish_time)
                }
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                helper.setText(R.id.tag, "电子书")
                val moocImageView1 = helper.getView<MoocImageView>(R.id.cover)
                //                Glide.with(getContext()).load(dataBean.getCover_url()).placeholder(R.mipmap.common_bg_cover_default).into((ImageView) view);
                moocImageView1.setImageUrl(dataBean.cover_url, 2)
                helper.setText(R.id.title, dataBean.title)
                if (TextUtils.isEmpty(dataBean.writer)) {
                    helper.setGone(R.id.platform, false)
                } else {
                    helper.setGone(R.id.platform, true)
                    helper.setText(R.id.platform, dataBean.writer)
                }
                if (TextUtils.isEmpty(dataBean.press)) {
                    helper.setText(R.id.orgs, dataBean.platform_zh)
                } else {
                    if (TextUtils.isEmpty(dataBean.press)) {
                        helper.setText(R.id.orgs, dataBean.platform_zh)
                    } else {
                        helper.setText(R.id.orgs, dataBean.platform_zh + " | " + dataBean.press)
                    }
                }
                if (dataBean.word_count != 0L) {
                    helper.setText(R.id.num, formatPlayCount(dataBean.word_count.toInt()) + "字")
                }
                helper.setText(R.id.column_name, dataBean.parent_name)
                helper.setText(R.id.before_time, dataBean.publish_time)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                helper.setText(R.id.tag, "期刊")
                val imagePer = helper.getView<MoocImageView>(R.id.cover)

//                Glide.with(getContext()).load(dataBean.getCover_url()).placeholder(R.mipmap.common_bg_cover_default).into(imagePer);
                imagePer.setImageUrl(dataBean.cover_url, 2)
                helper.setText(R.id.title, dataBean.title)
                helper.setText(R.id.orgs, dataBean.staff)
                helper.setText(R.id.des, dataBean.basic_date_time)
                helper.setGone(R.id.before_time, false)
                helper.setGone(R.id.platform, false)
                helper.setText(R.id.column_name, dataBean.parent_name)
                helper.setText(R.id.time, dataBean.publish_time)
            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {
                helper.setText(R.id.tag, "大师课")
                val moocImageView3 = helper.getView<MoocImageView>(R.id.cover)
                //                Glide.with(getContext()).load(dataBean.getCover_url()).placeholder(R.mipmap.common_bg_cover_default).into((ImageView) moocImageView);
                moocImageView3.setImageUrl(dataBean.cover_url, 2)
                helper.setText(R.id.title, dataBean.title)
                val masterOrderBean = dataBean.master_info
                if (masterOrderBean.author_info != null) {
                    if (!TextUtils.isEmpty(masterOrderBean.author_info!!.name)) {
                        helper.setText(R.id.about, dataBean.master_info.author_info!!.name)
                    }
                }
                helper.setText(R.id.column_name, dataBean.parent_name)
                helper.setText(R.id.price, "¥" + dataBean.price.toInt())
                helper.setText(R.id.desc, dataBean.desc)
            }
            ResourceTypeConstans.TYPE_DEFAULT -> {
                helper.setText(R.id.tag, typeStringMap[item.type])
                val cover1 = helper.getView<ImageView>(R.id.cover)
                Glide.with(context).load(dataBean.cover_url)
                    .placeholder(R.mipmap.common_bg_cover_default).into(cover1)
                helper.setText(R.id.title, dataBean.title)
            }
            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
                val taskBean = dataBean.event_task
                helper.setText(R.id.tag, typeStringMap[item.type])
                helper.setText(R.id.title, dataBean.title)
                val activityCover = helper.getView<MoocImageView>(R.id.cover)

//                Glide.with(getContext()).load(dataBean.getCover_url()).placeholder(R.mipmap.common_bg_cover_default).into(activityCover);
                activityCover.setImageUrl(dataBean.cover_url, 2)
                helper.setText(
                    R.id.orgs,
                    simpleDateFormat.format(Date(taskBean.apply_start_time * 1000)) + "-" + simpleDateFormat.format(
                        Date(taskBean.apply_end_time * 1000)
                    )
                )
                helper.setText(R.id.column_name, dataBean.parent_name)
                helper.setText(R.id.time, dataBean.publish_time)
            }
            ResourceTypeConstans.TYPE_TEST_VOLUME, ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
                helper.setText(R.id.tag, typeStringMap[item.type])
                if (TextUtils.isEmpty(dataBean.start_time) || TextUtils.isEmpty(dataBean.end_time)) {
                    helper.setGone(R.id.orgs, true)
                } else {
                    helper.setGone(R.id.orgs, false)
                    helper.setText(R.id.orgs, dataBean.start_time + "-" + dataBean.end_time)
                }
                helper.setText(R.id.title, dataBean.title)
                helper.setText(R.id.column_name, dataBean.parent_name)
                helper.setText(R.id.time, dataBean.publish_time)
            }
            ResourceTypeConstans.TYPE_BATTLE->{
                helper.setText(R.id.title, dataBean.title)
                helper.setText(R.id.column_name, dataBean.parent_name)
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                helper.setText(R.id.tag, typeStringMap[item.type])
                val imagePublication = helper.getView<MoocImageView>(R.id.cover)
                imagePublication.setImageUrl(dataBean.cover_url, 2)
                helper.setText(R.id.title, dataBean.title)
                val updateStr =
                    "更新至" + dataBean.periodicals_data.year + "年 第" + dataBean.periodicals_data.term + "期"
                helper.setText(R.id.device, updateStr)
                helper.setText(R.id.subtitle, dataBean.periodicals_data.unit)
                helper.setText(R.id.column_name, dataBean.periodicals_data.magname)
                helper.setText(R.id.timer, dataBean.publish_time)
            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL, ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {
                helper.setText(R.id.title, dataBean.title)
                helper.setText(R.id.orgs, dataBean.resource_info?.title)
                helper.setText(R.id.column_name, dataBean.parent_name)
                helper.setText(R.id.time, dataBean.resource_info?.updated_time)
                val imagePublication = helper.getView<ImageView>(R.id.cover)
                Glide.with(context).load(dataBean.cover_url).transform(RoundedCorners(
                    2.dp2px())).into(imagePublication)
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                helper.setText(R.id.title, dataBean.title)
                helper.setGone(R.id.column_name, false)
                helper.setText(R.id.column_name, dataBean.parent_name)
            }
            ResourceTypeConstans.TYPE_TASK -> {
                helper.setText(R.id.tvTitleTask, item.title)

                var peoNum = "${dataBean.join_num}人参与"
                if (dataBean.task_data.is_limit_num) { //限制人数
                    peoNum = "${dataBean.join_num}/${dataBean.task_data.limit_num}人参与"
                }
                helper.setText(R.id.tvPeopleNumTask, peoNum)
                val image: String =
                    if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
                Glide.with(context)
                    .load(image)
                    .error(R.mipmap.common_bg_cover_default)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(2f.dp2px())))
                    .into(helper.getView(R.id.ivImgTask))


                if (dataBean.time_mode == "1") {
                    helper.setText(R.id.tvTimeTask, "任务时间：永久开放")
                    helper.setText(R.id.tvGetTimeTask, "领取时间：永久开放")
                } else {
                    helper.setText(
                        R.id.tvTimeTask,
                        "任务时间：" + dataBean.task_start_date + "-" + dataBean.task_end_date
                    )
                    if (dataBean.start_time.isEmpty() && dataBean.end_time.isEmpty()) {
                        helper.setText(R.id.tvGetTimeTask, "永久开放")
                    } else {
                        helper.setText(R.id.tvGetTimeTask, "领取时间：" + dataBean.start_time + "-" + dataBean.end_time)
                    }
                }

                if (!TextUtils.isEmpty(dataBean.task_data.score?.success_score)) {
                    helper.setGone(R.id.tvRewardTask, false)
                    helper.setGone(R.id.tvRewardScoreTask, false)
                    helper.setText(R.id.tvRewardScoreTask, dataBean.task_data.score?.success_score)
                } else {
                    helper.setGone(R.id.tvRewardTask, true)
                    helper.setGone(R.id.tvRewardScoreTask, true)
                }

                helper.setBackgroundColor(R.id.tvStatusTask, Color.TRANSPARENT)
                val buttonText = helper.getViewOrNull<TextView>(R.id.tvStatusTask)
                buttonText?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15F)

                helper.getViewOrNull<TextView>(R.id.tvStatusTask)
                    ?.setCompoundDrawables(null, null, null, null)


                // 1进行中
//            2已完成
//            3失败
//            4未领取已过期
//            5 未开始领取
//            6 任务已报名未开始 （新增）
                when (dataBean.task_data.status) {
                    TaskConstants.TASK_STATUS_DOING -> {//进行中
                        helper.setText(R.id.tvStatusTask, "进行中")
                        helper.setTextColor(
                            R.id.tvStatusTask,
                            ContextCompat.getColor(context, R.color.color_10955B)
                        )

                    }
                    TaskConstants.TASK_STATUS_SUCCESS -> {// 任务成功
                        helper.setText(R.id.tvStatusTask, "已完成")// 加了一个图片
                        helper.setTextColor(
                            R.id.tvStatusTask,
                            ContextCompat.getColor(context, R.color.color_white)
                        )
                        val drawable: Drawable? =
                            ContextCompat.getDrawable(context, R.mipmap.common_icon_task_finish)
                        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                        helper.getViewOrNull<TextView>(R.id.tvStatusTask)
                            ?.setCompoundDrawables(drawable, null, null, null)
                    }
                    TaskConstants.TASK_STATUS_FAIL -> {// 任务失败
                        helper.setText(R.id.tvStatusTask, "任务失败")
                        helper.setTextColor(
                            R.id.tvStatusTask,
                            ContextCompat.getColor(context, R.color.color_F35600)
                        )

                    }
                    TaskConstants.TASK_STATUS_EXPIRED -> {//已过期
                        helper.setText(R.id.tvStatusTask, "已过期")
                        helper.setTextColor(
                            R.id.tvStatusTask,
                            ContextCompat.getColor(context, R.color.color_white)
                        )
                    }
                    TaskConstants.TASK_STATUS_CANNOT_GET -> {//未开始领取
                        helper.setText(R.id.tvStatusTask, "领取未开始")
                        helper.setTextColor(
                            R.id.tvStatusTask,
                            ContextCompat.getColor(context, R.color.color_white)
                        )
                    }
                    TaskConstants.TASK_STATUS_UNSTART -> {//任务已报名未开始
                        helper.setText(R.id.tvStatusTask, "任务未开始")
                        helper.setTextColor(
                            R.id.tvStatusTask,
                            ContextCompat.getColor(context, R.color.color_white)
                        )
                    }
                    else -> {
                        helper.setText(R.id.tvStatusTask, "查看详情")
                        helper.setBackgroundResource(
                            R.id.tvStatusTask,
                            R.drawable.shape_radius15_color_primary
                        )
                        helper.setTextColor(
                            R.id.tvStatusTask,
                            ContextCompat.getColor(context, R.color.color_white)
                        )
                        buttonText?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13F)

                    }
                }
            }
        }
    }

    private fun sp2px(spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return spValue * fontScale + 0.5f
    }

    private fun setTextData(id: Int, helper: BaseViewHolder, str: String) {
        if (TextUtils.isEmpty(str)) {
            helper.setGone(id, false)
        } else {
            helper.setGone(id, true)
            helper.setText(id, str)
        }
    }

    private fun isEnrolment(time: Long, startTime: Long, stopTime: Long): Int {
        return if (startTime > 0 && stopTime > 0) {
            if (time < startTime) { //尚未到报名期
                0
            } else {
                if (time <= stopTime) { //报名期
                    1
                } else { //报名期结束
                    2
                }
            }
        } else {
            3
        }
    }

    private fun setResourceType(
        holder: BaseViewHolder,
        bean: RecommendColumn,
        tvExamType: TextView,
        tvCerType: TextView,
        tvPriceType: TextView
    ) {
        val isHaveExam = bean.is_have_exam
        val isVerified = bean.verified_active
        val isFree = bean.is_free
        val platform: Int
        platform = try {
            bean.platform.toInt()
        } catch (e: Exception) {
            -1
        }
        tvExamType.visibility = View.VISIBLE
        tvCerType.visibility = View.VISIBLE
        tvPriceType.visibility = View.VISIBLE
        if (isHaveExam == "1" || isHaveExam == "true") {
            tvExamType.text = context.resources.getString(R.string.course_str_have_exam)
            tvExamType.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            tvExamType.text = context.resources.getString(R.string.course_str_no_exam)
            tvExamType.setTextColor(context.resources.getColor(R.color.color_636363))
        }
        if (!TextUtils.isEmpty(bean.is_have_exam_info)) {
            tvExamType.text = bean.is_have_exam_info
        }
        val dart = context.getString(R.string.point)
        if (isVerified == "1" || isVerified == "true") {
            tvCerType.text = String.format(
                "%s%s%s",
                dart,
                context.resources.getString(R.string.course_str_have_certificate),
                dart
            )
            tvCerType.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            tvCerType.text = String.format(
                "%s%s%s",
                dart,
                context.resources.getString(R.string.course_str_no_certificate),
                dart
            )
            tvCerType.setTextColor(context.resources.getColor(R.color.color_636363))
        }
        if (!TextUtils.isEmpty(bean.verified_active_info)) {
            if (!TextUtils.isEmpty(bean.is_have_exam_info)
                && !TextUtils.isEmpty(bean.is_free_info)
            ) {
                tvCerType.text = String.format("%s%s%s", dart, bean.verified_active_info, dart)
            }
            if (!TextUtils.isEmpty(bean.is_have_exam_info)
                && TextUtils.isEmpty(bean.is_free_info)
            ) {
                tvCerType.text = String.format("%s%s", dart, bean.verified_active_info)
            }
            if (TextUtils.isEmpty(bean.is_have_exam_info)
                && !TextUtils.isEmpty(bean.is_free_info)
            ) {
                tvCerType.text = String.format("%s%s", bean.verified_active_info, dart)
            }
            if (TextUtils.isEmpty(bean.is_have_exam_info)
                && TextUtils.isEmpty(bean.is_free_info)
            ) {
                tvCerType.text = bean.verified_active_info
            }
        }
        if (isFree == "1" || isFree == "true") { //免费
            tvPriceType.text = context.resources.getString(R.string.course_str_free)
            tvPriceType.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            tvPriceType.text = context.resources.getString(R.string.course_str_pay)
            tvPriceType.setTextColor(context.resources.getColor(R.color.color_636363))
        }
        if (!TextUtils.isEmpty(bean.is_free_info)) {
            tvPriceType.text = bean.is_free_info
        }
    }

    private fun updateRead(dataBean: RecommendColumn) {
//        RequestUtil.getUserApi().updateRecommendRead(String.valueOf(dataBean.getId())).compose(RxTimeUtils.<HttpResponse>applySchedulers())
//                .subscribe(new Observer<HttpResponse>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(HttpResponse httpResponse) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }
}