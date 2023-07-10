package com.mooc.studyproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.b_module_studyproject.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyproject.api.UserApi
import com.mooc.studyproject.databinding.StudyprojectItemCommentTopDynamicBinding
import com.mooc.studyproject.model.ItemComment
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class CommendListViewModel : BaseListViewModel<ItemComment>() {
    val studyDynamicDetails = MutableLiveData<StudyDynamic?>();

    var id: String = ""  //
    var headBinding: StudyprojectItemCommentTopDynamicBinding? = null
    override suspend fun getData(): Deferred<List<ItemComment>?> {
        return viewModelScope.async {
             if (offset==0) {
                 getCommentDynamic(id)
             }
            val await = ApiService.getRetrofit().create(UserApi::class.java).getCommentList(id, limit, offset).await()
            await.results
        }
    }


    fun getCommentDynamic(id: String) {
        launchUI {
            val bean = HttpService.userApi.getCommentDynamic(id).await()
            studyDynamicDetails.postValue(bean)
        }
    }
}