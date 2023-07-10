package com.mooc.commonbusiness.module.studyroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lxj.xpopup.core.BasePopupView
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.folder.FolderBean
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.model.studyroom.PublicStudyListResponse

class StudyListDetailViewModel : BaseStudyListViewModel() {

    var publicListNum = 0

    val studyFolderMenu = MutableLiveData<FolderBean>()

    val publicStatus by lazy {            //学习清单公开状态
        MutableLiveData<Boolean>().also {
            it.value = false
        }
    }

    /**
     * 获取学习清单中已删除的资源
     */
    fun getFolderResourceDelAsync(folderId: String) {

        launchUI {
            val await = HttpService.commonApi.getFolderResourceDelAsync(folderId).await()
            folderResourceDelList.postValue(await.data)
        }
    }

    /**
     * 重命名文件夹
     */
    fun renameFolder(id: String,pid:String, name: String,pop:BasePopupView) {
        launchUI {
            val response = studyRoomService?.renameFolder(id, name)
            if (response?.isSuccess == true) {
                pop.dismiss()
                //刷新文件夹，或者直接更改该文件夹效率更高
                getChildFolderNew(pid)
            } else {
                toast(response?.msg ?: "网络异常")
            }
        }
    }

    /**
     * 删除整个文件夹
     */
    fun deleteFolder(folderId: String) {
        launchUI({
            val deleteFolder = studyRoomService?.deleteFolder(folderId)
            toast(deleteFolder?.msg ?: "")
            if (deleteFolder?.isSuccess == true) {
//                val arrayList = studyListLiveData.value as ArrayList<FolderItem>

                //过滤掉删除的文件夹
                val filterNot = studyListDetailLiveData.value?.folder?.folder?.items?.filterNot {
                    it.id == folderId
                }

                filterNot?.let {
                    //发送新数据
                    studyListDetailLiveData.value?.folder?.folder?.items = filterNot as ArrayList<FolderItem>?
                    studyListDetailLiveData.postValue(studyListDetailLiveData.value)
//                    studyListLiveData.postValue(it as ArrayList<FolderItem>)
                }
                //重新刷新下文件夹接口
            }
        }, {
            toast("网络异常")
        })
    }

    /**
     * 获取是否可以公开学习清单
     * 并弹出对应的提示
     */
    fun getPublicMessage(folderId: String): LiveData<PublicStudyListResponse> {
        val liveData = MutableLiveData<PublicStudyListResponse>()
        launchUI {
            val response = HttpService.studyRoomApi.getPublicMessage(folderId).await().data
            liveData.postValue(response)
        }
        return liveData
    }

    /**
     * 公开学习清单
     */
    fun postPublicList(folderId: String): LiveData<HttpResponse<Any>?> {
        val liveData = MutableLiveData<HttpResponse<Any>>()
        launchUI {
            val response = HttpService.studyRoomApi.publicStudyList(folderId).await()
            liveData.postValue(response)
        }
        return liveData
    }

    /**
     * 取消公开学习清单
     */
    fun canclePublicStudyList(folderId: String): LiveData<HttpResponse<Any>> {
        val liveData = MutableLiveData<HttpResponse<Any>>()
        launchUI {
            val response = HttpService.studyRoomApi.canclePublicStudyList(folderId).await()
            toast(response.msg)
            liveData.postValue(response)
        }
        return liveData
    }

    fun getShareData(folder: String): LiveData<ShareDetailModel> {
        val liveData = MutableLiveData<ShareDetailModel>()
        launchUI {
            val shareDetailData =
                    HttpService.commonApi.getShareDetailData(
                            ResourceTypeConstans.TYPE_SOURCE_FOLDER.toString(),
                            folder
                    ).await()
            liveData.postValue(shareDetailData.data)
        }
        return liveData
    }


    fun getStudyRoomChildFolderMenu(folderId: String) {
        launchUI {
            val await = HttpService.commonApi.getStudyRoomFolderMenu(folderId).await();
            studyFolderMenu.postValue(await.data)
        }
    }


}