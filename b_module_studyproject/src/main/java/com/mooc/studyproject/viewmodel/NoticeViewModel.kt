package com.mooc.studyproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.b_module_studyproject.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyproject.model.Notice
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**

 * @Author limeng
 * @Date 1/11/22-5:49 PM
 */
class NoticeViewModel: BaseListViewModel<Notice>() {
    var planId:String = ""
    override suspend fun getData(): Deferred<List<Notice>?> {
        return  viewModelScope.async {
            val result= HttpService.userApi.getStudyPlanNoticeList(planId).await()
            result.results
        }
    }

}