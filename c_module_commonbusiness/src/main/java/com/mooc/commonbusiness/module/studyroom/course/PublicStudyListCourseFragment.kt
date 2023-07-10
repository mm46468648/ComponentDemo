package com.mooc.commonbusiness.module.studyroom.course

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.adapter.CommonCourseAdapter
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.utils.ShowDialogUtils
import com.mooc.resource.widget.Space120LoadMoreView


/**
 * 公开的学习清单课程fragment
 * 因为无法查看他人的课程进度等相关数据，展示样式同搜索
 */
class PublicStudyListCourseFragment :
    BaseUserLogListenFragment<CourseBean, PublicListCourseViewModel>() {


    companion object {
        fun getInstance(bundle: Bundle? = null): PublicStudyListCourseFragment {
            val fragment = PublicStudyListCourseFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun genericViewModel() {
        //文件夹id，如果不传代表是根目录，""
        val folderId = arguments?.getString(IntentParamsConstants.STUDYROOM_FOLDER_ID) ?: ""
        mViewModel = ViewModelProviders.of(this, BaseViewModelFactory(folderId))
            .get(PublicListCourseViewModel::class.java)
        mViewModel?.fromRecommend =
            arguments?.getBoolean(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND) ?: false
        mViewModel?.fromTask =
            arguments?.getBoolean(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK) ?: false
        mViewModel?.fromTaskId =
            arguments?.getString(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK_ID) ?: ""

    }

    override fun initAdapter(): BaseQuickAdapter<CourseBean, BaseViewHolder>? {
        val userId = arguments?.getString(IntentParamsConstants.MY_USER_ID) ?: ""
        mViewModel?.userId = userId
        return mViewModel?.getPageData()?.value?.let {
            val discoverMoocAdapter = CommonCourseAdapter(it)
            discoverMoocAdapter.setOnItemClickListener { adapter, view, position ->
                val courseBean = it[position]
                if (ResourceTypeConstans.allCourseDialogId.contains(courseBean.id)) {//工信部点击不进入课程详情页面，需要弹框的课程id
                    activity?.let { it1 -> ShowDialogUtils.showDialog(it1) }
                } else {
                    ResourceTurnManager.turnToResourcePage(courseBean)
                }
            }

//            discoverMoocAdapter.setFooterView(Space120FootView(requireContext()))
            discoverMoocAdapter
        }
    }

    override fun initEmptyView() {
        //title文案，学习清单详情，和学习室底部不同
        val tipStr = "该学习清单里没有任何资源文件"
        //在学习室底部
        emptyView.setTitle(tipStr)
        emptyView.setEmptyIcon(R.drawable.common_gif_folder_empty)
        emptyView.setGravityTop(20.dp2px())
        emptyView.setIconOverride(150.dp2px(), 150.dp2px())
    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }

//    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
//        return PaddingLeftRight15NoDividerDecoration()
//    }

}