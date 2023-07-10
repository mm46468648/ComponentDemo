package com.mooc.home.model

import com.mooc.commonbusiness.model.studyproject.StudyPlan
import java.util.*

/**
 * 学习项目完成后在Home页弹出相应的积分或勋章
 */
data class StudyCompletion (
        val code: Int = 0,
        val is_pop: Int = 0,
        val message: ArrayList<StudyPlan>? = null
)