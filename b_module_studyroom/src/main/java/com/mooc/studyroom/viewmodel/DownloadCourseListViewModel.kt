package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.course.BaseChapter
import com.mooc.commonbusiness.route.routeservice.CourseDownloadService

/**
 * 下载课程列表
 */
class DownloadCourseListViewModel : BaseViewModel() {


    val mChaptersLiveData = MutableLiveData<List<BaseChapter>>()

    fun loadData(courseId: String, classRoomId: String){
        val navigation = ARouter.getInstance().navigation(CourseDownloadService::class.java)
        mChaptersLiveData.postValue(navigation.findDownloadChapter(courseId,classRoomId))
    }
}