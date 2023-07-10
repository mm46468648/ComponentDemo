package com.mooc.battle.viewModel

import com.mooc.battle.BattleApi
import com.mooc.battle.UserApi
import com.mooc.battle.model.SkillInfo
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.net.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SkillMyCreateViewModel : BaseListViewModel2<SkillInfo>() {
    override suspend fun getData(): Flow<List<SkillInfo>> {
        return flow {
            val myCreateSkillList = ApiService.getRetrofit().create(BattleApi::class.java)
                .getMyCreateSkillList(offset, limit)
            emit(myCreateSkillList.data.results)
        }
    }
}