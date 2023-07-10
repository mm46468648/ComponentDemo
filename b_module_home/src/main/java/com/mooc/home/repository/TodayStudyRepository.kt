package com.mooc.home.repository

import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.home.HttpService
import com.mooc.home.model.TodayStudyData
import com.mooc.home.model.TodayStudyIconBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 今日学习数据仓库
 */
class TodayStudyRepository : BaseRepository() {

    /**
     * 获取今日学习数据
     */
    suspend fun getTodayStudy() : TodayStudyData {
        return request {
            HttpService.homeApi.getTodaySource().await()
        }
    }

    suspend fun getTodayStudyIcon(): Flow<TodayStudyIconBean> {
        return flow {
            val todayStudyIcon = HttpService.homeApi.getTodayStudyIcon()
            emit(todayStudyIcon)
        }
    }



}