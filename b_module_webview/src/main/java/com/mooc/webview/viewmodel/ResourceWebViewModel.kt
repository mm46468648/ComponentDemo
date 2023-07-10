package com.mooc.webview.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.ResDataBean
import com.mooc.commonbusiness.model.VerifyCode
import com.mooc.commonbusiness.model.external.LinkArticleBean
import com.mooc.commonbusiness.model.external.UserPermissionBean
import com.mooc.commonbusiness.model.home.NoteBean
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.commonbusiness.model.search.PeriodicalBean
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.model.studyroom.AddFloderResultBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.commonbusiness.utils.share.ShareSchoolUtil
import com.mooc.webview.api.RecommendApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.htmlparser.Node
import org.htmlparser.NodeFilter
import org.htmlparser.Parser
import org.htmlparser.tags.MetaTag
import org.htmlparser.tags.TitleTag
import org.htmlparser.visitors.ObjectFindingVisitor
import org.json.JSONException
import org.json.JSONObject

/**
 * 资源类型h5页面ViewModel
 * @Author limeng
 * @Date 2021/1/8-3:09 PM
 */
class ResourceWebViewModel : BaseViewModel() {
    //    var httpBean = MutableLiveData<HttpBean?>();
    val studyRoomService: StudyRoomService? =
            ARouter.getInstance().navigation(StudyRoomService::class.java)

    val articleWebShareDetaildata: MutableLiveData<ShareDetailModel> by lazy {
        MutableLiveData<ShareDetailModel>()
    }

    val upVerifyLiverData: MutableLiveData<HttpResponse<Boolean>> by lazy {
        MutableLiveData<HttpResponse<Boolean>>()
    }
    val titleLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val userPermissionLiveData: MutableLiveData<UserPermissionBean> by lazy {
        MutableLiveData<UserPermissionBean>()
    }

    val uploadArticleLiveData: MutableLiveData<HttpResponse<LinkArticleBean>> by lazy {
        MutableLiveData<HttpResponse<LinkArticleBean>>()
    }

    var postScorllSuccess = false

    fun getShareDetailData(resource_type: String, resource_id: String) {
        launchUI {
            val sharedata =
                    HttpService.commonApi.getShareDetailDataNew(ShareTypeConstants.SHARE_TYPE_RESOURCE, resource_type, resource_id)
            if (sharedata.isSuccess) {
                articleWebShareDetaildata.postValue(sharedata.data)
            }
        }
    }

    fun getStudyroomEnrollState(resourceType: Int, url: String): LiveData<ShareDetailModel> {
        val liveData = MutableLiveData<ShareDetailModel>()
        launchUI {
            val fromJsonStr = RequestBodyUtil.fromJsonStr(

                    GsonManager.getInstance()
                            .toJson(hashMapOf("resource_url" to url, "resource_type" to resourceType))
            )
            val sharedata = HttpService.commonApi.getResourceEnroll(fromJsonStr).await();
            liveData.postValue(sharedata.data)
        }
        return liveData
    }

    fun postAddToStudyRoom(
            loadUrl: String,
            resource_type: String,
            other_resource_id: String,
            context: Context
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("url", loadUrl)
        jsonObject.put("resource_type", resource_type)
        jsonObject.put("other_resource_id", other_resource_id)

        if (resource_type == ResourceTypeConstans.TYPE_BAIKE.toString()) {
            jsonObject.put("source", 0)
        }
        val studyRoomService: StudyRoomService? =
                ARouter.getInstance().navigation(StudyRoomService::class.java)
        studyRoomService?.showAddToStudyRoomPop(context, jsonObject) {
            if (it) {
                articleWebShareDetaildata.value?.is_enroll = "1"
                articleWebShareDetaildata.postValue(articleWebShareDetaildata.value)
            }
        }
    }

    fun postBaikeAddToStudyRoom(
            loadUrl: String,
            resource_type: String,
            title: String,
            context: Context
    ): LiveData<Boolean> {

        val addSuccess = MutableLiveData<Boolean>()
        val jsonObject = JSONObject()
        jsonObject.put("url", loadUrl)
        jsonObject.put("resource_type", resource_type)
        jsonObject.put("title", title)
        jsonObject.put("source", 0)
        val studyRoomService: StudyRoomService? =
                ARouter.getInstance().navigation(StudyRoomService::class.java)
        studyRoomService?.showAddToStudyRoomPop(context, jsonObject) {
            addSuccess.postValue(it)
        }
        return addSuccess
    }

    /**
     * TODO 此方法是只有测试卷才调用么？
     */
    fun postResourceScroll(source: String, original_url: String) {
        if (postScorllSuccess) return
        if (!GlobalsUserManager.isLogin()) return

        launchUI {
            val requestData = JSONObject()
            try {
                requestData.put("source", source)
                requestData.put("original_url", original_url)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val stringBody =
                    requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
            val data = HttpService.commonApi.postResourceScroll(stringBody).await();
            postScorllSuccess = true
//            httpBean.postValue(data)
        }
    }

    /**
     * 添加到学习室文件夹
     * @param toFolderId 文件夹id
     * @param jsonObject 资源需要传递的参数
     */
    fun addToFolder(
            toFolderId: String?,
            jsonObject: JSONObject
    ): MutableLiveData<AddFloderResultBean> {
        val liveData = MutableLiveData<AddFloderResultBean>()
        toFolderId?.let {
            launchUI {
                val moveToFolder = studyRoomService?.addToFolder(it, jsonObject)
                liveData.postValue(moveToFolder)
            }
        }
        return liveData
    }

    /**
     * 获取笔记信息
     */
    fun getNodeData(resId: String?): LiveData<NoteBean?> {
        val liveData = MutableLiveData<NoteBean?>()
        launchUI {
            val nodeBean = studyRoomService?.getNotInfo(resId)
            liveData.postValue(nodeBean)
        }
        return liveData
    }

    /***
     * 获取推荐文章
     */
    fun getRecommendArticle(resId: String): LiveData<MutableList<ArticleBean>> {
        val liveData = MutableLiveData<MutableList<ArticleBean>>()
        launchUI {
            val it = ApiService.retrofit.create(RecommendApi::class.java).getRecommendArticle(14, resId).await()
            liveData.postValue(it?.data)
        }
        return liveData
    }


    /**
     * 发送期刊信息入库
     */
    fun postPeriodicalToDB(body: RequestBody): LiveData<PeriodicalBean> {
        val liveData = MutableLiveData<PeriodicalBean>()
        launchUI {
            val await = HttpService.otherApi.postPeiodicalToDB(body).await()
            liveData.postValue(await)
        }
        return liveData

    }

    /**
     * 获取百科分享数据并分享到学友圈
     */
    fun getShareDataAndPost(context: Context, requestData: JSONObject) {
        launchUI {
            flow<ResDataBean> {
                val resData = HttpService.otherApi.getResData(RequestBodyUtil.fromJson(requestData))
                emit(resData)
            }.collect {
                ShareSchoolUtil.postSchoolShare(
                        context,
                        requestData.getString("resource_type"),
                        it.id,
                        it.url
                )
            }
        }
    }

    fun getVerifyCode(id:String): MutableLiveData<HttpResponse<VerifyCode>> {
        val liveData = MutableLiveData<HttpResponse<VerifyCode>>()
        launchUI {
            val await = HttpService.commonApi.getArticleVrifyCode(id).await()
            liveData.postValue(await)
        }
        return liveData;
    }

    fun postVerifyCode(str: String) {
        launchUI {
            val await = HttpService.commonApi.postVerifyCode(RequestBodyUtil.fromJsonStr(str)).await()
            upVerifyLiverData.postValue(await)
        }

    }

    fun getUserPermission(url: String) {

        launchUI {

            withContext(Dispatchers.IO) {
                var title = getArticleTitle(url);
                titleLiveData.postValue(title)
                if (title != null) {
                    if (title.isNotEmpty()) {
                        val await = HttpService.commonApi.getUserPermission().await();
                        userPermissionLiveData.postValue(await)
                    }
                }
            }


        }
    }


    //解析微信文章获取title
    fun getArticleTitle(url: String): String? {
        return try {
            val parser = Parser()
            var titleTag: TitleTag
            val visitor = ObjectFindingVisitor(
                TitleTag::class.java
            )
            parser.url = url
            var title: String? = ""
            parser.parse(object : NodeFilter {
                override fun accept(node: Node?): Boolean {
                    if (node is MetaTag) {
                        val property = node.getAttribute("property");
                        if (property == "og:title") {
                            title = node.metaContent
                            return true
                        }
                    }
                    return false
                }

            })


            title
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun uploadArticleLink(title: String, source_link: String) {
        launchUI {
            val requestData = JSONObject()
            try {
                requestData.put("title", title)
                requestData.put("source_link", source_link)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val stringBody =
                requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
            val await = HttpService.commonApi.uploadArticelLink(stringBody).await()
            uploadArticleLiveData.postValue(await)
        }

    }
}