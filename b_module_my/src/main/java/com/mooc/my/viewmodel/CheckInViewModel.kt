package com.mooc.my.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.my.model.CheckInDataBean
import com.mooc.my.repository.MyModelRepository
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.my.model.ComeonOtherResponse

class CheckInViewModel : BaseViewModel() {
    private val repository = MyModelRepository()

    //获取签到数据
    val mCheckInData : MutableLiveData<CheckInDataBean> by lazy {
        MutableLiveData<CheckInDataBean>()
    }

//    //请求签到数据
//    val mPostCheckInData : MutableLiveData<CheckInDataBean> by lazy {
//        MutableLiveData<CheckInDataBean>()
//    }


    fun getCheckInData(errorCallback:(()->Unit)? = null){
        launchUI ({
            mCheckInData.postValue(repository.getCheckInData())
        },{
            errorCallback?.invoke()
        })
    }

//    fun postCheckIn(){
//        launchUI {
//            mPostCheckInData.postValue(repository.postCheckInData())
//            //刷新签到数据
//            getCheckInData()
//        }
//    }

    fun postCheckInNew() : LiveData<CheckInDataBean?>{
        val response = MutableLiveData<CheckInDataBean>()
        launchUI ({
            val postCheckInData = repository.postCheckInData()
            response.postValue(postCheckInData)
        },{
            response.postValue(null)
        })
        return response
    }

    /**
     * 获取签到成员
     */
    fun getSignMembers() : LiveData<ComeonOtherResponse>{
        val liveData = MutableLiveData<ComeonOtherResponse>()
        launchUI {
            liveData.postValue(repository.getCheckInMember())
        }
        return liveData
    }


}