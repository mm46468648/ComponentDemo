package com.mooc.discover.adapter.special

import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
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
class SpecialMode1Adapter(data: ArrayList<RecommendContentBean.DataBean>?) :
    BaseQuickAdapter<RecommendContentBean.DataBean, BaseViewHolder>(
        R.layout.item_special_mode_one,
        data
    ), LoadMoreModule {


    override fun convert(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        setOneData(holder, item)
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
        //封面图
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image

        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_default)
            .placeholder(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into(holder.getView(R.id.ivCover))

        //标题
        holder.setText(R.id.tvTitle, item.title)

        //初始化tvThirdLeft,ivPlay,tvThirdRight,tvType
        holder.setText(R.id.tvThirdLeft, "")
        holder.setText(R.id.tvSecond, "")
        holder.setGone(R.id.ivPlay, true)
        holder.setGone(R.id.tvThirdRight, true)//微课显示，其余隐藏

        val value = typeStringMap[item.type]
        holder.setText(R.id.tvType, value)
        holder.setGone(R.id.tvType, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeListOne, value)
        holder.setGone(R.id.tvTypeListOne, true)

        when (item.type) {

            ResourceTypeConstans.TYPE_TASK -> {
                holder.setText(
                    R.id.tvSecond,
                    getSpaString(
                        item.success_score,
                        ContextCompat.getColor(context, R.color.color_5D5D5D),
                        ContextCompat.getColor(context, R.color.color_5D5D5D),
                        13.sp2px(),
                        15.sp2px()
                    )
                )

                holder.setText(
                    R.id.tvThirdLeft,
                    (if (item.join_num.isNullOrEmpty()) "0" else item.join_num) + "人参与"
                )
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
                holder.setText(R.id.tvType, R.string.publicaton)
                holder.setText(
                    R.id.tvSecond,
                    "更新至" + item.periodicals_data.year + "年" + "第" + item.periodicals_data.term + "期"
                )
                holder.setText(R.id.tvThirdLeft, item.periodicals_data.unit)
            }
            ResourceTypeConstans.TYPE_COURSE -> {
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tvTypeListOne, item.identity_name)
                    holder.setGone(R.id.tvTypeListOne, false)
                    holder.setGone(R.id.tvType, true)
                } else {
                    holder.setGone(R.id.tvTypeListOne, true)
                    holder.setGone(R.id.tvType, false)
                }
                holder.setText(R.id.tvSecond, item.org)
                holder.setText(R.id.tvThirdLeft, item.start_time)
            }
            ResourceTypeConstans.TYPE_KNOWLEDGE,
            ResourceTypeConstans.TYPE_MICRO_LESSON,
            -> {
                holder.setText(R.id.tvType, R.string.micro_course)
                holder.setText(R.id.tvSecond, item.org)//开课单位
                if (!item.video_duration.isNullOrEmpty()) {
                    val videoDuration: Long = try {
                        item.video_duration.toLong()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    holder.setGone(R.id.tvThirdRight, false)//微课显示，其余隐藏
                    holder.setText(
                        R.id.tvThirdRight,
                        timeParse(videoDuration)
                    )
                }

                holder.setText(R.id.tvThirdLeft, item.source)//来源
            }

            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.tvType, R.string.ebook)
                holder.setText(R.id.tvSecond, item.writer)
                holder.setText(R.id.tvThirdLeft, item.source)
            }

            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setText(R.id.tvType, R.string.album)
                holder.setGone(R.id.ivPlay, false)
                val album = item.album_data
                holder.setText(
                    R.id.tvSecond,
                    "${album?.track_count ?: 0}集"
                )
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setText(R.id.tvType, R.string.track)
                holder.setGone(R.id.ivPlay, false)
                val track = item.track_data
                holder.setText(
                    R.id.tvSecond,
                    track.subordinated_album?.album_title ?: ""
                )
                holder.setText(R.id.tvThirdLeft, timeParse(track.duration))
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
                holder.setText(R.id.tvType, R.string.periodical)
                holder.setText(R.id.tvSecond, item.staff)
                holder.setText(R.id.tvThirdLeft, item.source)

            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {//专栏文章
                holder.setText(R.id.tvType, R.string.article)
                holder.setText(R.id.tvSecond, item.staff)
                holder.setText(R.id.tvThirdLeft, item.source)
            }

            ResourceTypeConstans.TYPE_ARTICLE -> {
                holder.setText(R.id.tvType, R.string.article)
                holder.setText(R.id.tvSecond, item.staff)
                holder.setText(R.id.tvThirdLeft, item.source)
            }
            ResourceTypeConstans.TYPE_SPECIAL -> {//合集
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tvType, item.identity_name)
                } else {
                    holder.setText(R.id.tvType, R.string.special)
                }
                if (item.resource_info!=null&&!TextUtils.isEmpty(item.resource_info.title)) {
                    holder.setText(R.id.tvSecond, item.resource_info.title)
                }
                if (item.resource_info!=null&&!TextUtils.isEmpty(item.resource_info.updated_time)) {
                    holder.setText(R.id.tvThirdLeft, item.resource_info.updated_time + "更新")
                }

            }
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {//外部链接
//                holder.setGone(R.id.tvType, true)
                holder.setText(R.id.tvSecond, "")

            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读
                holder.setGone(R.id.tvType, true)
                holder.setText(R.id.tvSecond, "")

            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setText(R.id.tvType, R.string.study_plan)
                holder.setText(R.id.tvSecond, item.staff)
                holder.setText(R.id.tvThirdLeft, "${item.student_num}人")
            }
            ResourceTypeConstans.TYPE_COLUMN -> {//专栏
                holder.setText(R.id.tvType, R.string.column)
                holder.setText(R.id.tvSecond, item.desc)
                holder.setText(R.id.tvThirdLeft, item.update_time)
            }
            ResourceTypeConstans.TYPE_ACTIVITY -> {//活动
                holder.setText(R.id.tvType, R.string.activity)
                holder.setText(R.id.tvSecond, item.about)
                holder.setText(R.id.tvThirdLeft, item.update_time)
            }
            ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {////活动任务
                holder.setText(R.id.tvType, R.string.activity_task)
                holder.setText(R.id.tvSecond, item.about)
                holder.setText(R.id.tvThirdLeft, item.update_time)
            }
            ResourceTypeConstans.TYPE_BAIKE -> {//
                holder.setText(R.id.tvType, R.string.baike)
                holder.setText(R.id.tvSecond, item.source)

            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {//大师课
                holder.setText(R.id.tvType, R.string.master_talk)
                if (item.price > 0) {
                    val p: Float = item.getPrice() / 100
                    val price = String.format(context.getResources().getString(R.string.text_f), p)
                    holder.setText(
                        R.id.tvSecond, String.format(
                            context.getResources()
                                .getString(R.string.text_price_master), price
                        )
                    )

                } else {
                    holder.setText(R.id.tvSecond, "")
                }
                holder.setText(R.id.tvThirdLeft, item.getDesc())
            }
            ResourceTypeConstans.TYPE_TEST_VOLUME -> {//测试卷
                holder.setText(R.id.tvType, R.string.test_volume)
                holder.setText(R.id.tvSecond, item.source)
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {//问卷
                holder.setText(R.id.tvType, R.string.questionnaire)
                holder.setText(R.id.tvSecond, "")

            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {////自建音频
                holder.setText(R.id.tvType, R.string.oneself_track)
                holder.setGone(R.id.ivPlay, false)

                val musicBean = item.audio_data
                holder.setText(
                    R.id.tvSecond,
                    "播放 ${formatPlayCount(musicBean.audio_play_num.toInt())}次"

                )
                holder.setText(
                    R.id.tvThirdLeft,
                    timeParse(musicBean.audio_time.toLong())
                )


            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业

            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识
                holder.setText(R.id.tvSecond, item.resource_info?.title)
                if (item.resource_info!=null&&!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(
                        R.id.tvThirdLeft,
                        item.resource_info?.updated_time + "更新"
                    )
                }
            }

            else -> {
                holder.setGone(R.id.ivPlay, true)
                holder.setGone(R.id.tvType, true)
            }
        }

        //如果第二行没有内容,则隐藏
        val tvSecond = holder.getView<TextView>(R.id.tvSecond)
        if (TextUtils.isEmpty(tvSecond.text)) {
            tvSecond.visibility = View.GONE
        } else {
            tvSecond.visibility = View.VISIBLE
        }

        //如果第三行左边没有内容,则隐藏
        val tvThirdLeft = holder.getView<TextView>(R.id.tvThirdLeft)
        if (TextUtils.isEmpty(tvThirdLeft.text)) {
            tvThirdLeft.visibility = View.GONE
        } else {
            tvThirdLeft.visibility = View.VISIBLE
        }
    }


}