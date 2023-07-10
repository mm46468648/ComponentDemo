package com.mooc.studyroom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.studyroom.HttpService
import com.mooc.studyroom.model.FriendRank
import com.mooc.studyroom.model.ScoreDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FriendScoreRankViewModel : BaseListViewModel2<FriendRank>() {
    var type = -1
    var myRank = MutableLiveData<Int>()
    override suspend fun getData(): Flow<List<FriendRank>> {
        return flow {
            if (type != -1){
                val friendScoreRank =
                    HttpService.studyRoomApi.getFriendScoreRank(type, offset, limit)
                myRank.postValue(friendScoreRank.data.my_rank)
                emit(friendScoreRank.data.friend_rank?: emptyList<FriendRank>())
            }else{
                emit(emptyList<FriendRank>())
            }
        }
    }
}