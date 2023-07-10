package com.mooc.discover.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * 公告bean
 */
@Parcelize
class FoldListBean : Parcelable {
    var folder_list: ArrayList<TaskBindStudyListBean>? = null
}