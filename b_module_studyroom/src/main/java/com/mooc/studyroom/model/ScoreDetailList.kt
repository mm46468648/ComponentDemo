package com.mooc.studyroom.model

data class ScoreDetailList(
        var user_score : List<ScoreDetail>,
        var user_total_score:String
)

data class ScoreDetail(
        var current_score : String = "", //当前总分
        var studyplan_name : String = "", //学习项目名称
        var source : String = "",  //来源
        var score : Int = 0,  //得分
        var month_score : String = "",//月积分
        var month_date : String = "", //年-月
        var year_score : String="0", //年积分
        var year_date : String="0", //年
        var add_time : Long = 0 //时间戳秒值，使用需乘一千

)