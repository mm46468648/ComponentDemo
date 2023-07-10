package com.mooc.my.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**

 * @Author limeng
 * @Date 2020/10/12-3:33 PM
 */
data class QuestionListBean(

        var question_is_hot: String? = null,

        val question_name: String? = null,
        val hot_list: List<HotListBean>? = null,
        val not_hot_list: List<HotListBean>? = null
) {
     class HotListBean(override var itemType: Int) : MultiItemEntity{
        var id: Int? = null

         var question_type: String? = null
         var answer_content: String? = null
         var question_content: String? = null


     }
}
