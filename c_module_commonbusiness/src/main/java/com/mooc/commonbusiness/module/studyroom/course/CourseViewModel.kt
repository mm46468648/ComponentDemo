package com.mooc.commonbusiness.module.studyroom.course

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.CourseBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class CourseViewModel(var folderId : String?="") : BaseListViewModel<CourseBean>() {
    override suspend fun getData(): Deferred<List<CourseBean>?> {

        //如果不为空，查询的是学习清单子目录中的课程资源
        if(folderId?.isNotEmpty() == true){
            return viewModelScope.async {
                val await = HttpService.studyRoomApi.getFolderContentWithId(folderId, ResourceTypeConstans.TYPE_COURSE.toString(),offset,limit).await()
                await.course?.items
            }
        }
        return viewModelScope.async {
            val await = HttpService.studyRoomApi.getStudyRoomCourse(offset, limit).await()
            await.results
        }
    }

    /**
     * 在学习室中删除课程资源
     */
    fun deleteCourse(courseBean: CourseBean) {

    }
}