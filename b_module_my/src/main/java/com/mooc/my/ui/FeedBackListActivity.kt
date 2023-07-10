package com.mooc.my.ui

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.NetUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.manager.WeChatPresenter
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.pop.my.UploadPicturePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.R
import com.mooc.my.adapter.FeedBackListAdapter
import com.mooc.my.databinding.ActivityFeedBackBinding
import com.mooc.my.databinding.ActivityFeedBackListBinding
import com.mooc.my.model.FeedBackListItemBean
import com.mooc.my.viewmodel.FeedBackViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.MimeType
import com.ypx.imagepicker.bean.SelectMode
import com.ypx.imagepicker.data.OnImagePickCompleteListener
import io.reactivex.functions.Consumer
//import kotlinx.android.synthetic.main.activity_feed_back_list.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 *反馈对话页面
 * @author limeng
 * @date 2020/10/26
 *
 * 暂时用不上
 */
@Route(path = Paths.PAGE_FEED_BACK_LIST)
class FeedBackListActivity : BaseActivity(), TextWatcher, SwipeRefreshLayout.OnRefreshListener {
    companion object {
        const val FEED_ID = "feed_id"
        const val REQUEST_PERMISSIONS_CODE = 100
        const val FLAG_CHOOSE_CAMERA = 102 // 相机
        const val FLAG_CHOOSE_PHOTO = 103 // 相册

    }

    var rxPermissions: RxPermissions? = null
    var picturePop: UploadPicturePop? = null
    var bitmapUri: Uri? = null
    private var path: String? = null
    private var feedbackId: String? = null
    private var replyId: String? = null
    var itemDataList = ArrayList<FeedBackListItemBean>()

//    val mViewModel: FeedBackViewModel by lazy {
//        ViewModelProviders.of(this)[FeedBackViewModel::class.java]
//    }

    val mViewModel: FeedBackViewModel by viewModels()
    private lateinit var inflate : ActivityFeedBackListBinding

    var feedId: String? = null
    var mfeedBackListAdapter: FeedBackListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = ActivityFeedBackListBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initView()
        initData()
        initListener()
        initDataListener()
    }


    private fun initView() {
        inflate.swipeLayout.setOnRefreshListener(this)
        rxPermissions = RxPermissions(this)
        mfeedBackListAdapter = FeedBackListAdapter(null)
        inflate.rcvFeedbackList.layoutManager = LinearLayoutManager(this)
        inflate.rcvFeedbackList.adapter = mfeedBackListAdapter

    }

    private fun initData() {
        feedId = intent.getStringExtra(FEED_ID)
        if (feedId.isNullOrEmpty()) finish()
        getData()

    }

    /**
     *获取反馈列表数据
     */
    fun getData() {
        mViewModel.getFeedBackList(feedId)
    }

    private fun initListener() {
        inflate.commonTitle.setOnLeftClickListener { finish() }
        //发送意见
        inflate.send.setOnClickListener {
            sendTxtMsg()
        }
        inflate.imgChoose.setOnClickListener {
            if (rxPermissions?.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true && rxPermissions?.isGranted(
                            Manifest.permission.CAMERA
                    )!!
            ) {
                openDialog()
            } else {
                rxPermissions?.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                        ?.subscribe(Consumer {
                            if (it) {
                                openDialog()
                            } else {
                                showPermissionDialog()
                            }
                        })
            }


        }

        inflate.input.addTextChangedListener(this)
    }

    fun openDialog() {
        picturePop = UploadPicturePop(this, inflate.imgChoose)
        picturePop?.show()
        picturePop?.onCameraClick = {//打开相机

            getCamera()
        }
        picturePop?.onPhotoClick = {//相册
            ImagePicker.withMulti(WeChatPresenter()) //指定presenter                                 //设置选择的最大数
                    .setMaxCount(1) //设置列数
                    .setColumnCount(4) //设置要加载的文件类型，可指定单一类型
                    .mimeTypes(MimeType.ofImage()) //设置需要过滤掉加载的文件类型
                    .filterMimeTypes(MimeType.GIF)
                    .setPreview(true) //开启预览
                    //大图预览时是否支持预览视频
                    .setSinglePickImageOrVideoType(true) //当单选或者视频单选时，点击item直接回调，无需点击完成按钮
                    .setSinglePickWithAutoComplete(false)
                    .setOriginal(true) //显示原图
                    //设置单选模，当maxCount==1时，可执行单选（下次选中会取消上一次选中）
                    .setSelectMode(SelectMode.MODE_SINGLE) //设置视频可选取的最大时长
                    .pick(this, OnImagePickCompleteListener {
                        upLoadImage(it[0].path)
                    })
        }
    }

    private fun showPermissionDialog() {
        val publicDialogBean = PublicDialogBean()
        publicDialogBean.strMsg = resources.getString(R.string.permission_file_read)
        publicDialogBean.strTv = resources.getString(R.string.text_ok)
        val dialog = PublicOneDialog(this, publicDialogBean)
        XPopup.Builder(this)
                .asCustom(dialog)
                .show()
    }

    private fun getCamera() {
        ImagePicker.takePhoto(
                this, SimpleDateFormat("yyyyMMddHHmmss")
                .format(Date()), true
        ) { items -> //剪裁回调，主线程
            loge(items[0].path)

            if (items.isNotEmpty()) {
                upLoadImage(items[0].path)
            }
        }
    }

    /**
     * 发送文本内容
     */
    fun sendTxtMsg() {
        val content: String = inflate.input.text.toString().trim()
        if (content.isEmpty()) {
            toast("发送内容不能为空")
            return
        }
        val imgs = ""
        mViewModel.sendFeedMsg(feedbackId, content, replyId, imgs)
    }

    private fun initDataListener() {
        //获取的反馈列表数据
        mViewModel.feedBackListBean.observe(this, Observer {
            inflate.swipeLayout.isRefreshing = false
//           mfeedBackListAdapter?.setNewInstance(it)
            //设置数据第一条为发送者 循环判断接受者条目
            feedbackId = it.feedback?.get(0)?.id
            replyId = it.feedback?.get(0)?.user_id
            itemDataList.clear()
            itemDataList.add(FeedBackListItemBean(FeedBackListAdapter.TYPE_SENDER))
            val feedUserId: String? = it.feedback?.get(0)?.user_id
            it.reply?.let {
                for (i in it) {
                    val userId = i.user_id
                    if (feedUserId.equals(userId)) {
                        itemDataList.add(FeedBackListItemBean(FeedBackListAdapter.TYPE_SENDER))
                    } else {
                        itemDataList.add(FeedBackListItemBean(FeedBackListAdapter.TYPE_RECIEVER))

                    }
                }
            }
            mfeedBackListAdapter?.setNewInstance(itemDataList)
            mfeedBackListAdapter?.backListBean = it
            inflate.rcvFeedbackList.scrollToPosition(itemDataList.size - 1)

        })
        mViewModel.sendFeedMsgBean.observe(this, Observer {
            if ("1".equals(it.code)) {
                //重新刷新当前页面
                getData()
                inflate.input.setText("")
            }
        })
        //图片返回数据
        mViewModel.uploadFileBean.observe(this, Observer {
//                BitmapTools.deleteDir(BitmapTools.getDirectoryPath())
            if (it.code == 200) {//上传图片地址
                val imgs = "[\"${it.data.url}\"]"
                mViewModel.sendFeedMsg(feedbackId, "", replyId, imgs)
            } else {
                if (TextUtils.isEmpty(it.msg)) {
                    toast(getString(R.string.upload_img_faile))
                } else {
                    toast(it.msg)
                }
            }
        })
        mViewModel.getError().observe(this, Observer {
            if (it is HttpException) {
                if (it.code() == 403) {
                    toast(getString(R.string.toast_upload_pic_too_large))
                }
            }

        })
    }


    /**
     * 上传图片
     */
    private fun upLoadImage(path: String) {
        if (NetUtils.isNetworkConnected()) {
            upLoadImage2Net(path)
        } else {
            toast(getString(R.string.net_error))
        }
    }

    private fun upLoadImage2Net(path: String) {
        if (path.isNullOrEmpty()) return
        val file = File(path)
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val toRequestBody = file.asRequestBody("image/*; charset=utf-8".toMediaTypeOrNull())
        builder.addFormDataPart("file", file.name, toRequestBody)
        mViewModel.postImageFile(builder.build())

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {
        if (!p0.toString().isNullOrEmpty()) {
            inflate.input.setBackgroundResource(R.drawable.shape_radius1_5_width0_5_ceolorf)
            inflate.send.setVisibility(View.VISIBLE)
            inflate.imgChoose.setVisibility(View.GONE)
        } else {
            inflate.input.setBackgroundResource(R.drawable.shape_corner1_5_color_f1)
            inflate.send.setVisibility(View.INVISIBLE)
            inflate.imgChoose.setVisibility(View.VISIBLE)
        }
    }

    override fun onRefresh() {
        getData()
    }

}