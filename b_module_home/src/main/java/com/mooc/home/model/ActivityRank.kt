package com.mooc.home.model

/**
 * 活动排行
 */

data class ActivityRankResponse(
        var count:String,
        var activity:ArrayList<ActivityRank>
)
data class ActivityRank(
        var activity_url:String,
        var name:String,
        var img:String
)