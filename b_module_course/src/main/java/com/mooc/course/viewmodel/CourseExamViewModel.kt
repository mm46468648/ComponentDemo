package com.mooc.course.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.course.CourseApi
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.xuetang.VerifyStatusBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.course.model.SequentialBean
import com.mooc.course.repository.CourseRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class CourseExamViewModel : BaseListViewModel<SequentialBean>() {
    val mRepository by lazy { CourseRepository }
    var xtCourseId = ""

    //得分数据
    val coursePoint = MutableLiveData<Int>()
    //证书数据
    val verifyStatus by lazy {
        MutableLiveData<VerifyStatusBean>().also {
            getCourseVerifyStatus()
        }
    }
    override suspend fun getData(): Deferred<List<SequentialBean>> {
        val async = viewModelScope.async {
            val await = ApiService.xtRetrofit.create(CourseApi::class.java).getCourseExamsData(xtCourseId).await()
            coursePoint.postValue(await.course_point)
            await.chapters.flatMap {chapter->
                chapter.sequentials.forEach { sequential->
                    sequential.parentDisplayName = chapter.display_name
                }
                chapter.sequentials
            }
        }
        return async
    }

    /**
     * 获取证书状态
     */
    fun getCourseVerifyStatus(){
        launchUI {
            val courseVerifyStatus = mRepository.getCourseVerifyStatus(xtCourseId)
            if(courseVerifyStatus.data != null){
                verifyStatus.postValue(courseVerifyStatus.data)
            }
        }
    }
}