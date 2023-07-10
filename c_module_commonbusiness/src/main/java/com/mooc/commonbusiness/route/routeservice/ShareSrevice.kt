package com.mooc.commonbusiness.route.routeservice

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.mooc.commonbusiness.utils.IShare

/**
 * 分享服务
 */
interface ShareSrevice : IProvider{

    override fun init(context: Context?) {}

    fun share(activity: Activity, builder: IShare.Builder, shareCallBack: ((status: Int) -> Unit)? = null)

    fun shareAddScore(shareType:String,activity: Activity, builder: IShare.Builder, shareAddScoreCallBack: ((status: Int) -> Unit)? = null)
}