package com.mooc.home.ui.todaystudy.column

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.mooc.commonbusiness.base.BaseListViewModel
import com.google.gson.Gson
import com.mooc.commonbusiness.model.eventbus.RefreshTodayCompleteEvent
import com.mooc.home.HttpService
import com.mooc.home.model.todaystudy.TodaySuscribe
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus

/**
 * 今日学习订阅专栏
 */
class SubscribeColumnViewModel : BaseListViewModel<TodaySuscribe>() {

    /**
     * 全部数据
     */
    val todaySuscribeData = MutableLiveData<TodaySuscribe>()
    var nextOffset = getOffSetStart()//下一次请求分页的偏移量
    override suspend fun getData(): Deferred<List<TodaySuscribe>?> {
        return viewModelScope.async {



            if(offset == getOffSetStart()){
                nextOffset = getOffSetStart()
            }
            val await = HttpService.homeApi.getTodayTaskSubscribe(nextOffset, limit).await()

            //如果是第一页，并且列表为空
            if(nextOffset == 0 && await.colum_status?.isEmpty() == true){
                todaySuscribeData.postValue(await)
            }

            nextOffset = await.offset


            val arrayListOf = arrayListOf<TodaySuscribe>()

            await.colum_status?.forEach {
                it.resource_list?.forEach {todaySiscribe->
                    //赋值专栏id和name
                    todaySiscribe.colum_name = it.colum_name
                    todaySiscribe.colum_id = it.resource_id
                    arrayListOf.add(todaySiscribe)
                }
            }
            arrayListOf

        }
    }

    /**
     * 上传点击完成
     */
    fun postTaskComplete(adapter: BaseQuickAdapter<TodaySuscribe, *>, subscribe: TodaySuscribe) {
        //
        val postMap = hashMapOf<String, String>(
                "resource_id" to subscribe.id
                , "resource_info" to subscribe.parent_id
                , "task_type" to subscribe.task_type)
        val toJson = Gson().toJson(postMap)
        val stringBody = toJson.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        launchUI {
            HttpService.homeApi.postTodayTask(stringBody).await()
            //在数据列表中移除，已完成资源学习
//            getPageData().value?.remove(subscribe)
//            getPageData().postValue(getPageData().value)

            if(!isActive)return@launchUI
            adapter.remove(subscribe)
            //通知已完成，刷新
            EventBus.getDefault().post(RefreshTodayCompleteEvent())
        }
    }
}