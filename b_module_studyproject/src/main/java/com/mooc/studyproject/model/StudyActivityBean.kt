package com.mooc.studyproject.model

import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import java.io.Serializable

/**
 * 学习动态
 */
data class StudyActivityBean(
        var count: Int = 0,
        var next: String? = null,
        var results: ArrayList<StudyDynamic>? = null
) : Serializable