package com.mooc.commonbusiness.module.studyroom.course

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.search.CourseBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class PublicListCourseViewModel(var folderId: String) : BaseListViewModel<CourseBean>() {
    var userId = ""
    var fromTaskId = ""
    var fromRecommend = false
    var fromTask = false


    override suspend fun getData(): Deferred<List<CourseBean>?> {

        val mId = if(GlobalsUserManager.uid == userId) userId else ""
        val userId = if(GlobalsUserManager.uid == userId) "" else userId
        //如果不为空，查询的是学习清单子目录中的课程资源
        return viewModelScope.async {
            val await = if(fromRecommend){
                val fromStr = if(fromTask) "task" else ""
                HttpService.studyRoomApi.getRecommendPublicResourceList(
                    folderId,
                    ResourceTypeConstans.TYPE_COURSE.toString(),
                    fromStr,fromTaskId
                    , offset = offset, limit = limit
                ).await().data
            }else{
                HttpService.studyRoomApi.getPublicResourceList(
                    folderId,
                    ResourceTypeConstans.TYPE_COURSE.toString(),
                    mId,
                    userId,
                    offset,
                    limit
                ).await().data
            }
            await.folder.course?.items
        }

    }
}