package com.mooc.discover.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**

 * @Author limeng
 * @Date 2/23/22-1:41 PM
 */
@Parcelize
class TaskBindStudyListBean(var id: String = "",
                            var source_name: String = "",
                            var source_type: String = "",
                            var source_id: String = "",
                            var source_data: BindDateSourceData? = null

) : Parcelable

@Parcelize
class BindDateSourceData(
    var is_admin: Boolean = false,       //是否是运营推荐的
    var show_folder_id: String = ""       //运营推荐的清单使用的学习清单id
): Parcelable