package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.module.studyroom.BaseStudyListViewModel
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.studyroom.HttpService

class PublicStudyListViewModel(var userId: String = "") : BaseStudyListViewModel() {

    /**
     * 获取学习清单中已删除的资源
     */
    fun getFolderResourceDelAsync(folderId: String) {

        launchUI {
            val await = com.mooc.commonbusiness.api.HttpService.commonApi.getFolderResourceDelAsync(folderId).await()
            folderResourceDelList.postValue(await.data)
        }
    }

    /**
     * 获取公开清单的详情
     */
    fun getPublicDetail(folderId: String) {

        val mId = if (GlobalsUserManager.uid == userId) userId else ""
        val userId = if (GlobalsUserManager.uid == userId) "" else userId

        launchUI {
            val await = HttpService.studyRoomApi.publicListDetail(folderId, mId, userId).await()
            studyListDetailLiveData.postValue(await.data)
        }
    }

    /**
     * 获取运营推荐的学习清单的详情
     */
    fun getRecommendDetail(folderId: String) {
        launchUI {
            val await = HttpService.studyRoomApi.getRecommendStudyListDetail(folderId).await()
            studyListDetailLiveData.postValue(await.data)
        }
    }


    /**
     * 获取公开清单的详情
     */
    fun getPublicFolder(folderId: String) {

        launchUI {
            val await = HttpService.studyRoomApi.getNewResourceChileFolder(folderId, "folder").await()
            publicChildFolders.postValue(await.data)
        }
    }


    /**
     * 获取公开清单的Tab
     */
    fun getFolderTabs(folderId: String, userId: String, other_user_id: String, share_user_id: String) {

        launchUI {
            val await = HttpService.studyRoomApi.getFolderTabs(folderId, userId, other_user_id, share_user_id).await()
            folderTabs.postValue(await.data)
        }
    }


    /**
     * 获取运营推荐的公开清单的tab
     */
    fun getRecommendFolderTabs(folderId: String) {
        launchUI {
            val await = HttpService.studyRoomApi.getRecommendFolderTabs(folderId).await()
            folderTabs.postValue(await.data)
        }
    }


    /**
     * 收藏到我的学习清单
     */
    fun collectStudyList(folderId: String,onSuccess:((b:Boolean)->Unit)?=null) {
        launchUI {
            val await = HttpService.studyRoomApi.collectToMyStudyList(folderId).await()
            toast(await.msg)
            onSuccess?.invoke(await.isSuccess)
        }
    }


    /**
     * 点赞或者取消
     * @param like 0点赞，1取消点赞
     */
    fun priseStudyList(folderId: String, like: Int) {
        launchUI {
            val fromJsonStr = RequestBodyUtil.fromJsonStr(
                    GsonManager.getInstance().toJson(hashMapOf("folder_id" to folderId, "like" to like))
            )
            HttpService.studyRoomApi.pristStudyList(fromJsonStr).await()
        }
    }
}