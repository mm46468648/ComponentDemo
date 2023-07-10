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
class StudyRoomEbookViewModel(var folderId:String?="") : BaseListViewModel<EBookBean>() {
    override suspend fun getData(): Deferred<List<EBookBean>?> {
        return viewModelScope.async {
            val await = if(folderId?.isNotEmpty() == true){
                HttpService.studyRoomApi.getFolderContentWithId(folderId,ResourceTypeConstans.TYPE_E_BOOK.toString(),offset,limit).await()
            }else{
                HttpService.studyRoomApi.getUserStudyData(ResourceTypeConstans.TYPE_E_BOOK.toString(),offset,limit).await()
            }
//            val await =
            await.ebook?.items
        }
    }


    fun updateProgress(bookId:String,callBack:((i : Int)->Unit)){
        //获取这个电子书所在的区间
        var offset = 0;
        getPageData().value?.forEachIndexed{i, v->
            if(v.id == bookId){
                offset = i / 10
            }
        }

        viewModelScope.async {
            val await = if(folderId?.isNotEmpty() == true){
                HttpService.studyRoomApi.getFolderContentWithId(folderId,ResourceTypeConstans.TYPE_E_BOOK.toString(),offset,limit).await()
            }else{
                HttpService.studyRoomApi.getUserStudyData(ResourceTypeConstans.TYPE_E_BOOK.toString(),offset,limit).await()
            }
//            val await =
            val items = await.ebook?.items

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