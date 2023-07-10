package com.mooc.commonbusiness.model.search

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by liyusheng on 2017/5/10.
 */
@Parcelize
class SearchResultStaffsBean : Parcelable {
    var about: String? = null
    var name: String? = null
    var mailAddress: String? = null
    var staffId: String? = null
    var avatar: String? = null
    var courseId: String? = null
    var staffOrg: String? = null
    var id: String? = null

}