package com.mooc.commonbusiness.module.studyroom.ebook

import androidx.lifecycle.viewModelScope
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.search.EBookBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**
 * @param folderId 文件夹id，""，默认是根目录
 */
class PublicListEbookViewModel(var folderId:String="") : BaseListViewModel<EBookBean>() {

    var userId = ""
    var fromTaskId = ""
    var fromRecommend = false
    var fromTask = false

    override suspend fun getData(): Deferred<List<EBookBean>?> {

        val mId = if(GlobalsUserManager.uid == userId) userId else ""
        val userId = if(GlobalsUserManager.uid == userId) "" else userId

        return viewModelScope.async {
            val await =
            if(fromRecommend){
                val fromStr = if(fromTask) "task" else ""
                HttpService.studyRoomApi.getRecommendPublicResourceList(
                    folderId,
                    ResourceTypeConstans.TYPE_E_BOOK.toString(), fromStr,fromTaskId
                    , offset = offset, limit = limit
                ).await().data
            }else{
                HttpService.studyRoomApi.getPublicResourceList(
                    folderId,
                    ResourceTypeConstans.TYPE_E_BOOK.toString(),
                    mId,
                    userId,
                    offset,
                    limit
                ).await().data
            }
            await.folder.ebook?.items
        }
    }


    fun updateProgress(bookId:String,callBack:((i : Int)->Unit)){

        val mId = if(GlobalsUserManager.uid == userId) userId else ""
        val userId = if(GlobalsUserManager.uid == userId) "" else userId

        //获取这个电子书所在的区间
        var offset = 0;
        getPageData().value?.forEachIndexed{i, v->
            if(v.id == bookId){
               offset = i / 10
            }
        }

        viewModelScope.async {
            val await =
                if(fromRecommend){
                    val fromStr = if(fromTask) "task" else ""
                    HttpService.studyRoomApi.getRecommendPublicResourceList(
                        folderId,
                        ResourceTypeConstans.TYPE_E_BOOK.toString(), fromStr,fromTaskId
                        , offset = offset, limit = limit
                    ).await().data
                }else{
                    HttpService.studyRoomApi.getPublicResourceList(
                        folderId,
                        ResourceTypeConstans.TYPE_E_BOOK.toString(),
                        mId,
                        userId,
                        offset,
                        limit
                    ).await().data
                }
            val items = await.folder.ebook?.items
            items?.forEach {
                if (it.id == bookId){
                    val readProcess = it.read_process
                    getPageData().value?.forEachIndexed{i, v->
                        if(v.id == bookId){
                            v.read_process = readProcess
                            loge("updateposition: ${i} updateProgress: ${readProcess}")
                            callBack.invoke(i)
                            return@forEach
                        }
                    }
                }
            }



        }
    }
}