package com.mooc.studyroom.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.studyroom.HonorDataBean
import com.mooc.studyroom.R

/**
 *证书适配器
 * @Author limeng
 * @Date 2020/9/28-10:06 AM
 */
class CertificateAdapter(data: ArrayList<HonorDataBean>?, layoutId: Int = R.layout.studyroom_item_honor_certificate)
    : BaseQuickAdapter<HonorDataBean, BaseViewHolder>(layoutId, data) {
    override fun convert(holder: BaseViewHolder, item: HonorDataBean) {
        // apply_status  -1没有生成表示可以下载 0正在生成不能下载 1已生成
        //我的证书
        if (!item.link.isNullOrEmpty()) {
            holder.setText(R.id.tv_course_title, item.title)
            if ("3".equals(item.type)) {
                holder.setVisible(R.id.tv_honor_status,false)
            }else{
                holder.setVisible(R.id.tv_honor_status,true)
                holder.setText(R.id.tv_honor_status, "成绩评定：" + item.study_evaluate)
            }
            if (item.apply_status.equals("-1")) {
                holder.setText(R.id.online_certificate, R.string.download_online_certification)

            } else if (item.apply_status.equals("0")) {
                holder.setText(R.id.online_certificate, R.string.product_certification)
            } else if (item.apply_status.equals("1")) {
                holder.setText(R.id.online_certificate, R.string.product_certification_but_no_down)
            }
            return
        } else {
            holder.setText(R.id.tv_course_title, item.display_name)
            holder.setText(R.id.tv_honor_status, R.string.already_certification)

            holder.setText(R.id.online_certificate, R.string.download_online_certification)
            if (!item.download_url.isNullOrEmpty()) {
                holder.setVisible(R.id.online_certificate, true)
            } else {
                holder.setVisible(R.id.online_certificate, false)
            }
        }


    }
}