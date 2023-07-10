package com.mooc.course.ui.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.logd
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.net.NetMangaer
import com.mooc.common.utils.net.NetType
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.studylog.StudyLogManager
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.AudioPlayService
import com.mooc.commonbusiness.utils.incpoints.AddPointManager
import com.mooc.commonbusiness.utils.incpoints.FirstAddPointManager
import com.mooc.course.R
import com.mooc.course.databinding.CoursePlayActivityBinding
import com.mooc.course.manager.VideoActionManager
import com.mooc.course.manager.VideoPointManger
import com.mooc.course.model.GradePolicy
import com.mooc.course.ui.adapter.CoursePlayPageAdapter
import com.mooc.course.viewmodel.CoursePlayViewModel
import com.mooc.video.PlayerViewWrapper
import com.mooc.video.VideoControListener
import com.mooc.video.util.VideoPlayUtils
//import kotlinx.android.synthetic.main.course_play_activity.*
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.*


/**
 * 课程播放页面
 */
@Route(path = Paths.PAGE_COURSE_PLAY)
open class CoursePlayActivity : BaseActivity(), Player.EventListener {

    companion object {
        const val PARAMS_COURSE_GRADEPOLICY =
                "params_course_gradepolicy"   //课程，等级政策（用于考试fragmnet展示过关条件）

        //        const val testPlayerUrl =
//            "http://ws.cdn.xuetangx.com/40ec1b3da375eb66-20.mp4?wsSecret=4b26701cc0c1b77a943507ae9d482161&wsTime=1606130540&utm_xuetangx=P5Dgn3rg06F3sS3E"
        const val MESSAGE_PLAY_TIME_INCRESE = 0 //播放时间增加消息
    }

    val courseId by extraDelegate(IntentParamsConstants.COURSE_PARAMS_ID, "")
    val courseTitle by extraDelegate(IntentParamsConstants.COURSE_PARAMS_TITLE, "")
    val courseBean by extraDelegate(IntentParamsConstants.COURSE_PARAMS_DATA, CourseBean())

    var gradePolicy: GradePolicy? = null
    val mViewModel: CoursePlayViewModel by lazy {
//        ViewModelProviders.of(this, BaseViewModelFactory(courseId))
//            .get(CoursePlayViewModel::class.java)

        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CoursePlayViewModel(courseId) as T
            }
        }).get(CoursePlayViewModel::class.java)
    }

    var isPlayIng = false //是否正在播放

    //播放计时，如果上报完毕置空
    var mPlayTimeHandler: Handler? = null


    /**
     * 播放计时类
     */
    class MHandler(mActivity: Activity, var randomTime: Int) : Handler() {
        var playTime = 0
        var weakActivity = WeakReference<Activity>(mActivity)

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_PLAY_TIME_INCRESE -> {
                    playTime++
                    if (playTime > randomTime && weakActivity.get() != null) {
                        loge("playTime: ${playTime}  --- randomTime${randomTime}")
                        (weakActivity.get() as CoursePlayActivity).postAddPoint()
                        return
                    }
                    sendEmptyMessageDelayed(MESSAGE_PLAY_TIME_INCRESE, 1000)
                }
            }
            super.handleMessage(msg)
        }
    }

    /**
     * 增加积分
     */
    fun postAddPoint() {
        AddPointManager.startAddPointsTask(
                this,
                mViewModel.coursePlayLiveData.value?.title ?: "",
                ResourceTypeConstans.TYPE_COURSE.toString(),
                courseBean.id
        )
        mPlayTimeHandler?.removeMessages(MESSAGE_PLAY_TIME_INCRESE)
        mPlayTimeHandler = null
    }

    protected lateinit var inflater:CoursePlayActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = CoursePlayActivityBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        //初始化计时类
        if (mPlayTimeHandler == null) {
            val duratime = (6 + Random().nextInt(4)) * 60
            mPlayTimeHandler = MHandler(this, duratime)
        }

        //视频播放页暂停音频的播放
        ARouter.getInstance().navigation(AudioPlayService::class.java).stopPlay()
        //初始化数据
        initData()

        initPlayer()

        //观察章节切换
        observerSequentialChange()

        initViewPager()

        registNetObserver()

        //每日首次增加积分
        FirstAddPointManager.startAddFirstPointsTask(
                this,
                ResourceTypeConstans.typeAliasName[ResourceTypeConstans.TYPE_COURSE] ?: "",
                courseBean.id
        )
        postStudyLog(courseBean)
    }

    /**
     * 上传学习记录
     */
    fun postStudyLog(it: CourseBean) {
        val request = JSONObject()
        request.put("type", ResourceTypeConstans.TYPE_COURSE)
        request.put("url", it.id)
        request.put("title", it.title)
        StudyLogManager.postStudyLog(request)
    }

    private fun registNetObserver() {
        NetMangaer.getInstance(this.application).getNetTypeLiveData()
                .observe(this, Observer { str ->
                    // 网络状态改变
                    loge("测试", "Main网络状态改变：${str}")
                    //binding.netType = str
                    if (str == NetType.WIFI) {
                        inflater.playerWrapper.hideNotWifiDialog()
                        return@Observer
                    }

                    //非wifi，并且不允许流量下播放
                    if (!PlayerViewWrapper.allowNotWifiPlay && isPlayIng) {
                        inflater.playerWrapper.pause()
                        inflater.playerWrapper.showNotWifiDialog()
                    }

                })
    }


    /**
     * 观察章节切换数据监听
     */
    private fun observerSequentialChange() {
        //章节详情列表
        mViewModel.currentSequentialDetailChildrenList.observe(this, Observer {
            mViewModel.getSequentialPlayUrl(it.first().source)
        })

        //视频播放地址
        mViewModel.videlUrlStringData.observe(this, Observer {
            playUrl(it)
        })
        //视频播放地址和播放位置
        mViewModel.videlUrlAndPositionData.observe(this, Observer {
            //切换视频的时候，保存一下上一个位置

            val first = it.first
            val second = it.second
            if (first.isNotEmpty()) {
                playUrl(first, second)
            }
        })

    }


    /**
     * 初始化播放器
     */
    private fun initPlayer() {
        inflater.playerWrapper.setPlayerEventListener(this)
        inflater.playerWrapper.controllerListener = object : VideoControListener {
            override fun onShowController(b: Boolean) {
            }

            override fun onClickReply() {
                mViewModel.videlUrlAndPositionData.value?.let {
                    playUrl(it.first)
                }
            }
        }

        inflater.playerWrapper.getCustomLayoutView()?.findViewById<View>(R.id.ib_back)?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()


        inflater.playerWrapper.pause()
    }

    override fun onResume() {
        super.onResume()
        inflater.playerWrapper.resume()

    }

    /**
     * Player EVENT onPlayerStateChanged
     */
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        loge("playerReady: ${playWhenReady}  playbackState: ${playbackState}")
        if (playWhenReady && playbackState == Player.STATE_READY) {
            isPlayIng = true
            mPlayTimeHandler?.sendEmptyMessageDelayed(MESSAGE_PLAY_TIME_INCRESE, 1000)
            VideoPointManger.start()
        } else {
            isPlayIng = false
            mPlayTimeHandler?.removeMessages(MESSAGE_PLAY_TIME_INCRESE)
            VideoPointManger.pause()
            //记录播放位置，STATE_ENDED的时候也会走
            val playPosition = inflater.playerWrapper.getPlayPosition()
            if (playPosition != 0) {
                mViewModel.currentPlayPosition.postValue(playPosition)
            }
        }
        //播放结束，切换下一个视频
        if (playbackState == ExoPlayer.STATE_ENDED) {
            mViewModel.getNextUrl()
        }
    }

    /**
     * 给fragment，主动获取当前播放位置
     */
    fun getPlayPosition(): Int {
        if (!isPlayIng) return 0
        val playPosition = inflater.playerWrapper.getPlayPosition()
        return playPosition
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        logd("error state to ${error.toString()}")
    }

    /**
     * 播放视频
     */
    private fun playUrl(url: String) {
        inflater.playerWrapper.playUrl(url)
    }

    /**
     * 播放视频
     */
    private fun playUrl(url: String, position: Int) {
        inflater.playerWrapper.playUrl(url, position)
    }

    private fun initData() {
        //获取传递过来的登记政策数据
        gradePolicy = intent.getSerializableExtra(PARAMS_COURSE_GRADEPOLICY) as GradePolicy?
        gradePolicy?.let {
            mViewModel.courseGradePolicy.value = it
        }

        //监听错误信息
        mViewModel.getError().observe(this, Observer {
            loge(it.toString())
        })
        //监听课程信息
        mViewModel.getCoursePlayData().observe(this, Observer {
            loge(it)
            inflater.tvCourseName.text = it.title
        })
        //观察播放列表中的即将要播放的数据
        mViewModel.getData()
    }

    override fun onBackPressed() {
        if (inflater.playerWrapper.onBackPressed()) {
            super.onBackPressed()
        }
    }

    /**
     * 初始化下面viewpager
     */
    open fun initViewPager() {
//        inflater.stlTitle.setTabStrs(arrayOf("课件", "公告", "考核"))
//        inflater.viewPager.adapter = CoursePlayPageAdapter(courseId, this)
//        inflater.stlTitle.setViewPager2(inflater.viewPager)
    }

    override fun onDestroy() {
        super.onDestroy()
        inflater.playerWrapper.release()
        VideoActionManager.release()

        mPlayTimeHandler?.removeCallbacksAndMessages(null)
        mPlayTimeHandler = null
    }

}