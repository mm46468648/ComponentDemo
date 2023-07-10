package com.mooc.course.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.course.viewmodel.CourseListViewModel
import com.mooc.course.viewmodel.CoursePlayViewModel
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.course.R
import com.mooc.course.model.SequentialBean
import com.mooc.course.ui.adapter.CourseChapterListAdapter
import com.mooc.download.DownloadListener
import com.mooc.download.DownloadModel
import com.mooc.download.DownloadModelBuilder
import com.gavin.com.library.StickyDecoration
import com.gavin.com.library.listener.GroupListener
import com.mooc.download.DownloadManager
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.resource.ktextention.dp2px

/**
 * 课件
 * 不需要分页
 */
class CourseWareFragment : BaseListFragment<SequentialBean, CourseListViewModel>() {

    val xtCourseId by extraDelegate(IntentParamsConstants.COURSE_XT_PARAMS_ID, "")
    val parentModel: CoursePlayViewModel by lazy {
//        activity?.let {
//            ViewModelProviders.of(it, BaseViewModelFactory(xtCourseId))
//        }!![CoursePlayViewModel::class.java]
        ViewModelProviders.of(requireActivity())[CoursePlayViewModel::class.java]
    }


    companion object {
        fun getInstence(courseId: String): CourseWareFragment {
            val courseWareFragment = CourseWareFragment()
            courseWareFragment.arguments = Bundle().put(IntentParamsConstants.COURSE_XT_PARAMS_ID, courseId)
            return courseWareFragment
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //视频地址切换，设置给activity
        mViewModel?.sequentialDetailChildrenList?.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                toast("获取章节信息失败")
                return@Observer
            }
            //不为空给activity传递地址
            parentModel.currentSequentialDetailChildrenList.postValue(it)
        })


        //activity主动调用视频播放结束，切换下一个列表
        parentModel.videoEndLiveData.observe(viewLifecycleOwner, Observer {
            //同步Adapter的显示
            (mAdapter as CourseChapterListAdapter).currentSelectPosition++
            mViewModel?.changeNextSequential()
        })

    }

    override fun initAdapter(): BaseQuickAdapter<SequentialBean, BaseViewHolder>? {
        mViewModel?.xtCourseId = xtCourseId
        return mViewModel?.getPageData()?.value?.let {
            val courseChapterListAdapter = CourseChapterListAdapter(it)
            //点击下载事件
            courseChapterListAdapter?.addChildClickViewIds(R.id.ivDownloadState)
            courseChapterListAdapter?.setOnItemChildClickListener { baseQuickAdapter, view, i ->
                if(view.id == R.id.ivDownloadState){
                    downlaodAudio(it[i],i)
                }
            }
            courseChapterListAdapter.setOnItemClickListener { adapter, view, position ->
                //点击切换播放的视频
                courseChapterListAdapter.currentSelectPosition = position
                mViewModel?.getSequentDetailChildList(position,it[position].id)
            }
            courseChapterListAdapter
        }
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return mViewModel?.getPageData()?.value?.let {
            val groupListener = GroupListener { position -> //获取分组名
                if (position in it.indices) {
                    val get = it.get(position)
                    get.parentDisplayName
                } else ""
            }
            val decoration = StickyDecoration.Builder
                    .init(groupListener)
                    .setGroupBackground(ContextCompat.getColor(requireContext(), R.color.white))
                    .setTextSideMargin(14.dp2px())
                    .setGroupTextColor(ContextCompat.getColor(requireContext(), R.color.color_4A4A4A))
                    .setOnClickListener { position, id ->

                    }//重置span（使用GridLayoutManager时必须调用）
                    //.resetSpan(mRecyclerView, (GridLayoutManager) manager)
                    .build()
            decoration
        }

    }


    private fun downlaodAudio(sequentialBean: SequentialBean, position: Int) {

    }

    override fun neadLoadMore(): Boolean {
        return false
    }
}