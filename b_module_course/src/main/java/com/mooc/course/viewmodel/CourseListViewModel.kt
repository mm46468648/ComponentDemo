package com.mooc.course.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.course.CourseApi
import com.mooc.course.model.CourseChapter
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.xuetang.SequentialChildren
import com.mooc.commonbusiness.net.ApiService
import com.mooc.course.model.SequentialBean
import com.mooc.course.repository.CourseRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class CourseListViewModel : BaseListViewModel<SequentialBean>() {
    var xtCourseId = ""
    var currentPlaySequentialId = ""   //当前播放小节id
    var currentSequentialIndex = 0    //当前小节索引
    //仓库
    val mRepository : CourseRepository by lazy {
        CourseRepository
    }
    //章节信息（包含小节列表）
    val chapters : MutableLiveData<ArrayList<CourseChapter>> by lazy {
        val mutableLiveData = MutableLiveData<ArrayList<CourseChapter>>()
        mutableLiveData.value = arrayListOf()
        mutableLiveData
    }

    //小节详情中的分段视频集合（有可能是一段或者多段）
    val sequentialDetailChildrenList : MutableLiveData<List<SequentialChildren>> by lazy {
        MutableLiveData<List<SequentialChildren>>()
    }


    override suspend fun getData(): Deferred<List<SequentialBean>> {
        val async = viewModelScope.async {
            val await = ApiService.xtRetrofit.create(CourseApi::class.java).getCourseChapterList(xtCourseId).await()
            //章节列表数据
            val elements = await.chapters
            chapters.value?.addAll(elements)

            //将章节列表中的  小节列表重组 成一个大list
            val flatMapSequentiaList = await.chapters.flatMap { chapter ->
                chapter.sequentials.forEach { sequentia ->
                    sequentia.parentDisplayName = chapter.display_name
                }
                chapter.sequentials
            }

            //如果不为空，请求第一个章节信息
            if(flatMapSequentiaList.isNotEmpty()){
                getSequentDetailChildList(0,flatMapSequentiaList.first().id)
            }
            flatMapSequentiaList
        }
        return async
    }


    /**
     * 获取章节详情
     * 中的children集合
     * 集合中存储了多节视频信息
     *
     */
    fun getSequentDetailChildList(currentPosition:Int ,sequentialId : String){
        //避免重复请求，如果已选中则返回
        if(currentPlaySequentialId == sequentialId) return
        currentSequentialIndex = currentPosition
        currentPlaySequentialId = sequentialId
        launchUI {
            val sequentialDetail = mRepository.getSequentialDetail(xtCourseId, sequentialId)
            //过滤视频类型的集合,并取出chiildren集合
            val filter = sequentialDetail.verticals.filter {
                it.display_name == "视频"
            }.flatMap {
                it.children
            }

            sequentialDetailChildrenList.postValue(filter)
        }
    }

    /**
     * 切换下一个章节
     */
    fun changeNextSequential(){
        getPageData()?.value?.apply {
            if(currentSequentialIndex + 1 in this.indices){
                getSequentDetailChildList(++currentSequentialIndex,this[currentSequentialIndex].id)
            }
        }
    }


}