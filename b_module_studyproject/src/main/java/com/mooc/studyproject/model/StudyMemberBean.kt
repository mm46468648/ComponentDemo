package com.mooc.studyproject.model

import java.io.Serializable
import java.util.*

data class StudyMemberBean(
        var msg: String? = null,
        var code: Int = 0,
        var result: ArrayList<MemberListBean>? = null

) : Serializable

