package com.mooc.home.ui.todaystudy.adjusttarget

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.google.gson.Gson
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.home.HttpService
import com.mooc.home.model.todaystudy.TargetDetial
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class TargetAdjustViewModel : BaseListViewModel<TargetDetial>() {
    override suspend fun getData(): Deferred<List<TargetDetial>?> {
        return viewModelScope.async {
            val targetList = arrayListOf<TargetDetial>()
            val await = HttpService.homeApi.getTargetAdajustList().await()
            val data = await.data

            //调整目标顺序 （签到,任务,课程，音频，学习计划，电子书，刊物，最热资源，专栏，专题）
            data.checkin_status?.let { targetList.addAll(it) }
            data.task_system_status?.let { targetList.addAll(it) }
            data.course_status?.let { targetList.addAll(it) }
            data.album_status?.let { targetList.addAll(it) }
            data.studyplan_status?.let { targetList.addAll(it) }
            data.ebook_status?.let { targetList.addAll(it) }
            data.kanwu_status?.let { targetList.addAll(it) }
            data.most_hot_status?.let { targetList.addAll(it) }
            data.colum_status?.let { targetList.addAll(it) }
            data.special_status?.let { targetList.addAll(it) }


            targetList


        }
    }


    /**
     * 上传目标调整
     */
    fun postTargetAdjust(detail: TargetDetial) {
        val toRequestBody = Gson().toJson(detail)
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        launchUI {
            val await = HttpService.homeApi.postTargetAdajust(toRequestBody).await()
            toast(await.msg)
        }
    }
}