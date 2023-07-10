package com.mooc.discover.column

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.intellij.lang.annotations.Flow

class ColumnListViewModel : BaseViewModel(){


    fun getShareData(resourceId:String) : LiveData<ShareDetailModel>{
        val shareDate = MutableLiveData<ShareDetailModel>()
        launchUI {
            flow<ShareDetailModel> {
                val shareDetailData = HttpService.commonApi.getShareDetailDataNew(
                    ShareTypeConstants.SHARE_TYPE_RESOURCE,
                    ResourceTypeConstans.TYPE_COLUMN.toString(),
                    resourceId
                )

                emit(shareDetailData.data)
            }.collect {
                shareDate.postValue(it)
            }
        }
        return shareDate
    }
}