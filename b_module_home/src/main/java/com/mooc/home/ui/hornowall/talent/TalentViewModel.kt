package com.mooc.home.ui.hornowall.talent

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.home.HttpService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class TalentViewModel : BaseListViewModel<Any>() {

    //因为需要对数据进行组装后再显示，需要自己重新设置一下接口真正返回的偏移量
    var realOffset = 0
    override suspend fun getData(): Deferred<List<Any>?> {
        if(offset == 0){
            realOffset = offset //对realOffset重新赋值
        }
        val async = viewModelScope.async {
            loadProjectTalent()
        }
        return async
    }

    suspend fun loadProjectTalent() : List<Any>{
        val honorResponse = HttpService.homeApi.getHonorRoll(realOffset, limit).await()
        val honorRollList = arrayListOf<Any>()
        if(honorResponse.results?.isNotEmpty() == true){
            //组装数据
            val results = honorResponse.results
            results?.forEach {
                honorRollList.add(it)     //添加title
                it.user_complate_info.forEach {user -> //添加列表，截取前6名？
                    honorRollList.add(user)
                }
                honorRollList.add("CustomDivider")     //添加分割条
            }

            realOffset+= results?.size?:0
        }

        return honorRollList
    }


    override fun getLimitStart(): Int  = 3
}