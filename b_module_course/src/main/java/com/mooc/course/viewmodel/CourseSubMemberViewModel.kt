package com.mooc.course.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.CommonApi
import com.mooc.commonbusiness.base.BaseListViewModel

import com.mooc.commonbusiness.model.my.ParserStatusBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.course.CourseApi
import com.mooc.commonbusiness.model.FollowMemberBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.RequestBody

/**
 *
 * @author limeng
 * @date 2022/5/5
 */
class CourseSubMemberViewModel() : BaseListViewModel<FollowMemberBean>() {
    val mFollowStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }
    var course_id: String? = null
    var sub_users: String? = null

    override suspend fun getData(): Deferred<ArrayList<FollowMemberBean>?> {
        val asyn = viewModelScope.async {
            val value = ApiService.getRetrofit().create(CourseApi::class.java).getChoseCoursePeopleList(course_id,sub_users, limit, offset).await()
            value.data.result
        }
        return asyn
    }

    /**
     * 关注和取消关注
     */
    fun postFollowUser(body: RequestBody) {
        launchUI {
            val bean = ApiService.getRetrofit().create(CommonApi::class.java).postFollowUser(body).await()
            mFollowStatusBean.postValue(bean)
        }
    }


}