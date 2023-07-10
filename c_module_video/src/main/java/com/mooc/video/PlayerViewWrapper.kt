package com.mooc.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.ConfirmPopupView
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.logd
import com.mooc.common.utils.NetUtils
import com.mooc.common.utils.net.NetType
import com.mooc.video.databinding.ViewExoPlayerWrapperBinding
import com.mooc.video.util.VideoPlayUtils
import com.mooc.video.widget.MCustomControlView
//import kotlinx.android.synthetic.main.view_exo_player_wrapper.view.*

/**
 * 视频播放器包装类
 * 用于课程播放
 *
 */
open class PlayerViewWrapper @JvmOverloads constructor(
    var mContext: Context,
    attributeSet: AttributeSet? = null,
    defAttr: Int = 0
) : FrameLayout(mContext, attributeSet, defAttr) {

    private  var inflater: ViewExoPlayerWrapperBinding = ViewExoPlayerWrapperBinding.inflate(LayoutInflater.from(context),this)
    val exoPlayer by lazy {
        val application = AppGlobals.getApplication()
        ExoPlayerFactory.newSimpleInstance(
            application,  //视频每一这的画面如何渲染,实现默认的实现类
            DefaultRenderersFactory(application),  //视频的音视频轨道如何加载,使用默认的轨道选择器
            DefaultTrackSelector(),  //视频缓存控制逻辑,使用默认的即可
            DefaultLoadControl()
        )
    }

    companion object {
        var allowNotWifiPlay = false //是否允许非wifi状态下播放
    }

    protected var exoLoadingLayout: View? = null    //loading布局

    /***视频加载页,错误页,进度控件,锁屏按布局,自定义预览布局 */
    private var playReplayLayout: View? = null
    private var customControllLayout: View? = null

    //    private var customControllLayoutId: Int = -1
    //    var playerLayoutVerticalHeight = -1 //默认matchparent
    var controllerListener: VideoControListener? = null
    var activity: Activity? = null
    var playLocalMedia = false //是否是播放的本地媒体

    //    var controllerView: PlayerControlView? = null
    var controllerView: MCustomControlView? = null
//    var allowNotWifiPlay = false //是否允许非wifi状态下播放

    var noWifiNotPlayUrl = "" //因为不是wifi状态下，暂存的url

    //    var hasPrepare = false //视频源是否准备完毕
    var mPlayerView: PlayerView? = null

    var needPlayPosition = -1

    private inner class ComponentListener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateString: String
            // actually playing media
            if (playWhenReady && playbackState == Player.STATE_READY) {
                logd("onPlayerStateChanged: actually playing media")
                showReplyView(false)
                showLoadingStateView(false)
                controllerView?.show()

                if (needPlayPosition >= 0) {
                    logd("needPlayPosition: ${needPlayPosition}")
                    exoPlayer.seekTo(needPlayPosition.toLong() * 1000)
                    needPlayPosition = -1
                }
            }

            if (playbackState == Player.STATE_ENDED) {
                showReplyView(true)
            }


            stateString = when (playbackState) {
                Player.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                Player.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                Player.STATE_READY -> "ExoPlayer.STATE_READY     -"
                Player.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            logd("changed state to $stateString playWhenReady: $playWhenReady")
        }

    }


    private var mComponentListener = ComponentListener()

    init {

        //loading布局
        var loadId: Int = R.layout.layout_simple_exo_play_load
        var replayId: Int = R.layout.layout_simple_exo_play_replay
        //加载自定义属性
        if (attributeSet != null) {
            val a = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.VideoPlayerWrapper,
                0,
                0
            )

            try {
                loadId = a.getResourceId(R.styleable.VideoPlayerWrapper_vpw_load_layout_id, loadId)
                replayId =
                    a.getResourceId(R.styleable.VideoPlayerWrapper_vpw_replay_layout_id, replayId)
                val customControllLayoutId = a.getResourceId(
                    R.styleable.VideoPlayerWrapper_vpw_custom_controll_layout_id,
                    -1
                )

                if (customControllLayoutId != -1) {
                    customControllLayout = View.inflate(context, customControllLayoutId, null)
                }

            } finally {
                a.recycle()
            }
        }

        exoLoadingLayout = View.inflate(context, loadId, null)
        playReplayLayout = View.inflate(context, replayId, null)

        playReplayLayout?.visibility = GONE

//        inflater = ViewExoPlayerWrapperBinding.inflate(LayoutInflater.from(context),this)
//        LayoutInflater.from(mContext).inflate(R.layout.view_exo_player_wrapper, this)
        mPlayerView = findViewById(R.id.playerView)
        activity = VideoPlayUtils.scanForActivity(context)
        initView()

    }

    /**
     * 初始化控件
     */
    private fun initView() {
        //添加loading布局
        this.addView(exoLoadingLayout, this.getChildCount())
        this.addView(playReplayLayout, this.getChildCount())
        //播放器视图，绑定播放器
        mPlayerView?.player = exoPlayer

        controllerView = inflater.playerView.findViewById(R.id.exo_controller)

        controllerView?.let {
            bindControllerView(it)
            if (customControllLayout != null) {
                it.setCustomControllLayout(customControllLayout)
            }
        }

        //注册监听
        exoPlayer.addListener(mComponentListener)

    }

    fun getCustomLayoutView(): View? {
        return customControllLayout
    }

    //播放事件监听
    fun setPlayerEventListener(playerEventListener: Player.EventListener) {
        exoPlayer.addListener(playerEventListener)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun bindControllerView(controlView: MCustomControlView) {
        controlView.let {
//            this.controllerView = it
//            it.setPlayer(exoPlayer)
            it.setPlayerView(this@PlayerViewWrapper)
            inflater.playerView.showController()
            //点击中间

        }

        //设置播放按钮点击事件
        controlView.findViewById<ImageButton>(R.id.exo_play)
            .setOnTouchListener { view, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    if (!playLocalMedia && !allowNotWifiPlay && NetUtils.getNetworkType() != NetType.WIFI) {
                        showNotWifiDialog()
                        return@setOnTouchListener true
                    }
                }
                return@setOnTouchListener false
            }

        playReplayLayout?.findViewById<View>(R.id.exo_player_replay_btn_id)?.setOnClickListener {
            controllerListener?.onClickReply()
        }

    }

    /**
     * 播放视频
     * @param url 视频播放地址
     * @param position 需要定位的播放位置
     */
    open fun playUrl(url: String, position: Int = 0) {
        needPlayPosition = position
        //非wifi状态下提示
        if (!allowNotWifiPlay && !checkWifi()) {
            showNotWifiDialog()
            noWifiNotPlayUrl = url
            return
        }

        noWifiNotPlayUrl = ""
        showReplyView(false)
        showLoadingStateView(true)
        val mediaSource: MediaSource = PageListPlayManager.createMediaSource(url)
        exoPlayer.prepare(mediaSource)
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        exoPlayer.playWhenReady = true
    }

    /**
     * 播放本地视频
     * @param filePath 文件地址
     */
    fun playLocalFile(filePath: String) {
        playLocalMedia = true
        showReplyView(false)
        showLoadingStateView(true)
        val mediaSource: MediaSource = PageListPlayManager
//                .createLocalVideoMediaSource(FileUtils.getFileUri(mContext,filePath))
            .createLocalVideoMediaSource(Uri.parse(filePath))
        exoPlayer.prepare(mediaSource)
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        exoPlayer.playWhenReady = true
    }

    fun release() {
        if (exoPlayer != null) {
            exoPlayer.removeListener(mComponentListener)
            exoPlayer.release()
        }
    }

    fun getPlayPosition(): Int {
        return (exoPlayer.contentPosition / 1000).toInt()
    }

    /**
     * 展示加载中样式
     * @param show true显示，false不显示
     */
    fun showLoadingStateView(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.INVISIBLE
        exoLoadingLayout?.visibility = visibility
    }

    /**
     * 展示重新播放弹窗
     */
    fun showReplyView(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.INVISIBLE
        playReplayLayout?.visibility = visibility

        //显示结束的时候隐藏控制条
        if (show) {
            mPlayerView?.hideController()
        }
    }

    fun pause() {
        exoPlayer.setPlayWhenReady(false);
    }

    fun resume() {
        exoPlayer.setPlayWhenReady(true)


//        //返回该页面时，如果是横屏，则隐藏导航栏，不然会遮住下载按钮
//        val isLand: Boolean = VideoPlayUtils.Companion.isLand(this)
        //如果是横屏,重新设置下window flag
        if (controllerView?.isLand == true) {
            controllerView?.doOnConfigurationChanged(true)
        }

    }

    /**
     * 如果不是竖屏，先调用
     */
    fun onBackPressed(): Boolean {
        if (controllerView?.isLand == true) {
            activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            controllerView?.doOnConfigurationChanged(false)
            return false
        }
        return true
    }

    fun scaleLayout() {
        val contentView = activity?.findViewById<ViewGroup>(android.R.id.content)
        val videoViewParent = mPlayerView?.parent as ViewGroup
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        if (videoViewParent != null) {
            videoViewParent.removeView(mPlayerView)

            if (controllerView?.isLand == true) {
                contentView?.addView(mPlayerView, params)
            } else {
                addView(mPlayerView, 0, params)
            }
        }


    }


    /**
     * 是否是wifi状态
     */
    fun checkWifi(): Boolean {
        return NetUtils.getNetworkType() == NetType.WIFI
    }

    var asConfirm: ConfirmPopupView? = null

    /**
     * 显示非Wi-Fi状态提示
     * 如果不为空，代表还没有传入ur
     */
    fun showNotWifiDialog() {
        if (asConfirm != null && asConfirm?.isShow == true) return
        asConfirm = XPopup.Builder(mContext)
            .asConfirm("提示", "您当前网络不是wifi，是否继续观看")
            {
                clickAllowNotWifiPlay()
            }

        asConfirm?.show()
    }


    /**
     * 隐藏Wi-Fi状态提示
     */
    fun hideNotWifiDialog() {
        if (asConfirm != null && asConfirm?.isShow == true)
            asConfirm?.dismiss()
    }

    /**
     * 点击允许wifi下播放
     */
    fun clickAllowNotWifiPlay() {
        allowNotWifiPlay = true
        //如果数据源没有准备，则重新prepare
        if (noWifiNotPlayUrl.isNotEmpty()) {
            playUrl(noWifiNotPlayUrl)
        } else {
            resume()
        }
    }
}