package com.mooc.commonbusiness.model.studyproject

import android.os.Parcelable
import com.mooc.commonbusiness.model.studyproject.StudyPlan
import kotlinx.android.parcel.Parcelize

/**
 */
@Parcelize
data class StudyPlanDetailBean(
        var code: Int = 0,
        var user_id: Int = 0,
        var is_join: Int? = 0,
        var is_login: Int = 0,
        var user_join_date: Long = 0,
        var study_plan: StudyPlan? = null,
        var coupon_used_status: String? = null,
        var isFirstLoadPage: Boolean = false,
        var is_start_user: Boolean = false,
        var is_restrict: Boolean = false

) : Parcelable