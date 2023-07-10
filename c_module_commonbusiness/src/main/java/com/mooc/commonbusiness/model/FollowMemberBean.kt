package com.mooc.commonbusiness.model

import java.io.Serializable

class FollowMemberBean : Serializable {
    var state = 0
    var user_id: String? = null
    var name: String? = null
    var avatar: String? = null
    var avatar_identity: String? = null
    var introduction: String? = null

}