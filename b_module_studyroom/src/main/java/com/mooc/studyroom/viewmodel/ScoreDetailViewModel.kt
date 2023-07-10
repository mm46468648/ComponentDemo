package com.mooc.studyroom.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.Constans
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.ScoreDetail
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ScoreDetailViewModel : BaseListViewModel<ScoreDetail>() {

    var beginDate: String = ""   //从哪个月份开始查,格式yyyy-MM
    override suspend fun getData(): Deferred<List<ScoreDetail>?> {
        return viewModelScope.async {
            val await = ApiService.getRetrofit().create(StudyRoomApi::class.java).getStudyScoreDetailList(offset, limit, beginDate).await()
            await.user_score
            //筛选全部时，去掉头部年份
//            val list = ArrayList<ScoreDetail>()
//            if(offset == 0 && beginDate == "" && await.user_score.isNotEmpty()){
//                //查询全部,并且是第一页的时候,获取第一个的年积分数据
//                val get = await.user_score.get(0)
//                val element = ScoreDetail(studyplan_name = Constans.SCORE_TOTAL_STR,year_score = get.year_score,year_date = get.year_date)
//                list.add(element)
//            }
//            list.addAll(await.user_score)
//            list

        }
    }
}