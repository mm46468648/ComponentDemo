@file:Suppress("DEPRECATION")

package com.mooc.home.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.DateUtil
import com.mooc.common.utils.permission.PermissionRequestCallback
import com.mooc.common.utils.permission.PermissionUtil
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.common.utils.sharepreference.SpUtils
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.model.studyproject.StudyPlan
import com.mooc.commonbusiness.pop.studyproject.MedalPop
import com.mooc.commonbusiness.pop.studyproject.ShowStudyProjectScorePop
import com.mooc.commonbusiness.model.privacy.PrivacyPolicyCheckBean
import com.mooc.home.model.AlertMsgBean
import com.mooc.home.model.DailyReadBean
import com.mooc.home.ui.pop.DailyReadPop
import com.mooc.home.ui.pop.HomeAlertImagePop
import com.mooc.home.ui.pop.HomeAlertMessagePop
import com.mooc.home.ui.pop.PrivacyPolicyBecomesPop
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class HomePresenter(context: Context) {

    private var viewReference: WeakReference<Context>? = WeakReference(context)

    val mViewModel by lazy {
        viewReference?.get().let {
            ViewModelProviders.of(it as AppCompatActivity)[HomeViewModel::class.java]
        }
    }


    /**
     * 请求存储权限
     */
    fun requestPermission() {
        viewReference?.get()?.let {
            val refuse = SpUtils.get().getValue(SpConstants.SP_HOME_STORE_PERMISSION_REQUEST, false)

            if (!refuse && !PermissionUtil.hasPermissionRequest(it, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PermissionApplyActivity.launchActivity(
                        it,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        0,
                        object : PermissionRequestCallback {
                            override fun permissionSuccess() {

                            }

                            override fun permissionCanceled() {
                                SpUtils.get().putValue(SpConstants.SP_HOME_STORE_PERMISSION_REQUEST, true)
                            }

                            override fun permissionDenied() {

                            }
                        })
            }
        }


    }

    //获取首页特殊勋章
    fun getMedia() {
        viewReference?.get().let { context ->
            mViewModel.getSpecialMedal().observe(context as AppCompatActivity, { arrayList ->
                if (arrayList != null && arrayList.size > 0) {
                    // 展示勋章弹窗
                    arrayList.forEach {
                        if (!it.study_medal_img.isNullOrEmpty()) {
                            val medalPop = MedalPop(context, it.study_medal_img)
                            XPopup.Builder(context)
                                    .asCustom(medalPop)
                                    .show()
                        }
                    }

                }

            })
        }

    }

    /**
     * 获取完成的学习项目
     */
    fun getStudyPlanCompletion() {
        viewReference?.get().let { context ->
            mViewModel.getStudyPlanCompletion().observe(context as AppCompatActivity, { arrayList ->
                if (arrayList != null && arrayList.size > 0) {//可以进行弹框处理
                    arrayList.forEach { studyPlan ->
                        if (studyPlan.is_success == 1) {
                            if (studyPlan.is_read == 0) {//未读
                                if (studyPlan.plan_mode_status == 0 || studyPlan.is_calculate == 1) {//弹出来获得积分弹框
                                    showStudyProjectScorePop(context, studyPlan)
                                } else {
                                    showMediaPop(context, studyPlan)
                                }
                            } else {
                                showMediaPop(context, studyPlan)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun showStudyProjectScorePop(context: AppCompatActivity, studyplan: StudyPlan) {
        val centerPopupView = ShowStudyProjectScorePop(context, studyplan)
        XPopup.Builder(context)
                .setPopupCallback(object : SimpleCallback() {
                    override fun onDismiss(popupView: BasePopupView?) {
                        showMediaPop(context, studyplan)
                    }
                })
                .asCustom(centerPopupView)
                .show()
    }


    /**
     * 弹出获得计划勋章动画
     */
    fun showMediaPop(context: Context, studyplan: StudyPlan) {
        if (studyplan.is_read_medal == 0) {//弹出获得计划勋章动画
            val medalpop = MedalPop(context, studyplan.medal_default_link)
            XPopup.Builder(context)
                    .asCustom(medalpop)
                    .show()
        }
    }

    /**
     * 检查时间
     */
    fun checkTime() {
        val date = getTimeNow()
        val readTimeLast = SpDefaultUtils.getInstance().getString(SpConstants.CHECK_TIME, "")
        if (date != readTimeLast) {
            //上传当前时间
            postCheckTime()
        }
    }

    /**
     * 上传检查时间
     */
    private fun postCheckTime() {
        val requestData = JSONObject()
        try {
            requestData.put("user_ts", java.lang.String.valueOf(DateUtil.getCurrentTime() / 1000))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val toRequestBody = requestData.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        viewReference?.get().let {
            mViewModel.postCheckTime(toRequestBody).observe(it as AppCompatActivity, { httpResponse ->

                if (!httpResponse.isSuccess) {//返回false
                    if (!httpResponse.message.isNullOrEmpty()) {
                        toast(httpResponse.message)
                    }
                }
                SpDefaultUtils.getInstance().putString(SpConstants.CHECK_TIME, getTimeNow())
            })
        }


    }

    private fun getTimeNow(): String? {
        @SuppressLint("SimpleDateFormat") val sDateFormat = SimpleDateFormat("yyyyMMdd")
        return sDateFormat.format(Date())
    }


    fun release() {
        viewReference?.clear()
        viewReference = null
    }

    fun showDailyRead(dailyReadBean: DailyReadBean?) {
        if (dailyReadBean == null || dailyReadBean.image_url.isEmpty()) return
        viewReference?.get()?.let {
            XPopup.Builder(it)
                    .maxWidth(360.dp2px())
                    .asCustom(DailyReadPop(it, dailyReadBean))
                    .show()
        }

    }

    fun showPrivacyPolicy(privacyPolicyCheckBean: PrivacyPolicyCheckBean?) {
        if (privacyPolicyCheckBean == null) return
        viewReference?.get()?.let {
            XPopup.Builder(it)
                    .enableDrag(false)
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asCustom(PrivacyPolicyBecomesPop(it, privacyPolicyCheckBean) {
                        SpDefaultUtils.getInstance().putString(SpConstants.SP_PRIVACY_VERSION, privacyPolicyCheckBean.version)
                    })
                    .show()
        }
    }


    /**
     * 展示首页弹窗
     */
    fun showAlertDialog(alertMsgBean: AlertMsgBean) {
        viewReference?.get()?.apply {
            //如果图片不为空，展示图片弹窗,否则判断alert_title不为空展示消息类型弹窗
            val centerPopupView = if (alertMsgBean.alert_img.isNotEmpty()) {
                HomeAlertImagePop(this, alertMsgBean)
            } else {
                if (alertMsgBean.alert_title.isNotEmpty()) {
                    val signleButton = alertMsgBean.resource_type == 0 || (alertMsgBean.resource_id == 0 && alertMsgBean.link.isEmpty())
                    HomeAlertMessagePop(this, alertMsgBean, signleButton)
                } else {
                    null
                }
            }
            if (centerPopupView != null) {
                XPopup.Builder(this)
                        .asCustom(centerPopupView)
                        .show()
            }
        }
    }


}