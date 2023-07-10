package com.mooc.studyroom.ui.fragment.myhonor

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.toastMain
import com.mooc.common.utils.SDUtils
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.studyroom.HonorDataBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.utils.*
import com.mooc.download.DownloadModel
import com.mooc.studyroom.R
import com.mooc.studyroom.ui.adapter.CertificateAdapter
import com.mooc.commonbusiness.pop.studyroom.CertificationPop
import com.mooc.studyroom.viewmodel.CertificateViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException


/**
 * 证书页面
 * @author limeng
 * @date 2020/9/28
 */
class CertificateFragment : BaseListFragment2<HonorDataBean, CertificateViewModel>() {
    private var progressDialog: CustomProgressDialog? = null

    private val rxPermissions: RxPermissions by lazy {
        RxPermissions(requireActivity())
    }

    //    val DownloadConfig.certificateLoccation = "${DownloadConfig.defaultLocation}/certificate"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = CustomProgressDialog.createLoadingDialog(requireActivity())

    }

    /**
     * 保存文件到本地
     */
    private fun downLoadHonorFile(data: HonorDataBean) {
        if ("notpassing" == data.credential_status) {
            return
        }

        //使用分区存储框架
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            newMediaStore(data)
            return
        }
        // 电子证书
//        if ("true".equals(data.isElectricApplied())) {// 领过电子证书
        // 下载电子证书
        var url: String = ApiService.XT_ROOT_URL + data.download_url
        if (TextUtils.isEmpty(data.download_url)) {
            url = data.pdf_link
        }
        if (!TextUtils.isEmpty(url)) {
            val filePath: String? = getSavePath(url)
            val isExit: Boolean = SDUtils.checkFile(filePath)
            if (isExit) {
                toast("证书已下载,请去${DownloadConfig.certificateLoccation}目录下查看")
                return
            }
            //去下载图片吧
            val finalUrl = url

            progressDialog?.show()
            lifecycleScope.launch(Dispatchers.IO) {
                val b = DownloadUtils.DownloadSmallFile(finalUrl, filePath)
                withContext(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    if (b) {
                        toast("证书已下载,请去${DownloadConfig.certificateLoccation}目录下查看")
                    } else {
                        toast("证书下载失败")
                    }
                }
            }
        } else {
            toast("证书不可下载")
        }

    }

    /**
     * 文件地址
     */
    private fun getSavePath(source: String): String? {
        var fileType = ".pdf"
        val tmp = source.lastIndexOf(".")
        if (tmp != -1) {
            fileType = source.substring(tmp, source.length)
        }
//        val file: File = SDUtils.getSDFile(DownloadConfig.certificateLoccation, source.hashCode().toString() + fileType)

        val dir = File(DownloadConfig.certificateLoccation)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, "${source.hashCode()}${fileType}").absolutePath
    }

    var downloadState = DownloadModel.STATUS_NONE     //0,未开始,1,下载中,2,下载成功,3下载失败.
    @RequiresApi(Build.VERSION_CODES.Q)
    fun newMediaStore(data: HonorDataBean) {
        if(downloadState == DownloadModel.STATUS_DOWNLOADING){
            toastMain("下载中")
            return
        }
        progressDialog?.show()
        downloadState = DownloadModel.STATUS_DOWNLOADING
        //download File
        var url: String = ApiService.XT_ROOT_URL + data.download_url
        if (TextUtils.isEmpty(data.download_url)) {
            url = data.pdf_link
        }

        //文件名字
        var fileType = ".pdf"
        val tmp = url.lastIndexOf(".")
        if (tmp != -1) {
            fileType = url.substring(tmp, url.length)
        }

        val fileName = "${url.hashCode()}${fileType}";


        lifecycleScope.launch(Dispatchers.IO) {

            val client = OkHttpClient()
            val request: Request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body
                    val input = body!!.byteStream()
                    AndroidQStorageSaveUtils.saveFile2Public(requireContext(),input, Environment.DIRECTORY_DOWNLOADS,fileName)
                    downloadState = DownloadModel.STATUS_COMPLETED

                }
            } catch (e: IOException) {
                downloadState = DownloadModel.STATUS_ERROR
                e.printStackTrace()
            }finally {
                    progressDialog?.dismiss()
                    if (downloadState == DownloadModel.STATUS_COMPLETED) {
                        toastMain("证书已下载,请去${Environment.DIRECTORY_DOWNLOADS}/${DownloadConfig.DOWNLOAD_DIR_NAME}目录下查看")
                    } else {
                        toastMain("证书下载失败")
                    }
                    downloadState = DownloadModel.STATUS_NONE
            }

        }
    }
    private fun showPermissionDialog() {
        activity?.let { activity ->
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = resources.getString(R.string.permission_file_read)
            publicDialogBean.strTv = resources.getString(R.string.text_ok)
            val dialog = PublicOneDialog(activity, publicDialogBean)
            XPopup.Builder(activity)
                    .asCustom(dialog)
                    .show()

        }

    }

    override fun initAdapter(): BaseQuickAdapter<HonorDataBean, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let { it ->
            val myMedalAdapter = CertificateAdapter(it)

            myMedalAdapter.addChildClickViewIds(R.id.online_certificate)
            //下载证书
            myMedalAdapter.setOnItemChildClickListener { _, view, position ->
                if (view.id == R.id.online_certificate) {
                    val bean = myMedalAdapter.data[position]
                    // apply_status  -1没有生成表示可以下载 0正在生成不能下载 1已生成
                    //                    if (bean.apply_status == "-1") {
                    if (rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        downLoadHonorFile(bean)
                    } else {
                        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                ?.subscribe {
                                    if (it) {
                                        downLoadHonorFile(bean)
                                    } else {
                                        showPermissionDialog()
                                    }
                                }
                        //                        }

                    }
                }
            }
            //查看大图
            myMedalAdapter.setOnItemClickListener { adapter, _, position ->
                val bean = adapter.getItem(position) as HonorDataBean
                // apply_status  -1没有生成表示 可以下载 0正在生成不能下载 1已生成
                if (bean.link.isNotEmpty()) {
                    val window = activity?.let { it -> CertificationPop(it, bean.link) }
                    window?.showAtLocation(recycler_view, Gravity.CENTER_VERTICAL, 0, 0)
                    return@setOnItemClickListener
                }
                if (bean.download_url.isEmpty()) {
                    return@setOnItemClickListener
                }
                if (bean.credential_status == "notpassing") {
                    return@setOnItemClickListener
                }
                val window = activity?.let { CertificationPop(it, bean.src_url) }
                window?.showAtLocation(recycler_view, Gravity.CENTER_VERTICAL, 0, 0)

            }
            myMedalAdapter
        }
    }



}