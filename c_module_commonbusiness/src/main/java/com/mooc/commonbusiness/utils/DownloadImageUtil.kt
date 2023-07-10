package com.mooc.commonbusiness.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toastMain
import com.mooc.common.utils.BitmapTools
import com.mooc.common.utils.FileUtils
import com.mooc.common.utils.Md5Util
import com.mooc.commonbusiness.config.DownloadConfig
import okhttp3.*
import java.io.File
import java.io.IOException

/**
 * 统一的下载图片工具类
 * 适配Android Q分区存储
 */
class DownloadImageUtil {

    companion object{
        /**
         * @param imageUrl
         * 有可能返回的是base64的数据
         */
        fun download(context:Context,imageUrl:String){
            //base64
            if (imageUrl.startsWith("data:image")) {
                val images = imageUrl.split(",".toRegex()).toTypedArray()
                if (images.size > 1) {
                    downloadBase64(context,images[1])
                }
                return
            }
            //封面图
            val request = Request.Builder().url(imageUrl).build()
            val okHttpClient = OkHttpClient()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    loge(e.toString())
                }

                override fun onResponse(call: Call, response: Response) {

                    val inputStream = response.body?.byteStream()

                    //使用分区存储框架
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val decodeStream = BitmapFactory.decodeStream(inputStream)
                        newMediaStore(context,decodeStream,imageUrl)
                        return
                    }

                    //Android Q以下的存储方式
                    //图片下载
                    val folder = File(DownloadConfig.imageLocation)
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }
                    val file = File(DownloadConfig.imageLocation, "${Md5Util.getMD5Str(imageUrl)}.png")
                    file.outputStream().let {
                        inputStream?.copyTo(it, 2048)
                        broadcastSaveImage(context,file)
                        toastMain("图片已保存至" + file.path)
                        it.close()
                        inputStream?.close()
                    }
                }
            })

        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun newMediaStore(context:Context, bitmap: Bitmap, imageUrl: String) {
            val uri = AndroidQStorageSaveUtils.saveBitmap2Public(
                context,
                bitmap,
                "${Environment.DIRECTORY_DCIM}/${DownloadConfig.DOWNLOAD_DIR_NAME}",
                "${Md5Util.getMD5Str(imageUrl)}.png"
            )

            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            toastMain("图片已保存至${Environment.DIRECTORY_DCIM}/${DownloadConfig.DOWNLOAD_DIR_NAME}")
        }

        /**
         * 下载base64
         */
        private fun downloadBase64(context: Context,url: String) {
            val bitmap: Bitmap = BitmapTools.base64ToBitmap(url)

            val folder = File(DownloadConfig.imageLocation)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            //使用分区存储框架
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                newMediaStore(context,bitmap,"${Md5Util.getMD5Str(url)}.png")
                return
            }

            val file = File(DownloadConfig.imageLocation, "${Md5Util.getMD5Str(url)}.png")
            file.outputStream().let {
                val compress = bitmap.compress(
                    Bitmap.CompressFormat.JPEG, 100, it
                )
                broadcastSaveImage(context,file)
                if(compress){
                    toastMain("图片已保存至" + file.path)
                }
                it.close()
            }
        }

        /**
         * 广播通知图片保存到相册
         * @param context
         * @param file
         */
        fun broadcastSaveImage(context: Context, file: File) {
            val localContentResolver = context.contentResolver
            val localContentValues =
                FileUtils.getImageContentValues(context, file, System.currentTimeMillis())
            localContentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                localContentValues
            )
            val localIntent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
            val localUri = Uri.fromFile(file)
            localIntent.data = localUri
            context.sendBroadcast(localIntent)
        }
    }

}