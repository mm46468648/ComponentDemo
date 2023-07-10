package com.mooc.discover.binding

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mooc.discover.R
import com.mooc.discover.model.MasterOrderBean
import com.mooc.discover.model.RecommendContentBean
import com.mooc.discover.model.RecommendColumn
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import java.text.SimpleDateFormat
import java.util.*

object Bindings {

    @JvmStatic
    @BindingAdapter(value = ["imageUrl"])
    fun loadimage(imageView: ImageView, url: String?) {
        Glide.with(imageView.context).load(url).placeholder(R.mipmap.common_bg_cover_default).into(imageView)
    }


    var dort = "·"




//    @SuppressLint("SetTextI18n")
//    @JvmStatic
//    @BindingAdapter(value = ["typeVerifyRecommend"])
//    fun setTypeVerify(tv: TextView, bean: RecommendColumn) {
//        val isVerified: String = bean.verified_active
//        if (isVerified == "1" || isVerified == "true") {
//            tv.text = tv.context.resources.getString(R.string.course_str_have_certificate) + dort
//            tv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
//        } else {
//            tv.text = tv.context.resources.getString(R.string.course_str_no_certificate) + dort
//            tv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
//        }
//        if (!TextUtils.isEmpty(bean.verified_active_info)) {
//            tv.text = bean.verified_active_info + dort
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    @JvmStatic
//    @BindingAdapter(value = ["typeVerifyRecommend"])
//    fun setSpecialTypeVerify(tv: TextView, bean: RecommendContentBean.DataBean) {
//        val isVerified: String = bean.verified_active
//        if (isVerified == "1" || isVerified == "true") {
//            tv.text = tv.context.resources.getString(R.string.course_str_have_certificate) + dort
//            tv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
//        } else {
//            tv.text = tv.context.resources.getString(R.string.course_str_no_certificate) + dort
//            tv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
//        }
//        if (!TextUtils.isEmpty(bean.verified_active_info)) {
//            tv.setText(bean.verified_active_info + dort)
//        }
//    }



    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    @BindingAdapter(value = ["planState"])
    fun setPlanState(tv: TextView, dataBean: RecommendContentBean.DataBean) {
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd");


        var start: Date? = null;
        var end: Date? = null;
        var join_start: Date? = null;
        var join_end: Date? = null;


        if(dataBean.plan_starttime.isNotEmpty()){
            start = simpleDateFormat.parse(dataBean.getPlan_starttime());
        }

        if(dataBean.plan_endtime.isNotEmpty()){
            end = simpleDateFormat.parse(dataBean.getPlan_endtime());
        }
        if(dataBean.join_start_time.isNotEmpty()){
            join_start = simpleDateFormat.parse(dataBean.getJoin_start_time());
        }
        if(dataBean.join_end_time.isNotEmpty()){
            join_end = simpleDateFormat.parse(dataBean.getJoin_end_time());
        }
//        end = simpleDateFormat.parse(dataBean.getPlan_endtime());
//        join_start = simpleDateFormat.parse(dataBean.getJoin_start_time());
//        join_end = simpleDateFormat.parse(dataBean.getJoin_end_time());

        if (start != null && end != null && join_start != null && join_end != null) {

            when (isEnrolment(DateUtil.getCurrentTime(), start.getTime(), end.getTime())) {
                0 -> {
                    when (isEnrolment(DateUtil.getCurrentTime(), join_start.getTime(), join_end.getTime())) {
                        0 -> tv.visibility = View.GONE
                        1 -> {
                            tv.visibility = View.VISIBLE
                            tv.text = "报名中"
                        }

                        2 -> {
                            tv.visibility = View.VISIBLE
                            tv.text = "进行中"
                        }

                    }
                    tv.setBackgroundResource(R.drawable.bg_plan_light_green)
                }
                1 -> {
                    tv.visibility = View.VISIBLE
                    tv.text = "进行中"
                    tv.setBackgroundResource(R.drawable.bg_plan_light_green);
                }
                2 -> {
                    tv.visibility = View.VISIBLE
                    tv.text = "已结束"
                    tv.setBackgroundResource(R.drawable.shape_color9e0_corners2);
                }
            }
        }


    }

    fun isEnrolment(time: Long, startTime: Long, stopTime: Long): Int {
        if (startTime > 0 && stopTime > 0) {
            if (time < startTime) {//尚未到报名期
                return 0;
            } else {
                if (time <= stopTime) {//报名期
                    return 1;
                } else {//报名期结束
                    return 2;
                }
            }
        } else {
            return 3;
        }
    }



//    @JvmStatic
//    @BindingAdapter(value = ["typePriceRecommend"])
//    fun setTypePrice(tv: TextView, bean: RecommendColumn) {
//        val isFree: String = bean.is_free
//        if (isFree == "1" || isFree == "true") {
//            tv.text = tv.context.resources.getString(R.string.course_str_free)
//            tv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
//        } else {
//            tv.text = tv.context.resources.getString(R.string.course_str_pay)
//            tv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
//        }
//        if (!TextUtils.isEmpty(bean.is_free_info)) {
//            tv.text = bean.is_free_info
//        }
//    }


//    @JvmStatic
//    @BindingAdapter(value = ["typePriceRecommend"])
//    fun setSpecialTypePrice(tv: TextView, bean: RecommendContentBean.DataBean) {
//        val isFree: String = bean.is_free
//        if (isFree == "1" || isFree == "true") {
//            tv.text = tv.context.resources.getString(R.string.course_str_free)
//            tv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
//        } else {
//            tv.text = tv.context.resources.getString(R.string.course_str_pay)
//            tv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
//        }
//        if (!TextUtils.isEmpty(bean.is_free_info)) {
//            tv.text = bean.is_free_info
//        }
//    }


    @JvmStatic
    @BindingAdapter(value = ["typeString"])
    fun setTypeText(tv: TextView, type: Int) {
        tv.setText(ResourceTypeConstans.Companion.typeStringMap.get(type))
    }


}