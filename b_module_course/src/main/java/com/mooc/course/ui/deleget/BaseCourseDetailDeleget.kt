package com.mooc.course.ui.deleget

import android.content.Context
import android.widget.TextView
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.course.R
import com.mooc.course.binding.XtCourseDetialBindings
import com.mooc.course.model.ChaptersBean
//import com.mooc.commonbusiness.model.search.ChaptersBean
import com.mooc.course.model.StaffInfo
import com.mooc.course.model.XtCourseStatus
import com.mooc.course.ui.activity.CourseDetailActivity
import com.mooc.course.ui.pop.TeacherDetailPop
import java.util.ArrayList

/**
 * 课程详情公共代理类，处理公共逻辑
 */
open class BaseCourseDetailDeleget {
//    var childTitles = mutableListOf<String>(CourseDetailActivity.TAB_STR_COURSE)
//    var childPositionList = mutableListOf<Int>(10)

    /**
     * 标题在列表中所在的位置
     */
//    var titleTabsPosition = LinkedHashMap<String,Int>()
    var titleTabsPosition = linkedMapOf(CourseDetailActivity.TAB_STR_COURSE to 10,)
    var newChapterlist: ArrayList<ChaptersBean> = arrayListOf<ChaptersBean>() //进行组装过后的章节列表

    /**
     * 获取章节列表（有可能多级）
     */
    fun getChapters(chapters: List<ChaptersBean>): List<ChaptersBean> {
        newChapterlist.clear()
        combinationList(chapters as ArrayList<ChaptersBean>, 0)
        return newChapterlist
    }

    /**
     * 一级一级遍历出所有章节
     * @param childList
     * @param level
     */
    private fun combinationList(childList: ArrayList<ChaptersBean>, level: Int) {
        for (i in childList.indices) {
            val chapter: ChaptersBean = childList[i]
            chapter.level = level
            newChapterlist.add(chapter)

            //新学堂多级章节列表，使用section_list字段
            val section_list: List<ChaptersBean>? = chapter.section_list
            if (section_list?.isNotEmpty() == true) {
                combinationList(section_list as ArrayList<ChaptersBean>, level + 1)
            }

            //旧学堂多级章节列表，使用sequentials字段
            val sequentials: List<ChaptersBean>? = chapter.sequentials
            if (sequentials?.isNotEmpty() == true) {
                combinationList(sequentials as ArrayList<ChaptersBean>, level + 1)
            }


        }
    }

    /**
     * 设置新学堂，和中国大学mooc
     * 课程状态
     */
    open fun setNewXTCourseClassStatus(tvClassStatus : TextView,status: XtCourseStatus) {
//        val tvClassStatus = mBindingView?.tvClassStatus
        tvClassStatus.let { tv ->

            when (status.status) {
                CourseDetailActivity.COURSE_STATUS_SIGNUP_READY -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stoke1primary_solidwhite)
                    tv.text = "立即报名"
                    tv.setTextColor(tv.context.resources.getColor(R.color.colorPrimary))
                }
                CourseDetailActivity.COURSE_STATUS_SIGNUP_NOT_START -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stroke1_gray9)
                    val formatDate = TimeFormatUtil.formatDate(
                        status.signup_start.toLong().times(1000), TimeFormatUtil.yyyy_MM_dd
                    )

                    if(formatDate == XtCourseDetialBindings.CLASS_NOT_START_TIME){
                        tv.text = XtCourseDetialBindings.CLASS_TIME_NOT_SURE
                    }else{
                        tv.text = "报名未开始(" + formatDate + "开始)"
                    }
                    tv.setTextColor(tv.context.resources.getColor(R.color.color_9))
                }
                CourseDetailActivity.COURSE_STATUS_SIGNUP_END -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stroke1_gray9)
                    tv.text = "报名已结束(" + TimeFormatUtil.formatDate(
                        status.signup_end.toLong().times(1000),
                        TimeFormatUtil.yyyy_MM_dd
                    ) + "结束)"
                    tv.setTextColor(tv.context.resources.getColor(R.color.color_9))
                }
                CourseDetailActivity.COURSE_STATUS_CLASS_END -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stroke1_gray9)

                    val endStr = if((status.class_end?:0) == 0L){
                        "已结课"
                    }else{
                        val minus = status.class_end?.times(1000)
                        "已结课(${TimeFormatUtil.formatDate(minus ?: 0, TimeFormatUtil.yyyy_MM_dd)}结课)"
                    }
                    tv.text = endStr
                    tv.setTextColor(tv.context.resources.getColor(R.color.color_9))
                }
                CourseDetailActivity.COURSE_STATUS_NOT_READY -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stroke1_gray9)
                    tv.text = "不可报名"
                    tv.setTextColor(tv.context.getColorRes(R.color.color_9))
                }
                CourseDetailActivity.COURSE_STATUS_LOOK_BACK -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stoke1primary_solidwhite)
                    var formatStr = "回顾学习"
                    if (status.days != null) {
                        formatStr += "（剩余时间" + TimeFormatUtil.formatXtCourseTime(
                            (status.days
                                ?: 0 * 1000)
                        ).toString() + "）"
                    }
                    tv.text = formatStr
                    tv.setTextColor(tv.context.getColorRes(R.color.colorPrimary))
                }
                CourseDetailActivity.COURSE_STATUS_CLASS_END_END -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stoke1primary_solidwhite)
                    tv.text = "已结课"
                    tv.setTextColor(tv.context.getColorRes(R.color.colorPrimary))
                }
                CourseDetailActivity.COURSE_STATUS_CLASS_NOT_START -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stroke1_gray9)
                    tv.text = "已报名(" + TimeFormatUtil.formatDate(
                        status.class_start.toLong().times(1000),
                        TimeFormatUtil.yyyy_MM_dd
                    ) + "开课)"
                    tv.setTextColor(tv.context.getColorRes(R.color.color_9))
                }
                CourseDetailActivity.COURSE_STATUS_READY -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stoke1primary_solidwhite)
                    tv.text = "已报名,去学习"
                    tv.setTextColor(tv.context.getColorRes(R.color.colorPrimary))
                }
                CourseDetailActivity.COURSE_STATUS_CLASS_END_NO_LOOK_BACK -> {
                    tv.setBackgroundResource(R.drawable.shape_radius25_stroke1_gray9)
                    //                tv.setText("已报名(" + formatDate(bean.getClass_end()) + "结课)");
                    tv.text = "老师暂停开课"
                    tv.setTextColor(tv.context.getColorRes(R.color.color_9))
                }
            }
        }

    }

    /**
     * 展示教师弹窗
     */
     protected fun showTeacherPop(context: Context, staffInfo: StaffInfo) {
        XPopup.Builder(context)
            .asCustom(TeacherDetailPop(context, staffInfo))
            .show()
    }
}