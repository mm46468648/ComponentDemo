package com.mooc.studyroom.ui.activity

//import kotlinx.android.synthetic.main.studyroom_activity_studylist_detail.*
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.*
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.manager.StudyResourceEditPopUtil
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.RoomTabBean
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
import com.mooc.commonbusiness.model.eventbus.StudyRoomResourceChange
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.model.studyroom.PublicStudyListResponse
import com.mooc.commonbusiness.module.studyroom.FolderTabMenuFragmentAdapter
import com.mooc.commonbusiness.module.studyroom.StudyListAdapter
import com.mooc.commonbusiness.module.studyroom.StudyListDetailViewModel
import com.mooc.commonbusiness.pop.CommonAlertPop
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.pop.studyroom.CreateStudyListPop
import com.mooc.commonbusiness.pop.studyroom.FolderDeletePop
import com.mooc.commonbusiness.pop.studyroom.StudyRoomResourceEditPop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityStudylistDetailBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 学习清单详情页面
 */
@Route(path = Paths.PAGE_STUDYLIST_DETAIL)
class StudyListDetailActivity : BaseActivity() {

    //    val mViewModel: StudyListDetailViewModel by lazy {
//        ViewModelProviders.of(this)[StudyListDetailViewModel::class.java]
//    }
    val mViewModel: StudyListDetailViewModel by viewModels()

    var tabArray = mutableListOf<RoomTabBean>()

    //学习清单适配器
    val studyListAdapter = StudyListAdapter(arrayListOf())

    //文件夹id,name
    val foldId by extraDelegate(IntentParamsConstants.STUDYROOM_FOLDER_ID, "")
    val foldName by extraDelegate(IntentParamsConstants.STUDYROOM_FOLDER_NAME, "")


    var bottomFragmentAdapter: FolderTabMenuFragmentAdapter? = null;

    val awardTenScoreStr = "奖励10积分"
    val deductTenScoreStr = "扣除10积分"
    val listHave = "清单中存在"

    lateinit var inflater: StudyroomActivityStudylistDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
        if (foldId.isEmpty()) {
            finish()
            return
        }

        inflater = StudyroomActivityStudylistDetailBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        initView()

        //获取学习清单列表
//        mViewModel.getListLiveData().observe(this, Observer {
//            studyListAdapter.setList(it as MutableList<FolderItem>)
//        })

        mViewModel.getStudyListDetail().observe(this, Observer {
            if (it == null) return@Observer
            setPublicStatus(it.property.is_show)

            studyListAdapter.setList(it.folder.folder.items as MutableList<FolderItem>)
        })

        mViewModel.getChildFolderNew(foldId)

//        LogUtil.addLoadLog(LogPageConstants.PID_FOLDER + "#" + foldId)


        mViewModel.getStudyRoomChildFolderMenu(foldId)
        showFolderResourceDel()
    }

    /**
     * 学习清单资源后台删除后，提示用户：您的学习清单中“XX”“XX”资源已失效
     */
    private fun showFolderResourceDel() {
        mViewModel.getFolderResourceDelAsync(foldId)
        mViewModel.folderResourceDelList.observe(this, {
            if (it != null && it.size > 0) {
                val publicDialogBean = PublicDialogBean()
                var resourceStr = ""
                for (i in it.indices) {
                    val folderSource = it[i]
                    if (folderSource.title.isNotEmpty()) {
                        resourceStr += "\"" + folderSource.title + "\""
                    }

                }
                publicDialogBean.strMsg = "您的学习清单中" + resourceStr + "资源已失效"
                publicDialogBean.strTv = resources.getString(R.string.text_ok)
                val dialog = PublicOneDialog(this, publicDialogBean)
                XPopup.Builder(this)
                    .asCustom(dialog)
                    .show()

            }
        })
    }


    //是否是公开的子文件夹
    private fun isChildPubFolder(): Boolean {
        return mViewModel.getStudyListDetail().value?.property?.is_show == true && mViewModel.getStudyListDetail().value?.property?.pid != 0

    }

    //是否是子文件夹
    private fun isChildFolder(): Boolean {
        return mViewModel.getStudyListDetail().value?.property?.pid != 0

    }


    private fun refreshBottomFolderList() {
        if (inflater.viewPager2 != null && bottomFragmentAdapter != null)
            bottomFragmentAdapter?.refreshCurrentData(
                supportFragmentManager,
                inflater.viewPager2.currentItem
            )
    }

    /**
     * 初始化控件
     */
    private fun initView() {
//        commonTitle.middle_text = foldName
        inflater.commonTitle.tv_right?.visibility = View.GONE
        inflater.commonTitle.ib_right?.visibility = View.GONE
        val inflate = View.inflate(this, R.layout.studyroom_layout_sepcial_title, null)
        inflate.findViewById<TextView>(R.id.tvTitleLeft).text = foldName
        inflater.commonTitle.addExtraLayout(inflate)

        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.commonTitle.setOnRightIconClickListener(View.OnClickListener {

            mViewModel.getShareData(foldId).observe(this, Observer {
                showShareDialog(it)
            })

        })
        inflater.commonTitle.right_text = "去公开"
        inflater.commonTitle.tv_right?.setDrawLeft(R.mipmap.studyroom_ic_studylist_lock)
        inflater.commonTitle.setOnRightTextClickListener(View.OnClickListener {
            clickPublicStudyList()
        })
        initStudyListAdapter()


        inflater.commonTitle.tv_right?.post {
            val left = inflater.commonTitle.tv_right?.getX()?.toInt() ?: 0

            loge("left:${left}")

            val layoutParams = inflate.layoutParams
            layoutParams.width = left - 60.dp2px()
            inflate.layoutParams = layoutParams
        }

        mViewModel.studyFolderMenu.observe(this) {

            //防空判断
            if (it == null) {
                inflater.mctStudyList.visibility = View.GONE
                return@observe
            }
            it.level1?.forEachIndexed { index, folderTab ->
                tabArray.add(index, RoomTabBean(folderTab.type, folderTab.name))
            }

            if (it.level2 != null && (it.level2?.size ?: 0) > 0) {

                val leve2Tab = mutableListOf<Int>()
                it.level2?.forEachIndexed { index, folderTab -> leve2Tab.add(folderTab.type) }
                //有收藏
                it.level2_name?.let { it1 ->
                    tabArray?.add(RoomTabBean(it.level2_type, it.level2_name))
                    bottomFragmentAdapter = FolderTabMenuFragmentAdapter(
                        this,
                        foldId,
                        tabArray,
                        leve2Tab,
                        isChildPubFolder()
                    )
                    initBottomFragmentViewPager()
                }
            } else {
                if (tabArray.size == 0) {
                    tabArray.add(0, RoomTabBean(ResourceTypeConstans.TYPE_ROOM_TAB_EMPTY, ""))
                }
                bottomFragmentAdapter =
                    FolderTabMenuFragmentAdapter(this, foldId, tabArray, null, isChildPubFolder())
                initBottomFragmentViewPager()

            }
            //就剩一个空页面不显示tab
            if (tabArray.size == 1 && (tabArray.get(0).name?.isEmpty() == true)) {
                inflater.mctStudyList.visibility = View.GONE
            }


        }


    }

    fun setPublicStatus(status: Boolean) {
        if (status) {
            inflater.commonTitle.right_text = "取消公开"
            inflater.commonTitle.tv_right?.setDrawLeft(R.mipmap.studyroom_ic_studylist_unlock)
        } else {
            inflater.commonTitle.right_text = "去公开"
            inflater.commonTitle.tv_right?.setDrawLeft(R.mipmap.studyroom_ic_studylist_lock)
        }

        if (isChildFolder()) {
            inflater.commonTitle.tv_right?.visibility = View.GONE
            inflater.commonTitle.ib_right?.visibility = View.GONE
        } else {
            inflater.commonTitle.tv_right?.visibility = View.VISIBLE
            inflater.commonTitle.ib_right?.visibility = View.VISIBLE
        }

        //发送当前学习清单公开状态
//        LiveDataBus.get().with(LiveDataBusEventConstants.STUDYLIST_PUBLIC_STATUS)
//            .postStickyData(Pair<String, Boolean>(foldId, status))

//        EventBus.getDefault().postSticky(FolderPublicStatusEvent(foldId,status))
        mViewModel.publicStatus.value = status
    }

    /**
     * 点击公开或取消公开
     */
    private fun clickPublicStudyList() {
        if (inflater.commonTitle.right_text == "去公开") {
            mViewModel.getPublicMessage(foldId).observe(this, Observer {
                showPublicStatusDialog(it)
            })
        }

        if (inflater.commonTitle.right_text == "取消公开") {
            showCanclePublicDialog()
        }
    }

    /**
     * 展示公开状态弹窗
     */
    fun showPublicStatusDialog(response: PublicStudyListResponse) {
        val content = when (response.status) {
            1 -> {
                if (response.invalid_resources?.size == 1) {
                    "清单中存在 ${response.invalid_resources?.get(0)} 等1个失效资源，有效资源数量少于10个，不能公开。"
                } else if (response.invalid_resources?.size ?: 0 > 1) {
                    "清单中存在 ${response.invalid_resources?.get(0)}、${response.invalid_resources?.get(1)} 等${response.invalid_resources_count}个失效资源，有效资源数量少于10个，不能公开。"
                } else {
                    "资源数量少于10个，不能公开。"
                }
            }
            2 -> {
                "资源数量少于10个，不能公开。"
            }
            3 -> {
                if (response.invalid_resources?.size == 1) {
                    "清单中存在 ${response.invalid_resources?.get(0)} 等1个失效资源，公开后平台其它用户将通过您的个人主页看到此学习清单，每个学习清单奖励10积分，上限50分。"
                } else if (response.invalid_resources?.size ?: 0 > 1) {
                    "清单中存在 ${response.invalid_resources?.get(0)}、${response.invalid_resources?.get(1)} 等${response.invalid_resources_count}个失效资源，公开后平台其它用户将通过您的个人主页看到此学习清单，每个学习清单奖励10积分，上限50分。"
                } else {
                    "公开后平台其它用户将通过您的个人主页看到此学习清单，每个学习清单奖励10积分，上限50分。"
                }
            }
            4 -> {
                "公开后平台其它用户将通过您的个人主页看到此学习清单，每个学习清单奖励10积分，上限50分。"
            }
            else -> ""
        }

        //状态码是3，4的时候可以公开
        val confirmStr = if (response.status > 2) "确定" else "确定"


        var dialogContent: CharSequence = content
        //积分字样转换颜色
        if (content.contains(listHave)) {
            dialogContent = spannableString {
                str = content
                colorSpan {
                    start = listHave.length
                    end = content.lastIndexOf("等")
                    color = Color.parseColor("#E17F0E")
                }
            }
        }

        if (content.contains(awardTenScoreStr)) {
            dialogContent = spannableString {
                str = dialogContent
                colorSpan {
                    start = content.indexOf(awardTenScoreStr)
                    end = content.indexOf(awardTenScoreStr) + awardTenScoreStr.length
                    color = Color.parseColor("#E17F0E")
                }
            }
        }

        val cancle = object : CommonAlertPop.Cancle {
            override val position: Int
                get() = CommonAlertPop.Position.LEFT
            override val text: String
                get() = "取消"
            override val cancleBack: (() -> Unit)?
                get() = null
        }
        XPopup.Builder(this)
            .maxWidth(300.dp2px())
            .asCustom(CommonAlertPop(this, dialogContent, object : CommonAlertPop.Confirm {
                override val position: Int
                    get() = CommonAlertPop.Position.RIGHT

                override val text: String
                    get() = confirmStr
                override val confirmBack: () -> Unit
                    get() = {
                        //需要再调一个接口确认公开
                        mViewModel.postPublicList(foldId)
                            .observe(this@StudyListDetailActivity, Observer {
                                if (it != null) {
                                    if (it.isSuccess) {
                                        mViewModel.studyListDetailLiveData.value?.property?.show_count =
                                            mViewModel.studyListDetailLiveData.value?.property?.show_count
                                                ?: 0 + 1
                                        loge("currentShowCount: ${mViewModel.studyListDetailLiveData.value?.property?.show_count ?: 0}")
                                        //改变标题栏文案和图标
                                        setPublicStatus(true)
                                        //发送公开状态改变
                                        EventBus.getDefault()
                                            .post(StudyRoomResourceChange(StudyRoomResourceChange.TYPE_FOLODER))
                                    }
                                    toast(it.msg)
                                }
                            })

                    }

            }, if (response.status > 2) cancle else null))
            .show()
    }

    /**
     * 展示去掉公开弹窗
     */
    fun showCanclePublicDialog() {
        var realContent: CharSequence =
            if (mViewModel.studyListDetailLiveData.value?.property?.show_count ?: 0 > 5) {
                "是否设置学习清单为不公开？"
            } else {
                "学习清单设为不公开后，将扣除10积分"
            }

        if (realContent.contains(deductTenScoreStr))
            realContent = spannableString {
                str = realContent
                colorSpan {
                    start = realContent.indexOf(deductTenScoreStr)
                    end = realContent.indexOf(deductTenScoreStr) + deductTenScoreStr.length
                    color = Color.parseColor("#E17F0E")
                }
            }

        XPopup.Builder(this)
            .asCustom(CommonAlertPop(this, realContent, object : CommonAlertPop.Confirm {
                override val position: Int
                    get() = CommonAlertPop.Position.RIGHT

                override val text: String
                    get() = "确定"
                override val confirmBack: (() -> Unit)
                    get() = {
                        mViewModel.canclePublicStudyList(foldId)
                            .observe(this@StudyListDetailActivity, Observer {
                                if (it.isSuccess) {
                                    mViewModel.studyListDetailLiveData.value?.property?.show_count =
                                        mViewModel.studyListDetailLiveData.value?.property?.show_count
                                            ?: 0 - 1
                                    loge("currentShowCount: ${mViewModel.studyListDetailLiveData.value?.property?.show_count ?: 0}")
                                    setPublicStatus(false)
                                    //发送公开状态改变
                                    EventBus.getDefault()
                                        .post(StudyRoomResourceChange(StudyRoomResourceChange.TYPE_FOLODER))
                                }
                            })
                    }

            }, object : CommonAlertPop.Cancle {
                override val position: Int
                    get() = CommonAlertPop.Position.LEFT
                override val text: String
                    get() = "取消"
                override val cancleBack: (() -> Unit)?
                    get() = null
            }))
            .show()
    }

    /**
     * 展示分享弹窗
     */
    private fun showShareDialog(shareDetailModel: ShareDetailModel) {
        XPopup.Builder(this)
            .asCustom(CommonBottomSharePop(this, {
                val shareAddScore = ARouter.getInstance().navigation(
                    ShareSrevice::class.java
                )
                //todo 分享内容待完善
//                val targetUrl = shareDetailModel.share_link
//                val content =  "来自${GlobalsUserManager.userInfo?.name?:""}分享的学习清单，共XX个资源"
//                val imageRes = R.mipmap.studyroom_ic_collect_folder
//                val strTitle: String = foldName

                val targetUrl = shareDetailModel.share_link
                val content = shareDetailModel.share_desc
                val imageRes = shareDetailModel.share_picture
                val strTitle: String = shareDetailModel.share_title

                val build = IShare.Builder()
                    .setSite(it)
                    .setTitle(strTitle)
                    .setMessage(content)
                    .setWebUrl(targetUrl)
                    .setImageUrl(imageRes)
                    .build()
                //分享后获取积分
                shareAddScore.share(this, build) { shareStatus ->
                    if (shareStatus == 1) {
                        toast("分享成功")
                    }
                }
            }))
            .show()
    }

    /**
     * 初始化底部分类适配器
     */
    private fun initBottomFragmentViewPager() {
        val arrayList = ArrayList<String>();
        tabArray.forEachIndexed { index, s ->
            arrayList.add(s.name ?: "")
        }

        inflater.viewPager2.adapter = bottomFragmentAdapter
        inflater.mctStudyList.setUpWithViewPage2(
            inflater.viewPager2,
            arrayList
        )
    }


    /**
     * 初始化清单列表适配器
     */
    private fun initStudyListAdapter() {

        inflater.rvStudyList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        studyListAdapter.addHeaderView(StudyListAdapter.createAddHeadView(this) {
            if (!GlobalsUserManager.isLogin()) {     //检测登录
                ResourceTurnManager.turnToLogin()
                return@createAddHeadView
            }
            //公开状态下不可编辑
            if (mViewModel.getStudyListDetail().value?.property?.is_show == true) {
                toast(resources.getString(R.string.text_no_publicate_tip))
                return@createAddHeadView
            }
            //点击添加学习清单文件夹
            val createStudyListPop = CreateStudyListPop(this)
            createStudyListPop.onConfirmCallBack = {
                if (!isChildPubFolder()) {
                    //发送创建文件夹请求
                    mViewModel.createNewStudyFolder(it, foldId, createStudyListPop)
                }
            }
            XPopup.Builder(this)
                .autoOpenSoftInput(true)
                .asCustom(createStudyListPop)
                .show()
        }, orientation = LinearLayout.HORIZONTAL)

        studyListAdapter.setOnItemClickListener { adapter, view, position ->
//            val get = mViewModel.getListLiveData().value?.get(position)

            val get = (adapter.data as ArrayList<FolderItem>).get(position)
            //点击进入学习清单页面
            ARouter.getInstance().build(Paths.PAGE_STUDYLIST_DETAIL)
                .with(
                    Bundle().put(IntentParamsConstants.STUDYROOM_FOLDER_ID, get?.id)
                        .put(IntentParamsConstants.STUDYROOM_FOLDER_NAME, get?.name)
                )
                .navigation()
        }

        studyListAdapter.setOnItemLongClickListener { adapter, view, position ->
            //公开状态下不可编辑
            if (mViewModel.getStudyListDetail().value?.property?.is_show == true) {
                toast(resources.getString(R.string.text_no_publicate_tip))
                return@setOnItemLongClickListener true
            }


            val folderItem = adapter.data.get(position) as FolderItem
            showFolderEditPop(view, folderItem)
            return@setOnItemLongClickListener true
        }
        inflater.rvStudyList.adapter = studyListAdapter
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
        XPopup.Builder(this)
            .asCustom(FolderDeletePop(this, folderItem.name) {
                mViewModel.deleteFolder(folderItem.id)
            })
            .show()
    }

    /**
     * 显示文件夹创建弹窗，和重命名复用
     * @param folderItem 文件夹bean，如果不为空，则代表是需要重命名
     */
    private fun showFolderAddPop(folderItem: FolderItem? = null) {
        val createStudyListPop = CreateStudyListPop(this, folderItem)
        createStudyListPop.onConfirmCallBack = {
            if (folderItem != null) {
                //发送重命名文件夹请求
                mViewModel.renameFolder(folderItem.id, foldId, it, createStudyListPop)
            } else {
                //发送创建文件夹请求
                mViewModel.createNewStudyFolder(it, pop = createStudyListPop)
            }

        }
        XPopup.Builder(this)
            .autoOpenSoftInput(true)
            .asCustom(createStudyListPop)
            .show()
    }

    /***
     * 文件夹进行移动删除等操作事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFolderChangeEvent(resourceChange: StudyRoomResourceChange) {
        loge(this, "收到了学习清单文件夹改变事件 type${resourceChange}")
        if (resourceChange.moveType == StudyRoomResourceChange.TYPE_FOLODER) {
//            mViewModel.getChildFolder(foldId)
            mViewModel.getChildFolderNew(foldId)
        } else if (resourceChange.moveType == StudyRoomResourceChange.TYPE_RESOURCE) {
            refreshBottomFolderList()
        }
    }

    /***
     * 有资源添加到了学习室，刷新当前列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResourceChangeEvent(resourceChange: RefreshStudyRoomEvent) {
        //刷新下当前fragment
        var refreshItemIndex = inflater.viewPager2.currentItem

        //如果是其他资源类型，type=999
        var tempType = resourceChange.resourceType
        if (bottomFragmentAdapter?.lever2?.contains(tempType) == true) {
            tempType = ResourceTypeConstans.TYPE_ROOM_TAB_OTHER
        }

        tabArray.forEachIndexed { index, folderTab ->
            if (folderTab.type == tempType) {
                refreshItemIndex = index
            }
        }
        bottomFragmentAdapter?.refreshCurrentData(supportFragmentManager, refreshItemIndex)
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }
}