package com.mooc.course.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.course.model.CourseNotice
import com.mooc.course.ui.adapter.CourseNoticeAdapter
import com.mooc.course.viewmodel.CourseNoticeViewModel
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.put
import com.mooc.home.ui.decoration.Padding15Divider1Decoration

/**
 * 课程公告
 * 不需要分页
 */
class CourseNoticeFragment : BaseListFragment<CourseNotice,CourseNoticeViewModel>() {

    val xtCourseId  by extraDelegate(IntentParamsConstants.COURSE_XT_PARAMS_ID,"")
    companion object{
        fun getInstence(courseId : String) : CourseNoticeFragment{
            val courseWareFragment = CourseNoticeFragment()
            courseWareFragment.arguments = Bundle().put(IntentParamsConstants.COURSE_XT_PARAMS_ID,courseId)
            return courseWareFragment
        }
    }

    override fun initAdapter(): BaseQuickAdapter<CourseNotice, BaseViewHolder>? {
        mViewModel?.xtCourseId = xtCourseId
        return mViewModel?.getPageData()?.value?.let {
            CourseNoticeAdapter(it)
        }
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return Padding15Divider1Decoration()
    }
    override fun neadLoadMore(): Boolean {
        return false
    }
}