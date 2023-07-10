package com.mooc.discover.viewmodel

import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.discover.model.TaskDetailsBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MutualTaskChooseViewModle : BaseListViewModel2<TaskDetailsBean>() {

    var taskList : ArrayList<TaskDetailsBean>? = null

    override suspend fun getData(): Flow<List<TaskDetailsBean>> {
        return flow {
            emit(taskList?:listOf<TaskDetailsBean>())
        }
    }
}