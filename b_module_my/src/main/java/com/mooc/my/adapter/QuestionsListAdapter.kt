package com.mooc.my.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R
import com.mooc.my.model.QuestionListBean

/**

 * @Author limeng
 * @Date 2020/10/13-10:13 AM
 */
class QuestionsListAdapter(data: ArrayList<QuestionListBean.HotListBean>?) : BaseMultiItemQuickAdapter<QuestionListBean.HotListBean, BaseViewHolder>(data) {
    companion object {
        val TOP = 1
        val MID = 2
        val BOTTOM = 3
        val EMPTY = 4
    }

    init {
        addItemType(TOP, R.layout.my_question_head_question_list)
        addItemType(MID, R.layout.my_item_question_green)
        addItemType(BOTTOM, R.layout.my_item_question_list)
        addItemType(EMPTY, R.layout.my_question_item_xiao_meng)
    }

    override fun convert(holder: BaseViewHolder, item: QuestionListBean.HotListBean) {
        when (item.itemType) {
            TOP -> {
                holder.setGone(R.id.top, true)
                holder.setText(R.id.title, item.question_content)
                if ("常见问题".equals(item.question_content)) {
                    holder.setGone(R.id.more, true)
                }else{
                    holder.setGone(R.id.more, false)

                }
            }
            MID -> {
                holder.setText(R.id.title, item.question_content)
            }
            BOTTOM -> {
                holder.setText(R.id.title, item.question_content)
            }

        }
    }
}