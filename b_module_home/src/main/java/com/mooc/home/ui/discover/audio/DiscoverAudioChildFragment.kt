package com.mooc.home.ui.discover.audio

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.adapter.CommonAlbumAdapter
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.decoration.PaddingLeftRight15NoDividerDecoration
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.discover.view.DiscoverAcitivtyFloatView
import com.mooc.home.R
import com.mooc.home.ui.discover.DiscoverChildListFragmentInterface
import com.mooc.resource.widget.Space120LoadMoreView
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2

/**
 * 发现页音频列表
 */
class DiscoverAudioChildFragment : BaseListFragment2<AlbumBean, DiscoverAudoChildViewModel>()
    , DiscoverChildListFragmentInterface, DiscoverAcitivtyFloatView.FloatViewVisibale{


    var ordering = "-sort_top" //排序默认综合  -view_count：最热      -publish_time ：最新    -sort_top：综合
    var discoverAudioChildViewModel : DiscoverAudoChildViewModel? = null

    companion object {

        const val PARAMS_PARENT_ID = "params_parent_id"
        const val PARAMS_RESOURCE_TYPE = "params_resource_type"
        fun getInstance(bundle: Bundle? = null): DiscoverAudioChildFragment {
            val fragment = DiscoverAudioChildFragment()
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
        discoverAudioChildViewModel = mViewModel as DiscoverAudoChildViewModel
        discoverAudioChildViewModel?.map = hashMapOf(
                "ordering" to ordering,
                "type" to resourceType,
                "sort_pid" to pId)

        if(sortId.isNotEmpty()){  //第三级分类id
            discoverAudioChildViewModel?.map?.put("sort_id", sortId)
        }

        discoverAudioChildViewModel?.listCount?.observe(viewLifecycleOwner, Observer {
            //子fragment 跟父fragment通信
            if(parentFragment!=null && parentFragment is DiscoverAudioFragment){
                (parentFragment as DiscoverAudioFragment).setNumStr(it?:"0")
            }
//            LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_DISCOVER_CHILD_LIST_COUNT).postValue(it)
        })

        attachToScroll(recycler_view,requireActivity())
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initAdapter(): BaseQuickAdapter<AlbumBean, BaseViewHolder>? {
        return (mViewModel as DiscoverAudoChildViewModel).getPageData()?.value?.let {
//            val discoverMoocAdapter = DiscoverAudioAdapter(it)
            val discoverMoocAdapter = CommonAlbumAdapter(it)
            discoverMoocAdapter.setOnItemClickListener { _, _, position ->
                val albumBean = it[position]
//                toast(albumBean.album_title ?: "")
//                ARouter.getInstance().build(Paths.PAGE_ALBUM).withString(IntentParamsConstants.ALBUM_PARAMS_ID,albumBean.id).navigation()
                LogUtil.addClickLogNew(
                    LogEventConstants2.P_DISCOVER,
                    albumBean._resourceId,
                    "${albumBean._resourceType}",
                    albumBean.album_title,
                    "${LogEventConstants2.typeLogPointMap[albumBean._resourceType]}#${albumBean._resourceId}"
                )
                ResourceTurnManager.turnToResourcePage(albumBean)
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
            discoverAudioChildViewModel?.map?.clear()

            val pId = arguments?.getString(PARAMS_PARENT_ID, "") ?: ""
            val resourceType = arguments?.getString(PARAMS_RESOURCE_TYPE, "") ?: "-1"
            val discoverMoocChildViewModel = mViewModel as DiscoverAudoChildViewModel
            discoverMoocChildViewModel.map = hashMapOf(
                    "ordering" to ordering,
                    "type" to resourceType,
                    "sort_pid" to pId)

        }

        //追加查询条件
        discoverAudioChildViewModel?.map?.putAll(map)
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