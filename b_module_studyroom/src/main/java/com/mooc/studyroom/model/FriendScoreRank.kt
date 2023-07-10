package com.mooc.studyroom.model

/**
 * 好友积分排行数据bean
 */
class FriendScoreRank {
    var my_rank = 0   //我得排名
    var friend_rank: ArrayList<FriendRank>? = null
}

class FriendRank{
    var rank = 0
    var score = ""
    var avatar = ""
    var user_name = ""
}