package com.mooc.commonbusiness.base

import androidx.lifecycle.MutableLiveData

/**
 * @param M 数据模型
 * @param R 仓库
 */
abstract class BaseVmViewModel<M:Any>(var id:String,var type:Int) : BaseResourceViewModel(id,type) {


    val mRepository : BaseRepository by lazy {
        getRepository()
    }

    abstract fun getRepository() : BaseRepository

    val mLiveData = MutableLiveData<M>()

    fun initData(){
        launchUI {
            val request = mRepository.request(block())
            mLiveData.postValue(request)
        }
    }

    abstract fun block(): suspend () -> M
}