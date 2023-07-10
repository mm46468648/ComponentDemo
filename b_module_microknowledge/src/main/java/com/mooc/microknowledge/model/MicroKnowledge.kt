package com.mooc.microknowledge.model

class MicroKnowledge {
    var id: String = ""

    var exam_num: Int = 0
    var title: String = ""
    var des: String = ""
    var pic: String = ""
    var head_pic: String = ""
    var page_pic: String = ""
    var color: String = ""
    var list_tag: Int = 0


    //拆到statistics接口中了
    var click_num: Int = 0
    var like_num: Int = 0
    var share_num: Int = 0
    var certificate_num: Int = 0

    var like_status = false
    var medal_num: Int = 0
    var medal_link: String = ""

    var config: MicroKnowledgeConfig? = null
}