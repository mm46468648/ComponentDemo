package com.mooc.audio.ui

//import kotlinx.android.synthetic.main.activity_audio_own_build.*
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mooc.audio.R
import com.mooc.audio.databinding.ActivityAudioOwnBuildBinding
import com.mooc.audio.manager.PrePlayListManager
import com.mooc.audio.manager.TrackCloseTimerManager
import com.mooc.audio.manager.XiMaUtile
import com.mooc.audio.presenter.XimaAudioBusinessPresenter
import com.mooc.audio.viewmodel.OwnBuildAudioPlayViewModel
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.runOnMain
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.audio.BaseAudioModle
import com.mooc.commonbusiness.model.studyproject.MusicBean
import com.mooc.commonbusiness.module.web.MobileWebInterface
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.commonbusiness.utils.incpoints.AddPointManager
import com.mooc.commonbusiness.utils.incpoints.FirstAddPointManager
import com.mooc.webview.WebViewWrapper
import com.mooc.webview.stratage.AndroidWebkitStrategy
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException
import org.json.JSONObject


/**
 * 自建音频播放页面
 */
@Route(path = Paths.PAGE_AUDIO_OWN_BUILD_PLAY)
class OwnBuildUseXimaAudioActivity : BaseActivity(), XiMaUtile.StatusListener {

    val mViewModel: OwnBuildAudioPlayViewModel by viewModels()

    var xiMaUtile: XiMaUtile? = null
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
        audioBusinessPresenter
    }

    private lateinit var inflater: ActivityAudioOwnBuildBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivityAudioOwnBuildBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        xiMaUtile = XiMaUtile.getInstance()
//        webviewWrapper.strategy = AndroidWebkitStrategy(this)

        initData(intent)

        initObservser()
        initListener()
        initTimerCount()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        initData(intent)
    }

    private fun initData(data: Intent?) {

        inflater.progressBar.visibility = View.VISIBLE
        inflater.ivPlayStatus.visibility = View.GONE

        val trackId = data?.getStringExtra(IntentParamsConstants.AUDIO_PARAMS_ID) ?: ""
        mViewModel.trackId = trackId
        mViewModel.loadAudioInfo()
    }

    /**
     * 上传学习记录
     */
    fun postStudyScore() {

//        val request = JSONObject()
//        request.put("type", ResourceTypeConstans.TYPE_ONESELF_TRACK)
//        request.put("url", trackId)
//        request.put("title", mViewModel.trackInfoLiveData.value?.audio_name)
//        StudyLogManager.postStudyLog(request)

        //每日首次增加积分
//        FirstAddPointManager.startAddFirstPointsTask(
//            this, ResourceTypeConstans.typeAliasName[ResourceTypeConstans.TYPE_ONESELF_TRACK]
//                ?: "", mViewModel.trackId
//        )
        //学习资源增加积分
        xiMaUtile?.getmTrackScoreTimer()?.currentTimeCallBack = { title, type, u ->
            AddPointManager.startAddPointsTask(this, title, type, u)
        }

    }


    fun initObservser() {
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

        //音频数据变化监听
        mViewModel.trackInfoLiveData.observe(this, Observer {
            postStudyScore()
            setData(it)
        })

        mViewModel.getError().observe(this, Observer {
//            setRefreshState(LoadStateConstants.STATE_ERROR)
        })
    }

    private fun initListener() {
        //返回键监听
        inflater.commonTitle.setOnLeftClickListener { onBackPressed() }
        //举报按钮
        inflater.commonTitle.setOnRightTextClickListener({
            val put = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_ID, mViewModel.trackId)
                .put(
                    IntentParamsConstants.PARAMS_RESOURCE_TYPE,
                    ResourceTypeConstans.TYPE_ONESELF_TRACK
                )
                .put(
                    IntentParamsConstants.PARAMS_RESOURCE_TITLE,
                    mViewModel.trackInfoLiveData.value?.audio_name
                )
            ARouter.getInstance().build(Paths.PAGE_REPORT_DIALOG).with(put).navigation()
        })

        //点击加入学习室
        inflater.commonTitle.setOnSecondRightIconClickListener {
            mPresenter.addToStudyRoom(ResourceTypeConstans.TYPE_ONESELF_TRACK, mViewModel.trackId) {
                if (it) {
                    //更新加入学习室状态
                    inflater.commonTitle.setRightSecondIconRes(R.mipmap.common_ic_title_right_added_white)
                    inflater.commonTitle.ib_right_second?.isEnabled = false
                }
            }
        }

        //点击菜单栏
//        val builder = XPopup.Builder(this).watchView(commonTitle.ib_right)
        inflater.commonTitle.setOnRightIconClickListener({
            // showSettingPop(builder)
//            mPresenter.showSettingPop(builder)
            mPresenter.showSettingPop(inflater.commonTitle)

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

    /**
     * 设置数据
     */
    private fun setData(musicBean: MusicBean) {
        inflater.tvAudioname.text = musicBean.audio_name
        inflater.tvPlayCount.text =
            "播放${StringFormatUtil.formatPlayCount(musicBean.audio_play_num?.toLong())}次"

        //背景
        Glide.with(inflater.ivHead)
            .applyDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
            .load(musicBean.audio_bg_img)
            .placeholder(R.mipmap.audio_bg_playpage_head)
            .error(R.mipmap.audio_bg_playpage_head)
            .into(inflater.ivHead)

        //音频简介
        if (musicBean.audio_content.isNotEmpty()) {
            //添加一个javaScript接口，接收点击长按回调事件

            val json = JSONObject()
            json.put("url", musicBean.id)
            json.put("other_resource_id", musicBean.id)
            json.put("source_resource_type", ResourceTypeConstans.TYPE_ONESELF_TRACK)
            json.put("title", musicBean.trackTitle)

//            val strategy = AndroidWebkitStrategy(this, inflater.mWebview)
//            strategy.initWebView()
//            strategy.addJavascriptInterface(MobileWebInterface(this), "BigImagePreview")
//            strategy.openCustomLongTextPop(json)
            val html: String = HtmlUtils.getFormatHtml(musicBean.audio_content)
//            inflater.mWebview.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)

            val wrapper = WebViewWrapper(this,false)
            wrapper.strategy.openCustomLongTextPop(json)
            wrapper.loadDataWithBaseURL(html)
            inflater.flContainer.addView(wrapper.getView())


        }

        //添加测试卷布局
        if (musicBean.is_testpaper == "1" && musicBean.testpaper_name.isNotEmpty()) {
            inflater.tvTestPaperTitle.visibility = View.VISIBLE
            inflater.tvTestPaperName.visibility = View.VISIBLE
            inflater.tvTestPaperName.setText(musicBean.testpaper_name)
            inflater.tvTestPaperName.setOnClickListener {
                //跳转前暂停音频播放
                xiMaUtile?.pause()
                //跳转测试卷
                ARouter.getInstance()
                    .build(Paths.PAGE_WEB_RESOURCE)
                    .withInt(
                        IntentParamsConstants.PARAMS_RESOURCE_TYPE,
                        ResourceTypeConstans.TYPE_TEST_VOLUME
                    )
                    .withString(IntentParamsConstants.WEB_PARAMS_TITLE, "测试卷")
                    .withString(IntentParamsConstants.WEB_PARAMS_URL, musicBean.testpaper_url)
                    .navigation()
            }
        } else {
            inflater.tvTestPaperTitle.visibility = View.GONE
            inflater.tvTestPaperName.visibility = View.GONE
        }

        mPresenter.resourceId = musicBean.id
        mPresenter.resourceName = musicBean.trackTitle
//        TrackPlayManger.playWithMediaId(musicBean)
        loge(musicBean.resource_link)
        PrePlayListManager.getInstance().clearList()
        xiMaUtile?.setList(arrayListOf(musicBean) as List<BaseAudioModle>, 0)
        xiMaUtile?.playTrack(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        mPresenter.release()
        xiMaUtile?.getmTrackCloseTimer()?.currentTimeCallBack = null
        xiMaUtile?.getmTrackScoreTimer()?.currentTimeCallBack = null
        xiMaUtile?.removeStatusLIstener(this)
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
        inflater.tvPlayTime.setText(TimeFormatUtil.formatAudioPlayTime(inflater.sbPlayProgress.max.toLong()))
        inflater.ivPlayStatus.setImageResource(R.mipmap.audio_ic_middle_track_play)
    }

    override fun onSoundPrepared() {
    }

    override fun onSoundSwitch(var1: PlayableModel?, var2: PlayableModel?) {
//        var2?.dataId?.toString()?.let {
//            mViewModel.getTrackInfo(it)
//        }
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

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK && inflater.mWebview.canGoBack()) {
//            inflater.mWebview.goBack()
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    override fun onBackPressed() {
//        if (inflater.mWebview.canGoBack()) {
//            inflater.mWebview.goBack()
//        } else {
//            super.onBackPressed()
//        }
//    }
}