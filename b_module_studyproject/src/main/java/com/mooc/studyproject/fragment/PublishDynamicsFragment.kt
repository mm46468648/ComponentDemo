package com.mooc.studyproject.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.visiable
import com.mooc.common.utils.NetUtils
import com.mooc.commonbusiness.adapter.ImgCheckAdapter
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.BigImagePreview
import com.mooc.commonbusiness.manager.WeChatPresenter
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.image.ImageBean
import com.mooc.commonbusiness.pop.my.UploadPicturePop
import com.mooc.commonbusiness.utils.GetPathUtils
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.StudyprojectFragmentPublishDynamicsBinding
import com.mooc.studyproject.model.StudyPlanSource
import com.mooc.studyproject.ui.PublishingDynamicsActivity
import com.mooc.studyproject.ui.PublishingDynamicsCommentActivity
import com.mooc.studyproject.viewmodel.StudyProjectViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.MimeType
import com.ypx.imagepicker.bean.SelectMode
import com.ypx.imagepicker.data.OnImagePickCompleteListener
//import kotlinx.android.synthetic.main.studyproject_fragment_publish_dynamics.*
//import kotlinx.android.synthetic.main.studyproject_fragment_publish_dynamics.line
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 发表文字动态页面
 */
class PublishDynamicsFragment : Fragment() {
    var publishingImgAdapter: ImgCheckAdapter? = null
    private var rxPermissions: RxPermissions? = null
    var imageList = ArrayList<ImageBean>()//图片list
    private var param1: String? = null
    private var param2: Boolean = false
    var planId: String? = null
    var numLimit = 0
    var studyPlanSource: StudyPlanSource? = null
    private var isPublishComment = false //是否是发表评论
    private var dynamicId = "" //评论的动态id
    private var commendid = "" //评论的id
    private var commendUserId = "" //评论用户的id
    var picturePop: UploadPicturePop? = null
    var onlyShowText: Boolean = false
    var onlyShowPic: Boolean = false
    protected var dialog: CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getBoolean(ARG_PARAM2, false)
        }
    }

//    val model: StudyProjectViewModel by lazy {
//        ViewModelProviders.of(this).get(StudyProjectViewModel::class.java);
//    }
    val model: StudyProjectViewModel by viewModels()
    private var _binding : StudyprojectFragmentPublishDynamicsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View{
        // Inflate the layout for this fragment
        _binding = StudyprojectFragmentPublishDynamicsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initListener()
        initDataListener()

    }

    private fun initView() {
        dialog = CustomProgressDialog.createLoadingDialog(requireActivity())

        if (onlyShowPic) {
            binding.llText.visiable(false)
            binding.line.visiable(false)
        }
        if (onlyShowText) {
            binding.llPic.visiable(false)
            binding.line.visiable(false)
        }
        imageList.clear()
        imageList.add(ImageBean())//先添加一个空图片作为点击加图
        rxPermissions = activity?.let { RxPermissions(it) }
        binding.rcyPublishkImg.layoutManager = GridLayoutManager(activity, 4)
        publishingImgAdapter = ImgCheckAdapter(imageList)
        binding.rcyPublishkImg.adapter = publishingImgAdapter
        binding.submitBtn.isEnabled = true
        if (numLimit > 0) {
            binding.edtContent.hint = "请填写至少${numLimit}字以上的动态"
            binding.tvContentCount.text = "0"
        } else {
            binding.edtContent.hint = "请填写动态"
            binding.tvContentCount.text = "0"
        }
        setSubmitBtn(0)

    }

    private fun initData() {
        isPublishComment =
                arguments?.getBoolean(PublishingDynamicsCommentActivity.IS_PUBLISH_COMMENT, false)
                        ?: false
        dynamicId = arguments?.getString(
                PublishingDynamicsCommentActivity.PARAMS_DYNAMICID_KEY,
                dynamicId
        ) ?: ""
        commendid = arguments?.getString(
                PublishingDynamicsCommentActivity.PARAMS_COMMENTID_KEY,
                commendid
        ) ?: ""
        commendUserId = arguments?.getString(
                PublishingDynamicsCommentActivity.PARAMS_COMMENTUSERID_KEY,
                commendUserId
        ) ?: ""

        onlyShowPic = arguments?.getBoolean(
            PublishingDynamicsCommentActivity.PARAMS_ONLY_IMAGE,
            false
        ) ?: false

        onlyShowText = arguments?.getBoolean(
            PublishingDynamicsCommentActivity.PARAMS_ONLY_TEXT,
            false
        ) ?: false

        planId = arguments?.getString(
            PublishingDynamicsCommentActivity.STUDY_PLAN_ID,
            commendUserId
        ) ?: ""

        numLimit = arguments?.getInt(
            PublishingDynamicsCommentActivity.INTENT_STUDY_DYNAMIC_WORD_LIMIT,
            0
        ) ?: 0

        studyPlanSource =
            arguments?.getParcelable(PublishingDynamicsActivity.INTENT_STUDY_PLAN_RESOURCE)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun initListener() {
        binding.edtContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                binding.tvContentCount.text = s.length.toString()
                setSubmitBtn(s.toString().length)
            }
        })

        binding.edtContent.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            if (event.action == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        binding.submitBtn.setOnClickListener { v ->
            binding.submitBtn.isEnabled = false
            submit()
        }

        publishingImgAdapter?.addChildClickViewIds(R.id.imageView, R.id.img_del)
        publishingImgAdapter?.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.imageView -> {
                    if (publishingImgAdapter?.data?.get(position)?.url.isNullOrEmpty()) {
                        selectImage()
                    } else {
                        activity?.let {
                            val imageBigList = ArrayList<String>()//图片list
                            if (imageList.size > 0) {
                                for (i in 0 until imageList.size) {
                                    val url = imageList[i].url
                                    url?.let { it1 -> imageBigList.add(it1) }
                                }
                            }
                            BigImagePreview
                                    .setImageList(imageBigList)
                                    .setPosition(position)
                                    .start()
//                            imageList.add("")
//                            publishingImgAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                R.id.img_del -> {//删除单个图片
                    imageList.removeAt(position)
                    imageList.remove(ImageBean())
                    imageList.add(ImageBean())
                    binding.tvImgCount.text = (imageList.size - 1).toString() + "/9"
                    publishingImgAdapter?.notifyDataSetChanged()
                }

            }
        }

    }

    @SuppressLint("CheckResult")
    private fun selectImage() {
        if (rxPermissions?.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true && rxPermissions?.isGranted(
                        Manifest.permission.CAMERA
                ) ?: false
        ) {
            openDialog()
        } else {
            rxPermissions?.request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            )
                    ?.subscribe {
                        if (it) {
                            openDialog()
                        } else {
                            showPermissionDialog()
                        }
                    }
        }

    }

    private fun openDialog() {
        picturePop = activity?.let { view?.let { it1 -> UploadPicturePop(it, it1) } }
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
                    .pick(requireActivity(), OnImagePickCompleteListener {
                        upLoadImage(it[0].path)
                    })
        }
    }

    private fun getCamera() {
        ImagePicker.takePhoto(
                requireActivity(), SimpleDateFormat("yyyyMMddHHmmss")
                .format(Date()), true
        ) { items -> //剪裁回调，主线程
            loge(items[0].path)

            if (items.isNotEmpty()) {
//                upLoadImage(items[0].path)

                val filePathByUri = GetPathUtils.getRealPathFromURI(requireContext(), items[0].uri)
                upLoadImage(filePathByUri)
            }
        }
    }

    private fun showPermissionDialog() {
        activity?.let { activity ->
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = resources.getString(R.string.permission_file_read)
            publicDialogBean.strTv = resources.getString(R.string.text_ok)
            val dialog = PublicOneDialog(activity, publicDialogBean)
            XPopup.Builder(activity)
                    .asCustom(dialog)
                    .show()
        }

    }

    /**
     * 提交
     */
    private fun submit() {
        val content = binding.edtContent.text.toString().trim { it <= ' ' }
        if (numLimit > 0) {
            if (content.length < numLimit) {
                toast("本项目规定文字动态最少输入${numLimit}字")
                binding.submitBtn.isEnabled = true
                return
            }
        }
        if (!NetUtils.isNetworkConnected()) {
            toast(resources.getString(R.string.net_error))
            binding.submitBtn.isEnabled = true
            return
        }
        var imgPath = "" //上传的图片地址
        var checkWord = ""//复审的文字
        var needCheck = 0//需要复审
        val gson = Gson()
        val imageUrlList = ArrayList<String>()//图片list
        publishingImgAdapter?.data?.remove(ImageBean())

        val adapterDataList = publishingImgAdapter?.data
        val adapterDataListSize = adapterDataList?.size ?: 0
        if (adapterDataListSize > 0) {
            for (i in 0 until adapterDataListSize) {
                val adapterImageBean = adapterDataList?.get(i)
                val url = adapterImageBean?.url
                val word = adapterImageBean?.word
                if (url?.isNotEmpty() == true) {
                    imageUrlList.add(url)
                }
                if (word?.isNotEmpty() == true) {
                    needCheck = 1
                    checkWord += word
                }
            }
            imgPath = gson.toJson(imageUrlList)
        }

        if (isPublishComment && !TextUtils.isEmpty(dynamicId)) {       //如果是发表评论，上传到评论接口
            if (content.isEmpty() && imgPath.isEmpty()) {
                binding.submitBtn.isEnabled = true
                toast("内容不能为空")
                publishingImgAdapter?.data?.add(ImageBean())
                return
            }
            val requestData = JSONObject()

            requestData.put("study_activity", dynamicId)
            requestData.put("comment_user_id", GlobalsUserManager.uid)
            requestData.put("comment_content", content)
            requestData.put("comment_content_long", "0")
            requestData.put("comment_type", "0")
            requestData.put("comment_img", imgPath) //如新增的iamge字段 ，评论也可添加图片
            if (!TextUtils.isEmpty(commendid) && !TextUtils.isEmpty(commendUserId)) { //如果评论id不为空，代表回复评论
                requestData.put("comment_to", commendid)
                requestData.put("receiver_user_id", commendUserId)
            } else {
                requestData.put("comment_to", "")
                requestData.put("receiver_user_id", "0")
            }
            if (needCheck == 1) {
                requestData.put("need_check", needCheck)
                requestData.put("check_words", checkWord)
            }

            val stringBody =
                    requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
            showDialog()
            model.postCommentData(dynamicId, stringBody)

        } else {
            if (content.isEmpty() && imgPath.isEmpty()) {
                binding.submitBtn.isEnabled = true
                toast("动态内容不能为空")
                publishingImgAdapter?.data?.add(ImageBean())
                return
            }
            val requestData = JSONObject()

            requestData.put("study_plan", planId)
            requestData.put("publish_content", content)
            requestData.put("publish_img", imgPath)
            requestData.put("user_id", GlobalsUserManager.uid)
            requestData.put("publish_state", "1")
            requestData.put("activity_type", "0")
            requestData.put("activity_content_long", "")
            if (studyPlanSource != null) {
                requestData.put("activity_checkin_type", "0")
                requestData.put(
                        "checkin_source_id",
                        java.lang.String.valueOf(studyPlanSource?.id)
                )
                if (studyPlanSource?.is_re_chick == true) {
                    requestData.put("is_new_checkin", "1")
                    requestData.put("activity_bu_type", "1")
                }
            } else {
                requestData.put("activity_checkin_type", "1")
            }
            if (needCheck == 1) {
                requestData.put("need_check", needCheck)
                requestData.put("check_words", checkWord)
            }

            val stringBody =
                    requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
            showDialog()
            planId?.let { model.publishDynamics(it, stringBody) }

        }

    }

    fun showDialog() {
        if (dialog?.isShowing == false) {
            dialog?.show()

        }
    }

    private fun disDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()

        }
    }


    private fun initDataListener() {
        // 发布动态结果处理
        model.dynamicsBean.observe(viewLifecycleOwner, {
            disDialog()
            if (it != null) {
                binding.submitBtn.isEnabled = true
                toast(it.msg)
                if (it.error_code == 10004) {
                    val size = publishingImgAdapter?.data?.size ?: 0
                    if (size <= 0) {
                        publishingImgAdapter?.data?.add(ImageBean())
                    }
                } else {
                    binding.submitBtn.isEnabled = false
                    val intent = Intent()
                    intent.putExtra(IntelligentLearnListFragment.INTENT_STUDY_PLAN_DYNAMIC, it)
                    activity?.setResult(Activity.RESULT_OK, intent)
                    activity?.finish()
                }
            }
        })
        // 发布动态失败结果处理
        model.dynamicsException.observe(viewLifecycleOwner, {
            disDialog()
            binding.submitBtn.isEnabled = true
        })
        //发送评论结果
        model.sendCommendBean.observe(viewLifecycleOwner, Observer {
            disDialog()
            binding.submitBtn.isEnabled = false
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        })

        // 发布评论失败结果处理
        model.errException.observe(viewLifecycleOwner, {
            binding.submitBtn.isEnabled = true
        })

        // 图片选择监听
        model.uploadFileBean.observe(viewLifecycleOwner, Observer {

            if (it?.code == 200 || it?.code == 201) {//200成功  201需要复审
                if (it.data == null) return@Observer
                if (imageList.size > 0) {
                    val imageBean = ImageBean()
                    imageBean.url = it.data.url
                    if (it.data.word?.isNotEmpty() == true) {
                        imageBean.need_check = 1
                        imageBean.word = it.data.word
                    }
                    imageList.remove(ImageBean())
                    imageList.add(imageBean)
                    imageList.add(ImageBean())
                    binding.tvImgCount.text = (imageList.size - 1).toString() + "/9"
                    if (imageList.size > 9) {
                        imageList.remove(ImageBean())
                    }
                    publishingImgAdapter?.notifyDataSetChanged()
                }
            } else {
                if (TextUtils.isEmpty(it.msg)) {
                    toast(getString(R.string.upload_img_faile))
                } else {
                    toast(it.msg)
                }
            }
            dialog?.dismiss()
        })
    }

    private fun setSubmitBtn(length: Int) {
        if (numLimit > 0) {
            if (length > 0) {
                if (imageList.size > 0) {
                    binding.submitBtn.setBackgroundResource(R.drawable.shape_radius20_stoke1primary_solidwhite)
                    binding.submitBtn.setTextColor(resources.getColor(R.color.colorPrimary))
                } else {
                    binding.submitBtn.setBackgroundResource(R.drawable.shape_radius20_stroke1_gray9)
                    binding.submitBtn.setTextColor(resources.getColor(R.color.color_9))
                }
            } else {
                binding.submitBtn.setBackgroundResource(R.drawable.shape_radius20_stroke1_gray9)
                binding.submitBtn.setTextColor(resources.getColor(R.color.color_9))
            }
        } else {
            binding.submitBtn.setBackgroundResource(R.drawable.shape_radius20_stoke1primary_solidwhite)
            binding.submitBtn.setTextColor(resources.getColor(R.color.colorPrimary))
        }
    }

    /**
     * 上传图片到云获取地址
     */
    private fun upLoadImage(path: String?) {
        if (NetUtils.isNetworkConnected()) {
            upLoadImage2Net(path)
        } else {
            toast(getString(R.string.net_error))
        }
    }

    private fun upLoadImage2Net(path: String?) {
        dialog?.show()
        if (path.isNullOrEmpty()) return
        val file = File(path)
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val toRequestBody = file.asRequestBody("image/*; charset=utf-8".toMediaTypeOrNull())
        builder.addPart(Headers.headersOf("Content-Disposition", "form-data; name=\"" + "img" + "\"; filename =\"" + URLEncoder.encode(file.name, "UTF-8")), toRequestBody)

        val planIdRequestBody = planId?.toRequestBody("charset=utf-8".toMediaTypeOrNull())

        planIdRequestBody?.let { builder.addPart(Headers.headersOf("Content-Disposition", "form-data; name=\"" + "study_plan_id\""), it) }
        model.postImageFile(builder.build())

    }


}