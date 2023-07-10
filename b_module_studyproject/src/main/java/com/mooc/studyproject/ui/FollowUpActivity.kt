package com.mooc.studyproject.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.XPopupCallback
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.logi
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.GsonManager
import com.mooc.common.utils.NetUtils
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.common.utils.sharepreference.SpUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.pop.CommonAlertPop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.ServerTimeManager
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.dialog.followup.FollowupStatusDialog
import com.mooc.studyproject.R
import com.mooc.studyproject.api.UserApi
import com.mooc.studyproject.databinding.StudyprojectActivityFollowupBinding
import com.mooc.studyproject.dialog.FollowupCommitDialog
import com.mooc.studyproject.followup.*
import com.mooc.studyproject.model.*
//import com.mooc.studyproject.viewmodel.FollowupData
//import com.mooc.studyproject.viewmodel.FollowupResourse
//import com.mooc.studyproject.viewmodel.FollowupResponse
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.taisdk.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

@Route(path = Paths.PAGE_FOLLOWUP_NEW)
class FollowUpActivity : BaseActivity() {

    companion object {

        const val TAG = "FollowUpActivity"
        const val LAUNGUAGE = "zh_cn"    //语言
        const val CATELOG = "read_sentence" //评测分类
        const val RESULT_LEVEL = "complete" //结果等级
        const val TEXT_ENCODING = "utf-8" //结果等级
        const val VAD_BOS = "5000" // 设置语音前！！端点:静音超时时间，即用户多长时间不说话则当做超时处理
        const val VAD_EOS = "10000" //  设置语音后！！端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        const val KEY_SPEECH_TIMEOUT = 2 * 1000 * 60      // 语音输入超时时间，即用户最多可以连续说多长时间，（产品要求两分钟）
        const val KEY_SPEECH_TIMEOUT_SECOND = 2 * 60      // 语音输入超时时间,单位秒
        const val TEST_CONTENT =
                "特殊时期，特殊方法。新冠疫情的影响，2020年春季学期的开学时间一再被延期，在抗疫期间，北海八中党员积极响应党的号召，坚守岗位带头奋战在抗疫一线，但当前疫情依然严峻复杂，为提高党员们继续奋战的斗志，更是为了让党员们比学赶帮，弘扬正能量，同时，教育党员无论在什么时候，都不能忘记规定，要开展组织生活，因此北海八中党总支充分利用互联网，将党员教育搬到了线上，借助网课的形式将党课上到了“疫”线。"

    }

    lateinit var contentView: StudyprojectActivityFollowupBinding
    val rxPermissions by lazy {    //初始化权限校验
        RxPermissions(this)
    }

    //
    val mHandler by lazy {
        MHandler(this)
    }
    val mOral by lazy {
        val speedUtil = TecentSpeedUtil(this);
        speedUtil
    }


    val pop by lazy {
        val pop = FollowUpPopW(this);
        pop
    }

    val uploadMp3Pop by lazy {
        val pop = FollowUpPopW2(this);
        pop
    }

    var followUpResourceId = ""
    var parentResourceId = ""
    var parentResourceType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //状态栏变白
        contentView = DataBindingUtil.setContentView<StudyprojectActivityFollowupBinding>(
                this,
                R.layout.studyproject_activity_followup
        )
        initView()
        initData()
        initListener()


    }

    fun initView() {
        //测试文案
//        contentView.tvContent.text = TEST_CONTENT
        contentView.progressBar.max = KEY_SPEECH_TIMEOUT_SECOND
        contentView
    }

    override fun onBackPressed() {
        if(isUploadToHuaweiCloud){
            toast(FollowUpPopW2.STATUS_UPLOAD)
            return
        }
        if (status == 2) {
            //全部读完,退出当前页面
            setResult(Activity.RESULT_OK)
        }
        finish()
    }

    fun initListener() {

        contentView.commonTitle.setOnLeftClickListener {
            onBackPressed()
        }
        contentView.btStartFollowup.setOnClickListener {
//            checkRecordPermission()
            //点击查询跟读列表
            requestReadContentList()
        }

        contentView.ivStartReading.setOnClickListener {
            contentView.ivStartReading.isClickable = false
            checkRecordPermission()
            contentView.ivStartReading.postDelayed(Runnable {
                contentView.ivStartReading.isClickable = true
            }, 2000)
        }

        contentView.ivReading.setOnClickListener {
            showReadEnd()
            //点击主动停止朗读
            stopRead()
        }


    }


    /***
     * 上传本地音频
     */
    private fun uploadMp3ToTecent(text: String) {

        if (!TextUtils.isEmpty(mOral.currenMp3)) {
            mOral.oralLocalMp3(
                    this@FollowUpActivity,
                    contentView.tvContent.text.toString().trim(),
                    mOral.currenMp3,
                    totalAudioTime,
                    object : TecentSpeedUtil.upProgress {
                        override fun upProgress(progress: Int) {
                            runOnUiThread {
                                pop.refreshProgress(mOral.upProgress)
                            }
                        }

                        override fun onUpError(taiError: TAIError) {
                            runOnUiThread {
                                pop.dismiss()
                                postFollowError("", "", "", null, taiError.desc, taiError.code)
                                showLastReadDialog()
                                delRepeatAudioLimit();
                            }
                        }

                    },
                    mTaiEvaluatorListener
            );

            pop.refreshProgress(0)
            pop.show()


        }
    }


    /***
     * 显示上次未上传的提示弹窗
     */
    private fun showLastNoUploadDialog(text: String) {
        val lastMp3 = mOral.getLastMp3(parentResourceId + text);

        if (!TextUtils.isEmpty(lastMp3)) {
            val file = File(lastMp3)

            if (file.exists()) {
                //读取本地音频时长
                readNativeFileTime(lastMp3)
                mOral.currenMp3 = lastMp3
                showLastReadDialog()
            } else {
                //评测未通过
                if (currentReadBean?.is_fail!!) {
                    showReadResultDialog(FollowupStatusDialog.followUpState_fill)
                }

            }
        } else {
            //评测未通过
            if (currentReadBean?.is_fail!!) {
                showReadResultDialog(FollowupStatusDialog.followUpState_fill)

            }
        }
    }

    var disposable: Disposable? = null;

    fun readNativeFileTime(file: String) {
        disposable = Observable.create<Int> {
            val mediaPlayer = MediaPlayer();
            mediaPlayer.setDataSource(file)
            mediaPlayer.prepare()
            it.onNext(mediaPlayer.duration)
        }.compose(RxUtils.applySchedulers())
                .subscribe(Consumer { totalAudioTime = it / 1000 }, Consumer { totalAudioTime = 0 });


    }


    /***
     * 显示弱网环境弹窗
     */
    private fun showLastReadDialog() {
        val followupStatusDialog = FollowupLastReadDialog()
        followupStatusDialog?.onConfirm = {
            currentReadBean?.apply {
                getRepeatIsOverLimit()
//                uploadMp3ToTecent(this.context)
            }
        }
        followupStatusDialog?.onCancle = {
            delMp3File()
        }
        followupStatusDialog?.show(supportFragmentManager, "")
    }


    val mTAIOralEvaluationCallback: TAIOralEvaluationCallback = object : TAIOralEvaluationCallback {
        override fun onResult(error: TAIError?) {
        }

    }


    var file: String = "";

    //    var evalFaile = false;
    var result: TAIOralEvaluationRet? = null;

    val mTaiEvaluatorListener: TAIOralEvaluationListener = object : TAIOralEvaluationListener {


        override fun onEvaluationData(p0: TAIOralEvaluationData?, ret: TAIOralEvaluationRet?) {
            if (isDestroy) {
                return
            }

            runOnUiThread(Runnable {

                if (isFinishing) {
                    return@Runnable
                }
                pop?.dismiss();
                Log.i(
                    "msg", "流利度：" + ret?.pronFluency + "完整度：" + ret?.pronCompletion
                            + "得分：" + ret?.suggestedScore
                )

                if (p0 != null && p0.bEnd) {

                    Log.i("msg", "进入showRead END")
                    if (ret == null) {
//                        evalFaile = true;
                        showLastReadDialog()
                        return@Runnable
                    }
//                    evalFaile = false;
                    result = ret;
//                    mOral.stopLocalOral()
                    showReadEnd()
                    onConfrimCommitDialog(ret);
                    delRepeatAudioLimit()
                }
            })
        }

        override fun onEvaluationError(data: TAIOralEvaluationData?, taiError: TAIError?) {
            if(taiError!=null){
                runOnUiThread {
                    pop.dismiss()
                    postFollowError("", "", "", null, taiError.desc, taiError.code)
                    showLastReadDialog()
                    delRepeatAudioLimit();
                }
            }

        }

        override fun onFinalEvaluationData(
            data: TAIOralEvaluationData?,
            result: TAIOralEvaluationRet?
        ) {

        }

        override fun onEndOfSpeech(isSpeak: Boolean) {
        }

        override fun onVolumeChanged(p0: Int) {

        }


    }


    /**
     * 展示朗读完成，是否提交弹窗
     * @param fluency_score 流畅度
     * @param integrityScore 完整度
     */
    private fun showReadCompleteDialog(ret: TAIOralEvaluationRet?) {
        val followupStatusDialog = FollowupCommitDialog()
        followupStatusDialog.onConfirm = {
            onConfrimCommitDialog(ret)
        }
        followupStatusDialog.onCancle = {
            delMp3File()

        }
        followupStatusDialog.show(supportFragmentManager, "")

    }

    private fun delMp3File() {
        if (TextUtils.isEmpty(mOral.currenMp3)) {
            return
        }
        val file = File(mOral.currenMp3)
        if (file.exists()) {
            file.delete()
        }
//        mOral.currenMp3 = "";
//        mOral.saveMp3(resourseid + currentReadBean?.context)
        mOral.currenMp3="";
        mOral.deleteMp3Key(parentResourceId + currentReadBean?.context)
    }


    var fluency_score: String? = ""
    var integrityScore: String? = ""

    /**
     * 点击确认提交弹窗
     * 必须两个
     */
    private fun onConfrimCommitDialog(ret: TAIOralEvaluationRet?) {
        fluency_score = ret?.pronFluency?.times(100).toString()
        integrityScore = ret?.pronCompletion?.times(100).toString()

        //点击上传音频，比较流畅度和完整度，然后展示成功或者失败状态弹窗
        try {
            logi("msg", "流利度：" + fluency_score + "完整：" + integrityScore)
            logi("msg", "流利度：" + currentReadBean?.fluent_num + "完整：" + currentReadBean?.finish_num)
            if (fluency_score!!.toDouble() >= currentReadBean?.fluent_num ?: 0 && integrityScore!!.toDouble() >= currentReadBean?.finish_num ?: 0) {//朗读成功
                //上传音频
                uploadRepeatAudio(
                        File(mOral.currenMp3),
                        currentReadBean?.id,
                        totalAudioTime.toString(),
                        true);
            } else { //朗读失败，展示失败状态弹窗
                showReadResultDialog(FollowupStatusDialog.followUpState_fill)
                uploadRepeatAudio(
                        File(mOral.currenMp3),
                        currentReadBean?.id,
                        totalAudioTime.toString(),
                        false);
            }
        } catch (e: Exception) {
            loge(TAG, e.toString())
        }


    }

    /**
     * 点击状态弹窗
     */
    fun onConfirmStatusDialog(status: Int) {
        when (status) {
            FollowupStatusDialog.followUpState_Success -> {
                currentReadIndex++
                changeReadCnotent()
            }
            FollowupStatusDialog.followUpState_all_complete -> {
                //全部读完,退出当前页面
                setResult(Activity.RESULT_OK)
                finish()
            }
            else -> {
                showFollowUp(false)
            }
        }
    }

    /**
     * 停止倒计时
     */
    private fun stopTime() {
//        if (subscribe?.isDisposed == true) return
        subscribe?.dispose()
        subscribe = null
    }

    /**
     * 展示朗读结果
     * @param status 0成功，1失败，2，全部完成
     *
     */
    private fun showReadResultDialog(status: Int) {
        val followupStatusDialog = FollowupStatusDialog()
        followupStatusDialog.followUpState = status
        followupStatusDialog.onConfirm = {
            onConfirmStatusDialog(status)
        }
        followupStatusDialog.show(supportFragmentManager, "")
    }

    /**
     * 切换阅读内容
     */
    private fun changeReadCnotent() {
        if (currentReadIndex > followupResourceList?.lastIndex ?: 0) {
            //已经全部读完,退出当前页面
            finish()
            return
        }
        currentReadBean = followupResourceList?.get(currentReadIndex)
        currentReadBean?.apply {
            contentView.tvContent.text = this.context
            //当前阅读内容索引
            contentView.tvTotal.text =
                    "(${currentReadIndex + 1}/${followupResourceList?.size ?: currentReadIndex + 1})"
            showLastNoUploadDialog(this.context)
        }
    }


    /**
     * 切换阅读内容
     * 只负责切内容，不显示弹窗
     */
    private fun changeReadCnotentNew() {
        if (currentReadIndex > followupResourceList?.lastIndex ?: 0) {
            //已经全部读完,退出当前页面
            finish()
            return
        }
        currentReadBean = followupResourceList?.get(currentReadIndex)
        currentReadBean?.apply {
            contentView.tvContent.text = this.context
            //当前阅读内容索引
            contentView.tvTotal.text =
                "(${currentReadIndex + 1}/${followupResourceList?.size ?: currentReadIndex + 1})"
        }
    }


    var subscribe: Disposable? = null
    var totalAudioTime = 0; //音频总时长

    /**
     * 开始朗读倒计时
     */
    private fun startReadTime() {
        //重置audio时常
        totalAudioTime = 0
        loge(TAG, "开始计时:${System.currentTimeMillis()}")
        val foregroundColorSpan =
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary))
        showReadingLayout()
        stopTime()
        subscribe = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    totalAudioTime++
                    loge(TAG, "当前时常:${totalAudioTime} ---当前时间:${System.currentTimeMillis()}")

                    //剩余时间 如果使用it是在一秒后才返回 0 ，所以延迟了一秒
                    val plusTime = KEY_SPEECH_TIMEOUT_SECOND - totalAudioTime
                    Log.d(TAG, plusTime.toString())
                    showReadProgress(totalAudioTime)
                    if (plusTime <= 10) {
                        //如果还剩10s，显示10s倒计时
                        contentView.tvTime.visibility = View.VISIBLE
                        val plusTimeStr = "倒计时: ${plusTime} 秒"
                        val spannableString = SpannableString(plusTimeStr)
                        spannableString.setSpan(
                                foregroundColorSpan,
                                4,
                                plusTimeStr.length,
                                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        contentView.tvTime.text = spannableString
//                foregroundColorSpan.s
                    }

                    if (plusTime <= 0) { //结束录音，停止计时
                        //倒计时结束，朗读结束
                        stopRead()
                        showReadEnd()
                    }
                }
    }

    private fun stopRead() {
        Log.i("msg", "stop read")
        mOral.stopRecord(TAIOralEvaluationCallback {
            runOnUiThread {
                toastNetError(it)
            }
        })

        //生成wav后上传
        mOral.pcToWav(object : TencentAudioRecorder.IPC2WACSucess {
            override fun sucess() {
                getRepeatIsOverLimit()


            }

            override fun faile() {
                toast("音频录制失败,请重新录制")
            }
        });


    }

    private fun toastNetError(it: TAIError) {
        if (it.code != 0) {
            showLastReadDialog()
        }
    }

    private fun showReadProgress(progress: Int) {
        //进度条开始加载
        contentView.progressBar.progress = progress
    }

    /**
     * 检查权限通过，
     * 隐藏开始跟读
     * 显示跟读布局
     */
    private fun showFollowUp(allFinish: Boolean) {

        //改变标题的颜色和大小,加粗
        contentView.tvTitle.setTextColor(Color.parseColor("#222222"))
        contentView.tvTitle.setTextSize(20f)
        contentView.tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))


        //改变朗读内容的颜色，大小行间距
        contentView.tvContent.setTextColor(Color.parseColor("#222222"))
        contentView.tvContent.textSize = 18f
        contentView.tvContent.setLineSpacing(7.5f.dp2px().toFloat(), 1f)

        contentView.clBindRes.visibility = View.GONE
        contentView.btStartFollowup.visibility = View.GONE


        if (allFinish) {   //显示全部完成状态
            contentView.tvAllFinish.visibility = View.VISIBLE
        } else {    //显示跟读状态
            contentView.clStartRead.visibility = View.VISIBLE
        }
    }


    /**
     * 展示阅读布局
     * 进度条和动图同时显示
     */
    private fun showReadingLayout() {
        contentView.progressBar.visibility = View.VISIBLE
        contentView.ivReading.visibility = View.VISIBLE

//        val animationDrawable = contentView.ivReading.drawable as AnimationDrawable
//        animationDrawable.start()
        Glide.with(this).asGif().load(R.drawable.studyproject_followup_reading)
                .into(contentView.ivReading)
        contentView.ivStartReading.visibility = View.GONE

    }

    /**
     * 显示阅读结束
     * 重置进度
     * 展示开始按钮,隐藏进度等
     * 停止计时
     */
    private fun showReadEnd() {

        stopTime()
        contentView.progressBar.progress = 0
        contentView.progressBar.visibility = View.GONE
        contentView.ivReading.visibility = View.GONE
        contentView.tvTime.visibility = View.GONE
        contentView.ivStartReading.visibility = View.VISIBLE
    }

    /**
     * 开始跟读
     */
    private fun startFollowUp() {

        if (contentView.tvContent.text.isEmpty()) return
//        mIse.startEvaluating(contentView.tvContent.text.toString(), null, mEvaluatorListener)
        startReadTime()
        logi("msg", contentView.tvContent.text.toString())
        mOral.startRecord(
                contentView.tvContent.text.toString(),
            parentResourceId,
                mTaiEvaluatorListener,
                mTAIOralEvaluationCallback
        );
    }


    @SuppressLint("CheckResult")
    fun getServerTime() {
        if (Math.abs(ServerTimeManager.getInstance().serviceTime - System.currentTimeMillis()) > 60 * 1000) {
            showTimerDialog(false);
        } else {
            startFollowUp();
        }
    }

    fun showTimerDialog(isOut: Boolean) {

        val content = "检测到您的时间与标准时间不一致，会导致评测失败，请先校验手机时间"
        val positiveStr = "确定"

//        var dialog: PublicOneDialog =
//            PublicOneDialog(this@FollowUpNewActivity, R.style.DefaultDialog);

        XPopup.Builder(this)
                .asCustom(CommonAlertPop(this, content, object : CommonAlertPop.Confirm {
                    override val position: Int
                        get() = CommonAlertPop.Position.RIGHT

                    override val text: String
                        get() = positiveStr
                    override val confirmBack: (() -> Unit)?
                        get() = {

                        }
                }))
                .show()
    }


    /**
     * 检测录音权限
     */
    @SuppressLint("CheckResult")
    private fun checkRecordPermission() {

//        val a = "<read_sentence beg_pos=\"0\" content=\"特殊时期。特殊方法。新冠疫情的影响。二零二零年春季学期的开学时间一再被延期。在抗疫期间。北海八中党员积极响应党的号召。坚守岗位带头奋战在抗疫一线。但当前疫情依然严峻复杂。为提高党员们继续奋战的斗志。更是为了让党员们比学赶帮。弘扬正能量。同时。教育党员无论在什么时候。都不能忘记规定。要开展组织生活。因此北海八中党总支充分利用互联网。将党员教育搬到了线上。借助网课的形式将党课上到了疫线。\" end_pos=\"1020\" except_info=\"28676\" integrity_score=\"0.000000\" is_rejected=\"true\" time_len=\"1020\" total_score=\"0.000000\">"
//        val regex = Regex("total_score="+"\""+"\\S*"+"\"")
//        val findAll = regex.find(a)
//        Log.d(TAG, "totalMatch ${findAll?.value}")

        if (rxPermissions.isGranted(Manifest.permission.RECORD_AUDIO) && rxPermissions.isGranted(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
        ) {
            getServerTime()
            return
        }

        rxPermissions
                .requestEachCombined(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe { permission ->  // will emit 2 Permission objects
                    if (permission.granted) {
                        // `permission.name` is granted !
                        getServerTime()
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // Denied permission without ask never again
                    } else {
                        // Denied permission with ask never again
                        // Need to go to the settings
                        toast("需要您到手机设置中打开存储和录音权限")
                    }
                }
    }

//    var resourseid = "" //这个id是学习项目引用资源生成的唯一的id

    fun initData() {

//        val iSBindEbook = intent.getBooleanExtra(PARAMS_IS_BIND_EBOOK, false)

        followUpResourceId = intent.getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_ID) ?: ""
        parentResourceId = intent.getStringExtra(IntentParamsConstants.PARAMS_PARENT_ID) ?: ""
        parentResourceType = intent.getStringExtra(IntentParamsConstants.PARAMS_PARENT_TYPE) ?: ""

//        resourseid = intent.getStringExtra(PARAMS_CHECKIN_SOURCE_ID) ?: ""

        if (!NetUtils.isNetworkConnected()) {
            toast(resources.getString(R.string.net_error))
            return
        }


        ApiService.getRetrofit().create(UserApi::class.java).getFollowupDetail(followUpResourceId)
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<HttpResponse<FollowupResponse>>(this) {
                    override fun onSuccess(t: HttpResponse<FollowupResponse>) {
                        if (t.code != 200 || t.data == null) return

                        val data = t.data.results

                        initReadDetail(data)
                        if (!"1".equals(data.is_bind_source)) {
                            //直接设置可度
                            setFollowButtonEnable()
                            return
                        }
                        //如果绑定的资源id不为空，并且是电子书类型，需查询电子书信息
                        if ("1".equals(data.is_bind_source) && data.bind_source_id.isNotEmpty() && data.bind_source_type == ResourceTypeConstans.TYPE_E_BOOK) {

                            //需达到进度才能显示跟读按钮
                            if (data.ebook_process >= data.read_process) {
                                setFollowButtonEnable()
                            }


                            ApiService.getRetrofit().create(UserApi::class.java)
                                    .getEbookDetail(data.bind_source_id)
                                    .compose(RxUtils.applySchedulers()).subscribe(object :
                                            BaseObserver<EBookBean>(this@FollowUpActivity) {
                                        override fun onSuccess(t: EBookBean) {
                                            //显示绑定资源样式
                                            initEbookDetail(t, data)
                                        }

                                    })
                        }

                    }

                    override fun onFailure(code: Int, message: String?) {
                        super.onFailure(code, message)

                        loge(TAG, message ?: "")
                    }
                })


//        }

    }

    /**
     * 初始化阅读详情
     */
    private fun initReadDetail(data: FollowupResponse.FollowupDetail) {
        contentView.tvTitle.text = data.name
        contentView.tvContent.text = data.introduction
        contentView.tvTotal.text = "共 ${data.repeat_context_count} 篇朗读文稿"
    }

    //是否在评测中
    var is_evaluating: Boolean = false;

    /**
     * 请求跟读列表
     */
    fun requestReadContentList() {
//        val resourseid = intent.getStringExtra(PARAMS_RESOURSE_ID) ?: ""
//        val resourseType = intent.getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_TYPE) ?: ""
//        val checkin_resource_id = intent.getStringExtra(PARAMS_CHECKIN_SOURCE_ID) ?: ""

        //获取跟读内容列表
        ApiService.getRetrofit().create(UserApi::class.java)
                .getFollowupList(followUpResourceId, parentResourceId,parentResourceType)
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<HttpResponse<FollowupResourse>>(this) {

                    override fun onFailure(code: Int, message: String?) {
                        super.onFailure(code, message)

                        loge("followupactivity", message ?: "")
                    }

                    override fun onSuccess(t: HttpResponse<FollowupResourse>) {
                        onReadListResponse(t)
                    }
                })
    }

    /**
     * 设置绑定的电子书的信息
     */
    private fun initEbookDetail(t: EBookBean, data: FollowupResponse.FollowupDetail) {
        contentView.clBindRes.visibility = View.VISIBLE
        contentView.tvBindTitle.text = t.title
        contentView.tvWriter.text = t.writer
        contentView.mivCover.setImageUrl(t.picture, 2.dp2px().toInt())


        val needProgress: Int = (data.read_process * 100).toInt()  //需要达到
        val currentProgress: Int = (data.ebook_process * 100).toInt()   //当前进度
        val tipStr = "朗读前，以下书籍的阅读进度需达 ${needProgress}%\n" +
                "您本书当前的阅读进度为 ${currentProgress}% "

        val spannableString = SpannableString(tipStr)

        val currentColor = if (currentProgress >= needProgress) ContextCompat.getColor(
                this,
                R.color.colorPrimary
        ) else Color.parseColor("#BA3748")
        spannableString.setSpan(
                ForegroundColorSpan(
                        ContextCompat.getColor(
                                this,
                                R.color.colorPrimary
                        )
                ),
                tipStr.indexOf(needProgress.toString()),
                tipStr.indexOf(needProgress.toString()) + needProgress.toString().length + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
                RelativeSizeSpan(1.1f),
                tipStr.indexOf(needProgress.toString()),
                tipStr.indexOf(needProgress.toString()) + needProgress.toString().length + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
                ForegroundColorSpan(currentColor),
                tipStr.lastIndexOf(currentProgress.toString()),
                tipStr.lastIndexOf(currentProgress.toString()) + currentProgress.toString().length + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
                RelativeSizeSpan(1.1f),
                tipStr.lastIndexOf(currentProgress.toString()),
                tipStr.lastIndexOf(currentProgress.toString()) + currentProgress.toString().length + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        contentView.tvProgressTip.text = spannableString

        contentView.clBindRes.setOnClickListener {
            if (t.status < 0) {
                toast("资源已下线")
                return@setOnClickListener
            }
            //跳转到电子书页面
            ARouter.getInstance().build(Paths.PAGE_EBOOK_DETAIL)
                    .withString(IntentParamsConstants.EBOOK_PARAMS_ID, t.id).greenChannel().navigation()

        }
    }


    var followupResourceList: List<FollowupData>? = null
    var currentReadIndex = -1 //当前未达标内容位置
    var currentReadBean: FollowupData? = null

    /**
     * 当阅读列表响应
     */
    private fun onReadListResponse(t: HttpResponse<FollowupResourse>) {
        if (t.code != 200 || t.data == null) return


        //有数据后可以点击跟读，改变颜色
//        setFollowButtonEnable()
        is_evaluating = t.data.is_evaluating

        followupResourceList = t.data.results
        if (followupResourceList?.isEmpty() == true) return

        //评测中
        if (is_evaluating) {
            t.data.results?.apply {
                var needFollowupData: FollowupData? = null
                for (i in this.indices) {
                    if (this[i].status == "0") {
                        needFollowupData = this[i]
                        break
                    }
                }
                needFollowupData?.let { showEvaluatingDialogNew(it) }
            }
            return
        }

        followupResourceList = t.data.results
        if (followupResourceList?.isEmpty() == true) return
        //查找第一个未达标的资源，设置相应内容开始跟读
        followupResourceList?.apply {
            for (i in this.indices) {
                if (this[i].status == "0") {
                    currentReadBean = this[i]
                    currentReadIndex = i
                    break
                }
            }

            var isAllFinish = false //是否全部完成
            //如果都已完成，那么显示第最后一个跟读内容，并显示完成状态
            if (currentReadBean == null) {
                currentReadIndex = followupResourceList?.lastIndex ?: 0
                currentReadBean = followupResourceList?.get(currentReadIndex)
                isAllFinish = true
            }



            if (isAllFinish) {
                showBackReadResultDialog(
                        FollowupStatusDialog.followUpState_all_complete,
                        isAllFinish
                )
                return@apply
            }

            //后台是否评测成功
            val lastBean = isBackReadEvaluatSuccess()
            if (lastBean != null) {  //上一条后台评测成功

                if (!getBackReadDialogState(lastBean)) {  //没弹过弹窗
                    delMp3File()
                    saveBackReadDialogState(lastBean)  //记录已弹窗

                    showBackReadResultDialog(
                            FollowupStatusDialog.followUpState_Success,
                            isAllFinish
                    )


                }

            }

            setNextContent(isAllFinish)


        }

    }


    //上一条是否后台评测并评测成功
    fun isBackReadEvaluatSuccess(): FollowupData? {
        if (currentReadIndex > 0) {
            return followupResourceList?.get(currentReadIndex - 1)?.let {
                if (!it.is_fail && it.is_backend)
                    it
                else null
            }
        }
        return null
    }

    fun getBackReadDialogState(lastFollowData: FollowupData): Boolean {
//        var str = PreferenceUtils.getPrefString(
//            this,
//            lastFollowData?.id + resourseid + lastFollowData.repeat_books_id,
//            ""
//        )

        val value = SpUtils.get().getValue(SpConstants.SP_SERVER_FOLLOWUP_RESULT, "")
        if (value.isNotEmpty()) {
            val fromJson = GsonManager.getInstance().fromJson<ArrayList<ServerFollowupResult>>(
                    value,
                    object : TypeToken<ArrayList<ServerFollowupResult>>() {}.type
            )
            val find = fromJson.find {
                it.id == lastFollowData.id + parentResourceId + lastFollowData.repeat_books_id
            }

            return find?.show ?: false
        }

        return false
    }

    fun saveBackReadDialogState(lastFollowData: FollowupData) {
//        PreferenceUtils.setPrefString(
//            this,
//            lastFollowData?.id + resourseid + lastFollowData?.repeat_books_id,
//            "1"
//        )

        val value = SpUtils.get().getValue(SpConstants.SP_SERVER_FOLLOWUP_RESULT, "")
        val fromJson = if (value.isNotEmpty()) {
            GsonManager.getInstance().fromJson<ArrayList<ServerFollowupResult>>(
                    value,
                    object : TypeToken<ArrayList<ServerFollowupResult>>() {}.type
            )
        } else {
            arrayListOf()
        }
        fromJson.add(
                ServerFollowupResult(
                        lastFollowData.id + parentResourceId + lastFollowData.repeat_books_id,
                        true
                )
        )
        SpUtils.get().putValue(
                SpConstants.SP_SERVER_FOLLOWUP_RESULT,
                GsonManager.getInstance().toJson(fromJson)
        )

    }

    //显示下一条内容
    private fun setNextContent(isAllFinish: Boolean) {


        showFollowUp(isAllFinish)
        changeReadCnotent()
    }


    /**
     * 展示朗读结果
     * @param status 0成功，1失败，2，全部完成
     *
     */
    private fun showBackReadResultDialog(status: Int, isAllFinish: Boolean) {
        val followupStatusDialog = FollowupStatusDialog()
        followupStatusDialog.followUpState = status
        followupStatusDialog.onConfirm = {
            if (isAllFinish) {
                //全部读完,退出当前页面
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                changeReadCnotent()
            }
        }

        followupStatusDialog.show(supportFragmentManager, "")
    }

    /***
     * 显示评测中弹窗
     */
    private fun showEvaluatingDialog() {


        val content = "当前朗读内容正在评测中,请稍后再来查看结果。"
        val positiveStr = "确定"

//        var dialog: PublicOneDialog =
//            PublicOneDialog(this@FollowUpNewActivity, R.style.DefaultDialog);

        XPopup.Builder(this)
                .asCustom(CommonAlertPop(this, content, object : CommonAlertPop.Confirm {
                    override val position: Int
                        get() = CommonAlertPop.Position.RIGHT

                    override val text: String
                        get() = positiveStr
                    override val confirmBack: (() -> Unit)
                        get() = {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                }))

                .show()

    }

    var backTestTipPop: BasePopupView? = null

    /**
     * 展示新的评测提示弹窗
     * 同时开启轮训
     */
    fun showEvaluatingDialogNew(followupData: FollowupData) {

        uploadMp3Pop.setStatusData(followupData.repeat_status)
//        uploadMp3Pop.setPercent(2)
//        uploadMp3Pop.setPorText("排队中...")
        uploadMp3Pop.show()
        postLoopMessage(followupData,0)
    }


    /**
     * 设置跟读按钮可用
     */
    private fun setFollowButtonEnable() {
        contentView.btStartFollowup.isEnabled = true
        contentView.btStartFollowup.alpha = 1f
    }


    /**
     * 跟读资源时，弹出'您没读完整，再来一次吧'，上传跟读音频链接，上传音频url
     */
    private fun postAudioUrl(
            fluency_score: String,
            integrityScore: String,
            url: String,
            followupData: FollowupData?
    ) {
//        val studyplanId = intent.getStringExtra(PARAMS_STUDYPLAN_ID) ?: ""
//        val checkin_resource_id = intent.getStringExtra(PARAMS_CHECKIN_SOURCE_ID) ?: ""

        //上传成功再切换下一个音频
        val jsonObject = JSONObject()
//        jsonObject.put("studyplan_id", studyplanId)
        jsonObject.put("resource_id", parentResourceId)
        jsonObject.put("resource_type", parentResourceType)
        jsonObject.put("repeat_books_id", currentReadBean?.repeat_books_id)

        //如果totalAudioTime为0，不足一秒，置为1
        if (totalAudioTime == 0) {
            totalAudioTime = 1
        }
        jsonObject.put("audio_time", totalAudioTime * 1000)
        jsonObject.put("repeat_books_context_id", followupData?.id)
        jsonObject.put("audio_url", url)
        jsonObject.put("fluency_score", fluency_score)
        jsonObject.put("integrity_score", integrityScore)
        jsonObject.put("fluent_num", followupData?.fluent_num.toString())
        jsonObject.put("finish_num", followupData?.finish_num.toString())

//        val create = RequestBody.create(
//            MediaType.parse("application/json; charset=utf-8"),
//            jsonObject.toString()
//        )

        val create = RequestBodyUtil.fromJson(jsonObject)
        ApiService.getRetrofit().create(UserApi::class.java).postResourceUrl(create)
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<HttpResponse<Any>>(this, false) {
                    override fun onSuccess(t: HttpResponse<Any>?) {
                    }

                    override fun onFailure(code: Int, message: String?) {
                        super.onFailure(code, message)
                    }
                })
    }

    //上传错误
    private fun postFollowError(
            fluency_score: String,
            integrityScore: String,
            url: String,
            followupData: FollowupData?,
            desc: String,
            code: Int
    ) {
//        val studyplanId = intent.getStringExtra(PARAMS_STUDYPLAN_ID) ?: ""
//        val checkin_resource_id = intent.getStringExtra(PARAMS_CHECKIN_SOURCE_ID) ?: ""

//        var error = FollowError(code, desc);

        var error = hashMapOf<String, String>("code" to code.toString(), "msg" to desc)
        //上传成功再切换下一个音频
        val jsonObject = JSONObject()
        jsonObject.put("resource_id", parentResourceId)
        jsonObject.put("resource_type", parentResourceType)
        jsonObject.put("repeat_books_id", currentReadBean?.repeat_books_id)

        //如果totalAudioTime为0，不足一秒，置为1
        if (totalAudioTime == 0) {
            totalAudioTime = 1
        }
        jsonObject.put("audio_time", totalAudioTime * 1000)
        jsonObject.put("repeat_books_context_id", followupData?.id)
        jsonObject.put("audio_url", url)
        jsonObject.put("fluency_score", fluency_score)
        jsonObject.put("integrity_score", integrityScore)
        jsonObject.put("fluent_num", followupData?.fluent_num.toString())
        jsonObject.put("finish_num", followupData?.finish_num.toString())
//        jsonObject.put("extra", StringEscapeUtils.unescapeJava(Gson().toJson(error)));
        jsonObject.put("extra", GsonManager.getInstance().toJson(error));

//        val create = RequestBody.create(
//            MediaType.parse("application/json; charset=utf-8"),
//            jsonObject.toString()
//        )

        val create = RequestBodyUtil.fromJson(jsonObject)
        ApiService.getRetrofit().create(UserApi::class.java).postResourceUrl(create)
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<HttpResponse<Any>>(this, false) {
                    override fun onSuccess(t: HttpResponse<Any>?) {
                    }

                    override fun onFailure(code: Int, message: String?) {
                        super.onFailure(BaseObserver.CODE_NO_TOAST, message)
                    }
                })
    }

    var status: Int = -1

    /**
     * 上传音频url
     */
    private fun postAudioUrl(url: String, postResourceId: String?) {


//        val studyplanId = intent.getStringExtra(PARAMS_STUDYPLAN_ID) ?: ""
//        val checkin_resource_id = intent.getStringExtra(PARAMS_CHECKIN_SOURCE_ID) ?: ""

        //上传成功再切换下一个音频
        val jsonObject = JSONObject()
//        jsonObject.put("studyplan_id", studyplanId)
        jsonObject.put("resource_id", parentResourceId)
        jsonObject.put("resource_type", parentResourceType)
        jsonObject.put("repeat_books_id", currentReadBean?.repeat_books_id)

        //如果totalAudioTime为0，不足一秒，置为1
        if (totalAudioTime == 0) {
            totalAudioTime = 1
        }
        jsonObject.put("audio_time", totalAudioTime * 1000)
        jsonObject.put("repeat_books_context_id", postResourceId)
        jsonObject.put("audio_url", url)

//        val create = RequestBody.create(
//            MediaType.parse("application/json; charset=utf-8"),
//            jsonObject.toString()
//        )
        val create = RequestBodyUtil.fromJson(jsonObject)
        ApiService.getRetrofit().create(UserApi::class.java).postResourceAudioUrl(create)
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<HttpResponse<Any>>(this, false) {
                    override fun onSuccess(t: HttpResponse<Any>?) {
                        if (t?.isSuccess != true) return

                        //上传成功删除本地文件
                        delMp3File()
                        //根据当前位置索引
                        status = if (followupResourceList?.lastIndex == currentReadIndex)
                            FollowupStatusDialog.followUpState_all_complete else FollowupStatusDialog.followUpState_Success
                        showReadResultDialog(status)
                    }

                    override fun onFailure(code: Int, message: String?) {
                        super.onFailure(BaseObserver.CODE_HAS_TOAST, message)
                        showReadCompleteDialog(result)
                    }
                })
    }

//    var isWait = false;  //是自己评测还是服务器评测
//    var wait_time = 0

    //获取是否超过队列
    private fun getRepeatIsOverLimit() {
//        val studyplanId = intent.getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_ID) ?: ""
//        val studyplanId = intent.getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_TYPE) ?: ""

        currentReadBean?.apply {
            ApiService.getRetrofit().create(UserApi::class.java)
                    .getResourceAudioLimit(followUpResourceId,this.id, parentResourceId,parentResourceType)
                    .compose(RxUtils.applySchedulers())
                    .subscribe(object :
                            BaseObserver<HttpResponse<OverLimitBean>>(this@FollowUpActivity, false) {
                        override fun onSuccess(t: HttpResponse<OverLimitBean>?) {
                            timestamp = t?.data?.timestamp ?: ""
                            if (t?.code != 4103) { //不超出最大检测长度，sdk可以直接评
                                uploadMp3ToTecent(contentView.tvContent.text.toString())
                                return
                            }
                            //上传本地音频到华为云
                            if (!TextUtils.isEmpty(mOral.currenMp3)) {
                                uploadRepeatAudio(
                                        File(mOral.currenMp3),
                                        currentReadBean?.id,
                                        totalAudioTime.toString(),
                                        true, true);
                            }

                        }

                        override fun onFailure(code: Int, message: String?) {
                            super.onFailure(code, message)

                        }

                    })
        }

    }

    var timestamp: String = "";


    //从队列里删除
    private fun delRepeatAudioLimit() {

//        val studyplanId = intent.getStringExtra(PARAMS_STUDYPLAN_ID) ?: ""

        val jsonObject = JSONObject()
        jsonObject.put("resource_id", parentResourceId)
        jsonObject.put("resource_type", parentResourceType)
        jsonObject.put("repeat_books_context_id", currentReadBean?.id)
        jsonObject.put("repeat_books_id", currentReadBean?.repeat_books_id)
        jsonObject.put("timestamp", timestamp)


        ApiService.getRetrofit().create(UserApi::class.java)
                .delResourceAudioLimit(RequestBodyUtil.fromJson(jsonObject))
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<HttpResponse<Any>>(this, false) {
                    override fun onSuccess(t: HttpResponse<Any>?) {

                    }

                    override fun onFailure(code: Int, message: String?) {
                        super.onFailure(BaseObserver.CODE_NO_TOAST, message)
                    }

                });
    }

    var isUploadToHuaweiCloud = false //是否是上传华为云的状态
    //超过并发上传华为云


    /**
     * 上传华为云
     * @param isUploadWait 上传并等待后端评测
     */
    private fun uploadRepeatAudio(
            file: File,
            repeat_books_context_id: String?,
            audio_time: String,
            is_success: Boolean, isUploadWait: Boolean =false
    ) {

        if (isUploadWait) {
            uploadMp3Pop.setPercent(50)
            uploadMp3Pop.setPorText(FollowUpPopW2.STATUS_UPLOAD)
            uploadMp3Pop.show()
        }
//        else {
//            uploadMp3Pop.setPercent(0)
//            uploadMp3Pop.setPorText(FollowUpPopW2.STATUS_TEST)
//            uploadMp3Pop.show()
//        }

        isUploadToHuaweiCloud = true
//        val resourceId = intent.getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_ID) ?: ""
//        val resourceType = intent.getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_TYPE) ?: ""

        val requestBody = RequestBody.create("image/*;charset=utf-8".toMediaTypeOrNull(), file)
        val n1 = MultipartBody.Part.createFormData("is_wait", isUploadWait.toString());

        val n2 = MultipartBody.Part.createFormData("resource_id", parentResourceId);
        val n6 = MultipartBody.Part.createFormData("resource_type", parentResourceType);
        val n3 = MultipartBody.Part.createFormData("repeat_books_context_id", repeat_books_context_id ?: "");
        val n4 = MultipartBody.Part.createFormData("repeat_books_id", currentReadBean?.repeat_books_id ?: "");
        val n5 = MultipartBody.Part.createFormData("audio_time", audio_time);

        val list = listOf<MultipartBody.Part>(n1, n2, n3, n4, n5,n6)


        ApiService.getRetrofit().create(UserApi::class.java).uploadResourceAudio(list, requestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<HttpResponse<FollowUrlBean>>(this, false) {
                    override fun onSuccess(t: HttpResponse<FollowUrlBean>?) {
                        isUploadToHuaweiCloud =false
                        //显示评测中弹窗
                        if (isUploadWait) {   //是服务器评测
                            this@FollowUpActivity.currentReadBean?.let {
                                showEvaluatingDialogNew(it)
                            }
                            delMp3File()
                            return
                        }
                        uploadMp3Pop.dismiss()
                        if (is_success) {  //腾讯评测成功后上传Url
                            postAudioUrl(t!!.data.url, currentReadBean?.id);
                        } else {
                            postAudioUrl(
                                    fluency_score!!,
                                    integrityScore!!,
                                    t!!.data.url,
                                    currentReadBean
                            );
                        }

                    }

                    override fun onFailure(code: Int, message: String?) {
                        super.onFailure(BaseObserver.CODE_NO_TOAST, message)
//                        showReadResultDialog(FollowupStatusDialog.followUpState_fill)
                        isUploadToHuaweiCloud = false
                        uploadMp3Pop.dismiss()
                        if (isUploadWait) {
                            delMp3File()
                        }
                    }
                })
    }

    /**
     * 当上传音频失败
     * 如果文件存在重新上传一遍，最多3次
     * 如果不存在检测网络状态，提示用户打开网络并重新跟读
     */
    private fun onPostAudioFail() {
        toast("音频上传失败，请重新上传")
    }

    var isDestroy = false;

    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }
        mHandler.removeCallbacksAndMessages(null)
        isDestroy = true;
//        Glide.clear(contentView.ivReading)
        mOral.stopRecord(TAIOralEvaluationCallback {

        })
        stopTime()

    }

    /**
     * 发送开启轮询消息
     */
    fun postLoopMessage(followupData: FollowupData,waitTime:Int = 3) {
        mHandler.removeMessages(MHandler.MESSAGE_LOOP)
        mHandler.currentFollowUpData = followupData
        mHandler.sendEmptyMessageDelayed(MHandler.MESSAGE_LOOP,waitTime * 1000L);
    }

    /**
     * 检测后台评测是否成功
     */
    fun checkBackTestSuccess(currentReadBean: FollowupData) {

//        val resourseid = intent.getStringExtra(PARAMS_CHECKIN_SOURCE_ID) ?: ""
        ApiService.getRetrofit().create(UserApi::class.java)
                .loopFollowupResult(
                        parentResourceId,
                    parentResourceType,
                        currentReadBean.repeat_books_id ?: "",
                        currentReadBean.id ?: ""
                )
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<HttpResponse<LoopBean>>(this) {
                    override fun onSuccess(t: HttpResponse<LoopBean>) {
                        if (t.code == 4103) {          //code 200后台评测完成，  //1 排队， 2评测

                            uploadMp3Pop.setStatusData(t.data)
                            uploadMp3Pop.show()

                            if("1".equals(t.data.status)){
                                postLoopMessage(currentReadBean,1)
                            }else{
                                postLoopMessage(currentReadBean)
                            }
                        }

                        if (t.code == 200) {
                            uploadMp3Pop.dismiss()
                            onBackTestResult(t.data.success) //date,评测是否通过


                        }


                    }

                    override fun onFailure(code: Int, message: String?) {
                        super.onFailure(code, message)

                        postLoopMessage(currentReadBean)
                    }
                })
    }

    fun onBackTestResult(success: Boolean) {

        backTestTipPop?.dismiss()
        backTestTipPop = null


        //上传成功删除本地文件
        delMp3File()
        //消失弹窗
        showReadEnd()
        delRepeatAudioLimit()
        if (success) {      //评测成功
            if (currentReadIndex == -1) {
                onBackTestFinishi(true)
            }
            //根据当前位置索引,弹提示
            val status = if (followupResourceList?.lastIndex == currentReadIndex)
                FollowupStatusDialog.followUpState_all_complete else FollowupStatusDialog.followUpState_Success

            //记录一下弹出过成功弹窗
            followupResourceList?.get(currentReadIndex)?.let {
                saveBackReadDialogState(it)
            }
            showReadResultDialog(status)


        } else {
            if (currentReadIndex == -1) {
                onBackTestFinishi(false)
            }

            showReadResultDialog(FollowupStatusDialog.followUpState_fill)
        }


    }

    /**
     * 如果成功index+1
     * 如果失败查找第一个没过的
     */
    fun onBackTestFinishi(success: Boolean) {
        //查找第一个未达标的资源，设置相应内容开始跟读
        followupResourceList?.apply {
            for (i in this.indices) {
                if (this[i].status == "0") {

                    currentReadIndex = i
                    break
                }
            }

//            if(success){
//                currentReadIndex += 1
//            }

            if (currentReadIndex in this.indices) {
                currentReadBean = this[currentReadIndex]
            } else {
                currentReadBean = null
            }

            var isAllFinish = false //是否全部完成
            //如果都已完成，那么显示第最后一个跟读内容，并显示完成状态
            if (currentReadBean == null) {
                currentReadIndex = followupResourceList?.lastIndex ?: 0
                currentReadBean = followupResourceList?.get(currentReadIndex)
                isAllFinish = true
            }



            if (isAllFinish) {
                showBackReadResultDialog(
                        FollowupStatusDialog.followUpState_all_complete,
                        isAllFinish
                )
                return@apply
            }

            showFollowUp(isAllFinish)
            changeReadCnotentNew()


        }
    }


    /**
     * 播放计时类
     */
    class MHandler(mActivity: Activity) : Handler() {
        var playTime = 0
        var weakActivity = WeakReference<Activity>(mActivity)

        var currentFollowUpData: FollowupData? = null

        companion object {
            val LOOPTIME = 3
            val MESSAGE_LOOP = 1
        }


        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_LOOP -> {
                    playTime++
                    if (playTime > LOOPTIME && weakActivity.get() != null) {
                        currentFollowUpData?.let {
                            (weakActivity.get() as FollowUpActivity).checkBackTestSuccess(it)
                        }
                        return
                    }
                    sendEmptyMessageDelayed(MESSAGE_LOOP, 1000)
                }
            }
            super.handleMessage(msg)
        }
    }
}