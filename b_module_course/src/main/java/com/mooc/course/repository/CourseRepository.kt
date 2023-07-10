package com.mooc.course.repository

import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.model.xuetang.GetCourseEnrollDataBean
import com.mooc.commonbusiness.model.xuetang.SequentialPlayList
import com.mooc.commonbusiness.model.xuetang.VerifyStatusBean
import com.mooc.commonbusiness.model.xuetang.VideoUrl
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.course.CourseApi
import com.mooc.course.model.*
import okhttp3.RequestBody
import org.json.JSONObject

//import com.mooc.commonbusiness.model.search.ChaptersBean

object CourseRepository : BaseRepository() {

    val courseApi = ApiService.getRetrofit().create(CourseApi::class.java)
    suspend fun getCourseDetail(courseId: String): CourseDetail {
        return request {
            courseApi.getCourseDetail(courseId).await()
        }
    }

    /**
     * 获取视频播放地址，通过章节source
     */
    suspend fun getVideoPlayUrl(sequentialSource: String): VideoUrl {
        return request {
            HttpService.xtApi.getVideoUrl(sequentialSource).await()
        }
    }

    suspend fun getCoursePlayData(courseId: String): CourseBean {
        return request {
            HttpService.xtApi.getCourseEnrollDetail(courseId).await()
        }
    }

    /**
     * 同步课程
     */
    suspend fun postRedisCourse(courseId: String) {
        request {
            courseApi.postRedisCourse(courseId)
        }
    }

    /**
     * 选课
     */
    suspend fun selectionCourse(courseId: String): HttpResponse<Any> {
        return request {
            courseApi.selectionCourse(courseId).await()
        }
    }

    /**
     * 选课
     */
    suspend fun selectionNewXtCourse(courseId: String, classRoom: String): HttpResponse<Any> {
        return request {
            courseApi.addXtCourseToStudyRoom(courseId, classRoom).await()
        }
    }

    /**
     * 旧主站订阅课程
     * 现在不需要调用了
     */
    @Deprecated("")
    suspend fun enrollCourse(courseId: String): GetCourseEnrollDataBean {
        return request {
            HttpService.xtApi.postCourseEnroll(courseId).await()
        }
    }

    /**
     * 获取小节详情
     */
    suspend fun getSequentialDetail(xtCourseId: String, sequentialId: String): SequentialPlayList {
        return request {
            HttpService.xtApi.getSequenceDetail(xtCourseId, sequentialId).await()
        }
    }

    /**
     * 获取课程认证状态
     */
    suspend fun getCourseVerifyStatus(xtCourseId: String): HttpResponse<VerifyStatusBean> {
        return request {
            HttpService.xtApi.getCourseVerifyStatus(xtCourseId).await()
        }
    }


    /**
     * 获取智慧树章节
     */
    suspend fun getZHSCourseChapter(courseId: String): HttpResponse<ZHSChapterData> {
        return request {
            courseApi.getZHSChapters(courseId).await()
        }
    }

    suspend fun getZHSExamData(courseId: String): HttpResponse<ZHSExam> {
        return request {
            courseApi.getZHSExamData(courseId).await()
        }
    }

    suspend fun postCourseScore(body: RequestBody) {
        return request {
            courseApi.postCourseScoreAsync(body).await()
        }
    }

    suspend fun getXtCourseStatus(courseId: String, classRoomId: String): HttpResponse<XtCourseStatus> {
        return request {
            courseApi.getXtCourseState(courseId, classRoomId).await()
        }
    }

    suspend fun getXtCourseDownloadList(classRoomId: String): HttpResponse<List<ChaptersBean>> {
        return request {
            courseApi.getXtCourseDownloadList(classRoomId).await()
        }
    }

    suspend fun getXtCourseVideoMessage(classRoomId: String, videoId: String): HttpResponse<XtCourseVideoMessageBean> {
        return request {
            courseApi.getXtCourseVideoMessage(classRoomId, videoId).await()
        }
    }

    suspend fun postZHSCourseProcess(courseId: String) {
        request {
            val requestData = JSONObject()
            requestData.put("course_id", courseId)
            courseApi.zhsCourseProcess(RequestBodyUtil.fromJson(requestData))
        }
    }

    suspend fun getCourseAppraise(resource_type: String): HttpResponse<ArrayList<CourseScoreBean>> {
        return request {
            courseApi.getAppraise(resource_type).await()
        }
    }

    suspend fun postCourseAppraise(body: RequestBody): HttpResponse<Any> {
        return request {
            courseApi.postCourseAppraise(body).await()
        }
    }

}