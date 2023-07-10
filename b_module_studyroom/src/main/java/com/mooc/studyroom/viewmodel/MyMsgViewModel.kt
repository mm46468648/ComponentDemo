package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
//import com.mooc.commonbusiness.model.studyroom.MyMsgBean
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.resource.widget.SimpleTabLayout
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.MyMsgBean

/**

 * @Author limeng
 * @Date 2021/3/4-4:34 PM
 */
class MyMsgViewModel : BaseViewModel() {
    var myMsgBean = MutableLiveData<ArrayList<MyMsgBean>>()
    var error = MutableLiveData<String?>()

    fun getMyMsgData() {
        launchUI({

            val bean = ApiService.getRetrofit().create(StudyRoomApi::class.java).getMyMsg(10, 0).await()
            myMsgBean.postValue(bean.results)
        }, {
            error.postValue(it.message)
        })

    }
}