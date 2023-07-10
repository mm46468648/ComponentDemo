package com.mooc.studyroom.viewmodel

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.reflect.TypeToken
import com.mooc.common.global.AppGlobals
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.model.db.CourseDB
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.route.routeservice.CourseDownloadService
import com.mooc.download.db.DownloadDatabase
import com.mooc.newdowload.DownloadInfo
import com.mooc.studyroom.db.OldDbSearchManager
import com.mooc.studyroom.db.OldDownloadDatabaseHelper
import com.mooc.commonbusiness.model.studyroom.OldChapterBean
import com.mooc.newdowload.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.collections.ArrayList

class CourseDwonloadViewModel : BaseListViewModel2<CourseDB>() {

    override suspend fun getData(): Flow<List<CourseDB>?> {

        return flow<List<CourseDB>> {
            queryAllDwonloadCourseData()
            val navigation = ARouter.getInstance().navigation(CourseDownloadService::class.java)
            //通过数据库查询每个课程之下的视频文件数量，与大小

            val value = navigation.findAllDownloadCourse() ?: arrayListOf()

            value.forEach { course ->
                val courseDownloadService =
                    ARouter.getInstance().navigation(CourseDownloadService::class.java)
                val findDownloadChapter =
                    courseDownloadService.findDownloadChapter(course.courseId, course.classRoomID)

                var fileNum = 0
                var fileSize = 0L
                for (chapter in findDownloadChapter ?: arrayListOf()) {
                    val downloadModle = DownloadDatabase.database?.getDownloadDao()
                        ?.getDownloadModle(
                            chapter.generateDownloadId(
                                course.courseId,
                                course.classRoomID
                            )
                        )

                    if (downloadModle?.state == State.DOWNLOAD_COMPLETED) {
                        val size = downloadModle.size
                        fileNum++
                        fileSize += size
                    }
                }

                course.totalNum += fileNum
                course.totalSize += fileSize
            }
            emit(value)
        }
    }


    /**
     * 从老版本数据库中查询
     */
    fun queryAllDwonloadCourseData() {
        val helper = OldDownloadDatabaseHelper(AppGlobals.getApplication())
        var cursor: Cursor? = null
        try {
            val db: SQLiteDatabase = helper.getReadableDatabase()
            // 查询所有数据
            cursor = db.query(
                OldDownloadDatabaseHelper.TABLE_COUSE_DOWNLOAD_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    parseCourseDownloadBean(cursor)
                }

                courseDBHashMap.values.forEach {
                    navigation.insertCourseDb(it)
                }

                //同步完毕，删除原来的数据库
                val sql =
                    "DROP TABLE IF EXISTS \"" + OldDownloadDatabaseHelper.TABLE_COUSE_DOWNLOAD_NAME + "\""
                db.execSQL(sql)
            }
        } catch (e: Exception) {
        } finally {
            cursor?.close()
        }
    }

    val courseDBHashMap = HashMap<String, CourseDB>()

    val navigation by lazy { ARouter.getInstance().navigation(CourseDownloadService::class.java) }

    fun deleteOldCourseDownloadDb(courseDB: CourseDB) {
        val helper = OldDownloadDatabaseHelper(AppGlobals.getApplication()).writableDatabase
        if (courseDB.classRoomID.isNotEmpty()) {
            helper.execSQL("DELETE FROM download_info where _id like '%" + courseDB.classRoomID + "'")
        } else {
            helper.execSQL("DELETE FROM download_info where _id like '%" + courseDB.name + "'")
        }
        helper.close()
    }

    private fun parseCourseDownloadBean(cursor: Cursor): CourseDB? {
        val id = cursor.getString(cursor.getColumnIndex("_id"))
        val uri = cursor.getString(cursor.getColumnIndex("uri"))
        val path = cursor.getString(cursor.getColumnIndex("path"))
        val status = cursor.getInt(cursor.getColumnIndex("status"))
        val size = cursor.getLong(cursor.getColumnIndex("size"))

        if (5 != status) return null

        if (!id.contains(",")) return null

        val split = id.split(",".toRegex()).toTypedArray()
        //id包括 cover，chapterName，id，courseName
        if (split.size < 4) return null

        val courseDB = CourseDB()
        courseDB.cover = split[0]
        val courseId = split[2]
        courseDB.courseId = courseId
        courseDB.name = split[3]

//        if (split.size > 4) { //新学堂的课，(这个字段不是章节id，这个是章节视频id)
//            courseDB.classRoomID = split[4]
//        }
        courseDB.haveDownload = true
        val chapters1 = split[1]

        //将chapters，和path，保存在一个json里面
        val oldChapterBean = OldChapterBean(chapters1, path)
        oldChapterBean.courseId = courseId
        oldChapterBean.classRoomId = courseDB.classRoomID

        if (!courseDBHashMap.containsKey(courseId)) {
            val arrayListOf = arrayListOf<OldChapterBean>()
            arrayListOf.add(oldChapterBean)
            courseDB.oldDownloadChapter = GsonManager.getInstance().toJson(arrayListOf)
            courseDB.totalNum = 1
            courseDB.totalSize = size
            courseDBHashMap[courseId] = courseDB
        } else {
            val courseDB1 = courseDBHashMap[courseId]
            if (courseDB1 != null) {
                val num1 = courseDB1.totalNum + 1
                val size1 = courseDB1.totalSize + size
                val chapterList = GsonManager.getInstance()
                    .fromJson<ArrayList<OldChapterBean>>(courseDB1.oldDownloadChapter,
                        object : TypeToken<ArrayList<OldChapterBean>>() {}.type
                    )
                chapterList.add(oldChapterBean)
                courseDB1.oldDownloadChapter = GsonManager.getInstance().toJson(chapterList)
                courseDB1.totalNum = num1
                courseDB.totalSize = size1
            }
        }


        //同步插入下载数据库
        //构建下载模型
        val downloadedsavefilepath = cursor.getString(cursor.getColumnIndex("path"))
        val downloadInfo = DownloadInfo()
        downloadInfo.id = oldChapterBean.generateDownloadId(courseId, courseDB.classRoomID)
        downloadInfo.state = 5
        downloadInfo.fileName = chapters1
        downloadInfo.downloadPath = downloadedsavefilepath
        DownloadDatabase.database!!.getDownloadDao().insert(downloadInfo)
        Log.e(OldDbSearchManager.TAG, "id: " + id + "uri: " + uri + "path: " + path)
        return courseDB
    }


}