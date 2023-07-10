package com.mooc.home.ui.studyroom.business

import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.net.ApiService
import com.mooc.home.model.StudyScoreResponse

class StudyRoomRepository : BaseRepository() {

    suspend fun requestStudyScoreData() : StudyScoreResponse {
        return request {
             ApiService.getRetrofit().create(StudyRoomApi::class.java).getStudyUserScore().await()
        }
    }
}