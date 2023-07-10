package com.mooc.commonbusiness.model.studyroom

/**
 * 公开学习清单响应类
 */
class PublicStudyListResponse {
    var status : Int = 0       // 1,2,3,4  弹窗的4种状态，对应产品文档    4的时候无失效资源
    var invalid_resources_count : Int = 0       //失效的资源的个数
    var invalid_resources : List<String>? = null    //失效的资源名数组
}