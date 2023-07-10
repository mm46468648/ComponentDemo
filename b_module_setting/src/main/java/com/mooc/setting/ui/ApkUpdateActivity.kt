package com.mooc.setting.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.KeyEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.*
import com.mooc.common.utils.Md5Util
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.setting.R
import com.mooc.setting.UpdateManager
import com.mooc.setting.databinding.SetActivityApkUpdateBinding
import com.mooc.setting.model.ApkUpgradeData
//import kotlinx.android.synthetic.main.set_activity_apk_update.*
import java.io.File

@Route(path = Paths.PAGE_APK_UPDATE)
class ApkUpdateActivity : AppCompatActivity() {

    private val REQUESTCODE_INSTALL_UNKNOW_PERMISSION = 0
    private val apkUpgradeData: ApkUpgradeData? by extraDelegate<ApkUpgradeData?>(
            UpdateManager.PARAMS_UPDATEBEAN,
            null
    )
//    private val fromHome: String? by extraDelegate(UpdateManager.PARAMS_FROMHOME,null)
//    lateinit var apkUpgradeData: ApkUpgradeData
    private lateinit var inflater : SetActivityApkUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UpdateManager.isUpdatePageShow = true
        inflater = SetActivityApkUpdateBinding.inflate(layoutInflater)
        setContentView(inflater.root)

//        apkUpgradeData = intent.getParcelableExtra(UpdateManager.PARAMS_UPDATEBEAN) as ApkUpgradeData
//        loge(fromHome?:"from也为空")

        apkUpgradeData?.apply {
            inflater.tvUpdateContent.text = Html.fromHtml(this.app_presentation)
            inflater.tvHead.text = "新版本来啦\nV  ${this.version_name}"
            inflater.tvApkSize.text = "安装包大小: ${this.app_size}M"

            if (!app_force_upgrade) {    //如果不是强制升级,显示两个按钮
                inflater.btnUpdate.visibility = View.GONE
                inflater.btnUpdateCancel.visibility = View.VISIBLE
                inflater.btnUpdateOk.visibility = View.VISIBLE
            }
        }

        inflater.btnUpdateCancel.setOnClickListener {  //忽略此版本更新，从首页进来不弹弹窗
            SpDefaultUtils.getInstance().putString(
                    SpConstants.IGNORE_UPDATE_APK_VERSION,
                    apkUpgradeData?.version_code.toString()
            )
            finish()
        }
        inflater.btnUpdate.setOnClickListener(onClickUpdate)
        inflater.btnUpdateOk.setOnClickListener(onClickUpdate)
    }

    val onClickUpdate = View.OnClickListener {
        if (apkUpgradeData == null) {
//            finish()
            return@OnClickListener
        }
        clickUpdate()
    }

    private fun clickUpdate() {
        //8.0以上检查是否允许未知应用权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !this@ApkUpdateActivity.packageManager.canRequestPackageInstalls()) {
            toast("请允许安装未知应用权限")
            startInstallPermissionSettingActivity()
            return
        }

        if (checkApkIsDownloaded()) {
            installAPK()
            return
        }
        bottomShow(true)
        //立即更新,下载apk
        downloadApk()
    }

    /**
     * 底部显示控制
     * @param showProgress 是否是展示进度条
     * @param updateText 默认升级，如果已下载完毕显示安装
     */
    fun bottomShow(showProgress: Boolean, updateText: String = "升级") {
        if (showProgress) {
            inflater.llBottom.visibility = View.GONE
            inflater.progressBar.visibility = View.VISIBLE
            inflater.tvProgress.visibility = View.VISIBLE
            return
        }

        inflater.llBottom.visibility = View.VISIBLE
        inflater.progressBar.visibility = View.GONE
        inflater.tvProgress.visibility = View.GONE
        inflater.btnUpdate.setText(updateText)
        inflater.btnUpdateOk.setText(updateText)
    }

    /**
     * 下载安装包
     */
    private fun downloadApk() {
        if (checkApkIsDownloaded()) {
            installAPK()
            return
        }
        UpdateManager.downloadApk(
                apkUpgradeData?.version_code.toString(), apkUpgradeData?.app_url
                ?: ""
        ) {
            updateUi(it)
        }
    }

    /**
     * 更新下载ui
     */
    private fun updateUi(progress: Int) {
        runOnMain {
            if (progress == UpdateManager.PROGRESS_FAIL) {
                toast("下载失败")
                bottomShow(false)
                return@runOnMain
            }
            if (progress == UpdateManager.PROGRESS_FAIL_SSL) {
                toast("下载地址异常，SSL校验不通过")
                bottomShow(false)
                return@runOnMain
            }

            if (progress == 100) { //下载完毕，安装
                bottomShow(false, "安装")
                installAPK()
            }
            inflater.progressBar.progress = progress
            inflater.tvProgress.text = "$progress%"

            var m = progress.toFloat() / 100 * inflater.progressBar.width
            val i = inflater.progressBar.width - inflater.tvProgress.width
            if (m > i) {
                m = i.toFloat()
            }
            inflater.tvProgress.translationX = m     //下载进度跟着向前平移


        }
    }


    /**
     * 检查是否已经下载过，
     * 有可能点了下载，但是没有安装
     */
    private fun checkApkIsDownloaded(): Boolean {
        val downloadFileName = "app_moo_${apkUpgradeData?.version_code}.apk"
        val filePath = DownloadConfig.apkLocation + "/" + downloadFileName
        val file = File(filePath)
        //apk已经下载，且文件md5和服务器md5一致返回已下载
        if (file.exists() && apkUpgradeData?.apk_md5 == Md5Util.getFileMD5(file)) {
            return true
        }

        return false
    }

    /**
     * 安装APK
     */
    fun installAPK() {
        val downloadFileName = "app_moo_${apkUpgradeData?.version_code}.apk"
        val filePath = DownloadConfig.apkLocation + "/" + downloadFileName

        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.addCategory("android.intent.category.DEFAULT")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri: Uri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.fileProvider",
                    File(filePath)
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setDataAndType(
                    Uri.fromFile(File(filePath)),
                    "application/vnd.android.package-archive"
            )
        }
        startActivity(intent)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity() {
        val packageURI = Uri.parse("package:$packageName")
        //注意这个是8.0新API
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        startActivityForResult(intent, REQUESTCODE_INSTALL_UNKNOW_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESTCODE_INSTALL_UNKNOW_PERMISSION) {
            clickUpdate()
        }
    }


    //强制升级不允许返回关闭
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (apkUpgradeData?.app_force_upgrade == true) {
                return false
            } else {
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()

        UpdateManager.isUpdatePageShow = false
    }
}