package com.mooc.discover.model

class MasterOrderBean {
    var author_info: AuthorInfoBean? = null

    class AuthorInfoBean {
        var name: String? = null
        var tutor: String? = null
        var speaker: String? = null
    }
}