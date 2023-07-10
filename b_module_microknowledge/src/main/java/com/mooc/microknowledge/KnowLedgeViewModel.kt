package com.mooc.microknowledge

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.TestVolume
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.microknowledge.model.KnowledgeResource
import com.mooc.microknowledge.model.MicroKnowledge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.devilsen.czxing.code.BarcodeWriter
import me.devilsen.czxing.util.BarCodeUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class KnowLedgeViewModel : BaseViewModel() {

    val knowledgeDetailData = MutableLiveData<MicroKnowledge>()
    val knowledgeStatisticsData = MutableLiveData<MicroKnowledge>()
    val knowledgeResourceData = MutableLiveData<List<KnowledgeResource>>()
    val knowledgeTestData = MutableLiveData<List<TestVolume>>()
    val knowledgeMicroRecommendData = MutableLiveData<List<MicroKnowBean>>()

    fun getKnowLedgeDetail(sourceId: String, error: (() -> Unit)?) {
        launchUI({
            val knowLedgeDetail = HttpRequest.api.getKnowLedgeDetail(sourceId).data
            knowledgeDetailData.postValue(knowLedgeDetail)
        }, {
            error?.invoke()
        })
    }

    fun getKnowLedgeStatics(sourceId: String, error: (() -> Unit)?) {
        launchUI({
            val knowLedgeDetail = HttpRequest.api.getKnowLedgeStatistics(sourceId).data
            knowledgeStatisticsData.postValue(knowLedgeDetail)
        }, {
            error?.invoke()
        })
    }

    fun getKnowledgeResource(sourceId: String, error: (() -> Unit)?) {
        launchUI({
            val knowLedgeResource = HttpRequest.api.getKnowLedgeSources(sourceId).data
            knowledgeResourceData.postValue(knowLedgeResource)
        }, {
            error?.invoke()
        })
    }

    fun getKnowledgeMicroRecommend(sourceId: String, error: (() -> Unit)?) {
        launchUI({
            val knowLedgeMicroRecommend = HttpRequest.api.getKnowledgeMicroRecommend(sourceId).data
            knowledgeMicroRecommendData.postValue(knowLedgeMicroRecommend)
        }, {
            error?.invoke()
        })
    }


    fun getKnowledgeTest(sourceId: String, error: (() -> Unit)?) {
        launchUI({
            val knowLedgeTest = HttpRequest.api.getKnowLedgeExam(sourceId).data
            knowledgeTestData.postValue(knowLedgeTest)
        }, {
            error?.invoke()
        })
    }

    fun createShareBitmap(context: Context, knowLedge: MicroKnowledge): Flow<View> {
        return flow {
            val shareImageBitmap = viewModelScope.async(Dispatchers.IO) {
                getImageBitmap(knowLedge.pic) ?: BitmapFactory.decodeResource(
                    context.getResources(),
                    R.mipmap.common_bg_cover_default,
                    null
                )
            }
//            val shareData = MutableLiveData<HttpResponse<CMSShareBean>>()
            val jsonObject = JSONObject()
            jsonObject.put("source_type", ShareTypeConstants.SHARE_TYPE_RESOURCE)
            jsonObject.put("share_resource_type", ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE)
            jsonObject.put("source_id", knowLedge.id)

            val toRequestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val cmsShareBean = HttpService.commonApi.getCMSShareData(toRequestBody).data

            val shareHeadImageBitmap = viewModelScope.async(Dispatchers.IO) {
                getImageBitmap(GlobalsUserManager.userInfo?.avatar ?: "", true)
                    ?: BitmapFactory.decodeResource(
                        context.getResources(),
                        R.mipmap.common_ic_user_head_default,
                        null
                    )
            }

            val inflate = View.inflate(context, R.layout.knowledge_layout_share_invite, null)
            val introTv: TextView = inflate.findViewById(R.id.intro)
            val planIntro: TextView = inflate.findViewById(R.id.plan_intro)
            val nameTv: TextView = inflate.findViewById(R.id.name)
            val qr_img: ImageView = inflate.findViewById(R.id.qr_img)
            val image: ImageView = inflate.findViewById(R.id.img_plan)

            val header: ImageView = inflate.findViewById(R.id.iv_app_user_icon)

            val writer = BarcodeWriter()
            val bitmap = writer.write(
                cmsShareBean.url,
                BarCodeUtil.dp2px(context, 200f),
                Color.BLACK,
            )
            qr_img.setImageBitmap(bitmap)
            introTv.setText(cmsShareBean.invitation_words)
            planIntro.setText(knowLedge.title)
            nameTv.text = "——${GlobalsUserManager.userInfo?.name}"
//            planDes.setText(mStudyPlanDetailBean?.study_plan?.plan_subtitle)
            header.setImageBitmap(shareHeadImageBitmap.await())
            image.setImageBitmap(shareImageBitmap.await())
            emit(inflate)
        }
    }


    fun getImageBitmap(url: String, isCircle: Boolean = false): Bitmap? {
        return try {
            val request = Request.Builder().url(url).build()
            val okHttpClient = OkHttpClient()
            val response = okHttpClient.newCall(request).execute()
            val inputStream = response.body?.byteStream()

            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (isCircle) {
                BitmapUtils.makeRoundCorner(bitmap)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            null
        }
    }

    fun clickPrise(id: String, isLike: Int) {
        launchUI {
            HttpRequest.api.postKnowLedgePrise(id, isLike)
        }
    }
}