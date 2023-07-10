package com.mooc.search.adapter

import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.utils.DateStyle
import com.mooc.common.utils.DateUtil
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.search.R
import com.mooc.search.model.SearchItem
import org.jetbrains.annotations.NotNull
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SearchResultAdapterNew(list: ArrayList<SearchItem>) :
    BaseDelegateMultiAdapter<SearchItem, BaseViewHolder>(list) {


    //展示资源数组（仅限数组中的资源可以展示，多余要移除）
    var displayTypes = arrayOf(
        ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE,
        ResourceTypeConstans.TYPE_COURSE,
        ResourceTypeConstans.TYPE_TRACK,
        ResourceTypeConstans.TYPE_ALBUM,
        ResourceTypeConstans.TYPE_ARTICLE,
        ResourceTypeConstans.TYPE_PERIODICAL,
        ResourceTypeConstans.TYPE_PUBLICATION,
        ResourceTypeConstans.TYPE_E_BOOK,
        ResourceTypeConstans.TYPE_MICRO_LESSON,
        ResourceTypeConstans.TYPE_BAIKE,
        ResourceTypeConstans.TYPE_STUDY_PLAN,
        ResourceTypeConstans.TYPE_TU_LING,
        ResourceTypeConstans.TYPE_SOURCE_FOLDER,
        ResourceTypeConstans.TYPE_UNDEFINE
    )

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<SearchItem>() {
            override fun getItemType(@NotNull data: List<SearchItem>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                val resultBean = list.get(position)
                val type: Int = resultBean.resource_type ?: ResourceTypeConstans.TYPE_UNDEFINE
                //无图类型
                return if (resultBean.resource_type in displayTypes) {
                    resultBean.resource_type
                } else { //未定义类型
                    ResourceTypeConstans.TYPE_UNDEFINE
                }
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
            ?.addItemType(
                ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE,
                R.layout.search_item_micro_knowledge
            )
            ?.addItemType(ResourceTypeConstans.TYPE_COURSE, R.layout.search_item_course)
            ?.addItemType(ResourceTypeConstans.TYPE_TRACK, R.layout.search_item_audio)
            ?.addItemType(ResourceTypeConstans.TYPE_ALBUM, R.layout.search_item_audio_album)
            ?.addItemType(ResourceTypeConstans.TYPE_ARTICLE, R.layout.search_item_article)
            ?.addItemType(ResourceTypeConstans.TYPE_PERIODICAL, R.layout.search_item_periodical)
            ?.addItemType(ResourceTypeConstans.TYPE_PUBLICATION, R.layout.search_item_publication)
            ?.addItemType(ResourceTypeConstans.TYPE_E_BOOK, R.layout.search_item_e_book)
            ?.addItemType(ResourceTypeConstans.TYPE_MICRO_LESSON, R.layout.search_item_small_course)
            ?.addItemType(ResourceTypeConstans.TYPE_BAIKE, R.layout.search_item_baike)
            ?.addItemType(ResourceTypeConstans.TYPE_STUDY_PLAN, R.layout.search_item_study)
            ?.addItemType(ResourceTypeConstans.TYPE_TU_LING, R.layout.search_item_tuling)
            ?.addItemType(
                ResourceTypeConstans.TYPE_SOURCE_FOLDER,
                R.layout.search_item_study_forder
            )
            ?.addItemType(ResourceTypeConstans.TYPE_UNDEFINE, R.layout.common_item_empty)
    }

    override fun convert(holder: BaseViewHolder, item: SearchItem) {
        if (holder.itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) return


        val postion = holder.layoutPosition
        val topLayout = holder.getView<View>(R.id.llTop)
        val viewSpace = holder.getViewOrNull<View>(R.id.viewSpace)
        //如果当前资源和上一个资源不一样则显示头部
        //分割线第一个不展示,其他随头部展示
        if (postion > 0) {
            if (item.resource_type == data.get(postion - 1).resource_type) {
                topLayout.visibility = View.GONE
                viewSpace?.visibility = View.GONE
            } else {
                topLayout.visibility = View.VISIBLE
                viewSpace?.visibility = View.VISIBLE
            }
        } else {
            //第一个必显示
            topLayout.visibility = View.VISIBLE
            viewSpace?.visibility = View.GONE
        }

        //设置资源类型和数量
        holder.setText(R.id.title, ResourceTypeConstans.typeStringMap[item.resource_type])
        holder.setText(R.id.show_more, "查看全部(${item.ownTotalCount})")

        //设置资源内容
        when (item.resource_type) {
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> setMicroKnowData(holder, item)
            ResourceTypeConstans.TYPE_COURSE -> setCourseData(holder, item)
            ResourceTypeConstans.TYPE_E_BOOK -> setEBookData(holder, item)
            ResourceTypeConstans.TYPE_MICRO_LESSON -> setMiroLessonData(holder, item)
            ResourceTypeConstans.TYPE_ARTICLE -> setArticleData(holder, item)
            ResourceTypeConstans.TYPE_TRACK -> setTrackData(holder, item)
            ResourceTypeConstans.TYPE_ALBUM -> setAlbumData(holder, item)
            ResourceTypeConstans.TYPE_PERIODICAL -> setPeriodicalData(holder, item)
            ResourceTypeConstans.TYPE_PUBLICATION -> setPublicationData(holder, item)
            ResourceTypeConstans.TYPE_BAIKE -> setBaiKeData(holder, item)
            ResourceTypeConstans.TYPE_TU_LING -> setTuLingData(holder, item)
            ResourceTypeConstans.TYPE_STUDY_PLAN -> setStudyProData(holder, item)
            ResourceTypeConstans.TYPE_SOURCE_FOLDER -> setStudyFolder(holder, item);
        }
    }

    private fun setStudyFolder(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tvTitle, item.name)
        holder.setText(R.id.tvCreateName, item.username)
        holder.setText(R.id.tvCreateID, "ID " + item.user_id)
        holder.setText(R.id.tvResourceNum, item.resource_count.toString() + "个资源")
    }

    private fun setStudyProData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tv_title, item.plan_name)
        holder.setText(R.id.tv_people_num, String.format("%s人", item.plan_num))

        if ((item.plan_start_users?.size ?: 0) >= 1) {
            var temp = String.format("发起人:%s等", item.plan_start_users!!.get(0).name)
            if ((item.plan_start_users?.size ?: 0) > 1) {
                temp = temp.removeRange(temp.length - 1, temp.length)
            }
            holder.setText(R.id.tv_source, temp)
        } else {
            holder.setText(R.id.tv_source, "发起人:无")
        }
        if (item.time_mode == 1) {//时间永久
            holder.setText(
                R.id.tv_date,
                context.resources.getString(R.string.study_time_permanent_opening)
            )
        } else {
            holder.setText(
                R.id.tv_date,
                String.format(
                    "%s-%s",
                    DateUtil.StringToString(item.plan_starttime, DateStyle.YYYY_MM_DD_SPOT),
                    DateUtil.StringToString(item.plan_endtime, DateStyle.YYYY_MM_DD_SPOT)
                )
            )
        }


        var start: Date? = null
        var end: Date? = null
        var join_start: Date? = null
        var join_end: Date? = null
        var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            start = simpleDateFormat.parse(item.plan_starttime)
            end = simpleDateFormat.parse(item.plan_endtime)
            join_start = simpleDateFormat.parse(item.join_start_time)
            join_end = simpleDateFormat.parse(item.join_end_time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (start != null && end != null && join_start != null && join_end != null) {
            when (isEnrolment(DateUtil.getCurrentTime(), start.time, end.time)) {
                0 -> {
                    when (isEnrolment(DateUtil.getCurrentTime(), join_start.time, join_end.time)) {
                        0 -> holder.setGone(R.id.state, true)
                        1 -> {
                            holder.setGone(R.id.state, false)
                            holder.setText(R.id.state, "报名中")
                        }
                        2 -> {
                            holder.setGone(R.id.state, false)
                            holder.setText(R.id.state, "进行中")
                        }
                    }
                    holder.setBackgroundResource(
                        R.id.state,
                        R.drawable.shape_radius1_5_colora310955b
                    )
                }
                1 -> {
                    holder.setGone(R.id.state, false)
                    holder.setText(R.id.state, "进行中")
                    holder.setBackgroundResource(
                        R.id.state,
                        R.drawable.shape_radius1_5_colora310955b
                    )
                }
                2 -> {
                    holder.setGone(R.id.state, false)
                    holder.setText(R.id.state, "已结束")
                    holder.setBackgroundResource(
                        R.id.state,
                        R.drawable.shape_radius20_10percent_black
                    )
                }
            }
        }

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.plan_img)
            .placeholder(R.mipmap.common_bg_cover_default)
            .error(R.mipmap.common_bg_cover_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
    }

    private fun setTuLingData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tv_title, item.question)
        holder.setText(R.id.tv_content, Html.fromHtml(item.answer))
    }

    private fun setBaiKeData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_content, item.summary)

    }

    private fun setPublicationData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tvTitle, item.magname)

        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImg)
        Glide.with(context)
            .load(item.coverurl)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
        if (TextUtils.isEmpty(item.unit)) {
            holder.setText(R.id.tvSource, "")
        } else {
            holder.setText(R.id.tvSource, item.unit)

        }
        if (TextUtils.isEmpty(item.year)) {
            item.year = ""
        }
        if (TextUtils.isEmpty(item.term)) {
            item.term = ""
        }
        holder.setText(R.id.tvTime, "更新至${item.year}年第${item.term}期")
    }

    private fun setPeriodicalData(holder: BaseViewHolder, item: SearchItem) {

        holder.setText(R.id.tv_title, item.title)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.basic_cover_url)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
        if (TextUtils.isEmpty(item.basic_creator)) {
            holder.setVisible(R.id.tv_author, false)
        } else {
            holder.setVisible(R.id.tv_author, true)
            holder.setText(R.id.tv_author, item.basic_creator)

        }
        if (TextUtils.isEmpty(item.basic_date_time)) {
            holder.setVisible(R.id.tv_publish_time, false)
        } else {
            holder.setVisible(R.id.tv_publish_time, true)
            holder.setText(R.id.tv_publish_time, item.basic_date_time + "年出版")
        }
    }

    private fun setAlbumData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tv_title, item.album_title)
        holder.setText(
            R.id.tv_play_num,
            StringFormatUtil.formatPlayCount(item.play_count)
        )
        holder.setText(R.id.tv_collection, "${item.include_track_count}集")

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.cover_url_small)
            .error(R.mipmap.common_bg_cover_square_default)
            .placeholder(R.mipmap.common_bg_cover_square_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
    }

    private fun setTrackData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tv_title, item.track_title)
        holder.setText(R.id.tv_source, item.albumTitle)
        holder.setText(R.id.tv_play_num, StringFormatUtil.formatPlayCount(item.play_count.toInt()))
        holder.setText(R.id.tv_duration, TimeUtils.timeParse(item.duration))

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.cover_url_small)
            .error(R.mipmap.common_bg_cover_square_default)
            .placeholder(R.mipmap.common_bg_cover_square_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
    }

    private fun setArticleData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tv_title, item.title)

        var sourceStr = item.platform_zh
        if (!TextUtils.isEmpty(item.source)) {
            sourceStr += " | ${item.source}"
        }
        holder.setText(R.id.tv_source, sourceStr)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.picture)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
    }

    private fun setMiroLessonData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_source, item.platform_zh)
        holder.setText(
            R.id.tv_duration,
            TimeFormatUtil.formatAudioPlayTime(item.video_duration * 1000)
        )
        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.picture)
            .error(R.mipmap.common_bg_cover_default)
            .placeholder(R.mipmap.common_bg_cover_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
    }

    private fun setEBookData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_author, item.writer)
        holder.setText(R.id.tv_source, item.platform_zh + " | " + item.press)
        holder.setText(
            R.id.tv_word_num,
            StringFormatUtil.formatPlayCount(item.word_count) + "字"
        )

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        imageView?.let {
            Glide.with(context)
                .load(item.picture)
                .error(R.mipmap.common_bg_cover_vertical_default)
                .placeholder(R.mipmap.common_bg_cover_vertical_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }
    }

    private fun setMicroKnowData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tvTitleMicroKnow, item.title)//标题
        holder.setText(R.id.tvNumLearnMicroKnow, item.click_num + "人学习")//学习人数
        holder.setText(R.id.tvNumPassMicroKnow, item.exam_pass_num + "人通过微测试")//通过测试人数
        holder.setText(R.id.tvNumLikeMicroKnow, item.like_num + "人点赞")//点赞人数

        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgMicroKnow)
        imageView?.let {
            Glide.with(context)
                .load(item.pic)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }

    }

    private fun setCourseData(holder: BaseViewHolder, item: SearchItem) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_source, item.platform_zh)
        holder.setText(R.id.tv_author, item.org)
        holder.setText(R.id.tv_opening, item.course_start_time)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        imageView?.let {
            Glide.with(context)
                .load(item.picture)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }

        //免费付费等信息
        setCourseTypeInfo(holder.getView(R.id.tvCourseTypeInfo), item)
    }


    /**
     * 设置课程类型信息
     * (考试,证书,付费)
     * 中间点的规则,如果前面点亮,点也点亮
     */
    private fun setCourseTypeInfo(@NonNull textView: TextView, bean: SearchItem) {
        val isHaveExam: Int = bean.is_have_exam
        val isVerified: Int = bean.verified_active
        val isFree: String = bean.is_free
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
        if (isVerified == 1) {
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

        val detailColor = if (SkinManager.getInstance().needChangeSkin()) {
            SkinManager.getInstance().resourceManager.getColor("colorPrimary")
        } else {
            context.getColorRes(R.color.colorPrimary)
        }

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
        textView.setText(spannableString)


    }


    /**
     * 报名的类型
     */
    fun isEnrolment(time: Long, startTime: Long, stopTime: Long): Int {
        return if (time < startTime) { //尚未到报名期
            0
        } else if (time in startTime..stopTime) { //报名期
            1
        } else { //报名期结束
            2
        }
    }

}