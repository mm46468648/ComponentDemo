package com.mooc.ebook.viewmodel

import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.base.BaseVmViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.studylog.StudyLogManager
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.ebook.EbookApi
import com.mooc.ebook.repository.EBookRepository
import org.json.JSONObject

class EbookViewModel(var bookId : String) : BaseVmViewModel<EBookBean>(bookId,ResourceTypeConstans.TYPE_E_BOOK) {
    override fun getRepository(): BaseRepository = EBookRepository()

    override fun block(): suspend () -> EBookBean {
        return {
            ApiService.getRetrofit().create(EbookApi::class.java).getEbookDetail(bookId).await()
        }
    }


    /**
     * 上传学习记录
     */
    fun postStudyLog(it: EBookBean) {
        val request = JSONObject()
        request.put("type", ResourceTypeConstans.TYPE_E_BOOK)
        request.put("url", it.id)
        request.put("title", it.title)
        StudyLogManager.postStudyLog(request)
    }


}