package com.mooc.commonbusiness.model.studyroom

data class ShareSchoolCircleBean (
        /**
         * code : 1
         * message : 分享成功
         * score : 0
         * share_message : 今日学友圈已达上限3分
         */
        var code: Int = 0,
        var message: String? = null,
        var score: Int = 0,
        var share_message: String? = null
        )