package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.home.NoteBean
import com.mooc.commonbusiness.model.studyroom.ShareSchoolCircleBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.studyroom.StudyRoomApi
import okhttp3.RequestBody

/**

 * @Author limeng
 * @Date 2021/3/17-3:32 PM
 */
class NodeViewModel : BaseViewModel() {
    var deleteResultBean = MutableLiveData<HttpResponse<Any>>()
    var nodeResultBean = MutableLiveData<NoteBean>()
    val shareSchoolCircleBean = MutableLiveData<ShareSchoolCircleBean?>();

    fun postSchoolShare(body: RequestBody?) {
        launchUI {
            val bean =ApiService.getRetrofit().create(StudyRoomApi::class.java).postSchoolShare(body).await()
            shareSchoolCircleBean.postValue(bean)
        }
    }
    /**
     * 获取笔记信息
     */
    fun getNodeData(resId: String?) {
        launchUI({
            var nodeBean = ApiService.getRetrofit().create(StudyRoomApi::class.java).getNotInfo(resId).await()
            nodeResultBean.postValue(nodeBean)
        },{

        })
    }

    /**
     * 删除笔记
     */
    fun delNode(id: String?) {
        launchUI {
            var delBean = ApiService.getRetrofit().create(StudyRoomApi::class.java).delNote(id).await()
            deleteResultBean.postValue(delBean)
        }
    }
}