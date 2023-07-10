package com.mooc.studyproject.model

import java.io.Serializable

data class SourceCurrentBean(
        var has_checkin_activity: Int = 0,
        var checkin_activity_status: Int = 0
) : Serializable