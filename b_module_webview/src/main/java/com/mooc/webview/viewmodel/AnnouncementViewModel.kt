package com.mooc.webview.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.AnnouncementBean
import com.mooc.commonbusiness.model.studyproject.NoticeInfoBean


class AnnouncementViewModel  : BaseViewModel() {


    val commondata: MutableLiveData<AnnouncementBean> by lazy {
        MutableLiveData<AnnouncementBean>()
    }
    val noticeInfoBean= MutableLiveData<NoticeInfoBean> ()

    fun getData(ann_id: String) {

        launchUI {
            val understandData = HttpService.otherApi.getAnnouncementDetail(ann_id).await()
            if (understandData.isSuccess)
                commondata.postValue(understandData.data)
        }
    }
    fun getNoticeInfoData(planid: String?) {

        launchUI {
            val bean = HttpService.otherApi.getNoticeInfo(planid).await()
            noticeInfoBean.postValue(bean)
        }
    }


}