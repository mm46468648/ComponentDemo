package com.mooc.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.FollowMemberBean

import com.mooc.commonbusiness.model.my.ParserStatusBean
import com.mooc.my.repository.MyModelRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.RequestBody

/**

 * @Author limeng
 * @Date 2020/11/17-10:51 AM
 */
class FollowViewModel() : BaseListViewModel<FollowMemberBean>() {
    private val repository = MyModelRepository()
    val mFollowStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }
    var type: Int = 0
    var id: String? = null

    override suspend fun getData(): Deferred<ArrayList<FollowMemberBean>?> {
        val asyn = viewModelScope.async {
            val value = repository.getFollowFansList(type, id, limit, offset)
            value.data
        }
        return asyn
    }

    fun postFollowUser(body: RequestBody) {

        launchUI {
            var bean = repository.postFollowUser(body)
            mFollowStatusBean.postValue(bean)
        }
    }




}