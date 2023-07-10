package com.mooc.my.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.toastOnce
import com.mooc.common.utils.permission.PermissionRequestCallback
import com.mooc.common.utils.permission.PermissionUtil
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.WeChatPresenter
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.pop.my.UploadPicturePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.sp.SPUserUtils
import com.mooc.my.R
import com.mooc.my.databinding.ActivityUserInfoBinding
import com.mooc.my.databinding.SetActivityUserinfoEditBinding
import com.mooc.my.viewmodel.UserInfoEditViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.MimeType
import com.ypx.imagepicker.bean.selectconfig.CropConfig
import com.ypx.imagepicker.data.OnImagePickCompleteListener
import com.ypx.imagepicker.presenter.IPickerPresenter
//import kotlinx.android.synthetic.main.set_activity_userinfo_edit.*


@Route(path = Paths.PAGE_USERINFO_EDIT)
class UserInfoEditActivity : BaseActivity() {




//    val mViewModel by lazy {
//        ViewModelProviders.of(this)[UserInfoEditViewModel::class.java]
//    }
    val mViewModel by viewModels<UserInfoEditViewModel>()
    private lateinit var inflate : SetActivityUserinfoEditBinding


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = SetActivityUserinfoEditBinding.inflate(layoutInflater)
        setContentView(inflate.root)

        inflate.llUserHead.setOnClickListener {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                && ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED) {
//                toast(getString(R.string.permission_with_camera))
//                return@setOnClickListener
//            }
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)
//                && ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED) {
//                toast(getString(R.string.permission_with_camera))
//                return@setOnClickListener
//            }
//            //检测摄像头权限
//            if(!PermissionUtil.hasPermissionRequest(this,
//                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//                PermissionApplyActivity.launchActivity(
//                    this,
//                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    0,
//                    object : PermissionRequestCallback {
//                        override fun permissionSuccess() {
//                            showPictureChooseDialog()
//                        }
//
//                        override fun permissionCanceled() {
//                        }
//
//                        override fun permissionDenied() {
//
//                        }
//                    })
//                return@setOnClickListener
//            }
//            showPictureChooseDialog()

            val rxPermissions = RxPermissions(this)
            if (rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) && rxPermissions.isGranted(
                    Manifest.permission.CAMERA
                )
            ) {
                showPictureChooseDialog()
            } else {
                rxPermissions.requestEach(
                    Manifest.permission.CAMERA
                )
                    ?.subscribe {
                        if(it.granted){
                            rxPermissions.requestEach(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                ?.subscribe { second->
                                    if(second.granted){
                                        showPictureChooseDialog()
                                    }else if(second.shouldShowRequestPermissionRationale){

                                    }else{
                                        toastOnce(getString(R.string.permission_with_camera))
                                    }
                                }
                        }else if(it.shouldShowRequestPermissionRationale){

                        }else{
                            toastOnce(getString(R.string.permission_with_camera))
                        }
                    }
            }
        }
        //设置返回健
        inflate.commonTitle.setOnLeftClickListener {
            finish()
        }

        //监听用户信息改变
        LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_USERINFO_CHANGE)
            .observe(this, Observer<UserInfo> {
                loge("${this::class.java.simpleName}收到了用户信息改变")
                updateUserInfo(it)
            })


        //初始化数据
        GlobalsUserManager.userInfo?.apply {
            inflate.etNickname.setText(this.name)
            inflate.mivUesrHead.setImageUrl(this.avatar?:"",true)
        }


        //点击的时候显示光标
        inflate.etNickname.setOnClickListener {
//            etNickname.isCursorVisible = true
            showNimeEditDialog()
        }
        //点击完成
//        etNickname.setOnEditorActionListener { textView, i, keyEvent ->
//            if (i == EditorInfo.IME_ACTION_DONE) {
//                postUserName(textView.text.toString())
//                etNickname.isCursorVisible = false
//            }
//            return@setOnEditorActionListener false
//        }
    }

    /**
     * 更新用户信息
     */
    fun updateUserInfo(userInfo: UserInfo?) {

        userInfo?.apply {
            inflate.etNickname.text = this.name
        }


    }

    fun showNimeEditDialog(){

        ARouter.getInstance().build(Paths.PAGE_USERINFO_NAME)
            .navigation()

//        XPopup.Builder(this)
//            .asInputConfirm("设置昵称", "",name,"",
//            object : OnInputConfirmListener {
//                override fun onConfirm(text: String) {
////                    toast("input text: $text")
//                    postUserName(text.toString())
//                }
//            })
//            .show()
    }

    /**
     * 展示图片选择弹窗
     */
    private fun showPictureChooseDialog() {
        val uploadPicturePop = UploadPicturePop(this, inflate.llUserHead)
        uploadPicturePop.onCameraClick = {
            takePhoto()
        }
        uploadPicturePop.onPhotoClick = {
            singlePick()
        }
        uploadPicturePop.show()
    }

    /**
     * 单张选择
     */
    private fun singlePick() {
        ImagePicker.withMulti(WeChatPresenter())
                .mimeTypes(MimeType.ofImage())
                .filterMimeTypes(MimeType.GIF) //剪裁完成的图片是否保存在DCIM目录下
                //true：存储在DCIM下 false：存储在 data/包名/files/imagePicker/ 目录下
                .cropSaveInDCIM(false) //设置剪裁比例
                .setCropRatio(1, 1) //设置剪裁框间距，单位px
                .cropRectMinMargin(50) //是否圆形剪裁，圆形剪裁时，setCropRatio无效
                .cropAsCircle() //设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
                .cropStyle(CropConfig.STYLE_FILL) //设置留白模式下生成的图片背景色，支持透明背景
                .cropGapBackgroundColor(Color.TRANSPARENT)
                .crop(this, OnImagePickCompleteListener {
                    //图片剪裁回调，主线程
                    loge(it[0].cropUrl)

                    if(it.isNotEmpty()){
                        postUserHead(it[0].cropUrl)
                    }
                })
    }

    fun postUserName(userName : String){
        if(userName.isEmpty()){
            toast("昵称不能为空")
            return
        }
        //如果没变也不发送
        if(userName == GlobalsUserManager.userInfo?.name){
            return
        }
        mViewModel.postUserInfo(userName).observe(this, Observer {
            if(it.isSuccess){
                //将最新名字
                GlobalsUserManager.userInfo?.name = userName
                SPUserUtils.getInstance().saveUserInfo(GlobalsUserManager.userInfo)
                LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_USERINFO_CHANGE).postValue(GlobalsUserManager.userInfo)
                inflate.etNickname.setText(userName)
            }else{
                toast(it.msg)
            }
        })
    }

    fun postUserHead(fileUri : String){
        loge(fileUri)

        val createLoadingDialog = CustomProgressDialog.createLoadingDialog(this)
        createLoadingDialog.show()
        mViewModel.postUserHead(fileUri).observe(this, Observer {
            createLoadingDialog.dismiss()
            if(it == null) return@Observer
            inflate.mivUesrHead.setImageUrl(fileUri)
            //将最新图片地址保存
            GlobalsUserManager.userInfo?.avatar = it.url
            SPUserUtils.getInstance().saveUserInfo(GlobalsUserManager.userInfo)
            LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_USERINFO_CHANGE).postValue(GlobalsUserManager.userInfo)
        })
    }
    /**
     * 拍照并剪裁
     */
    private fun takePhoto(){
        //配置剪裁属性
        val cropConfig = CropConfig()
        cropConfig.setCropRatio(1, 1) //设置剪裁比例

        cropConfig.cropRectMargin = 100 //设置剪裁框间距，单位px

        cropConfig.isCircle = true //是否圆形剪裁
        //设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
        cropConfig.cropStyle = CropConfig.STYLE_FILL
        cropConfig.cropGapBackgroundColor = Color.TRANSPARENT

        val presenter: IPickerPresenter = WeChatPresenter()
        ImagePicker.takePhotoAndCrop(this, presenter, cropConfig) { items -> //剪裁回调，主线程
            loge(items[0].cropUrl)

            if(items.isNotEmpty()){
                postUserHead(items[0].cropUrl)
            }
        }
    }
}