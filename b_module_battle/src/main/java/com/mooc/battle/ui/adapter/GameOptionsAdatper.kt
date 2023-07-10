package com.mooc.battle.ui.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.battle.GameConstant
import com.mooc.battle.R
import com.mooc.battle.model.GameOptions

/**
 * 游戏选项适配器
 */
class GameOptionsAdatper(data: ArrayList<GameOptions>?) :
    BaseQuickAdapter<GameOptions, BaseViewHolder>(
        R.layout.item_game_option, data
    ) {

    var itemClick: ((item: GameOptions) -> Unit)? = null

    var right_answer = GameConstant.GAME_DEFAULT_ORDER_VALUE        //正确答案
    var my_answer = GameConstant.GAME_DEFAULT_ORDER_VALUE    //自己的答案
    var other_answer = GameConstant.GAME_DEFAULT_ORDER_VALUE //对手的答案

    override fun convert(holder: BaseViewHolder, item: GameOptions) {
        holder.setText(R.id.tvOption, item.title)
        if (right_answer == GameConstant.GAME_DEFAULT_ORDER_VALUE) {   //答案未出来之前
            if (item.order == my_answer) {            //如果我选了,变灰
                holder.itemView.setBackgroundResource(R.drawable.bg_game_options_myselect)
            } else {//其他透明
                holder.itemView.setBackgroundResource(R.drawable.bg_game_options_default)
            }
            holder.setImageResource(R.id.ivMySelect, R.drawable.bg_transparent_oval)
            holder.setImageResource(R.id.ivOtherSelect, R.drawable.bg_transparent_oval)
        } else {  //答案出来之后
            if (item.order == right_answer) {  //正确变绿
                holder.itemView.setBackgroundResource(R.drawable.bg_game_options_right)
                if (item.order == my_answer) {         //我如果选了正确的设置左边对号
                    holder.setImageResource(R.id.ivMySelect, R.mipmap.ic_game_option_select_right)
                } else {
                    holder.setImageResource(R.id.ivMySelect, R.drawable.bg_transparent_oval)
                }

                if (item.order == other_answer) { //如果对面是正确选择,设置右边对号
                    holder.setImageResource(
                        R.id.ivOtherSelect,
                        R.mipmap.ic_game_option_select_right
                    )
                } else {
                    holder.setImageResource(R.id.ivOtherSelect, R.drawable.bg_transparent_oval)
                }

            } else {
                //如果该选项是我或对手选的错误答案变红
                if (item.order == my_answer || item.order == other_answer) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_game_options_error)
                } else {
                    holder.itemView.setBackgroundResource(R.drawable.bg_game_options_default)
                }

                if (item.order == my_answer) {
                    holder.setImageResource(R.id.ivMySelect, R.mipmap.ic_game_option_select_error)
                } else {
                    holder.setImageResource(R.id.ivMySelect, R.drawable.bg_transparent_oval)
                }

                if (item.order == other_answer) { //如果对面是正确选择,设置右边对号
                    holder.setImageResource(
                        R.id.ivOtherSelect,
                        R.mipmap.ic_game_option_select_error
                    )
                } else {
                    holder.setImageResource(R.id.ivOtherSelect, R.drawable.bg_transparent_oval)
                }
            }
        }


        holder.getView<TextView>(R.id.tvOption).setOnClickListener {
            itemClick?.invoke(item)
        }

        holder.itemView.setOnClickListener {
            itemClick?.invoke(item)
        }
    }


    fun setMySelect(my_answer: String) {
        this.my_answer = my_answer
        notifyDataSetChanged()
    }


    fun setResult(right_answer: String, my_answer: String, other_answer: String) {
        this.right_answer = right_answer
        this.my_answer = my_answer
        this.other_answer = other_answer

        notifyDataSetChanged()
    }


}