package com.mooc.commonbusiness.model.studyproject

import android.os.Parcelable
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.TYPE_TEST_VOLUME
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import kotlinx.android.parcel.Parcelize

/**

 * @Author limeng
 * @Date 3/10/22-4:27 PM
 */
@Parcelize
class IntelligentBindInfo : BaseResourceInterface , Parcelable {
    var bind_test_paper_id: String? = null
    var test_paper_target: String? = null
    var title: String? = null
    var test_paper_link: String? = null
    override val _resourceId: String
        get() = if( bind_test_paper_id.isNullOrBlank())"0" else bind_test_paper_id!!
    override val _resourceType: Int
        get() = TYPE_TEST_VOLUME
    override val _other: Map<String, String>?
        get() {
            val map = hashMapOf<String, String>()
            map[IntentParamsConstants.WEB_PARAMS_URL] = if(test_paper_link.isNullOrBlank()) "" else test_paper_link!!
            map[IntentParamsConstants.WEB_PARAMS_TITLE ] =  if(title.isNullOrBlank()) "" else title!!

            return map

        }

}