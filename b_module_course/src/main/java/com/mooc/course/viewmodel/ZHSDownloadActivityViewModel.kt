package com.mooc.course.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel

/**
 * 智慧树下载Activity ViewModel
 */
class ZHSDownloadActivityViewModel(var courseId:String) : BaseViewModel() {

    //编辑模式
    val editMode by lazy {
        val mutableLiveData = MutableLiveData<Boolean>()
        mutableLiveData.value =false
        mutableLiveData
    }

    //全选
    val isAllSelect by lazy {
        val mutableLiveData = MutableLiveData<Boolean>()
        mutableLiveData.value =false
        mutableLiveData
    }

    //全部开始或者暂停,false 全部暂停，true全部开始
    val allStartDownload by lazy {
        val mutableLiveData = MutableLiveData<Boolean>()
        mutableLiveData.value =false
        mutableLiveData
    }


    /**
     * fragment控制显示全部开始或暂停
     */
    val downloadStatsFromFragment by lazy {
        val mutableLiveData = MutableLiveData<Boolean>()
        mutableLiveData.value =false
        mutableLiveData
    }



    //删除选中下载
    val deleteSelectDownload = MutableLiveData<Boolean>().also {
        it.value =false
        it
    }
}