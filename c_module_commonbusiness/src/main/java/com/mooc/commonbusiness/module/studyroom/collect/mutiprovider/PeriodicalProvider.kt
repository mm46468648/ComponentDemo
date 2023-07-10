package com.mooc.commonbusiness.module.studyroom.collect.mutiprovider

import android.text.Html
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.PeriodicalBean

class PeriodicalProvider : BaseItemProvider<Any>() {
    override val itemViewType: Int
        get() = ResourceTypeConstans.TYPE_PERIODICAL
    override val layoutId: Int
        get() = R.layout.home_item_studyroom_collect_periodical

    override fun convert(helper: BaseViewHolder, item: Any) {
        if (item is PeriodicalBean) {
            Glide.with(context)
                .load(item.basic_cover_url)
                .error(R.mipmap.common_bg_cover_vertical_default)
                .placeholder(R.mipmap.common_bg_cover_vertical_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(helper.getView(R.id.iv_periodical))

            helper.setText(R.id.tv_periodical_title, Html.fromHtml(item.title))

            if (TextUtils.isEmpty(item.basic_creator)) {
                helper.setVisible(R.id.tv_editor, false)
            } else {
                helper.setVisible(R.id.tv_editor, true)
            }

            helper.setText(R.id.tv_editor, item.basic_creator)


            if (TextUtils.isEmpty(item.basic_date_time)) {
                helper.setVisible(R.id.tv_time, false)
            } else {
                helper.setVisible(R.id.tv_time, true)
            }

            helper.setText(
                R.id.tv_time,
                String.format("%s年出版", Html.fromHtml(item.basic_date_time))
            )

        }
    }
}