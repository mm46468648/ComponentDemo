package com.mooc.setting.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.setting.SetApi
import com.mooc.setting.model.SettingCourseMsgBean
import com.mooc.setting.model.SettingMsgBean
import kotlinx.coroutines.async
import okhttp3.RequestBody
import java.util.*

/**

 * @Author limeng
 * @Date 2021/2/3-2:38 PM
 */
class MsgViewModel : BaseViewModel() {
    var dataList = ArrayList<SettingMsgBean>()
    var resultList = MutableLiveData<ArrayList<SettingMsgBean>>()
    var noticeResult = MutableLiveData<HttpResponse<Any>>()
    var courseResult = MutableLiveData<SettingCourseMsgBean>()
    fun getSettingData() {
        val courseBean = viewModelScope.async {
            try {
                ApiService.getRetrofit().create(SetApi::class.java).getSettingCourseMsg().await()
            }catch (e:Exception){
                loge(e.toString())
                arrayListOf<SettingCourseMsgBean>()
            }

        }
        val interactionMsg = viewModelScope.async {
            try {
                ApiService.getRetrofit().create(SetApi::class.java).getSettingInteractionMsg().await()
            }catch (e:Exception){
                null
            }
        }
        launchUI {
            dataList.clear()
            val bean = interactionMsg.await()
            bean?.let {
                val msgBean = SettingMsgBean(bean.msg, null,bean.allow_interaction, "1", null,1)// itemType 区分是提示条还是开关的item 1 开关 2 是提示，statusitem 是关于开关属于哪一类，是2课程还是1公告的
                val lineBean = SettingMsgBean(null, null,null, null,null, 2)
                dataList.add(msgBean)
                dataList.add(lineBean)
            }

            courseBean.await().forEach {
                val setBean =  SettingMsgBean(it.course_title,it.course_id, null, "2", it.notice_status,1)
                dataList.add(setBean)
            }
            resultList.postValue(dataList)
        }


    }

    //上传公告开关
    fun postSettingInteractionMsg(body: RequestBody) {
        launchUI {
            val result = ApiService.getRetrofit().create(SetApi::class.java).postSettingInteractionMsg(body).await()
            noticeResult.postValue(result)
        }
    }
    //上传课程开关
    fun postSettingCourseMsg(body: RequestBody) {
        launchUI {
            val result = ApiService.getRetrofit().create(SetApi::class.java).postSettingCourseMsg(body)?.await()
            courseResult.postValue(result)
        }
    }
}