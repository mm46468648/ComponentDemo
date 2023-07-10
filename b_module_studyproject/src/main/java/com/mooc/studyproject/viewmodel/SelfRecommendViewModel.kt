package com.mooc.studyproject.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.b_module_studyproject.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import org.json.JSONObject

class SelfRecommendViewModel : BaseViewModel() {
    var activityId: String = ""         //动态id
    var nominationReason: String = ""  //自荐原因

    //    var nominationImage: String = ""  //自荐图片
    var postSelfResult = MutableLiveData<HttpResponse<StudyDynamic>>();

    fun postSelfRecommend() {

        launchUI {
            val jsonObject = JSONObject()
            jsonObject.put("activity_id", activityId)
            jsonObject.put("nomination_reason", nominationReason)
//            jsonObject.put("nomination_image", nominationImage)

            val value = HttpService.userApi.postSelfRecommendAsync(RequestBodyUtil.fromJson(jsonObject)).await()
            postSelfResult.postValue(value)
        }
    }

}