package com.mooc.home.ui.discover

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.runOnMain
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseFragment
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.model.studyroom.DiscoverTab
import com.mooc.commonbusiness.net.network.IStateObserver
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.view.DiscoverAcitivtyFloatView
import com.mooc.discover.view.HomeTabCustomView
import com.mooc.home.R
import com.mooc.home.databinding.HomeFragmentHomeDiscoverBinding
import com.mooc.resource.listener.ViewPager2OnTabSelectedListener
import com.mooc.statistics.LogUtil
import com.tbruyelle.rxpermissions2.RxPermissions
//import kotlinx.android.synthetic.main.home_fragment_home_discover.*
import me.devilsen.czxing.Scanner
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.Field


/**
 *
 * @ProjectName:发现页
 * @Package:
 * @ClassName:
 * @Description:    java类作用描述
 * @Author:         xym
 * @CreateDate:     2020/8/20 7:40 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/20 7:40 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class DiscoverFragment : BaseFragment() {


    val discoverViewModel by viewModels<DiscoverViewModel>()
    var discoverPagerAdapter: DiscoverFragmentAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }

    private var _binding : HomeFragmentHomeDiscoverBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        _binding = HomeFragmentHomeDiscoverBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initListener()

        getFirstTabData()

        //floatview的显示隐藏监听
        DiscoverAcitivtyFloatView.showControllLiveData.observe(viewLifecycleOwner, {
            if (it) {
                binding.dafView.show()
            } else {
                binding.dafView.hide()
            }
        })

        fixViewPager2Sensitivity()

    }

    fun fixViewPager2Sensitivity() {
        try {
            val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView = recyclerViewField.get(binding.vpDiscover) as RecyclerView
            val touchSlopField: Field = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(recyclerView) as Int
//            touchSlopField.set(recyclerView, touchSlop * 4) //6 is empirical value
            touchSlopField.set(recyclerView, 60) //6 is empirical value
        } catch (ignore: java.lang.Exception) {
        }

    }

    //    @Permission(Manifest.permission.CAMERA, requestCode = 0)
    fun scanCode() {


//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                requireActivity(), arrayOf(Manifest.permission.CAMERA),
//                1
//            )
//            return
//        }
//
//        if (androidx.core.app.ActivityCompat.checkSelfPermission(
//                requireActivity(),
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_DENIED
//        ) {
//            toast(getString(R.string.permission_with_camera))
//            return
//        }


        Scanner.with(requireContext())
            .showAlbum(false)
            .setOnScanResultDelegate { _, result, _ ->
                runOnMain {
                    discoverViewModel.resolveQrResult(result)
                }
            }
            .start()
    }

    /**
     * 初始化监听
     */
    @SuppressLint("CheckResult")
    private fun initListener() {
        binding.moocSwipeRefresh.setOnRefreshListener {
            //如果tab没有数据
            refreshOnNoTabData()
        }


        //二维码
        binding.ibQrScan.setOnClickListener {
//            scanCode()
            val rxPermissions = RxPermissions(requireActivity())
            if (rxPermissions.isGranted(Manifest.permission.CAMERA)) {
                scanCode()
            } else {
                rxPermissions.requestEach(
                    Manifest.permission.CAMERA
                )
                    ?.subscribe {
                        if (it.granted) {
                            scanCode()
                        } else if (it.shouldShowRequestPermissionRationale) {

                        } else {
                            toast(getString(R.string.permission_with_camera))
                        }
                    }
            }
        }
        //跳转到搜索
        binding.tvSearch.setOnClickListener {
            val filter = "{type:" + "ALL" + ";}"
//            LogUtil.addClickLog(LogPageConstants.PID_DISCOVER, LogEventConstants.EID_TO_SEARCH, LogEventConstants.BID_FORM, LogPageConstants.PID_DISCOVER, LogPageConstants.PID_SEARCH, filter)
            ARouter.getInstance().build(Paths.PAGE_SEARCH).navigation()
        }


        //注册快捷入口点击切换此页面的tab事件的LiveDataBus，int，代表切换的tab类型
        LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_DISCOVER_TAB_CHANGE)
            .observe(this, Observer<Pair<Int, String>> {
                discoverViewModel.tabDatas.value?.data?.forEachIndexed { index, discoverTab ->

                    if (discoverTab.relation_type == it.first) {
                        //找到需要跳转的地方，并切换
                        if (it.second.isNotEmpty()) {
                            //如果不为空，则传递，需要切换的二级分类id
//                        LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_DISCOVER_TAB_CHILD_ID).postStickyData(it.second)
                            discoverViewModel.tabSelectPositionPair.postValue(it)
                        }
                        binding.vpDiscover.currentItem = index
                        return@forEachIndexed
                    }
                }
            })

        //通过intent切换子tab
        LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_CHILDE_FRAGMENT_SELECTPOSITION)
            .observerSticky(this, Observer<Pair<Int, Int>> {
                if (it.first == 0 && it.second == 0) { //需要切换到推荐
                    discoverViewModel.tabDatas.value?.data?.let {
                        val indexOf = it.indexOf(it.find { it.resource_type == -1 })
                        if (indexOf <  binding.vpDiscover.childCount) {
                            binding.vpDiscover.currentItem = indexOf
                        }
                    }
                }
            }, true)

    }


    /**
     * tab没加载成功的刷新
     */
    fun refreshOnNoTabData() {
        if (discoverViewModel.tabDatas.value?.data == null || discoverViewModel.tabDatas.value?.data?.isEmpty() == true) {
            getFirstTabData()
        }
    }


    /**
     * 获取一级分类
     */
    fun getFirstTabData() {

        discoverViewModel.tabDatas.observe(
            viewLifecycleOwner,
            object : IStateObserver<List<DiscoverTab>>() {
                override fun onDataChange(data: List<DiscoverTab>?) {
                    super.onDataChange(data)
                    binding.moocSwipeRefresh.isRefreshing = false

                    if (data?.isEmpty() == true) {
                        showEmptyView(true)
                        return
                    }
                    showEmptyView(false)
                    data?.apply {
                        discoverPagerAdapter = DiscoverFragmentAdapter(data, this@DiscoverFragment)
                        binding.vpDiscover.adapter = discoverPagerAdapter

                        setTabLayoutTab(data.map { tab -> tab.name } as ArrayList<String>)

                        //接口请求成功禁用refreshLayout
                        binding.moocSwipeRefresh.isEnabled = false
                    }

                }

                override fun onError(e: Throwable?) {
                    super.onError(e)
                    binding.moocSwipeRefresh.isRefreshing = false

                    showEmptyView(true)
                }
            })
        discoverViewModel.getDisCoverTabData()
    }

    fun showEmptyView(b: Boolean) {
        val emptyVisibale = if (b) View.VISIBLE else View.GONE
        binding.emptyView.visibility = emptyVisibale
    }


    fun setTabLayoutTab(str: ArrayList<String>) {
        binding.tabLayout.removeAllTabs()
        for (i in 0 until str.size) {
            binding.tabLayout.addTab( binding.tabLayout.newTab())
        }

        for (i in 0 until str.size) {
            binding.tabLayout.getTabAt(i)?.setCustomView(makeTabView(str[i]))
        }

        binding.tabLayout.getTabAt(0)?.select()

        //adapter建立关联
//        vpDiscover.addOnPageChangeListener(TabLayoutOnPageChangeListener(mTabLayout))
        binding.vpDiscover.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position), true)

                LogUtil.addClickLogNew(
                    LogEventConstants2.P_DISCOVER, "${position + 1}",
                    LogEventConstants2.ET_TAB, str[position]
                )


            }
        })
        binding.tabLayout.addOnTabSelectedListener(
            ViewPager2OnTabSelectedListener(
                binding.vpDiscover
            )
        )
    }

    /**
     * 设置tablayout tab 自定义样式
     * @param position
     * @return
     */
    private fun makeTabView(str: String): View {
        val tabView = HomeTabCustomView(requireContext())
        tabView.setTitle(str)
        return tabView
    }

    /**
     * 用户由于多点登录被挤掉，重新登录后刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(userInfo: UserLoginStateEvent) {
        if (userInfo.userInfo != null) {
            refreshOnNoTabData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }
}