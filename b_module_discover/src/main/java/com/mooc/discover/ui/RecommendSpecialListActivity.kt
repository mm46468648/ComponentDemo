package com.mooc.discover.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.*
import com.mooc.common.utils.NetUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.decoration.GrideItemDecoration
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.manager.ResourceUtil
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.share.ShareSchoolUtil
import com.mooc.discover.R
import com.mooc.discover.adapter.RecomSpecialListAdapter2
import com.mooc.discover.adapter.SpecialDefaultAdapter
import com.mooc.discover.adapter.special.SpecialMode1Adapter
import com.mooc.discover.adapter.special.SpecialMode2Adapter
import com.mooc.discover.adapter.special.SpecialMode5Adapter
import com.mooc.discover.databinding.ActivityRecommendListBinding
import com.mooc.discover.model.MenuBean
import com.mooc.discover.model.RecommendContentBean
import com.mooc.discover.model.RecommendResTypeBean
import com.mooc.discover.viewmodel.RecommendSpecialViewModel
import com.mooc.discover.window.CollumnMenuPopW
import com.mooc.statistics.LogUtil

/**
 * 专题页面
 * 类似专栏样式，可选择排序方式
 * @author limeng
 * @date 2020/11/18
 */
@Route(path = Paths.PAGE_RECOMMEND_SPECIAL)
class RecommendSpecialListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    OnLoadMoreListener {
    var resId: String = ""

    //    var mRecomSpecialListAdapter: RecomSpecialListAdapter2? = null
    var mRecomSpecialListAdapter: BaseQuickAdapter<RecommendContentBean.DataBean, BaseViewHolder>? =
        null
    var collumnMenuPopW: CollumnMenuPopW? = null
    var typeMenus = ArrayList<MenuBean>()
    var sortMenus = ArrayList<MenuBean>()
    var leftChecked = false
    var rightChecked = false
    val mViewModel: RecommendSpecialViewModel by viewModels()
    var currentType = "0"
    var page = 1
    var page_size = 20
    var sort = ""
    private lateinit var inflater: ActivityRecommendListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivityRecommendListBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initData()
        initDataListener()

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        getType()
    }

    private fun initData() {
        resId = intent.getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_ID)?:""

        val inflate = View.inflate(this, R.layout.discover_special_title_layout, null)
        inflater.commonTitle.addExtraLayout(inflate)

        getData()
        setSortList()
        getShareInfo(resId.toString())
    }

    private fun initView() {
        collumnMenuPopW = CollumnMenuPopW(this, inflater.llMenu) {
            inflater.tvMenuType.setDrawRight(R.mipmap.column_ic_category_down_gray)
            inflater.tvMenuSort.setDrawRight(R.mipmap.column_ic_category_down_gray)
        }
    }

    private fun initDataListener() {
        mViewModel.mRecommendContentBean.observe(this, Observer { it ->
            inflater.swipeLayoutRecommend.isRefreshing = false
            val tag = it.list_tag
//            inflater.commonTitle.middle_text = it.title
            inflater.commonTitle.findViewById<TextView>(R.id.tvTitleLeft).text = it.title
            if (page == 1) {
                while (inflater.rcvRecommendList.itemDecorationCount > 0) {
                    inflater.rcvRecommendList.removeItemDecorationAt(0);
                }
                inflater.rcvRecommendList.setPadding(15.dp2px(), 0, 15.dp2px(), 0)
                if (tag == 8) {   //列表模板样式九单独使用一个适配器
                    mRecomSpecialListAdapter = SpecialDefaultAdapter(null)
                    mRecomSpecialListAdapter?.setEmptyView(com.mooc.resource.widget.EmptyView(this))
                    mRecomSpecialListAdapter?.loadMoreModule?.setOnLoadMoreListener(this)
                    val layoutManager = LinearLayoutManager(this)
                    inflater.rcvRecommendList.setLayoutManager(layoutManager)
                    inflater.rcvRecommendList.adapter = mRecomSpecialListAdapter
                    initListener()
                } else if(tag == 0){
                    mRecomSpecialListAdapter = SpecialMode1Adapter(null)
                    mRecomSpecialListAdapter?.setEmptyView(com.mooc.resource.widget.EmptyView(this))
                    mRecomSpecialListAdapter?.loadMoreModule?.setOnLoadMoreListener(this)
                    val layoutManager = LinearLayoutManager(this)
                    inflater.rcvRecommendList.setLayoutManager(layoutManager)
                    inflater.rcvRecommendList.adapter = mRecomSpecialListAdapter
                    initListener()
                }else if(tag == 1){
                    mRecomSpecialListAdapter = SpecialMode2Adapter(null)
                    mRecomSpecialListAdapter?.setEmptyView(com.mooc.resource.widget.EmptyView(this))
                    mRecomSpecialListAdapter?.loadMoreModule?.setOnLoadMoreListener(this)
                    val layoutManager = LinearLayoutManager(this)
                    inflater.rcvRecommendList.setLayoutManager(layoutManager)
                    inflater.rcvRecommendList.adapter = mRecomSpecialListAdapter
                    initListener()
                }else if(tag == 4){
                    mRecomSpecialListAdapter = SpecialMode5Adapter(null)
                    mRecomSpecialListAdapter?.setEmptyView(com.mooc.resource.widget.EmptyView(this))
                    mRecomSpecialListAdapter?.loadMoreModule?.setOnLoadMoreListener(this)

                    val mGridLayoutManager =
                        GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
                    inflater.rcvRecommendList.layoutManager = mGridLayoutManager
                    val space = 15f.dp2px()
                    inflater.rcvRecommendList.addItemDecoration(GrideItemDecoration(space))
                    inflater.rcvRecommendList.adapter = mRecomSpecialListAdapter
                    initListener()
                }else {
                    mRecomSpecialListAdapter = RecomSpecialListAdapter2(null)
                    when (tag) {
                        7 -> {
                            val mGridLayoutManager =
                                GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
                            inflater.rcvRecommendList.layoutManager = mGridLayoutManager
                            val space = 15f.dp2px()
                            inflater.rcvRecommendList.setPadding(space, 10.dp2px(), 0, 0)
//
//                            while (inflater.rcvRecommendList.itemDecorationCount > 0) {
//                                inflater.rcvRecommendList.removeItemDecorationAt(0);
//                            }
                            inflater.rcvRecommendList.addItemDecoration(GrideItemDecoration(space))
                        }
                        6 -> {
                            val mGridLayoutManager =
                                GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
                            inflater.rcvRecommendList.layoutManager = mGridLayoutManager
                            val space = 15f.dp2px()
                            inflater.rcvRecommendList.setPadding(space, 10.dp2px(), 0, 0)

//                            while (inflater.rcvRecommendList.itemDecorationCount > 0) {
//                                inflater.rcvRecommendList.removeItemDecorationAt(0);
//                            }
                            inflater.rcvRecommendList.addItemDecoration(GrideItemDecoration(space))
                        }
                        else -> {
                            val layoutManager = LinearLayoutManager(this)
                            inflater.rcvRecommendList.setLayoutManager(layoutManager)
                        }
                    }

                    inflater.rcvRecommendList.adapter = mRecomSpecialListAdapter
                    mRecomSpecialListAdapter?.setEmptyView(com.mooc.resource.widget.EmptyView(this))
                    mRecomSpecialListAdapter?.loadMoreModule?.setOnLoadMoreListener(this)
                    initListener()
                }
            }
            if (it != null) {
                if (it.data != null && it.data.size > 0) {
                    it.data.forEach {
                        it.classType = tag
                    }
                    setData(it.data)
                } else {
                    mRecomSpecialListAdapter?.loadMoreModule?.isEnableLoadMore = false
                    mRecomSpecialListAdapter?.loadMoreModule?.loadMoreEnd()
                }
            }


            //如果配置显示再显示筛选框
            if (it.filter_status == 1) {
                inflater.llMenu.visibility = View.VISIBLE
            }

            setSubscribeState(it.is_subscribe)

        })
        //右面点击pop弹框显示类型数据
        mViewModel.mRecommendResTypeBean.observe(this, Observer {
            if (it.recommend_types == null) {
                return@Observer
            }
            val typesBeans = it.recommend_types
            typeMenus.clear()
            if (typesBeans != null) {
                for (i in typesBeans.indices) {
                    val typeBean: RecommendResTypeBean.RecommendTypesBean = typesBeans[i]
                    val menuBean = MenuBean()
                    menuBean.name = typeBean.type_name
                    menuBean.type = typeBean.type_id
                    menuBean.isCheck = false
                    typeMenus.add(menuBean)
                }
            }

            val menuBeanAll = MenuBean()
            menuBeanAll.name = "全部"
            menuBeanAll.type = 0
            menuBeanAll.isCheck = true
            typeMenus.add(0, menuBeanAll)
            collumnMenuPopW?.setData(typeMenus)
        })
    }

    private fun initListener() {
        mRecomSpecialListAdapter?.setOnItemClickListener { adapter, view, position ->
            if (mRecomSpecialListAdapter?.data?.get(position) != null) {
                val bean = mRecomSpecialListAdapter?.data?.get(position)
                bean?.let {
                    LogUtil.addClickLogNew(
                        "${LogEventConstants2.P_SPECIAL}#${resId}",
                        bean._resourceId, "${bean._resourceType}",
                        mViewModel.mRecommendContentBean.value?.title ?: ""
                    )

                    ResourceTurnManager.turnToResourcePage(it)

                    ResourceUtil.updateResourceRead(it.id.toString())
                }


            }
        }
        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.swipeLayoutRecommend.setOnRefreshListener(this)
        inflater.llMenuType.setOnClickListener {
            if (typeMenus.size == 0) {
                getType()
                return@setOnClickListener
            }
            collumnMenuPopW?.isTypeMenu = true
            if (collumnMenuPopW?.isShowing == true) {

                collumnMenuPopW?.Dismiss()
            } else {
                if (typeMenus != null) {
                    collumnMenuPopW?.setData(typeMenus)
                    collumnMenuPopW?.show()
                    inflater.tvMenuType.setDrawRight(R.mipmap.column_ic_category_down_gray_up)
                }
            }

        }
        inflater.llMenuSort.setOnClickListener {
            collumnMenuPopW?.isTypeMenu = false
            if (collumnMenuPopW?.isShowing == true) {
                collumnMenuPopW?.Dismiss()
            } else {
                if (sortMenus != null) {
                    collumnMenuPopW?.setData(sortMenus)
                    collumnMenuPopW?.show()
                    inflater.tvMenuSort.setDrawRight(R.mipmap.column_ic_category_down_gray_up)
                }
            }

        }
        collumnMenuPopW?.onClickListener = { menuBean: MenuBean?, isTypeMenu: Boolean ->
            if (isTypeMenu) {
                leftChecked = true
                revertList(typeMenus)
                for (menu in typeMenus) {
                    if (menu.type == menuBean?.type) {
                        menu.isCheck = true
                    }
                }
                collumnMenuPopW?.isTypeMenu = true
                inflater.tvMenuType.setText(menuBean?.name)
                currentType = menuBean?.type.toString()
            } else {
                rightChecked = true
                revertList(sortMenus)
                for (menu in sortMenus) {
                    if (menu.type == menuBean?.type) {
                        menu.isCheck = true
                    }
                }
                inflater.tvMenuSort.setText(menuBean?.name)
                collumnMenuPopW?.isTypeMenu = false
                if (menuBean?.name.equals("最新")) {
                    sort = "latest_update_time"
                } else if (menuBean?.name.equals("默认")) {
                    sort = ""
                } else {
                    sort = "page_view"
                }
            }

            collumnMenuPopW?.Dismiss()
            page = 1
            getData()

        }


    }

    /**
     * 获取某个专栏下数据
     */
    fun getData() {

        val map = mapOf<String, String>(
            "page" to page.toString(),
            "page_size" to page_size.toString(), "type" to currentType, "ordering" to sort
        )

        mViewModel.getRecommendListData(resId, map)

    }

    fun setSubscribeState(b:Boolean){
        val tvSubscribe = inflater.commonTitle.findViewById<TextView>(R.id.tvSubscribe)
        if(b){
           tvSubscribe.setTextColor(Color.parseColor("#AAAAAA"))
           tvSubscribe.setDrawLeft(R.mipmap.discover_ic_subscribe_added,4)
        }else{
            tvSubscribe.setTextColor(Color.parseColor("#10955b"))
            tvSubscribe.setDrawLeft(R.mipmap.discover_ic_subscribe_add,4)
            tvSubscribe.setOnClickListener {
                mViewModel.postSubscribe(resId).observe(this@RecommendSpecialListActivity, Observer { b->
                    if(b){
                        setSubscribeState(b)
                        toast(resources.getString(R.string.subscribe_str_success))
                    }
                })
            }
        }
    }
    /**
     * 获取分享信息
     */
    fun getShareInfo(resourceId: String) {
        if (resourceId == "0" || resourceId.isEmpty()) return
        mViewModel.articleWebShareDetaildata.observe(this, Observer {
            //share_status  是否分享 0是 -1否
//            onClickShare(it)
            initShare(it)
        })
        mViewModel.getShareDetailData(ResourceTypeConstans.TYPE_SPECIAL.toString(), resourceId)
    }

    fun initShare(shareDetailModel: ShareDetailModel){
        val ivShare = inflater.commonTitle.findViewById<ImageView>(R.id.ivShare)
        if ("0" == shareDetailModel.share_status) {    //等于0的时候才能分享

            ivShare.visibility = View.VISIBLE
            ivShare.setOnClickListener(View.OnClickListener {
                val shareImageUrl =
                    if (shareDetailModel.share_picture.isNotEmpty()) shareDetailModel.share_picture else UrlConstants.SHARE_LOGO_URL
                //去分享
                val choose: (platform: Int) -> Unit = { platform ->
                    if (platform == CommonBottomSharePop.SHARE_TYPE_SCHOOL) {
                        // 分享到学友圈
                        ShareSchoolUtil.postSchoolShare(
                            this,
                            ResourceTypeConstans.TYPE_SPECIAL.toString(),
                            resId.toString(),
                            shareImageUrl
                        )
                    } else {
                        val shareService =
                            ARouter.getInstance().navigation(ShareSrevice::class.java)
                        shareService.share(
                            this, IShare.Builder()
                                .setSite(platform)
                                .setTitle(shareDetailModel.share_title)
                                .setWebUrl(shareDetailModel.share_link)
                                .setMessage(shareDetailModel.share_desc)
                                .setImageUrl(shareImageUrl)
                                .build()
                        ) {
                            toast("分享成功")
                        }
                    }
                }
                XPopup.Builder(this)
                    .asCustom(CommonBottomSharePop(this, choose, false))
                    .show()
            })

        }else{
            ivShare.visibility = View.GONE
        }
    }

    fun onClickShare(shareDetailModel: ShareDetailModel) {
        if ("0" == shareDetailModel.share_status) {    //等于0的时候才能分享
            inflater.commonTitle.setRightFirstIconRes(R.mipmap.common_ic_right_share_gray)
            inflater.commonTitle.setOnRightIconClickListener(View.OnClickListener {
                val shareImageUrl =
                    if (shareDetailModel.share_picture.isNotEmpty()) shareDetailModel.share_picture else UrlConstants.SHARE_LOGO_URL
                //去分享
                val choose: (platform: Int) -> Unit = { platform ->
                    if (platform == CommonBottomSharePop.SHARE_TYPE_SCHOOL) {
                        // 分享到学友圈
                        ShareSchoolUtil.postSchoolShare(
                            this,
                            ResourceTypeConstans.TYPE_SPECIAL.toString(),
                            resId.toString(),
                            shareImageUrl
                        )
                    } else {
                        val shareService =
                            ARouter.getInstance().navigation(ShareSrevice::class.java)
                        shareService.share(
                            this, IShare.Builder()
                                .setSite(platform)
                                .setTitle(shareDetailModel.share_title)
                                .setWebUrl(shareDetailModel.share_link)
                                .setMessage(shareDetailModel.share_desc)
                                .setImageUrl(shareImageUrl)
                                .build()
                        ) {
                            toast("分享成功")
                        }
                    }
                }
                XPopup.Builder(this)
                    .asCustom(CommonBottomSharePop(this, choose, false))
                    .show()
            })

        }
    }


    fun getType() {
        mViewModel.getRecommendResTypes(resId)
    }

    override fun onRefresh() {
        if (NetUtils.isNetworkConnected()) {
            page = 1
            getData()
        } else {
            inflater.swipeLayoutRecommend.setRefreshing(false)
            toast(resources.getString(R.string.net_error))

        }

    }

    override fun onLoadMore() {
        inflater.rcvRecommendList.postDelayed(Runnable {
            page += 1
            getData()
        }, 1000)
    }

    private fun setSortList() {
        sortMenus.clear()
        val defaultBean = MenuBean()
        defaultBean.isCheck = true
        defaultBean.name = "默认"
        defaultBean.type = -1
        sortMenus.add(defaultBean)
        val menuBean = MenuBean()
        menuBean.isCheck = false
        menuBean.name = "最新"
        menuBean.type = 0
        sortMenus.add(menuBean)
        val menuBean1 = MenuBean()
        menuBean1.isCheck = false
        menuBean1.name = "最热"
        menuBean1.type = 1
        sortMenus.add(menuBean1)
    }


    /**
     * 设置数据
     */
    fun setData(beans: ArrayList<RecommendContentBean.DataBean>) {
        if (beans.size > 0) {
            if (page == 1) {
                mRecomSpecialListAdapter?.loadMoreModule?.isEnableLoadMore = beans.size >= page_size
                mRecomSpecialListAdapter?.setNewInstance(beans)
                mRecomSpecialListAdapter?.loadMoreModule?.loadMoreComplete()
            } else {
                mRecomSpecialListAdapter?.addData(beans)
                if (beans.size < page_size) {
                    mRecomSpecialListAdapter?.loadMoreModule?.isEnableLoadMore = false
                    mRecomSpecialListAdapter?.loadMoreModule?.loadMoreEnd()
                } else {
                    mRecomSpecialListAdapter?.loadMoreModule?.isEnableLoadMore = true
                    mRecomSpecialListAdapter?.loadMoreModule?.loadMoreComplete()
                }
            }
        } else {
            if (beans.size < page_size) {
                mRecomSpecialListAdapter?.loadMoreModule?.isEnableLoadMore = false
                mRecomSpecialListAdapter?.loadMoreModule?.loadMoreEnd()
            } else {
                mRecomSpecialListAdapter?.loadMoreModule?.isEnableLoadMore = true
                mRecomSpecialListAdapter?.loadMoreModule?.loadMoreComplete()
            }
        }

    }

    fun revertList(list: List<MenuBean>?) {
        if (list == null) {
            return
        }
        for (i in list) {
            i.isCheck = false
        }
    }
}