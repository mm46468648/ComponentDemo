package com.mooc.commonbusiness.module.studyroom.collect.mutiprovider

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.MicroBean
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import java.text.DecimalFormat

class MicroProvider(
    override val itemViewType: Int = ResourceTypeConstans.TYPE_MICRO_LESSON,
    override val layoutId: Int = R.layout.home_item_studyroom_collect_micro
) : BaseItemProvider<Any>() {

    override fun convert(helper: BaseViewHolder, item: Any) {
        if (item is MicroBean) {
            helper.setText(R.id.tvTitle, item.title)
            val platFormStr =
                if (item.platform_zh.isEmpty()) context.resources.getString(R.string.default_platform)
                else item.platform_zh
            helper.setText(R.id.tvSource, platFormStr)
            helper.setText(
                R.id.tvTime,
                "时长: ${TimeFormatUtil.formatAudioPlayTime(item.video_duration * 1000)}"
            )

            if (item.learned_process.isEmpty()) {//增加自建微课的进度
                helper.setGone(R.id.tvGet, true)
                helper.setText(R.id.tvGet, "")
            } else {
                helper.setGone(R.id.tvGet, false)
                setSpan(helper.getView(R.id.tvGet), getProcess(item.learned_process))
            }
            Glide.with(context)
                .load(item.picture)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(helper.getView(R.id.ivCover))

        }
    }

    /**
     * 获取已学百分比字段
     */
    private fun getProcess(learned_process: String): String {
        var process = 0.0
        try {
            process = learned_process.toDouble()
        } catch (e: Exception) {

        }

        return if (process == 0.0) {
            "已学: 0%"
        } else {
            "已学: ${getNoMoreThanTwoDigits(process)}"
        }
    }

    fun getNoMoreThanTwoDigits(number: Double): String {
        return DecimalFormat("#0.00").format(number) + "%"
    }

    @Suppress("DEPRECATION")
    private fun setSpan(textView: TextView, str: String) {
        val spannableString = SpannableString(str)
        spannableString.setSpan(
            ForegroundColorSpan(
                context.resources.getColor(R.color.color_5D5D5D)
            ), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //换肤
        val skinColor = SkinManager.getInstance().resourceManager.getColor("colorPrimary")
        spannableString.setSpan(
            ForegroundColorSpan(
                skinColor
            ), 3, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableString
    }
}