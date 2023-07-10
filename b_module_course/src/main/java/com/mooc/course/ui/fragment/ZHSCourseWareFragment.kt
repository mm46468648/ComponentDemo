@file:Suppress("DEPRECATION")

package com.mooc.course.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.course.model.LessonInfo
import com.mooc.course.ui.activity.CoursePlayActivity
import com.mooc.course.ui.adapter.ZHSCourseWareAdapter
import com.mooc.course.viewmodel.CoursePlayViewModel
import com.mooc.course.viewmodel.ZHSCourseWareViewModel

/**
 * 智慧树课件fragment
 */
class ZHSCourseWareFragment : BaseListFragment<LessonInfo, ZHSCourseWareViewModel>() {


    private val courseModel: CoursePlayViewModel by lazy {
        ViewModelProviders.of(requireActivity())[CoursePlayViewModel::class.java]
    }

    companion object {
        fun getInstance(courseId: String, courseBean: CourseBean): ZHSCourseWareFragment {
            val courseWareFragment = ZHSCourseWareFragment()
            courseWareFragment.arguments = Bundle().put(IntentParamsConstants.COURSE_PARAMS_ID, courseId).put(IntentParamsConstants.COURSE_PARAMS_DATA, courseBean)
            return courseWareFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel?.playUrl?.observe(viewLifecycleOwner, {
            if (it.isNotEmpty())
                courseModel.videlUrlStringData.postValue(it)
        })

        mViewModel?.playUrlAndPosition?.observe(viewLifecycleOwner, {
            courseModel.videlUrlAndPositionData.postValue(it)
        })

        mViewModel?.lastPlayChapterIndex?.observe(viewLifecycleOwner, {
            val zhsCourseWareAdapter = mAdapter as ZHSCourseWareAdapter
            //延迟一会儿移动到当前播放位置
            recycler_view.postDelayed(
                    {
                        zhsCourseWareAdapter.currentPlay = it
                        recycler_view.smoothScrollToPosition(it)
                    }, 500)

        })


        //activity主动跳用视频播放结束，切换下一个列表  todo 智慧树目前还没有用
        courseModel.videoEndLiveData.observe(viewLifecycleOwner, {
            //同步Adapter的显示
            (mAdapter as ZHSCourseWareAdapter).currentPlay++
            mViewModel?.changeNextSequential()
        })

        //activity主动传入需要记录的视频播放位置，下次续播
        courseModel.currentPlayPosition.observe(viewLifecycleOwner, {
            mViewModel?.saveCurrentLessonPlayPosition(it)
        })
    }

    override fun initAdapter(): BaseQuickAdapter<LessonInfo, BaseViewHolder>? {
        mViewModel?.courseId = (arguments?.getString(IntentParamsConstants.COURSE_PARAMS_ID) ?: "")
        mViewModel?.courseBean = (arguments?.getSerializable(IntentParamsConstants.COURSE_PARAMS_DATA)) as CourseBean
        return mViewModel?.getPageData()?.value?.let {
            val zhsCourseWareAdapter = ZHSCourseWareAdapter(it)
            zhsCourseWareAdapter.setOnItemClickListener { _, _, position ->
                zhsCourseWareAdapter.currentPlay = position

                //主动获取一下当前播放进度
                if (requireActivity() is CoursePlayActivity) {
                    mViewModel?.saveCurrentLessonPlayPosition((requireActivity() as CoursePlayActivity).getPlayPosition())
                }
                mViewModel?.changeChapter(position)
            }
            zhsCourseWareAdapter
        }
    }

    override fun neadLoadMore(): Boolean = false

}