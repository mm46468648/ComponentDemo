package com.mooc.studyproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class StudyClockState (
    var text //文案
            : String? = null,
    var title_color_code //字体颜色
            : String? = null,
    var click //能否点击
            : String? = null,
    var background_color_code //背景颜色
            : String? = null
): Parcelable