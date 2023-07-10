package com.mooc.search.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.search.SearchResultBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.search.api.SearchApi
import com.mooc.common.ktextends.loge
import com.mooc.search.model.SearchPopData
import com.mooc.search.utils.SearchConstants

/**
搜索数据获取
 * @Author limeng
 * @Date 2020/8/21-4:12 PM
 */
class SearchViewModel : BaseViewModel() {
    val searchData = MutableLiveData<SearchResultBean>()

    val fillterTypes = MutableLiveData<List<Int>>()

    fun addFilterType(list: MutableList<SearchPopData>){
        fillterTypes.postValue(list.map {
            it.type
        }.toList())
    }
    fun loadData(map: Map<String, String>) {
        launchUI({
            val searchdata = ApiService.getRetrofit().create(SearchApi::class.java).getSearchData(map)
            searchData.postValue(searchdata)
        }, {
            loge(it.toString())
        })
    }


}