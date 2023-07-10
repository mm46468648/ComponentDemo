package com.mooc.discover.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ShareDataBean(
    var share_title: String = "",
    var share_desc: String = "",
    var share_picture: String = "",
    var share_link: String = "",
)