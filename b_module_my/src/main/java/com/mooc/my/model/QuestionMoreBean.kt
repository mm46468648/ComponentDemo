package com.mooc.my.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * 更多问题的bean
 * @Author limeng
 * @Date 2020/10/12-3:44 PM
 */
data class QuestionMoreBean(
        private var count: Int = 0,
        val next: Any? = null,
        val previous: Any? = null,
        val results: ArrayList<ResultsBean>? = null
) {
    class ResultsBean(override var itemType: Int) :MultiItemEntity {
        val question_content: String? = null
        val answer_content: String? = null

    }
}