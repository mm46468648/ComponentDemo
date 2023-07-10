package com.mooc.my.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.my.model.*
import com.mooc.my.repository.MyModelRepository
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.my.UploadFileBean
import okhttp3.RequestBody

/**
关于意见反馈的view model
 * @Author limeng
 * @Date 2020/10/19-3:54 PM
 */
class FeedBackViewModel : BaseViewModel() {
    private val repository = MyModelRepository()
    val feedUserBean: MutableLiveData<ArrayList<FeedUserBean>> by lazy {
        MutableLiveData<ArrayList<FeedUserBean>>()
    }
    val uploadFileBean: MutableLiveData<HttpResponse<UploadFileBean>> by lazy {
        MutableLiveData<HttpResponse<UploadFileBean>>()
    }
    val feedBackListBean: MutableLiveData<FeedBackListBean> by lazy {
        MutableLiveData<FeedBackListBean>()
    }
    val sendFeedMsgBean: MutableLiveData<SendFeedMsgBean> by lazy {
        MutableLiveData<SendFeedMsgBean>()
    }
    val mFeedBackBeann: MutableLiveData<FeedBackBean> by lazy {
        MutableLiveData<FeedBackBean>()
    }
    val mFeedBean: MutableLiveData<FeedBean> by lazy {
        MutableLiveData<FeedBean>()
    }

    fun loadData(map: Map<String, String>) {
        launchUI {
            val data = repository.getFeedList(map)
            feedUserBean.postValue(data.results)
        }

    }

    fun postImageFile(body: RequestBody) {
        launchUI {
            val data = repository.postImageFile(body)
            uploadFileBean.postValue(data)
        }


    }

    fun getFeedBackList(feedId: String?) {
        launchUI {
            val data = repository.getFeedBackList(feedId)
            feedBackListBean.postValue(data)
        }

    }

    fun sendFeedMsg(id: String?, content: String, reply_id: String?, img_attachment: String) {
        launchUI {
            val data = repository.sendFeedMsg(id, content, reply_id, img_attachment)
            sendFeedMsgBean.postValue(data)
        }

    }

    //获取反馈类型
    fun getFeedType() {
        launchUI {
            val data = repository.getFeedType()
            mFeedBackBeann.postValue(data)
        }

    }

    //获取反馈类型
    fun postFeedback(body: RequestBody) {
        launchUI {
            val data = repository.postFeedback(body)
            mFeedBean.postValue(data)
        }

    }
}