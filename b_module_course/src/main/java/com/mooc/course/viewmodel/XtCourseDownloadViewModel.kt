package com.mooc.course.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
//import com.mooc.commonbusiness.model.search.ChaptersBean
import com.mooc.course.repository.CourseRepository
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.model.db.CourseDB
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.course.manager.db.CourseDbManger
import com.mooc.course.model.ChaptersBean
import com.mooc.download.DownloadModel
import com.mooc.download.db.DownloadDatabase
import com.mooc.newdowload.DownloadInfo
import com.mooc.newdowload.DownloadManager
import com.mooc.newdowload.State
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**
 * 学堂课程下载列表
 * (新版,支持二级和三级的章节结构)
 */
class XtCourseDownloadViewModel : BaseListViewModel<ChaptersBean>() {
    var classRoomId = ""
    var courseBean : CourseBean? = null
    var newChapterList = arrayListOf<ChaptersBean>()

    override suspend fun getData(): Deferred<List<ChaptersBean>> {
        return viewModelScope.async {
            val xtCourseDownloadList = CourseRepository.getXtCourseDownloadList(classRoomId).data

            //智慧树将课程信息插入数据库（记录播放位置，和下载）
            insertCourseToDb(xtCourseDownloadList)

            newChapterList.clear()
            xtCourseDownloadList.forEach { chapter ->
                //设置数据库需要用到的字段
                chapter.level = 0
                newChapterList.add(chapter)


                //是否有第三级结构
                if(chapter.section_list?.isNotEmpty()==true){

                    chapter.section_list.forEach { sectionList->
                        sectionList.level = 1
                        newChapterList.add(sectionList)

                        if (sectionList.leaf_list?.isNotEmpty() == true) {
                            sectionList.leaf_list?.forEach { leafList->
                                //设置数据库需要用到的字段
                                leafList.courseId = courseBean?.id?:""
                                leafList.classRoomId = classRoomId
                                leafList.level = 2
                                newChapterList.add(leafList)
                            }
                        }
                    }
                }else{
                    //只有二级结构
                    if (chapter.leaf_list?.isNotEmpty() == true) {
                        chapter.leaf_list?.forEach { leafList->
                            //设置数据库需要用到的字段
                            leafList.courseId = courseBean?.id?:""
                            leafList.classRoomId = classRoomId
                            leafList.level = 1
                            newChapterList.add(leafList)
                        }
                    }
                }
            }
            //设置视频章节的当前状态
            newChapterList.forEach {chapter->
                if(chapter.type == 0){
                    setDownloadState(chapter)
                }
            }
            newChapterList
        }
    }

    fun setDownloadState(item: ChaptersBean) {

        var downLoadInfo: DownloadInfo? = DownloadManager.DOWNLOAD_INFO_HASHMAP.get(item.generateDownloadId(item.courseId,item.classRoomId))
        if (downLoadInfo == null) {
            downLoadInfo = loadFromDb(item)
//            if (downLoadInfo?.state == DownloadModel.STATUS_DOWNLOADING || downLoadInfo?.state == DownloadModel.STATUS_WAIT) {
//                downLoadInfo.state = DownloadModel.STATUS_PAUSED
//            }
        }
        if(downLoadInfo == null){
            downLoadInfo = createDownloadInfo(item)
        }
//        DownloadManager.DOWNLOAD_INFO_HASHMAP.put(downLoadInfo.id, downLoadInfo)
        //有的时候状态是进行中，要重置成暂停状态
        if(downLoadInfo.state == State.DOWNLOADING || downLoadInfo.state == State.DOWNLOAD_WAIT){
            downLoadInfo.state = State.DOWNLOAD_STOP
        }
        item.downloadModel = downLoadInfo

    }
    /**
     * 初始化下载
     */
    private fun loadFromDb(item: ChaptersBean)  : DownloadInfo?{
        val downloadModle =
            DownloadDatabase.database?.getDownloadDao()?.getDownloadModle(item.generateDownloadId(item.courseId,item.classRoomId))


        return downloadModle
//        changeUI(downloadModle)

    }

    private fun createDownloadInfo(item: ChaptersBean): DownloadInfo {
        val downloadInfo = DownloadInfo()
        downloadInfo.id = item.generateDownloadId(item.courseId,item.classRoomId)
        downloadInfo.downloadPath = DownloadConfig.courseLocation+
                "/${CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT}" +"/${item.courseId}"+
                "/${item.classRoomId}"
        downloadInfo.state = State.DOWNLOAD_NOT
        downloadInfo.fileName = item.name
        return downloadInfo
    }



    private fun insertCourseToDb(zhsCourseChapter: List<ChaptersBean>) {
        courseBean?.apply {
            val courseDB = CourseDB()
            courseDB.courseId = this.id
            courseDB.classRoomID = this.classroom_id
            courseDB.platform = this.platform.toString()
            courseDB.cover = this.picture
            courseDB.name = this.title
            courseDB.chapters = GsonManager.getInstance().toJson(zhsCourseChapter)

            CourseDbManger.insertCourse(courseDB)
        }
    }


    fun getCourseVideoPath(chaptersBean: ChaptersBean,videoId: String) :LiveData<String>{
        val livePath = MutableLiveData<String>()
        launchUI {
            try {
                val xtCourseVideoMessage = CourseRepository.getXtCourseVideoMessage(classRoomId, videoId)
                xtCourseVideoMessage.data.sources?.quality10?.get(0)?.let {
                    livePath.postValue(it)
                }
            }catch (e : Exception){
                loge(e.toString())
            }

        }
        return livePath
    }

}

