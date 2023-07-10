package com.mooc.home.ui.todaystudy.must

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.constants.LoadStateConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.home.HttpService
import com.mooc.home.model.todaystudy.HotResource
import com.mooc.home.model.todaystudy.TodaySuscribe
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MustViewModel : BaseListViewModel2<Pair<Int, List<Any>>>() {



    /**
     * 上传新的排序
     */
    fun postNewOrderList(order: ArrayList<Int>) {
        val hashMapOf = hashMapOf<String, ArrayList<Int>>("order_list" to order)
        val fromJson = RequestBodyUtil.fromJsonStr(GsonManager.getInstance().toJson(hashMapOf))
        launchUI {
            HttpService.homeApi.postTodayStudyTabOrder(fromJson)
        }
    }


    /**
     * 最热资源
     * 上传点击完成
     */
    fun postTaskComplete(subscribe: HotResource) {
        //
        val postMap = hashMapOf<String, String>(
            "resource_id" to subscribe.resource_id,
            "resource_info" to subscribe.resource_type.toString(),
            "task_type" to subscribe.task_type.toString()
        )
        val toJson = Gson().toJson(postMap)
        val stringBody =
            toJson.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        launchUI {
            HttpService.homeApi.postTodayTask(stringBody).await()
        }
    }

    override suspend fun getData(): Flow<List<Pair<Int, List<Any>>>?> {
        return flow{
            val newList = arrayListOf<Pair<Int, List<Any>>>()
            val todayStudyMust = HttpService.homeApi.getTodayMust()
            //将接口中的资源，按照固定顺序添加到列表中，顺序要严格按照当前添加顺序,如果为空
            var orderList = todayStudyMust.order_list
            if (orderList == null || orderList.isEmpty()) {
                orderList = arrayListOf(
                    400,      //任务
                    200,      //签到
                    ResourceTypeConstans.TYPE_COURSE,
                    ResourceTypeConstans.TYPE_ALBUM,
                    ResourceTypeConstans.TYPE_STUDY_PLAN,
                    ResourceTypeConstans.TYPE_E_BOOK,
                    ResourceTypeConstans.TYPE_PUBLICATION,
                    300,      //热门资源
                )
            }

            //根据顺序再添加对应的资源
            orderList.forEach {
                when (it) {
                    200 -> {
                        if (todayStudyMust.checkin_status?.size ?: 0 > 0) {
                            newList.add(
                                Pair(
                                    MustAdapter2.TYPE_CHECKIN_STR,
                                    todayStudyMust.checkin_status!!
                                )
                            )
                        }
                    }
                    ResourceTypeConstans.TYPE_COURSE -> {
                        if (todayStudyMust.course_status.size > 0) {
                            newList.add(
                                Pair(
                                    MustAdapter2.TYPE_COURSE_STR,
                                    todayStudyMust.course_status
                                )
                            )
                        }
                    }
                    ResourceTypeConstans.TYPE_ALBUM -> {
                        if (todayStudyMust.album_status.size > 0) {
                            newList.add(
                                Pair(
                                    MustAdapter2.TYPE_ALBUM_STR,
                                    todayStudyMust.album_status
                                )
                            )
                        }
                    }
                    ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                        if (todayStudyMust.studyplan_status.size > 0) {
                            newList.add(
                                Pair(
                                    MustAdapter2.TYPE_STUDYPROJECT_STR,
                                    todayStudyMust.studyplan_status
                                )
                            )
                        }
                    }
                    ResourceTypeConstans.TYPE_E_BOOK -> {
                        if (todayStudyMust.ebook_status.size > 0) {
                            newList.add(
                                Pair(
                                    MustAdapter2.TYPE_EBOOK_STR,
                                    todayStudyMust.ebook_status
                                )
                            )
                        }
                    }
                    ResourceTypeConstans.TYPE_PUBLICATION -> {
                        if (todayStudyMust.kanwu_status?.size?:0 > 0) {
                            newList.add(
                                Pair(
                                    MustAdapter2.TYPE_PUBLICATION_STR,
                                    todayStudyMust.kanwu_status!!
                                )
                            )
                        }
                    }
                    300 -> {
                        if (todayStudyMust.most_hot_status.isNotEmpty()) {
                            val list = arrayListOf<HotResource>()
                            todayStudyMust.most_hot_status.forEach { mosthost ->
                                mosthost.resource_list?.let { resource ->
                                    list.addAll(resource)
                                }
                            }
                            newList.add(Pair(MustAdapter2.TYPE_HOT_STR, list))
                        }
                    }
                    400 -> {
                        if (todayStudyMust.task_system_status?.size ?: 0 > 0) {
                            newList.add(
                                Pair(
                                    MustAdapter2.TYPE_TASK_STR,
                                    todayStudyMust.task_system_status!!
                                )
                            )
                        }
                    }
                }
            }
            emit(newList)
        }
    }
}