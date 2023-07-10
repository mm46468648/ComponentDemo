package com.mooc.commonbusiness.module.studyroom

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.core.BasePopupView
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.eventbus.StudyRoomResourceChange
import com.mooc.commonbusiness.model.folder.FolderBean
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.model.studyroom.FolderResourceDelBean
import com.mooc.commonbusiness.model.studyroom.ResourceFolder
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * 学习清单逻辑基类
 */
open class BaseStudyListViewModel : BaseViewModel() {

    val studyRoomService: StudyRoomService? = ARouter.getInstance().navigation(StudyRoomService::class.java)

    //学习列表liveData
    val studyListLiveData = MutableLiveData<ArrayList<FolderItem>>()

    //学习清单资源后台删除后，提示用户：您的学习清单中“XX”“XX”资源已失效
    open val folderResourceDelList = MutableLiveData<ArrayList<FolderResourceDelBean>>()


    init {
        studyListLiveData.value = arrayListOf()
    }

    //学习清单详情
    open val studyListDetailLiveData = MutableLiveData<ResourceFolder>()

    //子文件夹清单
    open val publicChildFolders = MutableLiveData<ResourceFolder>()


    //子文件夹tabs
    open val folderTabs = MutableLiveData<FolderBean>()

    //学习清单公开状态
    open val lockState = MutableLiveData<Boolean>()

    fun getListLiveData() = studyListLiveData

    fun getStudyListDetail() = studyListDetailLiveData

    /**
     * 获取学习室根目录
     */
    open fun getRootFolder() {
        launchUI {
            val rootFolder = studyRoomService?.rootFolder()
            if (rootFolder?.folder?.items != null) {
                //构建一个根目录文件夹item,添加到根目录list中
                val rootFolderItem = FolderItem("0", "学习室")
                rootFolder.folder.items?.add(0, rootFolderItem)
                studyListLiveData.postValue(rootFolder.folder.items as ArrayList<FolderItem>)
            }
        }
    }


    /**
     * 获取学习清单文件夹子目录
     */
    open fun getChildFolder(id: String) {
        launchUI {
            val childFolder = studyRoomService?.childeRolder(id)
            val arrayList = childFolder?.folder?.items
            studyListLiveData.postValue(arrayList)

        }
    }


    /**
     * 通过新接口
     * 获取学习清单文件夹子目录
     */
    open fun getChildFolderNew(id: String) {
        launchUI {
            val childFolder = studyRoomService?.newChildeRolder(id)
            val detail = childFolder?.data
            studyListDetailLiveData.postValue(detail)
        }
    }

    /**
     * 创建学习清单文件夹
     * @param name 文件夹名字
     * @param pid 父文件夹id，""为根目录，其他为具体子目录
     */
    fun createNewStudyFolder(name: String, pid: String = "",pop:BasePopupView) {
        launchUI {
            val requestData = JSONObject()
            requestData.put("name", name)
            requestData.put("pid", pid)
            val createNewFolder = studyRoomService?.createNewFolder(requestData)
//            studyListLiveData.postValue(createNewFolder)
            if (createNewFolder?.isSuccess == true) {
                //刷新文件夹，或者直接添加到内存中列表结尾
                pop.dismiss()
                if (pid.isEmpty()) {     //为空是根目录,需查询根目录接口
                    getRootFolder()

                    //学习室刷新清单文件夹
                    EventBus.getDefault().post(StudyRoomResourceChange(StudyRoomResourceChange.TYPE_FOLODER))

                } else {
//                    getChildFolder(pid)
                    getChildFolderNew(pid)
                }
            } else {
                //成功失败，都弹出提示
                toast(createNewFolder?.msg ?: "网络异常")
            }
        }
    }
}