package com.mooc.ebook

import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.commonbusiness.model.search.EBookBean
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EbookApi {

    //获取掌阅电子书详情
    @GET("/api/resources/ebook/detail/{bookId}/")
    fun getEbookDetail(@Path("bookId") bookId: String?): Deferred<EBookBean>

    //获取推荐电子书
    @GET("/api/mobile/similar/resource/recommend/")
    fun getRecommendEBook(
            @Query("resource_type") resource_type: Int = 5,
            @Query("resource_id") resource_id: String): Deferred<HttpResponse<MutableList<EBookBean>>?>


}