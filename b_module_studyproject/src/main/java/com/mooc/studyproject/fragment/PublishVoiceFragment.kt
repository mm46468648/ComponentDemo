package com.mooc.studyproject.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.NetUtils
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.StudyprojectFragmentPublishVoiceBinding
import com.mooc.studyproject.model.StudyPlanSource
import com.mooc.studyproject.ui.PublishingDynamicsActivity
import com.mooc.studyproject.ui.PublishingDynamicsCommentActivity
import com.mooc.studyproject.utils.AudioRecoderUtils
import com.mooc.studyproject.utils.ToolUtil
import com.mooc.studyproject.viewmodel.StudyProjectViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
//import kotlinx.android.synthetic.main.studyproject_share_view_study_plan.*
//import kotlinx.android.synthetic.main.studyproject_fragment_publish_voice.*
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 发表语音动态页面
 */
class PublishVoiceFragment : Fragment(), AudioRecoderUtils.OnPlayStatusUpdateListener {

    private var mAudioRecoderUtils: AudioRecoderUtils? = null
    private var param1: String? = null
    private var param2: String? = null
    var planId: String? = null
    var studyPlanSource: StudyPlanSource? = null
    var voicePath = ""
    var dialog: CustomProgressDialog? = null
    private val minRecordTime = 1000 //1秒
    private var isPublishComment = false //是否是发表评论

    private var dynamicId = "" //评论的动态id

    private var commendid = "" //评论的id

    private var commendUserId = "" //评论用户的id

    var rxPermissions: RxPermissions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        planId = arguments?.getString(
            PublishingDynamicsCommentActivity.STUDY_PLAN_ID,
            commendUserId
        ) ?: ""

        studyPlanSource =
            arguments?.getParcelable(PublishingDynamicsActivity.INTENT_STUDY_PLAN_RESOURCE)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            isPublishComment = arguments?.getBoolean(PublishingDynamicsCommentActivity.IS_PUBLISH_COMMENT, false)
                    ?: false
            dynamicId = it.getString(PARAMS_DYNAMICID_KEY, dynamicId)
            commendid = it.getString(PARAMS_COMMENTID_KEY, commendid)
            commendUserId = it.getString(PARAMS_COMMENTUSERID_KEY, commendUserId)
        }
    }

    //    val model: StudyProjectViewModel by lazy {
//        ViewModelProviders.of(this).get(StudyProjectViewModel::class.java)
//    }
    val model: StudyProjectViewModel by viewModels()

    private var _binding : StudyprojectFragmentPublishVoiceBinding? = null
    private val  binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = StudyprojectFragmentPublishVoiceBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    companion object {
        //        const val IS_PUBLISH_COMMENT = "params_publishcomment_key"
        const val PARAMS_DYNAMICID_KEY = "params_dynamicid_key"
        const val PARAMS_COMMENTID_KEY = "params_commentid_key"
        const val PARAMS_COMMENTUSERID_KEY = "params_commentuserid_key"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                PublishVoiceFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)

                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rxPermissions = activity?.let { RxPermissions(it) }
        initData()
        initListener()
        initDataListener()
    }

    private fun initData() {
        mAudioRecoderUtils = AudioRecoderUtils()
        mAudioRecoderUtils?.setContext(activity)

        mAudioRecoderUtils?.setOnAudioStatusUpdateListener(object :
                AudioRecoderUtils.OnAudioStatusUpdateListener {
            override fun onUpdate(db: Double, time: Long) {
                binding.timeTv.text = ToolUtil.formatTime(time)
            }

            override fun onStop(filePath: String) {
                if (filePath == AudioRecoderUtils.CHECK_PERMISSION || binding.timeTv.text
                                .toString() == "00:00"
                ) {

                    mAudioRecoderUtils?.cancelRecord()
                    binding.timeTv.setTextColor(resources.getColor(R.color.color_9))
                    binding.timeTv.text = "00:00"
                    binding.tipPress.text = "按住录音"
                    showRecordView()
                    return
                }
                if ((mAudioRecoderUtils?.totalTime ?: 0) < minRecordTime) {
                    mAudioRecoderUtils?.cancelRecord()
                    toast("录制时间太短")
                    return
                }
                voicePath = filePath
                mAudioRecoderUtils?.filePath = filePath
                mAudioRecoderUtils?.refreshViewPrepare()
                showSendView()
            }
        })
        mAudioRecoderUtils?.setPlayView(binding.voicePlayerLL)
        mAudioRecoderUtils?.setPlayStatusUpdateListener(this)
        if (isPublishComment) {
            binding.btnVoiceSubmit.text = "提交评论"
        } else {
            binding.btnVoiceSubmit.text = "提交动态"
        }
        binding.btnVoiceSubmit.isEnabled = true
    }

    private fun initDataListener() {
        // 发布动态结果处理
        model.dynamicsBean.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.btnVoiceSubmit.isEnabled = true
                toast(it.msg)
                if (it.error_code == 10004) {
                } else {
                    binding.btnVoiceSubmit.isEnabled = false
                    val intent = Intent()
                    intent.putExtra(IntelligentLearnListFragment.INTENT_STUDY_PLAN_DYNAMIC, it)
                    activity?.setResult(Activity.RESULT_OK, intent)
                    activity?.finish()
                }
            }
        })
        //上传语音文件
        model.uploadVoiceBean.observe(viewLifecycleOwner, Observer {
            dialogDismiss()
            if (it?.success == true) {
                binding.btnVoiceSubmit.isEnabled = false
                publishDynamicsToNet(it.url)
            } else {
                binding.btnVoiceSubmit.isEnabled = true
                toast(getString(R.string.toast_get_upvoice_faile))
            }
        })
        // 发布动态失败结果处理
        model.dynamicsException.observe(viewLifecycleOwner, {
            binding.btnVoiceSubmit.isEnabled = true
        })
        //上传语音失败的结果处理
        model.exception.observe(viewLifecycleOwner, {

            binding.btnVoiceSubmit.isEnabled = true
            dialogDismiss()
            toast(it.toString())
        })
        //发表语音评论的回调
        model.sendCommendBean.observe(viewLifecycleOwner, Observer {
            dialogDismiss()
            binding.btnVoiceSubmit.isEnabled = false
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        })
        /**
         * 发表失败的回调
         */
        model.errException.observe(viewLifecycleOwner, Observer {
            dialogDismiss()
            binding.btnVoiceSubmit.isEnabled = true
            toast(getString(R.string.send_commend_fail))
        })
    }

    private fun dialogDismiss() {
        if (dialog != null) {
            dialog?.dismiss()
        }
    }


    private fun showSendView() {
        binding.voicePlayerLL.visibility = View.VISIBLE
        binding.sendLL.visibility = View.VISIBLE
        binding.tipPress.visibility = View.GONE
        binding.timeTv.visibility = View.GONE
        binding.recordImg.visibility = View.GONE
    }


    private fun showRecordView() {
        mAudioRecoderUtils?.delFile()
        binding.voicePlayerLL.setVisibility(View.GONE)
        binding.sendLL.setVisibility(View.GONE)
        binding.recordImg.visibility = View.VISIBLE
        binding.tipPress.setVisibility(View.VISIBLE)
        binding.timeTv.visibility = View.VISIBLE
        binding.recordImg.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun initListener() {
        binding.recordImg.setOnTouchListener(View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!checkPermissions()) {
                        return@OnTouchListener true
                    }

                    val centerY: Float = binding.recordImg.y + binding.recordImg.height / 2
                    binding.wv.setCenterY(centerY)
                    binding.wv.visibility = View.VISIBLE
                    binding.timeTv.setTextColor(resources.getColor(R.color.colorPrimary))
                    mAudioRecoderUtils?.startRecord()
                    binding.wv.start()
                    binding.tipPress.text = "开始录音"
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mAudioRecoderUtils?.stopRecord()
                    binding.wv.stop()
                    binding.wv.visibility = View.GONE
                }
            }
            true
        })

        binding.btnVoiceSubmit.setOnClickListener {
            binding.btnVoiceSubmit.isEnabled = false
            upLoadVoiceFile2Net(voicePath)
        }

        binding.resetVoiceBtn.setOnClickListener(View.OnClickListener {
            voicePath = ""
            mAudioRecoderUtils?.cancelRecord()
            binding.timeTv.setTextColor(resources.getColor(R.color.color_9))
            binding.timeTv.text = "00:00"
            binding.tipPress.setText("按住录音")
            showRecordView()
        })
        binding.playTv.setOnClickListener(View.OnClickListener {
            val state = mAudioRecoderUtils?.getPlayState()
            if (state == AudioRecoderUtils.PLAY_STATE_PLAYING) {
                mAudioRecoderUtils?.pausePlay()
            } else if (state == AudioRecoderUtils.PLAY_STATE_PAUSE) {
                mAudioRecoderUtils?.continuePlay()
            } else if (state == AudioRecoderUtils.PLAY_STATE_PREPARE || state == AudioRecoderUtils.PLAY_STATE_COMPLETE) {
                mAudioRecoderUtils?.startPlay(mAudioRecoderUtils?.getFilePath())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAudioRecoderUtils?.cancelRecord()
    }

    //请求权限
    fun checkPermissions(): Boolean {
        if (rxPermissions?.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true && rxPermissions?.isGranted(
                        Manifest.permission.RECORD_AUDIO) == true) {
            return true
        }

        rxPermissions?.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        )?.subscribe {
            return@subscribe
        }
        return false

//        PermissionUtil.hasPermissionRequest({Manifest.permission.WRITE_EXTERNAL_STORAGE,})
    }


    @SuppressLint("SetTextI18n")
    fun refreshViewComplete(total: Int) {
        binding.totalTime.setText(ToolUtil.formatTime(total.toLong()))
        binding.playVoice.setImageResource(R.mipmap.common_ic_voice_play)
        binding.playPrg.setProgress(0)
        binding.proTime.setText("00:00")
    }

    @SuppressLint("SetTextI18n")
    fun refreshViewPrepare(total: Int) {
        binding.playVoice.setImageResource(R.mipmap.common_ic_voice_play)
        binding.totalTime.setText(ToolUtil.formatTime(total.toLong()))
        binding.proTime.setText("00:00")
        binding.playPrg.setMax(total)
        binding.playPrg.setProgress(0)
    }

    private fun refreshViewUpdate(currentTime: Int, total: Int) {
        binding.playVoice.setImageResource(R.mipmap.common_ic_voice_pause)
        binding.totalTime.setText(
                mAudioRecoderUtils?.getTotalTime()?.toLong()?.let { ToolUtil.formatTime(it) })
        mAudioRecoderUtils?.getCurrentPro()?.let { binding.playPrg.setProgress(it) }
        binding.proTime.setText(
                mAudioRecoderUtils?.getCurrentPro()?.toLong()?.let { ToolUtil.formatTime(it) })
    }


    fun refreshViewPause() {
        binding.playVoice.setImageResource(R.mipmap.common_ic_voice_play)
        binding.totalTime.setText(
                mAudioRecoderUtils?.getTotalTime()?.toLong()?.let { ToolUtil.formatTime(it) })
        mAudioRecoderUtils?.getCurrentPro()?.let { binding.playPrg.setProgress(it) }
        binding.proTime.setText(
                mAudioRecoderUtils?.getCurrentPro()?.toLong()?.let { ToolUtil.formatTime(it) })
    }

    /**
     * 上传语音文件
     */
    private fun upLoadVoiceFile2Net(path: String) {
        if (TextUtils.isEmpty(path)) {
            binding.btnVoiceSubmit.isEnabled = true
            return
        }
        mAudioRecoderUtils?.stopPlay()
        mAudioRecoderUtils?.refreshViewPrepare()
        dialog = context?.let { CustomProgressDialog.createLoadingDialog(it) }
        dialog?.show()
        val file = File(path)
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val toRequestBody = file.asRequestBody("image/*; charset=utf-8".toMediaTypeOrNull())
        builder.addPart(
                Headers.headersOf(
                        "Content-Disposition",
                        "form-data; name=\"" + "file" + "\"; filename =\"" + file.name
                ), toRequestBody
        )
        model.publishVoice(builder.build())


    }

    private fun publishDynamicsToNet(voicePath: String) {
        if (TextUtils.isEmpty(voicePath)) {
            binding.btnVoiceSubmit.isEnabled = true
            return
        }

        if (!NetUtils.isNetworkConnected()) {
            binding.btnVoiceSubmit.isEnabled = true
            toast(getString(R.string.net_error))
            return
        }
        if (isPublishComment) {          //如果用户评论动态，请求评论接口
            val requestData = JSONObject()
            try {
                requestData.put("study_activity", dynamicId)
                requestData.put("comment_user_id", GlobalsUserManager.uid)
                requestData.put("comment_content", voicePath)
                requestData.put("comment_type", "1")

                requestData.put(
                        "comment_content_long",
                        java.lang.String.valueOf(mAudioRecoderUtils?.getTotalTime())
                )

                if (!TextUtils.isEmpty(commendid) && !TextUtils.isEmpty(commendUserId)) { //如果评论id不为空，代表回复评论
                    requestData.put("comment_to", commendid)
                    requestData.put("receiver_user_id", commendUserId)
                } else {
                    requestData.put("comment_to", "")
                    requestData.put("receiver_user_id", "0")
                }


            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val stringBody =
                    requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

            model.postCommentData(dynamicId.toString(), stringBody)

            return
        }
        val requestData = JSONObject()
        try {
            requestData.put("study_plan", planId.toString())
            requestData.put("publish_content", voicePath)
            requestData.put("publish_img", "")
            requestData.put("user_id", GlobalsUserManager.uid)
            requestData.put("publish_state", "1")
            requestData.put("activity_type", "1")
            requestData.put(
                    "activity_content_long",
                    java.lang.String.valueOf(mAudioRecoderUtils?.getTotalTime())
            )
            if (studyPlanSource != null) {
                requestData.put("activity_checkin_type", "0")
                requestData.put("checkin_source_id", java.lang.String.valueOf(studyPlanSource?.id))
                if (studyPlanSource?.is_re_chick == true) {
                    requestData.put("is_new_checkin", "1")
                    requestData.put("activity_bu_type", "1")
                }
            } else {
                requestData.put("activity_checkin_type", "1")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody =
                requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

        model.publishDynamics(planId.toString(), stringBody)

    }

    override fun playStart(totalPro: Int) {
        refreshViewUpdate(totalPro, totalPro)
    }

    override fun onUpdate(currentPro: Int) {
        mAudioRecoderUtils?.getTotalTime()?.let { refreshViewUpdate(currentPro, it) }
    }

    override fun onPlayComplete() {
        mAudioRecoderUtils?.getTotalTime()?.let { refreshViewComplete(it) }
    }

    override fun onPlayPause() {
        refreshViewPause()

    }

    override fun onContinuePlay() {
    }

    override fun onError() {
    }

    override fun onDestroy() {
        mAudioRecoderUtils?.releasePlay()
        mAudioRecoderUtils?.releaseAudio()
        super.onDestroy()
    }

}