package com.mooc.my.model

import com.mooc.commonbusiness.model.FollowMemberBean
import java.util.*

data class UserFollowBean(
        var count: Int = 0,
         var data: ArrayList<FollowMemberBean>? = null

)