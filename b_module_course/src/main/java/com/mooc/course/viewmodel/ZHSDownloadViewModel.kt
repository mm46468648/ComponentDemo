package com.mooc.course.viewmodel

import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.utils.GsonManager
import com.mooc.common.utils.aesencry.AesEncryptUtil
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.model.db.CourseDB
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.route.routeservice.CourseDownloadService
import com.mooc.course.manager.db.CourseDbManger
import com.mooc.course.model.LessonInfo
import com.mooc.course.model.ZHSChapter
import com.mooc.course.repository.CourseRepository
import com.mooc.download.db.DownloadDatabase
import com.mooc.newdowload.DownloadInfo
import com.mooc.newdowload.DownloadInfoBuilder
import com.mooc.newdowload.DownloadManager
import com.mooc.newdowload.State
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ZHSDownloadViewModel : BaseListViewModel<LessonInfo>() {

    val mRepository = CourseRepository
    var courseId: String = ""
    var courseBean: CourseBean? = null

    var chapterList = arrayListOf<LessonInfo>()

    override suspend fun getData(): Deferred<List<LessonInfo>?> {
        chapterList.clear()
        return viewModelScope.async {
            val zhsCourseChapter = mRepository.getZHSCourseChapter(courseId)

            //智慧树将课程信息插入数据库（记录播放位置，和下载）
            insertCourseToDb(zhsCourseChapter.data.chapterList)

            //对小章节进行组装，并设置大章节名字
            zhsCourseChapter.data.chapterList.forEachIndexed { index, chapter ->
                //第一级
                val lessonInfo = LessonInfo()
                lessonInfo.name = chapter.name
                lessonInfo.level = 0
                chapterList.add(lessonInfo)
                chapter.lessonInfo.forEach { lesson ->
//                    if(chapterList.size >= 3) return@forEachIndexed
                    //第二级
                    if (lesson.lessonVideoInfo == null) {
                        lesson.level = 2
                        chapterList.add(lesson)
                    } else {

                        lesson.level = 1
                        chapterList.add(lesson)

                        //第三级
                        lesson.lessonVideoInfo?.forEach { lessonInfo->
                            lessonInfo.level = 2
                            chapterList.add(lessonInfo)
                        }

                    }
                }

            }


            //同步查询相应的下载信息数据
            chapterList.forEachIndexed { index, lessonInfo ->
                if (lessonInfo.videoUrl?.isNotEmpty()==true) {
                    initDownloadInfo(lessonInfo)
                }
            }
            chapterList
        }

    }

    fun initDownloadInfo(lessonInfo: LessonInfo) {
        val key = lessonInfo.generateDownloadId(this.courseId)
        var downLoadInfo: DownloadInfo? = DownloadManager.DOWNLOAD_INFO_HASHMAP.get(key)
        if (downLoadInfo == null) {
            downLoadInfo = loadFromDB(lessonInfo)
            if (downLoadInfo == null) {
                downLoadInfo = createDownloadInfo(lessonInfo)
            }else{
                //有的时候状态是进行中，要重置成暂停状态
                if(downLoadInfo.state == State.DOWNLOADING || downLoadInfo.state == State.DOWNLOAD_WAIT){
                    downLoadInfo.state = State.DOWNLOAD_STOP
                }
            }
        }
        lessonInfo.downloadInfo = downLoadInfo

    }

    fun createDownloadInfo(sequentialBean: LessonInfo): DownloadInfo {
        val realVideoUrl = AesEncryptUtil.XODecode(sequentialBean.videoUrl)
        val courseId = this.courseId
        return DownloadInfoBuilder()
            .setDownloadID(sequentialBean.generateDownloadId(courseId))
            .setDownloadUrl(realVideoUrl)
            .setFilePath(DownloadConfig.courseLocation + "/" + CoursePlatFormConstants.COURSE_PLATFORM_ZHS + "/" + courseId)
            .setFileName(sequentialBean.name)
            .build()
    }

    private fun loadFromDB(sequentialBean: LessonInfo): DownloadInfo? {
        return DownloadDatabase.database?.getDownloadDao()
            ?.getDownloadModle(sequentialBean.generateDownloadId(courseId))
    }

    private fun insertCourseToDb(zhsCourseChapter: List<ZHSChapter>) {
        courseBean?.apply {
            val courseDB = CourseDB()
            courseDB.courseId = this.course_id
            courseDB.classRoomID = this.classroom_id
            courseDB.platform = this.platform.toString()
            courseDB.cover = this.picture
            courseDB.name = this.title
            courseDB.chapters = GsonManager.getInstance().toJson(zhsCourseChapter)

            CourseDbManger.insertCourse(courseDB)
        }
    }


    /**
     * 删除选中的下载章节
     * 并通知刷新
     */
    fun deleteSelectDownloadChapter(mAdapter: BaseQuickAdapter<LessonInfo, BaseViewHolder>?) {
        val filter = chapterList.filter {
            it.deleteDownloadSelect
        }

        val navigation = ARouter.getInstance().navigation(CourseDownloadService::class.java)
        navigation.deleteChapters(filter,courseId,"")


        filter.forEach {
            it.downloadInfo?.downloadSize = 0
            it.downloadInfo?.state = State.DOWNLOAD_NOT
            it.deleteDownloadSelect = false
        }
        mAdapter?.notifyDataSetChanged()


    }
}