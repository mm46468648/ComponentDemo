package com.mooc.commonbusiness.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.common.R;

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class CommonCourseOneAdapter @JvmOverloads constructor(
    data: MutableList<CourseBean>?,
    layoutResId: Int = R.layout.commonrs_item_course_one
) : BaseQuickAdapter<CourseBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: CourseBean) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_source, item.platform_zh)
        holder.setText(R.id.tv_author, item.org)
        holder.setText(R.id.tv_opening, item.course_start_time)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        imageView?.let {
            Glide.with(context)
                .load(item.picture)
                .error(R.mipmap.common_bg_cover_default)
                .placeholder(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }
        setResourceType(holder, item)
    }

    @SuppressLint("SetTextI18n")
    private fun setResourceType(holder: BaseViewHolder, detailBean: CourseBean) {
        val isHaveExam: String? = detailBean.is_have_exam
        val isVerified: String? = detailBean.verified_active
        val isFree: String? = detailBean.is_free
        var platform: Int = detailBean.platform
        var noExamAndVerified: String? = null

        val tvExamType: TextView = holder.getView(R.id.tvType)

        val tvCertificateType: TextView = holder.getView(R.id.tvCertificateType)
        val tvPriceType: TextView = holder.getView(R.id.tvPriceType)
        if (isHaveExam == "1") {
            tvExamType.text = context.resources.getString(R.string.course_str_have_exam)
            tvExamType.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            tvExamType.text = context.resources.getString(R.string.course_str_no_exam)
            tvExamType.setTextColor(context.resources.getColor(R.color.color_636363))
        }
//        if (!TextUtils.isEmpty(detailBean.is_have_exam_info)) {
//            tvExamType.setText(detailBean.is_have_exam_info)
//        }

        if (isVerified == "1" || isVerified == "1.0") {
//            if (platform == CoursePlatFormConstants.COURSE_PLATFORM_MOOC) {
//                tvCertificateType.text = context.getString(R.string.point) + context.resources.getString(R.string.course_str_charge_certificate)
//            } else {
//                tvCertificateType.text = context.getString(R.string.point) + context.resources.getString(R.string.course_str_free_certificate)
//            }
            tvCertificateType.text =
                context.getString(R.string.point) + context.resources.getString(R.string.course_str_have_certificate)
            tvCertificateType.setTextColor(context.resources.getColor(R.color.colorPrimary))

        } else {
            tvCertificateType.text =
                context.getString(R.string.point) + context.resources.getString(R.string.course_str_no_certificate)
            tvCertificateType.setTextColor(context.resources.getColor(R.color.color_636363))
        }

//        if (!TextUtils.isEmpty(detailBean.verified_active_info)) {
//            tvCertificateType.setText(context.getString(R.string.point)+detailBean.verified_active_info)
//        }
        if (isFree == "0") {
            tvPriceType.text =
                context.getString(R.string.point) + context.resources.getString(R.string.course_str_pay)
            tvPriceType.setTextColor(context.resources.getColor(R.color.color_636363))
        } else {
            tvPriceType.text =
                context.getString(R.string.point) + context.resources.getString(R.string.course_str_free)
            tvPriceType.setTextColor(context.resources.getColor(R.color.colorPrimary))
        }
//        if (!TextUtils.isEmpty(detailBean.is_free_info)) {
//            tvPriceType.setText(context.getString(R.string.point)+detailBean.is_free_info)
//        }
    }
}