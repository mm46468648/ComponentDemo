package com.mooc.commonbusiness.model.studyroom

/**
 * 发现页一级分类，二级分类通用模型
 * 实在不想定义太多
 */
data class DiscoverTab(
    var is_all: Boolean = false,
    var type: Int = 0,            //当一级分类为推荐时 1全部，2，我的订阅，3，专栏类型，4专题类型
    var resource_type: Int = 0,    //资源类型（-1，全部，2慕课，21音频，20学习项目，5电子书，14文章，33微课）
    var id: Int = 0,             //(1, u'推荐'), (2, u'学习项目'), (3, u'慕课'), (4, u'音频'), (5, u'文章'), (6, u'电子书'), (7, u'微课
    var name: String = "",      //一级分类中的数据
    var relation_type: Int = 0,      //(-1, u'推荐'), (0, u'学习项目'), (1, u'慕课'), (2, u'音频'), (3, u'文章'), (4, u'电子书'), (5, u'微课
    var title: String = "",   //tab 标题
    var children: List<SortChild>? = null,

    val own_operation: Int = 0 //是否自运营 0 否  1 是     0代表三个都不显示 ，只有综合列表，隐藏选项,1的情况比较多
)

/**
 * 第三级
 */
data class SortChild(
    var is_all: Boolean = false,
    val type: Int = 0,
    val id: Int = 0,
    val title: String = "",
    val resource_type: Int = 0,
    val isSelected: Boolean = false,
    val own_operation: Int = 0, //是否自运营 0 否  1 是     0代表三个都不显示 ，只有综合列表，隐藏选项,1的情况比较多
    val parent_id: Int = 0

)