package com.mooc.audio.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.mooc.audio.R
import com.mooc.audio.databinding.ActivityAudioPlayBinding
import com.mooc.audio.manager.*
import com.mooc.audio.presenter.XimaAudioBusinessPresenter
import com.mooc.audio.viewmodel.AudioPlayNewModel
import com.mooc.common.ktextends.*
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.constants.*
import com.mooc.commonbusiness.interfacewrapper.ARouterNavigationCallbackWrapper
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.model.studyproject.MusicBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.commonbusiness.utils.incpoints.AddPointManager
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException


/**
 * 新版音频播放页面
 * 使用新的架构模式
 * XimaUtil只处理播放逻辑
 */
@Route(path = Paths.PAGE_AUDIO_PLAY)
open class AudioActivity : BaseActivity(), XiMaUtile.StatusListener {

    companion object {
        const val PARAM_INIT_LOAD_PAGE = "param_init_load_page" //初始加载位置 (准确的说是下次加载位置)
        const val PARAM_TOTAL_LOAD_PAGE = "param_total_load_page" //总页码
        const val PARAM_LOAD_SORT = "param_load_sort" //排序
        const val PARAM_SIGNLE_AUDIO = "param_signle_audio" //单条音频（播完即停）

        const val STR_BEGIN_TIME = "00:00" //初始时间
        const val REQUSET_CODE_ALBUM = 0 //跳转音频课请求码
        const val ROUTE_FROM_AUDIO = "route_from_audio" //从音频跳转到音频课的请求
    }


    val mPresenter: XimaAudioBusinessPresenter by lazy {
        val audioBusinessPresenter = XimaAudioBusinessPresenter(this)
        //计时回调
        audioBusinessPresenter.onTimeCountCallback = {
            showTimeCount(it)
        }

        //获取分享内容
        audioBusinessPresenter.getShareContent = {
            mViewModel.getShareDetail()
        }

        //下载监听
        audioBusinessPresenter.downloadClick = {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), PermissionApplyActivity.REQUEST_CODE_DEFAULT
                )
            } else {
                mViewModel.donwloadTrack()
            }

        }
        audioBusinessPresenter
    }


    val mViewModel: AudioPlayNewModel by viewModels()

//    private var mPlayerManager: XmPlayerManager? = null


    var xiMaUtile: XiMaUtile? = null
    private lateinit var inflater: ActivityAudioPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xiMaUtile = XiMaUtile.getInstance()

        //进入的时候重置播放的状态
        if (xiMaUtile?.isPlaying == true) {
            xiMaUtile?.pause()
        }

        inflater = ActivityAudioPlayBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        resetUiState()

        initDataChangeObserver()

        initData(intent)

        initListener()

        //音频课老项目在initdata播放一段时间后开始上报里面
        initTimerCount()
    }

    private fun initData(data: Intent?) {
        //如果是底部浮窗进入,直接从预播放队列查询详情就行
        val trackId = data?.getStringExtra(IntentParamsConstants.AUDIO_PARAMS_ID) ?: ""
        val loadPage = data?.getIntExtra(XimaAudioActivity.PARAM_INIT_LOAD_PAGE, 1) ?: 1
        val offLineModel =
            data?.getBooleanExtra(IntentParamsConstants.AUDIO_PARAMS_OFFLINE, false) ?: false

        mPresenter.resourceId = trackId

        val fromBottomFloat =
            data?.getBooleanExtra(IntentParamsConstants.AUDIO_PARAMS_FROM_BOTTOM_FLOAD, false)
                ?: false
        if (fromBottomFloat) {
            mViewModel.offlineModel = PrePlayListManager.getInstance().offlineMode
            mViewModel.getTrackInfo(trackId)
            return
        }

        if (!TextUtils.isEmpty(trackId)) {
            mViewModel.trackId = trackId
            mViewModel.offlineModel = offLineModel
            mViewModel.startPage = loadPage
        }
        //有可能是通知栏点击进来，查看是否有当前播放音频
        if (TextUtils.isEmpty(trackId)) {
            val currentTrack = xiMaUtile?.currentTrack
            if (currentTrack == null) {
                finish()
                return
            }

            //如果是自建音频模型，跳转自建音频页面
            if (currentTrack is MusicBean) {
                ARouter.getInstance().build(Paths.PAGE_AUDIO_OWN_BUILD_PLAY)
                    .withString(IntentParamsConstants.AUDIO_PARAMS_ID, currentTrack.id)
                    .navigation(this, object : ARouterNavigationCallbackWrapper() {
                        override fun onArrival(postcard: Postcard?) {
                            finish()
                        }
                    })
                return
            }
            mViewModel.trackId = currentTrack.id
            mPresenter.resourceId = trackId
            mViewModel.offlineModel = offLineModel
        }

        mViewModel.getTrackInfo(trackId)

        loge("发起音频信息请求: ${mViewModel.trackId}")

    }


    /**
     * 初始化数据变化监听
     */
    private fun initDataChangeObserver() {
        //音频数据变化监听
        mViewModel.trackInfoLiveData.observe(this, Observer {
            setData(it)
        })

        //分享数据监听
        mViewModel.resourceShareDetaildata.observe(this, Observer {
            if ("0" == it.is_enroll) {
                inflater.commonTitle.setRightSecondIconRes(R.mipmap.common_ic_title_right_add_white)
                inflater.commonTitle.ib_right_second?.isEnabled = true
            } else {
                inflater.commonTitle.setRightSecondIconRes(R.mipmap.common_ic_title_right_added_white)
                inflater.commonTitle.ib_right_second?.isEnabled = false
            }
        })
    }


    /**
     * 如果是通过点击音频课再次打开，
     * 不同的id需要重新加载音频
     * 并切换播放
     * 通知栏点击也会走这里
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        loge("导航栏进入 onNewIntent")
        initData(intent)

    }


    /**
     * 初始化倒计时
     */
    fun initTimerCount() {
        if (xiMaUtile?.getmTrackCloseTimer() != null && xiMaUtile?.getmTrackCloseTimer()?.totalTime ?: 0 >= 0) {
//            tvTimeCount.visibility = View.VISIBLE
            xiMaUtile?.getmTrackCloseTimer()?.mTimeCloseListener =
                object : TrackCloseTimerManager.TimeCloseListener {
                    override fun selectTime(t: Long) {
                        showTimeCount(t)
                    }
                }
        }

    }


    /**
     * 展示倒计时时间
     */
    fun showTimeCount(totalTime: Long) {
        runOnMain {
            if (!isFinishing) {
                if (totalTime <= 0) {
                    inflater.tvTimeCount.visibility = View.GONE
                } else {
                    inflater.tvTimeCount.visibility = View.VISIBLE
                }
                val formatAudioPlayTime = TimeFormatUtil.formatAudioPlayTime(totalTime * 1000)
                inflater.tvTimeCount.text = formatAudioPlayTime
            }
        }
    }


    private fun initListener() {
        //点击返回
        inflater.commonTitle.setOnLeftClickListener {
            finish()
        }

        //点击举报
        inflater.commonTitle.setOnRightTextClickListener(View.OnClickListener {
            val put = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_ID, mViewModel.trackId)
                .put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, ResourceTypeConstans.TYPE_TRACK)
                .put(
                    IntentParamsConstants.PARAMS_RESOURCE_TITLE,
                    mViewModel.trackInfoLiveData.value?.track_title
                )
            ARouter.getInstance().build(Paths.PAGE_REPORT_DIALOG).with(put).navigation()
        })

        //点击加入学习室
        inflater.commonTitle.setOnSecondRightIconClickListener {
//            addToStudyRoom()
            mPresenter.addToStudyRoom(ResourceTypeConstans.TYPE_TRACK, mViewModel.trackId) {
                if (it) {
                    //更新加入学习室状态
                    inflater.commonTitle.setRightSecondIconRes(R.mipmap.common_ic_title_right_added_white)
                    inflater.commonTitle.ib_right_second?.isEnabled = false
                }
            }
        }

//        val builder = XPopup.Builder(this).watchView(commonTitle.ib_right)
        inflater.commonTitle.setOnRightIconClickListener(View.OnClickListener {
            // showSettingPop(builder)
//            mPresenter.showSettingPop(builder)
            inflater.commonTitle.ib_right?.let { ibRight ->
                mPresenter.showSettingPop(
                    ibRight,
                    mViewModel.has_permission == "1"
                )
            }
        })

        //点击播放暂停
        inflater.ivPlayStatus.setOnClickListener {
            if (xiMaUtile?.isPlaying == true) {
                xiMaUtile?.pause()
            } else {
                xiMaUtile?.play()
            }
        }

        //进度条拖动
        inflater.sbPlayProgress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            var startPosition = 0   //开始拖拽位置
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                startPosition = seekBar.progress / 1000
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                xiMaUtile?.audioPointManager?.postSeek(startPosition, seekBar.progress / 1000)
                xiMaUtile?.seekTo(seekBar.progress.toLong().toInt())
            }
        })



        xiMaUtile?.setStatusListener(this)
    }


    private fun resetUiState() {
        inflater.progressBar.visibility = View.VISIBLE
        inflater.ivPlayStatus.visibility = View.GONE
        inflater.sbPlayProgress.setProgress(0)
        inflater.tvPlayTime.setText("00:00")
        inflater.tvTotalTime.text = ("00:00")
    }

    private fun hideProgressBar() {
        inflater.progressBar.visibility = View.GONE
        inflater.ivPlayStatus.visibility = View.VISIBLE
        if (xiMaUtile?.isPlaying == true) {
            inflater.ivPlayStatus.setImageResource(R.mipmap.audio_ic_track_paly_pause)
        } else {
            inflater.ivPlayStatus.setImageResource(R.mipmap.audio_ic_middle_track_play)
        }
    }

    /**
     * 设置数据
     */
    private fun setData(trackModel: TrackBean) {

        //学习资源增加积分,倒计时回调
        xiMaUtile?.getmTrackScoreTimer()?.currentTimeCallBack = { title, type, u ->
            AddPointManager.startAddPointsTask(
                this, title, type, u
            )
        }

        inflater.tvAudioname.text = trackModel.track_title
        inflater.tvPlayCount.text = "播放${StringFormatUtil.formatPlayCount(trackModel.play_count)}次"
        Glide.with(this)
            .load(trackModel.subordinated_album?.cover_url_large ?: trackModel.cover_url_small)
            .into(inflater.ivAlbumCover)
        inflater.tvAlbumName.text = trackModel.subordinated_album?.album_title
        inflater.llAlbum.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_ALBUM)
                .withString(
                    IntentParamsConstants.ALBUM_PARAMS_ID,
                    trackModel.subordinated_album?.id
                )
                .withBoolean(ROUTE_FROM_AUDIO, true)
                .navigation(this, REQUSET_CODE_ALBUM)
        }

        resetUiState()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == REQUSET_CODE_ALBUM) {
            //
//            firstScroll = true
            loge("专辑页面onActivityResult")
            //到专辑页面重新选择后时候重置播放的状态
            if (xiMaUtile?.isPlaying == true) {
                xiMaUtile?.pause()
            }
            initData(data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onDestroy() {
        super.onDestroy()

        mPresenter.release()
        xiMaUtile?.removeStatusLIstener(this)
        xiMaUtile?.getmTrackCloseTimer()?.currentTimeCallBack = null
        xiMaUtile?.getmTrackScoreTimer()?.currentTimeCallBack = null
    }

    override fun onPlayStart() {
        inflater.progressBar.visibility = View.GONE
        inflater.ivPlayStatus.visibility = View.VISIBLE
        inflater.ivPlayStatus.setImageResource(R.mipmap.audio_ic_track_paly_pause)
    }

    override fun onPlayPause() {
        inflater.progressBar.visibility = View.GONE
        inflater.ivPlayStatus.visibility = View.VISIBLE
        inflater.ivPlayStatus.setImageResource(R.mipmap.audio_ic_middle_track_play)
    }

    override fun onPlayStop() {
        inflater.progressBar.visibility = View.GONE
        inflater.ivPlayStatus.visibility = View.VISIBLE
        inflater.ivPlayStatus.setImageResource(R.mipmap.audio_ic_middle_track_play)
    }

    override fun onSoundPlayComplete() {
        inflater.progressBar.visibility = View.GONE
        inflater.ivPlayStatus.visibility = View.VISIBLE
        inflater.ivPlayStatus.setImageResource(R.mipmap.audio_ic_middle_track_play)
    }

    override fun onSoundPrepared() {
    }

    override fun onSoundSwitch(var1: PlayableModel?, var2: PlayableModel?) {
        loge("ximalaya回调切换音频${var2?.dataId}")
        var2?.dataId?.toString()?.let {
            //如果切换的音频和当前音频不一样再请求
            if(mViewModel.trackInfoLiveData.value?.id != it){
                mViewModel.getTrackInfo(it,false)
            }
        }
    }

    override fun onBufferingStart() {
        inflater.progressBar.visibility = View.VISIBLE
    }

    override fun onBufferingStop() {
        inflater.progressBar.visibility = View.GONE
    }

    override fun onBufferProgress(var1: Int) {
    }

    override fun onPlayProgress(progress: Int, duration: Int) {
//        loge("progress: ${progress}, duration: ${duration}")
        inflater.progressBar.visibility = View.GONE
        inflater.ivPlayStatus.visibility = View.VISIBLE
        inflater.ivPlayStatus.setImageResource(R.mipmap.audio_ic_track_paly_pause)
        inflater.sbPlayProgress.max = duration
        inflater.sbPlayProgress.setProgress(progress)
        inflater.tvPlayTime.setText(TimeFormatUtil.formatAudioPlayTime(progress.toLong()))
        inflater.tvTotalTime.text = TimeFormatUtil.formatAudioPlayTime(duration.toLong())
    }

    override fun onError(var1: XmPlayerException?): Boolean {
        inflater.progressBar.visibility = View.GONE
        inflater.ivPlayStatus.visibility = View.VISIBLE
        inflater.ivPlayStatus.setImageResource(R.mipmap.audio_ic_middle_track_play)

        return false
    }

}