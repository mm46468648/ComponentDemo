package com.mooc.commonbusiness.module.report

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.ktextends.*
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.manager.WeChatPresenter
import com.mooc.commonbusiness.model.ReportBean
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.databinding.CommonActivityReportDialogBinding
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.ImageItem
import com.ypx.imagepicker.bean.MimeType
import com.ypx.imagepicker.bean.SelectMode
import com.ypx.imagepicker.data.OnImagePickCompleteListener
//import kotlinx.android.synthetic.main.common_activity_report_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * 举报弹窗
 * 弹窗样式的Activity
 * 方便处理选择图片逻辑可以减少和具体的资源页面的耦合
 *
 * version2：接口增加了图片鉴黄功能
 * 动画:从下到上进入
 */
@Route(path = Paths.PAGE_REPORT_DIALOG)
class ReportDialogNewActivity : BaseActivity() {
    //所举报的资源类型
    val resourceType by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_TYPE, -1)

    val mViewModel: ReportViewModel by lazy {
//        ViewModelProviders.of(this)[ReportViewModel::class.java]
//        ViewModelProviders.of(this,BaseViewModelFactory(resourceType.toString()))[ReportViewModel::class.java]

        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ReportViewModel(resourceType.toString()) as T
            }
        }).get(ReportViewModel::class.java)
    }

    val reportChoiceAdapter = ReportChoiceAdapter(null)


    val maxImageSelectNum = 9   //最多上报图片数量
    val maxContentLength = 200 //内存最大字数
//    val reportImageList = arrayListOf<ImageItem>()
    val reportImageUrl = arrayListOf<String>()
    val repostImageAdpater = ReportImageAdapter(reportImageUrl)
    var reportContent = ""   //举报内容


    //所举报的资源id
    val resourceId by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_ID, "")



    //所举报的资源标题
    val resourceTitle by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_TITLE, "")
    private lateinit var inflater: CommonActivityReportDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = CommonActivityReportDialogBinding.inflate(layoutInflater)
        inflater.vsReportDetail.setOnInflateListener { stub, inflated ->

        }
        setContentView(inflater.root)

        inflater.flRool.setOnClickListener { finish() }
        initWindow()

        initAdapter()

        mViewModel.onRequsetStatus.observe(this, Observer {
            if(it){
                finish()
            }
        })
    }

    /**
     * 初始化适配器
     */
    private fun initAdapter() {
        inflater.rvReportType.layoutManager = LinearLayoutManager(this)
        reportChoiceAdapter.setOnItemClickListener { adapter, view, position ->
            val reportBean =
            if(resourceType == ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC){
                mViewModel.reportChoiceLiveData.value?.results?.get(position)
            }else{
                mViewModel.reportChoiceLiveData.value?.report_choices?.get(position)
            }
            reportBean?.let { showReportDetailLayout(it) }
        }

        inflater.rvReportType.adapter = reportChoiceAdapter

        mViewModel.reportChoiceLiveData.observe(this, Observer {
            if(resourceType == ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC){
                reportChoiceAdapter.setList(it.results)
            }else{
                reportChoiceAdapter.setList(it.report_choices)
            }
        })


        inflater.rvReportType.layoutManager = LinearLayoutManager(this)

        //图片选择添加图片脚布局
        addFootView()
    }




    /**
     * 选择图片
     */
    private fun choosePicture() {
        val freeCount = maxImageSelectNum - reportImageUrl.size
        if (freeCount == 0) {
            toast("最多选择9张")
            return
        }
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
                .pick(this, OnImagePickCompleteListener {images->
                    onIamgePick(images)
                })
    }

    fun onIamgePick(images:ArrayList<ImageItem>){
        val createLoadingDialog = CustomProgressDialog.createLoadingDialog(this@ReportDialogNewActivity, true)
        createLoadingDialog.show()
        GlobalScope.launch {
            images.forEach {imageItem->
                //先上传图片是否鉴黄涉及政
                val fromImageFilePath = RequestBodyUtil.fromImageFilePath(imageItem.path)
                try {
                    val response = mViewModel.postImageFile(fromImageFilePath)
                    createLoadingDialog.dismiss()
                    if(response.code!=200){
                        toastMain(response.msg)
                        return@launch
                    }

                    reportImageUrl.add(response.data.url?:"")

                    withContext(Dispatchers.Main){
                        //图片选择回调，主线程
//                        reportImageList.addAll(images)
                        repostImageAdpater.notifyDataSetChanged()
                        tvPicNum?.text = "${reportImageUrl.size}/${maxImageSelectNum}"

                        val itemCount = repostImageAdpater.itemCount
                        //大于4个滑动一下
                        if (itemCount > 4) {
                            reportImageLayoutManager.scrollToPositionWithOffset(itemCount - 4, 0)
                        }
                        //大于9个隐藏添加按钮
                        if (itemCount >= maxImageSelectNum + repostImageAdpater.footerLayoutCount) {
                            hideFootView(true)
                        }
                    }
                }catch (e:Exception){
                    loge(e.toString())
                    createLoadingDialog.dismiss()
                }

            }

        }




    }

    val reportImageLayoutManager by lazy { LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) }

    /**
     * 展示举报详情布局
     */
    var tvPicNum: TextView? = null
    var tvDesNum: TextView? = null
    private fun showReportDetailLayout(reportBean: ReportBean) {
        inflater.rvReportType.visibility = View.GONE
        val inflate = findViewById<ViewStub>(R.id.vsReportDetail).inflate()
        val rvReportPic = inflate?.findViewById<RecyclerView>(R.id.rvReportPic)
        val etDes = inflate?.findViewById<EditText>(R.id.etDes)
        val tvReportSend = inflate?.findViewById<TextView>(R.id.tvReportSend)
        tvPicNum = inflate?.findViewById<TextView>(R.id.tvPicNum)
        tvDesNum = inflate?.findViewById<TextView>(R.id.tvDesNum)

        //点击发送报告
        tvReportSend?.setOnClickListener {

            reportBean.resourceId = resourceId
            reportBean.resourceType = resourceType
            reportBean.resourceTitle = resourceTitle
            mViewModel.checkReportContent2(reportBean, reportContent, reportImageUrl,this)
        }
        //监听输入
        etDes?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                reportContent = p0?.toString() ?: ""
                tvReportSend?.let {
                    changeSendButton(it) }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        //举报图片
        rvReportPic?.layoutManager = reportImageLayoutManager

        repostImageAdpater.addChildClickViewIds(R.id.ivDelete)
        repostImageAdpater.setOnItemChildClickListener { adapter, view, position ->
            reportImageUrl.remove(repostImageAdpater.data.get(position))
            repostImageAdpater.notifyItemRemoved(position)
            hideFootView(false)
            setChooseImageNum()
        }
        rvReportPic?.adapter = repostImageAdpater


    }

    /**
     * 修改发送按钮的颜色
     */
    private fun changeSendButton(tvReportSend: TextView) {
        val btnBgRes = if (reportContent.isNotEmpty()) R.drawable.shape_radius20_stoke1primary_solidno else R.drawable.shape_radius20_stroke1_gray9
        val btnTextColor = if (reportContent.isNotEmpty()) R.color.colorPrimary else R.color.color_9
        tvReportSend.setBackgroundResource(btnBgRes)
        tvReportSend.setTextColor(ContextCompat.getColor(this, btnTextColor))

        tvDesNum?.setText("${reportContent.length}/${maxContentLength}")
    }


    /**
     * 初始化window
     * 使显示在底部，并充满屏幕宽
     */
    private fun initWindow() {
        //设置布局在底部
        window.setGravity(Gravity.BOTTOM)
        //设置布局填充满宽度
        val layoutParams: WindowManager.LayoutParams = window.attributes
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = layoutParams
    }

    fun setChooseImageNum(){
//        tvPicNum?.text = "${reportImageList.size}/${maxImageSelectNum}"
        tvPicNum?.text = "${reportImageUrl.size}/${maxImageSelectNum}"
    }

    private fun addFootView() {
        repostImageAdpater.addFooterView(createFootView(this) {
            choosePicture() }
        , orientation = LinearLayout.HORIZONTAL)
    }

    fun hideFootView(show:Boolean) {
        val visiable = if(show) View.GONE else View.VISIBLE
        repostImageAdpater.footerLayout?.visibility = visiable
    }


    fun createFootView(context: Context, callBack: (() -> Unit)? = null): View {
//            val headView = View.inflate(context,R.layout.home_item_studyroom_studylist,null)
//            val findViewById = headView.findViewById<ImageView>(R.id.ivCover)
        val frameLayout = FrameLayout(context)
        val layoutParams = FrameLayout.LayoutParams(95.dp2px(), 95.dp2px())
        frameLayout.layoutParams = layoutParams
        val imageView = ImageView(context)
        imageView.layoutParams = FrameLayout.LayoutParams(78.dp2px(), 78.dp2px())
        imageView.setImageResource(R.mipmap.home_ic_folder_cover_add)
        imageView.setOnClickListener { callBack?.invoke() }
        val layoutParams1 = FrameLayout.LayoutParams(78.dp2px(), 78.dp2px())
        layoutParams1.gravity = Gravity.CENTER
        frameLayout.addView(imageView, layoutParams1)
        return frameLayout
    }


    //

}