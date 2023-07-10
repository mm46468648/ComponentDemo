package com.mooc.course.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.course.repository.CourseRepository
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.model.xuetang.SequentialChildren
import com.mooc.course.model.GradePolicy

/**
 * @param xtCourseId 学堂接口中使用的课程id
 */
class CoursePlayViewModel(var xtCourseId: String) : BaseViewModel() {

    var mRepository = CourseRepository


    //课程详情
    val coursePlayLiveData: MutableLiveData<CourseBean> by lazy {
        MutableLiveData<CourseBean>()
    }

    var childerIndex = 0 //单章小节播放索引 , (只属于单章小节，不是所有章节列表索引

    val currentPlayPosition = MutableLiveData<Int>()      //当前视频播放位置


    //小节详情中的分段视频集合（有可能是一段或者多段）
    val currentSequentialDetailChildrenList: MutableLiveData<List<SequentialChildren>> by lazy {
        MutableLiveData<List<SequentialChildren>>()
    }

    //视频播放信息
    val videlUrlStringData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    //视频播放地址和最近播放位置
    val videlUrlAndPositionData: MutableLiveData<Pair<String,Int>> by lazy {
        MutableLiveData<Pair<String,Int>>()
    }

    //视频播放结束动态
    val videoEndLiveData : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    //等级政策，（考试得分数据）
    val courseGradePolicy = MutableLiveData<GradePolicy>()

    //课程播放数据
    fun getCoursePlayData() = coursePlayLiveData


    //获取课程播放数据（标题等）
    fun getData() {
//        launchUI {
//            val coursePlayData = mRepository.getCoursePlayData(xtCourseId)
//            postStudyLog(coursePlayData)
//            coursePlayLiveData.postValue(coursePlayData)
//        }

//        postStudyLog(coursePlayData)
    }



    /**
     * 服务器获取课程小节视频播放地址
     * @param sequentialSource 章节id
     */
    fun getSequentialPlayUrl(sequentialSource: String) {
        launchUI {
            val sequentialPlayUrl = mRepository.getVideoPlayUrl(sequentialSource)
            if (sequentialPlayUrl.sources.isNotEmpty()) {
                videlUrlStringData.postValue(sequentialPlayUrl.sources.first())
            }
        }
    }

    /**
     * 获取下一个小节视频播放地址
     * todo 智慧树，目前还没有支持自动切换到下一个章节
     */
    fun getNextUrl(){

        currentSequentialDetailChildrenList.value?.apply {
            if((childerIndex + 1) in this.indices){
                getSequentialPlayUrl(this[++childerIndex].source)
                return
            }
            //在fragment列表中切换下一个章节
            videoEndLiveData.postValue(true)
        }
    }


    /**
     * 同步智慧树课程进度
     */
    fun postSynchronizeZHSCourseProcess(courseId:String){
        launchUI {
            mRepository.postZHSCourseProcess(courseId)
        }
    }
}