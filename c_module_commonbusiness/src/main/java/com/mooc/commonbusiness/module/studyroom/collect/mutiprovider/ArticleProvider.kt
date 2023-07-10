package com.mooc.commonbusiness.module.studyroom.collect.mutiprovider

import android.text.Html
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.ArticleBean

class ArticleProvider(
    override val itemViewType: Int = ResourceTypeConstans.TYPE_ARTICLE,
    override val layoutId: Int = R.layout.home_item_studyroom_collect_article
) : BaseItemProvider<Any>() {


    override fun convert(helper: BaseViewHolder, item: Any) {
        if (item is ArticleBean) {

            if (!TextUtils.isEmpty(item.picture)) {
                helper.setGone(R.id.con_img, false)
                helper.setGone(R.id.ll_no_img, true)

                Glide.with(context)
                    .load(item.picture)
                    .error(R.mipmap.common_bg_cover_vertical_default)
                    .placeholder(R.mipmap.common_bg_cover_vertical_default)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                    .into(helper.getView(R.id.iv_cover) as ImageView)

                helper.setText(R.id.tv_article_title, Html.fromHtml(item.title))

                if (item.task_finished == false) {
                    helper.setTextColorRes(R.id.tv_article_title, R.color.color_2)
                    helper.setVisible(R.id.tv_platform, false)
                } else {
                    helper.setTextColorRes(R.id.tv_article_title, R.color.color_A)
                    helper.setVisible(R.id.tv_platform, true)
                    helper.setTextColorRes(R.id.tv_platform, R.color.colorPrimary)
                    helper.setText(R.id.tv_platform, "已阅读")
                }

            } else {
                helper.setGone(R.id.con_img, true)
                helper.setGone(R.id.ll_no_img, false)
                helper.setText(R.id.tv_article_title_no_image, Html.fromHtml(item.title))
                helper.setText(R.id.tv_platform_no_image, item.source)
            }
        }
    }

}