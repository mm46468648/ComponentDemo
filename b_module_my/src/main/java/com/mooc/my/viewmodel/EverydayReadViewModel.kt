package com.mooc.my.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.FileUtils
import com.mooc.common.utils.Md5Util
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.utils.AndroidQStorageSaveUtils
import com.mooc.commonbusiness.utils.DownloadImageUtil
import com.mooc.commonbusiness.utils.ServerTimeManager
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.my.R
import com.mooc.my.model.ReadBean
import com.mooc.my.repository.MyModelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File


/**
搜索数据获取
 * @Author limeng
 * @Date 2020/8/21-4:12 PM
 */
class EverydayReadViewModel : BaseViewModel() {
    private val repository = MyModelRepository()

//    private val EverydayReadBean = MutableLiveData<EverydayReadBean>()

    //单日的数据
    private val singleReadBean = MutableLiveData<ReadBean>().also {
        //默认请求当天数据
        val formatDate = TimeFormatUtil.formatDate(
                ServerTimeManager.getInstance().serviceTime,
                TimeFormatUtil.yyyy_MM_dd
        )
        getSingleReadData(formatDate)
    }


    fun getSingleReadBeanData(): LiveData<ReadBean?> {
        return singleReadBean
    }


    fun getSingleReadData(date: String) {
        launchUI {
            repository.getSingleRead(date)
                    .catch {
                        singleReadBean.postValue(null)
                    }
                    .collect {
                        singleReadBean.postValue(it)
                    }
        }
    }

    fun createShareBitmap(context: Context, imageUrl: String): Flow<View> {
        return flow {
            val shareImageBitmap = viewModelScope.async(Dispatchers.IO) {
                getShareImageBitmap(imageUrl) ?: BitmapFactory.decodeResource(
                    context.getResources(),
                    R.mipmap.my_bg_dailyread_load,
                    null
                )
            }
            val shareHeadImageBitmap = viewModelScope.async(Dispatchers.IO) {
                getShareHeadImageBitmap(context, GlobalsUserManager.userInfo?.avatar ?: "")
                    ?: BitmapFactory.decodeResource(
                        context.getResources(),
                        R.mipmap.common_ic_user_head_default,
                        null
                    )
            }

            val inflate = View.inflate(context, R.layout.my_layout_dailyread_share, null)
            val ivImage = inflate.findViewById<ImageView>(R.id.imageView)
            val ivHead = inflate.findViewById<ImageView>(R.id.ivHead)
            val tvTime = inflate.findViewById<TextView>(R.id.tvTime)
            val tvName = inflate.findViewById<TextView>(R.id.tvName)

            ivImage.setImageBitmap(shareImageBitmap.await())
            ivHead.setImageBitmap(shareHeadImageBitmap.await())
            tvName.setText(GlobalsUserManager.userInfo?.name)

//            val formatDateFromString = TimeFormatUtil.formatDateFromString(
//                    date,
//                    TimeFormatUtil.yyyy_MM_dd,
//                    TimeFormatUtil.yyyyNMMYddR
//            )
            //data使用当天的日期
            val formatDateFromString = TimeFormatUtil.formatDate(
                ServerTimeManager.getInstance().serviceTime,
                TimeFormatUtil.yyyyNMMYddR
            )
            tvTime.setText(formatDateFromString)

            loge(Thread.currentThread())
            emit(inflate)
        }
    }

    fun getShareImageBitmap(imageUrl: String): Bitmap? {
        //封面图
        val request = Request.Builder().url(imageUrl).build()
        val okHttpClient = OkHttpClient()
        val response = okHttpClient.newCall(request).execute()
        val inputStream = response.body?.byteStream()
        val decodeStream = BitmapFactory.decodeStream(inputStream)


        val width = decodeStream.width
        val radio: Float = 360.dp2px() * 1.0f / width
//        loge("getScreenHeight: ${ScreenUtil.getScreenHeight(this@DailyReadingNewActivity)} currentY: ${imageView.top} resourceHeight: ${resource.height} resourceWidth: ${resource.width} radio: ${radio}")

        val bitmap: Bitmap? = BitmapUtils.scaleBitmap(decodeStream, radio)

        return bitmap
    }

    fun getShareHeadImageBitmap(context: Context, headUrl: String): Bitmap? {
        return try {
            val request = Request.Builder().url(
                    GlobalsUserManager.userInfo?.avatar
                            ?: "").build()
            val okHttpClient = OkHttpClient()
            val response = okHttpClient.newCall(request).execute()
            val inputStream = response.body?.byteStream()
            BitmapUtils.makeRoundCorner(BitmapFactory.decodeStream(inputStream))
        } catch (e: Exception) {
            null
        }
    }


    fun downloadImage(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PermissionApplyActivity.REQUEST_CODE_DEFAULT
            )
            return
        }
        getSingleReadBeanData().value.let {
            val imageUrl = it?.image_url ?: ""
            if (!checkUrl(imageUrl)) {
                toast("图片地址不正确")
                return
            }

            DownloadImageUtil.download(activity,imageUrl)
        }
    }


    /**
     * 检测图片地址是否正确
     */
    fun checkUrl(imageUrl: String): Boolean {
        return imageUrl.contains("moocnd.ykt.io")
                || imageUrl.contains("learning.mil.cn")
    }

}