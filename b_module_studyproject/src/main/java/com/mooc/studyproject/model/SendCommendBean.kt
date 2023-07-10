package com.mooc.studyproject.model

/**
 */
data class SendCommendBean(
        /**
         * id : 6
         * study_activity : 2
         * comment_user : {"id":12,"name":"小美","avatar":"http://storage.xuetangx.com/moocnd/img/89c4fc95149d0d7fc763516952e77baf.jpg","nickname":"小美"}
         * comment_content : 这是一个评论
         * comment_time : 2017-09-19 18:44:19
         * comment_status : 1
         * comment_to : null
         * like_num : 0
         */
        var id: Int = 0,
        var study_activity: Int = 0,
        var comment_user: CommentUserBean? = null,
        var comment_content: String? = null,
        var comment_time: String? = null,
        var comment_status: Int = 0,
        var comment_to: Any? = null,
        var like_num: Int = 0,
        var receiver_user_id: Int = 0
) {
    data class CommentUserBean(

            /**
             * id : 12
             * name : 小美
             * avatar : http://storage.xuetangx.com/moocnd/img/89c4fc95149d0d7fc763516952e77baf.jpg
             * nickname : 小美
             */
            var id: Int = 0,
            var name: String? = null,
            var avatar: String? = null,
            var nickname: String? = null
    )
}