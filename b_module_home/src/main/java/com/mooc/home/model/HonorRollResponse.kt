package com.mooc.home.model

import com.mooc.commonbusiness.model.UserInfo

data class HonorRollResponse(

        var id:String,        //学习项目id
        var plan_name:String,        //名称
        var success_nums:Int,     //几人
        var user_complate_info:ArrayList<UserInfo>

)
