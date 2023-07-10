//package com.mooc.course.ui.widget
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.FrameLayout
//import com.mooc.course.R
//import com.mooc.course.interfaces.PlayerController
//import com.mooc.course.interfaces.PlayerControllerListener
//import com.mooc.course.utils.VideoPlayUtils
//import com.google.android.exoplayer2.C
//import com.google.android.exoplayer2.ExoPlayer
//import com.google.android.exoplayer2.Player
//import kotlinx.android.synthetic.main.course_view_exo_control.view.*
//
///**
// * 播放器控制器
// * 完全自己实现所有的的控制按钮（播放暂停，进度条，时间等）
// */
//class CoursePlayControllerView @JvmOverloads constructor(var mContext: Context,attributeSet: AttributeSet? = null,defAttr : Int = 0)
//    : FrameLayout(mContext,attributeSet,defAttr), PlayerController, Player.EventListener {
//
//
//    companion object{
//        const val controllerShowTime = 2 * 1000L  //控制器显示时间，单位毫秒
//
//    }
//
//    var iAttachedToWindow = false
//
//
//    var mExoPlayer : ExoPlayer? = null
//    //控制器监听
//    var controlListener : PlayerControllerListener? = null
//
//    //需要自己跑定时器观测播放进度，显示进度条和时间
//    init {
//        LayoutInflater.from(mContext).inflate(R.layout.course_view_exo_control,this)
//
//        //播放
//        exo_play.setOnClickListener {
//            mExoPlayer?.playWhenReady = true
//        }
//
//        //暂停
//        exo_pause.setOnClickListener {
//            mExoPlayer?.playWhenReady = false
//        }
//
//        //点击全屏
//        exo_video_fullscreen.setButtonDrawable(R.drawable.course_ic_fullscreen_selector)
//        exo_video_fullscreen.setOnClickListener {
//            //切竖屏portrait screen
////            if (VideoPlayUtils.isLand(mContext)) {
////                //  doOnConfigurationChanged(false);
////                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
////                //切横屏landscape
////            } else {
////                //  doOnConfigurationChanged(true);
////                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
////            }
//            controlListener?.changeOritation(!VideoPlayUtils.isLand(mContext))
//        }
//
//
//
//    }
//
//
//    /**
//     * 关联播放器
//     */
//    fun setPlayer(exoPlayer : ExoPlayer){
//        mExoPlayer = exoPlayer
//        mExoPlayer?.addListener(this)
//    }
//
//    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//
//        //更新播放或暂停
//        updatePlayPauseButton()
//        //
//        updateProgress()
//
//    }
//
//    /**
//     * 更新播放进度
//     */
//    private fun updateProgress() {
//
//    }
//
//
//    override fun show() {
//        //todo 做平移动画
//        visibility = View.VISIBLE
//        //2000后隐藏
//        postDelayed({ hide() }, controllerShowTime)
//    }
//
//    override fun hide() {
//        visibility = View.INVISIBLE
//    }
//
//    fun isPlaying(): Boolean {
//
//        return mExoPlayer?.let {
//            it.playbackState != Player.STATE_ENDED && it.playbackState != Player.STATE_IDLE && it.playWhenReady
//        }?:false
//
//
////        return mExoPlayer? != null && mExoPlayer?.getPlaybackState() != mExoPlayer?.STATE_ENDED && mExoPlayer?.getPlaybackState() != mExoPlayer?.STATE_IDLE && mExoPlayer?.getPlayWhenReady()
//    }
//
//    /**
//     * Update play pause button.
//     */
//    protected fun updatePlayPauseButton() {
//        if (!isVisible() || !isAttachedToWindow) {
//            return
//        }
//        var requestPlayPauseFocus = false
//        val playing = isPlaying()
//        if (exo_play != null) {
//            requestPlayPauseFocus = requestPlayPauseFocus or (playing && exo_play.isFocused())
//            exo_play.setVisibility(if (playing) View.GONE else View.VISIBLE)
//        }
//        if (exo_pause != null) {
//            requestPlayPauseFocus = requestPlayPauseFocus or (!playing && exo_pause.isFocused())
//            exo_pause.setVisibility(if (!playing) View.GONE else View.VISIBLE)
//        }
//        if (requestPlayPauseFocus) {
//            requestPlayPauseFocus()
//        }
//    }
//
//    /**
//     * Request play pause focus.
//     */
//    protected fun requestPlayPauseFocus() {
//        val playing = isPlaying()
//        if (!playing && exo_pause != null) {
//            exo_pause.requestFocus()
//        } else if (playing && exo_pause != null) {
//            exo_pause.requestFocus()
//        }
//    }
//
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        iAttachedToWindow = true
//
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//
//        iAttachedToWindow = false
//    }
//
//    /**
//     * Returns whether the controller is currently visible.  @return the boolean
//     */
//    fun isVisible(): Boolean {
//        return visibility == View.VISIBLE
//    }
//
//}