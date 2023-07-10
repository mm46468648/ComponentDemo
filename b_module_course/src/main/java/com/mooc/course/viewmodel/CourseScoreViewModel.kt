package com.mooc.course.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.course.repository.CourseRepository
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.model.xuetang.SequentialChildren
import com.mooc.course.model.CourseScoreBean
import com.mooc.course.model.GradePolicy
import okhttp3.RequestBody
import retrofit2.http.Body

/**
 *关于评分的接口
 * @author limeng
 * @date 2022/5/5
 */
class CourseScoreViewModel: BaseViewModel() {

    var mRepository = CourseRepository

    //
    val appraiseList: MutableLiveData<ArrayList<CourseScoreBean>> by lazy {
        MutableLiveData<ArrayList<CourseScoreBean>>()
    }

    val postCourseScoreResult: MutableLiveData<HttpResponse<Any>> by lazy {
        MutableLiveData<HttpResponse<Any>>()
    }


    /**
     *获取资源评价的维度
     */
    fun getAppraise(resource_type: String) {
        launchUI {
            val beans = mRepository.getCourseAppraise(resource_type)
            if (beans.data != null) {
                appraiseList.postValue(beans.data)
            }
        }
    }
    /**
     *上传课程的评价
     */
    fun postAppraise(body: RequestBody) {
        launchUI {
            val  result= mRepository.postCourseAppraise(body)
            postCourseScoreResult.postValue(result)

        }
    }



}