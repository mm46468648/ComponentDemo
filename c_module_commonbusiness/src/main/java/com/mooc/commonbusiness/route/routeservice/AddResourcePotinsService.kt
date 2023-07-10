package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.alibaba.android.arouter.facade.template.IProvider

/**

 * @Author limeng
 * @Date 2021/2/25-5:18 PM
 */
interface AddResourcePotinsService : IProvider {
    override fun init(context: Context?) {
    }
    fun startAddPointsTask(lifecycleOwner: LifecycleOwner,
                           title: String?,
                           type: String?,
                           url: String?,
                           onSuccess: (() -> Unit)?)
//    fun canleAddPointsTask(lifecycleOwner: LifecycleOwner)
}