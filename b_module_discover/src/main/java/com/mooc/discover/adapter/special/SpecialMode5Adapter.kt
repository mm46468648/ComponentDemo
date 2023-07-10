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
class SpecialMode5Adapter(data: ArrayList<RecommendContentBean.DataBean>?) :
    BaseQuickAdapter<RecommendContentBean.DataBean, BaseViewHolder>(R.layout.item_special_mode_five,data), LoadMoreModule {



    override fun convert(holder: BaseViewHolder, item: RecommendContentBean.DataBean) {

                setFiveData(holder, item)

    }

    private fun setFiveData(holder: BaseViewHolder, dataBean: RecommendContentBean.DataBean) {
        val image: String =
            if (TextUtils.isEmpty(dataBean.getBig_image())) dataBean.getSmall_image() else dataBean.getBig_image()
        Glide.with(context)
            .load(image)
            .error(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .placeholder(R.mipmap.common_bg_cover_default)
            .into(holder.getView(R.id.ivCover))


        if (dataBean.getType() == ResourceTypeConstans.TYPE_ALBUM || dataBean.getType() == ResourceTypeConstans.TYPE_TRACK) {
            holder.setGone(R.id.ivPlay, false)
        } else {
            holder.setGone(R.id.ivPlay, true)

        }
        holder.setText(R.id.tvTitle, dataBean.title)

        val value = typeStringMap[dataBean.type]
        holder.setText(R.id.tvTypeFive, value)
        holder.setGone(R.id.tvTypeFive, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeListFive, value)
        holder.setGone(R.id.tvTypeListFive, true)

        when (dataBean.type) {
            ResourceTypeConstans.TYPE_COURSE -> {
                if (!TextUtils.isEmpty(dataBean.identity_name)) {
                    holder.setText(R.id.tvTypeListFive, dataBean.identity_name)
                    holder.setGone(R.id.tvTypeListFive, false)
                    holder.setGone(R.id.tvTypeFive, true)
                } else {
                    holder.setGone(R.id.tvTypeListFive, true)
                    holder.setGone(R.id.tvTypeFive, false)
                }
            }
            ResourceTypeConstans.TYPE_PUBLICATION -> {
            }
            ResourceTypeConstans.TYPE_TASK -> {
            }
            ResourceTypeConstans.TYPE_KNOWLEDGE, ResourceTypeConstans.TYPE_MICRO_LESSON -> {
            }
            ResourceTypeConstans.TYPE_PERIODICAL -> {
            }
            ResourceTypeConstans.TYPE_ALBUM -> {
                holder.setGone(R.id.ivPlay, false)
            }
            ResourceTypeConstans.TYPE_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
            }
            ResourceTypeConstans.TYPE_MASTER_TALK -> {
            }
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE, ResourceTypeConstans.TYPE_ARTICLE -> {
            }
            ResourceTypeConstans.TYPE_ACTIVITY -> {
            }
            ResourceTypeConstans.TYPE_ACTIVITY_TASK -> {
            }
            ResourceTypeConstans.TYPE_E_BOOK -> {
            }
            ResourceTypeConstans.TYPE_STUDY_PLAN -> {
            }
            ResourceTypeConstans.TYPE_COLUMN -> {
            }
            ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK -> {
//                holder.setGone(R.id.tvTypeFive, true)
            }
            ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                holder.setGone(R.id.tvTypeFive, true)
            }
            ResourceTypeConstans.TYPE_SPECIAL -> {
                if (!TextUtils.isEmpty(dataBean.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setText(R.id.tvTypeFive, dataBean.identity_name)
                }
            }

            ResourceTypeConstans.TYPE_BAIKE -> {
            }

            ResourceTypeConstans.TYPE_TEST_VOLUME -> {
            }
            ResourceTypeConstans.TYPE_QUESTIONNAIRE -> {
            }
            ResourceTypeConstans.TYPE_ONESELF_TRACK -> {
                holder.setGone(R.id.ivPlay, false)
            }
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {//微专业

            }
            ResourceTypeConstans.TYPE_BATTLE -> {//对战

            }
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {//微知识

            }
            else -> {
                holder.setGone(R.id.tvTypeFive, true)
                holder.setGone(R.id.ivPlay, true)
            }
        }
    }
}