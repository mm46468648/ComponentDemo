package com.mooc.home.ui.discover.mooc

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
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.decoration.PaddingLeftRight15NoDividerDecoration
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.discover.view.DiscoverAcitivtyFloatView
import com.mooc.home.R
import com.mooc.home.ui.discover.DiscoverChildListFragmentInterface
import com.mooc.resource.widget.Space120LoadMoreView
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2

/**
 * 发现页慕课列表
 */
class DiscoverMoocChildFragment : BaseListFragment<CourseBean, DiscoverMoocChildViewModel>(), DiscoverChildListFragmentInterface,DiscoverAcitivtyFloatView.FloatViewVisibale {


    var ordering = "-sort_top" //排序默认综合  -view_count：最热      -publish_time ：最新    -sort_top：综合
    var discoverMoocChildViewModel : DiscoverMoocChildViewModel? = null

    companion object {

        const val PARAMS_PARENT_ID = "params_parent_id"
        const val PARAMS_RESOURCE_TYPE = "params_resource_type"
        fun getInstance(bundle: Bundle? = null): DiscoverMoocChildFragment {
            val fragment = DiscoverMoocChildFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //根据父分类id，初始化Map参数
        val pId = arguments?.getString(PARAMS_PARENT_ID, "") ?: ""
        val sortId = arguments?.getString(DiscoverChildListFragmentInterface.PARAMS_SORT_ID, "") ?: ""
        val resourceType = arguments?.getString(PARAMS_RESOURCE_TYPE, "") ?: "-1"
        discoverMoocChildViewModel = mViewModel as DiscoverMoocChildViewModel
        discoverMoocChildViewModel?.map = hashMapOf(
                "ordering" to ordering,
                "type" to resourceType,
                "sort_pid" to pId)

        if(sortId.isNotEmpty()){  //第三级分类id
            discoverMoocChildViewModel?.map?.put("sort_id", sortId)
        }
        //条数
        discoverMoocChildViewModel?.listCount?.observe(viewLifecycleOwner, Observer {
            //子fragment 跟父fragment通信
            if(parentFragment!=null && parentFragment is DiscoverMoocFragment){
                (parentFragment as DiscoverMoocFragment).setNumStr(it?:"0")
            }
//            LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_DISCOVER_CHILD_LIST_COUNT).postValue(it)
        })

        attachToScroll(recycler_view,requireActivity())
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initAdapter(): BaseQuickAdapter<CourseBean, BaseViewHolder>? {
        return (mViewModel as DiscoverMoocChildViewModel).getPageData().value?.let {
//            val discoverMoocAdapter = DiscoverMoocAdapter(it)
            val discoverMoocAdapter = CommonCourseAdapter(it)
            discoverMoocAdapter.setOnItemClickListener { _, _, position ->
                if(position >= it.size) return@setOnItemClickListener
                val courseBean = it[position]

                val logId = if(courseBean.classroom_id.isNotEmpty()) "${courseBean.id}#${courseBean.classroom_id}" else courseBean.id
                LogUtil.addClickLogNew(
                    LogEventConstants2.P_DISCOVER,
                    logId,
                    "${courseBean._resourceType}",
                    courseBean.title,
                    "${LogEventConstants2.typeLogPointMap[courseBean._resourceType]}#${courseBean._resourceId}"
                )
                ResourceTurnManager.turnToResourcePage(courseBean)
            }
            discoverMoocAdapter
        }
    }

    /**
     * 三级分类点击切换列表数据
     * 需要重置所有的筛选状态
     * @param map 添加过来的参数
     * @param reset 是否重置，默认false
     */
    override fun changeListData(map:Map<String,String>,reset:Boolean){
        if(reset){    //当点击三级列表的时候需要重置，查询条件
            discoverMoocChildViewModel?.map?.clear()

            val pId = arguments?.getString(PARAMS_PARENT_ID, "") ?: ""
            val resourceType = arguments?.getString(PARAMS_RESOURCE_TYPE, "") ?: "-1"
            val discoverMoocChildViewModel = mViewModel as DiscoverMoocChildViewModel
            discoverMoocChildViewModel.map = hashMapOf(
                    "ordering" to ordering,
                    "type" to resourceType,
                    "sort_pid" to pId)

        }

        //追加查询条件
        discoverMoocChildViewModel?.map?.putAll(map)

        //将adapter置空后重新查询
        mAdapter?.data?.clear()
        mAdapter?.notifyDataSetChanged()
//        skeletonScreen?.show()
        loadDataWithRrefresh()

    }

    /**
     * 设置空布局
     */
    override fun initEmptyView() {
        emptyView.setTitle("抱歉,该分类内容为空,换个试试吧")
        emptyView.setEmptyIcon(R.mipmap.common_ic_empty)
        emptyView.setGravityTop(20.dp2px())
    }

//    override fun getItemDecoration(): RecyclerView.ItemDecoration = PaddingLeftRight15NoDividerDecoration()

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }
}