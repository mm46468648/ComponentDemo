package com.mooc.discover.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.discover.model.MyOrderBean
import com.mooc.discover.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.constants.LoadStateConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MyOrderViewModel : BaseListViewModel2<MyOrderBean.ResultsBean>() {

    override suspend fun getData(): Flow<List<MyOrderBean.ResultsBean>> {
        return flow{
            if(offset == getOffSetStart() && !GlobalsUserManager.isLogin()){ //未登录直接设置空数组
                emit(arrayListOf<MyOrderBean.ResultsBean>())
            }else{
                emit(HttpService.discoverApi.getMyOrder(limit, offset).results)
            }
        }
    }


}