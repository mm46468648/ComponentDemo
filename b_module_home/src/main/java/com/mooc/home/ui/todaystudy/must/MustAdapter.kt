package com.mooc.home.ui.todaystudy.must

import android.text.SpannableString
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.home.R
import com.mooc.home.model.todaystudy.*
import org.jetbrains.annotations.NotNull

/**
 * 最近在学适配器
 */
class MustAdapter(list: ArrayList<Any>) : BaseDelegateMultiAdapter<Any, BaseViewHolder>(list) {

    companion object{
        const val ITEM_TYPE_CHECKIN = 0   //签到状态
        const val ITEM_TYPE_COURSE = 1    //课程
        const val ITEM_TYPE_ALBUM = 2    //音频
        const val ITEM_TYPE_STUDYPROJECT = 3    //学习计划
        const val ITEM_TYPE_EBOOK = 4   //电子书
        const val ITEM_TYPE_HOT = 5    //最热资源
        const val ITEM_TYPE_ITEM_TYPE_STUDYPROJECT_SECOND = 6    //学习计划二级列表
        const val ITEM_TYPE_ITEM_TYPE_HOT_SECOND = 7    //大家最新在学的资源

    }
    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<Any>() {
            override fun getItemType(@NotNull data: List<Any>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                val obj = data[position]
                //如果类型，不属于任何样式，返回默认样式
                return when{
                    obj is CheckInStatus -> ITEM_TYPE_CHECKIN
                    obj is CourseStatus -> ITEM_TYPE_COURSE
                    obj is AlbumStatus -> ITEM_TYPE_ALBUM
                    obj is StudyplanStatus -> ITEM_TYPE_STUDYPROJECT
                    obj is EbookStatus -> ITEM_TYPE_EBOOK
                    obj is MostHotStatus -> ITEM_TYPE_HOT
                    obj is StudyPlanResource -> ITEM_TYPE_ITEM_TYPE_STUDYPROJECT_SECOND
                    obj is HotResource -> ITEM_TYPE_ITEM_TYPE_HOT_SECOND
                    else ->-1
                }
            }
        })
        // 第二部，绑定 item 类型(course,ablum,ebook可复用相同布局) （学习计划二级列表布局也可复用）
        getMultiTypeDelegate()
                ?.addItemType(ITEM_TYPE_CHECKIN, R.layout.home_item_todaystudy_must_checkin)
                ?.addItemType(ITEM_TYPE_COURSE, R.layout.home_item_todaystudy_must_course_new)
                ?.addItemType(ITEM_TYPE_ALBUM, R.layout.home_item_todaystudy_must_course)
                ?.addItemType(ITEM_TYPE_STUDYPROJECT, R.layout.home_item_todaystudy_must_studyproject)
                ?.addItemType(ITEM_TYPE_EBOOK, R.layout.home_item_todaystudy_must_course_new)
                ?.addItemType(ITEM_TYPE_HOT, R.layout.home_item_todaystudy_must_hot)
                ?.addItemType(ITEM_TYPE_ITEM_TYPE_STUDYPROJECT_SECOND, R.layout.home_item_todaystudy_must_course)
                ?.addItemType(ITEM_TYPE_ITEM_TYPE_HOT_SECOND, R.layout.home_item_todaystudy_must_hot_second)

    }
    override fun convert(holder: BaseViewHolder, item: Any) {
        //绑定课程，音频，电子书数据
        when(item){
            is CheckInStatus->{   //签到
                //签到天数放大
                val spannableString = spannableString {
                    str = "已连续签到${item.continues_days}天"
                    scaleSpan {
                        start = str.indexOf(item.continues_days)
                        end = str.indexOf(item.continues_days) + item.continues_days.length
                        scale = 1.4f
                    }
                }
                val tvCheckInStatusStr = if (item.has_checkin) "已打卡" else "打卡"
                holder.setText(R.id.tvTostudy,tvCheckInStatusStr)
//                holder.setText(R.id.tvDesc,"已连续签到${item.continues_days}天")
                holder.setText(R.id.tvDesc,spannableString)
                addChildClickViewIds(R.id.tvTostudy)
            }
            is CourseStatus->{    //课程
                holder.setText(R.id.tvName, "课程")
                holder.setText(R.id.tvTostudy, "去学习")

                holder.setText(R.id.tvTitle, item.title)
//                holder.setText(R.id.tvTime, "上次" + TimeFormatUtil.formatDate(item.update_time * 1000, "yyyy.MM.dd   HH:mm").toString() + "学过")
                addChildClickViewIds(R.id.tvTostudy)

                //仅学堂在线0、智慧树33、中国大学MOOC 6 课程显示已学、得分数据
                if(item.platform == CoursePlatFormConstants.COURSE_PLATFORM_XTZX
                    || item.platform == CoursePlatFormConstants.COURSE_PLATFORM_MOOC
                    || item.platform == CoursePlatFormConstants.COURSE_PLATFORM_ZHS
                    || item.platform == CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT){

                    holder.setGone(R.id.tvProcess,false)

                    //设置进度
                    val processStr = "已学: ${item.learned_process * 100}%"
                    holder.setText(R.id.tvProcess,getColorSpan(processStr))


                    //设置得分,mooc没有得分不展示
                    if(item.platform == CoursePlatFormConstants.COURSE_PLATFORM_MOOC){
                        holder.setGone(R.id.tvScore,true)
                    }else{
                        holder.setGone(R.id.tvScore,false)
                        val scoreStr = "得分: ${item.score}分"
                        holder.setText(R.id.tvScore,getColorSpan(scoreStr))
                    }

                }else{
                    holder.setGone(R.id.tvProcess,true)
                    holder.setGone(R.id.tvScore,true)
                }
            }
            is AlbumStatus->{     //音频
                holder.setText(R.id.tvName, "音频课")
                holder.setText(R.id.tvTostudy, "继续听")

                holder.setText(R.id.tvTitle, item.title)
                holder.setText(R.id.tvTime, "上次" + TimeFormatUtil.formatDate(item.update_time * 1000, "yyyy.MM.dd   HH:mm").toString() + "学过")
                addChildClickViewIds(R.id.tvTostudy)
            }
            is EbookStatus->{     //电子书
                holder.setText(R.id.tvName, "电子书")
                holder.setText(R.id.tvTostudy, "继续看")

                holder.setText(R.id.tvTitle, item.title)
//                holder.setText(R.id.tvTime, "上次" + TimeFormatUtil.formatDate(item.update_time * 1000, "yyyy.MM.dd   HH:mm").toString() + "学过")
                addChildClickViewIds(R.id.tvTostudy)

                val processStr = "进度: ${item.read_process}%"
                val readTimeStr = "时长: ${TimeFormatUtil.formatEbookReadTime(item.read_times)}"
                holder.setText(R.id.tvProcess,getColorSpan(processStr))
                holder.setText(R.id.tvScore,getColorSpan(readTimeStr))

            }

            is StudyPlanResource->{ //学习计划二级列表item
                holder.setText(R.id.tvTitle, item.study_plan)
            }
            is HotResource->{ //大家最新在学资源二级列表item
                holder.setText(R.id.tvHotTitle, item.title)
            }
        }

        //设置学习计划
        if(item is StudyplanStatus){
            //设置学习计划标题，及二级列表
            holder.setText(R.id.tvStudyPlanName, item.plan_name)
            holder.setText(R.id.tvStudyPlanProgress, item.plan_user_do.toString() + "/" + item.plan_resource_num.toString())

//            val resourceList = item.resource_list?:ArrayList<Any>()
//            val rvStudyPlan = holder.getView<RecyclerView>(R.id.rvStudyPlan)
//            rvStudyPlan.layoutManager = LinearLayoutManager(context)
//            rvStudyPlan.adapter = MustAdapter(resourceList as ArrayList<Any>)
        }

        //设置大家最新在学
        if(item is MostHotStatus){
            val resourceList = item.resource_list?:ArrayList()
            val rvStudyPlan = holder.getView<RecyclerView>(R.id.rvStudyPlan)
            rvStudyPlan.layoutManager = LinearLayoutManager(context)
            val mustAdapter = MustAdapter(resourceList as ArrayList<Any>)
            mustAdapter.setOnItemChildClickListener { adapter, view, position ->
                ResourceTurnManager.turnToResourcePage(resourceList[position])
            }
            rvStudyPlan.adapter = mustAdapter
        }

        if(item is CheckInStatus || item is StudyPlanResource || item is HotResource){
            return
        }

        //设置分割条和头部标题
        val adapterPosition = holder.layoutPosition
//        //如果没有签到的情况下，第一条也隐藏分割线
//        if(adapterPosition == 0 && item !is CheckInStatus) {
//            holder.getView<View>(R.id.viewSpaceTop).visibility = View.GONE
//        }

        //获取上一个资源类型，如果一样，则隐藏标题,如果有headview，要跳过headview
        val startPosition = headerLayoutCount
        val view = holder.getView<View>(R.id.viewSpaceTop)
        val tvName = holder.getView<View>(R.id.tvName)
        if(adapterPosition > startPosition){
            val lastItemViewType = getItemViewType(adapterPosition - 1)
            val currentItemViewType = getItemViewType(adapterPosition)

            if(currentItemViewType!=lastItemViewType){  //如果不一样则显示分割条,显示头部标题
                view.visibility = View.VISIBLE
                tvName.visibility = View.VISIBLE
            }else {
                view.visibility = View.GONE  //如果一样则隐藏分割条,隐藏头部标题
                tvName.visibility = View.GONE
            }
        }else{
            view.visibility = View.GONE
            tvName.visibility = View.VISIBLE
        }




    }


    fun getColorSpan(str:String) : SpannableString {
        return spannableString {
            this.str = str
            colorSpan {
                start = str.indexOf(":") + 1
                end = str.length
                color = context.getColorRes(R.color.colorPrimary)
            }
        }
    }
}