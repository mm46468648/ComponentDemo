package com.mooc.course.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel

class NewXtCourseDownloadActivityViewModel : BaseViewModel() {

    //点击全部开始 true全部开始，false全部暂停
//    val allDownloadStart = MutableLiveData<Boolean>()

    //fragment通知Activty改变全部开始状态 是否全部开始 true是，false暂停
    val changeAllTvShowState =  MutableLiveData<Boolean>()


    //Activity通知fragment，全部开始或者暂停,false 全部暂停，true全部开始
    val allStartDownload = MutableLiveData<Boolean>().also{
        it.value =false
        it
    }
}

