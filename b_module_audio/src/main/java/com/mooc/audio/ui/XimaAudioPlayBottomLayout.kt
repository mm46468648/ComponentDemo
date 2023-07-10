package com.mooc.audio.ui

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.lzf.easyfloat.EasyFloat
import com.mooc.audio.R
import com.mooc.audio.manager.XiMaUtile
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.audio.BaseAudioModle
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.model.studyproject.MusicBean
import com.mooc.commonbusiness.module.report.ReportDialogNewActivity
import com.mooc.commonbusiness.module.report.ShakeFeekbackDialogActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException
import java.lang.ref.WeakReference

/**
 * 喜马拉雅
 * 音频播放,底部浮窗
 */
object XimaAudioPlayBottomLayout {

    //需要过滤显示的Activity
    private var filterClassArray: Array<String> = arrayOf(
            XimaAudioActivity::class.java.name,
            AudioActivity::class.java.name,
            OwnBuildUseXimaAudioActivity::class.java.name,
            ReportDialogNewActivity::class.java.name,
            ShakeFeekbackDialogActivity::class.java.name,
            "CoursePlayActivity",
    )

    //底部需要偏移的Activity
    private var bottomOffsetClassArray: Array<String> = arrayOf(
            "com.mooc.home.ui.home.HomeActivity",
            "com.mooc.course.ui.activity.CourseDetailActivity",
            "com.mooc.ebook.EBookDetailActivity",
            "com.mooc.discover.ui.TaskDetailsActivity"
    )

    var lastActivity: WeakReference<AppCompatActivity>? = null
    private var lastTag = ""
    val mXiMaUtil: XiMaUtile by lazy {
        XiMaUtile.getInstance()
    }

    private var mAudioStatusListener: MAudioStatusListener? = null

    fun show(activity: AppCompatActivity) {
        //如果未播放，则不显示
        if (mXiMaUtil.isBottomFloatClose) return
        //播放空不显示
        val currentTrackModel = mXiMaUtil.currentTrack ?: return
        if (!TextUtils.isEmpty(lastTag)) EasyFloat.dismiss(lastTag)

        // 过滤掉一些Activity
        if (filterClassArray.contains(activity.componentName.className)) return
        //如果是home页面，要偏移底部BottomLayout高度的距离
        val bottomOffset =
                if (activity.componentName.className in bottomOffsetClassArray) (-68).dp2px() else 0
        lastActivity = WeakReference(activity)
        lastTag = activity.javaClass.simpleName

        val mShowView = MShowView(activity)

        EasyFloat.with(activity)
                .setTag(lastTag)
                .setGravity(Gravity.BOTTOM, 0, bottomOffset)
                .setDragEnable(false)
                .setMatchParent(widthMatch = true, heightMatch = false)
                // 设置Activity浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
                .setAnimator(null)
                .setLayout(mShowView) {
                    mShowView.setTrackInfo(currentTrackModel)
                }
                .show()


        if (mAudioStatusListener == null) {
            mAudioStatusListener = MAudioStatusListener()
        }
        mAudioStatusListener?.mShowView = mShowView

        //设置之前，先移除之前的
        mXiMaUtil.removeStatusLIstener(mAudioStatusListener)
        mXiMaUtil.setStatusListener(mAudioStatusListener)
    }

    class MAudioStatusListener : XiMaUtile.StatusListener {
        var mShowView: MShowView? = null
        override fun onPlayStart() {
            mShowView?.onPlayStart()
        }

        override fun onPlayPause() {
            mShowView?.onPlayPause()
        }

        override fun onPlayStop() {
            mShowView?.onPlayStop()
        }

        override fun onSoundPlayComplete() {
            mShowView?.onSoundPlayComplete()
        }

        override fun onSoundPrepared() {
            mShowView?.onSoundPrepared()
        }

        override fun onSoundSwitch(var1: PlayableModel?, var2: PlayableModel?) {
            mShowView?.onSoundSwitch(var1, var2)
        }

        override fun onBufferingStart() {
            mShowView?.onBufferingStart()
        }

        override fun onBufferingStop() {
            mShowView?.onBufferingStop()
        }

        override fun onBufferProgress(var1: Int) {
            mShowView?.onBufferProgress(var1)
        }

        override fun onPlayProgress(var1: Int, var2: Int) {
            mShowView?.onPlayProgress(var1, var2)
        }

        override fun onError(var1: XmPlayerException?): Boolean {
            return mShowView?.onError(var1) ?: false
        }

    }


    /**
     * 隐藏浮窗
     * 有些场景需要暂时调用隐藏或浮窗
     */
    fun hide(activity: AppCompatActivity, hide: Boolean) {
//        val floatView = EasyFloat.getFloatView(activity)
        val floatView = EasyFloat.getFloatView(activity.javaClass.simpleName)
        floatView?.visibility = if (hide) View.INVISIBLE else View.VISIBLE
    }


    /**
     *  关闭浮窗，同时停止音频
     */
    fun release() {
        mXiMaUtil.removeStatusLIstener(mAudioStatusListener)
        XiMaUtile.getInstance().release()
        EasyFloat.dismiss()
        lastActivity = null
        lastTag = ""
    }

    class MShowView(context: Context) : FrameLayout(context), XiMaUtile.StatusListener {
        init {
            View.inflate(context, R.layout.audio_float_audio_bottom, this)
            _init()
        }

        private var ivStart: ImageView? = null
        private var ivCover: ImageView? = null
        private var ivClose: ImageView? = null
        private var tvTime: TextView? = null
        var tvAudioTitle: TextView? = null
        private fun _init() {
            ivStart = findViewById(R.id.ivStart)
            ivCover = findViewById(R.id.ivCover)
            ivClose = findViewById(R.id.ivClose)
            tvTime = findViewById(R.id.tvTime)
            tvAudioTitle = findViewById(R.id.tvAudioTitle)
        }

        fun setTrackInfo(trackBean: BaseAudioModle) {
            tvAudioTitle?.text = trackBean.trackTitle
            this.setOnClickListener { //跳转音频播放
                if (trackBean is TrackBean) {
                    ARouter.getInstance().build(Paths.PAGE_AUDIO_PLAY)
                            .withBoolean(IntentParamsConstants.AUDIO_PARAMS_FROM_BOTTOM_FLOAD,true)
                            .withString(IntentParamsConstants.AUDIO_PARAMS_ID, trackBean.id)
                            .withString(IntentParamsConstants.ALBUM_PARAMS_ID, trackBean.albumTitle)
                            .withString(IntentParamsConstants.ALBUM_PARAMS_ID, trackBean.albumId)
                            .navigation()
                } else if (trackBean is MusicBean) {
                    //跳转到自建音频页面
                    ARouter.getInstance().build(Paths.PAGE_AUDIO_OWN_BUILD_PLAY)
                            .withString(IntentParamsConstants.AUDIO_PARAMS_ID, trackBean.id)
                            .navigation()
                }

            }
            ivClose?.setOnClickListener {
                //关闭浮窗，释放activity
                EasyFloat.dismiss(lastActivity?.get()?.javaClass?.simpleName)
                mXiMaUtil.isBottomFloatClose = true
                mXiMaUtil.pause()
                lastActivity = null

            }

            if(AppGlobals.getApplication()?.applicationContext!=null){
                ivCover?.let {
                    Glide.with(AppGlobals.getApplication()?.applicationContext!!).load(trackBean.coverUrl).into(it)
                }
            }
            //设置播放暂停
            ivStart?.setOnClickListener {
                if (mXiMaUtil.isPlaying) {
                    mXiMaUtil.pause()
                } else {
                    mXiMaUtil.play()
                }
            }
        }

        override fun onPlayStart() {
            ivStart?.setImageResource(R.mipmap.audio_ic_float_pause)
        }

        override fun onPlayPause() {
            ivStart?.setImageResource(R.mipmap.audio_ic_float_play)
        }

        override fun onPlayStop() {
            ivStart?.setImageResource(R.mipmap.audio_ic_float_play)
        }

        override fun onSoundPlayComplete() {
            ivStart?.setImageResource(R.mipmap.audio_ic_float_play)
        }

        override fun onSoundPrepared() {
        }

        override fun onSoundSwitch(var1: PlayableModel?, var2: PlayableModel?) {
            if (var2 != null) {
                val appTrackBean = mXiMaUtil.getAppTrackBean(var2.dataId)
//                val trackTitle = appTrackBean?.trackTitle ?: ""
//                tvAudioTitle?.text = trackTitle
                setTrackInfo(appTrackBean)
            }
        }

        override fun onBufferingStart() {
        }

        override fun onBufferingStop() {
        }

        override fun onBufferProgress(var1: Int) {
        }

        override fun onPlayProgress(var1: Int, var2: Int) {
            val timeStr = "${TimeFormatUtil.formatAudioPlayTime(var1.toLong())}/${
                TimeFormatUtil.formatAudioPlayTime(var2.toLong())
            }"
            tvTime?.text = timeStr
            ivStart?.setImageResource(R.mipmap.audio_ic_float_pause)
        }

        override fun onError(var1: XmPlayerException?): Boolean {
            return false
        }


    }
}