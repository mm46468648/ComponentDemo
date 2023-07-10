package com.mooc.studyproject.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import kotlinx.android.parcel.Parcelize

/**
 * Created by huangzuoliang on 2017/9/19.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class DynamicsBean(
        /**
         * msg : 动态生成成功
         * score : 5
         * success : true
         * activity : {"id":18,"study_plan":19,"user":{"id":12,"name":"小美","avatar":"http://storage.xuetangx.com/moocnd/img/89c4fc95149d0d7fc763516952e77baf.jpg","nickname":"小美"},"publish_time":"2017-09-19 03:22:02","publish_content":"这是测试","publish_img":"","like_num":0,"comment_num":0,"publish_state":1}
         */
        var msg: String? = null,
        var message: String? = null,
        var code: Int = 0,
        var score: Int = 0,
        var error_code: Int = 0,
        var isSuccess: Boolean = false,
        var activity: StudyDynamic? = null,
        var after_resource_status: SourceAfterBean? = null,
        val button: StudyClockState? = null,
        val next_button: StudyClockState? = null
) : Parcelable