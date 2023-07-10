package com.mooc.discover.adapter

import android.text.Html
import android.text.TextUtils
import android.widget.TextView
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.discover.R
import com.mooc.discover.model.RecommendColumn
import com.mooc.discover.model.ResultBean
import com.mooc.discover.view.HomeDiscoverColumnView
import org.jetbrains.annotations.NotNull
import java.lang.String

class RecommendGuessAdapter(var list: ArrayList<ResultBean>?) :
    BaseDelegateMultiAdapter<ResultBean, BaseViewHolder>(list), LoadMoreModule {


    //展示资源数组（仅限数组中的资源可以展示，多余要移除）
    var displayTypes = arrayOf(
        ResourceTypeConstans.TYPE_ARTICLE,
        ResourceTypeConstans.TYPE_NO_IMAGE,
        ResourceTypeConstans.TYPE_COURSE,
        ResourceTypeConstans.TYPE_MICRO_LESSON,
        ResourceTypeConstans.TYPE_KNOWLEDGE,
        ResourceTypeConstans.TYPE_ALBUM,
        ResourceTypeConstans.TYPE_TRACK,
        ResourceTypeConstans.TYPE_BAIKE,
        ResourceTypeConstans.TYPE_PERIODICAL,
        ResourceTypeConstans.TYPE_E_BOOK,
        ResourceTypeConstans.TYPE_RECOMMEND_USER,
        ResourceTypeConstans.TYPE_SPECIAL,
        ResourceTypeConstans.TYPE_SOURCE_FOLDER,
        ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE
    )

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<ResultBean>() {
            override fun getItemType(@NotNull data: List<ResultBean>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                val resultBean = list?.get(position)
                val type: Int = resultBean?.resource_type ?: ResourceTypeConstans.TYPE_UNDEFINE
                //无图类型
                return if ((type == ResourceTypeConstans.TYPE_ARTICLE || type == ResourceTypeConstans.TYPE_COLUMN_ARTICLE)
                    && TextUtils.isEmpty(resultBean?.picture)
                ) {
                    ResourceTypeConstans.TYPE_NO_IMAGE
                } else if (resultBean?.resource_type in displayTypes) {
                    resultBean?.resource_type ?: ResourceTypeConstans.TYPE_UNDEFINE
                } else { //未定义类型
                    ResourceTypeConstans.TYPE_UNDEFINE
                }
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()
            ?.addItemType(ResourceTypeConstans.TYPE_ARTICLE, R.layout.item_article_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_NO_IMAGE, R.layout.item_no_image_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_COURSE, R.layout.item_course_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_MICRO_LESSON, R.layout.item_micro_course_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_KNOWLEDGE, R.layout.item_knowlege_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_ALBUM, R.layout.item_album_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_TRACK, R.layout.item_track_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_BAIKE, R.layout.item_baike_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_PERIODICAL, R.layout.item_periodical_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_E_BOOK, R.layout.item_book_guess)
            ?.addItemType(ResourceTypeConstans.TYPE_RECOMMEND_USER, R.layout.item_recommend_user)
            ?.addItemType(ResourceTypeConstans.TYPE_SPECIAL, R.layout.item_recommend_rcy)
            ?.addItemType(ResourceTypeConstans.TYPE_SOURCE_FOLDER, R.layout.item_guess_study_folder)
            ?.addItemType(ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE, R.layout.item_micro_knowledge)
            ?.addItemType(ResourceTypeConstans.TYPE_UNDEFINE, R.layout.item_recommend_empty)
    }

    override fun convert(helper: BaseViewHolder, bean: ResultBean) {
//        val type: Int = bean.resource_type
        SkinManager.getInstance().injectSkin(helper.itemView)
        when (val type: Int = getItemViewType(helper.layoutPosition)) {
            ResourceTypeConstans.TYPE_ARTICLE -> {
                setTvType(helper, type)
                helper.setText(R.id.tvTitle, bean.title)

                var pStr = "${bean.platform_zh} | ${bean.source}"
                if (TextUtils.isEmpty(bean.platform_zh) || TextUtils.isEmpty(bean.source)) {
                    pStr = pStr.replace("|", "")
                }
                helper.setText(R.id.tvPlatform, pStr)


                Glide.with(context).load(bean.picture)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .into(helper.getView(R.id.ivCover))

            }
            ResourceTypeConstans.TYPE_NO_IMAGE -> {

                helper.setText(R.id.title, bean.title)
                if (TextUtils.isEmpty(bean.platform_zh)) {
                    helper.setText(R.id.org, bean.source)
                } else {
                    helper.setText(R.id.org, bean.platform_zh + " | " + bean.source)
                }
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                setTvType(helper, type)
                if (!TextUtils.isEmpty(bean.recommend_data?.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    helper.setText(R.id.tvTypeGuessCourse, bean.recommend_data?.identity_name)
                    helper.setGone(R.id.tvTypeGuessCourse, false)
                    helper.setGone(R.id.type_name, true)
                } else {
                    helper.setGone(R.id.tvTypeGuessCourse, true)
                    helper.setGone(R.id.type_name, false)
                }
                helper.setText(R.id.tvTitle, bean.title)
                helper.setText(R.id.tvCompany, bean.org)
                helper.setText(R.id.tvTime, bean.course_start_time)
                val tvTime = helper.getView<TextView>(R.id.tvTime)
                if (SkinManager.getInstance().needChangeSkin()) {
                    val tvTimeColor =
                        SkinManager.getInstance().resourceManager.getColor("colorPrimary")
                    tvTime.setTextColor(tvTimeColor)
                } else {
                    tvTime.setTextColor(context.getColorRes(R.color.colorPrimary))
                }
                helper.setText(R.id.tvPlatform, bean.platform_zh)
                setCourseTypeInfo(helper.getView(R.id.tvCourseTypeInfo), bean)
                Glide.with(context)
                    .load(bean.picture)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .into(helper.getView(R.id.ivCover));
            }

            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {
//                setTvType(helper, type)
                helper.setText(R.id.tvTitle, bean.recommend_data?.title)
                helper.setText(R.id.tvNumLearn, bean.recommend_data?.click_num + "人学习")
                helper.setText(R.id.tvNumPass, bean.recommend_data?.exam_pass_num + "人通过微测试")
                helper.setText(R.id.tvNumLike, bean.recommend_data?.like_num + "人点赞")
                Glide.with(context)
                    .load(bean.recommend_data?.pic)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .into(helper.getView(R.id.ivCover));
            }

            ResourceTypeConstans.TYPE_MICRO_LESSON -> {
                helper.setText(R.id.tvTitle, bean.title)
                helper.setText(R.id.desc, bean.platform_zh)

                if (!TextUtils.isEmpty(bean.recommend_data?.video_duration)) {
                    val video_duration: Long
                    video_duration = try {
                        bean.recommend_data?.video_duration!!.toLong()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    helper.setText(R.id.time, TimeUtils.timeParse(video_duration))
                } else {
                    helper.setText(R.id.time, "")
                }

                Glide.with(context)
                    .load(bean.picture)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(helper.getView(R.id.cover));
            }
            ResourceTypeConstans.TYPE_KNOWLEDGE -> {
                setTvType(helper, type)
                helper.setText(R.id.tvTitle, bean.frag_title)
                helper.setText(R.id.tvPlatform, context.getString(R.string.text_str_source_xt))
                Glide.with(context)
                    .load(bean.picture)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(helper.getView(R.id.ivCover));
            }

            ResourceTypeConstans.TYPE_SOURCE_FOLDER -> {
                helper.setText(R.id.tvTitle, bean.recommend_data?.name)
                helper.setText(R.id.tvCount, "共${bean.recommend_data?.resource_count}个学习资源")
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                setTvType(helper, type)
                val album: AlbumBean? = bean.album_data
                if (album != null && album.id.isNotEmpty()) {
                    helper.setText(R.id.tvTitle, album.albumTitle)
                    helper.setText(
                        R.id.tvPlayCount,
                        String.format(
                            context.getResources().getString(R.string.text_str_play_count),
                            album.playCount.toString()
                        )
                    )
                    helper.setText(R.id.tvSetNum, album.includeTrackCount.toString() + "集")
                    helper.setText(R.id.tvPlatform, context.getString(R.string.text_xi_ma))
                    Glide.with(context)
                        .load(album.cover_url_middle)
                        .transform(GlideTransform.centerCropAndRounder2)
                        .placeholder(R.mipmap.common_bg_cover_default)
                        .into(helper.getView(R.id.ivCover));
                }
            }

            ResourceTypeConstans.TYPE_TRACK -> {
                setTvType(helper, type);
//                helper.setGone(R.id.include, true);
                val track: TrackBean? = bean.track_data
                if (track != null && track.dataId > 0) {
                    helper.setText(R.id.tvTitle, track.trackTitle);
                    helper.setText(R.id.tvTimeLong, TimeUtils.timeParse(track.duration.toLong()));
                    helper.setText(R.id.tvPlatform, context.getString(R.string.text_xi_ma));
                    helper.setText(
                        R.id.tvTrackAlbum,
                        StringFormatUtil.formatPlayCount(track.playCount.toLong())
                    );
                    Glide.with(context)
                        .load(track.cover_url_small)
                        .transform(GlideTransform.centerCropAndRounder2)
                        .placeholder(R.mipmap.common_bg_cover_default)
                        .into(helper.getView(R.id.ivCover));
                }
            }

            ResourceTypeConstans.TYPE_BAIKE -> {
                setTvType(helper, type)
                helper.setText(R.id.tvTitle, bean.title)
                helper.setText(R.id.tvDesc, Html.fromHtml(bean.content))

            }


            ResourceTypeConstans.TYPE_PERIODICAL -> {
                setTvType(helper, type)
                helper.setText(R.id.tvTitle, Html.fromHtml(bean.title))
                helper.setText(R.id.tvAuthor, bean.basic_creator)
                helper.setText(R.id.tvTime, Html.fromHtml(bean.basic_date_time))
                Glide.with(context)
                    .load(bean.basic_cover_url)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(helper.getView(R.id.ivCover));
            }

            ResourceTypeConstans.TYPE_E_BOOK -> {
                setTvType(helper, type)
                helper.setText(R.id.tvTitle, bean.title)
                helper.setText(R.id.orgs, bean.writer)
                val orgTv: TextView = helper.getView(R.id.orgs)
                helper.setText(
                    R.id.num,
                    StringFormatUtil.formatPlayCount(bean.word_count.toLong()).toString() + "字"
                )
                val org = if (TextUtils.isEmpty(bean.press)) "" else bean.press
                if (TextUtils.isEmpty(bean.platform_zh)) {
                    helper.setText(R.id.tvPlatform, org)
                } else {
                    helper.setText(R.id.tvPlatform, bean.platform_zh.toString() + " | " + org)
                }
                Glide.with(context)
                    .load(bean.picture)
                    .transform(GlideTransform.centerCropAndRounder2)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .into(helper.getView(R.id.ivCover))
            }

            ResourceTypeConstans.TYPE_SPECIAL -> {
                val guessColumnView = helper.getView<HomeDiscoverColumnView>(R.id.rcy_guess);
                val list = arrayListOf<RecommendColumn>()
                if (bean.recommend_data != null) {
                    list.add(bean.recommend_data!!)
                }
                guessColumnView.setColumnData(list);
            }
        }
    }

    private fun setTvType(helper: BaseViewHolder, type: Int) {
        val typeStr = ResourceTypeConstans.typeStringMap[type]
        helper.setText(R.id.type_name, typeStr)
        helper.setGone(R.id.type_name, TextUtils.isEmpty(typeStr))
    }

    /**
     * 设置课程类型信息
     * (考试,证书,付费)
     * 中间点的规则,如果前面点亮,点也点亮
     */
    private fun setCourseTypeInfo(@NonNull textView: TextView, bean: ResultBean) {
        val isHaveExam: Int = bean.is_have_exam
        val isVerified: Int = bean.verified_active
        val isFree: Int = bean.is_free
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
        if (isFree == 0) {
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

    private fun setResourceType(@NonNull holder: BaseViewHolder, bean: ResultBean) {
        val isHaveExam: Int = bean.is_have_exam
        val isVerified: Int = bean.verified_active
        val isFree: Int = bean.is_free
        val platform: Int = bean.platform
        val dart = "·"
        if (isHaveExam == 1) {
            holder.setText(
                R.id.tvExamType,
                context.resources.getString(R.string.course_str_have_exam) + dart
            )
            holder.setTextColor(R.id.tvExamType, context.resources.getColor(R.color.colorPrimary))
        } else {
            holder.setText(
                R.id.tvExamType,
                context.resources.getString(R.string.course_str_no_exam) + dart
            )
            holder.setTextColor(R.id.tvExamType, context.resources.getColor(R.color.color_636363))
        }
        if (!TextUtils.isEmpty(bean.is_have_exam_info)) {
            holder.setText(R.id.tvExamType, bean.is_have_exam_info + dart)
        }
        if (isVerified == 1) {

            holder.setText(
                R.id.tvCertificateType,
                context.resources.getString(R.string.course_str_have_certificate) + dart
            )
            holder.setTextColor(
                R.id.tvCertificateType,
                context.resources.getColor(R.color.colorPrimary)
            )
        } else {
            holder.setText(
                R.id.tvCertificateType,
                context.resources.getString(R.string.course_str_no_certificate) + dart
            )
            holder.setTextColor(
                R.id.tvCertificateType,
                context.resources.getColor(R.color.color_636363)
            )
        }
        if (!TextUtils.isEmpty(bean.verified_active_info)) {
            holder.setText(R.id.tvCertificateType, bean.verified_active_info + dart)
        }
        if (isFree == 0) {
            holder.setText(R.id.tvPriceType, context.resources.getString(R.string.course_str_pay))
            holder.setTextColor(R.id.tvPriceType, context.resources.getColor(R.color.colorPrimary))
        } else {
            holder.setText(R.id.tvPriceType, context.resources.getString(R.string.course_str_free))
            holder.setTextColor(R.id.tvPriceType, context.resources.getColor(R.color.colorPrimary))
        }
        if (!TextUtils.isEmpty(bean.is_free_info)) {
            holder.setText(R.id.tvPriceType, bean.is_free_info)
        }
    }

}