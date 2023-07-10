package com.mooc.my.ui

//import com.example.commonbusiness.modle.my.PhotoPreviewActivity.Companion.EXTRA_PREVIEW_PHOTOS
//import cc.shinichi.library.ImagePreview
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.NetUtils
import com.mooc.common.utils.RegexUtil
import com.mooc.commonbusiness.adapter.FeedbackImgAdapter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.BigImagePreview
import com.mooc.commonbusiness.manager.WeChatPresenter
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.R
import com.mooc.my.adapter.FeedBackTypeAdapter
import com.mooc.my.databinding.ActivityCustomerServiceBinding
import com.mooc.my.databinding.ActivityFeedBackBinding
import com.mooc.my.viewmodel.FeedBackViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.MimeType
import com.ypx.imagepicker.bean.SelectMode
import com.ypx.imagepicker.data.OnImagePickCompleteListener
import io.reactivex.functions.Consumer
//import kotlinx.android.synthetic.main.activity_feed_back.*
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 *意见反馈内容编辑页面
 * @author limeng
 * @date 2020/10/16
 */
@Route(path = Paths.PAGE_FEED_BACK)
class FeedBackActivity : BaseActivity() {
    private var rxPermissions: RxPermissions? = null
    var mFeedbackImgAdapter: FeedbackImgAdapter? = null
    var mFeedBackTypeAdapter: FeedBackTypeAdapter? = null
    var imageList = ArrayList<String>()
    var bitmapUri: Uri? = null
    private var type = -1

//    val mViewModel: FeedBackViewModel by lazy {
//        ViewModelProviders.of(this)[FeedBackViewModel::class.java]
//    }
    val mViewModel: FeedBackViewModel by viewModels()
    private lateinit var inflate : ActivityFeedBackBinding

    companion object {
        const val SELECT_PIC = 100
        const val REQUEST_CODE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate = ActivityFeedBackBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initView()
        initData()
        initListener()
        initDataListener()

//        LogUtil.addLoadLog(LogPageConstants.PID_FEEDBACK)
    }

    /**
     * 所有数据获得结果处理
     */
    @SuppressLint("SetTextI18n")
    private fun initDataListener() {
        //接受反馈类型
        mViewModel.mFeedBackBeann.observe(this, Observer {
            if (!it.phone_num.isNullOrEmpty()) {
                inflate.activityFeedbackPhone.setText(it.phone_num)
                it.results?.get(0)?.isCheck = true
                mFeedBackTypeAdapter?.setNewInstance(it.results)
            }
        })
        //反馈结果
        mViewModel.mFeedBean.observe(this, Observer {
            if (it != null) {
                if ("200".equals(it.status)) {
                    toast(getString(R.string.feedback_submit_suc))
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    toast(getString(R.string.feedback_submit_fail))
                }
            }
        })

        mViewModel.uploadFileBean.observe(this, Observer {
            if (it.code == 200) {
                if (it.data == null) return@Observer
                if (imageList.size > 0) {
                    imageList.remove("")
                    imageList.add(it.data.url!!)
                    imageList.add("")
                    inflate.tvImgCount.setText((imageList.size - 1).toString() + "/4")
                    if (imageList.size > 4) {
                        imageList.remove("")
                    }
                    mFeedbackImgAdapter?.setNewInstance(imageList)
                }
            } else {
                if (!TextUtils.isEmpty(it.msg)) {
                    toast(it.msg)
                } else {
                    toast(getString(R.string.upload_img_faile))
                }
            }
        })
    }

    private fun initView() {
        rxPermissions = RxPermissions(this)
        inflate.activityFeedbackContent.setFilters(arrayOf<InputFilter>(LengthFilter(200)))

        imageList.clear()
        imageList.add("")//先添加一个空图片作为点击加图
        mFeedbackImgAdapter = FeedbackImgAdapter(imageList)
        mFeedBackTypeAdapter = FeedBackTypeAdapter(null)
        inflate.rcvFeedbackImg.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        inflate.rcvFeedbackImg.adapter = mFeedbackImgAdapter
        //摇一摇进来的页面处理
        if (intent.hasExtra("intent_key_feed_back")) {
            val filePath = intent.getStringExtra("intent_key_feed_back") ?: ""
            upLoadImage(filePath)
        }
        inflate.rcyFeedType.layoutManager = LinearLayoutManager(this)
        inflate.rcyFeedType.adapter = mFeedBackTypeAdapter

    }

    /**
     * 上传图片到云获取地址
     */
    private fun upLoadImage(path: String) {
        if (NetUtils.isNetworkConnected()) {
            upLoadImage2Net(path)
        } else {
            toast(getString(R.string.net_error))
        }
    }

    private fun initData() {
        getFeedType()
    }

    /**
     * 获取反馈类型
     */
    private fun getFeedType() {

        mViewModel.getFeedType()
    }

    private fun selectImage() {


    }

    @SuppressLint("SetTextI18n")
    private fun initListener() {
        mFeedbackImgAdapter?.addChildClickViewIds(R.id.imageView)
        mFeedbackImgAdapter?.addChildClickViewIds(R.id.img_del)
        mFeedbackImgAdapter?.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.imageView -> {
                    val url = mFeedbackImgAdapter?.data?.get(position)
                    if (!url.isNullOrEmpty()) {//点击查看大图

//                        ImagePreview
//                                .getInstance()
//                                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好；
//                                .setContext(this)
//                                .setShowDownButton(false)//关闭下载按钮
//                                // 设置从第几张开始看（索引从0开始）
//                                .setIndex(position)
//                                // 2：直接传url List
//                                .setImageList(imageList)
//
//                                // 3：只有一张图片的情况，可以直接传入这张图片的url
//
//                                // 开启预览
//                                .start();

                        BigImagePreview
                                .setImageList(imageList)
                                .setPosition(position)
                                .start()

                    } else {//选择添加图片
                        if (rxPermissions?.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true && rxPermissions?.isGranted(Manifest.permission.CAMERA)!!) {
                            openDialog()
                        } else {
                            rxPermissions?.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                                    ?.subscribe(Consumer {
                                        if (it) {
                                            openDialog()
                                        } else {
                                            showPermissionDialog()
                                        }
                                    })
                        }
                    }


                }
                R.id.img_del -> {//删除单个图片
                    imageList.removeAt(position)
                    imageList.remove("")
                    imageList.add("")
                    inflate.tvImgCount.setText((imageList.size - 1).toString() + "/4")
                    mFeedbackImgAdapter?.setNewInstance(imageList)
                }

            }
        }

        mFeedBackTypeAdapter?.addChildClickViewIds(R.id.cb_feed_type)
        mFeedBackTypeAdapter?.setOnItemChildClickListener { adapter, view, position ->
            //单条意见被选中
            mFeedBackTypeAdapter?.data?.forEach {
                it.isCheck = false
            }
            mFeedBackTypeAdapter?.data?.get(position)?.isCheck = true
            mFeedBackTypeAdapter?.notifyDataSetChanged()
        }
        //提交反馈信息
        inflate.activityFeedbackSubmit.setOnClickListener {
            val content: String = inflate.activityFeedbackContent.text.toString().trim()
            val phone: String = inflate.activityFeedbackPhone.text.toString().trim()

            type = getType()
            if (type == -1) {
                toast(getString(R.string.feed_title_empty))
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(content)) {
                toast(getString(R.string.feedback_content_empty))
                return@setOnClickListener

            }

            if (!TextUtils.isEmpty(phone)) {
                if (!RegexUtil.isMobileNO(phone)) {
                    toast(getString(R.string.text_regex_phone))
                    return@setOnClickListener
                }
            }

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inflate.activityFeedbackSubmit.getWindowToken(), 0)
            feedBackToNet(type, content, phone)
        }

        inflate.activityFeedbackContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                inflate.tvContentCount.setText(s.length.toString() + "/200")
            }
        })
        inflate.activityFeedbackContent.setOnFocusChangeListener({ v, hasFocus ->
            if (hasFocus) {
                inflate.activityFeedbackContent.setBackgroundResource(R.drawable.shape_radius1_5_width0_5_ceolorf)
                inflate.activityFeedbackContent.setTextColor(resources.getColor(R.color.color_3))
            } else {
                if (!TextUtils.isEmpty(inflate.activityFeedbackPhone.getText().toString())) {
                    inflate.activityFeedbackContent.setBackgroundResource(R.drawable.shape_radius1_5_width0_5_ceolorf)
                    inflate.activityFeedbackContent.setTextColor(resources.getColor(R.color.color_3))
                } else {
                    inflate.activityFeedbackContent.setBackgroundResource(R.drawable.shape_radius1_5_color_f1)
                }
            }
        })
        inflate.activityFeedbackPhone.setOnFocusChangeListener({ v, hasFocus ->
            if (hasFocus) {
                inflate.activityFeedbackPhone.setBackgroundResource(R.drawable.shape_radius1_5_width0_5_ceolorf)
                inflate.activityFeedbackPhone.setTextColor(resources.getColor(R.color.color_3))
            } else {
                if (!TextUtils.isEmpty(inflate.activityFeedbackPhone.getText().toString())) {
                    inflate.activityFeedbackPhone.setBackgroundResource(R.drawable.shape_radius1_5_width0_5_ceolorf)
                    inflate.activityFeedbackPhone.setTextColor(resources.getColor(R.color.color_3))
                } else {
                    inflate.activityFeedbackPhone.setBackgroundResource(R.drawable.shape_radius1_5_color_f1)
                }
            }
        })
    }

    fun openDialog() {
        //相册
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

    private fun showPermissionDialog() {
        val publicDialogBean = PublicDialogBean()
        publicDialogBean.strMsg = resources.getString(R.string.permission_file_read)
        publicDialogBean.strTv = resources.getString(R.string.text_ok)
        val dialog = PublicOneDialog(this, publicDialogBean)
        XPopup.Builder(this)
                .asCustom(dialog)
                .show()
    }

    private fun upLoadImage2Net(path: String) {
        if (path.isEmpty()) return
        val file = File(path)
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val toRequestBody = file.asRequestBody("image/*; charset=utf-8".toMediaTypeOrNull())
//        builder.addFormDataPart("file", file.name, toRequestBody)
        builder.addPart(Headers.headersOf("Content-Disposition", "form-data; name=\"" + "err_img" + "\"; filename =\"" + file.name), toRequestBody)

        mViewModel.postImageFile(builder.build())

    }


    /**
     * 获取反馈类型
     */
    private fun getType(): Int {
        val data = mFeedBackTypeAdapter?.data
        data.let {
            if (data != null) {
                for (i in data) {
                    if (i.isCheck) {
                        return i.question_type?.toInt() ?: 0
                    } else {
                        type = -1
                    }
                }
            } else {
                type = -1
            }
        }
        return -1
    }

    /**
     * 提交反馈信息
     */
    fun feedBackToNet(type: Int, content: String, contact: String) {
        if (TextUtils.isEmpty(contact) && TextUtils.isEmpty(content) && imageList.size == 0) {
            return
        }
        if (!NetUtils.isNetworkConnected()) {
            toast(getString(R.string.net_error))
            return
        }
        val requestData = JSONObject()
        try {
            requestData.put("question_type", type.toString())
            requestData.put("content", content)
            requestData.put("contact", contact)
            requestData.put("img_url", Gson().toJson(mFeedbackImgAdapter?.data))
            requestData.put("user", GlobalsUserManager.uid)
            requestData.put("back_origin", "1")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        mViewModel.postFeedback(stringBody)
    }
}