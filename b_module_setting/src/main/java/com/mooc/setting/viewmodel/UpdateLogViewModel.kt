package com.mooc.setting.viewmodel

import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.net.ApiService
import com.mooc.setting.SetApi
import com.mooc.setting.model.UpdateLogItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateLogViewModel : BaseListViewModel2<UpdateLogItem>() {
    override suspend fun getData(): Flow<List<UpdateLogItem>> {
        return flow {
            val updateLogs = ApiService.retrofit.create(SetApi::class.java).getUpdateLogs(offset)
            emit(updateLogs.data)
        }
    }
}