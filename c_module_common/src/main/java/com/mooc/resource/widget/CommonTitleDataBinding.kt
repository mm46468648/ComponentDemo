package com.mooc.resource.widget

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.put

object CommonTitleDataBinding {
    const val PARAMS_RESOURCE_ID = "params_resource_id"
    const val PARAMS_RESOURCE_TYPE = "params_resource_type"
    const val PARAMS_RESOURCE_TITLE = "params_resource_title"

    /**
     * 举报信息
     */
    @JvmStatic
    @BindingAdapter("report_resource_id","report_resource_type","report_resource_title")
    fun reportInfo(view: CommonTitleLayout?, resource_id: String?, resource_type: Int?, resource_title: String?) {
        if(TextUtils.isEmpty(resource_id)) return
        val put = Bundle().put(PARAMS_RESOURCE_ID, resource_id)
                    .put(PARAMS_RESOURCE_TYPE, resource_type)
                    .put(PARAMS_RESOURCE_TITLE, resource_title)
//        println("资源id$resource_id")
        view?.setOnRightTextClickListener(View.OnClickListener {
            if ((it as TextView).text == "举报") {
//                ARouter.getInstance().build(Paths.PAGE_REPORT_DIALOG).with(put).navigation()
                ARouter.getInstance().build("/commonBusiness/ReportDialogActivity").with(put).navigation()
            }
        })
    }
}