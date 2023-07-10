package com.mooc.studyroom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.studyroom.AddFloderResultBean
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.module.studyroom.BaseStudyListViewModel
import org.json.JSONObject

/**
 * 学习清单添加移动公共ViewModel
 */
class StudyListMoveViewModel : BaseStudyListViewModel() {
    //文件夹数据map，记录文件夹数据模型,包括根目录
    var pageDataMap = LinkedHashMap<String,ArrayList<FolderItem>>()
    /**
     * 记录页面数据
     */
    fun recordPageData(folderId:String,folderData:ArrayList<FolderItem>){
        pageDataMap.put(folderId,folderData)
    }



    /**
     * 返回上一页
     * @param currentId 当前页面id
     * @return 返回上一页之后的id，如果为空则代表跟目录
     */
    fun backToPrePage(currentId:String) : String{
        //删除当前页面
        pageDataMap.remove(currentId)
        //如果为空则抛出异常退出页面
        if(pageDataMap.isEmpty()) throw IllegalStateException("没有数据了，可以关闭页面了")
        //如果不为空，则返回最后一个key，同时更新列表数据
        val lastKey = pageDataMap.keys.last()
        val lastValue = pageDataMap[lastKey]
        getListLiveData().postValue(lastValue)
        return lastKey
    }

    /**
     * 移动到子文件夹
     * @param toFolderId 文件夹id
     * @param needMoveResourceId 需要移动的资源id
     * @param needMoveResourceType 需要移动饿资源type
     */
    fun moveToFolder(toFolderId: String?, needMoveResourceId: String, needMoveResourceType: String) : LiveData<HttpResponse<Any>>{
        val liveData  = MutableLiveData<HttpResponse<Any>>()
        toFolderId?.let {
            val json = JSONObject()

            //如果是课程资源，有可能携带班级信息
            if (needMoveResourceId.contains("_")) {
                //如果id包含——，则代表是课程资源拼接了班级信息
                val s: Array<String> = needMoveResourceId.split("_".toRegex()).toTypedArray()
                json.put("id",s[0])
                json.put("cid", s[1])
            }else{
                json.put("id",needMoveResourceId)
            }
            json.put("type",needMoveResourceType)
            launchUI {
                val moveToFolder = studyRoomService?.moveToFolder(it, json)
                liveData.postValue(moveToFolder)
            }
        }
        return liveData
    }


    /**
     * 添加到学习室文件夹
     * @param toFolderId 文件夹id
     * @param jsonObject 资源需要传递的参数
     */
    fun addToFolder(toFolderId: String?, jsonObject: JSONObject) : LiveData<AddFloderResultBean>{
        val liveData  = MutableLiveData<AddFloderResultBean>()
        toFolderId?.let {
            launchUI {
                val moveToFolder = studyRoomService?.addToFolder(it, jsonObject)
                liveData.postValue(moveToFolder)
            }
        }
        return liveData
    }

}