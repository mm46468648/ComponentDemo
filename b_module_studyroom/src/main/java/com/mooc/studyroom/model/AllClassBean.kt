package com.mooc.studyroom.model

/**

 * @Author limeng
 * @Date 2021/3/9-3:52 PM
 */
class AllClassBean {
    var id: String? = null
    var url: String? = null
    var link: String? = null
    var coverUrlSmall: String? = null
    var cover_image: String? = null
    var basic_cover_url: String? = null
    var picture: String? = null
    var coverUrlMiddle: String? = null
    var albumTitle: String? = null
    var title: String? = null
    var content: String? = null
    var source_title: String? = null
    var publish_content: String? = null
    var trackTitle: String? = null
    var publish_img_list: ArrayList<String>? = null
    var announcer: Announcer? = null
    var lastUptrack: LastUpTrack? = null

    class LastUpTrack {
        var trackTitle: String? = null
    }

    var activity_type: Int=0
    var activity_checkin_type: Int=0

    class Announcer {
        var nickname:String? = null
    }
}