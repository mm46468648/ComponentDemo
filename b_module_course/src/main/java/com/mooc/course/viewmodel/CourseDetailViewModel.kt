package com.mooc.course.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.base.BaseVmViewModel
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.course.CourseApi
import com.mooc.course.model.ChaptersBean
//import com.mooc.commonbusiness.model.search.ChaptersBean
import com.mooc.course.model.CourseDetail
import com.mooc.course.model.XtCourseStatus
import com.mooc.course.repository.CourseRepository
import org.json.JSONObject
import java.util.*

class CourseDetailViewModel(var courseId: String) :
        BaseVmViewModel<CourseDetail>(courseId, ResourceTypeConstans.TYPE_COURSE) {

    //    val repository: CourseRepository by lazy { CourseRepository() }
    val repository = CourseRepository

    /**
     * 新学堂课堂状态
     */
    val courseStatusLiveData = MutableLiveData<XtCourseStatus>()

    override fun getRepository(): BaseRepository {
        return repository
    }

    override fun block(): suspend () -> CourseDetail {
        return getCourseDetail()
    }

    private fun getCourseDetail(): suspend () -> CourseDetail {
        return {
            val courseDetail =
                    ApiService.getRetrofit().create(CourseApi::class.java).getCourseDetail(courseId).await()

            val courseDetail1 =
                    if (courseDetail.platform == CoursePlatFormConstants.COURSE_PLATFORM_MOOC) {
                        val await =
                                ApiService.getRetrofit().create(CourseApi::class.java).getMoocDetail(courseId)
                                        .await()
                        await.data.platform_zh = courseDetail.platform_zh
                        await.data.verified_active_info = courseDetail.verified_active_info
                        await.data.is_free_info = courseDetail.is_free_info
                        await.data.is_have_exam_info = courseDetail.is_have_exam_info
                        await.data
                    } else if (courseDetail.platform == CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT) {
                        ApiService.getRetrofit().create(CourseApi::class.java).getNewXtCourseDetail(courseId)
                                .await().data
                    } else {
                        courseDetail
                    }

//            getShareDetailData(ResourceTypeConstans.TYPE_COURSE.toString(),courseId)
            courseDetail1


        }
    }

    var newChapterlist: ArrayList<ChaptersBean> = arrayListOf<ChaptersBean>() //进行组装过后的章节列表

    /**
     * 获取章节列表（有可能多级）
     */
    fun getChapters(chapters: List<ChaptersBean>): List<ChaptersBean> {
        newChapterlist.clear()
        combinationList(chapters as ArrayList<ChaptersBean>, 0)
        return newChapterlist
    }

    /**
     * 一级一级遍历出所有章节
     * @param childList
     * @param level
     */
    private fun combinationList(childList: ArrayList<ChaptersBean>, level: Int) {
        for (i in childList.indices) {
            val chapter: ChaptersBean = childList[i]
            chapter.level = level
            newChapterlist.add(chapter)

            //新学堂多级章节列表，使用section_list字段
            val section_list: List<ChaptersBean>? = chapter.section_list
            if (section_list?.isNotEmpty() == true) {
                combinationList(section_list as ArrayList<ChaptersBean>, level + 1)
            }

            //旧学堂多级章节列表，使用sequentials字段
            val sequentials: List<ChaptersBean>? = chapter.sequentials
            if (sequentials?.isNotEmpty() == true) {
                combinationList(sequentials as ArrayList<ChaptersBean>, level + 1)
            }


        }
    }

    /**
     * 进入播放页面之前要
     * 同步课程
     */
    fun redisCourse(courseId: String) {
        launchUI {
            repository.postRedisCourse(courseId)
        }
    }

    /**
     * 选课
     */
    fun selectionCourse(courseId: String): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        launchUI {
            val selectionCourse = repository.selectionCourse(courseId)
            liveData.postValue(selectionCourse.isSuccess)
        }
        return liveData
    }


    /**
     * 新学堂课程选课
     */
    fun selectionNewXtCourse(courseId: String, classRoom: String): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        launchUI {
            val selectionCourse = repository.selectionNewXtCourse(courseId, classRoom)
            if ("加入成功" == selectionCourse.message) {
                courseStatusLiveData.value?.status = 9 //同步一下课程状态
                liveData.postValue(selectionCourse.isSuccess)
            } else {
                toast(selectionCourse.message)
            }
        }
        return liveData
    }

    /**
     * 订阅课程
     */
    fun enrollCourse(courseId: String): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        launchUI {
            try {
                val enrollCourse = repository.enrollCourse(courseId)
                liveData.postValue(true)
            } catch (e: Exception) {
                loge(e.toString())
            }
        }
        return liveData
    }

    fun getRecommendCourse(courseId: String): LiveData<MutableList<CourseBean>> {
        val liveData = MutableLiveData<MutableList<CourseBean>>()
        launchUI {
            val it = repository.courseApi.getRecommendCourse(2, courseId).await()
            liveData.postValue(it?.data)
        }
        return liveData;
    }


    /**
     * 添加到学习室文件夹
     * @param toFolderId 文件夹id
     * @param jsonObject 资源需要传递的参数
     */
    fun addToFolder(toFolderId: String, jsonObject: JSONObject): LiveData<HttpResponse<Any>> {
        val liveData = MutableLiveData<HttpResponse<Any>>()
        toFolderId.let {
            launchUI {
                val studyRoomService: StudyRoomService? =
                        ARouter.getInstance().navigation(StudyRoomService::class.java)
                val moveToFolder = studyRoomService?.moveToFolder(it, jsonObject)
                liveData.postValue(moveToFolder)
            }
        }
        return liveData
    }


    /**
     * 订阅课程公告
     * 提交一次改变一次上次订阅状态（由订阅改为未订阅，或者未订阅改为订阅）（第一次是订阅）
     */
    fun postEnrollUserSetting(courseId: String) {
        val requestData = JSONObject()
        requestData.put("course_id", courseId)

        launchUI {
            ApiService.getRetrofit().create(CourseApi::class.java)
                    .enrollUserSetting(RequestBodyUtil.fromJson(requestData)).await()
        }
    }

    /**
     * 查询学堂课程状态
     */
    fun queryXtCourseState(classRoomId: String) {
        launchUI {
            val xtCourseStatus = repository.getXtCourseStatus(courseId, classRoomId)
            courseStatusLiveData.postValue(xtCourseStatus.data)
        }
    }

}