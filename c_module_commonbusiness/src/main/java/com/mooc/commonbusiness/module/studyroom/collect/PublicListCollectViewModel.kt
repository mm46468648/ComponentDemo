package com.mooc.commonbusiness.module.studyroom.collect

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**
 * 学习清单收藏
 */
class PublicListCollectViewModel(var folderId:String="") : BaseListViewModel<Any>() {
    var type : Int = ResourceTypeConstans.TYPE_ARTICLE      //默认加载第一个文章
    var userId = ""

    var fromRecommend = false       //运营推荐的学习清单
    var fromTask = false      //任务入口进入
    var fromTaskId = ""        //任务id
    override suspend fun getData(): Deferred<List<Any>?> {
        val mId = if(GlobalsUserManager.uid == userId) userId else ""
        val userId = if(GlobalsUserManager.uid == userId) "" else userId
        //判断是子文件夹详情请求，还是根目录请求
        val studyDataResponse = if(fromRecommend){
            val fromStr = if(fromTask) "task" else ""
            HttpService.studyRoomApi.getRecommendPublicResourceList(folderId,type.toString(), fromStr,fromTaskId, offset = offset, limit = limit).await().data
        }else{
            HttpService.studyRoomApi.getPublicResourceList(folderId, type.toString(), mId, userId,offset,limit).await().data
        }
        //判断资源类型
        return viewModelScope.async {

            when(type){
                ResourceTypeConstans.TYPE_KNOWLEDGE->studyDataResponse.folder.knowledge?.items
                ResourceTypeConstans.TYPE_PERIODICAL->studyDataResponse.folder.chaoxing?.items
                ResourceTypeConstans.TYPE_BAIKE->studyDataResponse.folder.baike?.items
                ResourceTypeConstans.TYPE_ARTICLE->studyDataResponse.folder.article?.items
                ResourceTypeConstans.TYPE_ALBUM->studyDataResponse.folder.album?.items
                ResourceTypeConstans.TYPE_TRACK->studyDataResponse.folder.track?.items
                ResourceTypeConstans.TYPE_ONESELF_TRACK->studyDataResponse.folder.audio?.items
                ResourceTypeConstans.TYPE_MICRO_LESSON->studyDataResponse.folder.micro_course?.items
                else-> arrayListOf()
            }
        }
    }
}