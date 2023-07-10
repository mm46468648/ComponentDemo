package com.mooc.home.ui.hornowall.activityrank

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.home.HttpService
import com.mooc.home.model.ActivityRank
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ActivityRankViewModel: BaseListViewModel<ActivityRank>() {
    override suspend fun getData(): Deferred<List<ActivityRank>> {
        val async = viewModelScope.async {
            val activityList = HttpService.homeApi.getActivityList().await()
            activityList.activity
        }
        return async
    }
}