package com.mooc.setting.ui

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ljuns.logcollector.LogCollector
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.ChannelConstants
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.utils.DebugUtil
import com.mooc.resource.widget.CommonSettingItem
import com.mooc.resource.widget.ScaleImage
import com.mooc.setting.R
import com.mooc.setting.databinding.CommonActivityControllerBinding
import com.mooc.setting.ui.controller.LogAdapter
//import kotlinx.android.synthetic.main.common_activity_controller.*
import java.lang.ref.WeakReference
import kotlin.math.max

import com.lxj.xpopup.XPopup
import com.mooc.common.utils.sharepreference.SpUtils
import com.mooc.commonbusiness.constants.SpConstants


/**
 * 暗门，控制器，用于调试
 */
@Route(path = Paths.PAGE_CONTROLLER)
class ControllerActivity : BaseActivity() {


    var mHandler = MyHandler(this)

    companion object {
        const val LOG_KEY_HTTP = "http"
        var openLog = false    //是否开启日志
//        var openNoEncryp = false  //是否开启明文
    }

    private lateinit var inflater : CommonActivityControllerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = CommonActivityControllerBinding.inflate(layoutInflater)
        setContentView(inflater.root)


        initView()
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }


    }

    private fun initView() {
        setSwitchIcon(inflater.csiOpenLog, openLog)
        setSwitchIcon(inflater.csiOpenNoDebugMode, DebugUtil.debugMode)
        //log日志显示开关
        inflater.csiOpenLog.setOnClickListener {
            openLog = !openLog
            setOpenLogShow(openLog)
            setSwitchIcon(inflater.csiOpenLog, openLog)
        }

        //明文传输开关
        inflater.csiOpenNoDebugMode.setOnClickListener {
            DebugUtil.debugMode = !DebugUtil.debugMode
            setSwitchIcon(inflater.csiOpenNoDebugMode, DebugUtil.debugMode)
        }

        //选择测试账号
        inflater.csiEnterTestAccount.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_CONTROLLER_TEST_ACCOUNT).navigation()
        }

        setCurrentRootUrl()
        //切换域名
        inflater.csiChangeRootUrl.setOnClickListener {
            ApiService.setUserNewUrl(!ApiService.getUserNewUrl())
            setCurrentRootUrl()
        }
        //查看当前渠道
        inflater.csiChannel.rightText = ChannelConstants.channelName

        //切换皮肤
        inflater.csiChangeSkin.setOnClickListener{
            XPopup.Builder(this) //.maxWidth(600)
                .asCenterList(
                    "选择皮肤", arrayOf("festival", "pag", "default")
                ) { position, text ->
                    if("default" == text){
                        SkinManager.getInstance().changeSkin("")
                        SpUtils.get().putValue(SpConstants.SP_SKIN_Suffix, "")
                    }else{
                        SkinManager.getInstance().changeSkin(text)
                        SpUtils.get().putValue(SpConstants.SP_SKIN_Suffix, text)
                    }
                }
                .show()
        }

        //资源跳转测试
        inflater.csiResourceTest.setOnClickListener(View.OnClickListener {
            ARouter.getInstance().build(Paths.PAGE_CONTROLLER_TEST_RESOURCE).navigation()
        })
    }

    fun setCurrentRootUrl() {
        val url = if (ApiService.getUserNewUrl()) ApiService.NORMAL_BASE_URL else ApiService.OLD_BASE_URL
        inflater.csiChangeRootUrl.rightText = "$url"
    }


    private fun setOpenLogShow(openLog: Boolean) {
        if (openLog) {
            openLogShow()
        } else {
            closeLog()
        }
    }

    fun setSwitchIcon(controllerItem: CommonSettingItem, open: Boolean) {
        val csiOpenLogRightIcon =
                if (open) R.mipmap.set_ic_switch_open else R.mipmap.set_ic_switch_close
        controllerItem.rightIcon = csiOpenLogRightIcon
    }

    /**
     * 打开日志显示
     */
    private fun openLogShow() {
        //设置开关开启
//        csiOpenLog.rightIcon = R.mipmap.my_ic_setting_switch_open
        //开启浮窗，申请浮窗权限
        showFloatWindown()
        //开启日志捕获
        startLogThread()


    }

    val LogFloatTag = "logFloat"

    /**
     * 显示浮窗
     */
    private fun showFloatWindown() {

        EasyFloat.with(this)
                .setTag(LogFloatTag)
                .setShowPattern(ShowPattern.FOREGROUND)
                .setLocation(100, 100)
                .setAnimator(null)
                .setLayout(R.layout.common_float_app_log, OnInvokeView {

                    val content = it.findViewById<RelativeLayout>(R.id.rlContent)
                    val params = content.layoutParams as FrameLayout.LayoutParams

                    val rcvLog = it.findViewById<RecyclerView>(R.id.rcvLog)
                    val tvClear = it.findViewById<TextView>(R.id.tvClear)
                    tvClear.setOnClickListener {
                        LogCollector.getInstance(application).logChangeObserver.clear()
                    }
                    val logChangeObserver = LogCollector.getInstance(application).logChangeObserver
                    val logAdapter = LogAdapter(logChangeObserver.arrayList as ArrayList<String>)
                    logAdapter.onUpdateCallback = {
                        rcvLog.scrollToPosition(logAdapter.data.size - 1)
                    }
                    rcvLog.setAdapter(logAdapter)
                    logChangeObserver.addObserver(logAdapter)

                    it.findViewById<ScaleImage>(R.id.ivScale).onScaledListener =
                            object : ScaleImage.OnScaledListener {
                                override fun onScaled(x: Float, y: Float, event: MotionEvent) {
                                    params.width = max(params.width + x.toInt(), 400)
                                    params.height = max(params.height + y.toInt(), 300)
                                    // 更新xml根布局的大小
//                            content.layoutParams = params
                                    // 更新悬浮窗的大小，可以避免在其他应用横屏时，宽度受限
                                    EasyFloat.updateFloat(LogFloatTag, width = params.width, height = params.height)
                                }
                            }

                    it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
                        closeLog()
                    }

                })
                .show()
    }


    /**
     * 关闭
     * 关闭日志打印
     */
    private fun closeLog() {
        EasyFloat.dismiss(LogFloatTag)
        LogCollector.getInstance(application).release()
    }

    /**
     * 开启捕获日志线程
     */
    private fun startLogThread() {
        LogCollector.getInstance(application)
                .setCleanCache(true)
                .setTag("OkHttp")
//            .setLevel(LevelUtils.D)
                .start()
    }

    /**
     * MyHandler
     */
    class MyHandler(activity: Activity) : Handler() {
        private var mWeakReference: WeakReference<Activity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val activity = mWeakReference.get()
            if (activity != null) {
                if (msg.what == 0x11) {     //log信息
                    val line = msg.obj as String
                    //将line添加到
                    loge(line ?: "")
                }
            }
        }

    }

}