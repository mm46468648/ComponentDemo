package com.mooc.discover.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.discover.model.TabSortBean
import com.mooc.discover.request.HomeDiscoverRepository
import com.mooc.commonbusiness.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DiscoverMiddleViewModel : BaseViewModel() {


    val request = HomeDiscoverRepository()


    /**
     * 回去推荐分类
     * id固定传1
     */
    fun getTabSort() : LiveData<List<TabSortBean>>{
        val tabList =  MutableLiveData<List<TabSortBean>>()
        launchUI {
            val homeFirstTab = request.getDiscoverTab(1)
            tabList.postValue(homeFirstTab.data)
        }
        return tabList
    }

    /**
     * 回去推荐分类
     * id固定传1
     */
    fun getTabSortFlow() : Flow<List<TabSortBean>> {
        return flow {
            val homeFirstTab = request.getDiscoverTab(1)
            emit(homeFirstTab.data)
        }
    }
}