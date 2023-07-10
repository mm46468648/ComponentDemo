package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.net.ApiService
import com.mooc.common.ktextends.loge
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.StudyPlanAddBean
import com.mooc.studyroom.model.StudyProject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

class StudyProjectViewModel : BaseListViewModel<StudyProject>() {
    var outPlanBean = MutableLiveData<StudyPlanAddBean>()
    override suspend fun getData(): Deferred<List<StudyProject>?> {
        val async = viewModelScope.async {
            try {
                val recommendBaseResponse = ApiService.getRetrofit().create(StudyRoomApi::class.java)
                        .getUserStudyPlanData().await()
                recommendBaseResponse.results
            } catch (e: Exception) {
                loge(e.toString())
                return@async arrayListOf<StudyProject>()
            }

        }
        return async

    }

    /**
     * 退出学习计划
     */
    fun postOutStudyPlan(id: String) {
        val requestData = JSONObject()
        try {
            requestData.put("study_plan", id)
            requestData.put("user", GlobalsUserManager.uid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val toRequestBody = requestData.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        launchUI {
            val result = ApiService.getRetrofit().create(StudyRoomApi::class.java).postOutStudyPlan(toRequestBody)?.await()
            outPlanBean.postValue(result)
        }


    }

}