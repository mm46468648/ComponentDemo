package com.mooc.discover.adapter.style

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.discover.R
import com.mooc.discover.model.RecommendColumn
import com.mooc.resource.widget.tagtext.TagTextView

class HomeTagEightAdapter(data: ArrayList<RecommendColumn>?) :
    BaseDelegateMultiAdapter<RecommendColumn, BaseViewHolder>(data) {

    //展示资源数组（仅限数组中的资源可以展示,其余不显示）
    var typeArray = arrayOf(
        ResourceTypeConstans.TYPE_COURSE,//课程
        ResourceTypeConstans.TYPE_E_BOOK,//电子书
        ResourceTypeConstans.TYPE_ALBUM,//音频课
        ResourceTypeConstans.TYPE_ONESELF_TRACK,//自建音频
        ResourceTypeConstans.TYPE_TRACK,//音频
        ResourceTypeConstans.TYPE_COLUMN_ARTICLE,//专栏文章
        ResourceTypeConstans.TYPE_ARTICLE,//文章
        ResourceTypeConstans.TYPE_PERIODICAL,//期刊
        ResourceTypeConstans.TYPE_SPECIAL,//合集
        ResourceTypeConstans.TYPE_BATTLE,//对战
        ResourceTypeConstans.TYPE_MICRO_LESSON,//微课
        ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL,//微专业
        ResourceTypeConstans.TYPE_TASK,//任务
        ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE,//跟读
        ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE,//微知识
        ResourceTypeConstans.TYPE_ACTIVITY_TASK,//活动任务
        ResourceTypeConstans.TYPE_ACTIVITY,//活动
        ResourceTypeConstans.TYPE_TEST_VOLUME,//测试卷
        ResourceTypeConstans.TYPE_QUESTIONNAIRE,//问卷
        ResourceTypeConstans.TYPE_STUDY_PLAN,//学习项目
        ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK,//友情链接
        ResourceTypeConstans.TYPE_COLUMN,//专栏
        ResourceTypeConstans.TYPE_PUBLICATION,//刊物
    )

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<RecommendColumn>() {
            override fun getItemType(
                data: List<RecommendColumn>,
                position: Int
            ): Int {
                val dataBean = data[position]
                return if (dataBean.type in typeArray) dataBean.type else ResourceTypeConstans.TYPE_DEFAULT
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
            ?.addItemType(
                ResourceTypeConstans.TYPE_COURSE,
                R.layout.item_recommend_style_nine_course
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_E_BOOK,
                R.layout.item_recommend_style_nine_e_book
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_ALBUM,
                R.layout.item_recommend_style_nine_audio_album
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_ONESELF_TRACK,
                R.layout.item_recommend_style_nine_ownbuild_audio
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_TRACK,
                R.layout.item_recommend_style_nine_audio
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_COLUMN_ARTICLE,
                R.layout.item_recommend_style_nine_article
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_ARTICLE,
                R.layout.item_recommend_style_nine_article
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_PERIODICAL,
                R.layout.item_recommend_style_nine_periodical
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_SPECIAL,
                R.layout.item_recommend_style_nine_special
            )
            ?.addItemType(ResourceTypeConstans.TYPE_TASK, R.layout.item_recommend_style_nine_task)
            ?.addItemType(
                ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE,
                R.layout.item_recommend_style_nine_followup
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_MICRO_LESSON,
                R.layout.item_recommend_style_nine_micro_lesson
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE,
                R.layout.item_recommend_style_nine_micro_knowledge
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL,
                R.layout.item_recommend_style_nine_micro_professional
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_BATTLE,
                R.layout.item_recommend_style_nine_battle
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_TEST_VOLUME,
                R.layout.item_recommend_style_nine_test_volume
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_QUESTIONNAIRE,
                R.layout.item_recommend_style_nine_questionnaire
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_ACTIVITY,
                R.layout.item_recommend_style_nine_activity_task
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_ACTIVITY_TASK,
                R.layout.item_recommend_style_nine_activity_task
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_STUDY_PLAN,
                R.layout.item_recommend_style_nine_study_plan
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK,
                R.layout.item_recommend_style_nine_out_link
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_COLUMN,
                R.layout.item_recommend_style_nine_column
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_PUBLICATION,
                R.layout.item_recommend_style_nine_publication
            )
            ?.addItemType(
                ResourceTypeConstans.TYPE_DEFAULT,
                R.layout.item_recommend_style_nine_default
            )
    }

    override fun convert(holder: BaseViewHolder, item: RecommendColumn) {
        val viewBottomLine = holder.getViewOrNull<View>(R.id.viewBottomLine)
        if (viewBottomLine != null) {  //最后一个line隐藏
            if (holder.layoutPosition == data.size - 1) {
                viewBottomLine.visibility = View.INVISIBLE
            } else {
                viewBottomLine.visibility = View.VISIBLE
            }
        }
        //设置资源内容
        when (holder.itemViewType) {
            ResourceTypeConstans.TYPE_COURSE -> {
                setCourseData(holder, item)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                setEBookData(holder, item)
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                setAlbumData(holder, item)
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                setOwnTrackData(holder, item)
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                setTrackData(holder, item)
            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE,
            ResourceTypeConstans.TYPE_ARTICLE -> {
                setArticleData(holder, item)
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                setPeriodicalData(holder, item)
            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                setSpecial(holder, item)
            }
            ResourceTypeConstans.TYPE_BATTLE -> {
                setBattle(holder, item)
            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {
                setMicroProfessional(holder, item)
            }
            ResourceTypeConstans.TYPE_TASK -> {
                setTaskData(holder, item)
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                setFollowUpData(holder, item)
            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {
                setMicroKnowLedge(holder, item)
            }
            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                setMicroLesson(holder, item)
            }
            ResourceTypeConstans.TYPE_TEST_VOLUME -> {
                setTestVolume(holder, item)
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
                setQuestionnaire(holder, item)
            }
            ResourceTypeConstans.TYPE_ACTIVITY, ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
                setActivityTask(holder, item)
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                setStudyPlan(holder, item)
            }
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {
                setOutLink(holder, item)
            }
            ResourceTypeConstans.TYPE_COLUMN -> {
                setColumn(holder, item)
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                setPublication(holder, item)
            }
            ResourceTypeConstans.TYPE_DEFAULT -> {
                setDefault(holder, item)
            }
        }
    }


    private fun setPublication(holder: BaseViewHolder, item: RecommendColumn) {
        val value = ResourceTypeConstans.typeStringMap[item.type]
        holder.setText(R.id.tvTypePublication, value)
        holder.setText(R.id.tvTitlePublication, item.title)
        if (TextUtils.isEmpty(item.periodicals_data.year)) {
            item.periodicals_data.year = ""
        }
        if (TextUtils.isEmpty(item.periodicals_data.term)) {
            item.periodicals_data.term = ""
        }
        holder.setText(
            R.id.tvTimePublication,
            "更新至${item.periodicals_data.year}年第${item.periodicals_data.term}期"
        )
        holder.setText(R.id.tvSourcePublication, item.periodicals_data.unit)
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgPublication)
        imageView?.let {
            Glide.with(context)
                .load(image)
                .placeholder(R.mipmap.common_bg_cover_vertical_default)
                .error(R.mipmap.common_bg_cover_vertical_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }
    }

    private fun setColumn(holder: BaseViewHolder, item: RecommendColumn) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleColumn)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        val value = ResourceTypeConstans.typeStringMap[item.type]
        tagView.setTagStart(arrayListOf(value), item.title)
        if (TextUtils.isEmpty(item.desc)
            && TextUtils.isEmpty(item.update_time)
        ) {
            holder.setGone(R.id.llBottomColumn, true)
        } else {
            holder.setGone(R.id.llBottomColumn, true)
        }
        when {
            !TextUtils.isEmpty(item.desc) -> {
                holder.setGone(R.id.tvLastTitleColumn, false)
                holder.setText(R.id.tvLastTitleColumn, item.desc)
            }
            else -> {
                holder.setGone(R.id.tvLastTitleColumn, true)
            }
        }
        if (!TextUtils.isEmpty(item.update_time)) {
            holder.setGone(R.id.tvTimeColumn, false)
            holder.setText(R.id.tvTimeColumn, item.update_time)
        } else {
            holder.setGone(R.id.tvTimeColumn, true)
        }
    }

    private fun setOutLink(holder: BaseViewHolder, item: RecommendColumn) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleOutLink)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        val value = ResourceTypeConstans.typeStringMap[item.type]
        tagView.setTagStart(arrayListOf(value), item.title)
    }

    private fun setStudyPlan(holder: BaseViewHolder, item: RecommendColumn) {
        val value = ResourceTypeConstans.typeStringMap[item.type]
        holder.setText(R.id.tvTypeStudy, value)
        holder.setText(R.id.tvTitleStudy, item.title)
        holder.setText(R.id.tvPromoterStudy, item.staff)
//        if (item.time_mode == "1") {//时间永久
//            holder.setText(
//                R.id.tvTimeStudy,
//                context.resources.getString(R.string.study_time_permanent_opening)
//            )
//        } else {
//            holder.setText(R.id.tvTimeStudy, item.start_time)
//        }
        holder.setText(R.id.tvEnrollment, "${item.student_num}人")
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgStudy)
        imageView?.let {
            Glide.with(context)
                .load(image)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }
    }

    private fun setActivityTask(holder: BaseViewHolder, item: RecommendColumn) {
        val value = ResourceTypeConstans.typeStringMap[item.type]
        holder.setText(R.id.tvTypeActivityTask, value)
        holder.setText(R.id.tvTitleActivityTask, item.title)
//        if (!TextUtils.isEmpty(item.about)) {
//            holder.setGone(R.id.tvSubActivityTask, false)
//            holder.setText(R.id.tvSubActivityTask, item.about)
//        } else {
//            holder.setGone(R.id.tvSubActivityTask, true)
//        }
//        if (!TextUtils.isEmpty(item.update_time)) {
//            holder.setGone(R.id.tvTimeActivityTask, false)
//            holder.setText(R.id.tvTimeActivityTask, item.update_time)
//        } else {
//            holder.setGone(R.id.tvTimeActivityTask, true)
//        }

        holder.setGone(R.id.tvSubActivityTask, true)
        holder.setGone(R.id.tvTimeActivityTask, true)
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgActivityTask)
        imageView?.let {
            Glide.with(context)
                .load(image)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }
    }

    private fun setMicroKnowLedge(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.tvTitle, item.title)
        holder.setText(R.id.tvStudyCount, item.click_num + "人学习")
        holder.setText(R.id.tvCompleteCount, item.exam_pass_num + "人通过微测试")
        holder.setText(R.id.tvPriseCount, item.like_num + "人点赞")
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgMicroKnow)
        imageView?.let {
            Glide.with(context)
                .load(image)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }
    }

    private fun setMicroLesson(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.tvTitleMicroLesson, item.title)
        if (!TextUtils.isEmpty(item.video_duration)) {
            val videoDuration: Long = try {
                item.video_duration.toLong()
            } catch (e: NumberFormatException) {
                0
            }
            holder.setText(
                R.id.tvDurationMicroLesson,
                TimeFormatUtil.formatAudioPlayTime(videoDuration * 1000)
            )

        }
        holder.setText(R.id.tvSourceMicroLesson, item.source)
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgMicroLesson)
        imageView?.let {
            Glide.with(context)
                .load(image)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }
    }

    private fun setSpecial(holder: BaseViewHolder, item: RecommendColumn) {
        val tagView = holder.getView<TagTextView>(R.id.tvSpecialTitle)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        val value = ResourceTypeConstans.typeStringMap[item.type]
        tagView.setTagStart(arrayListOf(value), item.title)
        if (TextUtils.isEmpty(item.resource_info?.title)
            && TextUtils.isEmpty(item.resource_info?.updated_time + "更新")) {
            holder.setGone(R.id.llBottomSpecial, true)
        } else {
            holder.setGone(R.id.llBottomSpecial, true)
        }
        when {
            !TextUtils.isEmpty(item.resource_info?.title) -> {
                holder.setGone(R.id.tvLastTitle, false)
                holder.setText(R.id.tvLastTitle, item.resource_info?.title)
            }
            else -> {
                holder.setGone(R.id.tvLastTitle, true)
            }
        }
        if (!TextUtils.isEmpty(item.resource_info?.updated_time + "更新")) {
            holder.setGone(R.id.tvTime, false)
            holder.setText(R.id.tvTime, item.resource_info?.updated_time + "更新")
        } else {
            holder.setGone(R.id.tvTime, true)
        }

    }

    private fun setTestVolume(holder: BaseViewHolder, item: RecommendColumn) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleTestVolume)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        val value = ResourceTypeConstans.typeStringMap[item.type]
        tagView.setTagStart(arrayListOf(value), item.title)
        holder.setText(R.id.tvSourceTestVolume, item.source)
    }

    private fun setQuestionnaire(holder: BaseViewHolder, item: RecommendColumn) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleQuestionnaire)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        val value = ResourceTypeConstans.typeStringMap[item.type]
        tagView.setTagStart(arrayListOf(value), item.title)
    }

    private fun setDefault(holder: BaseViewHolder, item: RecommendColumn) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleDefault)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        val value = ResourceTypeConstans.typeStringMap[item.type]
        tagView.setTagStart(arrayListOf(value), item.title)
    }

    private fun setBattle(holder: BaseViewHolder, item: RecommendColumn) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleBattle)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        tagView.setTagStart(arrayListOf("对战"), item.title)
    }

    private fun setMicroProfessional(holder: BaseViewHolder, item: RecommendColumn) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleMicroProfessional)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        tagView.setTagStart(arrayListOf("微专业"), item.title)
    }

    private fun setFollowUpData(holder: BaseViewHolder, item: RecommendColumn) {
        val tagView = holder.getView<TagTextView>(R.id.tvTitleFollowUp)
        val tagBg = context.getDrawableRes(R.drawable.common_shape_bg_item_tag)
        tagView.setTagsBgDrawable(tagBg)
        tagView.setTagTextColor(Color.WHITE)
        tagView.setTagTextSize(9)
        tagView.setTagStart(arrayListOf("跟读"), item.title)
    }

    private fun setAlbumData(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.tvTitleAlbum, item.title)
        holder.setText(
            R.id.tvPlayNumAlbum,
            StringFormatUtil.formatPlayCount(item.album_data.play_count)
        )
        holder.setText(R.id.tvCollectionAlbum, "${item.album_data.track_count}集")
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

    private fun setTrackData(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.tvTitleAudio, item.track_data.track_title)
        holder.setText(R.id.tvSourceAudio, item.track_data.album?.albumTitle)
        holder.setText(R.id.tvPlayNumAudio, item.track_data.play_count.toString())
        holder.setText(R.id.tvDurationAudio, TimeUtils.timeParse(item.track_data.duration))
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

    private fun setOwnTrackData(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.tvTitleSelfBuildAudio, item.title)
        holder.setText(
            R.id.tvPlayNumSelfBuildAudio,
            StringFormatUtil.formatPlayCount(item.audio_data.audio_play_num)
        )
        holder.setText(
            R.id.tvDurationSelfBuildAudio,
            TimeUtils.timeParse(item.audio_data.audio_time)
        )
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

    private fun setArticleData(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.tvTitleArticle, item.title)

        var sourceStr = ""
        if (!TextUtils.isEmpty(item.platform_zh)) {
            sourceStr = item.platform_zh
            if (!TextUtils.isEmpty(item.source)) {
                sourceStr += " | ${item.source}"
            }
        } else {
            if (!TextUtils.isEmpty(item.source)) {
                sourceStr += item.source
            }
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

    private fun setPeriodicalData(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.tvTitlePeriodical, item.title)
        if (!TextUtils.isEmpty(item.staff)) {
            holder.setGone(R.id.tvStaffPeriodical, false)
            holder.setText(R.id.tvStaffPeriodical, item.staff)
        } else {
            holder.setGone(R.id.tvStaffPeriodical, true)

        }
        if (!TextUtils.isEmpty(item.basic_date_time)) {
            holder.setGone(R.id.tvTimePeriodical, false)
            holder.setText(R.id.tvTimePeriodical, item.basic_date_time + "年出版")
        } else {
            holder.setGone(R.id.tvTimePeriodical, true)
        }
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

    private fun setTaskData(holder: BaseViewHolder, item: RecommendColumn) {
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

    private fun setEBookData(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.tvTitleEbook, item.title)
        holder.setText(R.id.tvAuthorEbook, item.writer)
        var source = item.source
        if (!TextUtils.isEmpty(item.press)) {
            source = source + " | " + item.press
        }
        holder.setText(R.id.tvSourceEbook, source)
        holder.setText(
            R.id.tvWordNumEbook,
            StringFormatUtil.formatPlayCount(item.word_count) + "字"
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

    private fun setCourseData(holder: BaseViewHolder, item: RecommendColumn) {
        holder.setText(R.id.tvTitleCourse, item.title)
        holder.setText(R.id.tvSourceCourse, item.source)
        holder.setText(R.id.tvAuthorCourse, item.org)
        holder.setText(R.id.tvOpeningCourse, item.start_time)
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
    private fun setCourseTypeInfo(textView: TextView, bean: RecommendColumn) {
//        val isHaveExam: Int = bean.is_have_exam
//        val isVerified: Boolean = bean.verified_active
//        val isFree: Boolean = bean.is_free

        val isHaveExam: String = bean.is_have_exam
        val isVerified: String = bean.verified_active
        val isFree: String = bean.is_free

        var detail = ""
        val dot = " · "
        //是否有考试
        if (isHaveExam == "1") {
            detail += context.resources.getString(R.string.course_str_have_exam)
        } else {
            detail += context.resources.getString(R.string.course_str_no_exam)
        }
        detail += dot
        //是否有证书
        if (isVerified == "1") {
            detail += context.resources.getString(R.string.course_str_have_certificate)
        } else {
            detail += context.resources.getString(R.string.course_str_no_certificate)
        }
        detail += dot
        //是否免费
        if (isFree == "0") {
            detail += context.resources.getString(R.string.course_str_pay)
        } else {
            detail += context.resources.getString(R.string.course_str_free)
        }

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

}