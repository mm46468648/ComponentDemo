package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi

class StudyRecordActivityViewModel : BaseViewModel() {

    val onDeleteSuccess = MutableLiveData<Boolean>()

    val selectStudyProject = MutableLiveData<Boolean>()
    /**
     * 删除学习记录
     */
    fun deleteStudyRecord() {
        launchUI {
            val await = ApiService.getRetrofit().create(StudyRoomApi::class.java).deleteStudyRecord().await()
            onDeleteSuccess.postValue(await.isSuccess)
        }
    }
}