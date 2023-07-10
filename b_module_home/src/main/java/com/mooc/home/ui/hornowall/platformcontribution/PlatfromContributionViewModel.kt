package com.mooc.home.ui.hornowall.platformcontribution

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.home.HttpService
import com.mooc.home.model.honorwall.PlatformContributionBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class PlatfromContributionViewModel : BaseListViewModel<PlatformContributionBean>() {

    var type = "1"
    var map : HashMap<String,String> = hashMapOf()
    override suspend fun getData(): Deferred<List<PlatformContributionBean>?> {
        return viewModelScope.async {
            val ranking = HttpService.homeApi.getDevoteRankingResult(type ,offset, limit).await()
            ranking.data
        }
    }
}