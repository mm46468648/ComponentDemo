package com.mooc.studyroom.ui.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.studyroom.R
import com.mooc.studyroom.model.MoveArticleBean

/**
用来展示弹框中 所有的文件夹相关列表
 * @Author limeng
 * @Date 2021/3/19-3:27 PM
 */
class MoveResourceAdapter(data: ArrayList<MoveArticleBean>?) : BaseMultiItemQuickAdapter<MoveArticleBean, BaseViewHolder>(data) {

    companion object {
        const val TYPE_NEW_FOLDER = 1
        const val TYPE_STUDY_ROOM = 2
        const val TYPE_RETURN = 3
        const val TYPE_FOLDER = 0
    }


    init {
        addItemType(0, R.layout.studyroom_item_move_resource)//0 默认类型
        addItemType(TYPE_NEW_FOLDER, R.layout.studyroom_item_add_resource)// 新建学习清单
        addItemType(TYPE_STUDY_ROOM, R.layout.studyroom_item_study_room)// 学习室
        addItemType(3, R.layout.studyroom_item_move_back)// 返回
    }

    override fun convert(holder: BaseViewHolder, item: MoveArticleBean) {
        when (item.itemType) {
            TYPE_FOLDER -> {//移动到文件夹
                if (item.next) {//判断是否有下一级
                    holder.setGone(R.id.tv_next, false)
                } else {
                    holder.setGone(R.id.tv_next, true)

                }
                holder.setText(R.id.tv_name,item.name)
            }
            TYPE_NEW_FOLDER -> {//点击弹框去新建学习清单
            }
            TYPE_STUDY_ROOM -> {//移动到学习是
            }
            TYPE_RETURN -> {//点击返回到上一栏
            }
            else -> {
            }
        }
    }
}