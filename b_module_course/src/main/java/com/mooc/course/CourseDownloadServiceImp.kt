package com.mooc.course

import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.reflect.TypeToken
import com.mooc.common.utils.FileUtils
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.model.course.BaseChapter
import com.mooc.commonbusiness.model.db.CourseDB
//import com.mooc.commonbusiness.model.search.ChaptersBean
import com.mooc.commonbusiness.model.studyroom.OldChapterBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.CourseDownloadService
import com.mooc.course.manager.db.CourseDbManger
import com.mooc.course.model.ChaptersBean
import com.mooc.course.model.ZHSChapter
import com.mooc.download.db.DownloadDatabase
import java.io.File
import kotlin.collections.ArrayList

@Route(path = Paths.SERVICE_COURSE_DOWNLOAD)
class CourseDownloadServiceImp : CourseDownloadService {
    override fun findAllDownloadCourse(): List<CourseDB>? {
        return CourseDbManger.findAllDownloadCourse()
    }

    override fun findDownloadChapter(courseId: String, classRoomID: String): List<BaseChapter> {
        val courseDB = CourseDbManger.findCourseById(courseId, classRoomID)
        val totalList = arrayListOf<BaseChapter>()
        if (courseDB?.platform == CoursePlatFormConstants.COURSE_PLATFORM_ZHS.toString()) {
            totalList.addAll(combileZHSChapter(courseDB))
        }

        if (courseDB?.platform == CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT.toString()) {
//            totalList.addAll(combileNewXtChapter(courseDB))
            totalList.addAll(combileXtChapter(courseDB))
        }

        //插入老版本同步过来的数据
        if (courseDB?.oldDownloadChapter?.isNotEmpty() == true) {
            val elements = combileFromOld(courseDB)
            //过滤掉已经下载的课程
            val filter = elements.filter {
                !totalList.map { new ->
                    new.name
                }.contains(it.name)
            }
            totalList.addAll(filter)
        }
        return totalList
//        return CourseDbManger.findDownloadChapter(courseId)
    }

    fun combileFromOld(courseDB: CourseDB): List<BaseChapter> {
        val fromJson = GsonManager.getInstance()
            .fromJson<ArrayList<ChaptersBean>>(
                courseDB.oldDownloadChapter,
                object : TypeToken<ArrayList<OldChapterBean>>() {}.type
            )

        //同步查询相应的下载信息数据，没有下载就移除
        val iterator = fromJson.iterator()
        while (iterator.hasNext()) {
            val item: BaseChapter = iterator.next()
//            loge("activityStack : " + item.get()?.localClassName)
            val downloadModle = DownloadDatabase.database?.getDownloadDao()
                ?.getDownloadModle(item.generateDownloadId(courseDB.courseId, courseDB.classRoomID))
            if (downloadModle == null) {
                iterator.remove()
            }
        }
        return fromJson
    }

//    private fun combileNewXtChapter(courseDB: CourseDB): List<BaseChapter> {
//        //对小章节进行组装，并设置大章节名字
//        val baseChapterList = arrayListOf<BaseChapter>()
////        val chapterList = GsonManager.getInstance().convert(courseDB.chapters,ArrayList<ZHSChapter>()::class.java)
//        val chapterList =
//            GsonManager.getInstance().fromJson<ArrayList<ChaptersBean>>(
//                courseDB.chapters,
//                object : TypeToken<ArrayList<ChaptersBean>>() {}.type
//            )
//
//        chapterList.forEach { chapter ->
//            //设置数据库需要用到的字段
//            baseChapterList.add(chapter)
//            chapter.section_list?.forEach {  //如果视频集合不为空，则添加这个章节
//                if (it.leaf_list?.isNotEmpty() == true) {
//                    //设置数据库需要用到的字段
//                    it.chapterName = chapter.name
//                    it.level = 1
//                    baseChapterList.add(it)
//                }
//            }
//        }
//
//
//        //同步查询相应的下载信息数据，没有下载就移除
//        val iterator = baseChapterList.iterator()
//        while (iterator.hasNext()) {
//
//            val item: BaseChapter = iterator.next()
////            loge("activityStack : " + item.get()?.localClassName)
//            val downloadModle = DownloadDatabase.database?.getDownloadDao()
//                ?.getDownloadModle(item.generateDownloadId(courseDB.courseId, courseDB.classRoomID))
//            if (downloadModle == null) {
//                iterator.remove()
//            }
//        }
//        return baseChapterList
//    }

    private fun combileXtChapter(courseDB: CourseDB): List<BaseChapter> {
        //对小章节进行组装，并设置大章节名字
        val baseChapterList = arrayListOf<BaseChapter>()
//        val chapterList = GsonManager.getInstance().convert(courseDB.chapters,ArrayList<ZHSChapter>()::class.java)
        val chapterList =
            GsonManager.getInstance().fromJson<ArrayList<ChaptersBean>>(
                courseDB.chapters,
                object : TypeToken<ArrayList<ChaptersBean>>() {}.type
            )

        chapterList.forEach { chapter ->
            //设置数据库需要用到的字段
            baseChapterList.add(chapter)
            //是否有第三级结构
            if(chapter.section_list?.isNotEmpty()==true){
                chapter.section_list.forEach { sectionList->
                    sectionList.level = 1
                    baseChapterList.add(sectionList)
                    if (sectionList.leaf_list?.isNotEmpty() == true) {
                        sectionList.leaf_list?.forEach { leafList->
                            leafList.level = 2
                            baseChapterList.add(leafList)
                        }
                    }
                }
            }else{
                //只有二级结构
                if (chapter.leaf_list?.isNotEmpty() == true) {
                    chapter.leaf_list?.forEach { leafList->
                        leafList.level = 1
                        baseChapterList.add(leafList)
                    }
                }
            }
        }





        //同步查询相应的下载信息数据，没有下载就移除
        val iterator = baseChapterList.iterator()
        while (iterator.hasNext()) {

            val item: BaseChapter = iterator.next()
//            loge("activityStack : " + item.get()?.localClassName)
            val downloadModle = DownloadDatabase.database?.getDownloadDao()
                ?.getDownloadModle(item.generateDownloadId(courseDB.courseId, courseDB.classRoomID))
            if (downloadModle == null) {
                iterator.remove()
            }
        }
        return baseChapterList
    }

    fun combileZHSChapter(courseDB: CourseDB): List<BaseChapter> {
        //对小章节进行组装，并设置大章节名字
        val baseChapterList = arrayListOf<BaseChapter>()
//        val chapterList = GsonManager.getInstance().convert(courseDB.chapters,ArrayList<ZHSChapter>()::class.java)
        val chapterList =
            GsonManager.getInstance().fromJson<ArrayList<ZHSChapter>>(
                courseDB.chapters,
                object : TypeToken<ArrayList<ZHSChapter>>() {}.type
            )
        chapterList.forEach { chapter ->
            chapter.lessonInfo.forEach { lesson ->

                if (lesson.lessonVideoInfo != null) {    //如果有第三极结构

                    lesson.lessonVideoInfo?.forEach {
                        it.lessonName = lesson.name
                        it.chapterName = chapter.name
//                            it.courseBean = courseBean
                        baseChapterList.add(it)
                    }
                } else {   //没有第三级
                    lesson.lessonName = chapter.name
//                        lesson.courseBean = courseBean
                    baseChapterList.add(lesson)
                }
            }
        }


        //同步查询相应的下载信息数据，没有下载就移除
        val iterator = baseChapterList.iterator()
        while (iterator.hasNext()) {

            val item: BaseChapter = iterator.next()
//            loge("activityStack : " + item.get()?.localClassName)
            val downloadModle = DownloadDatabase.database?.getDownloadDao()
                ?.getDownloadModle(item.generateDownloadId(courseDB.courseId, courseDB.classRoomID))
            if (downloadModle == null) {
                iterator.remove()
            }
        }
        return baseChapterList
    }

    override fun deleteCourse(courseBean: CourseDB) {
        //将文件夹都删除，
        var filePath =
            DownloadConfig.courseLocation + "/${courseBean.platform}" + "/${courseBean.courseId}"
        if (courseBean.classRoomID.isNotEmpty()) {
            filePath += "/${courseBean.classRoomID}"
        }
        val file = File(filePath)
        FileUtils.deleteByParentPath(file)
        // 删除缓存数据库中的数据
        deleteChapters(
            findDownloadChapter(courseBean.courseId, courseBean.classRoomID),
            courseBean.courseId,
            courseBean.classRoomID
        )
        // 删除课程数据库
        CourseDbManger.deleteCourse(courseBean)
    }

    override fun deleteChapters(
        chapters: List<BaseChapter>,
        courseId: String,
        classRoomId: String
    ) {
        chapters.forEach {
            val downloadModle = DownloadDatabase.database?.getDownloadDao()
                ?.getDownloadModle(it.generateDownloadId(courseId, classRoomId))


            downloadModle?.let { it1 ->
                val file = File(downloadModle.downloadPath, downloadModle.fileName)
                if (file.exists()) {
                    file.delete()
                }
                DownloadDatabase.database?.getDownloadDao()?.delete(it1)
            }
        }
    }

    /**
     * 从老的数据库同步过来的
     * 如果没有
     */
    override fun insertCourseDb(courseDB: CourseDB) {
        CourseDbManger.insertCourse(courseDB)
    }
}