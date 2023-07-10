package com.mooc.commonbusiness.module.studyroom.collect

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.repackaged.com.google.common.collect.Iterators
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.adapter.HorizontalTabAdapter
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.eventbus.ArticleReadFinishEvent
import com.mooc.commonbusiness.model.folder.FolderTab
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.commonbusiness.model.studyroom.DiscoverTab
import com.mooc.commonbusiness.module.studyroom.StudyListDetailViewModel
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.StatisticsService
import com.mooc.resource.widget.NoIntercepteRecyclerView
import com.mooc.resource.widget.Space120LoadMoreView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


/**
 * 学习室课程
 *
 */
class StudyRoomCollectFragment : BaseUserLogListenFragment<Any, CollectViewModel>() {
    val logService by lazy {
        ARouter.getInstance().navigation(StatisticsService::class.java)
    }

    companion object {
        const val PARAMS_TYPES_ARRAY = "params_types_array" //资源类型数组
        fun getInstance(bundle: Bundle? = null): StudyRoomCollectFragment {
            val fragment = StudyRoomCollectFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

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

    var folderId: String = ""
    var parentViewModel: StudyListDetailViewModel? = null

    override fun genericViewModel() {
        folderId = arguments?.getString(IntentParamsConstants.STUDYROOM_FOLDER_ID) ?: ""
        resourceIds = arguments?.getIntegerArrayList(PARAMS_TYPES_ARRAY)

        if (folderId.isNotEmpty()) {
            parentViewModel =
                ViewModelProviders.of(requireActivity())[StudyListDetailViewModel::class.java]
        }
        mViewModel = ViewModelProviders.of(this, BaseViewModelFactory(folderId))
            .get(CollectViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        view.findViewById<FrameLayout>(R.id.flRoot).addView(initRvTabView())
        (refresh_layout?.layoutParams as MarginLayoutParams).setMargins(0, 48.dp2px(), 0, 0)

        super.onViewCreated(view, savedInstanceState)


    }

    var horizontalTabAdapter : HorizontalTabAdapter? = null

    /**
     * 初始化横向滚动头部
     */
    private fun initRvTabView(): RecyclerView {
        val rvHorziontalTab = NoIntercepteRecyclerView(requireContext())
        rvHorziontalTab.id = R.id.rv_studyroom_collect_tab
        val layoutParams =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 48.dp2px())
//        layoutParams.topMargin = 10.dp2px()
        rvHorziontalTab.layoutParams = layoutParams
        rvHorziontalTab.setPadding(15, 10.dp2px(), 10.dp2px(), 0)

        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvHorziontalTab.layoutManager = linearLayoutManager
        horizontalTabAdapter = HorizontalTabAdapter(tabList)
        horizontalTabAdapter?.apply {
            this.setOnItemClickListener { adapter, view, position ->
                this.selectPosition = position
                selectTab(tabList[position])
            }
            rvHorziontalTab.adapter = horizontalTabAdapter
            this.selectPosition = 0
        }
        if(tabList.isNotEmpty()){
            selectTab(tabList[0])
        }
        rvHorziontalTab.setBackgroundColor(Color.WHITE)
        return rvHorziontalTab
    }

    /**
     * 点击切换Tab
     */
    private fun selectTab(discoverTab: DiscoverTab) {
        (mViewModel as CollectViewModel).type = discoverTab.id
        (mViewModel as CollectViewModel).initData()
    }

    //当前列表可编辑状态
    var currentEditableStatus = true

    override fun initAdapter(): BaseQuickAdapter<Any, BaseViewHolder>? {
        val let = (mViewModel as CollectViewModel).getPageData().value?.let {
            val discoverMoocAdapter = CollectAdapter(it)

            discoverMoocAdapter.setOnItemClickListener { adapter, view, position ->
                val any = it[position] as BaseResourceInterface

                logService.addClickLog(
                    LogEventConstants2.P_ROOM,
                    any._resourceId,
                    "${any._resourceType}",
                    "",
                    "${LogEventConstants2.typeLogPointMap[any._resourceType]}#${any._resourceId}"
                )

                if (any is ArticleBean) {
                    val articleBean = any as ArticleBean
                    articleBean.folder_id =
                        arguments?.getString(IntentParamsConstants.STUDYROOM_FOLDER_ID);
                }

                ResourceTurnManager.turnToResourcePage(any)
            }

            discoverMoocAdapter.setOnItemLongClickListener { adapter, view, position ->

                if (currentEditableStatus) {
                    val any = adapter.data[position] as StudyResourceEditable
                    discoverMoocAdapter.showEditPop(view, any)
                } else {
                    toast(resources.getString(R.string.text_no_publicate_tip))
                }



                return@setOnItemLongClickListener true
            }

            discoverMoocAdapter
        }

//        //获取公开状态，确定是否可以编辑
        parentViewModel?.publicStatus?.observe(this,
            Observer<Boolean> {
                currentEditableStatus = !it
            })
        return let
    }

    override fun neadLoadMore(): Boolean {
        //学习清单详情中需要
        return true
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
        emptyView.setButton("+添加学习资源", View.OnClickListener {
            //跳转专题页面可以通过拦截器判断登录状态
            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                return@OnClickListener
            }
            //已登录，跳转发现页
            ARouter.getInstance().build(Paths.PAGE_HOME)
                .withInt(IntentParamsConstants.HOME_SELECT_POSITION, 0)
                .withInt(IntentParamsConstants.HOME_SELECT_CHILD_POSITION, 0)
                .navigation()
        })
//        }
    }


    /**
     * 更新tab的分类数据
     */
    fun updateTabResource(newResourceIds: ArrayList<Int>?) {
        //如果是空的，直接显示空布局
        if(newResourceIds == null) return
        if (!compairTwoList(resourceIds as ArrayList<Int>, newResourceIds)) {
            //重新组装数据，并更新
            resourceIds = newResourceIds
            tabList.clear()
            resourceIds?.forEach {
                val tab = DiscoverTab()
                tab.id = it
                tab.title = ResourceTypeConstans.typeStringMap[it] ?: ""
                tabList.add(tab)
            }
            horizontalTabAdapter?.selectPosition = 0
            if(tabList.isNotEmpty()){
                selectTab(tabList[0])
            }
        }
    }

    /**
     * 比较两个数组是否相同
     */
    fun compairTwoList(old: ArrayList<Int>, new: ArrayList<Int>): Boolean {
        if (old.size != new.size) return false
        return Iterators.elementsEqual(old.iterator(), new.iterator())

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onArticleReadEvent(event: ArticleReadFinishEvent) {
        updateArticleReadFinish(event.id)
    }
}