package com.mooc.course.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.course.model.ZHSExam
import com.mooc.course.model.ZHSExamData
import com.mooc.course.repository.CourseRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.RequestBody

class ZHSExamViewModel : BaseListViewModel<ZHSExamData>() {

    var courseId = ""
    val mRepository = CourseRepository
    var headData = MutableLiveData<ZHSExam>()

    override suspend fun getData(): Deferred<List<ZHSExamData>> {
        return viewModelScope.async {
            val data = mRepository.getZHSExamData(courseId).data
            if(data == null){
                arrayListOf<ZHSExamData>()
            }else{
                headData.postValue(data)
                data.exam_list
            }
        }
    }

    fun postCourseScore(body: RequestBody) {
        launchUI {
            mRepository.postCourseScore(body)
        }
    }


}