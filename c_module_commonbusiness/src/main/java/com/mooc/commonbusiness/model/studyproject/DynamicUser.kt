package com.mooc.commonbusiness.model.studyproject

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 动态中的User信息
 */

@Parcelize
data class DynamicUser(
        var id: String ?=null,
        var name: String? = null,
        var avatar: String? = null,
        var nickname: String? = null,
        var avatar_identity: String? = null


): Parcelable {

}