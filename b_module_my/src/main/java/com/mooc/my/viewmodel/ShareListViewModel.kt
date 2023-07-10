package com.mooc.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.my.ParserStatusBean
import com.mooc.my.model.SchoolSourceBean
import com.mooc.my.repository.MyModelRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.RequestBody
import java.util.ArrayList


class ShareListViewModel() : BaseListViewModel<SchoolSourceBean>() {

    private val repository = MyModelRepository()
    val mSchoolCircleBean: MutableLiveData<SchoolSourceBean> by lazy {
        MutableLiveData<SchoolSourceBean>()
    }
    val mParserStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }

    val mDeleteStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }

    var userId: String = ""

    override suspend fun getData(): Deferred<ArrayList<SchoolSourceBean>?> {
        val asyn = viewModelScope.async {
            repository.getUserShareSchoolCircle(userId) }


        return asyn
    }

    fun postLikeSchoolResource(body: RequestBody) {
        launchUI {
            val bean = repository.postLikeSchoolResource(body)
            mParserStatusBean.postValue(bean)
        }
    }

    fun delSchoolResource(body: RequestBody) {
        launchUI {
            val bean = repository.delSchoolResource(body)
            mDeleteStatusBean.postValue(bean)
        }
    }


}
