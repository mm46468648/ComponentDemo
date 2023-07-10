package com.mooc.my.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.my.model.CertificateBean
import com.mooc.my.repository.MyModelRepository
import com.mooc.commonbusiness.base.BaseViewModel

/**

 * @Author limeng
 * @Date 2020/9/23-7:58 PM
 */
class HonorViewModel : BaseViewModel() {
    private val repository = MyModelRepository()
    val certificateBeanBean: MutableLiveData<ArrayList<CertificateBean.ResultsBean>> by lazy {
        MutableLiveData<ArrayList<CertificateBean.ResultsBean>>()
    }
//    val honorBean : MutableLiveData<ArrayList<HonorDataBean>> by lazy {
//        val mutableLiveData = MutableLiveData<ArrayList<HonorDataBean>>()
//        mutableLiveData.value = ArrayList<HonorDataBean>()
//        mutableLiveData
//    }

//    fun loadData() {
//        launchUI {
//            val honorResult = async { repository.getHonorData(1000, 0) }
//            val myCertificateResult = async { repository.getMyCertificateData(1000, 0) }
//            honorBean.value?.addAll(honorResult.await().results)
//            honorBean.value?.addAll(myCertificateResult.await().results)
//        }
//    }

    fun loadCertificateData() {
        launchUI {
            val honorResult = repository.getCertificateData(1000,0)
            certificateBeanBean.value = honorResult.results
            certificateBeanBean.postValue(honorResult.results)
        }
    }
}