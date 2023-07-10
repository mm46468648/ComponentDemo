package com.mooc.setting.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mooc.common.ktextends.toastMain
import com.mooc.setting.R
import com.mooc.setting.databinding.SetActivityBindqrcodeBinding
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.DownloadImageUtil
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException

@Route(path = Paths.PAGE_DOWNLOAD_QR)
class QrCodeDownloadActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentView = DataBindingUtil.setContentView<SetActivityBindqrcodeBinding>(this, R.layout.set_activity_bindqrcode)

        contentView.commonTitleLayout.setOnLeftClickListener {
            finish()
        }

        //下载二维码到手机相册
        contentView.btnSave.setOnClickListener {
//            lifecycleScope.launch {
//                Glide.with(this@QrCodeDownloadActivity).downloadOnly().load(getString(R.string.qr_code_download_url)).into(object : CustomTarget<File>(){
//                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
//                        val launchImageFile = File(filesDir, "qrdownload.png")
//                        resource.copyTo(launchImageFile,true)
//
//                        if(!this@QrCodeDownloadActivity.isFinishing){
//                            sendBroadcast(launchImageFile)
//                            toastMain("图片已保存至" + launchImageFile.path)
//                        }
//                    }
//                    override fun onLoadCleared(placeholder: Drawable?) {
//
//                    }
//                })
//            }

            DownloadImageUtil.download(this@QrCodeDownloadActivity,getString(R.string.qr_code_download_url))
        }
    }

//    /**
//     * 保存图片后发送广播通知更新数据库
//     */
//    private fun sendBroadcast(file: File) {
//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(this.contentResolver,
//                file.absolutePath, file.name, null)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//        // 最后通知图库更新
//        this.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.absolutePath)))
//    }
}