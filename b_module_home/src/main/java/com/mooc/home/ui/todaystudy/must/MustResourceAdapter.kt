package com.mooc.home.ui.todaystudy.must

import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.home.R
import com.mooc.home.model.todaystudy.*
import com.mooc.statistics.LogUtil
import org.jetbrains.annotations.NotNull
import java.text.DecimalFormat

/**
 * 最近在学子资源列表适配器
 */
class MustResourceAdapter(list: ArrayList<Any>) :
    BaseDelegateMultiAdapter<Any, BaseViewHolder>(list) {

    companion object {
        const val ITEM_TYPE_CHECKIN = 0   //签到状态
        const val ITEM_TYPE_COURSE = 1    //课程
        const val ITEM_TYPE_ALBUM = 2    //音频
        const val ITEM_TYPE_STUDYPROJECT = 3    //学习计划
        const val ITEM_TYPE_EBOOK = 4   //电子书
        const val ITEM_TYPE_HOT = 5    //最热资源
        const val ITEM_TYPE_PUBLICATION = 6    //刊物
        const val ITEM_TYPE_TASK = 7    //我的任务

    }

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<Any>() {
            override fun getItemType(@NotNull data: List<Any>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                val obj = data[position]
                //如果类型，不属于任何样式，返回默认样式
                return when {
                    obj is CheckInStatus -> ITEM_TYPE_CHECKIN
                    obj is CourseStatus -> ITEM_TYPE_COURSE
                    obj is AlbumStatus -> ITEM_TYPE_ALBUM
                    obj is StudyplanStatus -> ITEM_TYPE_STUDYPROJECT
                    obj is EbookStatus -> ITEM_TYPE_EBOOK
                    obj is HotResource -> ITEM_TYPE_HOT
                    obj is Publication -> ITEM_TYPE_PUBLICATION
                    obj is TaskStatus -> ITEM_TYPE_TASK
                    else -> -1
                }
            }
        })
        // 第二部，绑定 item 类型(course,ablum,ebook可复用相同布局) （学习计划二级列表布局也可复用）
        getMultiTypeDelegate()
            ?.addItemType(ITEM_TYPE_CHECKIN, R.layout.home_item_todaystudy_must_checkin)
            ?.addItemType(ITEM_TYPE_COURSE, R.layout.home_item_todaystudy_must_course_new)
            ?.addItemType(ITEM_TYPE_ALBUM, R.layout.home_item_todaystudy_must_course)
            ?.addItemType(ITEM_TYPE_PUBLICATION, R.layout.home_item_todaystudy_must_course)
            ?.addItemType(ITEM_TYPE_STUDYPROJECT, R.layout.home_item_todaystudy_must_studyproject)
            ?.addItemType(ITEM_TYPE_EBOOK, R.layout.home_item_todaystudy_must_course_new)
            ?.addItemType(ITEM_TYPE_HOT, R.layout.home_item_todaystudy_must_hot_second)
            ?.addItemType(ITEM_TYPE_TASK, R.layout.home_item_todaystudy_must_task)
            ?.addItemType(ResourceTypeConstans.TYPE_UNDEFINE, R.layout.item_recommend_empty)


    }

    override fun convert(holder: BaseViewHolder, item: Any) {
        SkinManager.getInstance().injectSkin(holder.itemView)
        //绑定课程，音频，电子书数据
        when (item) {
            is CheckInStatus -> { //签到
                //签到天数放大
                val spannableString = spannableString {
                    str = "已连续签到 ${item.continues_days} 天"
                    absoluteSpan {
                        start = str.indexOf(item.continues_days)
                        end = str.indexOf(item.continues_days) + item.continues_days.length
                        size = 30.dp2px()
                    }
                }
                val tvCheckInStatusStr = if (item.has_checkin) "已打卡" else "打卡"
                holder.setText(R.id.tvTostudy, tvCheckInStatusStr)
//                holder.setText(R.id.tvDesc,"已连续签到${item.continues_days}天")
                holder.setText(R.id.tvDesc, spannableString)
                holder.getView<TextView>(R.id.tvTostudy).setOnClickListener {
                    LogUtil.addClickLogNew(
                        LogEventConstants2.P_TODAY,
                        "1",
                        LogEventConstants2.ET_CHECK,
                        "打卡"
                    )
                    ARouter.getInstance().build(Paths.PAGE_CHECKIN).navigation()
                }
            }
            is CourseStatus -> {    //课程
                holder.setText(R.id.tvName, "课程")
                holder.setText(R.id.tvTostudy, "去学习")

                holder.setText(R.id.tvTitle, item.title)
//                holder.setText(R.id.tvTime, "上次" + TimeFormatUtil.formatDate(item.update_time * 1000, "yyyy.MM.dd   HH:mm").toString() + "学过")
                addChildClickViewIds(R.id.tvTostudy)

                //仅好大学在线，学堂在线0、智慧树33、中国大学MOOC ,工信部付费课课程显示已学、得分数据
                if (item.platform == CoursePlatFormConstants.COURSE_PLATFORM_XTZX
                    || item.platform == CoursePlatFormConstants.COURSE_PLATFORM_MOOC
                    || item.platform == CoursePlatFormConstants.COURSE_PLATFORM_ZHS
                    || item.platform == CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT
                    || item.platform == CoursePlatFormConstants.COURSE_GOOD_COLLEGE
                    || (item.platform == CoursePlatFormConstants.COURSE_PUBLIC_MESSAGE && item.platform_category == 1)
                ) {

                    holder.setGone(R.id.tvProcess, false)
                    //设置进度
//                    val processStr = "已学: ${Math.round(item.learned_process * 100) / 100f}%"
                    val processStr =
                        "已学: ${getNoMoreThanTwoDigits(item.learned_process.toDouble())}"
                    holder.setText(R.id.tvProcess, getColorSpan(processStr))
                    if (!TextUtils.isEmpty(item.score)) {//不为空
                        val scoreStr = "得分: ${item.score}分"
                        holder.setText(R.id.tvScore, getColorSpan(scoreStr))
                        if (item.platform == CoursePlatFormConstants.COURSE_PUBLIC_MESSAGE) {//工信部课程
                            holder.setGone(R.id.tvScore, item.is_show_score != true)
                        } else {
                            //设置得分, 0分，0.0 分都是显示空，其他显示得分
                            holder.setGone(R.id.tvScore, item.score == "0" || item.score == "0.0")
                        }
                    } else {//为空不显示
                        holder.setGone(R.id.tvScore, true)
                    }


                } else {
                    holder.setGone(R.id.tvProcess, true)
                    holder.setGone(R.id.tvScore, true)
                }
            }
            is AlbumStatus -> {     //音频
                holder.setText(R.id.tvName, "音频课")
                holder.setText(R.id.tvTostudy, "继续听")

                holder.setText(R.id.tvTitle, item.title)
                holder.setText(
                    R.id.tvTime,
                    "上次" + TimeFormatUtil.formatDate(item.update_time * 1000, "yyyy.MM.dd   HH:mm")
                        .toString() + "学过"
                )
                addChildClickViewIds(R.id.tvTostudy)
            }
            is Publication -> {     //刊物
                holder.setText(R.id.tvName, "刊物")
                holder.setText(R.id.tvTostudy, "去学习")

                holder.setText(R.id.tvTitle, item.title)
                holder.setText(
                    R.id.tvTime,
                    "上次" + TimeFormatUtil.formatDate(item.update_time * 1000, "yyyy.MM.dd   HH:mm")
                        .toString() + "学过"
                )
                addChildClickViewIds(R.id.tvTostudy)
            }

            is EbookStatus -> {     //电子书
                holder.setText(R.id.tvName, "电子书")
                holder.setText(R.id.tvTostudy, "继续看")

                holder.setText(R.id.tvTitle, item.title)
//                holder.setText(R.id.tvTime, "上次" + TimeFormatUtil.formatDate(item.update_time * 1000, "yyyy.MM.dd   HH:mm").toString() + "学过")
                addChildClickViewIds(R.id.tvTostudy)

                val processStr = "进度: ${item.read_process}%"
                val readTimeStr = "累计时长: ${TimeFormatUtil.formatEbookReadTime(item.read_times)}"
                holder.setText(R.id.tvProcess, getColorSpan(processStr))
                holder.setText(R.id.tvScore, getColorSpan(readTimeStr))

            }

            is StudyplanStatus -> { //学习计划二级列表item
                holder.setText(R.id.tvStudyPlanName, item.plan_name)
                holder.setText(
                    R.id.tvStudyPlanProgress,
                    item.plan_user_do.toString() + "/" + item.plan_resource_num.toString()
                )
            }
            is HotResource -> { //大家最新在学资源二级列表item
                holder.setText(R.id.tvHotTitle, item.title)
            }
            is TaskStatus -> { //我的任务
                holder.setText(R.id.tvTitle, item.title)

                val value2 = "距任务截止还有${item.remain_days}天"

                //换肤
                val skinColor = if (SkinManager.getInstance().needChangeSkin()) {
                    SkinManager.getInstance().resourceManager.getColor("colorPrimary")
                } else {
                    context.getColorRes(R.color.colorPrimary)
                }
                val spannableString2 = spannableString {
                    str = value2
                    colorSpan {
                        color = skinColor
                        start = value2.indexOf("${item.remain_days}")
                        end =
                            value2.indexOf("${item.remain_days}") + "${item.remain_days}".length
                    }
                }
                holder.setText(R.id.tvTaskEndTime, spannableString2)

                //永久任务不显示截止时间
                holder.setGone(R.id.tvTaskEndTime, item.task_data?.time_mode == 1)

                val totalCount = item.total_count
                val completedCount = item.completed_count


                val completeStr = "已完成: ${completedCount}/${totalCount}"
                val completeSpannableString = spannableString {
                    str = completeStr
                    colorSpan {
                        color = skinColor
                        start = 4
                        end = completeStr.length
                    }
                }


                val todayComplete = "今日已完成: ${item.today_finish}"
                val todayCompleteSpannableString = spannableString {
                    str = todayComplete
                    colorSpan {
                        color = skinColor
                        start = 6
                        end = todayComplete.length
                    }
                }

                if (item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_NOMAL) {
                    //普通任务,显示已完成A/B,今日已完成个数
                    holder.setText(R.id.tvSecond, completeSpannableString)
                    holder.setText(R.id.tvThird, todayCompleteSpannableString)
                    holder.setGone(R.id.tvThird, false)
                } else if (item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_CUSTOM && item.task_data?.type == TaskConstants.TASK_TYPE_REED_BOOK) {
                    //青年读书会定制任务,显示已完成A/B,今日已完成个数
                    holder.setText(R.id.tvSecond, completeSpannableString)
                    holder.setText(R.id.tvThird, todayCompleteSpannableString)
                    holder.setGone(R.id.tvThird, false)
                } else if (item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_SUM || item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_CONTINUE) {
                    //累计,连续任务,显示今日已完成A/B
                    val value = "今日已完成: ${completedCount}/${totalCount}"
                    val valueSpannableString = spannableString {
                        str = value
                        colorSpan {
                            color = skinColor
                            start = 6
                            end = value.length
                        }
                    }
                    holder.setText(R.id.tvSecond, valueSpannableString)
                    holder.setGone(R.id.tvThird, true)
                } else if (item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_COMBINE) {
                    //组合任务已完成A/B、今日已完成个数
                    holder.setText(R.id.tvSecond, completeSpannableString)
                    holder.setText(R.id.tvThird, todayCompleteSpannableString)
                    holder.setGone(R.id.tvThird, false)
                }
            }

        }

        //除了签到,和默认隐藏最后一个分割线
        if (item !is CheckInStatus && holder.getViewOrNull<View>(R.id.viewLine) != null) {
            val i = holder.adapterPosition == data.size - 1
            holder.setGone(R.id.viewLine, i)
        }
    }


    fun getColorSpan(str: String): SpannableString {
        //换肤
        val skinColor =
            SkinManager.getInstance().resourceManager.getColor("colorPrimary")

        return spannableString {
            this.str = str
            colorSpan {
                start = str.indexOf(":") + 1
                end = str.length
                color = skinColor
            }
        }
    }

    fun getNoMoreThanTwoDigits(number: Double): String {
        return DecimalFormat("#0.00%").format(number)
    }


}
