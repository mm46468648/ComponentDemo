package com.mooc.search.api

import com.mooc.commonbusiness.model.search.DataBean
import com.mooc.commonbusiness.model.search.SearchResultBean
import com.mooc.search.model.SearchItem
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**

 * @Author limeng
 * @Date 2020/8/10-11:31 AM
 */
interface SearchApi {
    //获取搜索资源结果
    @GET("/weixin/official/search/")
    suspend fun getSearchData(@QueryMap map: Map<String, String>): SearchResultBean


    @GET("/weixin/official/search/")
    suspend fun getSearchDataNew(@QueryMap map: Map<String, String>): HashMap<String,DataBean<SearchItem>>

    //获取搜索资源结果(返回Observable类型)
    @GET("/weixin/official/search/")
    fun getResSearchObservabal(@QueryMap map: Map<String, String>): Observable<SearchResultBean>
}