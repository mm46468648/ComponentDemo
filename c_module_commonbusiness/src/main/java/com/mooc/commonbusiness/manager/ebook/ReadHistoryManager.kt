package com.mooc.commonbusiness.manager.ebook

import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.model.search.EBookBean

/**
 * 阅读历史
 */
class ReadHistoryManager {

    companion object{
        /**
         * 存储
         */
        fun save(ebookDetail: EBookBean){
            EbookDatabase.database?.getEbookDao()?.insert(ebookDetail.generateEbookDB())
        }

        /**
         * 删除记录
         */
        fun delete(bookId : String){
            EbookDatabase.database?.getEbookDao()?.delete(bookId)
        }

        /**
         * 删除记录
         */
        fun delete(book : EBookBean){
            EbookDatabase.database?.getEbookDao()?.delete(book.generateEbookDB())
        }


        /**
         * 查询阅读历史列表
         */
        fun searchReadHistoryList() : List<EBookBean>?{
            return EbookDatabase.database?.getEbookDao()?.getReadHistoryList()?.map {
                GsonManager.getInstance().convert(it.data,EBookBean::class.java)
            }
        }
    }
}