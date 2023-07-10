package com.mooc.commonbusiness.interfaces

import org.json.JSONObject

/**
 * 学习室资源编辑借口
 * 实现此借口，证明可以被编辑，长安现实编辑弹框
 *
 * /**
 * 调接口的时候需要传递的json包括
 * 资源类型 type
 * 资源id id
 *
 * 新学堂课程特殊，需求多传一个cid，需要以"_"下划线的形式将"cid"拼接在课程id后面
*/
 */
interface StudyResourceEditable {

    val resourceId : String
    val sourceType : String

}