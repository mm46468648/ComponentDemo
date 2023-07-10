package com.mooc.commonbusiness.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.LoadStateConstants
import com.mooc.resource.widget.EmptyView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.lang.reflect.ParameterizedType


/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    通用列表类型Fragment，支持下拉刷新和上拉加载，以及错误处理
 * @Author:         xym
 * @CreateDate:     2020/8/19 10:07 AM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/19 10:07 AM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 * @param T 数据模型
 * @param M ViewModle类型
 */
abstract class BaseListFragment2<T, M : BaseListViewModel2<T>> : BaseFragment() ,LifecycleObserver {

    protected var mViewModel: M? = null
    var mAdapter: BaseQuickAdapter<T, BaseViewHolder>? = null
    val emptyView by lazy {
        com.mooc.resource.widget.EmptyView(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.common_fragment_layout_recyclerlist, null)

        genericViewModel()

        obseverDataChange()

        observerStateChange()

        mAdapter = initAdapter()

        initView(inflate)

        lifecycle.addObserver(this)

        return inflate
    }

    /**
     * 监听网络状态改变
     */
    private fun observerStateChange() {
        //监听数据请求状态，以决定交互显示
        mViewModel?.getPageState()?.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoadStateConstants.STATE_REFRESH_COMPLETE -> {
//                    refresh_layout?.isRefreshing = false
                    refresh_layout?.finishRefresh()
                    skeletonScreen?.hide()
                }
                LoadStateConstants.STATE_CURRENT_COMPLETE -> {
                    if (neadLoadMore()) {
                        mAdapter?.loadMoreModule?.loadMoreComplete()
                    }
                }
                LoadStateConstants.STATE_ALL_COMPLETE -> {
                    if (neadLoadMore()) {
                        mAdapter?.loadMoreModule?.loadMoreEnd()
                    }
                }
                LoadStateConstants.STATE_ERROR -> {
                    if (neadLoadMore()) {
                        mAdapter?.loadMoreModule?.loadMoreFail()
                    }
//                    refresh_layout?.isRefreshing = false
                    refresh_layout?.finishRefresh()
                }
                LoadStateConstants.STATE_DATA_EMPTY -> {
                    //设置空
                    mAdapter?.setEmptyView(emptyView)
                    initEmptyView()
                    mAdapter?.notifyDataSetChanged()
//                    empty_view.visibility = View.VISIBLE
//                    recycler_view.visibility = View.GONE
                }
            }
        })
    }

    /**
     * 监听数据请求成功改变
     */
    private fun obseverDataChange() {
        //触发页面初始化数据加载的逻辑
        mViewModel?.getPageData()?.observe(viewLifecycleOwner, {
            if (it.isNotEmpty())
                mAdapter?.notifyDataSetChanged()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadDataWithRrefresh()
    }

//    var refresh_layout: SwipeRefreshLayout? = null
    var refresh_layout: SmartRefreshLayout? = null
    lateinit var recycler_view: RecyclerView
    lateinit var flRoot: FrameLayout
    var skeletonScreen : RecyclerViewSkeletonScreen? = null
    private fun initView(inflate: View) {
        recycler_view = inflate.findViewById<RecyclerView>(R.id.recycler_view)
        flRoot = inflate.findViewById<FrameLayout>(R.id.flRoot)
        refresh_layout = inflate.findViewById<SmartRefreshLayout>(R.id.refreshLayout)
//        refresh_layout = inflate.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
//        empty_view = inflate.findViewById<EmptyView>(R.id.empty_view)
        recycler_view.setLayoutManager(getLayoutManager())
        recycler_view.setItemAnimator(null)

        //装饰器
        getItemDecoration()?.let {
            recycler_view.addItemDecoration(it)
        }

        if (emptyView.parent != null) {
            (emptyView.parent as ViewGroup).removeView(emptyView)
        }
//        //设置空
//        mAdapter?.setEmptyView(emptyView)
        //下拉刷新
        refresh_layout?.setEnableRefresh(needPullToRefresh())
        refresh_layout?.setOnRefreshListener {
            mViewModel?.initData()
        }

        //loadMore配置,如果adapter，实现了loadmodule接口，就代表需要分页加载
        if (neadLoadMore()) {
            mAdapter?.loadMoreModule?.isAutoLoadMore = getAutoLoadMore()

            mAdapter?.loadMoreModule?.loadMoreView = getLoadMoreView()

            mAdapter?.loadMoreModule?.setOnLoadMoreListener {
                mViewModel?.loadData()
            }
        }


//        recycler_view.adapter = mAdapter

        val skeletonLayout = if(getLayoutManager() is GridLayoutManager) R.layout.common_item_skeleton_gride else R.layout.common_item_skeleton_list
        skeletonScreen = Skeleton.bind(recycler_view)
            .adapter(mAdapter)
            .load(skeletonLayout)
            .show()

    }

    /**
     * 加载数据并加载刷新动画
     */
    open fun loadDataWithRrefresh() {
//        refresh_layout?.isRefreshing = true
        refresh_layout?.autoRefreshAnimationOnly()
        mViewModel?.initData()
    }

    /**
     * 设置空布局样式
     */
    open fun initEmptyView() {}

    /**
     * 获取布局管理器
     */
    open fun getLayoutManager(): RecyclerView.LayoutManager =
        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    /**
     * 获取装饰器
     */
    open fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    /**
     * 是否启用下拉刷新
     * 默认启用
     */
    open fun needPullToRefresh() = true

    /**
     * 是否需要加载更多
     */
    open fun neadLoadMore() = mAdapter is LoadMoreModule

    /**
     * 是否自动加载
     */
    open fun getAutoLoadMore() = true

//    /**
//     * 是否需要登录才能显示数据的页面
//     */
//    open fun needLogin() = false

    /**
     * 获取子类适配器
     */
    abstract fun initAdapter(): BaseQuickAdapter<T, BaseViewHolder>?

    /**
     * 加载更多跟布局
     */
    open fun getLoadMoreView(): BaseLoadMoreView {
        return SimpleLoadMoreView()
    }

    /**
     * 初始化子类的viewModel
     */
    open fun genericViewModel() {
        //利用 子类传递的 泛型参数实例化出absViewModel 对象。
        val type = javaClass.genericSuperclass as ParameterizedType?
        val arguments = type!!.actualTypeArguments
        if (arguments.size > 1) {
            val argument = arguments[1]
            val modelClaz = (argument as Class<M>).asSubclass(BaseListViewModel2::class.java)
            mViewModel = ViewModelProviders.of(this)[modelClaz] as M


        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifePause(){
//        refresh_layout?.isRefreshing = false
        refresh_layout?.finishRefresh()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onLifeResume(){
    }
}