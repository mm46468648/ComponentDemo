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
class SpecialMode2Adapter(data: ArrayList<RecommendContentBean.DataBean>?) :
    BaseQuickAdapter<RecommendContentBean.DataBean, BaseViewHolder>(R.layout.item_special_mode_two,data), LoadMoreModule {


    override fun convert(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        setTwoData(holder, item)

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


    private fun setTwoData(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {
        
        //封面图
        val image: String =
            if (TextUtils.isEmpty(item.big_image)) item.small_image else item.big_image
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_default)
            .into(holder.getView(R.id.ivCover))
        
        //标题
        holder.setText(R.id.tvTitle, item.title)
        //推荐理由
        holder.setText(R.id.tvBottom, item.recommend_reason)

        //初始化tvFirst,tvSecond,tvThird,tvFourth
        holder.setText(R.id.tvFirst, "")
        holder.setText(R.id.tvSecond, "")
        holder.setText(R.id.tvThird, "")
        holder.setText(R.id.tvFourth, "")


//        holder.setGone(R.id.rlLeftLineRight, true)
//        holder.setGone(R.id.tvRight, true)
//        holder.setGone(R.id.tvCenterOneStyleTwo, true)
//        holder.setGone(R.id.tvCenterTwoStyleTwo, true)
//        holder.setGone(R.id.tvCenterThreeStyleTwo, true)



        //资源类型
        val value = typeStringMap[item.type]
        holder.setText(R.id.tv_type, value)
        holder.setGone(R.id.tv_type, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeListTwo, value)
        holder.setGone(R.id.tvTypeListTwo, true)

        when (item.type) {
            ResourceTypeConstans.TYPE_TASK -> {
                val spanStr = getSpaString(
                    item.success_score,
                    ContextCompat.getColor(context, R.color.color_c),
                    ContextCompat.getColor(context, R.color.color_white),
                    12.sp2px(),
                    15.sp2px()
                )

                val total = "${spanStr}      |      ${(if (item.join_num.isNullOrEmpty()) "0" else item.join_num)}人参与"
                holder.setText(R.id.tvFourth,total)
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {

                val total = "更新至${item.periodicals_data.year}年第${item.periodicals_data.term}期      |      ${item.periodicals_data.unit}"
                holder.setText(R.id.tvFourth,total)
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

                val total = "${item.start_time}      |      ${item.source}"
                holder.setText(R.id.tvFourth,total)
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
                holder.setText(R.id.tv_type, R.string.ebook)

//                holder.setGone(R.id.tvCenterOneStyleTwo, false)
//                holder.setText(R.id.tvCenterOneStyleTwo, item.staff)
//                holder.setText(R.id.tvLineLift, item.writer)
                holder.setText(R.id.tvFirst, item.writer)

            }
            ResourceTypeConstans.TYPE_KNOWLEDGE, ResourceTypeConstans.TYPE_MICRO_LESSON -> {

                var total = item.source
                if (!TextUtils.isEmpty(item.getVideo_duration())) {
                    val video_duration: Long = try {
                        item.getVideo_duration().toLong()
                    } catch (e: java.lang.NumberFormatException) {
                        0
                    }
                    total += "      |      ${timeParse(video_duration)}"
                }
                holder.setText(R.id.tvFourth,total)

            }

            ResourceTypeConstans.TYPE_ALBUM -> {
                val album: AlbumBean = item.album_data
                val num: String = album.track_count.toString() + "集"
                holder.setText(R.id.tvFirst, num)

            }
            ResourceTypeConstans.TYPE_TRACK -> {
                val track: TrackBean = item.track_data
                if (track.subordinated_album != null) {
                    var total = track.subordinated_album?.album_title
                    if (track.duration != 0L) {
                        total += "      |      ${timeParse(track.duration)}"
                    }
                    holder.setText(R.id.tvFourth,total)
                }
            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {
//                holder.setGone(R.id.tvCenterOneStyleTwo, false)

                if (item.price > 0) {
                    val p: Float = item.price / 100
                    val price = String.format(context.resources.getString(R.string.text_f), p)
                    holder.setText(
                        R.id.tvFirst,
                        String.format(
                            context.resources.getString(R.string.text_price_master), price
                        )
                    )
                } else {
                    holder.setText(R.id.tvFirst, "")

                }
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {

                val total = "${item.staff}      |      ${item.source}"
                holder.setText(R.id.tvFourth,total)

            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {

                var total = "${item.staff}      |      ${item.source}"
                if(TextUtils.isEmpty(item.staff) || TextUtils.isEmpty(item.source)){
                    total = total.replace("|","").trim()
                }

                holder.setText(R.id.tvFourth,total)
            }
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {//友情链接
//                holder.setGone(R.id.tv_type, true)
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {//跟读
                holder.setGone(R.id.tv_type, true)
            }
            ResourceTypeConstans.TYPE_ACTIVITY -> {
                holder.setText(R.id.tvFirst, item.about)
                holder.setText(R.id.tvSecond, item.publish_time)

            }
            ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
                holder.setText(R.id.tvFirst, item.about)
                holder.setText(R.id.tvSecond, item.publish_time)
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                holder.setText(R.id.tvFirst, item.staff)
                var total =  if (item.time_mode.equals("1")) {//时间永久
                    context.resources.getString(R.string.study_time_permanent_opening)
                } else {
                    getTime(item.start_time)
                }

                total += "      |      ${item.student_num}人"

                holder.setText(R.id.tvFourth,total)

            }
            ResourceTypeConstans.TYPE_COLUMN -> {
                holder.setText(R.id.tvFirst, item.staff)
                holder.setText(R.id.tvSecond, item.desc)
                holder.setText(R.id.tvThird, item.update_time)
            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tv_type, item.identity_name)
                }
//                setViewVisibility(
//                    holder.getView(R.id.tvCenterTwoStyleTwo),
//                    item.resource_info?.title ?: ""
//                )
//                setViewVisibility(
//                    holder.getView(R.id.tvCenterThreeStyleTwo),
//                    item.resource_info?.updated_time ?: ""
//                )
                holder.setText(R.id.tvSecond, item.resource_info?.title)
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(
                        R.id.tvThird,
                        item.resource_info?.updated_time + "更新"
                    )
                }
            }
            ResourceTypeConstans.TYPE_BAIKE -> {
//                holder.setGone(R.id.tvCenterOneStyleTwo, false)
//                setViewVisibility(holder.getView(R.id.tvCenterOneStyleTwo), item.source)
                holder.setText(R.id.tvFirst, item.source)
            }
            ResourceTypeConstans.TYPE_TEST_VOLUME -> {
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                val bean = item.audio_data
                if (bean != null) {
                    val total = "播放 ${bean.audio_play_num}次      |      ${timeParse(bean.audio_time)}"
                    holder.setText(R.id.tvFourth,total)
                }
            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业

            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识
//                holder.setGone(R.id.tvCenterOneStyleTwo, false)
//                holder.setGone(R.id.tvCenterTwoStyleTwo, false)
//                setViewVisibility(
//                    holder.getView(R.id.tvCenterTwoStyleTwo),
//                    item.resource_info?.title ?: ""
//                )
//                setViewVisibility(
//                    holder.getView(R.id.tvCenterThreeStyleTwo),
//                    item.resource_info?.updated_time ?: ""
//                )
                holder.setText(R.id.tvSecond, item.resource_info?.title)
                if (!TextUtils.isEmpty(item.resource_info?.updated_time)) {
                    holder.setText(
                        R.id.tvThird,
                        item.resource_info?.updated_time + "更新"
                    )
                }
            }
            else -> {
                holder.setGone(R.id.tvFirst, true)
                holder.setGone(R.id.tvSecond, true)
                holder.setGone(R.id.tvThird, true)
                holder.setGone(R.id.tvFourth, true)
            }
        }

        setViewVisibility(holder.getView(R.id.tvFirst))
        setViewVisibility(holder.getView(R.id.tvSecond))
        setViewVisibility(holder.getView(R.id.tvThird))
        setViewVisibility(holder.getView(R.id.tvFourth))
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

    private fun setViewVisibility(textView: TextView) {
        val str = textView.text
        if (TextUtils.isEmpty(str)) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
        }
    }
}