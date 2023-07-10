package com.mooc.ebook.viewmodel

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.ebook.EbookApi
import com.mooc.ebook.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.devilsen.czxing.code.BarcodeWriter
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception


class EbookShareViewModel() : BaseViewModel() {

    val EbookShareDetaildata: MutableLiveData<ShareDetailModel> by lazy {
        MutableLiveData<ShareDetailModel>()
    }

    /**
     * 获取cms配置的分享背景图
     * 生成特殊的分享图片，并分享
     * @param resource_type 资源类型
     * @param resource_id 资源id
     * @param recourceCover 资源封面图
     * @param resourceTitle 资源标题
     * @param resourceDes 资源描述
     */
    fun getShareDetailData(
            context: Context,
            resource_type: String,
            resource_id: String,
            recourceCover: String,
            resourceTitle: String,
            resourceDes: String
    ): LiveData<Bitmap> {
        val liveData = MutableLiveData<Bitmap>()
        viewModelScope.launch(Dispatchers.IO) {
            val jsonObject = JSONObject()
            jsonObject.put("source_type", NormalConstants.SHARE_SOURCE_TYPE_E_BOOK)
            jsonObject.put("source_id", resource_id)
            val sharedata =
                    HttpService.commonApi.getCMSShareData(RequestBodyUtil.fromJson(jsonObject)).data;
            //生成邀请的二维码
            val asyncQr = async {
                val writer = BarcodeWriter()
                val bitmap = writer.write(
                        sharedata.url, 90.dp2px(),
                        Color.BLACK, null
                )
                bitmap
            }
            //封面图
            val asyncCover = async {
                val request = Request.Builder().url(
                        recourceCover
                                ?: ""
                ).build()
                val okHttpClient = OkHttpClient()
                val response = okHttpClient.newCall(request).execute()
                val inputStream = response.body?.byteStream()
                val decodeStream = BitmapFactory.decodeStream(inputStream)
                val convertRoundBitmap = BitmapUtils.convertRoundBitmap(decodeStream, 2.dp2px())
                convertRoundBitmap
            }
            //头像的bitmap
            val asyncHead = async {

                try {
                    val request = Request.Builder().url(GlobalsUserManager.userInfo?.avatar
                            ?: "").build()
                    val okHttpClient = OkHttpClient()
                    val response = okHttpClient.newCall(request).execute()
                    val inputStream = response.body?.byteStream()
                    BitmapUtils.makeRoundCorner(BitmapFactory.decodeStream(inputStream))
                } catch (e: Exception) {
                    BitmapFactory.decodeResource(
                            context.getResources(),
                            R.mipmap.common_ic_user_head_default,
                            null
                    )
                }

            }


            //生成分享图
            async(Dispatchers.Main) {
                val inflate = View.inflate(context, R.layout.common_layout_share_invite, null)
                val tvResourceName: TextView = inflate.findViewById(R.id.tvResourceName)
                val tvResourceDes: TextView = inflate.findViewById(R.id.tvResourceDes)
                val tvUserName: TextView = inflate.findViewById(R.id.tvUserName)
                val tvIntro: TextView = inflate.findViewById(R.id.tvIntro)

                val userHeader: ImageView = inflate.findViewById(R.id.ivUserHead)
                val ivQrcode: ImageView = inflate.findViewById(R.id.ivQrcode)
                val ivCover: ImageView = inflate.findViewById(R.id.ivCover)

                userHeader.setImageBitmap(asyncHead.await())
                ivQrcode.setImageBitmap(asyncQr.await())
                ivCover.setImageBitmap(asyncCover.await())

                tvUserName.text = "——" + GlobalsUserManager.userInfo?.name
                tvIntro.setText(sharedata?.invitation_words)
                tvResourceName.setText(resourceTitle)
                tvResourceDes.setText(resourceDes)

                val createUnShowBitmapFromLayout = BitmapUtils.createUnShowBitmapFromLayout(inflate)
                //转换完毕的bitmap
                liveData.postValue(createUnShowBitmapFromLayout)

            }


        }

        return liveData
    }

    private val shareService: ShareSrevice by lazy {
        ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
    }

    /**
     * 获取cms配置的分享背景图
     * 生成特殊的分享图片，并分享
     * @param resource_type 资源类型
     * @param resource_id 资源id
     * @param recourceCover 资源封面图
     * @param resourceTitle 资源标题
     * @param resourceDes 资源描述
     */
    fun makeShareDetailAndShare(
            context: Activity,
            resource_type: String,
            resource_id: String,
            recourceCover: String,
            resourceTitle: String,
            resourceDes: String,
            shareSit: Int
    ) {

        val dialog = CustomProgressDialog.createLoadingDialog(context)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    dialog.show()
                }
                val jsonObject = JSONObject()
                jsonObject.put("source_type", NormalConstants.SHARE_SOURCE_TYPE_E_BOOK)
                jsonObject.put("source_id", resource_id)

                val sharedata =
                        HttpService.commonApi.getCMSShareData(RequestBodyUtil.fromJson(jsonObject)).data
                //生成邀请的二维码
                val asyncQr = async {
                    val writer = BarcodeWriter()
                    val bitmap = writer.write(
                            sharedata.url, 90.dp2px(),
                            Color.BLACK, null
                    )
                    bitmap
                }
                //封面图
                val asyncCover = async {
                    val request = Request.Builder().url(
                            recourceCover
                                    ?: ""
                    ).build()
                    val okHttpClient = OkHttpClient()
                    val response = okHttpClient.newCall(request).execute()
                    val inputStream = response.body?.byteStream()
                    val decodeStream = BitmapFactory.decodeStream(inputStream)
                    val convertRoundBitmap = BitmapUtils.convertRoundBitmap(decodeStream, 2.dp2px())
                    convertRoundBitmap
                }
                //头像的bitmap
                val asyncHead = async {

                    try {
                        val request = Request.Builder().url(GlobalsUserManager.userInfo?.avatar
                                ?: "").build()
                        val okHttpClient = OkHttpClient()
                        val response = okHttpClient.newCall(request).execute()
                        val inputStream = response.body?.byteStream()
                        BitmapUtils.makeRoundCorner(BitmapFactory.decodeStream(inputStream))
                    } catch (e: Exception) {
                        BitmapFactory.decodeResource(
                                context.getResources(),
                                R.mipmap.common_ic_user_head_default,
                                null
                        )
                    }

                }


                //生成分享图
                async(Dispatchers.Main) {
                    val inflate = View.inflate(context, R.layout.common_layout_share_invite, null)
                    val tvResourceName: TextView = inflate.findViewById(R.id.tvResourceName)
                    val tvResourceDes: TextView = inflate.findViewById(R.id.tvResourceDes)
                    val tvUserName: TextView = inflate.findViewById(R.id.tvUserName)
                    val tvIntro: TextView = inflate.findViewById(R.id.tvIntro)

                    val userHeader: ImageView = inflate.findViewById(R.id.ivUserHead)
                    val ivQrcode: ImageView = inflate.findViewById(R.id.ivQrcode)
                    val ivCover: ImageView = inflate.findViewById(R.id.ivCover)

                    userHeader.setImageBitmap(asyncHead.await())
                    ivQrcode.setImageBitmap(asyncQr.await())
                    ivCover.setImageBitmap(asyncCover.await())

                    tvUserName.text = "——" + GlobalsUserManager.userInfo?.name
                    tvIntro.setText(sharedata?.invitation_words)
                    tvResourceName.setText(resourceTitle)
                    tvResourceDes.setText(resourceDes)
                    //转换完毕的bitmap
                    val createUnShowBitmapFromLayout = BitmapUtils.createUnShowBitmapFromLayout(inflate)

                    dialog.dismiss()
                    val build = IShare.Builder()
                            .setSite(shareSit)
                            .setImageBitmap(createUnShowBitmapFromLayout)
                            .build()
                    shareService.shareAddScore(ShareTypeConstants.TYPE_EBOOK, context, build)
                }

            } catch (e: Exception) {
                dialog.dismiss()
            }
        }


    }

    /**
     * 创建电子书分享邀请图片
     */
    fun createShareInviteBitmap() {

    }

    fun getDeleCourseData(
            loadUrl: String,
            resource_type: String,
            other_resource_id: String,
            context: Context
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("url", loadUrl)
        jsonObject.put("resource_type", resource_type)
        jsonObject.put("other_resource_id", other_resource_id)
        val studyRoomService: StudyRoomService? =
                ARouter.getInstance().navigation(StudyRoomService::class.java)
        studyRoomService?.showAddToStudyRoomPop(context, jsonObject) {
            if (it) {
                EbookShareDetaildata.value?.is_enroll = "1"
                EbookShareDetaildata.postValue(EbookShareDetaildata.value)
            }
        }
    }

    fun deleteResourse(resourceId: String, sourceType: String) {
        launchUI {
            val studyRoomService = ARouter.getInstance().navigation(StudyRoomService::class.java)
            val deleteResFromFolder = studyRoomService.deleteResFromFolder(resourceId, sourceType)
            if (!deleteResFromFolder.isSuccess) {
                toast(deleteResFromFolder.message)
            }
            EbookShareDetaildata.value?.is_enroll = "0"
            EbookShareDetaildata.postValue(EbookShareDetaildata.value)
        }
    }

    fun uploadZYReaderProgress(bookId: String) {
        launchUI {
            val jsonObject = JSONObject()
            jsonObject.put("resource_type", ResourceTypeConstans.TYPE_E_BOOK)
            jsonObject.put("resource_id", bookId)

            HttpService.commonApi.updateZYReaderProgress(RequestBodyUtil.fromJson(jsonObject))
                    .await().data;
        }
    }

    fun getRecommendEBooks(id: String): LiveData<MutableList<EBookBean>> {
        val liveData = MutableLiveData<MutableList<EBookBean>>()
        launchUI {
            val it = ApiService.retrofit.create(EbookApi::class.java).getRecommendEBook(5, id).await()
            liveData.postValue(it?.data)
        }
        return liveData;
    }


}