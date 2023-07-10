package com.mooc.battle.viewModel

import androidx.lifecycle.viewModelScope
import com.mooc.battle.BattleApi
import com.mooc.battle.model.EventData
import com.mooc.battle.model.RankDeatilsBean
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.net.ApiService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus

/**

 * @Author limeng
 * @Date 2022/12/28-10:16 上午
 */
class GameRankListViewModel : BaseListViewModel<RankDeatilsBean.RankListBean>() {
    var tournament_id: String? = null
    fun getRankOffset(): Int {

        return offset

    }

    override suspend fun getData(): Deferred<List<RankDeatilsBean.RankListBean>?> {

        val async = viewModelScope.async {
            val response = ApiService.getRetrofit().create(BattleApi::class.java)
                .getGameRankList(tournament_id, limit, offset)
            if (response.data != null&&offset==0) {
                EventBus.getDefault().post(EventData(response.data))
            }


            response.data.tournament_rank_list
        }

        return async
    }
}