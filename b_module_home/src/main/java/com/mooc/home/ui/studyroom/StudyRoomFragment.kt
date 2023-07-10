package com.mooc.home.ui.studyroom

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.repackaged.com.google.common.collect.Iterators
import com.google.android.material.appbar.AppBarLayout
import com.lxj.xpopup.XPopup
import com.mooc.changeskin.SkinManager
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.base.BaseFragment
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.manager.StudyResourceEditPopUtil
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
import com.mooc.commonbusiness.model.eventbus.ScoreChangeNeedRefreshEvent
import com.mooc.commonbusiness.model.eventbus.StudyRoomResourceChange
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.model.folder.FolderTab
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.module.studyroom.FolderStudyRoomTabMenuFragmentAdapter
import com.mooc.commonbusiness.module.studyroom.StudyListAdapter
import com.mooc.commonbusiness.pop.studyroom.CreateStudyListPop
import com.mooc.commonbusiness.pop.studyroom.FolderDeletePop
import com.mooc.commonbusiness.pop.studyroom.StudyRoomResourceEditPop
import com.mooc.commonbusiness.route.Paths
import com.mooc.home.R
import com.mooc.home.constans.TagConstants.Companion.INTENT_TOTAL_SCORE
import com.mooc.home.databinding.HomeFragmentHomeStudyroomBinding
import com.mooc.home.model.StudyScoreResponse
import com.mooc.home.ui.pop.ShowWelcomeStudyRoomPop
import com.mooc.home.ui.studyroom.business.StudyRoomViewModel
import com.mooc.resource.utils.AppBarStateChangeListener
import com.mooc.statistics.LogUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Home学习室
 */
class StudyRoomFragment : BaseFragment() {


    val mViewModel: StudyRoomViewModel by viewModels()

    //学习清单适配器
    val studyListAdapter = StudyListAdapter(arrayListOf())

    var studyRoomFragmentAdapter: FolderStudyRoomTabMenuFragmentAdapter? = null;
    var tabArray = arrayListOf<FolderTab>()

    private var _binding: HomeFragmentHomeStudyroomBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentHomeStudyroomBinding.inflate(layoutInflater, container, false)
        SkinManager.getInstance().injectSkin(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //observer
        initObserver()

        loadData()

        initView()


    }


    private fun initObserver() {

        mViewModel.studyFolderMenu.observe(viewLifecycleOwner) {

            if (it == null) {
                return@observe
            }
            val newTrbArray = arrayListOf<FolderTab>()
            //添加一级tab
            if (it.level1?.isNotEmpty() == true) {
                newTrbArray.addAll(it.level1!!)
            }

            //添加其他tab
            if (it.level2?.isNotEmpty() == true) {
                newTrbArray.add(
                    FolderTab(
                        it.level2_type ?: ResourceTypeConstans.TYPE_ROOM_TAB_OTHER,
                        it.level2_name ?: "其他"
                    )
                )
            }

            //如果都没有添加一个空Fragment
            if (newTrbArray.isEmpty()) {
                newTrbArray.add(FolderTab(ResourceTypeConstans.TYPE_ROOM_TAB_EMPTY, ""))
            }


            //其他中包含的资源数组
            val level2Types = it.level2?.map { f ->
                f.type
            }

            //判断新老是否相同
            val e = compairTwoList(tabArray, newTrbArray)
            if (!e) {  //如果不相同重置
                tabArray = newTrbArray
                studyRoomFragmentAdapter =
                    FolderStudyRoomTabMenuFragmentAdapter(this, "0", tabArray, null)
                initBottomFragmentViewPager()
            }

            //设置其他中的二级菜单
            if (studyRoomFragmentAdapter != null) {
                if (level2Types != null)
                    studyRoomFragmentAdapter?.setLevel2(
                        childFragmentManager,
                        level2Types as ArrayList<Int>?
                    )
            }
        }


        //获取头部数据
        mViewModel.studyRoomliveDate.observe(viewLifecycleOwner, Observer {
            initHead(it)
        })

        //获取学习清单列表
        mViewModel.studyListLiveData.observe(viewLifecycleOwner, Observer {
            //设置排序按钮的显示隐藏
            val sortButtonVisiable = if (it.size > 1) View.VISIBLE else View.GONE
            binding.tvStudyListSort.visibility = sortButtonVisiable
            studyListAdapter.setList(it as MutableList<FolderItem>)
        })
    }


    /**
     * 比较两个数组是否相同
     */
    fun compairTwoList(old: ArrayList<FolderTab>, new: ArrayList<FolderTab>): Boolean {
        if (old.size != new.size) return false
        return Iterators.elementsEqual(old.iterator(), new.iterator())

    }

    private fun loadData() {
        mViewModel.getStudyScoreData()
        mViewModel.getRootFolder()
        mViewModel.getStudyRoomChildFolderMenu("0")
    }


    /**
     * 初始化底部分类适配器
     */
    private fun initBottomFragmentViewPager() {
        val names = tabArray.map {
            it.name
        }

        binding.viewPage2.adapter = studyRoomFragmentAdapter

        binding.mctStudyRoom.setUpWithViewPage2(
            binding.viewPage2,
            names as ArrayList<String>
        )

    }

    /**
     * 刷新底部文件夹列表
     * @param  resourceType 资源类型
     * 由于资源类型显示位置不确定，需要定位位置
     */
    fun refreshBottomFolderList(resourceType: Int? = null) {
        //刷新下当前fragment
        var refreshPosition = resourceType ?: binding.viewPage2.currentItem

        if (resourceType != null) {

            //如果是其他资源类型，type=999
            var tempType = resourceType
            if (studyRoomFragmentAdapter?.lever2?.contains(resourceType) == true) {
                tempType = ResourceTypeConstans.TYPE_ROOM_TAB_OTHER
            }

            tabArray.forEachIndexed { index, folderTab ->
                if (folderTab.type == tempType) {
                    refreshPosition = index
                }
            }
        }

        studyRoomFragmentAdapter?.refreshCurrentData(childFragmentManager, refreshPosition)
    }

    private fun initView() {
        //下拉刷新
        binding.msrlRoot.setOnRefreshListener {
            loadData()
            refreshBottomFolderList()
            binding.msrlRoot.postDelayed({
                hideRefreshView()
            }, 2000)
        }
        initStudyListAdapter()

        //点击设置首页
        val boolean = SpDefaultUtils.getInstance().getBoolean(SpConstants.STUDY_ROOM_FIRST, false)
        val tvSetHomeStr = if (boolean) "取消设为首页" else "设为首页"
        binding.tvSetHome.text = tvSetHomeStr
        binding.tvSetHome.setOnClickListener {
            val b = SpDefaultUtils.getInstance().getBoolean(SpConstants.STUDY_ROOM_FIRST, false)
            SpDefaultUtils.getInstance().putBoolean(SpConstants.STUDY_ROOM_FIRST, !b)
            val tvSetHomeStr = if (b) "设为首页" else "取消设为首页"
            binding.tvSetHome.text = tvSetHomeStr
        }
        //查看积分详情
        binding.tvLookScoreDetail.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_SCORE_DETAIL).navigation()
        }

        //点击数据看板
        binding.tvDataBoard.setOnClickListener {
            LogUtil.addClickLogNew(
                LogEventConstants2.P_ROOM,
                "1",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.studyroom_data_board)
            )
            ARouter.getInstance().build(Paths.PAGE_DATA_BOARD).navigation()
        }

        //点击学习档案
        binding.tvStudyRecord.setOnClickListener {
            LogUtil.addClickLogNew(
                LogEventConstants2.P_ROOM,
                "2",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.studyroom_study_record)
            )
            ARouter.getInstance().build(Paths.PAGE_STUDY_RECORD).navigation()
        }
        //点击学习清单排序
        binding.tvStudyListSort.setOnClickListener {
            //跳转到学习清单排序
            ARouter.getInstance().build(Paths.PAGE_STUDYLIST_SORT).navigation()
        }

        //点击我的下载
        binding.tvMyDownload.setOnClickListener {
            LogUtil.addClickLogNew(
                LogEventConstants2.P_ROOM,
                "3",
                LogEventConstants2.ET_ICON,
                resources.getString(R.string.studyroom_my_download)
            )

            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), PermissionApplyActivity.REQUEST_CODE_DEFAULT
                )
            } else {
                ARouter.getInstance().build(Paths.PAGE_MY_DOWNLOAD).navigation()
            }
        }

        //避免刷新事件被拦截
        binding.appBar.addOnOffsetChangedListener(object : com.mooc.resource.utils.AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: Int) {
                when (state) {
                    State.EXPANDED -> {
                        binding.msrlRoot.isEnabled = true
                    }
                    else -> {
                        binding.msrlRoot.isEnabled = false
                    }
                }
            }
        })
        //注册滑动监听
        binding.viewPage2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                LogUtil.addClickLogNew(
                    LogEventConstants2.P_ROOM,
                    "${position + 1}",
                    LogEventConstants2.ET_TAB,
                    tabArray[position].name
                )
            }
        })

        //好友积分排行
        binding.tvFriendRank.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_FRIEND_SCORE_RANK).navigation()
        }

    }

    fun hideRefreshView() {
        if (isAdded && !isDetached) { //如果没有被销毁
            binding.msrlRoot?.isRefreshing = false
        }
    }

    /**
     * 初始化清单列表适配器
     */
    private fun initStudyListAdapter() {
        binding.ibMessageTip.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_MY_MSG).navigation()
        }
        binding.rvStudyList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        //添加创建清单头部
        studyListAdapter.addHeaderView(StudyListAdapter.createAddHeadView(requireContext()) {
            if (!GlobalsUserManager.isLogin()) {     //检测登录
                ResourceTurnManager.turnToLogin()
                return@createAddHeadView
            }
            //点击添加学习清单文件夹
            showFolderAddPop()
        }, orientation = LinearLayout.HORIZONTAL)
        //添加收藏的清单头部
        studyListAdapter.addHeaderView(StudyListAdapter.createCollectHeadView(requireContext()) {
            if (!GlobalsUserManager.isLogin()) {     //检测登录
                ARouter.getInstance().build(Paths.PAGE_LOGIN).navigation()
                return@createCollectHeadView
            }
            //点击进入收藏的清单页面
            ARouter.getInstance().build(Paths.PAGE_COLLECT_STUDY_LIST).navigation()
        }, orientation = LinearLayout.HORIZONTAL)

        studyListAdapter.setOnItemClickListener { _, _, position ->
            //点击进入学习清单页面
            val get = mViewModel.studyListLiveData.value?.get(position)

            LogUtil.addClickLogNew(
                LogEventConstants2.P_ROOM,
                "${get?.id}",
                LogEventConstants2.ET_QD,
                "${get?.name}"
            )


            ARouter.getInstance().build(Paths.PAGE_STUDYLIST_DETAIL)
                .with(
                    Bundle().put(IntentParamsConstants.STUDYROOM_FOLDER_ID, get?.id)
                        .put(IntentParamsConstants.STUDYROOM_FOLDER_NAME, get?.name)
                )
                .navigation()

        }
        studyListAdapter.setOnItemLongClickListener { _, view, position ->
            val folderItem = mViewModel.studyListLiveData.value?.get(position)
            if (folderItem?.is_show == true) {
                toast(resources.getString(R.string.text_no_publicate_tip))
                return@setOnItemLongClickListener true
            }
            folderItem?.let {
                showFolderEditPop(view, it)
            }
            return@setOnItemLongClickListener true
        }

        binding.rvStudyList.adapter = studyListAdapter
    }

    /**
     * 显示文件夹创建弹窗，和重命名复用
     * @param folderItem 文件夹bean，如果不为空，则代表是需要重命名
     */
    private fun showFolderAddPop(folderItem: FolderItem? = null) {
        val createStudyListPop = CreateStudyListPop(requireContext(), folderItem)
        createStudyListPop.onConfirmCallBack = {
            if (folderItem != null) {
                //发送重命名文件夹请求
                mViewModel.renameFolder(folderItem.id, it, pop = createStudyListPop)
            } else {
                //发送创建文件夹请求
                mViewModel.createNewStudyFolder(it, pop = createStudyListPop)
            }

        }
        XPopup.Builder(requireContext())
            .autoOpenSoftInput(true)
            .asCustom(createStudyListPop)
            .show()
    }


    /**
     * 显示文件夹编辑pop
     */
    private fun showFolderEditPop(view: View, folderItem: FolderItem) {
        StudyResourceEditPopUtil.showEditPop(view, folderItem) {
            when (it) {
                StudyRoomResourceEditPop.EVENT_DELETE -> {
                    //显示删除文件夹弹窗
                    showFolderDeletePop(folderItem)
                }
                StudyRoomResourceEditPop.EVENT_MOVE -> {
                    // 进入移动文件夹页面
                    ARouter.getInstance().build(Paths.PAGE_STUDYLIST_MOVE)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, folderItem.id)
                        .withString(
                            IntentParamsConstants.PARAMS_RESOURCE_TYPE,
                            ResourceTypeConstans.TYPE_STUDY_FOLDER
                        )
                        .navigation()
                }
                StudyRoomResourceEditPop.EVENT_RENAME -> {
                    // 显示重命名弹窗
                    showFolderAddPop(folderItem)
                }

            }
        }
    }

    /**
     * 展示文件夹删除弹窗
     */
    private fun showFolderDeletePop(folderItem: FolderItem) {
        XPopup.Builder(requireContext())
            .asCustom(FolderDeletePop(requireContext(), folderItem.name) {
                mViewModel.deleteFolder(folderItem.id)
            })
            .show()
    }


    /**
     * 设置头部数据
     * @param it 当为null的时候，为退出登录状态
     */
    private fun initHead(it: StudyScoreResponse?) {
        if (it == null) {   //如果为空，则清空积分信息
            binding.tvTodayScore.text = ""
            binding.tvAllScore.text = ""
            binding.tvRegistNum.text = ""
            binding.tvMessageNum?.visibility = View.INVISIBLE
            binding.tvFriendRank?.visibility = View.INVISIBLE
            return
        }
        it.apply {
            binding.tvFriendRank?.visibility = View.INVISIBLE
            binding.tvTodayScore.setText(it.today_score.toString())
            val totalScoreStr = "共${it.total_score}积分"
            val spannableString = spannableString {
                scaleSpan {
                    str = totalScoreStr
                    start = 1
                    end = totalScoreStr.indexOf("积分")
                    scale = 1.4f
                }
            }
            binding.tvAllScore.text = spannableString
            binding.tvRegistNum.text = it.user_total.toString()

            val totalScore=it.total_score;

            binding.tvIntegralList.setOnClickListener {
                ARouter.getInstance().build(Paths.PAGE_INTEGRADL_EXCHANGE).withInt(INTENT_TOTAL_SCORE,totalScore).navigation();
            }

            val unreadNum = if (it.unread_num > 999) 999 else this.unread_num
            binding.tvMessageNum.text = unreadNum.toString()
            if (unreadNum <= 0) {
                binding.tvMessageNum?.visibility = View.INVISIBLE

            } else {
                binding.tvMessageNum?.visibility = View.VISIBLE
            }
            if (!it.learn_read) {
                if (it.learn_score > 0) {
                    val showStudyRoomPointPop = ShowWelcomeStudyRoomPop(
                        requireContext(),
                        it.learn_score.toString(),
                        it.learn_count.toString()
                    )
                    XPopup.Builder(requireContext())
                        .asCustom(showStudyRoomPointPop)
                        .show()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(userInfo: UserLoginStateEvent) {
        loge("${this::class.java.simpleName}收到了登录事件")
        if (userInfo.userInfo == null) {    //null代表退出登录
            initHead(null)
            //清空学习清单
            studyListAdapter.setList(null)
            return
        }
        //登录态，重新查询数据
        loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserScoreEvent(userInfo: ScoreChangeNeedRefreshEvent) {
        if (GlobalsUserManager.isLogin()) {
            mViewModel.getStudyScoreData()
        }
    }

    /***
     * 文件夹进行移动删除等操作事件
     * 3。4。1新版本增加公开操作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFolderChangeEvent(resourceChange: StudyRoomResourceChange) {
        if (resourceChange.moveType == StudyRoomResourceChange.TYPE_FOLODER) {
            mViewModel.getRootFolder()
        } else if (resourceChange.moveType == StudyRoomResourceChange.TYPE_RESOURCE) {
            refreshBottomFolderList()
        }
    }

    /***
     * 有资源添加到了学习室，刷新当前列表
     * (当前排序是乱的，所以需要先定位位置)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResourceChangeEvent(resourceChange: RefreshStudyRoomEvent) {
//        val refreshItemIndex = when (resourceChange.resourceType) {
//            ResourceTypeConstans.TYPE_COURSE -> 0
//            ResourceTypeConstans.TYPE_E_BOOK -> 1
//            ResourceTypeConstans.TYPE_NOTE -> 2
//            else -> 3
//        }
        refreshBottomFolderList(resourceChange.resourceType)
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)

    }
}