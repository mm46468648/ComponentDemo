package com.mooc.home.ui.discover.audio

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.home.HttpService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DiscoverAudoChildViewModel : BaseListViewModel2<AlbumBean>() {

    var map: HashMap<String, String> = hashMapOf()
    var listCount = MutableLiveData<String?>()   //搜索列表数目
    var lastRequestPage = 1

    override fun getLimitStart(): Int = 30
    override fun getOffSetStart(): Int = 0

    override suspend fun getData(): Flow<List<AlbumBean>?> {
        return flow {
//            val page = (if (offset == 0) 1 else (offset / limit) + 1).toString()
//            map["page"] = page    //page,从1，开始，并且以此加1
            if (offset == 0) {
                lastRequestPage = 1
            }
            map["page"] = lastRequestPage.toString()
            map["page_size"] = limit.toString()

            val course = HttpService.homeApi.getSearchResult(map)
            if (course.album?.count != null) {
                lastRequestPage++
                listCount.postValue(course.album?.count.toString())
            }
            emit(course.album?.items ?: listOf<AlbumBean>())
        }
    }
}