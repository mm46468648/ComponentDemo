package com.mooc.home.ui.studyroom.business

import com.mooc.home.model.StudyScoreResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface StudyRoomApi {

    //获取积分数据（学习室头部）
    @GET("/student/sub_statistics/")
    fun getStudyUserScore(): Deferred<StudyScoreResponse>
}