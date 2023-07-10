package com.mooc.commonbusiness.model.search

import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.commonbusiness.model.studyproject.StudyProject

/**

 * @Author limeng
 * @Date 2020/8/10-4:36 PM
 */
data class SearchResultBean(
    var album: DataBean<AlbumBean>? = null,
    var course: DataBean<CourseBean>? = null,
    var article: DataBean<ArticleBean>? = null,
    var ebook: DataBean<EBookBean>? = null,
    var baike: DataBean<BaiKeBean>? = null,
    var micro_course: DataBean<MicroBean>? = null,
    var track: DataBean<TrackBean>? = null,
    var tuling: DataBean<TulingBean>? = null,
    var chaoxing: DataBean<PeriodicalBean>? = null,
    var study_plan: DataBean<StudyProject>? = null,
    var kanwu: DataBean<PublicationBean>? = null,
    var folder: DataBean<FolderBean>? = null,
    var micro_knowledge: DataBean<MicroKnowBean>? = null
)