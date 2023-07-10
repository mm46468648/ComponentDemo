package com.mooc.commonbusiness.utils.incpoints

import androidx.lifecycle.LifecycleOwner
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.AddResourcePotinsService

/**
 * 资源增加积分
 * @Author limeng
 * @Date 2021/2/25-2:47 PM
 */
@Route(path = Paths.SERVICE_ADDRESOURCEPOINTS)
class AddResourcePointsImpl : AddResourcePotinsService {
    override fun startAddPointsTask(lifecycleOwner: LifecycleOwner,
                                    title: String?,
                                    type: String?,
                                    url: String?,
                                    onSuccess: (() -> Unit)?) {
//        AddPointManager.startAddPointsTask(lifecycleOwner, title,type,url,onSuccess)
    }

//    override fun canleAddPointsTask(lifecycleOwner: LifecycleOwner) {
//        TODO("Not yet implemented")
//    }


}