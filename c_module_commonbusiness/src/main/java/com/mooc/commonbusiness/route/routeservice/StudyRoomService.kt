package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.home.NoteBean
import com.mooc.commonbusiness.model.studyroom.AddFloderResultBean
import com.mooc.commonbusiness.model.studyroom.ResourceFolder
import org.json.JSONObject


interface StudyRoomService : IProvider {
    //添加笔记
    fun addNote(str: String)
    //获取笔记信息
    suspend fun getNotInfo(id: String?): NoteBean

    //获取学习室根目录
    suspend fun rootFolder(type:String = "folder"): ResourceFolder

    //学习室子目录
    suspend fun childeRolder(id:String="",type:String = "folder"): ResourceFolder

    //学习室子目录
    suspend fun newChildeRolder(id:String="",type:String = "folder"): HttpResponse<ResourceFolder>

    //创建学习室
    suspend fun createNewFolder(json : JSONObject) : HttpResponse<Any>

    //删除文件夹、学习室自己添加资源
    suspend fun deleteResFromFolder(resId:String,resType:String) : HttpResponse<Any>
    //重命名文件夹
    suspend fun renameFolder(id:String,name:String) : HttpResponse<Any>

    //删除文件夹
    suspend fun deleteFolder(folderId:String) : HttpResponse<Any>
    /**
     *  添加到学习室文件夹 （学习资源首次添加到学习室）
     *  @param toFolderId 移动到的文件夹id，根目录"0"
     *  @param json 是内置资源类型的（比如文章电子书，音频等）传递类型和id，不是内置资源需看具体情况
     */
    suspend fun addToFolder(toFolderId :  String,json:JSONObject) : AddFloderResultBean
    /**
     *  移动到文件夹
     *  @param toFolderId 移动到的文件夹id，根目录"0"
     */
    suspend fun moveToFolder(toFolderId :  String,json:JSONObject) : HttpResponse<Any>
    //显示加入学习室弹窗
    fun showAddToStudyRoomPop(context: Context, json: JSONObject?, callback : ((success:Boolean)->Unit)? = null)
}