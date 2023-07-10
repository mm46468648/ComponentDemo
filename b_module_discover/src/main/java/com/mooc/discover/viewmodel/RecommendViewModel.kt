package com.mooc.discover.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.constants.LoadStateConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.AnnouncementBean
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.*

/**
 * 发现推荐
 */
class RecommendViewModel : BaseViewModel() {

    var guessOffset = 0 //猜你喜欢分页offset
    val guessLimit = 10
    val guessLoadMoreStatus = MutableLiveData<Int>()

    //猜你喜欢
    val guessList by lazy {
        MutableLiveData<ArrayList<ResultBean>>().also {
            it.value = arrayListOf()
        }
    }
    val likeRecommend = MutableLiveData<Boolean>()

    //轮播图banner
    var bannerData = MutableLiveData<BannerBean>()

    //公告
    var noticeData = MutableLiveData<List<AnnouncementBean>>()

    //快速入口
    var quickEntry = MutableLiveData<List<QuickEntry>>()

    //专栏数据
    var columnData = MutableLiveData<List<RecommendColumn>>()

    var taskData = MutableLiveData<TaskBean>()


    fun getBanner() {
        launchUI {
            val banner = HttpService.discoverApi.getBannerData()
            bannerData.postValue(banner.data)
        }
    }

    fun getNoticeData() {
        launchUI {
            val noticeResponse = HttpService.discoverApi.getNoticeData().await()
            noticeData.postValue(noticeResponse.data)
        }
    }

    fun getRecommendDiscoverColumn() {
        launchUI {
            val column = HttpService.discoverApi.getRecommendDiscoverColumn().await()
            columnData.postValue(column.results)
        }
    }


    fun getQuickEntrys() {
        launchUI {
            val entry = HttpService.discoverApi.getQuickEntry().await()
            quickEntry.postValue(entry.data)
        }
    }


    fun getTask() {
        launchUI {
            val task = HttpService.discoverApi.getTaskData().await()
            taskData.postValue(task.data)
        }
    }

    /**
     * 获取猜你喜欢
     */
    fun getGuessLike() {
        launchUI({
            val guessLikeResponse = HttpService.discoverApi.getGuess(guessOffset, guessLimit).await()
            guessLoadMoreStatus.postValue(LoadStateConstants.STATE_CURRENT_COMPLETE)
            if (guessOffset == 0) {
                guessList.value?.clear()
            }

            if (guessLikeResponse.data != null) {
                val guessLikeBean = guessLikeResponse.data
                likeRecommend.postValue(guessLikeBean.like_recommend)
                if (guessLikeBean.result?.isEmpty() == true) {
                    if (guessOffset != 0) {
                        guessLoadMoreStatus.postValue(LoadStateConstants.STATE_ALL_COMPLETE)
                    }
                } else {
                    //当前页码等于随机展示推荐用户的页码，插入一个推荐用户类型
                    if (guessOffset == randomRecomendUserPage * guessLimit) {
                        val attention = ResultBean()
                        attention.resource_type = ResourceTypeConstans.TYPE_RECOMMEND_USER
                        guessList.value?.add(attention)
                    }
                    val size = guessLikeBean.result?.size ?: 0
                    guessOffset += size
                    guessLikeBean.result?.let { guessList.value?.addAll(it) }
                }
                if (!guessLikeBean.like_recommend) {//如果兴趣设置关闭则不显示猜你喜欢相关
                    guessList.value?.clear()
                    guessLoadMoreStatus.postValue(LoadStateConstants.STATE_ALL_COMPLETE)
                }
            } else {
                if (guessOffset != 0) {
                    likeRecommend.postValue(true)
                    guessLoadMoreStatus.postValue(LoadStateConstants.STATE_ALL_COMPLETE)
                } else {
                    likeRecommend.postValue(false)
                }
            }
            guessList.postValue(guessList.value)

        }, {
            if (guessOffset != 0) {
                likeRecommend.postValue(true)
            } else {
                likeRecommend.postValue(false)
            }
            guessLoadMoreStatus.postValue(LoadStateConstants.STATE_ERROR)
        })
    }


    //随机展示推荐相似用户的页数
    var randomRecomendUserPage = 0

    /**
     * 刷新数据
     */
    fun refreshData() {
        getBanner()
        getNoticeData()
        getTask()
        getQuickEntrys()
        getRecommendDiscoverColumn()

        //重置列表为0
        guessOffset = 0

        randomRecomendUserPage = (0..2).random()
//        loge("推荐用户随机插入位置: ${randomRecomendUserPage}")
        getGuessLike()
    }

}