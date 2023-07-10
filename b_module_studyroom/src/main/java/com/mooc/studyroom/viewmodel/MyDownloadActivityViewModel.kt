package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel

class MyDownloadActivityViewModel : BaseViewModel() {

    val editMode : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also {
            it.value = false
        }
    }


}