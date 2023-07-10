package com.mooc.setting.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.FileMgr
import com.mooc.common.utils.SDUtils
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.common.utils.sharepreference.SpUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.utils.DebugUtil
import com.mooc.setting.R
import com.mooc.setting.databinding.SetActivityMainBinding
//import kotlinx.android.synthetic.main.set_activity_main.*

@Route(path = Paths.PAGE_SETTING)
class SettingActivity : BaseActivity() {


    companion object {
        const val REQUEST_CODE_CHOOSE_DOWNLOAD_PATH = 0
    }

    private lateinit var inflater : SetActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = SetActivityMainBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        initDownloadPathData()
        initListener()
    }

    /**
     * 设置下载路径数据
     */
    private fun initDownloadPathData() {

        //从sp中读取，默认的存储路径
        val value = SpUtils.get().getValue(SpConstants.DEFAULT_DOWNLOAD_LOCATION, DownloadConfig.defaultLocation)
        setDownloadPath(value != DownloadConfig.defaultLocation, value)
        //获取当前手机剩余空间
        val bytes: LongArray = SDUtils.getDirSize(getExternalFilesDir(null)?.absolutePath ?: "")
        inflater.tvStoreSize.setText("剩余${FileMgr.FormetFileSize(bytes[0])}/总共${FileMgr.FormetFileSize(bytes[1])}")

    }

    private fun initListener() {
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }

        //点击进入开发者模式
//        viewHiddenDor.setOnClickListener {
//            clickHiddenDor()
//        }

        //进入消息设置
        inflater.msgSetting.setOnClickListener {
            if (GlobalsUserManager.isLogin()) {
                ARouter.getInstance().build(Paths.SERVICE_SETTING_MSG).navigation()
            } else {
                ResourceTurnManager.turnToLogin()
            }
        }
        //隐私设置
        inflater.privacySetting.setOnClickListener {
            if (GlobalsUserManager.isLogin()) {
                ARouter.getInstance().build(Paths.SERVICE_SETTING_PRIVACY).navigation()
            } else {
                ResourceTurnManager.turnToLogin()
            }

        }

        if (DebugUtil.debugMode) {
            inflater.csiController.visibility = View.VISIBLE
        }

        //点击进入控制台
        inflater.csiController.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_CONTROLLER).navigation()
        }

        //进入下载二维码页面
        inflater.csiQrDownload.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_DOWNLOAD_QR).navigation()
        }

        //点击进入关于我们
        inflater.csiAbout.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_ABOUT).navigation()
        }

        //点击进入平台操作指南
        inflater.csiOperateGuide.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_WEB)
                    .with(Bundle().put(IntentParamsConstants.WEB_PARAMS_TITLE, "平台操作指南").put(IntentParamsConstants.WEB_PARAMS_URL, UrlConstants.HELP_DOCUMENT_URL))
                    .navigation()
        }


        //初始化摇一摇反馈开关
        var boolean = SpDefaultUtils.getInstance().getBoolean(SpConstants.SHAKE_FEEDBACK, false)
        setShakeOpenRes(boolean)
        inflater.csiShakeFeedback.setRightTextClickFunction {
            boolean = !boolean
            //设置资源，并同步sp
            setShakeOpenRes(boolean)
            SpDefaultUtils.getInstance().putBoolean(SpConstants.SHAKE_FEEDBACK, boolean)
        }

        //初始化仅wifi下载开关
        var onlyWifiDownload = SpDefaultUtils.getInstance().getBoolean(SpConstants.ONLY_WIFI_DOWNLOAD, true)
        setOnlyWifiDownloadRes(onlyWifiDownload)
        inflater.csiOnlyWifiDownload.setRightTextClickFunction {
            onlyWifiDownload = !onlyWifiDownload
            //设置资源，并同步sp
            setOnlyWifiDownloadRes(onlyWifiDownload)
            SpDefaultUtils.getInstance().putBoolean(SpConstants.ONLY_WIFI_DOWNLOAD, onlyWifiDownload)
        }

        //设置下载路径点击
        inflater.clDownloadPathSetting.setOnClickListener {
            //使用分区存储框架
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                toast("Android 10 以上暂不支持更改")
                return@setOnClickListener
            }
            ARouter.getInstance().build(Paths.PAGE_DOWNLOAD_PATH_CHOOSE).navigation(this, REQUEST_CODE_CHOOSE_DOWNLOAD_PATH)
        }
    }

    val counts: Int = 16
    val duration: Long = 3 * 1000
    val mHits = LongArray(counts)

    /**
     * 点击暗门，开启调试模式
     */
    private fun clickHiddenDor() {

        /**
         * 实现双击方法
         * src 拷贝的源数组
         * srcPos 从源数组的那个位置开始拷贝.
         * dst 目标数组
         * dstPos 从目标数组的那个位子开始写数据
         * length 拷贝的元素的个数
         */
        System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1)
        //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
        mHits[mHits.size - 1] = SystemClock.uptimeMillis()
        if (mHits[0] >= (SystemClock.uptimeMillis() - duration)) {
            val tips = "您已在[" + duration + "]ms内连续点击【" + mHits.size + "】次,进入开发者模式"
            toast(tips)
            inflater.csiController.visibility = View.VISIBLE
            DebugUtil.debugMode = true
        }
    }


    /**
     * 设置摇一摇开关图片资源
     */
    fun setShakeOpenRes(open: Boolean) {
        val openRes = if (open) R.mipmap.set_ic_switch_open else R.mipmap.set_ic_switch_close
        inflater.csiShakeFeedback.rightIcon = openRes

    }

    /**
     * 设置仅wifi下载图片资源
     */
    fun setOnlyWifiDownloadRes(open: Boolean) {
        val openRes = if (open) R.mipmap.set_ic_switch_open else R.mipmap.set_ic_switch_close
        inflater.csiOnlyWifiDownload.rightIcon = openRes

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_DOWNLOAD_PATH) {

            val setCustomChoose = data?.getBooleanExtra(IntentParamsConstants.SET_CUSTOM_CHOOSE, false)
                    ?: false
            val setCustomPath = data?.getStringExtra(IntentParamsConstants.SET_CUSTOM_PATH)
                    ?: DownloadConfig.defaultLocation
            //保存自定义的目录
            SpUtils.get().putValue(SpConstants.DEFAULT_DOWNLOAD_LOCATION, setCustomPath)
            setDownloadPath(setCustomChoose, setCustomPath)
        }
    }

    /**
     * 设置下载路径
     * @param custom 是否是自定义的路径
     * @param path 路径
     */
    private fun setDownloadPath(custom: Boolean, path: String) {

        if (!custom) { //默认，手机存储器
            inflater.tvStoreType.setText("手机存储器")
            inflater.tvStorePath.setText("文件路径:${DownloadConfig.defaultLocation}")
        } else {
            inflater.tvStoreType.setText("自定义目录")
            inflater.tvStorePath.setText("文件路径:${path}")
//            DownloadConfig.videoLocation = setCustomPath
        }
    }
}
