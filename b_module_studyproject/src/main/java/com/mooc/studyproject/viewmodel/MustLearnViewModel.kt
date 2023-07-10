package com.mooc.studyproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.b_module_studyproject.httpserver.HttpService
import com.mooc.common.ktextends.toastMain
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.studyproject.model.*

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.RequestBody
import java.util.ArrayList

/**

 * @Author limeng
 * @Date 3/9/22-4:51 PM
 */
class MustLearnViewModel : BaseListViewModel<StudyPlanSource>() {
    var id:String?=null
    val resultMsgBean = MutableLiveData<ResultMsgBean?>()
    val resourceStatusBean = MutableLiveData<ResourceStatusBean?>()
//    val dynamicsBean = MutableLiveData<DynamicsBean?>()
//    val studyMemberBean = MutableLiveData<StudyMemberBean?>()
//    var audioInfoException = MutableLiveData<Exception?>();
//    val postFillStudyPlanBean = MutableLiveData<PostFillStudyPlanBean?>();

    override suspend fun getData(): Deferred<List<StudyPlanSource>?> {
        return viewModelScope.async {
            if (id != null) {
                val bean = HttpService.userApi.getLearnStudyNotFinish(id!!, limit, offset).await()
                bean?.results
            }else{
                ArrayList<StudyPlanSource>()
            }

        }
    }
    fun postResourceClickStatus(body: RequestBody) {
        launchUI( {
            val bean = HttpService.userApi.postResourceClickStatus(body).await()
            resultMsgBean.postValue(bean)
        },{
//            audioInfoException.postValue(it)
            toastMain("服务器异常")
        })
    }
//    fun postFillStudyPlan(body: RequestBody) {
//        launchUI {
//            val bean = HttpService.userApi.postFillStudyPlan(body).await()
//            postFillStudyPlanBean.postValue(bean)
//        }
//    }
    fun getResourceStatus(id: String) {
        launchUI {
            val bean = HttpService.userApi.getResourceStatus(id).await()
            resourceStatusBean.postValue(bean)
        }
    }
    fun publishDynamics(studyPlan: String, body: RequestBody) {
//        launchUI{
//            val bean = HttpService.userApi.publishDynamics(studyPlan, body).await()
//            dynamicsBean.postValue(bean)
//        }
    }
    fun getStudySourceMembers(id: String, checkin_source_id: String) {
//        launchUI {
//            val bean = HttpService.userApi.getStudySourceMembers(id, checkin_source_id).await()
//            studyMemberBean.postValue(bean)
//        }
    }

    fun publishDynamicsNew(studyPlan: String, body: RequestBody):LiveData<DynamicsBean>{
        val liveData = MutableLiveData<DynamicsBean>()
        launchUI{
            val bean = HttpService.userApi.publishDynamics(studyPlan, body).await()
            liveData.postValue(bean)
        }
        return liveData

    }

    fun getStudySourceMembersNew(id: String, checkin_source_id: String):LiveData<StudyMemberBean> {
        val liveData = MutableLiveData<StudyMemberBean>()
        launchUI {
            val bean = HttpService.userApi.getStudySourceMembers(id, checkin_source_id).await()
            liveData.postValue(bean)
        }
        return liveData
    }

    fun postFillStudyPlanNew(body: RequestBody): LiveData<PostFillStudyPlanBean>{
        val liveData = MutableLiveData<PostFillStudyPlanBean>()
        launchUI {
            val bean = HttpService.userApi.postFillStudyPlan(body).await()
            liveData.postValue(bean)
        }

        return liveData
    }
}