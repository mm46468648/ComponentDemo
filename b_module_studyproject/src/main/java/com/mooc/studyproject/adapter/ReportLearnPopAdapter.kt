package com.mooc.studyproject.adapter

import android.text.TextUtils
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.ReportBean
import com.mooc.studyproject.R

/**

 * @Author limeng
 * @Date 2020/12/18-2:28 PM
 */
class ReportLearnPopAdapter(data: ArrayList<ReportBean>?):
        BaseQuickAdapter<ReportBean, BaseViewHolder>(R.layout.studyproject_item_report_learn_pop,data) {
     var reportId = -1

    override fun convert(holder: BaseViewHolder, dataBean: ReportBean) {
        if (!TextUtils.isEmpty(dataBean.detail)) {
            holder.setText(R.id.tvDetail, dataBean.detail.trim())
        }
        if (!dataBean.isSelected) {
            holder.getView<CheckBox>(R.id.checkBox).setChecked(false)
            reportId = -1
            holder.getView<CheckBox>(R.id.checkBox).setButtonDrawable(context.getResources().getDrawable(R.mipmap.studyproject_ic_popw_item_normal))
        } else {
            holder.getView<CheckBox>(R.id.checkBox).setChecked(true)

            reportId = dataBean.report_id
            holder.getView<CheckBox>(R.id.checkBox).setButtonDrawable(context.getResources().getDrawable(R.mipmap.studyproject_iv_select_resource))
        }
//        itemViewHolder.parent.setOnClickListener(View.OnClickListener {
//            if (itemViewHolder.checkBox.isChecked()) {
//                itemViewHolder.checkBox.setChecked(false)
//                dataBean.setSelected(false)
//                reportId = -1
//                itemViewHolder.checkBox.setButtonDrawable(mContext.getResources().getDrawable(R.mipmap.ic_popw_item_normal))
//            } else {
//                itemViewHolder.checkBox.setChecked(true)
//                dataBean.setSelected(true)
//                reportId = dataBean.getReport_id()
//                itemViewHolder.checkBox.setButtonDrawable(mContext.getResources().getDrawable(R.mipmap.iv_select_resource))
//            }
//            for (i in currentDataList.indices) {
//                if (i != position) {
//                    currentDataList.get(i).setSelected(false)
//                }
//            }
//            notifyDataSetChanged()
//            if (itemClickListener != null) {
//                itemClickListener.onItemClick(reportId, position)
//            }
//        })
    }
}