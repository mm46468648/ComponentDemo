package com.mooc.home.ui.discover.ebook

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.decoration.PaddingLeftRight15NoDividerDecoration
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.discover.view.DiscoverAcitivtyFloatView
import com.mooc.home.ui.discover.DiscoverChildListFragmentInterface
import com.mooc.resource.widget.Space120LoadMoreView
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2

/**
 * 发现页音频列表
 */
class EbookChildFragment : BaseListFragment<EBookBean, EbookChildViewModel>()
    , DiscoverChildListFragmentInterface,DiscoverAcitivtyFloatView.FloatViewVisibale {


    var ordering = "-sort_top" //排序默认综合  -view_count：最热      -publish_time ：最新    -sort_top：综合
    var discoverEbookChildViewModel : EbookChildViewModel? = null

    companion object {


        fun getInstance(bundle: Bundle? = null): EbookChildFragment {
            val fragment = EbookChildFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //根据父分类id，初始化Map参数
        val pId = arguments?.getString(DiscoverChildListFragmentInterface.PARAMS_PARENT_ID, "") ?: ""
        val sortId = arguments?.getString(DiscoverChildListFragmentInterface.PARAMS_SORT_ID, "") ?: ""
        val resourceType = arguments?.getString(DiscoverChildListFragmentInterface.PARAMS_RESOURCE_TYPE, "") ?: "-1"
        discoverEbookChildViewModel = mViewModel as EbookChildViewModel
        discoverEbookChildViewModel?.map = hashMapOf(
                "ordering" to ordering,
                "type" to resourceType,
                "sort_pid" to pId)

        if(sortId.isNotEmpty()){  //第三级分类id
            discoverEbookChildViewModel?.map?.put("sort_id", sortId)
        }
        //条数
        discoverEbookChildViewModel?.listCount?.observe(viewLifecycleOwner, Observer {
            //子fragment 跟父fragment通信
            if(parentFragment!=null && parentFragment is EbookFragment){
                (parentFragment as EbookFragment).setNumStr(it?:"0")
            }
//            LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_DISCOVER_CHILD_LIST_COUNT).postValue(it)
        })

        attachToScroll(recycler_view,requireActivity())
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initAdapter(): BaseQuickAdapter<EBookBean, BaseViewHolder>? {
        return (mViewModel as EbookChildViewModel).getPageData()?.value?.let {
            val discoverMoocAdapter = EbookAdapter(it)
//            val discoverMoocAdapter = CommonEBookAdapter(it)
            discoverMoocAdapter.setOnItemClickListener { _, _, position ->
                val eBookBean = it[position]
//                ARouter.getInstance().build(Paths.PAGE_EBOOK_DETAIL).withString(IntentParamsConstants.EBOOK_PARAMS_ID,eBookBean.id).navigation()

                LogUtil.addClickLogNew(
                    LogEventConstants2.P_DISCOVER,
                    eBookBean._resourceId,
                    "${eBookBean._resourceType}",
                    eBookBean.title,
                    "${LogEventConstants2.typeLogPointMap[eBookBean._resourceType]}#${eBookBean._resourceId}"
                )
                ResourceTurnManager.turnToResourcePage(eBookBean)
            }
            discoverMoocAdapter
        }
    }

    /**
     * tab点击切换列表数据
     * @param map 添加过来的参数
     * @param reset 是否重置，默认false   (原来设计的时候需要重置，查询条件，现在不需要了保持查询条件)
     */
    override fun changeListData(map:Map<String,String>,reset:Boolean){
//        if(reset){
//            discoverEbookChildViewModel?.map?.clear()
//
//            val pId = arguments?.getString(BaseInPARAMS_PARENT_ID, "") ?: ""
//            val resourceType = arguments?.getString(PARAMS_RESOURCE_TYPE, "") ?: "-1"
//
//            discoverEbookChildViewModel?.map = hashMapOf(
//                    "ordering" to ordering,
//                    "type" to resourceType,
//                    "sort_pid" to pId)
//
//        }

        //追加查询条件
        discoverEbookChildViewModel?.map?.putAll(map)
//        skeletonScreen?.show()
        loadDataWithRrefresh()
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration = PaddingLeftRight15NoDividerDecoration()

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }
}