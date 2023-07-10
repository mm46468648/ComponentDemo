package com.mooc.studyproject.adapter

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.studyroom.R
import com.mooc.studyroom.model.IntegralListBean

import org.jetbrains.annotations.NotNull
import java.io.UnsupportedEncodingException
import java.lang.String
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.charset
import kotlin.text.format
import kotlin.text.toByteArray

class IntegralExchangeAdapter(data: ArrayList<Any>?):
    BaseDelegateMultiAdapter<Any, BaseViewHolder>(data), LoadMoreModule {

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<Any>() {
            override fun getItemType(@NotNull data: List<Any>, position: Int): Int {

                return ResourceTypeConstans.TYPE_INTEGRAL
            }
        })

        getMultiTypeDelegate()
            ?.addItemType(ResourceTypeConstans.TYPE_INTEGRAL, R.layout.item_integral_exchange)
    }

    private fun timeToString(time: Long): kotlin.String? {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return dateFormat.format(Date(time))
    }

    private fun setTextSpan(isStatus: Int, textView: TextView, data: IntegralListBean) {
        val spannableString: SpannableString
        val score = String.format(
            context.getResources().getString(R.string.integral_exchange_score),
            data.prize_score
        )
        spannableString = SpannableString(score)
        if (isStatus == 1) {
            spannableString.setSpan(
                ForegroundColorSpan(
                    context.getResources().getColor(R.color.white)
                ), 0, score.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            spannableString.setSpan(
                ForegroundColorSpan(
                    context.getResources().getColor(R.color.color_9)
                ), 0, score.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        spannableString.setSpan(
            AbsoluteSizeSpan(sp2px(30f)),
            0,
            score.length - 3,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(sp2px(16f)),
            score.length - 3,
            score.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableString
    }

    private fun sp2px(spValue: Float): Int {
        val fontScale: Float = context.getResources().getDisplayMetrics().scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    override fun convert(holder: BaseViewHolder, item: Any) {
        var  data=item as IntegralListBean;

        var title = ""
        if (!TextUtils.isEmpty(data.getPrize_title())) {
            title = data.getPrize_title()
            try {
                val srtByte = title.toByteArray(charset("GBK"))
                if (srtByte.size > 20) {
                    val str = ByteArray(20)
                    System.arraycopy(srtByte, 0, str, 0, 20)
                    title = kotlin.String.format(
                        context.getResources().getString(R.string.text_str_more),
                        String(str, Charset.forName("GBK"))
                    )
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }

        holder.getView<TextView>(R.id.tv_exchange_title).text=title

        if (data.is_expire === 1) {
            holder.getView<LinearLayout>(R.id.ll_integral).setBackgroundResource(R.drawable.bg_integral_blue)
            holder.getView<TextView>(R.id.tv_exchange_status).setVisibility(View.VISIBLE)
            holder.getView<TextView>(R.id.tv_exchange_title).setTextColor(context.getResources().getColor(R.color.white))
            holder.getView<TextView>(R.id.tv_exchange_time).setTextColor(context.getResources().getColor(R.color.white))
        } else {
            holder.getView<LinearLayout>(R.id.ll_integral).setBackgroundResource(R.drawable.bg_integral_gray)
            holder.getView<TextView>(R.id.tv_exchange_status).setVisibility(View.GONE)
            holder.getView<TextView>(R.id.tv_exchange_title).setTextColor(context.getResources().getColor(R.color.color_9))
            holder.getView<TextView>(R.id.tv_exchange_time).setTextColor(context.getResources().getColor(R.color.color_9))
        }
        setTextSpan(data.is_expire, holder.getView(R.id.tv_exchange_score), data)
        holder.getView<TextView>(R.id.tv_exchange_time).setText(
            kotlin.String.format(
                context.getResources().getString(R.string.integral_exchange_time),
                timeToString(data.prize_start_time),
                timeToString(data.prize_end_time)
            )
        )
    }

}