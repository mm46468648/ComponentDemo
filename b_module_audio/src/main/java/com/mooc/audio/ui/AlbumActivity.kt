package com.mooc.audio.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.loadmore.LoadMoreStatus
import com.chad.library.adapter.base.module.BaseLoadMoreModule
import com.lxj.xpopup.XPopup
import com.mooc.audio.R
import com.mooc.audio.databinding.ActivityAlbumBinding
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.audio.ui.adapter.AlbumListAdapter
import com.mooc.audio.viewmodel.AlbumViewModel
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.*
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.commonbusiness.utils.share.ShareSchoolUtil
import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.*
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.model.eventbus.AlbumRefreshEvent
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.pop.CommonMenuPopupW
import com.mooc.newdowload.DownloadInfo
import com.mooc.newdowload.DownloadManager
import com.mooc.newdowload.State
import com.mooc.resource.widget.CommonTitleDataBinding
import com.mooc.resource.widget.EmptyView
import com.mooc.statistics.LogUtil
//import kotlinx.android.synthetic.main.activity_album.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.util.*


/**
 * 音频课页面
 */
@Route(path = Paths.PAGE_ALBUM)
class AlbumActivity : BaseActivity() {

    val albumId by extraDelegate(IntentParamsConstants.ALBUM_PARAMS_ID, "")

    val mViewModel: AlbumViewModel by lazy {
//        ViewModelProviders.of(this, BaseViewModelFactory(albumId)).get(AlbumViewModel::class.java)
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return AlbumViewModel(albumId) as T
            }
        }).get(AlbumViewModel::class.java)
    }

    var albumListAdapter: AlbumListAdapter? = null

    private lateinit var inflater: ActivityAlbumBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivityAlbumBinding.inflate(layoutInflater)

        setContentView(inflater.root)
        initView()
        initAdapter()

        //头部数据返回监听
        mViewModel.albumDetailLiveDta.observe(this, Observer {
            initHeadDetail(it)
        })

        //列表加载监听
        mViewModel.albumListLiveData.observe(this, Observer { arr->
//            swiperefreshLayout.isRefreshing = false
            albumListAdapter?.notifyDataSetChanged()
        })

        //加载状态监听
        mViewModel.pageState.observe(this, Observer {
            setLoadState(it)
        })
        //学习室和分享数据
        mViewModel.resourceShareDetaildata.observe(this, Observer {
            if ("0" == it.is_enroll) {
                inflater.commonTitle.setRightSecondIconRes(R.mipmap.common_ic_title_right_add)
            } else {
                inflater.commonTitle.setRightSecondIconRes(R.mipmap.common_ic_title_right_added)
                inflater.commonTitle.ib_right_second?.isEnabled = false
            }
        })

        //初始化数据
//        loadData()
        inflater.swiperefreshLayout.isRefreshing = true
        mViewModel.loadAlbumInfo("") {
            loge(it.toString())
            inflater.swiperefreshLayout.isRefreshing = false
        }

        EventBus.getDefault().register(this)

    }

    override fun onResume() {
        super.onResume()

        registChangeAudioPosition()

    }


    /**
     * 注册上次在听
     */
    private fun registChangeAudioPosition() {
        LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_LISTEN_TRACK_ID)
            .observerSticky(this, Observer<String> {
                loge(" ${this}接收到需要定位的音频id ${it}")
                albumListAdapter?.lastListenId = it
                //并且定位到此位置
                scrollToPosition(it)
            }, true)
    }

    private fun scrollToPosition(it: String) {
        albumListAdapter?.data?.forEachIndexed { index, trackModel ->
            if (trackModel.id == it) {
                inflater.rvAudioList.scrollToPosition(index)
            }
        }
    }

    /**
     * 初始化View
     */
    private fun initView() {

        //下拉刷新
        inflater.swiperefreshLayout.setOnRefreshListener {
            mViewModel.loadBefore()
        }
        //点击加入学习室
        inflater.commonTitle.setOnSecondRightIconClickListener {
            addToStudyRoom()
        }
        //设置返回健
        inflater.commonTitle.setOnLeftClickListener {
            finish()
        }
        //点击菜单
//        val watchView = XPopup.Builder(this).watchView(commonTitle.ib_right)
        inflater.commonTitle.setOnRightIconClickListener(View.OnClickListener {
            showMenuPop()
        })

        //点击举报
        CommonTitleDataBinding.reportInfo(inflater.commonTitle, albumId, ResourceTypeConstans.TYPE_ALBUM, "")
        //解决局部刷新闪动问题
        (inflater.rvAudioList.getItemAnimator() as DefaultItemAnimator).supportsChangeAnimations = false
        //正序，倒序加载
        inflater.tvLoadSort.setOnClickListener {
            val sort = if (mViewModel.sort == "1") "0" else "1"

            setSortStr(sort)
            inflater.swiperefreshLayout.isRefreshing = true

            //重置加载更多状态
            try {
                val isEnableLoadMore =
                    BaseLoadMoreModule::class.java.getDeclaredField("isEnableLoadMore")
                isEnableLoadMore.isAccessible = true
                isEnableLoadMore.setBoolean(albumListAdapter?.loadMoreModule, true)

                val loadMoreStatus =
                    BaseLoadMoreModule::class.java.getDeclaredField("loadMoreStatus")
                loadMoreStatus.isAccessible = true
                loadMoreStatus.set(albumListAdapter?.loadMoreModule, LoadMoreStatus.Complete)

            } catch (e: Exception) {
                loge(e.toString())
            }


            mViewModel.loadAlbumInfo(sort) {
                loge(it.toString())
                inflater.swiperefreshLayout.isRefreshing = false
            }


        }

        //点击批量下载
        inflater.tvDownload.setOnClickListener {
            albumListAdapter?.let { //切换批量下载状态
                it.allDownloadMode = true
            }

            //显示退出
            inflater.tvAllExit.visibility = View.VISIBLE
            inflater.tvDownload.visibility = View.GONE
            inflater.tvAllSelect.visibility = View.VISIBLE
            inflater.tvLoadSort.visibility = View.GONE
        }

        //点击退出批量下载，重置所有状态
        inflater.tvAllExit.setOnClickListener {
            if (inflater.tvAllExit.text == "下载") {
                //点击下载，开始下载
                allDownload()
                //并重制所有状态，
                resetStr()
                return@setOnClickListener
            }
            //等于"退出的时候才能退出"
            resetStr()
        }


        inflater.tvAllSelect.setOnClickListener { view ->
            albumListAdapter?.let { //切换全选状态
                if (checkDownloadPositionList.size == it.data.size) {
                    //全选的时候点击，改为全不选
                    checkDownloadPositionList.clear()
                    inflater.tvAllSelect.setDrawLeft(R.mipmap.audio_ic_all_unselected, 0.5f.dp2px())
                } else {
                    //非全选的时候点击，改为全选
                    checkDownloadPositionList.clear()
                    checkDownloadPositionList.addAll(it.data)
                    inflater.tvAllSelect.setDrawLeft(R.mipmap.audio_ic_all_select, 0.5f.dp2px())
                }
                it.reverseList = checkDownloadPositionList
                setHeadUi()

            }
        }

    }

    fun setHeadUi() {
        val haveCheck = checkDownloadPositionList.isNotEmpty()
        val allExitStr = if (haveCheck) "下载" else "退出"
        inflater.tvAllExit.text = allExitStr
    }

    private fun showMenuPop() {
        val setItems = arrayListOf("分享")
        if(mViewModel.albumDetailLiveDta.value?.has_permission == "1"){
            setItems.add("屏蔽")
        }
        val commonTitleMenuPop = CommonMenuPopupW(this, setItems, inflater.commonTitle)
        commonTitleMenuPop.onTypeSelect = {
            if (it == "分享") {
                showSharePop()
            }else if(it == "屏蔽"){
                mViewModel.postSheildAblum()
            }
        }
        commonTitleMenuPop.show()
    }

    /**
     * 点击分享点击事件
     */
    val onShareItemClick: (platForm: Int, shareDetail: ShareDetailModel) -> Unit =
        { platForm, shareDetail ->
            val targetUrl: String =
                UrlConstants.ALBUM_SHARE_URL + albumId + UrlConstants.SHARE_FOOT + UrlConstants.SHARE_FOOT_MASTER + "&resource_type=" + ResourceTypeConstans.TYPE_ALBUM.toString() + "&resource_id=" + albumId
            if (platForm == CommonBottomSharePop.SHARE_TYPE_SCHOOL) {
                //分享到学友圈
                ShareSchoolUtil.postSchoolShare(
                    this,
                    ResourceTypeConstans.TYPE_ALBUM.toString(),
                    albumId,
                    mViewModel.albumDetailLiveDta.value?.cover_url_small
                )
            } else {
                val shareAddScore = ARouter.getInstance().navigation(ShareSrevice::class.java)
                shareAddScore.shareAddScore(
                    ShareTypeConstants.TYPE_ALBUM,
                    this, IShare.Builder()
                        .setSite(platForm)
                        .setWebUrl(shareDetail.weixin_url)
                        .setTitle(shareDetail.share_title)
                        .setMessage(shareDetail.share_desc)
                        .setImageUrl(shareDetail.share_picture)
                        .build()
                )
            }
        }

    private fun showSharePop() {
//        toast("展示分享弹窗")
        var chooseBack: ((platform: Int) -> Unit)? = null
        if (mViewModel.resourceShareDetaildata.value != null) {
            chooseBack = { platform ->
                onShareItemClick.invoke(platform, mViewModel.resourceShareDetaildata.value!!)
            }
        } else {
            mViewModel.getShareInfo().observe(this, Observer {
                chooseBack = { platform ->
                    onShareItemClick.invoke(platform, mViewModel.resourceShareDetaildata.value!!)
                }
            })
        }
        val commonBottomSharePop = CommonBottomSharePop(this, chooseBack, false)
        XPopup.Builder(this)
            .asCustom(commonBottomSharePop)
            .show()


    }


    /**
     * 重置
     */
    private fun resetStr() {
        albumListAdapter?.let { //切换批量下载状态
            it.allDownloadMode = false
            albumListAdapter?.allSelect = false
        }
        inflater.tvAllExit.setText("退出")
        inflater.tvAllExit.visibility = View.GONE
        inflater.tvDownload.visibility = View.VISIBLE
        inflater.tvAllSelect.setDrawLeft(R.mipmap.audio_ic_all_unselected)
        inflater.tvAllSelect.visibility = View.GONE
        inflater.tvLoadSort.visibility = View.VISIBLE
        checkDownloadPositionList.clear()

    }

    /**
     * 将选中的下载列表
     * 批量下载
     */
    private fun allDownload() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), PermissionApplyActivity.REQUEST_CODE_DEFAULT
            )
            return
        }

        checkDownloadPositionList.forEach {
            val get = DownloadManager.DOWNLOAD_INFO_HASHMAP.get(it.generateDownloadDBId())
            if (get != null && get.state != State.DOWNLOADING && get.state != State.DOWNLOAD_COMPLETED) {
                download(it)
            }
        }

    }


    //批量下载选中的要下载的音频的信息
    var checkDownloadPositionList = mutableListOf<TrackBean>()


    /**
     * 初始化适配器
     */
    private fun initAdapter() {
        val dataList = mViewModel.albumListLiveData.value
        dataList?.let {     //初始化Adapter
            albumListAdapter = AlbumListAdapter(it)
            albumListAdapter?.reverseList = checkDownloadPositionList
            inflater.rvAudioList.layoutManager = LinearLayoutManager(this)
            inflater.rvAudioList.adapter = albumListAdapter




            albumListAdapter?.setOnItemClickListener { adapter, view, position ->
                if (albumListAdapter?.allDownloadMode == true) {     //如果是批量下载状态
                    //如果已下载,或下载中，（也就是文字不为空）则返回
                    if (view.findViewById<TextView>(R.id.tvHasDown).text.isNotEmpty()) return@setOnItemClickListener
                    //点击切换选中状态

                    val element = dataList[position]
                    if (checkDownloadPositionList.contains(element)) {
                        checkDownloadPositionList.remove(element)
                    } else {
                        checkDownloadPositionList.add(element)
                    }
                    albumListAdapter?.reverseList = checkDownloadPositionList
                    setHeadUi()
                } else {   //正常状态进入播放页面
                    val trackBean = it[position]

                    //当前位置对应的 每页100条的页码
                    val currentPostionPage = (mViewModel.beforePageOffset * 10 + position) / 100 + 1

                    loge("想要播放:${ trackBean.id}")
                    val bundle = Bundle().put(IntentParamsConstants.AUDIO_PARAMS_ID, trackBean.id)
                        .put(IntentParamsConstants.ALBUM_PARAMS_ID, albumId)
                        .put(IntentParamsConstants.ALBUM_PARAMS_LIST_SORT, mViewModel.sort)
                        .put(XimaAudioActivity.PARAM_INIT_LOAD_PAGE, currentPostionPage)
                        .put(XimaAudioActivity.PARAM_TOTAL_LOAD_PAGE, mViewModel.total_page)
                        .put(XimaAudioActivity.PARAM_SIGNLE_AUDIO, false)

                    val routeFromAudio =
                        intent.getBooleanExtra(XimaAudioActivity.ROUTE_FROM_AUDIO, false)
                    if (routeFromAudio) {    //音频跳转过来，选中携带数据并关闭
                        LiveDataBus.get().hashMap.remove(LiveDataBusEventConstants.EVENT_LISTEN_TRACK_ID)
                        val intent = Intent()
                        intent.putExtras(bundle)
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {

                        LogUtil.addClickLogNew(
                            LogEventConstants2.T_ALBUM,
                            trackBean._resourceId,
                            "${ResourceTypeConstans.TYPE_ALBUM}",
                            trackBean.track_title,
                            "${LogEventConstants2.typeLogPointMap[trackBean._resourceType]}#${trackBean._resourceId}"
                        )
                        ARouter.getInstance().build(Paths.PAGE_AUDIO_PLAY)
                            .with(bundle)
                            .navigation()
                    }

                }
            }

            albumListAdapter?.addChildClickViewIds(R.id.ivDownloadState, R.id.ivAdd)
            albumListAdapter?.setOnItemChildClickListener { baseQuickAdapter, view, i ->
                //点击下载事件
                if (view.id == R.id.ivDownloadState) {
                    download(dataList[i])
                }
                //点击+号单独将音频添加到学习室
                if (view.id == R.id.ivAdd) {
                    addAudioToStudyRoom(dataList[i], i)
                }
            }
            albumListAdapter?.loadMoreModule?.setOnLoadMoreListener {
//                loadData()
                mViewModel.loadMore()
            }
        }
    }

    fun download(trackModel: TrackBean) {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), PermissionApplyActivity.REQUEST_CODE_DEFAULT
            )
            return
        }
        //1.先从缓存中获取
        var downLoadInfo: DownloadInfo? =
            DownloadManager.DOWNLOAD_INFO_HASHMAP.get(trackModel.generateDownloadDBId())
        if (downLoadInfo == null) {
            downLoadInfo = createDownloadInfo(trackModel)
            DownloadManager.DOWNLOAD_INFO_HASHMAP.put(downLoadInfo.id, downLoadInfo)
            // 第一次展示,初始化处理,加载本地数据，用于显示用户以前的操作
            handleDownload(trackModel)
        } else {
            loge(downLoadInfo.toString())
            // 第二次展示及以后，用于更新
            handleDownload(trackModel)
        }


    }

    private fun handleDownload(trackModel: TrackBean) {

        DownloadManager.getInstance().handleDownload(trackModel.generateDownloadDBId())
    }

    private fun createDownloadInfo(trackModel: TrackBean): DownloadInfo {
        val downloadInfo = DownloadInfo()
        downloadInfo.id = trackModel.generateDownloadDBId()
        downloadInfo.downloadPath = DownloadConfig.audioLocation +
                "/${albumId}"
        downloadInfo.downloadUrl = trackModel.play_url_32
        downloadInfo.state = State.DOWNLOAD_NOT
        downloadInfo.fileName = trackModel.track_title
        return downloadInfo
    }


    /**
     * 设置加载状态
     */
    private fun setLoadState(it: Int) {
        when (it) {
            LoadStateConstants.STATE_DATA_EMPTY -> {
                albumListAdapter?.setEmptyView(com.mooc.resource.widget.EmptyView(this))
            }
            LoadStateConstants.STATE_REFRESH_COMPLETE -> {
                inflater.swiperefreshLayout.isRefreshing = false
            }
            LoadStateConstants.STATE_CURRENT_COMPLETE -> {
                albumListAdapter?.loadMoreModule?.loadMoreComplete()
            }
            LoadStateConstants.STATE_ALL_COMPLETE -> {
                albumListAdapter?.loadMoreModule?.loadMoreEnd()
            }
            LoadStateConstants.STATE_ERROR -> {
                inflater.swiperefreshLayout.isRefreshing = false
                albumListAdapter?.loadMoreModule?.loadMoreFail()
            }
        }
    }

    private fun initHeadDetail(it: AlbumListResponse) {
        inflater.tvAlbumTitle.text = it.album_title
        inflater.tvPlayCount.text = "播放: ${StringFormatUtil.formatPlayCount(it.play_count)}次"
        inflater.tvAudioCount.text = "音频集数${it.include_track_count}集"
        inflater.tvVoicer.text = "${it.announcer?.nickname}"
        Glide.with(inflater.ivCover).load(it.cover_url_large).into(inflater.ivCover)

        setSortStr(it.params?.sort ?: "1")
    }

    /**
     * 设置查询顺序
     */
    fun setSortStr(sort: String) {
        val tvLoasSortDrawLeft =
            if (sort == "1") R.mipmap.audio_ic_album_sort_up else R.mipmap.audio_ic_album_sort_down
        val tvLoasSortStr = if (sort == "1") "正序" else "倒序"
        inflater.tvLoadSort.setDrawLeft(tvLoasSortDrawLeft)
        inflater.tvLoadSort.text = tvLoasSortStr
    }

    /**
     * 音频课加入学习室
     */
    private fun addToStudyRoom() {
        val jsonObject = JSONObject()
        jsonObject.put("url", albumId)
        jsonObject.put("resource_type", ResourceTypeConstans.TYPE_ALBUM)
        jsonObject.put("other_resource_id", albumId)

        //显示加入学习室弹窗
        val studyRoomService: StudyRoomService? =
            ARouter.getInstance().navigation(StudyRoomService::class.java)
        studyRoomService?.showAddToStudyRoomPop(this, jsonObject) {
            if (it) {
                //更新加入学习室状态
                inflater.commonTitle.setRightSecondIconRes(R.mipmap.common_ic_title_right_added)
                inflater.commonTitle.ib_right_second?.isEnabled = false
            }

        }
    }


    /**
     * 音频课加入学习室
     */
    private fun addAudioToStudyRoom(trackBean: TrackBean, i: Int) {
        if (trackBean.enrolled) return

        val jsonObject = JSONObject()
        jsonObject.put("url", trackBean.id)
        jsonObject.put("resource_type", ResourceTypeConstans.TYPE_TRACK)
        jsonObject.put("other_resource_id", trackBean.id)

        //显示加入学习室弹窗
        val studyRoomService: StudyRoomService? =
            ARouter.getInstance().navigation(StudyRoomService::class.java)
        studyRoomService?.showAddToStudyRoomPop(this, jsonObject) {
            if (it) {
                //更新加入学习室状态
                trackBean.enrolled = true
                albumListAdapter?.notifyItemChanged(i)
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRemoveEvent(event:AlbumRefreshEvent){
        albumListAdapter?.data?.forEach {
            if(!TextUtils.isEmpty(event.trackId) && event.trackId == it.id){
                albumListAdapter?.remove(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
        //终止所有下载，移除缓存
        DownloadManager.getInstance().clearAllTask()
    }
}
