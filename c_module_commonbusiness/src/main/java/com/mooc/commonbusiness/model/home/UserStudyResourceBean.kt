package com.mooc.commonbusiness.model.home

import com.mooc.commonbusiness.model.search.*
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.model.search.CourseBean

/**
 * 公开的学习清单资源列表
 *
 */
data class PublicFolder(
        var folder: UserStudyResourceBean
)

data class UserStudyResourceBean(
        var knowledge: UserStudyResource<KnowledgeBean>?= null,
        val course: UserStudyResource<CourseBean>? = null,
        val baike: UserStudyResource<BaiKeBean>? = null,
        val article: UserStudyResource<ArticleBean>? = null,
        val chaoxing: UserStudyResource<PeriodicalBean>? = null,
        val album: UserStudyResource<AlbumBean>? = null,
        val track: UserStudyResource<TrackBean>? = null,
        val note: UserStudyResource<NoteBean>? = null,
        val ebook: UserStudyResource<EBookBean>? = null,
        val audio: UserStudyResource<OwnTrackBean>? = null,
        val micro_course:UserStudyResource<MicroBean>? = null,
        val kanwu:UserStudyResource<PublicationBean>? = null
)

data class UserStudyResource<T>(
        var items:ArrayList<T>
)