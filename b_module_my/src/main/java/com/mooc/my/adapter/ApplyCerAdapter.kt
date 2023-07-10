package com.mooc.my.adapter

import androidx.annotation.Nullable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R
import com.mooc.my.model.CertificateBean


class ApplyCerAdapter(@Nullable data: ArrayList<CertificateBean.ResultsBean>?,layoutId: Int = R.layout.my_item_apply_cer) : BaseQuickAdapter<CertificateBean.ResultsBean, BaseViewHolder>(layoutId, data) {
    override fun convert(holder: BaseViewHolder, item: CertificateBean.ResultsBean) {
        holder.setText(R.id.title, item.title)
        holder.setText(R.id.cer_assessment, item.study_evaluate)

        //-1没有生成表示可以下载 0正在生成不能下载 1已生成
        if ("0".equals(item.apply_status)) {
            holder.setText(R.id.tv_apply_cer, R.string.product_certification)
//            helper.setOnClickListener(R.id.tv_apply_cer, null)
        } else {
            holder.setText(R.id.tv_apply_cer, R.string.apply_cer2)
        }
        if ("3".equals(item.type)) {
            holder.setGone(R.id.tip,true)
        }else{
            holder.setGone(R.id.tip,false)
        }
    }
}