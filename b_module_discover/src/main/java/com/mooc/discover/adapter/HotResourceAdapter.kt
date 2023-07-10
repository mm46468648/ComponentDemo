package com.mooc.discover.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.R
import com.mooc.discover.model.RecommendContentBean
import com.mooc.discover.binding.Bindings
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.discover.model.HotCourseDetail
import org.jetbrains.annotations.NotNull

/**
适用于推荐的  课程和文章列表 文章分为有图和没图
 * @Author limeng
 * @Date 2020/11/12-6:17 PM
 */
class HotResourceAdapter(data: ArrayList<Any>?) :
    BaseDelegateMultiAdapter<Any, BaseViewHolder>(data), LoadMoreModule {

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<Any>() {
            override fun getItemType(@NotNull data: List<Any>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                //如果类型，不属于任何样式，返回默认样式
                val any = data[position]
                if (any is HotCourseDetail) return ResourceTypeConstans.TYPE_COURSE
                if (any is RecommendContentBean.DataBean) return ResourceTypeConstans.TYPE_ARTICLE
                return ResourceTypeConstans.TYPE_UNDEFINE
            }
        })
        // //两个类型，课程和文章 （有图没有图的）
        getMultiTypeDelegate()
            ?.addItemType(ResourceTypeConstans.TYPE_COURSE, R.layout.item_binding_course)
            ?.addItemType(ResourceTypeConstans.TYPE_ARTICLE, R.layout.item_binding_article)
            ?.addItemType(ResourceTypeConstans.TYPE_UNDEFINE, R.layout.common_item_empty)
    }


    override fun convert(holder: BaseViewHolder, item: Any) {
        when {
            holder.itemViewType == ResourceTypeConstans.TYPE_COURSE && item is HotCourseDetail -> {
                setCourse(holder, item)
            }
            holder.itemViewType == ResourceTypeConstans.TYPE_ARTICLE && item is ArticleBean -> {
                setArticleItem(holder, item)
            }
        }
    }

    //拷贝来自 RecommendSpecialAdapter
    @SuppressLint("SetTextI18n")
    fun setCourse(helper: BaseViewHolder, bean: HotCourseDetail) {

        helper.setText(R.id.title, bean.title)
        helper.setText(
            R.id.type_name,
            ResourceTypeConstans.typeStringMap.get(ResourceTypeConstans.TYPE_COURSE)
        )

        helper.setText(R.id.org, bean.org)

        helper.setText(R.id.platform, bean.platform_zh)


        helper.setGone(R.id.platform, TextUtils.isEmpty(bean.platform_zh))


        val isHaveExam = bean.is_have_exam
        val tv = helper.getView<TextView>(R.id.tvExamType)

        if (isHaveExam == 1) {
            tv.text = tv.context.resources.getString(R.string.course_str_have_exam) + Bindings.dort
            tv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
        } else {
            tv.text = tv.context.resources.getString(R.string.course_str_no_exam) + Bindings.dort
            tv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
        }
        if (!TextUtils.isEmpty(bean.is_have_exam_info)) {
            tv.text = bean.is_have_exam_info + Bindings.dort
        }

        val vertificateTv = helper.getView<TextView>(R.id.tvCertificateType)

        val isVerified = bean.verified_active
        if (isVerified == 1) {
            vertificateTv.text =
                tv.context.resources.getString(R.string.course_str_have_certificate) + Bindings.dort
            vertificateTv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
        } else {
            vertificateTv.text =
                tv.context.resources.getString(R.string.course_str_no_certificate) + Bindings.dort
            vertificateTv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
        }
        if (!TextUtils.isEmpty(bean.verified_active_info)) {
            vertificateTv.text = bean.verified_active_info + Bindings.dort
        }

        val isFreeTv = helper.getView<TextView>(R.id.tvPriceType)

        val isFree = bean.is_free
        if (isFree == 1) {
            isFreeTv.text = tv.context.resources.getString(R.string.course_str_free)
            isFreeTv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
        } else {
            isFreeTv.text = tv.context.resources.getString(R.string.course_str_pay)
            isFreeTv.setTextColor(tv.context.resources.getColor(R.color.color_636363))
        }
        if (!TextUtils.isEmpty(bean.is_free_info)) {
            isFreeTv.setText(bean.is_free_info)
        }
        Glide.with(context).load(bean.picture).placeholder(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into(helper.getView(R.id.cover))
    }

    private fun setArticleItem(helper: BaseViewHolder, bean: ArticleBean) {
        helper.setText(R.id.title, bean.title)
        helper.setText(
            R.id.type_name,
            ResourceTypeConstans.typeStringMap.get(ResourceTypeConstans.TYPE_ARTICLE)
        )



        if (TextUtils.isEmpty(bean.platform_zh)) {
            helper.setText(R.id.org_platform, bean.source);
        } else {
            if (TextUtils.isEmpty(bean.source)) {
                helper.setText(R.id.org_platform, bean.platform_zh);
            } else {
                helper.setText(R.id.org_platform, bean.platform_zh + " | " + bean.source);
            }
        }


        helper.setText(R.id.time, bean.publish_time)


        Glide.with(context).load(bean.picture).placeholder(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into(helper.getView(R.id.cover));
    }

}