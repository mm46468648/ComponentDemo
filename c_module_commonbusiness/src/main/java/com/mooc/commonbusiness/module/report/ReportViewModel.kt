package com.mooc.commonbusiness.module.report

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.model.ReportBean
import com.mooc.commonbusiness.model.ReportChoicesBean
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toJSON
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.my.UploadFileBean
import com.ypx.imagepicker.bean.ImageItem
import kotlinx.coroutines.*
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 举报页面
 */
class ReportViewModel(var resourceType: String) : BaseViewModel() {

    val mRepository = ReportRepository()

    val onRequsetStatus = MutableLiveData<Boolean>()

    val reportChoiceLiveData: MutableLiveData<ReportChoicesBean> by lazy {
        MutableLiveData<ReportChoicesBean>().also {
            if (resourceType == ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC.toString())
                loadDynamicReportChoices()
            else
                loadReportChoices()
        }
    }


    /**
     * 获取举报选项
     */
    fun loadReportChoices() {
        launchUI {
            val loadReportChoices = mRepository.loadReportChoices()
            reportChoiceLiveData.postValue(loadReportChoices)
        }
    }

    /**
     * 获取学习项目动态举报选项
     */
    fun loadDynamicReportChoices() {
        launchUI {
            val loadReportChoices = mRepository.loadDynamicReportChoices()
            reportChoiceLiveData.postValue(loadReportChoices)
        }
    }


    fun postRequst(content: String, imageNetPathJson: String? = null) {


    }

    val minContentLength = 10  //内容最少字数
    val minImageCount = 1 //最少上传图片数量

    /**
     * 检查举报内容
     */
    @Deprecated("请使用checkReportContent2")
    fun checkReportContent(
        reportBean: ReportBean,
        content: String,
        repostImage: ArrayList<ImageItem>,
        context: Context
    ) {

        if (content.isEmpty()) {
            toast("举报内容为空")
            return
        }
        if (content.length < 10) {
            toast("至少填写${minContentLength}个字");
            return
        }
        if (repostImage.isEmpty()) {
            toast("至少选一张图片")
            return
        }

//        if (repostImage.isEmpty()) {
//            postRequst(content, reportBean)
//        }
        if (repostImage.isNotEmpty()) {  //如果上传的图片不为空
            val createLoadingDialog = CustomProgressDialog.createLoadingDialog(context, true)
            createLoadingDialog.show()
            viewModelScope.launch {
//                val imageNetUrlList = arrayListOf<String>()   //图片网络地址
                val imageNetUrlList = CopyOnWriteArrayList<String>()
                coroutineScope {
                    repostImage.forEachIndexed { index, imageItem ->
                        val fromImageFilePath = RequestBodyUtil.fromImageFilePath(imageItem.path)
                        async {
//                        HttpService.commonApi.postImageFile(fromImageFilePath)
                            postImageFile(fromImageFilePath, imageNetUrlList)
                        }
                    }
                }

                loge(imageNetUrlList, "json:" + imageNetUrlList.toJSON())
                //消失弹窗
                withContext(Dispatchers.Main) { createLoadingDialog.dismiss() }
                //等待图片上传完毕，一起发送
                postRequst(content, reportBean, imageNetUrlList.toJSON())
            }
        }
    }

    /**
     * 新的
     * 检查举报内容
     * @param repostImage 直接传递上传好的图片
     */
    fun checkReportContent2(
        reportBean: ReportBean,
        content: String,
        repostImage: ArrayList<String>,
        context: Context
    ) {

        if (content.isEmpty()) {
            toast("举报内容为空")
            return
        }
        if (content.length < 10) {
            toast("至少填写${minContentLength}个字");
            return
        }
        if (repostImage.isEmpty()) {
            toast("至少选一张图片")
            return
        }

        //等待图片上传完毕，一起发送
        postRequst(content, reportBean, repostImage.toJSON())
    }

    suspend fun postImageFile(
        requestBody: RequestBody,
        imageNetUrlList: CopyOnWriteArrayList<String>
    ) {
        try {
            val s = HttpService.commonApi.postImageFile(requestBody).await().data.url ?: ""
            imageNetUrlList.add(s)
        } catch (e: Exception) {

        }
    }

    suspend fun postImageFile(requestBody: RequestBody): HttpResponse<UploadFileBean> {
        return HttpService.commonApi.postImageFile(requestBody).await()
    }

    private fun postRequst(
        content: String,
        reportBean: ReportBean,
        imageNetPathJson: String? = null
    ) {
        val requestData = JSONObject()
        try {
            if (reportBean.resourceType == ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC) {
                requestData.put("report_reason_id", reportBean.report_id.toString())
                requestData.put("activity_id", reportBean.resourceId)
            } else {
                requestData.put("report_id", reportBean.report_id.toString())
                requestData.put("resource_id", reportBean.resourceId)
                requestData.put("resource_title", reportBean.resourceTitle)
                requestData.put("type_num", reportBean.resourceType.toString())
            }
            requestData.put("content", content)
            if (imageNetPathJson?.isNotEmpty() == true) {
                requestData.put("publish_img", imageNetPathJson)
            }

            val fromJson = RequestBodyUtil.fromJson(requestData)
            launchUI(
                {
                    val await =
                        if (reportBean.resourceType == ResourceTypeConstans.TYPE_STUDY_PLAN_DYNAMIC) {
                            HttpService.commonApi.postDynamicReportData(fromJson).await()
                        } else {
                            HttpService.commonApi.postReportData(fromJson).await()
                        }
                    toast(await.message)
                    onRequsetStatus.postValue(true)
                },
                {
                    toast(it.toString())
                }
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}