package com.mooc.commonbusiness.adapter

import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.R
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.CourseBean

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class CommonCourseAdapter @JvmOverloads constructor(
    data: MutableList<CourseBean>?,
    var isGoneTag: Boolean = false,
    layoutResId: Int = R.layout.commonrs_item_course
) : BaseQuickAdapter<CourseBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: CourseBean) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_source, item.platform_zh)
        holder.setText(R.id.tv_author, item.org)
        holder.setText(R.id.tv_opening, item.course_start_time)

        holder.setText(R.id.tv_course_Lable, R.string.course)
        holder.setText(R.id.tvTypeCommonCourse, R.string.course)

        if (isGoneTag) {//不显示type
            holder.setGone(R.id.tv_course_Lable, true)
            holder.setGone(R.id.tvTypeCommonCourse, true)
        } else {
            if (!TextUtils.isEmpty(item.identity_name)) {
                holder.setText(R.id.tvTypeCommonCourse, item.identity_name)
                holder.setGone(R.id.tv_course_Lable, true)
                holder.setGone(R.id.tvTypeCommonCourse, false)
            } else {
                holder.setGone(R.id.tv_course_Lable, false)
                holder.setGone(R.id.tvTypeCommonCourse, true)
            }
        }

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        imageView?.let {
            Glide.with(context)
                .load(item.picture)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)

        }
        setResourceType(holder, item)
    }

    private fun setResourceType(holder: BaseViewHolder, detailBean: CourseBean) {
        val isHaveExam: String? = detailBean.is_have_exam
        val isVerified: String? = detailBean.verified_active
        val isFree: String? = detailBean.is_free
//        lar platform: Int = detailBean.platform
//        lar noExamAndVerified: String? = null

        val tvExamType: TextView = holder.getView(R.id.tvType)

        val tvCertificateType: TextView = holder.getView(R.id.tvCertificateType)
        val tvPriceType: TextView = holder.getView(R.id.tvPriceType)
        if (isHaveExam == "1") {
            tvExamType.text = context.resources.getString(R.string.course_str_have_exam)
            with(tvExamType) { setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)) }

        } else {
            tvExamType.setText(context.getResources().getString(R.string.course_str_no_exam))
            tvExamType.setTextColor(ContextCompat.getColor(context, R.color.color_636363))
        }
//        if (!TextUtils.isEmpty(detailBean.is_have_exam_info)) {
//            tvExamType.setText(detailBean.is_have_exam_info)
//        }
        if (isVerified == "1" || isVerified == "true") {
            tvCertificateType.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        } else {
            tvCertificateType.setTextColor(ContextCompat.getColor(context, R.color.color_636363))
        }

        val verifiStr = if (TextUtils.isEmpty(detailBean.verified_active_info)) {
            if (isVerified == "1" || isVerified == "true") {
                "有证书"
            } else {
                "无证书"
            }
        } else {
            detailBean.verified_active_info
        }
        tvCertificateType.setText("${context.getString(R.string.point)} ${verifiStr}")

//        if (!TextUtils.isEmpty(detailBean.verified_active_info)) {
//            tvCertificateType.setText(context.getString(R.string.point)+detailBean.verified_active_info)
//        }
        if (isFree == "0") {
            tvPriceType.setText(
                context.getString(R.string.point) + " " + context.getResources()
                    .getString(R.string.course_str_pay)
            )
            tvPriceType.setTextColor(context.getResources().getColor(R.color.color_636363))
        } else {
            tvPriceType.setText(
                context.getString(R.string.point) + " " + context.getResources()
                    .getString(R.string.course_str_free)
            )
            tvPriceType.setTextColor(context.getResources().getColor(R.color.colorPrimary))
        }
//        if (!TextUtils.isEmpty(detailBean.is_free_info)) {
//            tvPriceType.setText(context.getString(R.string.point)+detailBean.is_free_info)
//        }
    }
}