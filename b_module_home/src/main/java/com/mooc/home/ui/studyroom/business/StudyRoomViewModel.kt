package com.mooc.home.ui.studyroom.business

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.core.BasePopupView
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
import com.mooc.commonbusiness.model.folder.FolderBean
import com.mooc.home.model.StudyScoreResponse
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class StudyRoomViewModel : BaseViewModel() {

    val mRepository: StudyRoomRepository by lazy { StudyRoomRepository() }
    val studyRoomService: StudyRoomService? = ARouter.getInstance().navigation(StudyRoomService::class.java)

    val studyFolderMenu = MutableLiveData<FolderBean>()

    //学习列表liveData
    val studyListLiveData = MutableLiveData<List<FolderItem>>()

    val studyRoomliveDate: MutableLiveData<StudyScoreResponse> by lazy {
        MutableLiveData<StudyScoreResponse>().also {
//            getStudyScoreData()
        }
    }

    fun getStudyRoomChildFolderMenu(folderId: String) {
        launchUI {
            val await = com.mooc.commonbusiness.api.HttpService.commonApi.getStudyRoomFolderMenu(folderId).await();
            studyFolderMenu.postValue(await.data)
        }
    }


    /**
     * 获取学习积分数据
     */
    fun getStudyScoreData() {
        launchUI {
            val requestStudyScoreData = mRepository.requestStudyScoreData()
            studyRoomliveDate.postValue(requestStudyScoreData)
        }
    }


//    /**
//     * 创建学习清单文件夹
//     */
//    fun createNewStudyFolder(name: String, pid: String = "") {
//        launchUI {
//            val requestData = JSONObject()
//            requestData.put("name", name)
//            requestData.put("pid", pid)
//            val createNewFolder = studyRoomService?.createNewFolder(requestData)
//            toast(createNewFolder?.msg)
////            studyListLiveData.postValue(createNewFolder)
//            if(createNewFolder?.isSuccess == true){
//                //刷新文件夹，或者直接添加到内存中列表结尾
//                getRootFolder()
//            }
//        }
//    }

    /**
     * 创建学习清单文件夹
     * @param pop 成功之后才能关闭
     */
    fun createNewStudyFolder(name: String, pid: String = "",pop:BasePopupView) {
        launchUI {
            val requestData = JSONObject()
            requestData.put("name", name)
            requestData.put("pid", pid)
            val createNewFolder = studyRoomService?.createNewFolder(requestData)
            toast(createNewFolder?.msg)
//            studyListLiveData.postValue(createNewFolder)
            if(createNewFolder?.isSuccess == true){
                //刷新文件夹，或者直接添加到内存中列表结尾
                pop.dismiss()
                getRootFolder()
            }
        }
    }

    /**
     * 获取学习室根目录
     */
    fun getRootFolder() {
        launchUI {
            val rootFolder = studyRoomService?.rootFolder()
            if(rootFolder?.folder?.items?.isNotEmpty()== true){
                studyListLiveData.postValue(rootFolder.folder.items)
            }
        }
    }

    /**
     * 重命名文件夹
     */
    fun renameFolder(id:String,name:String){
        launchUI {
            val response = studyRoomService?.renameFolder(id,name)
            if(response?.isSuccess == true){
                //刷新文件夹，或者直接更改该文件夹效率更高
                getRootFolder()
            }else{
                toast(response?.message?:"网络异常")
            }
        }
    }

    /**
     * 重命名文件夹
     * @param pop 成功之后才能关闭
     */
    fun renameFolder(id:String,name:String,pop:BasePopupView){
        launchUI {
            val response = studyRoomService?.renameFolder(id,name)
            if(response?.isSuccess == true){
                //刷新文件夹，或者直接更改该文件夹效率更高
                pop.dismiss()
                getRootFolder()
            }else{
                toast(response?.message?:"网络异常")
            }
        }
    }

    /**
     * 删除整个文件夹
     */
    fun deleteFolder(folderId:String){
        launchUI ({
            val deleteFolder = studyRoomService?.deleteFolder(folderId)
            toast(deleteFolder?.msg?:"")
            if(deleteFolder?.isSuccess == true){
                val arrayList = studyListLiveData.value as ArrayList<FolderItem>

                //过滤掉删除的文件夹
                val filterNot = arrayList.filterNot {
                    it.id == folderId
                }
                //发送新数据
                studyListLiveData.postValue(filterNot)

                //测试说当清单被删除的时候，要刷新下课程接口
                EventBus.getDefault().post(RefreshStudyRoomEvent(ResourceTypeConstans.TYPE_COURSE))
            }
        },{
            toast("网络异常")
        })
    }

}