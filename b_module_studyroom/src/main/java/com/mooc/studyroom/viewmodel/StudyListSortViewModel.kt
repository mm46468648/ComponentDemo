package com.mooc.studyroom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.module.studyroom.BaseStudyListViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * 学习清单排序
 */
class StudyListSortViewModel : BaseStudyListViewModel() {

    /**
     * 发送新的排序
     */
    fun postFolderSort() : LiveData<Boolean> {

        val statusLiveData = MutableLiveData<Boolean>()
        val idList = getListLiveData().value?.map { it.id.toInt() }
        val jsonObject = JSONObject()
        val idJsonArray = JSONArray(idList)
        jsonObject.put("folder_list",idJsonArray)
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        launchUI {
            val response = ApiService.getRetrofit().create(StudyRoomApi::class.java).postFolderNewSort(body).await()
            statusLiveData.postValue(response.isSuccess)
        }

        return statusLiveData

    }
}