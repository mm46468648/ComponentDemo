package com.mooc.studyproject.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@SuppressLint("ParcelCreator")
@Parcelize
data class NoticeBean  (
        /**
     * count : 1
     * next : null
     * previous : null
     * results : [{"id":1,"studyplan":68,"title":"公告标题","content":"公告内容","user_id":101,"status":1,"order":2,"created_time":"2019-02-12 16:15:03","updated_time":"2019-02-12 16:15:03"}]
     */
     val count: Int = 0,
        val next:  @RawValue Any? = null,
        val previous: @RawValue  Any? = null,
        val results: @RawValue List<Notice>? = null
): Parcelable