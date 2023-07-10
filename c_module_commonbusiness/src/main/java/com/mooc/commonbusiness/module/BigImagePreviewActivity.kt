package com.mooc.commonbusiness.module

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.BitmapTools
import com.mooc.common.utils.Md5Util
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.adapter.BigImagePreviewPagerAdapter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.databinding.CommonActivityBigimagePreviewBinding
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.DownloadImageUtil
import com.mooc.commonbusiness.utils.DownloadUtils
import com.mooc.commonbusiness.utils.IShare
//import kotlinx.android.synthetic.main.common_activity_bigimage_preview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException

/**
 * 大图预览
 */
@Route(path = Paths.PAGE_BIG_IMAGE_PREVIEW)
class BigImagePreviewActivity : BaseActivity() {
    var imageStrList: ArrayList<String>? = null
    private val imagePosition by extraDelegate(IntentParamsConstants.COMMON_IMAGE_PREVIEW_POSITION, 0)

    var currentPosition = 0
    val imageShare by extraDelegate(IntentParamsConstants.COMMON_IMAGE_PREVIEW_FLAG, 0)

    val imageAdapter by lazy {
        BigImagePreviewPagerAdapter(imageStrList ?: arrayListOf())
    }

    private lateinit var inflater: CommonActivityBigimagePreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = CommonActivityBigimagePreviewBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        imageStrList = intent.getStringArrayListExtra(IntentParamsConstants.COMMON_IMAGE_PREVIEW_LIST)
        currentPosition = imagePosition
        //adapter
        inflater.viewPage2.adapter = imageAdapter

        //选到最后一个,不带动画
        if (currentPosition > 0 && currentPosition < imageStrList?.size ?: 0) {
            inflater.viewPage2.postDelayed({
                inflater.viewPage2.setCurrentItem(currentPosition, false)
            }, 20)
        }

        if (imageShare == 1) {
            inflater.commonTitle.ib_right?.visibility = View.GONE
        }

        //标题
        updateMiddleStr()

        //share
        inflater.commonTitle.setOnRightIconClickListener {
            clickShare()
        }

        //download
        inflater.commonTitle.setOnSecondRightIconClickListener {
            clickDownload()
        }

        //listener
        initListener()
    }


    /**
     * 滑动监听
     */
    private fun initListener() {
//        inflater.viewPage2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                currentPosition = position
//                updateMiddleStr()
//            }
//        })
        inflater.commonTitle.setOnLeftClickListener { finish() }

        inflater.viewPage2.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
                updateMiddleStr()
            }
        })
    }

    /**
     * 更新中间显示
     */
    private fun updateMiddleStr() {
        val middleStr = "${currentPosition + 1}/${imageStrList?.size}"
        inflater.commonTitle.middle_text = middleStr
//        common_title.middle_text = middleStr
    }

    private fun clickDownload() {

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PermissionApplyActivity.REQUEST_CODE_DEFAULT
            )
            return
        }
        val currentImgUrl = imageStrList?.get(currentPosition) ?: ""

//        //base64
//        if (currentImgUrl.startsWith("data:image")) {
//            val images = currentImgUrl.split(",".toRegex()).toTypedArray()
//            if (images.size > 1) {
//                downloadBase64(images[1])
//            }
//            return
//        }
        DownloadImageUtil.download(this,currentImgUrl)
    }


    /**
     * 点击分享
     */
    private fun clickShare() {
        val currentImgUrl = imageStrList?.get(currentPosition) ?: ""


        val commonBottomSharePop = CommonBottomSharePop(this)
        commonBottomSharePop.onItemClick = {
            //将图片url，分享
            val shareService = ARouter.getInstance().navigation(ShareSrevice::class.java)

            //base64
            if (currentImgUrl.startsWith("data:image")) {
                val images = currentImgUrl.split(",".toRegex()).toTypedArray()
                if (images.size > 1) {
                    val bitmap: Bitmap = BitmapTools.base64ToBitmap(images[1])
                    val builder = IShare.Builder()
                            .setImageBitmap(bitmap)
                            .build()
                    shareService.share(this, builder)
                }
            } else {
                val builder = IShare.Builder()
                        .setSite(it)
                        .setImageUrl(currentImgUrl)
                        .build()
                shareService.share(this, builder)
            }

        }
        XPopup.Builder(this)
                .asCustom(commonBottomSharePop)
                .show()
    }

    /**
     * 下载base64
     */
    private fun downloadBase64(url: String) {
//        val bitmap: Bitmap = BitmapTools.base64ToBitmap(url)
//
//        val folder = File(DownloadConfig.imageLocation)
//        if (!folder.exists()) {
//            folder.mkdirs()
//        }
//        val file = File(DownloadConfig.imageLocation, Md5Util.getMD5Str(url).toString() + ".png")
//
//        if (file.exists()) {
//            toast("图片已保存至" + file.path)
//            return
//        }
//        val path: String = BitmapTools.saveBitmapToSDCard(this, bitmap)
//        val file1 = File(path)
//        toast("图片已保存至" + file.path)
//        sendBroadcast(file1)
//        BitmapTools.deleteDir(BitmapTools.getDirectoryPath());
    }

//    /**
//     * 保存图片后发送广播通知更新数据库
//     */
//    @Suppress("DEPRECATION")
//    private fun sendBroadcast(file: File) {
//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(this.contentResolver,
//                    file.absolutePath, file.name, null)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//        // 最后通知图库更新
//        this.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.absolutePath)))
//    }
}
