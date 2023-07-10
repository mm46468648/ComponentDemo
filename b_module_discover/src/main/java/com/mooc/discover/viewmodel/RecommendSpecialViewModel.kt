package com.mooc.discover.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.discover.model.RecommendContentBean
import com.mooc.discover.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.constants.ShareTypeConstants.Companion.SHARE_TYPE_RESOURCE
import com.mooc.discover.model.RecommendResTypeBean
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class RecommendSpecialViewModel : BaseListViewModel<RecommendContentBean.DataBean>() {

    var mTab: String?=null
    override suspend fun getData(): Deferred<List<RecommendContentBean.DataBean>> {

        val async = viewModelScope.async {
            val await = HttpService.discoverApi.getRecommendDetailList(mTab?:"1", limit, offset).await()
            await.results!!
        }
        return async
    }


    //推荐专题页面使用
    var mRecommendContentBean = MutableLiveData<RecommendContentBean>()
    var mRecommendResTypeBean = MutableLiveData<RecommendResTypeBean>()
    val articleWebShareDetaildata: MutableLiveData<ShareDetailModel> by lazy {
        MutableLiveData<ShareDetailModel>()
    }

    fun getRecommendListData(id: String?, map: Map<String, String>) {
        launchUI {
            val await = HttpService.discoverApi.getRecommendListWithId(id, map).await()
            mRecommendContentBean.postValue(await)
        }
    }

    fun getRecommendResTypes(parentId: String?) {
        launchUI {
            val await = HttpService.discoverApi.getRecommendResTypes(parentId).await()
            mRecommendResTypeBean.postValue(await)
        }
    }

    fun getShareDetailData(resource_type: String, resource_id: String) {
        launchUI {
            val sharedata = com.mooc.commonbusiness.api.HttpService.commonApi.getShareDetailDataNew(SHARE_TYPE_RESOURCE,resource_type, resource_id)
            if (sharedata.isSuccess) {
                articleWebShareDetaildata.postValue(sharedata.data)
            }
        }
    }

    /**
     * 发送订阅
     */
    fun postSubscribe(id:String) : LiveData<Boolean>{
        var mRecommendResTypeBean = MutableLiveData<Boolean>()
        launchUI {
            val await = HttpService.discoverApi.postSpecialSubscribe(id)
            mRecommendResTypeBean.postValue(await.isSuccess)
        }
        return mRecommendResTypeBean
    }




}