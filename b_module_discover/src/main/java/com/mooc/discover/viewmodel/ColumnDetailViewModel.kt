package com.mooc.discover.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.discover.model.RecommendContentBean
import com.mooc.discover.request.HomeDiscoverRepository
import com.mooc.commonbusiness.base.BaseViewModel

/**
 * @param 专栏id
 */
class ColumnDetailViewModel(var resId:String) : BaseViewModel() {

    var currentPage:Int = 1
    var pageSize = 10
    val mRepository by lazy {   HomeDiscoverRepository() }
    val columnDetailData = MutableLiveData<RecommendContentBean>()   //专栏详情
    val columnList = MutableLiveData<ArrayList<RecommendContentBean.DataBean>>().also {
        it.value = ArrayList<RecommendContentBean.DataBean>()
    }   //专栏列表

    /**
     * 刷新列表
     * 将currentPage 重置为1
     */
    fun refreshList(){
        currentPage = 1
        getColumnList()
    }

    /**
     * 请求专栏详情和专栏列表
     */
    fun getColumnList(){
        launchUI {
            val columnDetail = mRepository.getColumnDetail(resId, currentPage, pageSize) ?: return@launchUI
            if(columnDetailData.value == null){
                columnDetailData.postValue(columnDetail)
            }

            if(columnDetail.data.isNotEmpty()){
                if(currentPage == 1){
                    columnList.value?.clear()
                }
                currentPage++
                columnList.value?.addAll(columnDetail.data)
                columnList.postValue(columnList.value)
            }

        }
    }
}