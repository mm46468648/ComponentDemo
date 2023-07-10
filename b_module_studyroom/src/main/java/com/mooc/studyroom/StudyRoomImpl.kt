package com.mooc.studyroom

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.home.NoteBean
import com.mooc.commonbusiness.model.studyroom.AddFloderResultBean
import com.mooc.commonbusiness.model.studyroom.ResourceFolder
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.studyroom.ui.pop.AddToStudyZoneBottomDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * 学习室服务实现类
 */
@Route(path = Paths.SERVICE_STUDYROOM)
class StudyRoomImpl : StudyRoomService {
    override fun addNote(str: String) {
        loge("成功加入笔记$str")
    }

    //获取笔记信息
    override suspend fun getNotInfo(id: String?): NoteBean {
        return studyRoomApi.getNotInfo(id).await()

    }

    /**
     * 获取学习室根目录
     */
    override suspend fun rootFolder(type: String): ResourceFolder {
        return studyRoomApi.getResourceRootFolder(type).await()
    }

    /**
     * 获取学习室子目录
     */
    override suspend fun childeRolder(id: String, type: String): ResourceFolder {
        val resourceRootFolder = studyRoomApi.getResourceChileFolder(id,type).await()
        return resourceRootFolder
    }

    /**
     * 新的
     * 获取学习室子目录
     * 方法
     */
    override suspend fun newChildeRolder(id: String, type: String):  HttpResponse<ResourceFolder> {
        val resourceRootFolder = studyRoomApi.getNewResourceChileFolder(id,type).await()
        return resourceRootFolder
    }

    /**
     * 创建子文件夹
     */
    override suspend fun createNewFolder(json: JSONObject): HttpResponse<Any> {
        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return studyRoomApi.createFolder(body).await()
    }

    /**
     * 删除资源
     */
    override suspend fun deleteResFromFolder(resId: String, resType: String) : HttpResponse<Any> {
        //如果是课程，需要掉其他接口
        if(resType == ResourceTypeConstans.TYPE_COURSE.toString()){
            if(resId.contains("_")){ //代表新学堂课程
                val courseIdArray = resId.split("_")
                return studyRoomApi.delNewCourseFromFolder(courseIdArray[0],courseIdArray[1]).await()
            }
            return studyRoomApi.deleteCourseFromFolder(resId).await()
        }

//        if(resType == ResourceTypeConstans.TYPE_BAIKE.toString()){
//            //这里的resid,要传递百科的url字段
//            val fromJsonStr = RequestBodyUtil.fromJsonStr(
//                GsonManager.getInstance()
//                    .toJson(hashMapOf("url" to resId, "resType" to ResourceTypeConstans.TYPE_BAIKE))
//            )
//            return studyRoomApi.deleteBaikeFromFolder(fromJsonStr).await()
//        }
        return studyRoomApi.deleteResFromFolder(resId,resType).await()
    }

    override suspend fun renameFolder(id: String, name: String): HttpResponse<Any> {
        return studyRoomApi.reNameFolder(id,name).await()
    }

    override suspend fun deleteFolder(folderId: String): HttpResponse<Any> {
        return studyRoomApi.delFolder(folderId).await()
    }

    override suspend fun addToFolder(toFolderId: String, json: JSONObject): AddFloderResultBean {
        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return studyRoomApi.addResToFolder(toFolderId,body).await()
    }

    override suspend fun moveToFolder(toFolderId: String, json: JSONObject) : HttpResponse<Any> {
        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return studyRoomApi.moveResToFolder(toFolderId,body).await()
    }

    override fun showAddToStudyRoomPop(context: Context, json: JSONObject?, callback: ((success: Boolean) -> Unit)?) {
        if(context is FragmentActivity){
            val moveToStudyZoneBottomDialog = AddToStudyZoneBottomDialog()
            moveToStudyZoneBottomDialog.callback = callback
            moveToStudyZoneBottomDialog.resourceJson = json
            moveToStudyZoneBottomDialog.show(context.supportFragmentManager,"MoveToFolder")
        }
    }


    /**
     * 修改文件夹名字
     */


    lateinit var studyRoomApi: StudyRoomApi
    override fun init(context: Context) {
        studyRoomApi = ApiService.getRetrofit().create(StudyRoomApi::class.java)
    }

}