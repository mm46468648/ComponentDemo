package com.mooc.microknowledge.ui

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.getDrawableRes
import com.mooc.common.utils.TimeUtils.timeParse
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.typeStringMap
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.formatPlayCount
import com.mooc.microknowledge.R
import com.mooc.microknowledge.model.KnowledgeResource
import com.mooc.resource.widget.tagtext.TagTextView

class MicroKnowLedgeResourceAdapter(list: ArrayList<KnowledgeResource>) :
    BaseDelegateMultiAdapter<KnowledgeResource, BaseViewHolder>(list) {
    companion object {
        const val TYPE_RECOMMEND_STYLE_DEFAULT = -1
        const val TYPE_RECOMMEND_STYLE_ONE = 0
        const val TYPE_RECOMMEND_STYLE_TWO = 1
        const val TYPE_RECOMMEND_STYLE_THREE = 2
        const val TYPE_RECOMMEND_STYLE_FOUR = 3
        const val TYPE_RECOMMEND_STYLE_FIVE = 4
        const val TYPE_RECOMMEND_STYLE_SIX = 5     //无模板样式
        const val TYPE_RECOMMEND_STYLE_SEVEN = 6
        const val TYPE_RECOMMEND_STYLE_EIGHT = 7
    }

    val typeArray = -1..7    //支持的模板区间

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<KnowledgeResource>() {
            override fun getItemType(
                data: List<KnowledgeResource>,
                position: Int
            ): Int {
                val dataBean = data[position]
                return if (dataBean.classType in typeArray) dataBean.classType else TYPE_RECOMMEND_STYLE_SIX
            }
        })
        // //两个类型，课程和文章 （有图没有图的）
        getMultiTypeDelegate()
            ?.addItemType(TYPE_RECOMMEND_STYLE_DEFAULT, R.layout.knowledge_item_style_default)
            ?.addItemType(TYPE_RECOMMEND_STYLE_ONE, R.layout.knowledge_item_style_one)
            ?.addItemType(TYPE_RECOMMEND_STYLE_TWO, R.layout.knowledge_item_style_two)
            ?.addItemType(TYPE_RECOMMEND_STYLE_THREE, R.layout.knowledge_item_style_three)
            ?.addItemType(TYPE_RECOMMEND_STYLE_FOUR, R.layout.knowledge_item_style_four)
            ?.addItemType(TYPE_RECOMMEND_STYLE_FIVE, R.layout.knowledge_item_style_five)
            ?.addItemType(TYPE_RECOMMEND_STYLE_SIX, R.layout.knowledge_item_style_six)
            ?.addItemType(TYPE_RECOMMEND_STYLE_SEVEN, R.layout.knowledge_item_style_seven)
            ?.addItemType(TYPE_RECOMMEND_STYLE_EIGHT, R.layout.knowledge_item_style_eight)
    }


    override fun convert(holder: BaseViewHolder, item: KnowledgeResource) {
        when (holder.itemViewType) {
            TYPE_RECOMMEND_STYLE_DEFAULT -> {
                setDefaultData(holder, item)
            }
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
            TYPE_RECOMMEND_STYLE_SIX -> {
                setSixData(holder, item)
            }
            TYPE_RECOMMEND_STYLE_SEVEN -> {
                setSevenData(holder, item)
            }
            TYPE_RECOMMEND_STYLE_EIGHT -> {
                setEightData(holder, item)
            }
        }
    }

    private fun sp2px(spValue: Float): Int {
        val fontScale: Float = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    private fun setDefaultData(holder: BaseViewHolder, item: KnowledgeResource) {
        //设置资源内容
        when (item.resource_type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setGone(R.id.includeCourse, false)
                holder.setGone(R.id.includeEbook, true)
                holder.setGone(R.id.includeAlbum, true)
                holder.setGone(R.id.includeSelfBuiltAudio, true)
                holder.setGone(R.id.includeTrack, true)
                holder.setGone(R.id.includeArticle, true)
                holder.setGone(R.id.includePeriodical, true)
                holder.setGone(R.id.includeSpecial, true)
                holder.setGone(R.id.includeTask, true)
                holder.setGone(R.id.includeFollowUp, true)
                setCourseData(holder, item)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setGone(R.id.includeCourse, true)
                holder.setGone(R.id.includeEbook, false)
                holder.setGone(R.id.includeAlbum, true)
                holder.setGone(R.id.includeSelfBuiltAudio, true)
                holder.setGone(R.id.includeTrack, true)
                holder.setGone(R.id.includeArticle, true)
                holder.setGone(R.id.includePeriodical, true)
                holder.setGone(R.id.includeSpecial, true)
                holder.setGone(R.id.includeTask, true)
                holder.setGone(R.id.includeFollowUp, true)
                setEBookData(holder, item)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.includeCourse, true)
                holder.setGone(R.id.includeEbook, true)
                holder.setGone(R.id.includeAlbum, false)
                holder.setGone(R.id.includeSelfBuiltAudio, true)
                holder.setGone(R.id.includeTrack, true)
                holder.setGone(R.id.includeArticle, true)
                holder.setGone(R.id.includePeriodical, true)
                holder.setGone(R.id.includeSpecial, true)
                holder.setGone(R.id.includeTask, true)
                holder.setGone(R.id.includeFollowUp, true)
                setAlbumData(holder, item)
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.includeCourse, true)
                holder.setGone(R.id.includeEbook, true)
                holder.setGone(R.id.includeAlbum, true)
                holder.setGone(R.id.includeSelfBuiltAudio, false)
                holder.setGone(R.id.includeTrack, true)
                holder.setGone(R.id.includeArticle, true)
                holder.setGone(R.id.includePeriodical, true)
                holder.setGone(R.id.includeSpecial, true)
                holder.setGone(R.id.includeTask, true)
                holder.setGone(R.id.includeFollowUp, true)
                setOwnTrackData(holder, item)
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.includeCourse, true)
                holder.setGone(R.id.includeEbook, true)
                holder.setGone(R.id.includeAlbum, true)
                holder.setGone(R.id.includeSelfBuiltAudio, true)
                holder.setGone(R.id.includeTrack, false)
                holder.setGone(R.id.includeArticle, true)
                holder.setGone(R.id.includePeriodical, true)
                holder.setGone(R.id.includeSpecial, true)
                holder.setGone(R.id.includeTask, true)
                holder.setGone(R.id.includeFollowUp, true)
                setTrackData(holder, item)
            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE,
            ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setGone(R.id.includeCourse, true)
                holder.setGone(R.id.includeEbook, true)
                holder.setGone(R.id.includeAlbum, true)
                holder.setGone(R.id.includeSelfBuiltAudio, true)
                holder.setGone(R.id.includeTrack, true)
                holder.setGone(R.id.includeArticle, false)
                holder.setGone(R.id.includePeriodical, true)
                holder.setGone(R.id.includeSpecial, true)
                holder.setGone(R.id.includeTask, true)
                holder.setGone(R.id.includeFollowUp, true)
                setArticleData(holder, item)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setGone(R.id.includeCourse, true)
                holder.setGone(R.id.includeEbook, true)
                holder.setGone(R.id.includeAlbum, true)
                holder.setGone(R.id.includeSelfBuiltAudio, true)
                holder.setGone(R.id.includeTrack, true)
                holder.setGone(R.id.includeArticle, true)
                holder.setGone(R.id.includePeriodical, false)
                holder.setGone(R.id.includeSpecial, true)
                holder.setGone(R.id.includeTask, true)
                holder.setGone(R.id.includeFollowUp, true)
                setPeriodicalData(holder, item)
            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                holder.setGone(R.id.includeCourse, true)
                holder.setGone(R.id.includeEbook, true)
                holder.setGone(R.id.includeAlbum, true)
                holder.setGone(R.id.includeSelfBuiltAudio, true)
                holder.setGone(R.id.includeTrack, true)
                holder.setGone(R.id.includeArticle, true)
                holder.setGone(R.id.includePeriodical, true)
                holder.setGone(R.id.includeSpecial, false)
                holder.setGone(R.id.includeTask, true)
                holder.setGone(R.id.includeFollowUp, true)
                setSpecial(holder, item)
            }
            ResourceTypeConstans.TYPE_TASK -> {
                holder.setGone(R.id.includeCourse, true)
                holder.setGone(R.id.includeEbook, true)
                holder.setGone(R.id.includeAlbum, true)
                holder.setGone(R.id.includeSelfBuiltAudio, true)
                holder.setGone(R.id.includeTrack, true)
                holder.setGone(R.id.includeArticle, true)
                holder.setGone(R.id.includePeriodical, true)
                holder.setGone(R.id.includeSpecial, true)
                holder.setGone(R.id.includeTask, false)
                holder.setGone(R.id.includeFollowUp, true)
                setTaskData(holder, item)
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                holder.setGone(R.id.includeCourse, true)
                holder.setGone(R.id.includeEbook, true)
                holder.setGone(R.id.includeAlbum, true)
                holder.setGone(R.id.includeSelfBuiltAudio, true)
                holder.setGone(R.id.includeTrack, true)
                holder.setGone(R.id.includeArticle, true)
                holder.setGone(R.id.includePeriodical, true)
                holder.setGone(R.id.includeSpecial, true)
                holder.setGone(R.id.includeTask, true)
                holder.setGone(R.id.includeFollowUp, false)
                setFollowUpData(holder, item)
            }
        }
    }

    private fun setSpecial(holder: BaseViewHolder, item: KnowledgeResource) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleSpecial)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        tagView.setTagStart(arrayListOf("专题"), item.title)

    }

    private fun setFollowUpData(holder: BaseViewHolder, item: KnowledgeResource) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleFollowUp)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        tagView.setTagStart(arrayListOf("跟读"), item.name)
    }

    private fun setAlbumData(holder: BaseViewHolder, item: KnowledgeResource) {
        holder.setText(R.id.tvTitleAlbum, item.album_title)
        holder.setText(
            R.id.tvPlayNumAlbum,
            formatPlayCount(item.play_count)
        )
        holder.setText(R.id.tvCollectionAlbum, "${item.include_track_count}集")
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgAlbum)
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_square_default)
            .placeholder(R.mipmap.common_bg_cover_square_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
    }

    private fun setTrackData(holder: BaseViewHolder, item: KnowledgeResource) {
        holder.setText(R.id.tvTitleAudio, item.track_title)
        holder.setText(R.id.tvSourceAudio, item.albumTitle)
        holder.setText(R.id.tvPlayNumAudio, formatPlayCount(item.play_count))
        holder.setText(R.id.tvDurationAudio, timeParse(item.duration))
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getView<ImageView>(R.id.ivImgAudio)
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_square_default)
            .placeholder(R.mipmap.common_bg_cover_square_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView)
    }

    private fun setOwnTrackData(holder: BaseViewHolder, item: KnowledgeResource) {
        holder.setText(R.id.tvTitleSelfBuildAudio, item.title)
        holder.setText(
            R.id.tvPlayNumSelfBuildAudio,
            formatPlayCount(item.audio_play_num)
        )
        holder.setText(R.id.tvDurationSelfBuildAudio, timeParse(item.audio_time))
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getView<ImageView>(R.id.ivImgSelfBuildAudio)
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_square_default)
            .placeholder(R.mipmap.common_bg_cover_square_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView)
    }

    private fun setArticleData(holder: BaseViewHolder, item: KnowledgeResource) {
        holder.setText(R.id.tvTitleArticle, item.title)

        var sourceStr = item.platform_zh
        if (!TextUtils.isEmpty(item.source)) {
            sourceStr += " | ${item.source}"
        }
        holder.setText(R.id.tvSourceArticle, sourceStr)

        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgArticle)
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
    }

    private fun setPeriodicalData(holder: BaseViewHolder, item: KnowledgeResource) {
        holder.setText(R.id.tvTitlePeriodical, item.title)

        holder.setText(R.id.tvSourcePeriodical, item.source_zh)
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgPeriodical)
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
    }

    private fun setTaskData(holder: BaseViewHolder, item: KnowledgeResource) {
        holder.setText(R.id.tvTitleTask, item.title)

        var peoNum = "${item.join_num}人参与"
        if (item.is_limit_num) { //限制人数
            peoNum = "${item.join_num}/${item.limit_num}人参与"
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


        if (item.time_mode == 1) {
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

        if (!TextUtils.isEmpty(item.score?.success_score)) {
            holder.setGone(R.id.tvRewardTask, false)
            holder.setGone(R.id.tvRewardScoreTask, false)
            holder.setText(R.id.tvRewardScoreTask, item.score?.success_score)
        } else {
            holder.setGone(R.id.tvRewardTask, true)
            holder.setGone(R.id.tvRewardScoreTask, true)
        }

//        holder.setBackgroundResource(R.id.tvTaskStatus, R.drawable.shape_button_look_details_white)
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
        when (item.status) {
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

    private fun setEBookData(holder: BaseViewHolder, item: KnowledgeResource) {
        holder.setText(R.id.tvTitleEbook, item.title)
        holder.setText(R.id.tvAuthorEbook, item.writer)
        holder.setText(R.id.tvSourceEbook, item.source_zh + " | " + item.press)
        holder.setText(
            R.id.tvWordNumEbook,
            formatPlayCount(item.word_count) + "字"
        )
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgEbook)
        imageView?.let {
            Glide.with(context)
                .load(image)
                .error(R.mipmap.common_bg_cover_vertical_default)
                .placeholder(R.mipmap.common_bg_cover_vertical_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }
    }

    private fun setCourseData(holder: BaseViewHolder, item: KnowledgeResource) {
        holder.setText(R.id.tvTitleCourse, item.title)
        holder.setText(R.id.tvSourceCourse, item.source_zh)
        holder.setText(R.id.tvAuthorCourse, item.org)
        holder.setText(R.id.tvOpeningCourse, item.course_start_time)
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgCourse)
        imageView?.let {
            Glide.with(context)
                .load(image)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }

        //免费付费等信息
        setCourseTypeInfo(holder.getView(R.id.tvStatusCourse), item)
    }


    /**
     * 设置课程类型信息
     * (考试,证书,付费)
     * 中间点的规则,如果前面点亮,点也点亮
     */
    private fun setCourseTypeInfo(textView: TextView, bean: KnowledgeResource) {
        val isHaveExam: Int = bean.is_have_exam
        val isVerified: Boolean = bean.verified_active
        val isFree: Boolean = bean.is_free
        var detail = ""
        val dot = " · "
        //是否有考试
        if (isHaveExam == 1) {
            detail += context.resources.getString(R.string.course_str_have_exam)
        } else {
            detail += context.resources.getString(R.string.course_str_no_exam)
        }
        detail += dot
        //是否有证书
        if (isVerified) {
            detail += context.resources.getString(R.string.course_str_have_certificate)
        } else {
            detail += context.resources.getString(R.string.course_str_no_certificate)
        }
        detail += dot
        //是否免费
        if (!isFree) {
            detail += context.resources.getString(R.string.course_str_pay)
        } else {
            detail += context.resources.getString(R.string.course_str_free)
        }

//        val detailColor = if (SkinManager.getInstance().needChangeSkin()) {
//            SkinManager.getInstance().resourceManager.getColor("colorPrimary")
//        } else {
//            context.getColorRes(R.color.colorPrimary)
//        }

        val detailColor = context.getColorRes(R.color.colorPrimary)

        var spannableString = spannableString {
            str = detail
        }

        if (detail.contains("有证书 · ")) {
            spannableString = spannableString {
                str = spannableString
                colorSpan {
                    color = detailColor
                    start = detail.indexOf("有证书 · ")
                    end = detail.indexOf("有证书 · ") + 5
                }
            }
        }

        if (detail.contains("有考试 · ")) {
            spannableString = spannableString {
                str = spannableString
                colorSpan {
                    color = detailColor
                    start = detail.indexOf("有考试 · ")
                    end = detail.indexOf("有考试 · ") + 5
                }
            }
        }

        if (detail.contains("免费")) {
            spannableString = spannableString {
                str = spannableString
                colorSpan {
                    color = detailColor
                    start = detail.indexOf("免费")
                    end = detail.indexOf("免费") + 2
                }
            }
        }
        textView.text = spannableString
    }

    private fun setOneData(holder: BaseViewHolder, item: KnowledgeResource) {
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

        val value = typeStringMap[item.resource_type]
        holder.setText(R.id.tv_type_one, value)
        holder.setGone(R.id.tv_type_one, TextUtils.isEmpty(value))
        when (item.resource_type) {

            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setText(R.id.tv_type_one, R.string.course)
                holder.setText(R.id.tv_center_style_one_resource, item.org)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.course_start_time)
            }

            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.tv_type_one, R.string.ebook)
                holder.setText(R.id.tv_center_style_one_resource, item.writer)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.source_zh)
            }

            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setText(R.id.tv_type_one, R.string.album)
                holder.setGone(R.id.ivPlay, false)
                holder.setText(
                    R.id.tv_center_style_one_resource,
                    "${item.include_track_count}集"
                )
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setText(R.id.tv_type_one, R.string.track)
                holder.setGone(R.id.ivPlay, false)
                holder.setText(
                    R.id.tv_center_style_one_resource,
                    item.album_title
                )
                holder.setText(
                    R.id.tv_bottom_left_style_one_resource,
                    timeParse(item.duration)
                )

            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.tv_type_one, R.string.periodical)
                holder.setText(R.id.tv_center_style_one_resource, item.staff)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.source_zh)

            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {//专栏文章
                holder.setText(R.id.tv_type_one, R.string.article)
                holder.setText(R.id.tv_center_style_one_resource, item.staff)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.source_zh)
            }

            ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setText(R.id.tv_type_one, R.string.article)
                holder.setText(R.id.tv_center_style_one_resource, item.staff)
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.source_zh)
            }
            ResourceTypeConstans.TYPE_SPECIAL -> {//合集
//                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
//                    holder.setText(R.id.tv_type_one, item.identity_name)
//                } else {
//                    holder.setText(R.id.tv_type_one, R.string.special)
//                }
                holder.setText(R.id.tv_center_style_one_resource, "")


            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读
                holder.setText(R.id.tv_center_style_one_resource, "")

            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {////自建音频
                holder.setText(R.id.tv_type_one, R.string.oneself_track)
                holder.setGone(R.id.ivPlay, false)

                holder.setText(
                    R.id.tv_center_style_one_resource,
                    "播放 ${formatPlayCount(item.audio_play_num)}次"

                )
                holder.setText(
                    R.id.tv_bottom_left_style_one_resource,
                    timeParse(item.audio_time)
                )


            }

            ResourceTypeConstans.TYPE_TASK -> {
                holder.setText(R.id.tv_type_one, R.string.str_task)
                if (!TextUtils.isEmpty(item.success_score)) {
                    val successScoreStr = "奖励积分 " + item.success_score
                    val spannableString = SpannableString(successScoreStr)
                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(13f)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(15f)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.setText(R.id.tv_center_style_one_resource, spannableString)

                }
                holder.setText(R.id.tv_bottom_left_style_one_resource, item.join_num + "人参与")
            }

            else -> {
                holder.setGone(R.id.tv_reasons_one, true)
                holder.setGone(R.id.ivPlay, true)
                holder.setGone(R.id.tv_type_one, true)
            }
        }
    }

    private fun setTwoData(holder: BaseViewHolder, item: KnowledgeResource) {
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

        val value = typeStringMap[item.resource_type]
        holder.setText(R.id.tv_type, value)
        holder.setGone(R.id.tv_type, TextUtils.isEmpty(value))


        when (item.resource_type) {

            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setText(R.id.tv_type, R.string.course)

                holder.setGone(R.id.rlLeftLineRight, false)

                holder.setText(R.id.tvLineRight, item.course_start_time)
                holder.setText(R.id.tvLineLift, item.source_zh)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.tv_type, R.string.ebook)

                holder.setGone(R.id.tvCenterOneStyleTwo, false)

                holder.setText(R.id.tvCenterOneStyleTwo, item.staff)
                holder.setText(R.id.tvLineLift, item.writer)


            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setText(R.id.tv_type, R.string.album)
                holder.setGone(R.id.tvCenterOneStyleTwo, false)

                val num: String = item.include_track_count.toString() + "集"
                holder.setText(R.id.tvCenterOneStyleTwo, num)

            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setText(R.id.tv_type, R.string.track)
                holder.setText(R.id.tvLineLift, item.album_title)
                holder.setText(R.id.tvLineRight, timeParse(item.duration))
                holder.setGone(R.id.viewLine, item.duration == 0L)
                holder.setGone(R.id.tvLineRight, item.duration == 0L)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.tv_type, R.string.periodical)
                holder.setGone(R.id.rlLeftLineRight, false)

                setViewVisibility(
                    holder.getView(R.id.tvLineLift),
                    holder.getView(R.id.viewLine),
                    item.staff
                )
                setViewVisibility(
                    holder.getView(R.id.tvLineRight),
                    holder.getView(R.id.viewLine),
                    item.source_zh
                )
                holder.setText(R.id.tvLineLift, item.staff)
                holder.setText(R.id.tvLineRight, item.source_zh)

            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setText(R.id.tv_type, R.string.article)
                holder.setGone(R.id.rlLeftLineRight, false)
                setViewVisibility(
                    holder.getView(R.id.tvLineRight),
                    holder.getView(R.id.viewLine),
                    item.source_zh
                )
                holder.setText(
                    R.id.tvLineLift,
                    if (TextUtils.isEmpty(item.staff)) "-" else item.staff
                )
                holder.setText(R.id.tvLineRight, item.source_zh)
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读

            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
//                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
//                    holder.setText(R.id.tv_type, item.identity_name)
//                } else {
//                    holder.setText(R.id.tv_type, R.string.special)
//                }
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setText(R.id.tv_type, R.string.oneself_track)

                holder.setGone(R.id.rlLeftLineRight, false)

                holder.setText(R.id.tvLineLift, "播放 ${item.audio_play_num}次")
                holder.setGone(R.id.viewLine, item.audio_time == 0L)
                holder.setGone(R.id.tvLineRight, item.audio_time == 0L)
                holder.setText(R.id.tvLineRight, timeParse(item.audio_time))
            }
            ResourceTypeConstans.TYPE_TASK -> {
                holder.setText(R.id.tv_type, R.string.str_task)
                holder.setGone(R.id.rlLeftLineRight, false)
                setViewVisibility(
                    holder.getView(R.id.tvLineRight),
                    holder.getView(R.id.viewLine),
                    item.join_num
                )
                if (!TextUtils.isEmpty(item.success_score)) {
                    val successScoreStr = "奖励积分 " + item.success_score
                    val spannableString = SpannableString(successScoreStr)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_c)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_white)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(12f)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(15f)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.setText(R.id.tvLineLift, spannableString)

                } else {
                    holder.setText(R.id.tvLineLift, "-")
                }

                holder.setText(R.id.tvLineRight, item.join_num + "人参与")
            }
            else -> {
                holder.setGone(R.id.tvCenterOneStyleTwo, true)
                holder.setGone(R.id.tvCenterTwoStyleTwo, true)
                holder.setGone(R.id.tvCenterThreeStyleTwo, true)
                holder.setGone(R.id.rlLeftLineRight, true)
            }
        }
    }

    private fun setThreeData(holder: BaseViewHolder, item: KnowledgeResource) {
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        Glide.with(context)
            .load(image)
            .transform(GlideTransform.centerCropAndRounder2)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .into(holder.getView(R.id.iv_recommend_style_three))


        setViewVisibility(holder.getView(R.id.tv_reasons_three), item.recommend_reason)
        holder.setGone(R.id.tv_reasons_three, true)
        holder.setText(R.id.tvTitleStyleThree, item.title)
        holder.setText(R.id.tv_right_style_three, "")
        holder.setText(R.id.tvBottomStyleThree, "")
//        holder.setGone(R.id.tv_right_style_three, false)

        holder.setGone(R.id.tvCenterOneStyleThree, true)
        holder.setGone(R.id.tvCenterTwoStyleThree, true)
        holder.setGone(R.id.tvBottomStyleThree, false)
        holder.setGone(R.id.ivPlay, true)

        val value = typeStringMap[item.resource_type]
        holder.setText(R.id.tv_type_three, value)
        holder.setGone(R.id.tv_type_three, TextUtils.isEmpty(value))

        when (item.resource_type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setGone(R.id.tv_type_three, false)
                holder.setText(R.id.tv_type_three, R.string.course)
                setViewVisibility(holder.getView(R.id.tvCenterOneStyleThree), item.source_zh)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
                holder.setText(R.id.tvBottomStyleThree, item.course_start_time)
                holder.setText(R.id.tvCenterOneStyleThree, item.source_zh)

            }

            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setGone(R.id.tv_type_three, false)
                holder.setText(R.id.tv_type_three, R.string.periodical)
                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvCenterOneStyleThree, item.staff)
                holder.setText(R.id.tv_right_style_three, item.source_zh)
                holder.setText(R.id.tvBottomStyleThree, item.desc)

            }

            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setGone(R.id.tv_type_three, false)
                holder.setText(R.id.tv_type_three, R.string.article)
                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvCenterOneStyleThree, item.staff)
                holder.setText(R.id.tvBottomStyleThree, item.desc)
                holder.setText(R.id.tv_right_style_three, item.source_zh)

            }

            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setGone(R.id.tv_type_three, false)
                holder.setText(R.id.tv_type_three, R.string.ebook)
                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvCenterOneStyleThree, item.writer)
                holder.setText(R.id.tvBottomStyleThree, item.desc)
                holder.setText(R.id.tv_right_style_three, item.source_zh)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.tv_type_three, false)
                holder.setText(R.id.tv_type_three, R.string.album)
                holder.setGone(R.id.ivPlay, false)

                setViewVisibility(
                    holder.getView(R.id.tvCenterTwoStyleThree),
                    item.include_track_count.toString()
                )
                val num = "${item.include_track_count}集"
                holder.setText(R.id.tvCenterTwoStyleThree, num)

                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvBottomStyleThree, item.desc)


            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.tv_type_three, false)
                holder.setText(R.id.tv_type_three, R.string.track)
                holder.setGone(R.id.ivPlay, false)


                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.setText(R.id.tvCenterOneStyleThree, item.album_title)
                holder.setText(R.id.tv_right_style_three, timeParse(item.duration))

                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvBottomStyleThree, item.desc)

            }

            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                holder.setGone(R.id.tv_type_three, false)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1

            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                holder.setGone(R.id.tv_type_three, false)
//                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
//                    holder.setText(R.id.tv_type_three, item.identity_name)
//                } else {
//                    holder.setText(R.id.tv_type_three, R.string.special)
//                }
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
                holder.setGone(R.id.tv_type_three, false)
                holder.setText(R.id.tv_type_three, R.string.oneself_track)
                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.setText(R.id.tv_right_style_three, timeParse(item.audio_time))
                holder.setText(
                    R.id.tvCenterOneStyleThree,
                    "播放 ${formatPlayCount(item.audio_play_num)}次"
                )

                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 2
                holder.setText(R.id.tvBottomStyleThree, item.desc)
            }

            ResourceTypeConstans.TYPE_TASK -> {
                holder.setGone(R.id.tv_type_three, false)
                holder.setText(R.id.tv_type_three, R.string.str_task)
                holder.setGone(R.id.tvCenterOneStyleThree, false)
                holder.setGone(R.id.tvCenterTwoStyleThree, false)
                holder.setGone(R.id.tvBottomStyleThree, false)
                holder.getView<TextView>(R.id.tvBottomStyleThree).maxLines = 1
                if (!TextUtils.isEmpty(item.success_score)) {
                    val successScoreStr = "奖励积分 " + item.success_score
                    val spannableString = SpannableString(successScoreStr)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_6B)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_5D5D5D)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(13f)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(15f)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.setText(R.id.tvCenterOneStyleThree, spannableString)

                } else {
                    holder.setText(R.id.tvCenterOneStyleThree, "")
                }

                holder.setText(R.id.tv_right_style_three, item.join_num + "人参与")
                if (item.time_mode == 1) {
                    holder.setText(R.id.tvCenterTwoStyleThree, "报名时间：永久开放")
                    holder.setText(R.id.tvBottomStyleThree, "任务时间：永久开放")
                } else {

                    if (item.start_time.isEmpty() && item.end_time.isEmpty()) {
                        holder.setText(R.id.tvCenterTwoStyleThree, "永久开放")
                    } else {
                        holder.setText(
                            R.id.tvCenterTwoStyleThree,
                            "报名时间：" + item.start_time + "-" + item.end_time
                        )
                    }
                    holder.setText(
                        R.id.tvBottomStyleThree,
                        "任务时间：" + item.task_start_date + "-" + item.task_end_date
                    )
                }
            }

            else -> {
                holder.setGone(R.id.tv_type_three, true)
                holder.setGone(R.id.ivPlay, true)
                holder.setGone(R.id.tvCenterOneStyleThree, true)
                holder.setGone(R.id.tvCenterTwoStyleThree, true)
                holder.setGone(R.id.tvBottomStyleThree, true)
//                holder.setGone(R.id.tv_right_style_three, true)
                holder.setGone(R.id.tv_reasons_three, true)
            }
        }
    }

    private fun setFourData(holder: BaseViewHolder, dataBean: KnowledgeResource) {
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


        val value = typeStringMap[dataBean.resource_type]
        holder.setText(R.id.tv_type_four, value)
        holder.setGone(R.id.tv_type_four, TextUtils.isEmpty(value))

        when (dataBean.resource_type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setGone(R.id.tv_type_four, false)
                holder.setText(R.id.tv_type_four, R.string.course)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.org)
                holder.setText(R.id.tvCenterOneRight, dataBean.course_start_time)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setGone(R.id.tv_type_four, false)
                holder.setText(R.id.tv_type_four, R.string.periodical)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.staff)
                holder.setText(R.id.tvCenterOneRight, dataBean.source_zh)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.tv_type_four, false)
                holder.setText(R.id.tv_type_four, R.string.album)
                holder.setGone(R.id.ivPlay, false)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                val num: String = String.format(
                    context.resources
                        .getString(R.string.text_str_album_count),
                    java.lang.String.valueOf(dataBean.include_track_count)
                )
                holder.setText(R.id.tvCenterOneLeft, num)

            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.tv_type_four, false)
                holder.setText(R.id.tv_type_four, R.string.track)
                holder.setGone(R.id.ivPlay, false)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.album_title)
                holder.setText(R.id.tvCenterOneRight, timeParse(dataBean.duration))
            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setGone(R.id.tv_type_four, false)
                holder.setText(R.id.tv_type_four, R.string.article)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.staff)
                holder.setText(R.id.tvCenterOneRight, dataBean.source_zh)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setGone(R.id.tv_type_four, false)
                holder.setText(R.id.tv_type_four, R.string.ebook)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.writer)
                holder.setText(R.id.tvCenterOneRight, dataBean.source_zh)

            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                holder.setGone(R.id.tv_type_four, false)
                holder.setGone(R.id.llCenterOne, true)
                holder.setGone(R.id.llCenterTwo, true)

            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                holder.setGone(R.id.tv_type_four, false)
//                if (!TextUtils.isEmpty(dataBean.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
//                    holder.setText(R.id.tv_type_four, dataBean.identity_name)
//                } else {
//                    holder.setText(R.id.tv_type_four, R.string.special)
//                }
                holder.setGone(R.id.llCenterOne, true)
                holder.setGone(R.id.llCenterTwo, true)

            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
                holder.setGone(R.id.tv_type_four, false)
                holder.setText(R.id.tv_type_four, R.string.oneself_track)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)

                holder.setText(
                    R.id.tvCenterOneLeft,
                    "播放 ${formatPlayCount(dataBean.audio_play_num)}次"
                )
                holder.setText(
                    R.id.tvCenterOneRight,
                    timeParse(dataBean.audio_time)
                )
            }
            ResourceTypeConstans.TYPE_TASK -> {
                holder.setGone(R.id.ivPlay, true)
                holder.setGone(R.id.tv_type_four, false)
                holder.setText(R.id.tv_type_four, R.string.str_task)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, false)
                if (!TextUtils.isEmpty(dataBean.success_score)) {
                    val successScoreStr = "奖励积分 " + dataBean.success_score
                    val spannableString = SpannableString(successScoreStr)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_6B)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_5D5D5D)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(13f)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(15f)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.setText(R.id.tvCenterOneLeft, spannableString)

                } else {
                    holder.setText(R.id.tvCenterOneLeft, "")
                }

                holder.setText(R.id.tvCenterTwoLeft, dataBean.join_num + "人参与")

            }
            else -> {
                holder.setGone(R.id.tv_type_four, true)
                holder.setGone(R.id.llCenterOne, false)
                holder.setGone(R.id.llCenterTwo, true)
                holder.setText(R.id.tvCenterOneLeft, dataBean.source_zh)

            }
        }


    }

    private fun setFiveData(holder: BaseViewHolder, dataBean: KnowledgeResource) {
        val image: String =
            if (TextUtils.isEmpty(dataBean.big_image)) dataBean.small_image else dataBean.big_image
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_square_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_square_default)
            .into(holder.getView(R.id.iv_recommend_style_five))


        if (dataBean.resource_type == ResourceTypeConstans.TYPE_ALBUM || dataBean.resource_type == ResourceTypeConstans.TYPE_TRACK) {
            holder.setGone(R.id.ivPlay, false)
        } else {
            holder.setGone(R.id.ivPlay, true)

        }
        holder.setText(R.id.tv_title_style_five, dataBean.title)

        val value = typeStringMap[dataBean.resource_type]
        holder.setText(R.id.tvTypeFive, value)
        holder.setGone(R.id.tvTypeFive, TextUtils.isEmpty(value))

        when (dataBean.resource_type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setGone(R.id.tvTypeFive, false)
                holder.setText(R.id.tvTypeFive, R.string.course)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setGone(R.id.tvTypeFive, false)
                holder.setText(R.id.tvTypeFive, R.string.periodical)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.tvTypeFive, false)
                holder.setGone(R.id.ivPlay, false)
                holder.setText(R.id.tvTypeFive, R.string.album)
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.tvTypeFive, false)
                holder.setGone(R.id.ivPlay, false)
                holder.setText(R.id.tvTypeFive, R.string.track)
            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setGone(R.id.tvTypeFive, false)
                holder.setText(R.id.tvTypeFive, R.string.article)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setGone(R.id.tvTypeFive, false)
                holder.setText(R.id.tvTypeFive, R.string.ebook)
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                holder.setGone(R.id.tvTypeFive, false)
            }

            ResourceTypeConstans.TYPE_SPECIAL -> {
                holder.setGone(R.id.tvTypeFive, false)
//                if (!TextUtils.isEmpty(dataBean.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
//                    holder.setText(R.id.tvTypeFive, dataBean.identity_name)
//                } else {
//                    holder.setText(R.id.tvTypeFive, R.string.special)
//                }
            }

            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
                holder.setGone(R.id.tvTypeFive, false)
                holder.setText(R.id.tvTypeFive, R.string.oneself_track)
            }
            ResourceTypeConstans.TYPE_TASK -> {
                holder.setGone(R.id.tvTypeFive, false)
                holder.setText(R.id.tvTypeFive, R.string.str_task)
            }
            else -> {
                holder.setGone(R.id.tvTypeFive, true)
                holder.setGone(R.id.ivPlay, true)
            }
        }


    }

    private fun setSixData(holder: BaseViewHolder, item: KnowledgeResource) {
        //设置type类型
        holder.setText(R.id.tvSexTitle, item.title)
        holder.setText(R.id.tvSexSub, "")
        holder.setText(R.id.tvSexTime, "")
        holder.setText(R.id.tv_middle, "")
        holder.setGone(R.id.tv_middle, true)


        val value = typeStringMap[item.resource_type]
        holder.setText(R.id.tvSexType, value)
        holder.setGone(R.id.tvSexType, TextUtils.isEmpty(value))

        when (item.resource_type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setText(R.id.tvSexSub, item.org)
                holder.setText(R.id.tvSexTime, item.course_start_time)
            }
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.tvSexSub, item.staff)
                holder.setText(R.id.tvSexTime, item.source_zh)
            }
            //合集
            ResourceTypeConstans.TYPE_SPECIAL -> {
//                if (!TextUtils.isEmpty(item.identity_name)) {
//                    holder.setText(R.id.tvSexType, item.identity_name)
//                }
            }
            //跟读
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {

            }
            //电子书
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.tvSexSub, item.writer)
                holder.setText(R.id.tvSexTime, item.source_zh)
            }

            ResourceTypeConstans.TYPE_ALBUM -> {
                val num: String = item.include_track_count.toString() + "集"
                holder.setText(R.id.tvSexSub, num)
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setText(R.id.tvSexSub, item.album_title)
                holder.setText(R.id.tvSexTime, timeParse(item.duration))
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setText(R.id.tvSexTitle, item.title)
                holder.setText(R.id.tvSexTime, timeParse(item.audio_time))
                holder.setText(R.id.tvSexSub, "")
            }

            ResourceTypeConstans.TYPE_TASK -> {
                if (!TextUtils.isEmpty(item.success_score)) {
                    val successScoreStr = "奖励积分 " + item.success_score
                    val spannableString = SpannableString(successScoreStr)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_5D5D5D)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_5D5D5D)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(12f)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(15f)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holder.setText(R.id.tvSexSub, spannableString)

                } else {
                    holder.setText(R.id.tvSexSub, "")
                }

                holder.setText(R.id.tvSexTime, item.join_num + "人参与")
            }

            else -> {
                holder.setGone(R.id.tv_middle, true)
            }

        }

    }

    private fun setSevenData(helper: BaseViewHolder, item: KnowledgeResource) {
        val itemViewType: Int = helper.itemViewType
        if (itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) return

        helper.setGone(R.id.icon_play, true)
        val type = typeStringMap[item.resource_type]
        helper.setText(R.id.tv_identify, type)
        helper.setText(R.id.tvFrom, "")
        helper.setText(R.id.org, "")
        helper.setText(R.id.time, "")
        helper.setText(R.id.title, "")
        helper.setGone(R.id.tv_identify, TextUtils.isEmpty(type))
        helper.setGone(R.id.tvFrom, false)
        helper.setGone(R.id.time, true)

        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        //默认一个图片一个标题
        helper.setText(R.id.title, item.title)
        Glide.with(context)
            .load(image)
            .placeholder(R.mipmap.common_bg_cover_big_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into((helper.getView<View>(R.id.cover) as ImageView))


        when (item.resource_type) {
            ResourceTypeConstans.TYPE_SPECIAL -> {
//                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
//                    helper.setText(R.id.tv_identify, item.identity_name)
//                }
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                helper.setText(R.id.org, item.org)
                helper.setText(R.id.tvFrom, item.course_start_time)
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                helper.setGone(R.id.icon_play, false)
                helper.setText(R.id.org, item.album_title)
                helper.setText(R.id.tvFrom, timeParse(item.duration))
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                helper.setGone(R.id.icon_play, false)
                val num = String.format(
                    context.resources.getString(R.string.text_str_album_count),
                    item.include_track_count.toString()
                )
                helper.setText(R.id.org, num)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                helper.setText(R.id.org, item.staff)
                helper.setText(R.id.tvFrom, item.source_zh)
            }
            ResourceTypeConstans.TYPE_ARTICLE, ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {
                helper.setText(R.id.org, item.staff)
                helper.setText(R.id.tvFrom, item.source_zh)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                helper.setText(R.id.org, item.writer)
                helper.setText(R.id.tvFrom, item.source_zh)
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                helper.setGone(R.id.icon_play, false)
                try {
                    helper.setText(
                        R.id.org,
                        String.format("播放 %s次", formatPlayCount(item.audio_play_num))
                    )
                } catch (e: Exception) {
                    helper.setText(R.id.org, String.format("播放 %s次", 0))
                }
                try {
                    helper.setText(R.id.tvFrom, timeParse(item.audio_time))
                } catch (e: Exception) {
                    helper.setText(R.id.tvFrom, "")
                }
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读
//                helper.setText(R.id.org, item.staff)

            }
            ResourceTypeConstans.TYPE_TASK -> {
                if (!TextUtils.isEmpty(item.success_score)) {
                    val successScoreStr = "奖励积分 " + item.success_score
                    val spannableString = SpannableString(successScoreStr)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_5D5D5D)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_5D5D5D)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(11f)),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        AbsoluteSizeSpan(sp2px(15f)),
                        4,
                        successScoreStr.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    helper.setText(R.id.org, spannableString)

                } else {
                    helper.setText(R.id.org, "")
                }

                if (!TextUtils.isEmpty(item.join_num)) {
                    helper.setGone(R.id.time, false)
                    helper.setText(R.id.time, item.join_num + "人参与")
                } else {
                    helper.setGone(R.id.time, true)
                }
                helper.setText(R.id.tvFrom, "")

            }
            else -> {
                helper.setText(R.id.org, "")
                helper.setText(R.id.time, "")
                helper.setText(R.id.tvFrom, "")
            }
        }

        //判断中间行是否有文字,如果都没有则gone
        val org = helper.getViewOrNull<TextView>(R.id.org)
        val tvFrom = helper.getViewOrNull<TextView>(R.id.tvFrom)

        val layoutParams = org?.layoutParams as ConstraintLayout.LayoutParams
        if (item.resource_type == ResourceTypeConstans.TYPE_ARTICLE || item.resource_type == ResourceTypeConstans.TYPE_COLUMN_ARTICLE) {
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

    private fun setEightData(holder: BaseViewHolder, dataBean: KnowledgeResource) {
        val image: String =
            if (TextUtils.isEmpty(dataBean.big_image)) dataBean.small_image else dataBean.big_image
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .into(holder.getView(R.id.ivStyleEightKnow))

        setViewVisibility(holder.getView(R.id.tvReasonsEightKnow), dataBean.recommend_reason)
        holder.setGone(R.id.tvReasonsEightKnow, true)
        holder.setText(R.id.tvReasonsEightKnow, dataBean.recommend_reason)
        holder.setGone(R.id.ivPlay, true)
        holder.setText(R.id.tvTitleStyleEight, dataBean.title)

        val value = typeStringMap[dataBean.resource_type]
        holder.setText(R.id.tvTypeEight, value)
        holder.setGone(R.id.tvTypeEight, TextUtils.isEmpty(value))

        when (dataBean.resource_type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                holder.setGone(R.id.tvTypeEight, false)
                holder.setText(R.id.tvTypeEight, R.string.course)
                holder.setText(R.id.tvBottomStyleEight, dataBean.org)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setGone(R.id.tvTypeEight, false)
                holder.setText(R.id.tvTypeEight, R.string.periodical)
                holder.setText(R.id.tvBottomStyleEight, dataBean.staff)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.tvTypeEight, false)
                holder.setText(R.id.tvTypeEight, R.string.album)
                holder.setGone(R.id.ivPlay, false)
                val num: String = String.format(
                    context.resources
                        .getString(R.string.text_str_album_count),
                    java.lang.String.valueOf(dataBean.include_track_count)
                )
                holder.setText(R.id.tvBottomStyleEight, num)

            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.tvTypeEight, false)
                holder.setText(R.id.tvTypeEight, R.string.track)
                holder.setGone(R.id.ivPlay, false)
                holder.setText(R.id.tvBottomStyleEight, dataBean.album_title)
            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setGone(R.id.tvTypeEight, false)
                holder.setText(R.id.tvTypeEight, R.string.article)
                holder.setText(R.id.tvBottomStyleEight, dataBean.staff)
            }

            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setGone(R.id.tvTypeEight, false)
                holder.setText(R.id.tvTypeEight, R.string.ebook)
                holder.setText(R.id.tvBottomStyleEight, dataBean.writer)
            }

            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                holder.setGone(R.id.tvTypeEight, false)
            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                holder.setGone(R.id.tvTypeEight, false)
//                if (!TextUtils.isEmpty(dataBean.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
//                    holder.setText(R.id.tvTypeEight, dataBean.identity_name)
//                } else {
//                    holder.setText(R.id.tvTypeEight, R.string.special)
//                }

            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.tvTypeEight, false)
                holder.setText(R.id.tvTypeEight, R.string.oneself_track)
                holder.setGone(R.id.ivPlay, false)
                holder.setText(
                    R.id.tvBottomStyleEight,
                    timeParse(dataBean.audio_time)
                )
            }

            ResourceTypeConstans.TYPE_TASK -> {
                holder.setGone(R.id.tvTypeEight, false)
                holder.setText(R.id.tvTypeEight, R.string.str_task)
                holder.setText(R.id.tvBottomStyleEight, dataBean.join_num + "人参与")
            }

            else -> {
                holder.setGone(R.id.tvTypeEight, true)
                holder.setText(R.id.tvBottomStyleEight, dataBean.source_zh)
            }
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

}