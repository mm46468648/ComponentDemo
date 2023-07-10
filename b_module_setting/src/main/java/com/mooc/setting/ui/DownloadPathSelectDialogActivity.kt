package com.mooc.setting.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.setting.R
import com.mooc.setting.fileselector.FileSelectConstant
import com.mooc.setting.fileselector.FsActivity
import com.mooc.commonbusiness.model.set.PathStoreBean
import com.mooc.commonbusiness.model.set.PatshStore
import com.google.gson.Gson
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.route.Paths
import com.mooc.setting.databinding.SettingActivityDownloadPathChooseBinding
import com.tbruyelle.rxpermissions2.RxPermissions
//import kotlinx.android.synthetic.main.setting_activity_download_path_choose.*

/**
 * 下载路径选择弹窗
 */
@Route(path = Paths.PAGE_DOWNLOAD_PATH_CHOOSE)
class DownloadPathSelectDialogActivity : BaseActivity() {

    companion object {
        const val FILE_SELECTOR_REQUEST_CODE = 0
    }

    val defaultFilePaths by lazy {    //默认的存储路径
        getExternalFilesDir(null)?.absolutePath?:""
    }

    var customStorePath = ""    //默认是自定义目录
    private lateinit var inflater : SettingActivityDownloadPathChooseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = SettingActivityDownloadPathChooseBinding.inflate(layoutInflater)
        initWindow()
        setContentView(inflater.root)


        inflater.tvOk.setOnClickListener {
            //先存储选项
            savePathChoose()

            val path = if(inflater.ivCustomStoreSelect.isSelected) customStorePath else defaultFilePaths
            //同步更改defaultPath
            DownloadConfig.resetDefault(path)
            //将路径返回到上一级
            val intent = Intent()
            intent.putExtra(IntentParamsConstants.SET_CUSTOM_PATH,path)
            intent.putExtra(IntentParamsConstants.SET_CUSTOM_CHOOSE,inflater.ivCustomStoreSelect.isSelected)
            setResult(Activity.RESULT_OK,intent)
            //结束页面
            finish()
        }

        //点击获取自定义存储路径
        inflater.tvCustomFilePath.setOnClickListener {
            chooseCustomFilePath()
        }

        //选中默认存储
        inflater.ivPhoneStoreSelect.setOnClickListener {
            if(!it.isSelected){
                setChooseDeatult(true)
            }
        }

        //选中自定义存储
        inflater.ivCustomStoreSelect.setOnClickListener {
            if(!it.isSelected){
                setChooseDeatult(false)
            }
        }

        //初始化默认值
        customStorePath = defaultFilePaths
        inflater.ivPhoneStoreSelect.isSelected = true

        //设置默认存储路径
        inflater.tvDefaultFilepath.setText("文件路径${defaultFilePaths}")

        val defaultJson = SpDefaultUtils.getInstance().getString(SpConstants.DEFAULT_DOWNLOAD_LOCATION_JSON, "")
        val gson = Gson()
        val pathStoreBean = gson.fromJson(defaultJson, PathStoreBean::class.java)
        if (pathStoreBean?.storageBeans != null && pathStoreBean.storageBeans.size > 1) {
            val patshStore = pathStoreBean.storageBeans[1]
            if(patshStore.checked){
                customStorePath = patshStore.path
                setChooseDeatult(!patshStore.checked)
            }


        }


    }

    private fun savePathChoose() {
        //构建序列号对象
        val arrayList = ArrayList<PatshStore>()
        val patshDefaultStore = PatshStore(inflater.ivPhoneStoreSelect.isSelected, "手机存储器", defaultFilePaths)
        val patshCustomStore = PatshStore(inflater.ivCustomStoreSelect.isSelected, "自定义目录", customStorePath.trim())
        arrayList.add(patshDefaultStore)
        arrayList.add(patshCustomStore)
        val pathStoreBean = PathStoreBean(arrayList)
        //json
        val gson = Gson()
        SpDefaultUtils.getInstance().putString(SpConstants.DEFAULT_DOWNLOAD_LOCATION_JSON,gson.toJson(pathStoreBean).toString())
    }


    val rxPermissions by lazy {
        RxPermissions(this)
    }

    /**
     * 选择自定义路径
     */
    private fun chooseCustomFilePath() {
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted: Boolean ->
                    if (granted) {
                        //跳转到文件路径选择页面
                        val intent = Intent()
                        intent.setClass(applicationContext, FsActivity::class.java)
                        intent.putExtra(FileSelectConstant.SELECTOR_REQUEST_CODE_KEY, FileSelectConstant.SELECTOR_MODE_FOLDER)
                        startActivityForResult(intent, FILE_SELECTOR_REQUEST_CODE)
                    } else {
                        // At least one permission is denied
                    }
                }

    }

    /**
     * 初始化window
     * 使显示在底部，并充满屏幕宽
     */
    private fun initWindow() {
        //设置布局在底部
        window.setGravity(Gravity.CENTER)
        //设置布局填充满宽度
        val layoutParams: WindowManager.LayoutParams = window.attributes
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = layoutParams
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == FILE_SELECTOR_REQUEST_CODE) {
            val stringArrayListExtra = data?.getStringArrayListExtra(FileSelectConstant.SELECTOR_BUNDLE_PATHS)
            loge(stringArrayListExtra ?: "")
            //赋值
            customStorePath = stringArrayListExtra?.get(0)?:defaultFilePaths
            inflater.tvCustomFilePath.setText("文件路径: ${customStorePath}")
            setChooseDeatult(false)
        }
    }

    /**
     * 设置选中默认
     * @param default 是否是默认文件夹
     */
    fun setChooseDeatult(default: Boolean) {
        inflater.ivPhoneStoreSelect.isSelected = default
        inflater.ivCustomStoreSelect.isSelected = !default

        if(default){
            inflater.tvCustomFilePath.setText("选择路径:")
        }else{
            //设置sp中的自定义存储路径
            inflater.tvCustomFilePath.setText("文件路径:${customStorePath}")
        }
    }
}