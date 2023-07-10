package com.mooc.discover.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseFragment
import com.mooc.commonbusiness.constants.LoadStateConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.manager.ResourceUtil
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.model.eventbus.UserSettingChangeEvent
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.adapter.RecommendGuessAdapter
import com.mooc.discover.databinding.FragmentRecommendBinding
import com.mooc.discover.model.ResultBean
import com.mooc.discover.view.*
import com.mooc.discover.viewmodel.RecommendViewModel
import com.mooc.resource.widget.Space120LoadMoreView
import com.mooc.statistics.LogUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 推荐全部fragment
 */
class RecommendAllFragment : BaseFragment(), DiscoverAcitivtyFloatView.FloatViewVisibale {


    val mViewModel: RecommendViewModel by viewModels()


    //bannerView
    private val bannerView by lazy {
        HomeDiscoverBannerView(requireContext())
    }

    //公告
    private val noticeView by lazy {
        HomeDiscoverNoticeView(requireContext())
    }


    //快捷入口
    private val quickEntryView by lazy {
        HomeDiscoverQuickEntryView(requireContext())
    }

    //任务入口
    val taskView by lazy {
        HomeDiscoverTaskView(requireContext())
    }

    //专栏
    private val columnView by lazy {
        HomeDiscoverColumnView(requireContext())
    }

    //猜你喜欢
    private val footerView by lazy {
        getFootView()
    }


    /**
     * 获取脚布局
     * (查看全部栏目)
     */
    @SuppressLint("InflateParams")
    private fun getFootView(): View {
        val view = LayoutInflater.from(activity).inflate(R.layout.column_footer_view, null, false);

        view.findViewById<View>(R.id.show_all_column).setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_COLUMN_ALL).navigation()
        }

        SkinManager.getInstance().injectSkin(view)
        return view;
    }


    //适配器
    private val guessAdapter: RecommendGuessAdapter by lazy {
        val recommendGuessAdapter = RecommendGuessAdapter(mViewModel.guessList.value)
        //添加头布局
        recommendGuessAdapter.addHeaderView(bannerView)
        recommendGuessAdapter.addHeaderView(noticeView)
        recommendGuessAdapter.addHeaderView(quickEntryView)
        recommendGuessAdapter.addHeaderView(taskView)
        recommendGuessAdapter.addHeaderView(columnView)
        recommendGuessAdapter.addHeaderView(footerView)
        recommendGuessAdapter
    }

//    var skeletonScreen : ViewSkeletonScreen? = null

    private var _binding : FragmentRecommendBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecommendBinding.inflate(layoutInflater,container,false)
        SkinManager.getInstance().injectSkin(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        loadData()
        mViewModel.refreshData()

    }

    /**
     * 初始化控件
     */
    private fun initView() {
//        skeletonScreen = Skeleton.bind(refreshLayout)
//            .load(R.layout.discover_layout_recommend_skeleton)
//            .show()

        //配置猜你喜欢adapter
        guessAdapter.loadMoreModule.setOnLoadMoreListener {
            mViewModel.getGuessLike()
        }
        guessAdapter.setOnItemClickListener { adapter, view, position ->
            val resultBean = adapter.data[position] as ResultBean
            LogUtil.addClickLogNew(
                    "${LogEventConstants2.P_DISCOVER}#REC", "${resultBean._resourceId}",
                    resultBean._resourceType.toString(), resultBean.title, "${LogEventConstants2.typeLogPointMap[resultBean._resourceType]}#${resultBean._resourceId}")

            ResourceTurnManager.turnToResourcePage(resultBean)

            ResourceUtil.updateResourceRead(resultBean.id)
        }
        guessAdapter.loadMoreModule.loadMoreView = Space120LoadMoreView()
        binding.rlvRecommend.layoutManager = LinearLayoutManager(activity)
        binding.rlvRecommend.adapter = guessAdapter


        binding.refreshLayout.setOnRefreshListener {
            mViewModel.refreshData()
            binding.refreshLayout.finishRefresh(2000)
        }
        attachToScroll(binding.rlvRecommend, requireActivity())
    }


    /**
     * 观察数据改变
     */
    fun loadData() {

        mViewModel.guessLoadMoreStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoadStateConstants.STATE_REFRESH_COMPLETE -> {
//                    view_refresh.isRefreshing = false
                    binding.refreshLayout.finishRefresh()
                }
                LoadStateConstants.STATE_CURRENT_COMPLETE -> {
                    guessAdapter.loadMoreModule.loadMoreComplete();
                }
                LoadStateConstants.STATE_ALL_COMPLETE -> {
                    guessAdapter.loadMoreModule.loadMoreEnd()
                }
                LoadStateConstants.STATE_ERROR -> {
                    guessAdapter.loadMoreModule.loadMoreFail()
//                    view_refresh.isRefreshing = false
                    binding.refreshLayout.finishRefresh()
                }
                LoadStateConstants.STATE_DATA_EMPTY -> {
                }
            }
        })

        mViewModel.quickEntry.observe(viewLifecycleOwner, {
//            skeletonScreen?.hide()
            quickEntryView.setRotationBean(it)
        })

        mViewModel.columnData.observe(viewLifecycleOwner, {
            columnView.setColumnData(it)
        })

        mViewModel.bannerData.observe(viewLifecycleOwner, {
//            skeletonScreen?.hide()
            bannerView.setBannerBean(it)
        })

        mViewModel.noticeData.observe(viewLifecycleOwner, {
            noticeView.setNoticeList(it)
        })

        mViewModel.guessList.observe(viewLifecycleOwner, {
            guessAdapter.notifyDataSetChanged()
        })
        //是否设置兴趣内容推荐
        mViewModel.likeRecommend.observe(viewLifecycleOwner, {

            val clGuessLike = footerView.findViewById<View>(R.id.clGuessLike)
            if (it) {
                clGuessLike.visibility = View.VISIBLE
            } else {
                clGuessLike.visibility = View.INVISIBLE
            }
        })
        mViewModel.taskData.observe(viewLifecycleOwner, {
            taskView.setImg(it)
        })

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(userInfo: UserLoginStateEvent) {
        loge("${this::class.java.simpleName}收到了登录事件")
        if (userInfo.userInfo != null && mViewModel.guessOffset == 0) { //只有登录状态，并且没数据的时候再刷新
            mViewModel.getGuessLike()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPrivacyEvent(userSetting: UserSettingChangeEvent) {
        if (userSetting.userSettingBean != null) { //设置更新后刷新接口
            when (userSetting.flag) {
                1 -> {//修改了允许个性化推荐
                    mViewModel.guessOffset = 0
                    mViewModel.getGuessLike()
                    mViewModel.getRecommendDiscoverColumn()
                }
                2 -> {//修改了发现页感兴趣内容推荐
                    mViewModel.guessOffset = 0
                    mViewModel.getGuessLike()
                }
                3 -> {//修改了首页栏目推荐
                    mViewModel.getRecommendDiscoverColumn()
                }
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)

    }
}