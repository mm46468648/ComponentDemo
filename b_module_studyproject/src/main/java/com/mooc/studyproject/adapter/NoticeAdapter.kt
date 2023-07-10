package com.mooc.studyproject.adapter

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.model.Notice
import com.mooc.studyproject.R


class NoticeAdapter( data: ArrayList<Notice>?) : BaseMultiItemQuickAdapter<Notice, BaseViewHolder>(data) {
    var planId:String?=null
    init {
        addItemType(1, R.layout.studyproject_item_notice_header)
        addItemType(2, R.layout.studyproject_item_notice)
        addItemType(3, R.layout.studyproject_item_notice_more)
    }
    override fun convert(helper: BaseViewHolder, item: Notice) {
        when (helper.getItemViewType()) {
            1 ->
            helper.itemView.setOnClickListener {
                if (TextUtils.isEmpty(planId)) {
                    return@setOnClickListener
                }
                ARouter.getInstance().build(Paths.PAGE_NOTICE_LIST).withString("id",planId).navigation()
            }
            2 -> {
                helper.setText(R.id.title, item.title)
                helper.setText(R.id.time, item.created_time)
                helper.itemView.setOnClickListener{
                    if (TextUtils.isEmpty(item.id.toString() + "")) {
                        return@setOnClickListener
                    }
                    ARouter.getInstance().build(Paths.PAGE_NOTICE_INFO).withString("id",item.id).navigation()
                }
            }
            3 -> {
                helper.setText(R.id.title, item.title)
                helper.setText(R.id.time, item.created_time)
                helper.itemView.setOnClickListener {
                    if (TextUtils.isEmpty(item.id.toString() + "")) {
                        return@setOnClickListener
                    }
                    ARouter.getInstance().build(Paths.PAGE_NOTICE_INFO).withString("id",item.id).navigation()

                }
            }
        }
    }


}