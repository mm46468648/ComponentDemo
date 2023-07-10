package com.mooc.course.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.course.manager.VideoPointManger
import com.mooc.course.manager.db.CourseDbManger
import com.mooc.course.model.LessonInfo
import com.mooc.course.repository.CourseRepository
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.GsonManager
import com.mooc.common.utils.aesencry.AesEncryptUtil
import com.mooc.commonbusiness.model.db.CourseDB
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.course.model.ZHSChapter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ZHSCourseWareViewModel : BaseListViewModel<LessonInfo>() {

    var courseId: String = ""
    val mRepository = CourseRepository
    var currentSequentialIndex = 0    //当前小节索引
    var playUrl = MutableLiveData<String>()
    var playUrlAndPosition = MutableLiveData<Pair<String, Int>>()
    var courseBean: CourseBean? = null

    var chapterList = arrayListOf<LessonInfo>()

    var lastPlayChapterIndex = MutableLiveData<Int>()
//    var chapterResponse : ZHSChapterData? = null

    var isFirstLoad = true    //首次加载，自动播放，再次刷新，就不需要了
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
                if(chapter.lessonInfo != null){
                    chapter.lessonInfo.forEach { lesson ->
                        //第二级
                        if (lesson.lessonVideoInfo == null) {
                            lesson.level = 2
                            lesson.chapterId = chapter.chapterId
                            chapterList.add(lesson)
                        } else {

                            lesson.level = 1
                            chapterList.add(lesson)

                            //第三级
                            lesson.lessonVideoInfo?.forEach { lessonInfo ->
                                lessonInfo.level = 2
                                lessonInfo.chapterId = chapter.chapterId
                                //第三级携带lessonid,打点需要
                                lessonInfo.lessonId = lesson.lessonId
                                chapterList.add(lessonInfo)
                            }

                        }
                    }
                }
            }


            //获取上次播放的章节id
            val findCourseById = CourseDbManger.findCourseById(courseId, "")
            val lastLessonId = findCourseById?.lastPlayChapterId ?: ""
//            lastPlayChapterId.postValue(lastLessonId)

            //获取播放的lesson位置
            for (i in chapterList.indices) {
                val lessonInfo = chapterList[i]
                if (lastLessonId.isNotEmpty()) {
                    if (lessonInfo.id == lastLessonId) {
                        currentSequentialIndex = i
                        break
                    }
                } else {
                    if (lessonInfo.videoUrl?.isNotEmpty() == true) {
                        currentSequentialIndex = i
                        break;
                    }
                }
            }

            lastPlayChapterIndex.postValue(currentSequentialIndex)



            VideoPointManger.initChapterInfo(
                courseId, zhsCourseChapter.data.videoNum
                    ?: "", zhsCourseChapter.data.ts ?: "", zhsCourseChapter.data?.token ?: ""
            )


            //发送应该播放的url
            if (isFirstLoad && currentSequentialIndex < chapterList.size) {
                val lessonInfo = chapterList[currentSequentialIndex]
                //视频打点，初始化章节信息
                onChapterChange(lessonInfo)
                isFirstLoad = false
            }
            chapterList
        }
    }

    fun saveLastPlayChapter(lessonInfo: LessonInfo) {
        //如果lessonVideoId 不为空，则记录lessonVideoId
        CourseDbManger.findCourseById(courseId, "")?.let {
            it.lastPlayChapterId = lessonInfo.id
            CourseDbManger.updateCourse(it)
        }
    }

    /**
     * 改变视频打点信息
     */
    fun changeVideoPointInfo(lessonInfo: LessonInfo) {
        VideoPointManger.changeLesson(lessonInfo)
    }

    /**
     * 点击列表切换章节
     */
    fun changeChapter(index: Int) {
        if (index !in chapterList.indices) return
        val lessonInfo = chapterList[index]
        if (lessonInfo.videoUrl?.isNotEmpty() == true) {
            currentSequentialIndex = index
            onChapterChange(lessonInfo)
        }
    }

    private fun onChapterChange(lessonInfo: LessonInfo) {
        lessonInfo.videoUrl?.let { it ->
            changeVideoPointInfo(lessonInfo)
            getVideoPlayUrlAndPosition(lessonInfo)
            saveLastPlayChapter(lessonInfo)
        }
    }

    /**
     * 切换下一章节
     */
    fun changeNextSequential() {
        //查找下一个可播放的章节
        chapterList.forEachIndexed { index, lessonInfo ->
            if (index > currentSequentialIndex && lessonInfo.videoUrl?.isNotEmpty() == true) {
                currentSequentialIndex = index
                onChapterChange(lessonInfo)
                return@forEachIndexed
            }
        }
    }

    /**
     * 通过AES解密，获取实际的播放地址
     */
    private fun getVideoPlayUrl(videoUrl: String) {
        if (videoUrl.isEmpty()) return
        val xoRdecode = AesEncryptUtil.XODecode(videoUrl)
        loge(xoRdecode)
        //发送应该播放的url
        playUrl.postValue(xoRdecode)
    }


    /**
     * 通过AES解密，获取实际的播放地址
     */
    private fun getVideoPlayUrlAndPosition(lessonInfo: LessonInfo) {
        if (lessonInfo.videoUrl?.isEmpty() == true) return
        val xoRdecode = AesEncryptUtil.XODecode(lessonInfo.videoUrl)
        val currentLessonPlayPosition = getCurrentLessonPlayPosition(lessonInfo.id)
        loge("get lessonId: ${lessonInfo.id} playPosition: ${currentLessonPlayPosition}")
        //发送应该播放的url
        playUrlAndPosition.postValue(Pair(xoRdecode, currentLessonPlayPosition))
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
     * 保存当前章节播放位置
     * 等于0的时候也不保存
     * @param i playPosition
     * todo 加一个缓存优化查询效率
     */
    fun saveCurrentLessonPlayPosition(i: Int) {
        if (currentSequentialIndex !in chapterList.indices || i <= 0) return

        val currentLessonInfo = chapterList[currentSequentialIndex]

        loge("save LessonId: ${currentLessonInfo.id}  currentPlayPosition: ${i}")
        val let = CourseDbManger.findCourseById(courseId, "")?.let {
            val convert = GsonManager.getInstance().fromJson<List<ZHSChapter>>(
                it.chapters,
                object : TypeToken<ArrayList<ZHSChapter>>() {}.type
            )
            //对小章节进行组装，并设置大章节名字
            convert.forEach { chapter ->
                chapter.lessonInfo.forEachIndexed { index, lesson ->
                    //第二级
                    if (lesson.lessonVideoInfo == null) {
                        if (currentLessonInfo.id == lesson.id) {
                            lesson.lastPlayPosition = i
                        }
                    } else {
                        //第三级
                        lesson.lessonVideoInfo?.forEach { lessonInfo ->
                            if (currentLessonInfo.id == lessonInfo.id) {
                                lessonInfo.lastPlayPosition = i
                            }
                        }
                    }
                }
            }

            val toJson = GsonManager.getInstance().toJson(convert)
            it.chapters = toJson
            it
        }
        let?.let {
            CourseDbManger.updateCourse(it)
        }

    }

    /**
     * 获取当前章节播放位置
     * todo 加一个缓存优化查询效率
     */
    fun getCurrentLessonPlayPosition(lessonId: String): Int {
        val realPosition = CourseDbManger.findCourseById(courseId, "")?.let {
            var position = 0
            val convert = GsonManager.getInstance().fromJson<List<ZHSChapter>>(it.chapters,
                object : TypeToken<ArrayList<ZHSChapter>>() {}.type
            )
            //对小章节进行组装，并设置大章节名字
            convert.forEach { chapter ->
                chapter.lessonInfo.forEachIndexed { index, lesson ->
                    //第二级
                    if (lesson.lessonVideoInfo == null) {
                        if (lessonId == lesson.id) {
                            position = lesson.lastPlayPosition
                        }
                    } else {
                        //第三级
                        lesson.lessonVideoInfo?.forEach { lessonInfo ->
                            if (lessonId == lessonInfo.id) {
                                position = lessonInfo.lastPlayPosition
                            }
                        }
                    }
                }
            }
            position
        }
        return realPosition ?: 0
    }

}