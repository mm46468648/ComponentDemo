package com.mooc.discover.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.discover.R
import com.mooc.discover.model.TabSortBean
import com.mooc.discover.viewmodel.DiscoverMiddleViewModel
import com.mooc.commonbusiness.base.BaseFragment
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.adapter.DiscoverRecoomendAdapter
import com.mooc.discover.adapter.DiscoverRecoomendTabAdapter
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.discover.databinding.LayoutDiscoverMiddleBinding
//import kotlinx.android.synthetic.main.layout_discover_middle.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion

/**
 * 发现页推荐fragment
 */
class DiscoverRecommendFragment : BaseFragment(), LifecycleObserver {


    val model: DiscoverMiddleViewModel by viewModels()
    var currentSelectPosition = 0      //默认选中0
    private var STORE_CURRENT_POSITION = "STORE_CURRENT_POSITION" //记录当前选中位置

    var currentTabList: List<TabSortBean>? = null

    private var _binding : LayoutDiscoverMiddleBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutDiscoverMiddleBinding.inflate(layoutInflater,container,false)
//        val inflate = inflater.inflate(R.layout.layout_discover_middle, container, false)
        lifecycle.addObserver(this)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topRcy.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        binding.gifOrder.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_COLUMN_SUBSCRIBE_ALL).navigation()
        }

        binding.mRefresh.setOnRefreshListener {
            getChildTab()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        currentSelectPosition = savedInstanceState?.getInt(STORE_CURRENT_POSITION) ?: 0
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(STORE_CURRENT_POSITION, currentSelectPosition)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifePause() {
        binding.mRefresh.isRefreshing = false
//        gif_order?.onObserverPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onLifeResume() {
//        gif_order?.onObserverResume()
        getChildTab()
    }

    private fun getChildTab() {
        if (currentTabList?.isNotEmpty() == true) return


        lifecycleScope.launchWhenResumed {
            model.getTabSortFlow()
                .catch {
                    binding.mRefresh.isRefreshing = false
                }
                .onCompletion {
                    binding.mRefresh.isRefreshing = false
                }
                .collect {
                    currentTabList = it
                    if (it.isNotEmpty()) {
                        binding.mRefresh.isEnabled = false
                    }
                    //tabAdapter
                    initChildTab(it)
                }
        }
    }

    fun initChildTab(tabList:List<TabSortBean>){
        val map = tabList.map {
            it.title
        }
        val discoverRecoomendTabAdapter =
            DiscoverRecoomendTabAdapter(map as ArrayList<String>)
        discoverRecoomendTabAdapter.setOnItemClickListener { adapter, view, position ->
            currentSelectPosition = position
            discoverRecoomendTabAdapter.selectPosition = position
            binding.topRcy.smoothToCenter(position)
            LogUtil.addClickLogNew(
                LogEventConstants2.P_DISCOVER,
                "${position + 1}",
                LogEventConstants2.ET_TAB,
                map[position]
            )
            binding.viewPager.setCurrentItem(position, true)
        }
        binding.topRcy.adapter = discoverRecoomendTabAdapter
        discoverRecoomendTabAdapter.selectPosition = currentSelectPosition
        //viewpager
        val discoverRecoomendAdapter = DiscoverRecoomendAdapter(tabList, childFragmentManager)
//            view_pager.addOnPageChangeListener(VpListener(top_rcy,map))
        binding.viewPager.setAdapter(discoverRecoomendAdapter)
        binding.viewPager.currentItem = currentSelectPosition
    }
}