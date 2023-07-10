package com.mooc.discover.adapter

import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.sp2px
import com.mooc.common.utils.TimeUtils.timeParse
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.typeStringMap
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.formatPlayCount
import com.mooc.discover.R
import com.mooc.discover.model.MusicBean
import com.mooc.discover.model.RecommendContentBean

/**
发现推荐专题样式
 * @Author limeng
 * @Date 2020/11/18-6:25 PM
 */
class RecomSpecialListAdapter2(data: ArrayList<RecommendContentBean.DataBean>?) :
    BaseDelegateMultiAdapter<RecommendContentBean.DataBean, BaseViewHolder>(data), LoadMoreModule {

    companion object {
        const val TYPE_RECOMMEND_STYLE_ONE = 0 //列表样式一 对应设计稿样式一
        const val TYPE_RECOMMEND_STYLE_TWO = 1//列表样式二  对应设计稿样式二
        const val TYPE_RECOMMEND_STYLE_THREE = 2//列表样式三 对应设计稿样式三
        const val TYPE_RECOMMEND_STYLE_FOUR = 3//列表样式四  对应设计稿样式四
        const val TYPE_RECOMMEND_STYLE_FIVE = 4//列表样式五   对应设计稿样式五
        const val TYPE_RECOMMEND_STYLE_NO_IMAGE = 5//列表样式无图  对应设计稿样式八
        const val TYPE_RECOMMEND_STYLE_SIX = 6//列表样式六   对应设计稿样式六
        const val TYPE_RECOMMEND_STYLE_SEVEN = 7//列表模板样式七   对应设计稿样式七
    }

    val typeArray = 0..7    //支持的模板区间

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<RecommendContentBean.DataBean>() {
            override fun getItemType(
                data: List<RecommendContentBean.DataBean>,
                position: Int
            ): Int {
                val dataBean = data[position]
                return if (dataBean.classType in typeArray) dataBean.classType else TYPE_RECOMMEND_STYLE_NO_IMAGE
            }
        })
        // //两个类型，课程和文章 （有图没有图的）
        getMultiTypeDelegate()
            ?.addItemType(TYPE_RECOMMEND_STYLE_ONE, R.layout.item_recommend_style_one)
            ?.addItemType(TYPE_RECOMMEND_STYLE_TWO, R.layout.item_recommend_style_two)
            ?.addItemType(TYPE_RECOMMEND_STYLE_THREE, R.layout.item_recommend_style_three)
            ?.addItemType(TYPE_RECOMMEND_STYLE_FOUR, R.layout.item_recommend_style_four)
            ?.addItemType(TYPE_RECOMMEND_STYLE_FIVE, R.layout.item_recommend_style_five)
            ?.addItemType(TYPE_RECOMMEND_STYLE_NO_IMAGE, R.layout.item_recommend_style_no_image)
            ?.addItemType(TYPE_RECOMMEND_STYLE_SIX, R.layout.item_recommend_style_six)
            ?.addItemType(TYPE_RECOMMEND_STYLE_SEVEN, R.layout.item_recommend_style_seven)
    }


    override fun convert(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        when (holder.itemViewType) {
            TYPE_RECOMMEND_STYLE_ONE -> {
                setOneData(holder, item)
            }
            TYPE_RECOMMEND_STYLE_TWO -> {
                setTwoData(holder, item)
            }

            TYPE_RECOMMEND_STYLE_THREE -> {
                setThreeData(holder, item)
            }
            TYPE_RECOMMEND_STYLE_FOUR -> {
                setFourData(holder, item)
            }
            TYPE_RECOMMEND_STYLE_FIVE -> {
                setFiveData(holder, item)
            }
            TYPE_RECOMMEND_STYLE_NO_IMAGE -> {
                setNoImageData(holder, item)
            }
            TYPE_RECOMMEND_STYLE_SIX -> {
                setSixData(holder, item)
            }
            TYPE_RECOMMEND_STYLE_SEVEN -> {
                setSevenData(holder, item)
            }
        }
    }

    /**
     * 任务时间
     */
    private fun getTaskTime(dataBean: RecommendContentBean.DataBean): String {
        val taskTime: String = if (dataBean.time_mode.equals("1")) {
            "任务时间：永久开放"
        } else {
            "任务时间：" + dataBean.task_start_date + "-" + dataBean.task_end_date
        }
        return taskTime
    }

    /**
     * 任务报名时间
     */
    private fun getEnrollTime(dataBean: RecommendContentBean.DataBean): String {
        val enrollTime: String = if (dataBean.time_mode.equals("1")) {
            "报名时间：永久开放"
        } else {
            if (dataBean.start_time.isEmpty() && dataBean.end_time.isEmpty()) {
                "永久开放"
            } else {
                "报名时间：" + dataBean.start_time + "-" + dataBean.end_time
            }
        }
        return enrollTime
    }

    private fun getSpaString(
        success_score: String,
        startColor: Int,
        endColor: Int,
        startSize: Int,
        endSize: Int
    ): SpannableString {
        val scoreStr: String
        if (TextUtils.isEmpty(success_score)) {
            scoreStr = "奖励积分 0"
        } else {
            scoreStr = "奖励积分 " + success_score
        }
        val spannableString = spannableString {
            str = scoreStr
            colorSpan {
                color = startColor
                start = 0
                end = 4
            }
            colorSpan {
                color = endColor
                start = 4
                end = scoreStr.length
            }
            absoluteSpan {
                size = startSize
                start = 0
                end = 4
            }
            absoluteSpan {
                size = endSize
                start = 4
                end = scoreStr.length
            }
        }
        return spannableString
    }

    private fun setOneData(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_default)
            .placeholder(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into(holder.getView(R.id.iv_recommend_style_one))

        holder.setText(R.id.tv_title_style_one_resource, item.title)
        holder.setText(R.id.tv_bottom_left_style_one_resource, "")

        holder.setGone(R.id.ivPlay, true)
        holder.setGone(R.id.tv_reasons_one, true)
        holder.setGone(R.id.tv_bottom_right_style_one_resource, true)//微课显示，其余隐藏

        val value = typeStringMap[item.type]
        holder.setText(R.id.tv_type_one, value)
        holder.setGone(R.id.tv_type_one, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeListOne, value)
        holder.setGone(R.id.tvTypeListOne, true)

        when (item.type) {

            ResourceTypeConstans.TYPE_TASK -> {
                holder.setText(
                    R.id.tv_center_style_one_resource,
                    getSpaString(
                        item.success_score,
                        ContextCompat.getColor(context, R.color.color_5D5D5D),
                        ContextCompat.getColor(context, R.color.color_5D5D5D),
                        13.sp2px(),
                        15.sp2px()
                    )
                )

                holder.setText(
                    R.id.tv_bottom_left_style_one_resource,
                    (if (item.join_num.isNullOrEmpty()) "0" else item.join_num) + "人参与"
                )
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                holder.setText(R.id.tv_type_one, R.string.publicaton)
                holder.setText(
                    R.id.tv_center_style_one_resource,
                    "更新至" + item.periodicals_data.year + "年" + "第" + item.periodicals_data.term + "期"
                )
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.periodicals_data.unit)
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tvTypeListOne, item.identity_name)
                    holder.setGone(R.id.tvTypeListOne, false)
                    holder.setGone(R.id.tv_type_one, true)
                } else {
                    holder.setGone(R.id.tvTypeListOne, true)
                    holder.setGone(R.id.tv_type_one, false)
                }
                holder.setText(R.id.tv_center_style_one_resource, item.org)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.start_time)
            }
            ResourceTypeConstans.TYPE_KNOWLEDGE,
            ResourceTypeConstans.TYPE_MICRO_LESSON,
            -> {
                holder.setText(R.id.tv_type_one, R.string.micro_course)
                holder.setText(R.id.tv_center_style_one_resource, item.org)//开课单位
                if (!item.video_duration.isNullOrEmpty()) {
                    val videoDuration: Long = try {
                        item.video_duration.toLong()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    holder.setGone(R.id.tv_bottom_right_style_one_resource, false)//微课显示，其余隐藏
                    holder.setText(
                        R.id.tv_bottom_right_style_one_resource,
                        timeParse(videoDuration)
                    )
                }

                holder.setText(R.id.tv_bottom_left_style_one_resource, item.source)//来源
            }

            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.tv_type_one, R.string.ebook)
                holder.setText(R.id.tv_center_style_one_resource, item.writer)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.source)
            }

            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setText(R.id.tv_type_one, R.string.album)
                holder.setGone(R.id.ivPlay, false)
                val album = item.album_data
                holder.setText(
                    R.id.tv_center_style_one_resource,
                    "${album?.track_count ?: 0}集"
                )
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setText(R.id.tv_type_one, R.string.track)
                holder.setGone(R.id.ivPlay, false)
                val track = item.track_data
                holder.setText(
                    R.id.tv_center_style_one_resource,
                    track.subordinated_album?.album_title ?: ""
                )
                holder.setText(R.id.tv_bottom_left_style_one_resource, timeParse(track.duration))
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.tv_type_one, R.string.periodical)
                holder.setText(R.id.tv_center_style_one_resource, item.staff)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.source)

            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {//专栏文章
                holder.setText(R.id.tv_type_one, R.string.article)
                holder.setText(R.id.tv_center_style_one_resource, item.staff)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.source)
            }

            ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setText(R.id.tv_type_one, R.string.article)
                holder.setText(R.id.tv_center_style_one_resource, item.staff)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.source)
            }
            ResourceTypeConstans.TYPE_SPECIAL -> {//合集
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tv_type_one, item.identity_name)
                } else {
                    holder.setText(R.id.tv_type_one, R.string.special)
                }
                if (!TextUtils.isEmpty(item.resource_info?.title)) {
                    holder.setText(R.id.tv_center_style_one_resource, item.resource_info?.title)
                }
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(
                        R.id.tv_bottom_left_style_one_resource,
                        item.resource_info?.updated_time + "更新"
                    )
                }

            }
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {//外部链接
//                holder.setGone(R.id.tv_type_one, true)
                holder.setText(R.id.tv_center_style_one_resource, "")

            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读
                holder.setGone(R.id.tv_type_one, true)
                holder.setText(R.id.tv_center_style_one_resource, "")

            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setText(R.id.tv_type_one, R.string.study_plan)
                holder.setText(R.id.tv_center_style_one_resource, item.staff)
                holder.setText(R.id.tv_bottom_left_style_one_resource, "${item.student_num}人")
            }
            ResourceTypeConstans.TYPE_COLUMN -> {//专栏
                holder.setText(R.id.tv_type_one, R.string.column)
                holder.setText(R.id.tv_center_style_one_resource, item.desc)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.update_time)
            }
            ResourceTypeConstans.TYPE_ACTIVITY -> {//活动
                holder.setText(R.id.tv_type_one, R.string.activity)
                holder.setText(R.id.tv_center_style_one_resource, item.about)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.update_time)
            }
            ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {////活动任务
                holder.setText(R.id.tv_type_one, R.string.activity_task)
                holder.setText(R.id.tv_center_style_one_resource, item.about)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.update_time)
            }
            ResourceTypeConstans.TYPE_BAIKE -> {//
                holder.setText(R.id.tv_type_one, R.string.baike)
                holder.setText(R.id.tv_center_style_one_resource, item.source)

            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {//大师课
                holder.setText(R.id.tv_type_one, R.string.master_talk)
                if (item.price > 0) {
                    val p: Float = item.getPrice() / 100
                    val price = String.format(context.getResources().getString(R.string.text_f), p)
                    holder.setText(
                        R.id.tv_center_style_one_resource, String.format(
                            context.getResources()
                                .getString(R.string.text_price_master), price
                        )
                    )

                } else {
                    holder.setText(R.id.tv_center_style_one_resource, "")
                }
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.getDesc())
            }
            ResourceTypeConstans.TYPE_TEST_VOLUME -> {//测试卷
                holder.setText(R.id.tv_type_one, R.string.test_volume)
                holder.setText(R.id.tv_center_style_one_resource, item.source)
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {//问卷
                holder.setText(R.id.tv_type_one, R.string.questionnaire)
                holder.setText(R.id.tv_center_style_one_resource, "")

            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {////自建音频
                holder.setText(R.id.tv_type_one, R.string.oneself_track)
                holder.setGone(R.id.ivPlay, false)

                val musicBean = item.audio_data
                holder.setText(
                    R.id.tv_center_style_one_resource,
                    "播放 ${formatPlayCount(musicBean.audio_play_num.toInt())}次"

                )
                holder.setText(
                    R.id.tv_bottom_left_style_one_resource,
                    timeParse(musicBean.audio_time.toLong())
                )


            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业

            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识
                holder.setText(R.id.tv_center_style_one_resource, item.resource_info?.title)
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(
                        R.id.tv_bottom_left_style_one_resource,
                        item.resource_info?.updated_time + "更新"
                    )
                }
            }

            else -> {
                holder.setGone(R.id.tv_reasons_one, true)
                holder.setGone(R.id.ivPlay, true)
                holder.setGone(R.id.tv_type_one, true)
//                holder.setGone(R.id.tv_type_one,true)
            }
        }
    }

    private fun setTwoData(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_default)
            .into(holder.getView(R.id.iv_recommend_style_two))
        holder.setText(R.id.tvTitleStyleTwo, item.title)
        holder.setText(R.id.tvReasonsStyleTwo, item.recommend_reason)

        holder.setGone(R.id.rlLeftLineRight, true)
        holder.setGone(R.id.tvRight, true)
        holder.setGone(R.id.tvCenterOneStyleTwo, true)
        holder.setGone(R.id.tvCenterTwoStyleTwo, true)
        holder.setGone(R.id.tvCenterThreeStyleTwo, true)
        holder.setGone(R.id.viewLine, true)

        val value = typeStringMap[item.type]
        holder.setText(R.id.tv_type, value)
        holder.setGone(R.id.tv_type, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeListTwo, value)
        holder.setGone(R.id.tvTypeListTwo, true)
        when (item.type) {
            ResourceTypeConstans.TYPE_TASK -> {
                holder.setGone(R.id.rlLeftLineRight, false)
                holder.setGone(R.id.tvRight, false)
                holder.setGone(R.id.viewLine, false)

                holder.setText(
                    R.id.tvLineLift,
                    getSpaString(
                        item.success_score,
                        ContextCompat.getColor(context, R.color.color_c),
                        ContextCompat.getColor(context, R.color.color_white),
                        12.sp2px(),
                        15.sp2px()
                    )
                )
                holder.setText(
                    R.id.tvRight,
                    (if (item.join_num.isNullOrEmpty()) "0" else item.join_num) + "人参与"
                )
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                holder.setGone(R.id.rlLeftLineRight, false)
                holder.setGone(R.id.tvRight, false)
                holder.setGone(R.id.viewLine, false)

                holder.setText(
                    R.id.tvLineLift,
                    "更新至" + item.periodicals_data.year + "年" + "第" + item.periodicals_data.term + "期"
                )
                holder.setText(R.id.tvRight, item.periodicals_data.unit)
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tvTypeListTwo, item.identity_name)
                    holder.setGone(R.id.tvTypeListTwo, false)
                    holder.setGone(R.id.tv_type, true)
                } else {
                    holder.setGone(R.id.tvTypeListTwo, true)
                    holder.setGone(R.id.tv_type, false)
                }
                holder.setGone(R.id.rlLeftLineRight, false)

                holder.setText(R.id.tvLineRight, item.start_time)
                holder.setText(R.id.tvLineLift, item.source)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.tv_type, R.string.ebook)

                holder.setGone(R.id.tvCenterOneStyleTwo, false)

                holder.setText(R.id.tvCenterOneStyleTwo, item.staff)
                holder.setText(R.id.tvLineLift, item.writer)


            }
            ResourceTypeConstans.TYPE_KNOWLEDGE, ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                holder.setGone(R.id.rlLeftLineRight, false)
                holder.setGone(R.id.viewLine, false)

                holder.setText(
                    R.id.tvLineLift,
                    if (TextUtils.isEmpty(item.source)) "-" else item.source
                )

                if (!TextUtils.isEmpty(item.getVideo_duration())) {
                    val video_duration: Long
                    video_duration = try {
                        item.getVideo_duration().toLong()
                    } catch (e: java.lang.NumberFormatException) {
                        0
                    }
                    holder.setText(R.id.tvLineRight, timeParse(video_duration))
                }

            }

            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.tvCenterOneStyleTwo, false)

                val album: AlbumBean = item.album_data
                val num: String = album.track_count.toString() + "集"
                holder.setText(R.id.tvCenterOneStyleTwo, num)

            }
            ResourceTypeConstans.TYPE_TRACK -> {
                val track: TrackBean = item.track_data
                holder.setGone(R.id.rlLeftLineRight, false)

                if (track.subordinated_album != null) {
                    holder.setText(R.id.tvLineLift, track.subordinated_album?.album_title)
                    holder.setText(R.id.tvLineRight, timeParse(track.duration))
                    holder.setGone(R.id.viewLine, track.duration == 0L)
                    holder.setGone(R.id.tvLineRight, track.duration == 0L)
                }
            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {
                holder.setGone(R.id.tvCenterOneStyleTwo, false)

                if (item.price > 0) {
                    val p: Float = item.price / 100
                    val price = String.format(context.resources.getString(R.string.text_f), p)
                    holder.setText(
                        R.id.tvCenterOneStyleTwo,
                        String.format(
                            context.resources.getString(R.string.text_price_master), price
                        )
                    )
                } else {
                    holder.setText(R.id.tvCenterOneStyleTwo, "")

                }
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setGone(R.id.rlLeftLineRight, false)

                setViewVisibility(
                    holder.getView(R.id.tvLineLift),
                    holder.getView(R.id.viewLine),
                    item.staff
                )
                setViewVisibility(
                    holder.getView(R.id.tvLineRight),
                    holder.getView(R.id.viewLine),
                    item.source
                )
                holder.setText(R.id.tvLineLift, item.staff)
                holder.setText(R.id.tvLineRight, item.source)

            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setGone(R.id.rlLeftLineRight, false)
                setViewVisibility(
                    holder.getView(R.id.tvLineRight),
                    holder.getView(R.id.viewLine),
                    item.getSource()
                )
                holder.setText(
                    R.id.tvLineLift,
                    if (TextUtils.isEmpty(item.getStaff())) "-" else item.staff
                )
                holder.setText(R.id.tvLineRight, item.source)
            }
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {//友情链接
//                holder.setGone(R.id.tv_type, true)
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读
                holder.setGone(R.id.tv_type, true)
            }
            ResourceTypeConstans.TYPE_ACTIVITY -> {
                holder.setGone(R.id.tvCenterOneStyleTwo, false)
                holder.setGone(R.id.tvCenterTwoStyleTwo, false)
                setViewVisibility(holder.getView(R.id.tvCenterOneStyleTwo), item.about)
                setViewVisibility(holder.getView(R.id.tvCenterTwoStyleTwo), item.publish_time)
                holder.setText(R.id.tvCenterOneStyleTwo, item.about)
                holder.setText(R.id.tvCenterTwoStyleTwo, item.publish_time)

            }
            ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
                holder.setGone(R.id.tvCenterOneStyleTwo, false)
                holder.setGone(R.id.tvCenterTwoStyleTwo, false)
                setViewVisibility(holder.getView(R.id.tvCenterOneStyleTwo), item.about)
                setViewVisibility(holder.getView(R.id.tvCenterTwoStyleTwo), item.publish_time)
                holder.setText(R.id.tvCenterOneStyleTwo, item.about)
                holder.setText(R.id.tvCenterTwoStyleTwo, item.publish_time)
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setGone(R.id.tvCenterOneStyleTwo, false)
                holder.setGone(R.id.rlLeftLineRight, false)
                setViewVisibility(holder.getView(R.id.tvCenterOneStyleTwo), item.getStaff())
                setViewVisibility(
                    holder.getView(R.id.tvLineLift),
                    holder.getView(R.id.viewLine),
                    item.start_time
                )
                setViewVisibility(
                    holder.getView(R.id.tvLineRight),
                    holder.getView(R.id.viewLine),
                    item.student_num
                )
                holder.setText(R.id.tvCenterOneStyleTwo, item.staff)
                if (item.time_mode.equals("1")) {//时间永久
                    holder.setText(
                        R.id.tvLineLift,
                        context.resources.getString(R.string.study_time_permanent_opening)
                    )
                } else {
                    holder.setText(R.id.tvLineLift, getTime(item.start_time))
                }
                holder.setText(R.id.tvLineRight, "${item.student_num}人")

            }
            ResourceTypeConstans.TYPE_COLUMN -> {
                holder.setGone(R.id.tvCenterOneStyleTwo, false)
                holder.setGone(R.id.tvCenterTwoStyleTwo, false)
                holder.setGone(R.id.tvCenterThreeStyleTwo, false)
                setViewVisibility(holder.getView(R.id.tvCenterOneStyleTwo), item.getStaff())
                setViewVisibility(holder.getView(R.id.tvCenterTwoStyleTwo), item.desc)
                setViewVisibility(holder.getView(R.id.tvCenterThreeStyleTwo), item.update_time)
                holder.setText(R.id.tvCenterOneStyleTwo, item.staff)
                holder.setText(R.id.tvCenterTwoStyleTwo, item.desc)
                holder.setText(R.id.tvCenterThreeStyleTwo, item.update_time)


            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tv_type, item.identity_name)
                }
                setViewVisibility(
                    holder.getView(R.id.tvCenterTwoStyleTwo),
                    item.resource_info?.title ?: ""
                )
                setViewVisibility(
                    holder.getView(R.id.tvCenterThreeStyleTwo),
                    item.resource_info?.updated_time ?: ""
                )
                holder.setText(R.id.tvCenterTwoStyleTwo, item.resource_info?.title)
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(
                        R.id.tvCenterThreeStyleTwo,
                        item.resource_info?.updated_time + "更新"
                    )
                }
            }
            ResourceTypeConstans.TYPE_BAIKE -> {
                holder.setGone(R.id.tvCenterOneStyleTwo, false)
                setViewVisibility(holder.getView(R.id.tvCenterOneStyleTwo), item.source)
                holder.setText(R.id.tvCenterOneStyleTwo, item.source)
            }
            ResourceTypeConstans.TYPE_TEST_VOLUME -> {
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                val bean = item.audio_data

                holder.setGone(R.id.rlLeftLineRight, bean == null)

                if (bean != null) {
                    holder.setText(R.id.tvLineLift, "播放 ${bean.audio_play_num}次")
                    holder.setGone(R.id.viewLine, bean.audio_time == 0L)
                    holder.setGone(R.id.tvLineRight, bean.audio_time == 0L)
                    holder.setText(R.id.tvLineRight, timeParse(bean.audio_time))
                }
            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业

            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识
                holder.setGone(R.id.tvCenterOneStyleTwo, false)
                holder.setGone(R.id.tvCenterTwoStyleTwo, false)
                setViewVisibility(
                    holder.getView(R.id.tvCenterTwoStyleTwo),
                    item.resource_info?.title ?: ""
                )
                setViewVisibility(
                    holder.getView(R.id.tvCenterThreeStyleTwo),
                    item.resource_info?.updated_time ?: ""
                )
                holder.setText(R.id.tvCenterTwoStyleTwo, item.resource_info?.title)
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(
                        R.id.tvCenterThreeStyleTwo,
                        item.resource_info?.updated_time + "更新"
                    )
                }
            }
            else -> {
                holder.setGone(R.id.tvCenterOneStyleTwo, true)
                holder.setGone(R.id.tvCenterTwoStyleTwo, true)
                holder.setGone(R.id.tvCenterThreeStyleTwo, true)
                holder.setGone(R.id.rlLeftLineRight, true)
            }
        }
    }

    private fun setThreeData(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        Glide.with(context)
            .load(image)
            .transform(GlideTransform.centerCropAndRounder2)
            .error(R.mipmap.common_bg_cover_default)
            .placeholder(R.mipmap.common_bg_cover_default)
            .into(holder.getView(R.id.iv_recommend_style_three))


//        setViewVisibility(holder.getView(R.id.tv_reasons_three), item.recommend_reason)
//        holder.setGone(R.id.tv_reasons_three, true)
        holder.setText(R.id.tvTitleStyleThree, item.title)
        holder.setText(R.id.tv_right_style_three, "")
        holder.setText(R.id.tvBottomStyleThree, "")
        holder.setText(R.id.tvCenterOneStyleThree, "")
        holder.setText(R.id.tvCenterTwoStyleThree, "")
//        holder.setGone(R.id.tv_right_style_three, false)

//        holder.setGone(R.id.tvCenterOneStyleThree, true)
//        holder.setGone(R.id.tvCenterTwoStyleThree, true)
        holder.setGone(R.id.ivPlay, true)

        val value = typeStringMap[item.type]
        holder.setText(R.id.tv_type_three, value)
        holder.setGone(R.id.tv_type_three, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeListThree, value)
        holder.setGone(R.id.tvTypeListThree, true)

        when (item.type) {
            ResourceTypeConstans.TYPE_TASK -> {

//                holder.setGone(R.id.tvCenterOneStyleThree, false)
//                holder.setGone(R.id.tvCenterTwoStyleThree, false)

                //将两个时间输出到一行
                holder.setText(
                    R.id.tvCenterTwoStyleThree,
                    getEnrollTime(item) + "/n" + getTaskTime(item)
                )
//                holder.setText(
//                    R.id.tvBottomStyleThree,
//                    getTaskTime(item)
//                )
                holder.setText(
                    R.id.tvCenterOneStyleThree,
                    getSpaString(
                        item.success_score,
                        ContextCompat.getColor(context, R.color.color_6B),
                        ContextCompat.getColor(context, R.color.color_5D5D5D),
                        13.sp2px(),
                        15.sp2px()
                    )
                )
                holder.setText(
                    R.id.tv_right_style_three,
                    (if (item.join_num.isNullOrEmpty()) "0" else item.join_num) + "人参与"
                )

            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
//                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.setText(R.id.tvBottomStyleThree, item.periodicals_data.unit)
                holder.setText(
                    R.id.tvCenterOneStyleThree,
                    "更新至" + item.periodicals_data.year + "年" + "第" + item.periodicals_data.term + "期"
                )
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tv_right_style_three, item.source)

            }
            ResourceTypeConstans.TYPE_COURSE -> {
                if (!TextUtils.isEmpty(item.identity_name)) {
                    holder.setText(R.id.tvTypeListThree, item.identity_name)
                    holder.setGone(R.id.tvTypeListThree, false)
                    holder.setGone(R.id.tv_type_three, true)
                } else {
                    holder.setGone(R.id.tvTypeListThree, true)
                    holder.setGone(R.id.tv_type_three, false)
                }
//                setViewVisibility(holder.getView(R.id.tvCenterOneStyleThree), item.getSource())
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
                holder.setText(R.id.tvBottomStyleThree, item.start_time)
                holder.setText(R.id.tvCenterOneStyleThree, item.source)

            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
//                setViewVisibility(holder.getView(R.id.tvCenterOneStyleThree), item.staff)
//                setViewVisibility(holder.getView(R.id.tvCenterTwoStyleThree), item.student_num)

                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
                holder.setText(R.id.tvCenterOneStyleThree, item.staff)
                holder.setText(R.id.tvCenterTwoStyleThree, "${item.student_num}人")
                if (item.time_mode.equals("1")) {//时间永久
                    holder.setText(
                        R.id.tvBottomStyleThree,
                        context.resources.getString(R.string.study_time_permanent_opening)
                    )
                } else {
                    holder.setText(R.id.tvBottomStyleThree, item.start_time)
                }

            }

            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvCenterOneStyleThree, item.staff)
                holder.setText(R.id.tv_right_style_three, item.source)
                holder.setText(R.id.tvBottomStyleThree, item.desc)

            }

            ResourceTypeConstans.TYPE_KNOWLEDGE, ResourceTypeConstans.TYPE_MICRO_LESSON -> {
//                setViewVisibility(holder.getView(R.id.tvCenterOneStyleThree), item.getSource())
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
                holder.setText(R.id.tvCenterOneStyleThree, item.source)
                if (!TextUtils.isEmpty(item.getVideo_duration())) {
                    val video_duration: Long
                    video_duration = try {
                        item.getVideo_duration().toLong()
                    } catch (e: java.lang.NumberFormatException) {
                        0
                    }
                    holder.setText(R.id.tvBottomStyleThree, timeParse(video_duration))

                }

            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
//                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvCenterOneStyleThree, item.staff)
                holder.setText(R.id.tvBottomStyleThree, item.desc)
                holder.setText(R.id.tv_right_style_three, item.source)

            }

            ResourceTypeConstans.TYPE_E_BOOK -> {
//                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvCenterOneStyleThree, item.writer)
                holder.setText(R.id.tvBottomStyleThree, item.desc)
                holder.setText(R.id.tv_right_style_three, item.source)
            }
            ResourceTypeConstans.TYPE_ACTIVITY -> {
//                setViewVisibility(holder.getView(R.id.tvCenterOneStyleThree), item.getAbout())
//                setViewVisibility(holder.getView(R.id.tvCenterTwoStyleThree), item.getSource())
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
                holder.setText(R.id.tvCenterOneStyleThree, item.about)
                holder.setText(R.id.tvCenterTwoStyleThree, item.source)

            }
            ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
//                setViewVisibility(holder.getView(R.id.tvCenterOneStyleThree), item.getAbout())
//                setViewVisibility(holder.getView(R.id.tvCenterTwoStyleThree), item.getSource())
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
                holder.setText(R.id.tvCenterOneStyleThree, item.about)
                holder.setText(R.id.tvCenterTwoStyleThree, item.source)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.ivPlay, false)
                val album = item.album_data

                if (album != null) {
//                    setViewVisibility(
//                        holder.getView(R.id.tvCenterTwoStyleThree),
//                        album.track_count.toString()
//                    )
                    val num = "${album.track_count.toString()}集"
                    holder.setText(R.id.tvCenterTwoStyleThree, num)

                }
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvBottomStyleThree, item.desc)


            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.ivPlay, false)

                val track = item.track_data
                if (track != null) {
                    var trackToAlbum: String? = null
                    val time: String
                    var centerTrack = ""
                    if (track.subordinated_album != null && !TextUtils.isEmpty(track.subordinated_album?.album_title)) {
                        trackToAlbum = track.subordinated_album?.album_title
                        centerTrack = "$trackToAlbum    "
                        if ((trackToAlbum?.length ?: 0) > 8) {
                            centerTrack = trackToAlbum?.substring(0, 7) + "...    "
                        }
                    }
                    holder.setGone(R.id.tvCenterOneStyleThree, false)
                    holder.setText(R.id.tvCenterOneStyleThree, centerTrack)
                    holder.setText(R.id.tv_right_style_three, timeParse(track.duration))

                }
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvBottomStyleThree, item.desc)

            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {
                holder.setGone(R.id.tvCenterOneStyleThree, false)



                if (item.getPrice() > 0) {
                    val p: Float = item.getPrice() / 100
                    val price = String.format(context.getResources().getString(R.string.text_f), p)
                    holder.setText(
                        R.id.tvCenterOneStyleThree,
                        String.format(
                            context.getResources().getString(R.string.text_price_master), price
                        )
                    )

                } else {
                    holder.setText(R.id.tvCenterOneStyleThree, "")

                }
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvBottomStyleThree, item.desc)
            }

            ResourceTypeConstans.TYPE_COLUMN -> {
//                setViewVisibility(holder.getView(R.id.tvCenterOneStyleThree), item.about)
//                setViewVisibility(holder.getView(R.id.tvCenterTwoStyleThree), item.desc)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
                holder.setText(R.id.tvCenterOneStyleThree, item.about)
                holder.setText(R.id.tvCenterTwoStyleThree, item.desc)
                holder.setText(R.id.tvBottomStyleThree, item.update_time)

            }
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {
//                holder.setGone(R.id.tv_type_three, true)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1

            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                holder.setGone(R.id.tv_type_three, true)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1

            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tv_type_three, item.identity_name)
                }
//                setViewVisibility(
//                    holder.getView(R.id.tvCenterOneStyleThree),
//                    item.resource_info?.title ?: ""
//                )
//                setViewVisibility(
//                    holder.getView(R.id.tvCenterTwoStyleThree),
//                    item.resource_info?.updated_time ?: ""
//                )
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
                holder.setText(R.id.tvCenterOneStyleThree, item.resource_info?.title)
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(
                        R.id.tvCenterTwoStyleThree,
                        item.resource_info?.updated_time + "更新"
                    )
                }
            }
            ResourceTypeConstans.TYPE_BAIKE -> {
//                setViewVisibility(holder.getView(R.id.tvCenterOneStyleThree), item.source)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvCenterOneStyleThree, item.desc)
                holder.setText(R.id.tvBottomStyleThree, item.source)

            }
            ResourceTypeConstans.TYPE_TEST_VOLUME -> {
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
                holder.setGone(R.id.tvCenterOneStyleThree, false)
                val musicBean = item.audio_data
                if (musicBean != null) {
                    holder.setText(R.id.tv_right_style_three, timeParse(musicBean.audio_time))
                    holder.setText(
                        R.id.tvCenterOneStyleThree,
                        "播放 ${formatPlayCount(musicBean.audio_play_num)}次"
                    )

                } else {
                    holder.setText(R.id.tv_right_style_three, "")
                    holder.setText(R.id.tvCenterOneStyleThree, "")
                }
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvBottomStyleThree, item.desc)


            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业

            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识
//                setViewVisibility(
//                    holder.getView(R.id.tvCenterOneStyleThree),
//                    item.resource_info?.title ?: ""
//                )
//                setViewVisibility(
//                    holder.getView(R.id.tvCenterTwoStyleThree),
//                    item.resource_info?.updated_time ?: ""
//                )
                holder.setText(R.id.tvCenterOneStyleThree, item.resource_info?.title)
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(
                        R.id.tvCenterTwoStyleThree,
                        item.resource_info?.updated_time + "更新"
                    )
                }
            }
            else -> {
                holder.setGone(R.id.tv_type_three, true)
                holder.setGone(R.id.ivPlay, true)
                holder.setGone(R.id.tvCenterOneStyleThree, true)
                holder.setGone(R.id.tvCenterTwoStyleThree, true)
                holder.setGone(R.id.tvBottomStyleThree, true)
                holder.setGone(R.id.tv_right_style_three, true)
//                holder.setGone(R.id.tv_reasons_three, true)
            }
        }


        val tvCenterOneStyleThree = holder.getView<TextView>(R.id.tvCenterOneStyleThree)
        if (TextUtils.isEmpty(tvCenterOneStyleThree.text)) {
            tvCenterOneStyleThree.visibility = View.GONE
        } else {
            tvCenterOneStyleThree.visibility = View.VISIBLE
        }

        val tvCenterTwoStyleThree = holder.getView<TextView>(R.id.tvCenterTwoStyleThree)
        if (TextUtils.isEmpty(tvCenterTwoStyleThree.text)) {
            tvCenterTwoStyleThree.visibility = View.GONE
        } else {
            tvCenterTwoStyleThree.visibility = View.VISIBLE
        }

        val tvBottomStyleThree = holder.getView<TextView>(R.id.tvBottomStyleThree)
        if (TextUtils.isEmpty(tvBottomStyleThree.text)) {
            tvBottomStyleThree.visibility = View.GONE
        } else {
            tvBottomStyleThree.visibility = View.VISIBLE
        }

        val tvRightStyleThree = holder.getView<TextView>(R.id.tv_right_style_three)
        if (TextUtils.isEmpty(tvRightStyleThree.text)) {
            tvRightStyleThree.visibility = View.GONE
        } else {
            tvRightStyleThree.visibility = View.VISIBLE
        }


    }

    private fun setFourData(holder: BaseViewHolder, dataBean: RecommendContentBean.DataBean) {
        val image: String =
            if (TextUtils.isEmpty(dataBean.big_image)) dataBean.small_image else dataBean.big_image
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_default)
            .into(holder.getView(R.id.iv_recommend_style_four))

        setViewVisibility(holder.getView(R.id.tv_reasons_four), dataBean.recommend_reason)
        holder.setGone(R.id.tv_reasons_four, true)
        holder.setText(R.id.tv_reasons_four, dataBean.recommend_reason)
        holder.setGone(R.id.ivPlay, true)
        holder.setText(R.id.tvTitleStyleFour, dataBean.title)
        holder.setText(R.id.tvCenterOneRight, "")
        holder.setText(R.id.tvCenterTwoRight, "")


        val value = typeStringMap[dataBean.type]
        holder.setText(R.id.tv_type_four, value)
        holder.setGone(R.id.tv_type_four, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeListFour, value)
        holder.setGone(R.id.tvTypeListFour, true)

        when (dataBean.type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                if (!TextUtils.isEmpty(dataBean.identity_name)) {
                    holder.setText(R.id.tvTypeListFour, dataBean.identity_name)
                    holder.setGone(R.id.tvTypeListFour, false)
                    holder.setGone(R.id.tv_type_four, true)
                } else {
                    holder.setGone(R.id.tvTypeListFour, true)
                    holder.setGone(R.id.tv_type_four, false)
                }
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                if (!TextUtils.isEmpty(dataBean.org)) {
                    holder.setText(R.id.tvCenterOneLeft, dataBean.org)
                }
                if (!TextUtils.isEmpty(dataBean.start_time)) {
                    holder.setText(R.id.tvCenterOneRight, dataBean.start_time)
                    holder.setGone(R.id.tvCenterOneRight, false)
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }

            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, false)
                holder.setGone(R.id.tvCenterOneRight, true)
                holder.setGone(R.id.tvCenterTwoRight, true)

                holder.setText(
                    R.id.tvCenterOneLeft,
                    "更新至" + dataBean.periodicals_data.year + "年" + "第" + dataBean.periodicals_data.term + "期"
                )
                holder.setText(R.id.tvCenterTwoLeft, dataBean.periodicals_data.unit)

            }
            ResourceTypeConstans.TYPE_TASK -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)

                holder.setText(
                    R.id.tvCenterOneLeft,
                    getSpaString(
                        dataBean.success_score,
                        ContextCompat.getColor(context, R.color.color_6B),
                        ContextCompat.getColor(context, R.color.color_5D5D5D),
                        13.sp2px(),
                        15.sp2px()
                    )
                )
                if (!TextUtils.isEmpty(dataBean.join_num)) {
                    holder.setText(R.id.tvCenterOneRight, dataBean.join_num + "人参与")
                    holder.setGone(R.id.tvCenterOneRight, false)
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }

            }
            ResourceTypeConstans.TYPE_KNOWLEDGE, ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.source)

                if (!TextUtils.isEmpty(dataBean.video_duration)) {
                    val videoDuration: Long = try {
                        dataBean.video_duration.toLong()
                    } catch (e: java.lang.NumberFormatException) {
                        0
                    }
                    holder.setText(R.id.tvCenterOneRight, timeParse(videoDuration))
                    holder.setGone(R.id.tvCenterOneRight, false)
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.staff)
                if (!TextUtils.isEmpty(dataBean.source)) {
                    holder.setText(R.id.tvCenterOneRight, dataBean.source)
                    holder.setGone(R.id.tvCenterOneRight, false)
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.ivPlay, false)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setGone(R.id.tvCenterOneRight, true)
                val album = dataBean.album_data
                if (album != null) {
                    val num: String = String.format(
                        context.resources
                            .getString(R.string.recommend_album_count),
                        java.lang.String.valueOf(album.track_count)
                    )
                    holder.setText(R.id.tvCenterOneLeft, num)

                }
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                val track = dataBean.track_data
                if (track != null) {
                    if (track.subordinated_album != null && !TextUtils.isEmpty(track.subordinated_album?.album_title)) {
                        holder.setText(R.id.tvCenterOneLeft, track.subordinated_album?.album_title)
                    }
                    holder.setText(R.id.tvCenterOneRight, timeParse(track.duration))
                    holder.setGone(R.id.tvCenterOneRight, false)
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }
            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setGone(R.id.tvCenterOneRight, true)
                if (dataBean.price > 0) {
                    val p = dataBean.price / 100
                    val price = String.format(context.resources.getString(R.string.text_f), p)
                    holder.setText(
                        R.id.tvCenterOneLeft,
                        String.format(
                            context.resources.getString(R.string.text_price_master), price
                        )
                    )

                } else {
                    holder.setText(R.id.tvCenterOneLeft, "")
                }
            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.staff)
                if (!TextUtils.isEmpty(dataBean.source)) {
                    holder.setText(R.id.tvCenterOneRight, dataBean.source)
                    holder.setGone(R.id.tvCenterOneRight, false)
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }
            }
            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.about)
                if (!TextUtils.isEmpty(dataBean.publish_time)) {
                    holder.setText(R.id.tvCenterOneRight, dataBean.publish_time)
                    holder.setGone(R.id.tvCenterOneRight, false)
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }

            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.writer)
                if (!TextUtils.isEmpty(dataBean.source)) {
                    holder.setText(R.id.tvCenterOneRight, dataBean.source)
                    holder.setGone(R.id.tvCenterOneRight, false)
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, false)
                holder.setGone(R.id.tvCenterOneRight, true)

                holder.setText(R.id.tvCenterOneLeft, dataBean.staff)
                holder.setText(R.id.tvCenterTwoLeft, "${dataBean.student_num}人")
                if (dataBean.time_mode.equals("1")) {//时间永久
                    holder.setText(
                        R.id.tvCenterTwoRight,
                        context.resources.getString(R.string.study_time_permanent_opening)
                    )
                    holder.setGone(R.id.tvCenterTwoRight, false)

                } else {
                    if (!TextUtils.isEmpty(dataBean.start_time)) {
                        holder.setText(R.id.tvCenterTwoRight, getTime(dataBean.start_time))
                        holder.setGone(R.id.tvCenterTwoRight, false)
                    } else {
                        holder.setGone(R.id.tvCenterTwoRight, true)
                    }
                }


            }
            ResourceTypeConstans.TYPE_COLUMN -> {
                if (!TextUtils.isEmpty(dataBean.desc)) {
                    holder.setGone(R.id.llCenterOne, false)
                    holder.setGone(R.id.tvCenterOneRight, true)
                    holder.setText(R.id.tvCenterOneLeft, dataBean.desc)
                } else {
                    holder.setGone(R.id.llCenterOne, true)
                }
                if (!TextUtils.isEmpty(dataBean.update_time)) {
                    holder.setGone(R.id.llCenterTwo, false)
                    holder.setGone(R.id.tvCenterTwoRight, true)
                    holder.setText(R.id.tvCenterTwoLeft, dataBean.update_time)
                } else {
                    holder.setGone(R.id.llCenterTwo, true)
                }

            }
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {
                holder.setGone(R.id.llCenterOne, true)
                holder.setGone(R.id.llCenterTwo, true)

            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                holder.setGone(R.id.llCenterOne, true)
                holder.setGone(R.id.llCenterTwo, true)

            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(dataBean.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tv_type_four, dataBean.identity_name)
                }
                holder.setGone(R.id.llCenterTwo, true)
                if (TextUtils.isEmpty(dataBean.resource_info.title)
                    && TextUtils.isEmpty(dataBean.resource_info.updated_time)
                ) {
                    holder.setGone(R.id.llCenterOne, true)
                } else {
                    holder.setGone(R.id.llCenterOne, false)
                }
                if (!TextUtils.isEmpty(dataBean.resource_info.title)) {
                    holder.setText(R.id.tvCenterOneLeft, dataBean.resource_info.title)
                }
                if (!TextUtils.isEmpty(dataBean.resource_info.updated_time)) {
                    holder.setGone(R.id.tvCenterOneRight, false)
                    holder.setText(
                        R.id.tvCenterOneRight,
                        dataBean.resource_info.updated_time + "更新"
                    )
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }

            }
            ResourceTypeConstans.TYPE_BAIKE -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.source)

            }
            ResourceTypeConstans.TYPE_TEST_VOLUME -> {
                holder.setGone(R.id.llCenterOne, true)
                holder.setGone(R.id.llCenterTwo, true)
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
                holder.setGone(R.id.llCenterOne, true)
                holder.setGone(R.id.llCenterTwo, true)
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)

                val musicBean = dataBean.audio_data
                if (musicBean != null) {
                    holder.setText(
                        R.id.tvCenterOneLeft,
                        "播放 ${formatPlayCount(musicBean.audio_play_num.toInt())}次"
                    )
                    holder.setText(
                        R.id.tvCenterOneRight,
                        timeParse(musicBean.audio_time)
                    )
                    holder.setGone(R.id.tvCenterOneRight, false)
                } else {
                    holder.setText(R.id.tvCenterOneLeft, "")
                    holder.setText(R.id.tvCenterOneRight, "")
                    holder.setGone(R.id.tvCenterOneRight, true)
                }
            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业
                holder.setGone(R.id.llCenterOne, true)
                holder.setGone(R.id.llCenterTwo, true)
            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战
                holder.setGone(R.id.llCenterOne, true)
                holder.setGone(R.id.llCenterTwo, true)
            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识
                holder.setGone(R.id.llCenterTwo, true)
                if (TextUtils.isEmpty(dataBean.resource_info.title)
                    && TextUtils.isEmpty(dataBean.resource_info.updated_time)
                ) {
                    holder.setGone(R.id.llCenterOne, true)
                } else {
                    holder.setGone(R.id.llCenterOne, false)
                }
                if (!TextUtils.isEmpty(dataBean.resource_info.title)) {
                    holder.setText(R.id.tvCenterOneLeft, dataBean.resource_info.title)
                }
                if (!TextUtils.isEmpty(dataBean.resource_info.updated_time)) {
                    holder.setGone(R.id.tvCenterOneRight, false)
                    holder.setText(
                        R.id.tvCenterOneRight,
                        dataBean.resource_info.updated_time + "更新"
                    )
                } else {
                    holder.setGone(R.id.tvCenterOneRight, true)
                }
            }
            else -> {
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setGone(R.id.tvCenterOneRight, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.source)
            }
        }
    }

    private fun setFiveData(holder: BaseViewHolder, dataBean: RecommendContentBean.DataBean) {
        val image: String =
            if (TextUtils.isEmpty(dataBean.getBig_image())) dataBean.getSmall_image() else dataBean.getBig_image()
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_default)
            .into(holder.getView(R.id.iv_recommend_style_five))


        if (dataBean.getType() == ResourceTypeConstans.TYPE_ALBUM || dataBean.getType() == ResourceTypeConstans.TYPE_TRACK) {
            holder.setGone(R.id.ivPlay, false)
        } else {
            holder.setGone(R.id.ivPlay, true)

        }
        holder.setText(R.id.tv_title_style_five, dataBean.title)

        val value = typeStringMap[dataBean.type]
        holder.setText(R.id.tvTypeFive, value)
        holder.setGone(R.id.tvTypeFive, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeListFive, value)
        holder.setGone(R.id.tvTypeListFive, true)

        when (dataBean.type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                if (!TextUtils.isEmpty(dataBean.identity_name)) {
                    holder.setText(R.id.tvTypeListFive, dataBean.identity_name)
                    holder.setGone(R.id.tvTypeListFive, false)
                    holder.setGone(R.id.tvTypeFive, true)
                } else {
                    holder.setGone(R.id.tvTypeListFive, true)
                    holder.setGone(R.id.tvTypeFive, false)
                }
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
            }
            ResourceTypeConstans.TYPE_TASK -> {
            }
            ResourceTypeConstans.TYPE_KNOWLEDGE, ResourceTypeConstans.TYPE_MICRO_LESSON -> {
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.ivPlay, false)
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {
            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
            }
            ResourceTypeConstans.TYPE_ACTIVITY -> {
            }
            ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
            }
            ResourceTypeConstans.TYPE_COLUMN -> {
            }
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {
//                holder.setGone(R.id.tvTypeFive, true)
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                holder.setGone(R.id.tvTypeFive, true)
            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(dataBean.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tvTypeFive, dataBean.identity_name)
                }
            }

            ResourceTypeConstans.TYPE_BAIKE -> {
            }

            ResourceTypeConstans.TYPE_TEST_VOLUME -> {
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业

            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识

            }
            else -> {
                holder.setGone(R.id.tvTypeFive, true)
                holder.setGone(R.id.ivPlay, true)
            }
        }


    }

    private fun setNoImageData(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        //设置type类型
        holder.setText(R.id.tvNoImageTitle, item.title)
        holder.setText(R.id.tvNoImageSub, "")
        holder.setText(R.id.tvNoImageTime, "")
        holder.setText(R.id.tvNoImageMiddle, "")
        holder.setGone(R.id.tvNoImageMiddle, true);


        val value = typeStringMap[item.type]
        val tvSixType = holder.getView<TextView>(R.id.tvNoImageType)
        val llType = holder.getView<View>(R.id.llType)
        tvSixType.text = value
        if (TextUtils.isEmpty(value)) {
            llType.visibility = View.GONE
        } else {
            llType.visibility = View.VISIBLE
        }
        tvSixType.setTextColor(ContextCompat.getColor(context, R.color.color_AC))
        tvSixType.setBackgroundResource(R.drawable.shape_radius2_stroke05_c0c0c0)

        //初始化
        holder.setText(R.id.tvNoImageMiddle, "")
        holder.setText(R.id.tvNoImageSub, "")

        when (item.type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setText(R.id.tvNoImageSub, item.org)
                holder.setText(R.id.tvNoImageTime, item.start_time)
                if (!TextUtils.isEmpty(item.identity_name)) {
                    tvSixType.text = item.identity_name
                    tvSixType.setTextColor(ContextCompat.getColor(context, R.color.color_10955B))
                    tvSixType.setBackgroundResource(R.drawable.shape_radius2_stroke05_10955b)
                } else {
                    tvSixType.setText(R.string.course)
                }
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                holder.setText(R.id.tvNoImageSub, item.source)
                if (!TextUtils.isEmpty(item.video_duration)) {
                    val videoDuration: Long = try {
                        item.video_duration.toLong()
                    } catch (e: java.lang.NumberFormatException) {
                        0
                    }
                    holder.setText(R.id.tvNoImageTime, timeParse(videoDuration))
                }
            }
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.tvNoImageSub, item.staff)
                holder.setText(R.id.tvNoImageTime, item.source)
            }

            ResourceTypeConstans.TYPE_TASK -> {
                holder.setText(
                    R.id.tvNoImageSub,
                    getSpaString(
                        item.success_score,
                        ContextCompat.getColor(context, R.color.color_6B),
                        ContextCompat.getColor(context, R.color.color_5D5D5D),
                        12.sp2px(),
                        15.sp2px()
                    )
                )
                holder.setText(
                    R.id.tvNoImageTime,
                    (if (item.join_num.isNullOrEmpty()) "0" else item.join_num) + "人参与"
                )
            }
            //合集
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(item.identity_name)) {
                    tvSixType.text = item.identity_name
                }
                holder.setText(R.id.tvNoImageMiddle, item.resource_info?.title)
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(R.id.tvNoImageTime, item.resource_info?.updated_time + "更新")
                }
            }
            //外链
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {

            }
            //跟读
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {

            }
            //电子书
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.tvNoImageSub, item.writer)
                holder.setText(R.id.tvNoImageTime, item.plateform)
            }

            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setText(R.id.tvNoImageSub, item.staff)
                holder.setText(R.id.tvNoImageTime, item.student_num.toString() + "人")
            }

            ResourceTypeConstans.TYPE_ALBUM -> {
                val album: AlbumBean = item.album_data
                val num: String = album.track_count.toString() + "集"
                holder.setText(R.id.tvNoImageSub, num)
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
                holder.setText(R.id.tvNoImageSub, item.source)
            }
            ResourceTypeConstans.TYPE_TEST_VOLUME -> {//测试卷
                holder.setText(R.id.tvNoImageSub, item.source)
            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                val track = item.track_data
                if (track.subordinated_album != null) {
                    holder.setText(R.id.tvNoImageSub, track.subordinated_album!!.album_title)
                }
                holder.setText(R.id.tvNoImageTime, timeParse(track.duration))
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                val musicBean: MusicBean = item.audio_data
                holder.setText(R.id.tvNoImageTime, timeParse(musicBean.audio_time))
                holder.setText(R.id.tvNoImageSub, "")
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                holder.setText(R.id.tvNoImageTime, item.periodicals_data.unit)
                holder.setText(
                    R.id.tvNoImageSub,
                    "更新至" + item.periodicals_data.year + "年" + "第" + item.periodicals_data.term + "期"
                )
            }

            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
                holder.setText(R.id.tvNoImageMiddle, item.about)
                holder.setText(R.id.tvNoImageTime, item.publish_time)
            }

            ResourceTypeConstans.TYPE_COLUMN -> {

                holder.setText(R.id.tvNoImageMiddle, item.desc)
                holder.setText(R.id.tvNoImageTime, item.update_time)
            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识
                holder.setText(R.id.tvNoImageMiddle, item.resource_info?.title)
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(R.id.tvNoImageSub, item.resource_info?.updated_time + "更新")
                }

            }
            else -> {
                holder.setGone(R.id.tvNoImageMiddle, true)
            }

        }

        val tvNoImageMiddle = holder.getView<TextView>(R.id.tvNoImageMiddle)
        if (!TextUtils.isEmpty(tvNoImageMiddle.text)) {
            holder.setGone(R.id.tvNoImageMiddle, false)
        } else {
            holder.setGone(R.id.tvNoImageMiddle, true)
        }

        val tvNoImageSub = holder.getView<TextView>(R.id.tvNoImageSub)
        if (!TextUtils.isEmpty(tvNoImageSub.text)) {
            holder.setGone(R.id.tvNoImageSub, false)
        } else {
            holder.setGone(R.id.tvNoImageSub, true)
        }


    }

    private fun setSixData(helper: BaseViewHolder, item: RecommendContentBean.DataBean) {
        val itemViewType: Int = helper.itemViewType
        if (itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) return

        helper.setGone(R.id.icon_play, true)
        helper.setText(R.id.tvCenterSixLeft, "")
        helper.setText(R.id.tvCenterSixRight, "")
        helper.setText(R.id.time, "")
        helper.setGone(R.id.llCenterSix, true)
        helper.setGone(R.id.tvCenterSixLeft, true)
        helper.setGone(R.id.tvCenterSixRight, true)
        helper.setGone(R.id.time, true)

        val type = typeStringMap[item.type]
        helper.setText(R.id.tv_identify, type)
        helper.setGone(R.id.tv_identify, TextUtils.isEmpty(type))
        helper.setText(R.id.tvTypeListSix, type)
        helper.setGone(R.id.tvTypeListSix, true)

        helper.setText(R.id.title, item.title)

        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        //默认一个图片一个标题
        Glide.with(context)
            .load(image)
            .placeholder(R.mipmap.common_bg_cover_big_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into((helper.getView<View>(R.id.cover) as ImageView))


        when (item.type) {
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    helper.setText(R.id.tv_identify, item.identity_name)
                }
                if (!TextUtils.isEmpty(item.resource_info?.title)) {
                    helper.setGone(R.id.llCenterSix, false)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                    helper.setGone(R.id.tvCenterSixRight, true)
                    helper.setText(R.id.tvCenterSixLeft, item.resource_info?.title)
                }
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    helper.setText(R.id.time, item.resource_info?.updated_time + "更新")
                    helper.setGone(R.id.time, false)
                } else {
                    helper.setGone(R.id.time, true)
                }
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                helper.setGone(R.id.llCenterSix, false)

                if (!TextUtils.isEmpty(item.source)) {
                    helper.setText(R.id.tvCenterSixLeft, item.source)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                if (!TextUtils.isEmpty(item.video_duration)) {
                    val videoDuration: Long = try {
                        item.video_duration.toLong()
                    } catch (e: java.lang.NumberFormatException) {
                        0
                    }
                    helper.setText(R.id.tvCenterSixRight, timeParse(videoDuration))
                    helper.setGone(R.id.tvCenterSixRight, false)
                } else {
                    helper.setGone(R.id.tvCenterSixRight, true)
                }
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                if (!TextUtils.isEmpty(item.identity_name)) {
                    helper.setText(R.id.tvTypeListSix, item.identity_name)
                    helper.setGone(R.id.tvTypeListSix, false)
                    helper.setGone(R.id.tv_identify, true)
                } else {
                    helper.setGone(R.id.tvTypeListSix, true)
                    helper.setGone(R.id.tv_identify, false)
                }
                helper.setGone(R.id.llCenterSix, false)
                if (!TextUtils.isEmpty(item.org)) {
                    helper.setText(R.id.tvCenterSixLeft, item.org)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                if (!TextUtils.isEmpty(item.start_time)) {
                    helper.setText(R.id.tvCenterSixRight, item.start_time)
                    helper.setGone(R.id.tvCenterSixRight, false)
                } else {
                    helper.setGone(R.id.tvCenterSixRight, true)
                }
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                helper.setGone(R.id.icon_play, false)
                val (_, _, _, _, subordinated_album, _, _, _, _, duration) = item.track_data
                helper.setGone(R.id.llCenterSix, false)
                if (subordinated_album != null
                    && !TextUtils.isEmpty(subordinated_album.album_title)
                ) {
                    helper.setText(R.id.tvCenterSixLeft, subordinated_album.album_title)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                if (!TextUtils.isEmpty(timeParse(duration))) {
                    helper.setText(R.id.tvCenterSixRight, timeParse(duration))
                    helper.setGone(R.id.tvCenterSixRight, false)
                } else {
                    helper.setGone(R.id.tvCenterSixRight, true)
                }
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                helper.setGone(R.id.icon_play, false)
                val album = item.album_data
                val num = String.format(
                    context.resources.getString(R.string.recommend_album_count),
                    album.include_track_count.toString()
                )
                helper.setGone(R.id.llCenterSix, false)
                helper.setGone(R.id.tvCenterSixLeft, false)
                helper.setGone(R.id.tvCenterSixRight, true)
                helper.setText(R.id.tvCenterSixLeft, num)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                helper.setGone(R.id.llCenterSix, false)
                if (!TextUtils.isEmpty(item.staff)) {
                    helper.setText(R.id.tvCenterSixLeft, item.staff)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                if (!TextUtils.isEmpty(item.source)) {
                    helper.setText(R.id.tvCenterSixRight, item.source)
                    helper.setGone(R.id.tvCenterSixRight, false)
                } else {
                    helper.setGone(R.id.tvCenterSixRight, true)
                }
            }
            ResourceTypeConstans.TYPE_TASK -> {
                helper.setGone(R.id.llCenterSix, false)
                helper.setGone(R.id.tvCenterSixRight, true)
                if (!TextUtils.isEmpty(item.success_score)) {
                    helper.setText(
                        R.id.tvCenterSixLeft,
                        getSpaString(
                            item.success_score,
                            ContextCompat.getColor(context, R.color.color_5D5D5D),
                            ContextCompat.getColor(context, R.color.color_5D5D5D),
                            11.sp2px(),
                            15.sp2px()
                        )
                    )
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                helper.setGone(R.id.time, false)
                helper.setText(
                    R.id.time,
                    (if (item.join_num.isNullOrEmpty()) "0" else item.join_num) + "人参与"
                )
            }
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {
                helper.setGone(R.id.llCenterSix, false)
                if (!TextUtils.isEmpty(item.staff)) {
                    helper.setText(R.id.tvCenterSixLeft, item.staff)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                if (!TextUtils.isEmpty(item.source)) {
                    helper.setText(R.id.tvCenterSixRight, item.source)
                    helper.setGone(R.id.tvCenterSixRight, false)
                } else {
                    helper.setGone(R.id.tvCenterSixRight, true)
                }
            }
            ResourceTypeConstans.TYPE_COLUMN -> {
                helper.setGone(R.id.tvCenterSixRight, true)
                if (!TextUtils.isEmpty(item.desc)) {
                    helper.setGone(R.id.llCenterSix, false)
                    helper.setText(R.id.tvCenterSixLeft, item.desc)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.llCenterSix, true)
                    helper.setGone(R.id.tvCenterSixLeft, true)

                }
                if (!TextUtils.isEmpty(item.update_time)) {
                    helper.setGone(R.id.time, false)
                    helper.setText(R.id.time, item.update_time)
                } else {
                    helper.setGone(R.id.time, true)
                }

            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                helper.setGone(R.id.llCenterSix, false)
                if (!TextUtils.isEmpty(item.writer)) {
                    helper.setText(R.id.tvCenterSixLeft, item.writer)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                if (!TextUtils.isEmpty(item.platform)) {
                    helper.setText(R.id.tvCenterSixRight, item.platform)
                    helper.setGone(R.id.tvCenterSixRight, false)
                } else {
                    helper.setGone(R.id.tvCenterSixRight, true)
                }
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                helper.setGone(R.id.tvCenterSixRight, true)
                if (!TextUtils.isEmpty(item.staff)) {
                    helper.setGone(R.id.llCenterSix, false)
                    helper.setText(R.id.tvCenterSixLeft, item.staff)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.llCenterSix, true)
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                helper.setText(R.id.time, item.student_num.toString() + "人")
                helper.setGone(R.id.time, false)

            }
            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {

            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                helper.setGone(R.id.icon_play, false)
                val musicBean = item.audio_data
                helper.setGone(R.id.llCenterSix, false)
                if (musicBean != null
                    && !TextUtils.isEmpty(formatPlayCount(musicBean.audio_play_num))
                ) {
                    helper.setText(
                        R.id.tvCenterSixLeft,
                        String.format("播放 %s次", formatPlayCount(musicBean.audio_play_num))
                    )
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                if (musicBean != null
                    && !TextUtils.isEmpty(timeParse(musicBean.audio_time))
                ) {
                    helper.setText(R.id.tvCenterSixRight, timeParse(musicBean.audio_time))
                    helper.setGone(R.id.tvCenterSixRight, false)
                } else {
                    helper.setGone(R.id.tvCenterSixRight, true)
                }
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                val updateStr =
                    "更新至" + item.periodicals_data.year + "年 第" + item.periodicals_data.term + "期"
                helper.setGone(R.id.llCenterSix, false)
                helper.setGone(R.id.tvCenterSixLeft, false)
                helper.setGone(R.id.tvCenterSixRight, true)
                helper.setText(R.id.tvCenterSixLeft, updateStr)
                helper.setText(R.id.time, item.periodicals_data.unit)
                helper.setGone(R.id.time, false)

            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业

            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识
                helper.setGone(R.id.tvCenterSixRight, true)
                if (!TextUtils.isEmpty(item.resource_info?.title)) {
                    helper.setGone(R.id.llCenterSix, false)
                    helper.setText(R.id.tvCenterSixLeft, item.resource_info?.title)
                    helper.setGone(R.id.tvCenterSixLeft, false)
                } else {
                    helper.setGone(R.id.llCenterSix, true)
                    helper.setGone(R.id.tvCenterSixLeft, true)
                }
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    helper.setText(R.id.time, item.resource_info?.updated_time + "更新")
                    helper.setGone(R.id.time, false)
                } else {
                    helper.setGone(R.id.time, true)
                }
            }
            else -> {

            }
        }

    }

    private fun setSevenData(helper: BaseViewHolder, item: RecommendContentBean.DataBean) {
        val itemViewType: Int = helper.itemViewType
        if (itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) return

        helper.setGone(R.id.icon_play, true)

        helper.setText(R.id.tvFrom, "")
        helper.setText(R.id.org, "")
        helper.setText(R.id.time, "")
        helper.setText(R.id.title, "")
        helper.setGone(R.id.tvFrom, false)
        val type = typeStringMap[item.type]
        helper.setText(R.id.tv_identify, type)
        helper.setGone(R.id.tv_identify, TextUtils.isEmpty(type))
        helper.setText(R.id.tvTypeListSeven, type)
        helper.setGone(R.id.tvTypeListSeven, true)

        val image: String =
            if (TextUtils.isEmpty(item.getBig_image())) item.getSmall_image() else item.getBig_image()
        //默认一个图片一个标题
        helper.setText(R.id.title, item.title)
        Glide.with(context)
            .load(image)
            .placeholder(R.mipmap.common_bg_cover_big_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into((helper.getView<View>(R.id.cover) as ImageView))


        when (item.type) {
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    helper.setText(R.id.tv_identify, item.identity_name)
                }
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    helper.setText(R.id.time, item.resource_info?.updated_time + "更新")
                }
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                if (!TextUtils.isEmpty(item.video_duration)) {
                    val video_duration: Long
                    video_duration = try {
                        item.video_duration.toLong()
                    } catch (e: java.lang.NumberFormatException) {
                        0
                    }
                    helper.setText(R.id.time, timeParse(video_duration))
                }
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                helper.setText(R.id.time, item.org)
                if (!TextUtils.isEmpty(item.identity_name)) {
                    helper.setText(R.id.tvTypeListSeven, item.identity_name)
                    helper.setGone(R.id.tvTypeListSeven, false)
                    helper.setGone(R.id.tv_identify, true)
                } else {
                    helper.setGone(R.id.tvTypeListSeven, true)
                    helper.setGone(R.id.tv_identify, false)
                }
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                helper.setGone(R.id.icon_play, false)
                val (_, _, _, _, subordinated_album, _, _, _, _, duration) = item.track_data
                if (subordinated_album != null) {
                    helper.setText(R.id.time, subordinated_album!!.album_title)
                }
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                helper.setGone(R.id.icon_play, false)
                val album = item.album_data
                val num = String.format(
                    context.resources.getString(R.string.recommend_album_count),
                    album.include_track_count.toString()
                )
                helper.setText(R.id.time, num)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                helper.setText(R.id.time, item.staff)
            }
            ResourceTypeConstans.TYPE_TASK -> {
                helper.setText(
                    R.id.time,
                    (if (item.join_num.isNullOrEmpty()) "0" else item.join_num) + "人参与"
                )
            }
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {
                helper.setText(R.id.time, item.staff)
            }
            ResourceTypeConstans.TYPE_COLUMN -> {
                helper.setText(R.id.time, item.update_time)

            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                helper.setText(R.id.time, item.writer)
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                helper.setText(R.id.time, item.staff)
            }
            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
                helper.setText(R.id.org, item.about)
                helper.setText(R.id.time, item.publish_time)
                helper.setGone(R.id.time, false)

            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                helper.setGone(R.id.icon_play, false)
                val musicBean = item.audio_data
                try {
                    helper.setText(R.id.time, timeParse(musicBean.audio_time))
                } catch (e: Exception) {
                    helper.setText(R.id.time, "")
                }
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                val updateStr =
                    "更新至" + item.periodicals_data.year + "年 第" + item.periodicals_data.term + "期"
                helper.setText(R.id.time, updateStr)

            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业

            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    helper.setText(R.id.time, item.resource_info?.updated_time + "更新")
                }
            }
            else -> {


            }
        }

        //判断中间行是否有文字,如果都没有则gone
        val org = helper.getViewOrNull<TextView>(R.id.org)
        val tvFrom = helper.getViewOrNull<TextView>(R.id.tvFrom)

        val layoutParams = org?.layoutParams as ConstraintLayout.LayoutParams
        if (item.type == ResourceTypeConstans.TYPE_ARTICLE || item.type == ResourceTypeConstans.TYPE_COLUMN_ARTICLE) {
            layoutParams.matchConstraintPercentWidth = 0.5f
        } else {
            layoutParams.matchConstraintPercentWidth = 0.7f
        }
        org.layoutParams = layoutParams


        if (TextUtils.isEmpty(org.text) && TextUtils.isEmpty(tvFrom?.text)) {
            helper.setGone(R.id.org, true)
            helper.setGone(R.id.tvFrom, true)
        } else {
            helper.setGone(R.id.org, false)
            helper.setGone(R.id.tvFrom, false)
        }
    }


    private fun setViewVisibility(textView: TextView, str: String) {
        if (!TextUtils.isEmpty(str)) {
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
    }

    private fun setViewVisibility(textView: TextView, line: View, str: String) {
        if (!TextUtils.isEmpty(str)) {
            textView.visibility = View.VISIBLE
            line.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
            line.visibility = View.GONE
        }
    }

    private fun getTime(str: String): String {
        var tempStr = str
        if (!TextUtils.isEmpty(tempStr)) {
            val times = tempStr.split("-".toRegex()).toTypedArray()
            val start: String
            val end: String
            if (times.size > 1) {
                start = setFirstTime(times[0]).toString()
                end = setFirstTime(times[1]).toString()
                tempStr = "$start-$end"
            } else {
                tempStr = setFirstTime(times[0]).toString()
            }
        }
        return tempStr
    }

    private fun setFirstTime(str: String): String {
        var tempStr = str
        if (!TextUtils.isEmpty(tempStr)) {
            val s = tempStr.split("月".toRegex()).toTypedArray()
            var first = s[0]
            if (first.toInt() <= 9) {
                val monStrings = first.split("0".toRegex()).toTypedArray()
                first = monStrings[1]
                tempStr = first + "月" + s[1]
            }
        }
        return tempStr
    }

    private fun setViewVisibility(textView: TextView, line: View, a: Int) {
        if (a > 0) {
            textView.visibility = View.VISIBLE
            line.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
            line.visibility = View.GONE
        }
    }

    private fun setViewVisibility(textView: TextView, a: Int) {
        if (a > 0) {
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
    }
}