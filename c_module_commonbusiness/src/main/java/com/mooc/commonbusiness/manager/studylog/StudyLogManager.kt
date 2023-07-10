package com.mooc.commonbusiness.manager.studylog

import com.mooc.commonbusiness.api.HttpService
import com.mooc.common.ktextends.loge
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class StudyLogManager {

    companion object{
        fun postStudyLog(body: JSONObject){
            val toRequestBody =  body.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            GlobalScope.launch {
                try {
                    HttpService.commonApi.postStudyLog(toRequestBody)
                }catch (e: Exception){
                    loge(e.toString())
                }
            }
        }
    }

}