package com.mooc.home.ui.discover

//import kotlinx.android.synthetic.main.home_fragment_discover_base.*
//import kotlinx.android.synthetic.main.home_fragment_discover_base.rvLeft
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.adapter.HorizontalTabAdapter
import com.mooc.commonbusiness.base.BaseFragment
import com.mooc.commonbusiness.model.studyroom.DiscoverTab
import com.mooc.commonbusiness.model.studyroom.SortChild
import com.mooc.commonbusiness.utils.ShowDialogUtils
import com.mooc.home.R
import com.mooc.home.databinding.HomeFragmentDiscoverBaseBinding
import com.mooc.home.ui.discover.adapter.Child2SortAdapter
import com.mooc.home.ui.discover.mooc.DiscoverMoocChildFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion

/**
 * 发现页Fragment基类
 * 处理公共的二级分类请求逻辑
 */
abstract class BaseDiscoverFragment : BaseFragment(), LifecycleObserver {

    companion object {
        const val PARAMS_PARENT_ID = "params_parent_id"
        const val PARAMS_SORT_ID = "params_sort_id"
        const val PARAMS_RESOURCE_TYPE = "params_resource_type"
    }

    //
//    val discoverViewModel by lazy {
//        ViewModelProviders.of(this)[DiscoverViewModel::class.java]
//    }
    val discoverViewModel by viewModels<DiscoverViewModel>()


    //    val parentViewModel by lazy {
//        ViewModelProviders.of(requireParentFragment())[DiscoverViewModel::class.java]
//    }
    val parentViewModel by viewModels<DiscoverViewModel>(ownerProducer = { requireParentFragment() })
    private var _binding: HomeFragmentDiscoverBaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentDiscoverBaseBinding.inflate(layoutInflater, container, false)
//        val inflate = inflater.inflate(R.layout.home_fragment_discover_base, null)
        lifecycle.addObserver(this)
        return binding.root
    }

//    abstract fun getLayout(): Int

//    protected var childRoot : View? = null

    //    protected var childFragmentImpl: DiscoverChildListFragmentInterface? = null
    protected var childListFragment: Fragment? = null


    var tabId = -1// 一级分类id
    var parentResourceType = -1//一级分类中的resourceType
    val horizontalTabAdapter = HorizontalTabAdapter(arrayListOf())
    val linearLayoutManager by lazy {
        LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        childListFragment = getListFragment()
        //加载二级分类数据
//        getChildTab()
//        observeChildSelect()

    }

    /**
     * 监听需要跳转的位置
     */
    private fun observeChildSelect() {
        if (horizontalTabAdapter.data.isEmpty()) return
        val data = horizontalTabAdapter.data
        //没有赋值的时候，选中默认
        if (parentViewModel.tabSelectPositionPair.value == null) {
            selectTab(0, data)
        }
        parentViewModel.tabSelectPositionPair.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            val type = arguments?.getInt(DiscoverFragmentAdapter.PARAM_RELATION_TYPE) ?: -1
            if (it.first != type || it.second.isEmpty()) return@Observer

            //查找id，所在的位置
            var indexOfChild = data.indexOfFirst { discover ->
                it.second == discover.id.toString()
            }
            //防止找不到，默认选中当前,都没有则0
            if (indexOfChild < 0) {
                indexOfChild =
                    if (horizontalTabAdapter.selectPosition != -1) horizontalTabAdapter.selectPosition else 0
            }
//            selectTab(indexOfChild,data[indexOfChild].apply { this.resource_type = parentResourceType })
            selectTab(indexOfChild, data)
            parentViewModel.tabSelectPositionPair.value = null
        })
    }

    abstract fun getListFragment(): Fragment?

    /**
     * 初始化View
     */
    private fun initView() {
        //初始化头部
        tabId = arguments?.getInt(DiscoverFragmentAdapter.PARAM_TABID) ?: -1
        parentResourceType = arguments?.getInt(DiscoverFragmentAdapter.PARAM_RESOURCE_TYPE) ?: -1

        if (tabId == 3) { //tabid 为3代表幕客类型，需要现实慕课类型特有头部
            binding.discoverMoocHead.visibility = View.VISIBLE
        }
        //初始化emptyView点击事件
        binding.emptyView.setOnClickListener {
            getChildTab()
        }

        initHeadview()

        binding.swipeRoot.setOnRefreshListener {
            getChildTab()
        }
    }


    var currentSort = "-sort_top" //当前排序类型，默认综合
    var currentPlatForm = "" //课程当前选中平台,默认全部
    var currentCourseStatus = "2" //当前搜索课程状态，默认全部
    var currentCourseTypeMap = mapOf<String, String>()

    var currentTabList: List<DiscoverTab>? = null

    private fun initHeadview() {

        //设置headView排序点击事件,点击切换数据
        binding.discoverCommonSortHead.onClickSortCallBack = {
            currentSort = it
            changeChildListData(mapOf("ordering" to it))
        }

        //设置课程平台点击事件,点击切换数据
        binding.discoverMoocHead.onPlatformCheckCallback = {
            currentPlatForm = it
            changeChildListData(mapOf("platform" to it))
        }

        //设置课程状态选择事件回调
        binding.discoverMoocHead.onCourseStateCallback = {
            currentCourseStatus = it
            changeChildListData(mapOf("process" to it))
        }

        //设置课程类型选择事件回调
        binding.discoverMoocHead.onCourseTypeCheckCallback =
            { is_free, verified_active, is_have_exam ->
                currentCourseTypeMap = mapOf(
                    "is_free" to is_free,
                    "verified_active" to verified_active,
                    "is_have_exam" to is_have_exam
                )
                changeChildListData(currentCourseTypeMap)
            }

    }

    /**
     * 统一抽取到Base中，发现都一样
     * 获取二级分类数据
     */
    private fun getChildTab() {
        if (tabId == -1) return
        if (currentTabList?.isNotEmpty() == true) return


        lifecycleScope.launchWhenResumed {
            binding.swipeRoot.isRefreshing = true
            discoverViewModel.getChildColumeTabFlow(tabId)
                .onCompletion {
                    binding.swipeRoot.isRefreshing = false
                }
                .collect { it ->
                    currentTabList = it
                    if (it.isNotEmpty()) {
                        binding.swipeRoot.isEnabled = false
                        //初始化Adapter
                        binding.rvHorziontalTab.layoutManager = linearLayoutManager
                        horizontalTabAdapter.setList(it)
                        horizontalTabAdapter.setOnItemClickListener { _, _, position ->
                            selectTab(position, it as MutableList<DiscoverTab>)
                        }
                        binding.rvHorziontalTab.adapter = horizontalTabAdapter

                        //回调加载完成
                        binding.emptyView.visibility = View.GONE

                        //如果有事件需要打开特定位置，则按需选中
                        observeChildSelect()
                    }
                }
        }
    }


    fun selectTab(position: Int, tabs: MutableList<DiscoverTab>) {
        if (position !in tabs.indices) return
        horizontalTabAdapter.selectPosition = position
        val tab = tabs.get(position).apply {
            this.resource_type = parentResourceType
        }
        //让rv滑动过去
        linearLayoutManager.scrollToPositionWithOffset(position, 180.dp2px())

        //先隐藏左边
        binding.rvLeft.visibility = View.GONE
        //显示隐藏，排序

        setOwnOperationHeadState(tab.own_operation)
        //查找是否有三级分类
//        onChildTabCallBack(tab)
//        discoverViewModel.getChildSort(tab.id)
        discoverViewModel.getChildSortCallBack(tab.id).observe(viewLifecycleOwner, Observer {
            //判断是否有三级分类，选择是否显示左边分类view
            if (it.isEmpty()) {
                binding.rvLeft.visibility = View.GONE
                onTabClick(tab)
                binding.discoverMoocHead.leftMargin = 0
                return@Observer
            }
            binding.rvLeft.visibility = View.VISIBLE
            binding.discoverMoocHead.leftMargin = 78.dp2px()
            //设置适配器
            val child2SortAdapter = Child2SortAdapter(it as ArrayList<SortChild>)
            child2SortAdapter.setOnItemClickListener { adpater, _, position ->
                if (it[position].parent_id == 591//二级菜单为慕课中的职业培训
                    && (it[position].id == 598 || it[position].id == 731)//三级菜单为慕课->职业培训->信息素养和心理咨询
                ) {//工信部点击不进入课程详情页面，需要弹框的课程
                    activity?.let { it1 -> ShowDialogUtils.showDialog(it1) }

                } else {
                    child2SortAdapter.selectPosition = position
                    //重置head数据
//                discoverMoocHead.resetState()
//                discoverCommonSortHead.resetState()
                    setOwnOperationHeadState((adpater.data.get(position) as SortChild).own_operation)
                    changeChildListData(mapOf("sort_id" to it[position].id.toString()), false)
                }

            }
            binding.rvLeft.layoutManager = LinearLayoutManager(requireContext())
            binding.rvLeft.adapter = child2SortAdapter

            //默认加载第一个分类的数据
            onTabClick(tab, it[0].id.toString())
        })

//        onTabClick(tab)
    }


    /**
     * 设置头部显示状态，综合，最新，最热
     * @param own_operation  0代表三个都不显示 ，只有综合列表
     */
    fun setOwnOperationHeadState(own_operation: Int) {
        binding.discoverCommonSortHead.visibility = View.VISIBLE

        if (own_operation == 0) {
            currentSort = "-sort_top"
        }
//        val visiable = if(own_operation == 0) View.GONE else View.VISIBLE
//        discoverCommonSortHead.visibility = visiable
        binding.discoverCommonSortHead.setOwnOperation(own_operation, currentSort)


    }

    /**
     * 子类实现tab切换回调
     * 子类除了提供的ListFragment不一样，其他都一样，统一抽取到Base中
     */
    fun onTabClick(tab: DiscoverTab, sort_id: String = "") {
        val put = Bundle().put(PARAMS_PARENT_ID, tab.id.toString())
            .put(PARAMS_RESOURCE_TYPE, tab.resource_type.toString()).put(PARAMS_SORT_ID, sort_id)
        if (childListFragment?.isAdded == true) {
//            childListFragment?.arguments = put
            changeChildListData(
                hashMapOf("sort_pid" to tab.id.toString(), "sort_id" to sort_id),
                true
            )
            return
        }
        childListFragment?.let {
            it.arguments = put
            childFragmentManager.beginTransaction().replace(R.id.flChildContainer, it).commit()
        }
    }


    /**
    //     * 切换子列表数据数据
    //     * @param sortChild 三级分类id
    //     */
    fun changeChildListData(map: Map<String, String>, reset: Boolean = false) {

        var plus = map.plus(mapOf("ordering" to currentSort))   //刷新的时候携带当前选中的排序
        if (childListFragment is DiscoverMoocChildFragment) {   //如果是慕课，要携带幕课的其他选项
            plus = plus.plus(mapOf("platform" to currentPlatForm, "process" to currentCourseStatus))
            if (currentCourseTypeMap.isNotEmpty()) {
                plus = plus.plus(currentCourseTypeMap)
            }
        }
        (childListFragment as DiscoverChildListFragmentInterface).changeListData(plus, reset)
    }

    /**
     * 设置当前列表有多少条数
     */
    open fun setNumStr(num: String) {
        binding.discoverCommonSortHead.setNumText(num)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifePause() {
        binding.swipeRoot.isRefreshing = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onLifeResume() {
        getChildTab()
    }


}