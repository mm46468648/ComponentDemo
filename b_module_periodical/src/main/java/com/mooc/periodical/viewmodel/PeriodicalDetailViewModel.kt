package com.mooc.periodical.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.periodical.PeriodicalApi
import com.mooc.periodical.model.PeriodicalDetail

class PeriodicalDetailViewModel(var id:String) : BaseViewModel() {

    val detailLiveData : MutableLiveData<PeriodicalDetail> by lazy {
        MutableLiveData<PeriodicalDetail>().also {
            loadDetail()
        }
    }

//    val periodicalListData =  MutableLiveData<List<PeriodicalBean>>()

    /**
     * 当前选中的期
     * 2021 A
     */
    val currentSelectTerm = MutableLiveData<Pair<Int,String>>()

    fun loadDetail(){
        launchUI {
            val periodicalDetail =
                ApiService.getRetrofit().create(PeriodicalApi::class.java).getPeriodicalDetail(id)

            val data = periodicalDetail.await().data

            //查询最新一期的列表
            if(data.terms?.isNotEmpty() == true){
                data?.terms?.get(0)?.let {
                    if(it.value.isNotEmpty()){
//                        loadList(it.year,it.value.get(0))
                        currentSelectTerm.value = Pair(it.year,it.value.get(0))
                    }
                }

            }
            detailLiveData.postValue(data)


        }
    }
}