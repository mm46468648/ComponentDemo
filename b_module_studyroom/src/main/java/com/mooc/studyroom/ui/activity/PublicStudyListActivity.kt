package com.mooc.studyroom.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.*
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.RoomTabBean
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.model.studyroom.Property
import com.mooc.commonbusiness.module.studyroom.FolderTabCollectFragmentAdapter
import com.mooc.commonbusiness.module.studyroom.StudyListAdapter
import com.mooc.commonbusiness.pop.CommonAlertPop
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityOtherStudylistBinding
import com.mooc.studyroom.viewmodel.PublicStudyListViewModel
//import kotlinx.android.synthetic.main.studyroom_activity_other_studylist.*



/**
 * 他人的学习清单详情页面
 */
@Route(path = Paths.PAGE_PUBLIC_STUDY_LIST)
class PublicStudyListActivity : BaseActivity() {

    //如果没有值代表从收藏进入，不需要传
    val userId by extraDelegate(IntentParamsConstants.MY_USER_ID, "")
    //是否是来自运营推荐
    val fromRecommend by extraDelegate(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND, false)
    //是否是从任务详情点击
    val fromTask by extraDelegate(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK, false)
    val fromTaskId by extraDelegate(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK_ID, "")

    val mViewModel: PublicStudyListViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory(userId))[PublicStudyListViewModel::class.java]
    }

    //学习清单适配器
    val studyListAdapter = StudyListAdapter(arrayListOf())

    var tabArray = mutableListOf<RoomTabBean>()

    //文件夹id,name
    val foldId by extraDelegate(IntentParamsConstants.STUDYROOM_FOLDER_ID, "")
    private val fromFoldId by extraDelegate(IntentParamsConstants.STUDYROOM_FROM_FOLDER_ID, "")
    val childFolderId by extraDelegate(IntentParamsConstants.STUDY_ROOM_CHILD_FOLDER_ID, "")
    val foldName by extraDelegate(IntentParamsConstants.STUDYROOM_FOLDER_NAME, "")

    //是否被收藏过
    var collected = false
    var prised = false


    var bottomFragmentAdapter: FolderTabCollectFragmentAdapter? = null;
    private lateinit var inflater: StudyroomActivityOtherStudylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (foldId.isEmpty()) {
            finish()
            return
        }
        inflater = StudyroomActivityOtherStudylistBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        prised = intent.getBooleanExtra(IntentParamsConstants.STUDYROOM_STUDYLIST_PRISED, false)
        initView()


        inflater.rvStudyList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        //自己的才添加创建文件夹按钮
        if (userId == GlobalsUserManager.uid) {
            studyListAdapter.addHeaderView(StudyListAdapter.createAddHeadView(this) {
                if (!GlobalsUserManager.isLogin()) {     //检测登录
                    ResourceTurnManager.turnToLogin()
                    return@createAddHeadView
                }

                if (userId != GlobalsUserManager.uid) {
                    toast(resources.getString(R.string.text_other_folder_tip))
                    return@createAddHeadView
                }

                //公开状态下不可编辑
                toast(resources.getString(R.string.text_no_publicate_tip))
                return@createAddHeadView

            }, orientation = LinearLayout.HORIZONTAL)
        }
        onFolderClick()

        inflater.rvStudyList.adapter = studyListAdapter
        //获取详情  包括folder
        if(fromRecommend){
            mViewModel.getRecommendDetail(foldId)
        }else{
            mViewModel.getPublicDetail(foldId)
        }

        val begin = System.currentTimeMillis();
        loge("begin: ${begin}")
        //显示点赞收藏
        mViewModel.getStudyListDetail().observe(this, Observer {
            if (it != null) {
                loge("duration: ${System.currentTimeMillis() - begin}")
                setPublicDetail(it.property)

                //运营推荐只显示收藏,并且未收藏状态下才显示,收藏成功后隐藏
                if(fromRecommend && !collected){
                    inflater.clBottom2.visibility = View.VISIBLE
                    inflater.tvCollect2.setOnClickListener {
                        mViewModel.collectStudyList(foldId) { b ->
                            if (b) {
                                collected = true
                                inflater.clBottom2.visibility = View.GONE
                            }
                        }
                    }
                }else{
                    if (userId.isNotEmpty() && userId != GlobalsUserManager.uid) {
                        inflater.clBottom.visibility = View.VISIBLE

                        //点赞或取消点赞
                        inflater.tvPrise.setOnClickListener {
                            val postCode = if (prised) 0 else 1
                            mViewModel.priseStudyList(foldId, postCode)
                            prised = !prised
                            updatePriseDrawable(prised)
                        }

                        inflater.tvCollect.setOnClickListener {
                            if (collected) {
                                showCollectedPop()
                                return@setOnClickListener
                            }
                            mViewModel.collectStudyList(foldId)
                            collected = true
                        }
                    }
                }

                val items = it.folder.folder.items
                studyListAdapter.setList(items)

                //空的不显示文件夹
                if (items == null || items.size == 0) {
                    inflater.rvStudyList.visibility = View.GONE
                } else {
                    inflater.rvStudyList.visibility = View.VISIBLE
                }
                //别人的子文件夹不显示底部
                if (isChildPubFolder()) {
                    inflater.clBottom.visibility = View.GONE
                    inflater.commonTitle.tv_right?.visibility = View.GONE
                }

            }

        })


        if(fromRecommend){
            mViewModel.getRecommendFolderTabs(foldId)
        }else{
            //获取tab菜单
            if (userId.isEmpty()) { //是自己收藏的
                mViewModel.getFolderTabs(foldId, "", "", "");
            } else {
                if (userId != GlobalsUserManager.uid) {   //他人公开的
                    mViewModel.getFolderTabs(foldId, "", userId, "");
                } else {     //自己公开的
                    mViewModel.getFolderTabs(foldId, userId, "", "");
                }
            }
        }



        mViewModel.folderTabs.observe(this
        ) {

            it.level1?.forEachIndexed { index, folderTab ->
                tabArray.add(index, RoomTabBean(folderTab.type, folderTab.name))
            }

            if (it.level2 != null && it.level2?.size ?: 0 > 0) {

                val leve2Tab = mutableListOf<Int>()
                it.level2?.forEachIndexed { index, folderTab -> leve2Tab.add(folderTab.type) }
                //有收藏
                it.level2_name?.let { it1 ->
                    tabArray.add(RoomTabBean(it.level2_type, it.level2_name))
//                    bottomFragmentAdapter = FolderTabCollectFragmentAdapter(this, foldId, tabArray, leve2Tab, userId,fromRecommend,fromTask)
                    bottomFragmentAdapter = FolderTabCollectFragmentAdapter(this, foldId, tabArray, leve2Tab, userId,fromRecommend,fromTaskId)
                    initBottomFragmentViewPager()
                }
            } else {
                if (tabArray.size == 0) {
                    tabArray.add(0, RoomTabBean(ResourceTypeConstans.TYPE_ROOM_TAB_EMPTY, ""))
                }
//                bottomFragmentAdapter = FolderTabCollectFragmentAdapter(this, foldId, tabArray, null, userId,fromRecommend,fromTask)
                bottomFragmentAdapter = FolderTabCollectFragmentAdapter(this, foldId, tabArray, null, userId,fromRecommend,fromTaskId)
                initBottomFragmentViewPager()

            }

            //就剩一个空页面不显示tab
            if (tabArray.size == 1 && (tabArray.get(0).name?.isEmpty() == true)) {
                inflater.mctStudyList.visibility = View.GONE
            }


        }

        showFolderResourceDel()
    }

    /**
     * 学习清单资源后台删除后，提示用户：您的学习清单中“XX”“XX”资源已失效
     */
    private fun showFolderResourceDel() {
        var id = ""

        if (fromFoldId.isNotEmpty()) {
            id = fromFoldId
        } else {
            if (foldId.isNotEmpty()) {
                id = foldId
            }
        }
        if (id.isEmpty()) {
            return
        }
        mViewModel.getFolderResourceDelAsync(id)
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

    private fun onFolderClick() {
        studyListAdapter.setOnItemClickListener(OnItemClickListener { adapter, view, position ->
            val get = adapter.data.get(position) as FolderItem

            ARouter.getInstance().build(Paths.PAGE_PUBLIC_STUDY_LIST)
                    .withString(IntentParamsConstants.STUDYROOM_FOLDER_ID, get.id)
                    .withString(IntentParamsConstants.STUDY_ROOM_CHILD_FOLDER_ID, get.folder_id)
                    .withString(IntentParamsConstants.STUDYROOM_FOLDER_NAME, get.name)
                    .withString(IntentParamsConstants.MY_USER_ID, userId)
                    .navigation()


        })

    }


    //是否是公开的子文件夹
    private fun isChildPubFolder(): Boolean {
        return mViewModel.getStudyListDetail().value?.property?.pid != 0
    }

    private fun setPublicDetail(property: Property) {
        prised = property.is_like
        collected = property.is_collect
        updatePriseDrawable(property.is_like)


        val inflate = View.inflate(this, R.layout.studyroom_layout_sepcial_title, null)
        val findViewById = inflate.findViewById<TextView>(R.id.tvTitleLeft)
        inflater.commonTitle.addExtraLayout(inflate)

        if(fromRecommend){
            findViewById.setText(property.folder_name)
            findViewById.setDrawRight(R.mipmap.studyroom_ic_from_recommend,5.dp2px())
        }else{
            findViewById.setText(foldName)
            findViewById.setDrawRight(null)
        }

    }

    fun showCollectedPop() {
        val dialogContent = "当前学习清单已被收藏，是否更新同步清单资源？"
        XPopup.Builder(this)
                .asCustom(CommonAlertPop(this, dialogContent, object : CommonAlertPop.Confirm {
                    override val position: Int
                        get() = CommonAlertPop.Position.RIGHT

                    override val text: String
                        get() = "确定"
                    override val confirmBack: (() -> Unit)?
                        get() = {
                            mViewModel.collectStudyList(foldId)
                        }

                }, object : CommonAlertPop.Cancle {
                    override val position: Int
                        get() = CommonAlertPop.Position.LEFT

                    override val text: String
                        get() = "取消"
                    override val cancleBack: (() -> Unit)? = null

                }))
                .show()
    }

    /**
     * 初始化控件
     */
    private fun initView() {
//        if(foldName.isNotEmpty() && fromRecommend){
//            commonTitle.middle_text = foldName
//        }

        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.commonTitle.tv_right?.visibility = View.GONE

    }

    fun updatePriseDrawable(priseState: Boolean) {
        val drawableLeftRes = if (priseState) R.mipmap.studyroom_ic_studylist_prise else R.mipmap.studyroom_ic_studylist_unprise
        inflater.tvPrise.setDrawLeft(drawableLeftRes)
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
        inflater.mctStudyList.setUpWithViewPage2(inflater.viewPager2, arrayList)
    }



}