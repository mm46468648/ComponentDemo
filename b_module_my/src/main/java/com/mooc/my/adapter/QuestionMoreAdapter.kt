package com.mooc.my.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R
import com.mooc.my.model.QuestionMoreBean


class QuestionMoreAdapter(data: ArrayList<QuestionMoreBean.ResultsBean>?) : BaseMultiItemQuickAdapter<QuestionMoreBean.ResultsBean, BaseViewHolder>(data) {
    companion object {
        const val NORMAL = 1
        const val XIAO_MENG = 2
    }

    init {
        addItemType(NORMAL, R.layout.my_item_question_list)
        addItemType(XIAO_MENG, R.layout.my_question_item_xiao_meng)
    }

    override fun convert(holder: BaseViewHolder, item: QuestionMoreBean.ResultsBean) {
        when (item.itemType) {
            NORMAL -> {
                holder.setText(R.id.title, item.question_content)

            }

        }
    }


}