package com.mooc.home.ui.discover.ebook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.home.HttpService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class EbookChildViewModel : BaseListViewModel<EBookBean>() {

    var map: HashMap<String, String> = hashMapOf()
    var listCount = MutableLiveData<String?>()   //搜索列表数目
    var lastRequestPage = 1
    override suspend fun getData(): Deferred<List<EBookBean>?> {
        //原来这样判断是因为offset会累加
        //page,从1，开始，第二页需根据情况判断是+1还是加2 如果不满足整除就加2 如果满足整除就加1
//        val page = (if (offset == 0) 1 else (offset / limit) + 1).toString()
//        map["page"] = page    //page,从1，开始，并且以此加1
        if(offset == 0){
            lastRequestPage = 1
        }
        map["page"] = lastRequestPage.toString()
        map["page_size"] = limit.toString()
        return viewModelScope.async {
            val course = HttpService.homeApi.getSearchResult(map).ebook
            if (course?.count != null) {
                lastRequestPage++
                listCount.postValue(course.count.toString())
            }
            course?.items
        }
    }


    override fun getOffSetStart(): Int = 0
    override fun getLimitStart(): Int = 30

}