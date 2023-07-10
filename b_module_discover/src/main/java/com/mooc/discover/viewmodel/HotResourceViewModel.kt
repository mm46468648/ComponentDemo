package com.mooc.discover.viewmodel

import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.HotCourseDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HotResourceViewModel : BaseListViewModel2<Any>() {

    var resourceType : Int = -1
    override suspend fun getData(): Flow<List<Any>> {
        return flow {
            if(resourceType == -1) {
                emit(arrayListOf())
                return@flow
            }
            val await = HttpService.discoverApi.getRecommendHotRes(resourceType, limit, offset)

            if(resourceType == ResourceTypeConstans.TYPE_COURSE){
                emit(await.result.map {   //用string接受的返回值类型需要转换一下
                    val toJson = GsonManager.getInstance().toJson(it)
                    GsonManager.getInstance().convert(toJson,HotCourseDetail::class.java)
                })
            }

            if(resourceType == ResourceTypeConstans.TYPE_ARTICLE){
                emit(await.result.map {
                    val toJson = GsonManager.getInstance().toJson(it)
                    GsonManager.getInstance().convert(toJson,ArticleBean::class.java)
                })
            }
        }
    }
}