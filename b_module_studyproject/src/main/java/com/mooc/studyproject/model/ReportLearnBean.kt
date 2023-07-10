package com.mooc.studyproject.model

import com.mooc.commonbusiness.model.ReportBean
import java.io.Serializable
import java.util.*

data class ReportLearnBean(
        var results: ArrayList<ReportBean>? = null
) : Serializable