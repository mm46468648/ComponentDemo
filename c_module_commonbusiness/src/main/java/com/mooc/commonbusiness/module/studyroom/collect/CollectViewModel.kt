package com.mooc.commonbusiness.module.studyroom.collect

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**
 * 学习清单收藏
 */
class CollectViewModel(var folderId:String?="") : BaseListViewModel<Any>() {
    var type : Int = -1      //默认加载第一个文章
    override suspend fun getData(): Deferred<List<Any>?> {

        //判断是子文件夹详情请求，还是根目录请求
        val studyDataResponse = if(folderId?.isNotEmpty() == true){
            HttpService.studyRoomApi.getFolderContentWithId(folderId,type.toString(),offset,limit).await()
        }else
            HttpService.studyRoomApi.getUserStudyData(type.toString(),offset,limit).await()

        //判断资源类型
        return viewModelScope.async {

            when(type){
                ResourceTypeConstans.TYPE_KNOWLEDGE->studyDataResponse.knowledge?.items
                ResourceTypeConstans.TYPE_PERIODICAL->studyDataResponse.chaoxing?.items
                ResourceTypeConstans.TYPE_BAIKE->studyDataResponse.baike?.items
                ResourceTypeConstans.TYPE_ARTICLE->studyDataResponse.article?.items
                ResourceTypeConstans.TYPE_ALBUM->studyDataResponse.album?.items
                ResourceTypeConstans.TYPE_TRACK->studyDataResponse.track?.items
                ResourceTypeConstans.TYPE_ONESELF_TRACK->studyDataResponse.audio?.items
                ResourceTypeConstans.TYPE_MICRO_LESSON->studyDataResponse.micro_course?.items
                else-> arrayListOf()
            }
        }
    }
}