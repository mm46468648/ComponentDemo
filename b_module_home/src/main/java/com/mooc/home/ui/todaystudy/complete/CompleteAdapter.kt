package com.mooc.home.ui.todaystudy.complete

import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.repackaged.com.squareup.javapoet.TypeSpec
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.getDrawableRes
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.home.R
import com.mooc.home.model.todaystudy.TodayTask
import com.mooc.resource.widget.Space120FootView

class CompleteAdapter(list:ArrayList<TodayTask>)
    : BaseQuickAdapter<TodayTask,BaseViewHolder>(R.layout.home_item_todaystudy_complete,list) {


    override fun convert(holder: BaseViewHolder, item: TodayTask) {
        //设置类型名
        holder.setText(R.id.tvName,item.name)
        //签到打卡，隐藏一集标题,其他显示，显示已打卡状态，其他隐藏
        holder.setGone(R.id.tvTitle,CompleteViewModel.CHECKIN_TITLE == item.name)
//        holder.setGone(R.id.tvState,CompleteViewModel.CHECKIN_TITLE != item.name)

        //换肤
        val tvResourceTitle = holder.getView<TextView>(R.id.tvName)
        val tvResourceTitleDrawableLefet =
            SkinManager.getInstance().resourceManager.getDrawableByName("shape_width4_height17_primary")
        tvResourceTitle.setDrawLeft(tvResourceTitleDrawableLefet,10.dp2px())
        val tvState = holder.getView<TextView>(R.id.tvState)
        val tvStateColor = SkinManager.getInstance().resourceManager.getColor("colorPrimary")
        tvState.setTextColor(tvStateColor)
        //设置下面
        when(item.name){
            CompleteViewModel.CHECKIN_TITLE->{
                holder.setText(R.id.tvTitle,"")
                val continuesDays = item.checkin_status?.get(0)?.continues_days.toString()
                val continuesStr = "已经连续签到${continuesDays}天"
//                holder.setGone(R.id.tvState,false)
                holder.setText(R.id.tvState,"已打卡")

                val spannableString = spannableString {
                    str = continuesStr
                    colorSpan {
                        start = continuesStr.indexOf(continuesDays)
                        end = continuesStr.indexOf(continuesDays) + continuesDays.length
                        color = Color.parseColor("#1982ff")
                    }
                }
                holder.setGone(R.id.tvName,false)
                holder.setText(R.id.tvDesc,spannableString)

                holder.getView<TextView>(R.id.tvState).setTextSize(15f)
            }
            else->{
                //设置头部标题和一级标题
                holder.setText(R.id.tvTitle,item.title)
                holder.setText(R.id.tvDesc,item.desc)

                val stateStr = if(item.name == CompleteViewModel.TASK_TITLE) "已完成" else ""
                holder.setText(R.id.tvState,stateStr)
                holder.getView<TextView>(R.id.tvState).setTextSize(11f)

                //获取上一个资源类型，如果一样，则隐藏标题
                val adapterPosition = holder.layoutPosition-headerLayoutCount
                if(adapterPosition > 0){

                    val lastItemViewType = data.get(adapterPosition - 1).name
                    val currentItemViewType = data.get(adapterPosition).name

                    //如果不一样则显示头部标题,和分割线
                    holder.setGone(R.id.tvName,currentItemViewType ==lastItemViewType)


                    //根据一级标题是否一样，判断是否现实一级标题
                    val lastTitle = data[adapterPosition - 1].title
                    val currentTitle = data[adapterPosition].title
                    holder.setGone(R.id.tvTitle,lastTitle == currentTitle)

                }else{
                    holder.setGone(R.id.tvName,false)
                    holder.setGone(R.id.tvTitle,false)
                }

                holder.setGone(R.id.tvTitle,item.title.isEmpty())

                //是否显示分割线
                if(adapterPosition < data.size - 1 - footerLayoutCount){
                    val lastItemViewType = data.get(adapterPosition + 1).name
                    val currentItemViewType = data.get(adapterPosition).name
                    holder.setGone(R.id.viewBottomLine,currentItemViewType ==lastItemViewType)
                }else{
                    holder.setGone(R.id.viewBottomLine,true)
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        setFooterView(Space120FootView(context))
    }
}