package com.mooc.commonbusiness.model.search

import android.os.Parcelable
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import com.mooc.commonbusiness.model.db.EbookDB
import kotlinx.android.parcel.Parcelize

/**

 * @Author limeng
 * @Date 2020/8/10-4:40 PM
 */
@Parcelize
data class EBookBean(
    @JvmField
        var id:String = "",
    @JvmField
        var title: String = "",
    var writer: String = "",
    var picture: String = "",
    var ebook_id: String = "",
    @JvmField
        var word_count: Long = 0,
    var press: String = "", //来源？
    var read_process: String = "0",//阅读进度
    var platform_zh: String = "",  //来源？
    var status : Int = 0, //资源是否已下线，<0代表下线

        //详情中的字段
    var is_enroll : Boolean = false, //是否加入学习室
    var cate: String? = "",
    var recommend_word: String = "",
    var pub_date: String = "",
    var isbn: String = "",
    var filesize: String = "",
    var description: String = "",
    val resource_status: Int = 0,
    var task_finished:Boolean = false,//任务是否完成
    var is_task:Boolean = false//是否是任务


): StudyResourceEditable, Parcelable,BaseResourceInterface {
    override val _resourceId: String
        get() = id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_E_BOOK
    override val _resourceStatus: Int
        get() = resource_status
    override val _other: Map<String, String>?
        get() = null
    override val resourceId: String
        get() = id
    override val sourceType: String
        get() = ResourceTypeConstans.TYPE_E_BOOK.toString()


    fun generateEbookDB():EbookDB{
        val ebookDb = EbookDB()
        ebookDb.id = id
        ebookDb.data = GsonManager.getInstance().toJson(this)
        return ebookDb
    }
}