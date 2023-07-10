package com.mooc.home.ui.discover.micrlprofession

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.adapter.CommonCourseAdapter
import com.mooc.commonbusiness.adapter.CommonMicroKnowAdapter
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.discover.view.DiscoverAcitivtyFloatView
import com.mooc.home.R
import com.mooc.home.ui.discover.DiscoverChildListFragmentInterface
import com.mooc.resource.widget.Space120LoadMoreView
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.model.MicroProfession
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.commonbusiness.model.studyproject.StudyProject
import com.mooc.discover.adapter.StudyProjectAdapter
import com.mooc.discover.viewmodel.StudyProjectViewModel
import com.mooc.home.ui.decoration.Padding15Divider1Decoration

/**
 * 发现页慕课列表
 */
class MicroProfessionFragment : BaseListFragment2<MicroProfession, MicroKnowProfessionViewModel>(),DiscoverAcitivtyFloatView.FloatViewVisibale {


    var ordering = "-sort_top" //排序默认综合  -view_count：最热      -publish_time ：最新    -sort_top：综合
    var discoverMoocChildViewModel : MicroKnowProfessionViewModel? = null

    companion object {

        const val PARAMS_PARENT_ID = "params_parent_id"
        const val PARAMS_RESOURCE_TYPE = "params_resource_type"
        fun getInstance(bundle: Bundle? = null): MicroProfessionFragment {
            val fragment = MicroProfessionFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        attachToScroll(recycler_view,requireActivity())
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initAdapter(): BaseQuickAdapter<MicroProfession, BaseViewHolder>? {
        return (mViewModel as MicroKnowProfessionViewModel).getPageData().value?.let {
            val adapter = MicroProfessionAdapter(it)

            adapter.setOnItemClickListener { adapter, _, position ->
                val bean = adapter.data.get(position) as MicroProfession

                LogUtil.addClickLogNew(
                    LogEventConstants2.P_DISCOVER,
                    bean._resourceId,
                    "${bean._resourceType}",
                    bean.title,
                    "${LogEventConstants2.typeLogPointMap[bean._resourceType]}#${bean._resourceId}"
                )
                ResourceTurnManager.turnToResourcePage(bean)
            }

            adapter

        }
    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }
}