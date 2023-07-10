package com.mooc.studyproject.adapter

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.studyroom.R
import com.mooc.studyroom.model.IntegralListBean
import com.mooc.studyroom.model.IntegralRecordListBean
import org.jetbrains.annotations.NotNull
import java.io.UnsupportedEncodingException
import java.lang.String
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Any
import kotlin.ByteArray
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.text.format

class IntegralRecordAdapter(data: ArrayList<Any>?):
    BaseDelegateMultiAdapter<Any, BaseViewHolder>(data), LoadMoreModule {

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<Any>() {
            override fun getItemType(@NotNull data: List<Any>, position: Int): Int {

                return ResourceTypeConstans.TYPE_INTEGRAL_RECORD
            }
        })

        getMultiTypeDelegate()
            ?.addItemType(ResourceTypeConstans.TYPE_INTEGRAL_RECORD, R.layout.item_integral_exchange)
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
        var data = item as IntegralRecordListBean;

        val scorePrize: IntegralRecordListBean.ScorePrize = data.score_prize
        var title = ""
        if (!TextUtils.isEmpty(scorePrize.getPrize_title())) {
            title = scorePrize.getPrize_title()
            try {
                val srtByte = title.toByteArray(charset("GBK"))
                if (srtByte.size > 20) {
                    val str = ByteArray(20)
                    System.arraycopy(srtByte, 0, str, 0, 20)
                    title = kotlin.String.format(
                        context.getResources().getString(R.string.text_str_more),
                        kotlin.text.String(str, Charset.forName("GBK"))
                    )
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
       holder.getView<TextView>(R.id.tv_record_title).setText(title)
        if (data.prize_status === 0) {
            holder.getView<TextView>(R.id.tv_record_status).setText(R.string.integral_record_audit_tip)
        } else {
            holder.getView<TextView>(R.id.tv_record_status).setText(R.string.integral_record_exchanged_tip)
        }
        val score: kotlin.String = String.format(
            context.getResources().getString(R.string.integral_exchange_score),
            scorePrize.getPrize_score()
        )
        holder.getView<TextView>(R.id.tv_record_score).setText(score)
        holder.getView<TextView>(R.id.tv_record_time).setText(timeToString(data.created_time))

    }

}