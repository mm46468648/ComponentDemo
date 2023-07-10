package com.mooc.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.model.my.ParserStatusBean
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.search.api.MyModelApi
import com.mooc.my.model.SchoolSourceBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.RequestBody

/**
 *学友圈
 * @author limeng
 * @date 2021/4/9
 */
class SchoolCircleViewModel:BaseListViewModel<SchoolSourceBean>() {
    val mParserStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }
    override suspend fun getData(): Deferred<List<SchoolSourceBean>?> {
       return  viewModelScope.async {
           val result=ApiService.getRetrofit().create(MyModelApi::class.java).getSchoolCircle(limit,offset).await()
           result.share_list
       }

    }
    val mDeleteStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }
    fun delSchoolResource(body: RequestBody) {
        launchUI {
            val bean = ApiService.getRetrofit().create(MyModelApi::class.java).delSchoolResource(body).await()
            mDeleteStatusBean.postValue(bean)
        }
    }

    fun postLikeSchoolResource(body: RequestBody) {
        launchUI {
            val bean =ApiService.getRetrofit().create(MyModelApi::class.java).postLikeSchoolResource(body).await()
            mParserStatusBean.postValue(bean)
        }
    }
}