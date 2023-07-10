package com.mooc.studyproject.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.b_module_studyproject.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.my.CMSShareBean
import com.mooc.commonbusiness.model.my.UploadFileBean
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.commonbusiness.model.studyroom.ShareSchoolCircleBean
import com.mooc.studyproject.model.*
import okhttp3.RequestBody
import java.util.*

/**

 * @Author limeng
 * @Date 2020/11/25-6:51 PM
 */
class StudyProjectViewModel : BaseViewModel() {
    val studyPlanDetailBean = MutableLiveData<StudyPlanDetailBean>();
    val noticeBean = MutableLiveData<NoticeBean>();
    val exChangeBean = MutableLiveData<ExChangeBean>();
    val studyPlanAddBean = MutableLiveData<StudyPlanAddBean?>();
    val studyPlansFinishBean = MutableLiveData<StudyPlansBean?>();
    val studyPlansNoFinishBean = MutableLiveData<StudyPlansBean?>();
    val resultMsgBean = MutableLiveData<ResultMsgBean?>();
    val resourceStatusBean = MutableLiveData<ResourceStatusBean?>();
    val dynamicsBean = MutableLiveData<DynamicsBean?>();
    val studyMemberBean = MutableLiveData<StudyMemberBean?>();
    val postFillStudyPlanBean = MutableLiveData<PostFillStudyPlanBean?>();
    val studyActivityBean = MutableLiveData<StudyActivityBean?>();
    val studyActivityWithTitleBean = MutableLiveData<StudyActivityBean?>();
    val delDynamicBean = MutableLiveData<ResultMsgBean?>();
    //    val reportLearnBean = MutableLiveData<ReportLearnBean?>();
    val reportDataResult = MutableLiveData<ResultMsgBean?>();
    val stopStudyPlan = MutableLiveData<PostFillStudyPlanBean?>();
    val shareSchoolCircleBean = MutableLiveData<ShareSchoolCircleBean?>();
    val uploadFileBean = MutableLiveData<HttpResponse<UploadFileBean>>();
    val sendCommendBean = MutableLiveData<SendCommendBean?>();
    val studyDynamicDetails = MutableLiveData<StudyDynamic?>();
    val stopCpmmendBean = MutableLiveData<PostFillStudyPlanBean?>();
    val detlteCommendBean = MutableLiveData<PostFillStudyPlanBean?>();
    val uploadVoiceBean = MutableLiveData<UploadVoiceBean?>();
    var exception = MutableLiveData<Exception?>();
    var errException = MutableLiveData<Exception?>();
    var dynamicsException = MutableLiveData<Exception?>();
//    var audioInfoException = MutableLiveData<Exception?>();
    var orderMessageResult = MutableLiveData<HttpResponse<Any>>();
//    var shareView = MutableLiveData<HttpResponse<CMSShareBean>>();

    fun orderMessage(requestBody: RequestBody) {
        launchUI {
            val result = HttpService.userApi.orderMessage(requestBody)?.await()
            orderMessageResult.postValue(result)
        }

    }

    fun getCMSShareData(requestBody: RequestBody): MutableLiveData<HttpResponse<CMSShareBean>> {
        val shareView = MutableLiveData<HttpResponse<CMSShareBean>>();
        launchUI {
            val resule =
                    com.mooc.commonbusiness.api.HttpService.commonApi.getCMSShareData(requestBody)
            shareView.postValue(resule)
        }
        return shareView
    }

    fun getData(resId: String) {
        launchUI {
            val bean = HttpService.userApi.getStudyPlanDetail(resId).await()
            studyPlanDetailBean.postValue(bean)
        }
    }

    fun getStudyPlanNoticeList(noteId: String) {
        launchUI {
            val bean = HttpService.userApi.getStudyPlanNoticeList(noteId).await()
            noticeBean.postValue(bean)
        }
    }

    fun exchangeCode(body: RequestBody) {
        launchUI {
            val bean = HttpService.userApi.exchangeCode(body).await()
            exChangeBean.postValue(bean)
        }
    }

    fun postAddStudyPlan(body: RequestBody) {
        launchUI {
            val bean = HttpService.userApi.postAddStudyPlan(body).await()
            studyPlanAddBean.postValue(bean)
        }
    }

    fun getLearnStudyNotFinish(id: String, limit: Int, offset: Int) {
        launchUI {
            val bean = HttpService.userApi.getLearnStudyNotFinish(id, limit, offset).await()
            studyPlansNoFinishBean.postValue(bean)
        }
    }

//    fun getLearnStudyFinish(id: String, limit: Int, offset: Int) {
//        launchUI {
//            val bean = HttpService.userApi.getLearnStudyFinish(id, limit, offset).await()
//            studyPlansFinishBean.postValue(bean)
//        }
//    }
//
//    fun postResourceClickStatus(body: RequestBody) {
//        launchUI( {
//            val bean = HttpService.userApi.postResourceClickStatus(body).await()
//            resultMsgBean.postValue(bean)
//        },{
//            audioInfoException.postValue(it)
//        })
//    }

    fun publishDynamics(studyPlan: String, body: RequestBody) {
        launchUI({
            val bean = HttpService.userApi.publishDynamics(studyPlan, body).await()
            dynamicsBean.postValue(bean)
        }, {
            dynamicsException.postValue(it)
        })
    }

    fun getResourceStatus(id: String) {
        launchUI {
            val bean = HttpService.userApi.getResourceStatus(id).await()
            resourceStatusBean.postValue(bean)
        }
    }

    fun getStudySourceMembers(id: String, checkin_source_id: String) {
        launchUI {
            val bean = HttpService.userApi.getStudySourceMembers(id, checkin_source_id).await()
            studyMemberBean.postValue(bean)
        }
    }

    fun postFillStudyPlan(body: RequestBody) {
        launchUI {
            val bean = HttpService.userApi.postFillStudyPlan(body).await()
            postFillStudyPlanBean.postValue(bean)
        }
    }

    fun getStudyPlanDetailActivity(id: String, limit: Int, offset: Int) {
        launchUI {
            val bean = HttpService.userApi.getStudyPlanDetailActivity(id, limit, offset).await()
            studyActivityBean.postValue(bean)
        }
    }

    fun getStudyPlanMyActivity(id: String, limit: Int, offset: Int) {
        launchUI {
            val bean = HttpService.userApi.getStudyPlanMyActivity(id, limit, offset).await()
            studyActivityBean.postValue(bean)
        }
    }

    fun getStudyPlanDetailWithTitle(id: String, map: HashMap<String, String>) {
        launchUI {
            val bean = HttpService.userApi.getStudyPlanDetailWithTitle(id, map).await()
            studyActivityWithTitleBean.postValue(bean)
        }
    }

    fun getStudyPlanMyActivity(id: String, map: HashMap<String, String>) {
        launchUI {
            val bean = HttpService.userApi.getStudyDynamicWithTitleMy(id, map).await()
            studyActivityWithTitleBean.postValue(bean)
        }
    }

    fun delDynamic(id: String) {
        launchUI {
            val bean = HttpService.userApi.delDynamic(id).await()
            delDynamicBean.postValue(bean)
        }
    }

    fun getReportLearnData() {
//        launchUI {
//            val bean = HttpService.userApi.getReportLearnData().await()
//            reportLearnBean.postValue(bean)
//        }
    }

    fun postReportLearnData(body: RequestBody) {
        launchUI {
            val bean = HttpService.userApi.postReportLearnData(body).await()
            reportDataResult.postValue(bean)
        }
    }

    fun postStopStudyPlan(body: RequestBody) {
        launchUI {
            val bean = HttpService.userApi.postStopStudyPlan(body).await()
            stopStudyPlan.postValue(bean)
        }
    }

    fun postSchoolShare(body: RequestBody) {
        launchUI {
            val bean = HttpService.userApi.postSchoolShare(body).await()
            shareSchoolCircleBean.postValue(bean)
        }
    }

    fun postImageFile(body: RequestBody?) {
        launchUI {
            val bean = com.mooc.commonbusiness.api.HttpService.commonApi.postDynamicImageAsync(body).await()
            uploadFileBean.postValue(bean)
        }
    }

    fun postCommentData(study_activity: String, body: RequestBody) {
        launchUI(
                {
                    val bean = HttpService.userApi.postCommentData(study_activity, body).await()
                    sendCommendBean.postValue(bean)
                }, {
            errException.postValue(it)
        }
        )
    }

    fun getCommentDynamic(id: String) {
        launchUI {
            val bean = HttpService.userApi.getCommentDynamic(id).await()
            studyDynamicDetails.postValue(bean)
        }
    }

    fun postStopCommentStudyPlan(body: RequestBody) {
        launchUI {
            val bean = HttpService.userApi.postStopCommentStudyPlan(body).await()
            stopCpmmendBean.postValue(bean)
        }
    }


    fun delComment(comment_id: String) {
        launchUI {
            val bean = HttpService.userApi.delComment(comment_id).await()
            detlteCommendBean.postValue(bean)
        }
    }

    fun publishVoice(body: RequestBody) {
        launchUI({
            val bean = HttpService.userApi.publishVoice(body).await()
            uploadVoiceBean.postValue(bean)
        }, {
            exception.postValue(it)
        })
    }


}