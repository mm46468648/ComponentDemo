package com.mooc.commonbusiness.model

class ShareScoreResponse {
    /**
     * code : 1
     * message : 分享成功
     * score : 0
     * share_message : 今日学友圈已达上限3分
     */
    var code = 0
    var message: String? = null
    var score = 0
    var share_message: String? = null

    //分享积分
    var share_medal_img: String? = null
    var share_code = 0
    var share_score = 0
    var share_count = 0

}