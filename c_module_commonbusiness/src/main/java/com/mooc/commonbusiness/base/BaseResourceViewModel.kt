package com.mooc.commonbusiness.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel

/**
 * 处理学习资源类型的VielModle
 */
open class BaseResourceViewModel(var resourceId: String, var resourceType: Int) : BaseViewModel() {


    val resourceShareDetaildata = MutableLiveData<ShareDetailModel>().also {
        getShareDetailData(resourceType.toString(), resourceId)
    }

    /**
     * 获取分享和加入学习室状态
     */
    fun getShareDetailData(resource_type: String, resource_id: String) {
        launchUI {
            val sharedata =
                HttpService.commonApi.getShareDetailDataNew(ShareTypeConstants.SHARE_TYPE_RESOURCE,resource_type, resource_id)
            if (sharedata.isSuccess) {
                resourceShareDetaildata.postValue(sharedata.data)
            }
        }
    }

    /**
     * 直接返回分享信息
     */
    fun getShareInfo(resource_type: String, resource_id: String): LiveData<ShareDetailModel> {
        val mutableLiveData = MutableLiveData<ShareDetailModel>()
        launchUI {
            val shareDetailData =
                HttpService.commonApi.getShareDetailDataNew(ShareTypeConstants.SHARE_TYPE_RESOURCE,resource_type, resource_id)
            mutableLiveData.postValue(shareDetailData.data)
        }
        return mutableLiveData
    }
}