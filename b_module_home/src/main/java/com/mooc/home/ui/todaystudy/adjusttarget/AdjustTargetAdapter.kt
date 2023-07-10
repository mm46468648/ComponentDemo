package com.mooc.home.ui.todaystudy.adjusttarget

import android.graphics.Typeface
import android.widget.TextView
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.home.R
import com.mooc.home.model.todaystudy.TargetDetial
import org.jetbrains.annotations.NotNull

class AdjustTargetAdapter(list : ArrayList<TargetDetial>)
    : BaseDelegateMultiAdapter<TargetDetial, BaseViewHolder>(list) {

    companion object{
        const val ITEM_TYPE_CHECKIN = 0   //签到状态,或者学习计划
        const val ITEM_TYPE_OTHER_RESOURCE = 1    //其他资源类型

        const val RESOURCE_TYPE_CHECKIN = 1   //签到资源类型
        const val RESOURCE_TYPE__STUDY_PROJECT = 2   //学习项目资源类型
        const val RESOURCE_TYPE__COLUMN = 3  //专栏资源类型
        const val RESOURCE_TYPE__SPECIAL = 4   //专题资源类型
        const val RESOURCE_TYPE__MOSTHOT = 5   //最热资源类型
        const val RESOURCE_TYPE__COURSE = 6   //课程资源类型
        const val RESOURCE_TYPE__ALBUM = 7   //音频课资源类型
        const val RESOURCE_TYPE__EBOOK = 8   //电子书资源类型

        val resourceTitleMap = hashMapOf<Int,String>(
                1 to "签到打卡" ,
                2 to "学习项目" ,
                3 to "已订专栏" ,
                4 to "已订专题" ,
                5 to "大家最新在学的资源" ,
                6 to "课程" ,
                7 to "音频课" ,
                8 to "电子书",
                10 to "我的任务",
                51 to "刊物")
    }
    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<TargetDetial>() {
            override fun getItemType(@NotNull data: List<TargetDetial>, position: Int): Int {
                val obj = data[position]
                // 根据resource_type
                return when(obj.resource_type){
                    RESOURCE_TYPE_CHECKIN ,RESOURCE_TYPE__STUDY_PROJECT-> ITEM_TYPE_CHECKIN   //签到或者学习计划
                    else ->ITEM_TYPE_OTHER_RESOURCE
                }
            }
        })
        // 第二部，绑定 item 类型(course,ablum,ebook可复用相同布局) （学习计划二级列表布局也可复用）
        getMultiTypeDelegate()
                ?.addItemType(ITEM_TYPE_CHECKIN, R.layout.home_item_target_studyproject)
                ?.addItemType(ITEM_TYPE_OTHER_RESOURCE, R.layout.home_item_target_resource)

    }
    override fun convert(holder: BaseViewHolder, item: TargetDetial) {
        when(holder.itemViewType){
            ITEM_TYPE_CHECKIN->{   //签到或者学习计划
                if(item.resource_type == RESOURCE_TYPE_CHECKIN){ //签到
                    holder.setText(R.id.tvProjectName,"签到打卡提醒")
                }else{
                    holder.setText(R.id.tvProjectName,item.plan_name)
                }
                //设置开关
                val switchRes = if(item.is_open == 1) R.mipmap.home_ic_switch_open else R.mipmap.home_ic_switch_close
                holder.setImageResource(R.id.ivSwitch,switchRes)
            }
            else -> {    //其他资源
                //如果没有标题的，把标题字段设置为每日几条，再改一下大小
                val tvTargetTitle = holder.getView<TextView>(R.id.tvTargetTitle)
                if(item.colum_name.isEmpty()){
                    holder.setText(R.id.tvTargetTitle,"每日学习${item.resource_num}条")
                    if(item.is_open != 1){ //如果不是打开状态设置为0条
                        holder.setText(R.id.tvTargetTitle,"每日学习0条")
                    }
                    tvTargetTitle.setTypeface(Typeface.DEFAULT)
                    holder.setText(R.id.tvTargetNum,"")
                }else{
                    tvTargetTitle.setTypeface(Typeface.DEFAULT_BOLD)
                    holder.setText(R.id.tvTargetTitle,item.colum_name)
                    holder.setText(R.id.tvTargetNum,"每日学习${item.resource_num}条")
                    if(item.is_open != 1){ //如果不是打开状态设置为0条
                        holder.setText(R.id.tvTargetNum,"每日学习0条")
                    }
                }
                holder.setGone(R.id.tvTargetNum,item.colum_name.isEmpty())
            }
        }

        //设置资源标题
        holder.setText(R.id.tvResourceName, resourceTitleMap[item.resource_type])
        //除了第一条签到，如果当前item和上一个资源类型相同，则隐藏头部
        val adapterPosition = holder.adapterPosition
        if(adapterPosition ==0){
            holder.setGone(R.id.tvResourceName,true)
        }else{
            val lastItemViewType = data.get(adapterPosition - 1).resource_type
            val currentItemViewType = data.get(adapterPosition).resource_type

            //如果不一样则显示头部标题
            holder.setGone(R.id.tvResourceName,currentItemViewType ==lastItemViewType)
        }

        //除了最后一条，如何当前条和下一条资源类型相同，则显示底部分割线，
        if(adapterPosition < data.size-1){
            val nextItemViewType = data.get(adapterPosition+1).resource_type
            val currentItemViewType = data.get(adapterPosition).resource_type
            holder.setGone(R.id.viewBottomLine,currentItemViewType != nextItemViewType)
        }



    }

}