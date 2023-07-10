package com.mooc.commonbusiness.module.studyroom.collect

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.adapter.HorizontalTabAdapter
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.resource.widget.NoIntercepteRecyclerView
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.eventbus.ArticleReadFinishEvent
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.commonbusiness.model.studyroom.DiscoverTab
import com.mooc.resource.widget.Space120LoadMoreView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList


/**
 * 学习室课程
 *
 */
class PublicListCollectFragment : BaseUserLogListenFragment<Any, PublicListCollectViewModel>() {


    companion object {
        fun getInstance(bundle: Bundle? = null): PublicListCollectFragment {
            val fragment = PublicListCollectFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

//    fun setResTypes(types: MutableList<Int>): PublicListCollectFragment {
//        resourceIds = types;
//        return this;
//    }

    //收藏资源 id 数组
//    var resourceIds = mutableListOf<Int>()
    var resourceIds: java.util.ArrayList<Int>? = null

    //构建收藏资源 数据集合
    val tabList: ArrayList<DiscoverTab> by lazy {
        ArrayList<DiscoverTab>().apply {
            resourceIds?.forEach {
                val tab = DiscoverTab()
                tab.id = it
                tab.title = ResourceTypeConstans.typeStringMap[it] ?: ""
                this.add(tab)
            }
        }
    }


    override fun genericViewModel() {
        val folderId = arguments?.getString(IntentParamsConstants.STUDYROOM_FOLDER_ID) ?: ""
        resourceIds = arguments?.getIntegerArrayList(IntentParamsConstants.STUDYROOM_COLLECT_TABLIST)
        mViewModel = ViewModelProviders.of(this, BaseViewModelFactory(folderId))
            .get(PublicListCollectViewModel::class.java)
        mViewModel?.fromRecommend =
            arguments?.getBoolean(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND) ?: false
        mViewModel?.fromTask =
            arguments?.getBoolean(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK) ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<FrameLayout>(R.id.flRoot).addView(initRvTabView())
        (refresh_layout?.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
            0,
            48.dp2px(),
            0,
            0
        )
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onResume() {
        super.onResume()

    }

    /**
     * 初始化横向滚动头部
     */
    private fun initRvTabView(): RecyclerView {
        val rvHorziontalTab = NoIntercepteRecyclerView(requireContext())
        val layoutParams =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 48.dp2px())
//        layoutParams.topMargin = 10.dp2px()
        rvHorziontalTab.layoutParams = layoutParams
        rvHorziontalTab.setPadding(0, 10.dp2px(), 10.dp2px(), 0)

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvHorziontalTab.layoutManager = linearLayoutManager
        val horizontalTabAdapter = HorizontalTabAdapter(tabList)
        horizontalTabAdapter.setOnItemClickListener { adapter, view, position ->
            horizontalTabAdapter.selectPosition = position
            selectTab(tabList[position])
        }
        rvHorziontalTab.adapter = horizontalTabAdapter
        if(tabList.isNotEmpty()){
            horizontalTabAdapter.selectPosition = 0
            selectTab(tabList[0])
        }
        rvHorziontalTab.setBackgroundColor(Color.WHITE)
        return rvHorziontalTab
    }

    /**
     * 点击切换Tab
     */
    private fun selectTab(discoverTab: DiscoverTab) {
        mViewModel?.type = discoverTab.id
        mViewModel?.initData()
    }


    override fun initAdapter(): BaseQuickAdapter<Any, BaseViewHolder>? {
        val userId = arguments?.getString(IntentParamsConstants.MY_USER_ID) ?: ""
        mViewModel?.userId = userId
        return mViewModel?.getPageData()?.value?.let {
            val discoverMoocAdapter = CollectAdapter(it)

            discoverMoocAdapter.setOnItemClickListener { adapter, view, position ->
                val any = it[position] as BaseResourceInterface
//                toast(any.toString())
                if (any is ArticleBean) {
                    val articleBean = any as ArticleBean
                    articleBean.folder_id =
                        arguments?.getString(IntentParamsConstants.STUDYROOM_FOLDER_ID);
                }
                ResourceTurnManager.turnToResourcePage(any)
            }
            discoverMoocAdapter
        }
    }

    /**
     * 更新文章阅读完成
     */
    fun updateArticleReadFinish(id: String) {
        //获取文章所在的位置
        (mAdapter as CollectAdapter).data.forEachIndexed { index, any ->
            if (any is ArticleBean &&  any.id == id) {
                any.task_finished = true
                mAdapter?.notifyItemChanged(index)
            }
        }
    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }

    override fun initEmptyView() {
        val tipStr = if (mViewModel?.folderId?.isEmpty() == true) "你还没有添加资源"
        else "抱歉，该学习清单里没有任何资源文件"
        //在学习室底部
        emptyView.setTitle(tipStr)
        emptyView.setEmptyIcon(R.drawable.common_gif_folder_empty)
        emptyView.setGravityTop(20.dp2px())
        emptyView.setIconOverride(150.dp2px(), 150.dp2px())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onArticleReadEvent(event: ArticleReadFinishEvent) {
        updateArticleReadFinish(event.id)
    }

}