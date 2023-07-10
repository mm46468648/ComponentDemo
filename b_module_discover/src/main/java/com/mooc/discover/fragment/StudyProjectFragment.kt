package com.mooc.discover.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.adapter.StudyProjectAdapter
import com.mooc.commonbusiness.model.studyproject.StudyProject
import com.mooc.discover.viewmodel.StudyProjectViewModel
import com.mooc.resource.widget.Space120LoadMoreView
//import com.mooc.b_module_discover.view.Padding15Divider1Decoration
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.discover.view.DiscoverAcitivtyFloatView
import com.mooc.home.ui.decoration.Padding15Divider1Decoration
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.manager.ResourceTurnManager

/**
 *
 * @ProjectName:发现页电子书Fragment
 * @Package:
 * @ClassName:
 * @Description:    java类作用描述
 * @Author:         xym
 * @CreateDate:     2020/8/11 5:06 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/11 5:06 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class StudyProjectFragment : BaseListFragment2<StudyProject, StudyProjectViewModel>(),
    DiscoverAcitivtyFloatView.FloatViewVisibale {

    companion object {
        fun getInstance(bundle: Bundle? = null): StudyProjectFragment {
            val fragment = StudyProjectFragment()
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

    override fun initAdapter(): BaseQuickAdapter<StudyProject, BaseViewHolder>? {
        return (mViewModel as StudyProjectViewModel).getPageData().value?.let {
            val adapter = StudyProjectAdapter(it)

            adapter.setOnItemClickListener { adapter, _, position ->
                val bean = adapter.data.get(position) as StudyProject

                LogUtil.addClickLogNew(
                    LogEventConstants2.P_DISCOVER,
                    bean._resourceId,
                    "${bean._resourceType}",
                    bean.plan_name,
                    "${LogEventConstants2.typeLogPointMap[bean._resourceType]}#${bean._resourceId}"
                )
            ResourceTurnManager.turnToResourcePage(bean)
//                ARouter.getInstance().build(Paths.PAGE_STUDYPROJECT)
//                    .withString(IntentParamsConstants.STUDYPROJECT_PARAMS_ID, bean.id.toString())
//                    .navigation()
            }

            adapter

        }
    }


    override fun getItemDecoration(): RecyclerView.ItemDecoration =
        Padding15Divider1Decoration()


    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }
}