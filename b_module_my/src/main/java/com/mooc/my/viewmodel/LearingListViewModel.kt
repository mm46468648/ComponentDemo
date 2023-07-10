package com.mooc.my.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.my.model.LearingListBean
import com.mooc.my.repository.MyModelRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async


class LearingListViewModel() : BaseListViewModel<LearingListBean>() {

    private val repository = MyModelRepository()
    var userId: String = ""

    override suspend fun getData(): Deferred<ArrayList<LearingListBean>?> {
        val asyn = viewModelScope.async {
            repository.getLearningList(userId, limit, offset)?.results
        }
        return asyn
    }
}
