package com.mooc.home.model

import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.model.search.TrackBean

data class GuessLikeResponse(
        var total:String,
        var result:List<GuessYouLike>
)
data class GuessYouLike(
        var is_free :Int = 0,//是否免费  0 付费 1 免费
        val verified_active: Int = 0,  //是否有证书 0 无证书 1 有证书
        val is_have_exam: Int = 0, //是否有考试 0 无考试 1 有考试
        val is_free_info: String = "", // 是否收费文案
        val title: String = "", // 标题
        val picture: String = "", // 图片
        val resource_type: String = "", // 资源类型
        val verified_active_info : String = "",// 证书文案
        val is_have_exam_info : String = "", // 考试文案
        val platform_zh : String = "",// 机构？
        val org : String = "",// 课程类型时机构
        val course_start_time : String = "",// 课程类型时开课时间
        val press : String = "",// 电子书类型时（出版社）
        val album_data: AlbumBean, //音频课
        val track_data: TrackBean //音频






)