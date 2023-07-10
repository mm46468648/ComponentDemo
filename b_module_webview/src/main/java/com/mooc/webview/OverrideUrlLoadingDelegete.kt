package com.mooc.webview

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.webkit.WebView
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.toastMain
import com.mooc.common.utils.permission.PermissionRequestCallback
import com.mooc.common.utils.permission.PermissionUtil
import com.mooc.commonbusiness.base.PermissionApplyActivity
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.webview.util.ReLocationUrlManager
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*

/**
 * shouldOverrideUrlLoading
 * 代理类
 */
class OverrideUrlLoadingDelegete {


    fun shouldOverrideUrlLoading(webView: WebView, url: String) {

        //微信支付
        if (url.startsWith("weixin://wap/pay?")) {
            val uri = Uri.parse(url) //url为你要链接的地址
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            AppGlobals.getApplication()?.startActivity(intent)
            return
        }
        //网页上点击了"继续支付"
        if (url.startsWith("https://mclient.alipay.com/h5Continue.htm?")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            AppGlobals.getApplication()?.startActivity(intent)
            return
        }

        //新学堂课程点击下载文件url
//        if (url.contains("api/v1/lms/service/download/")) {
//            val stringStringMap: Map<String, String>? = UrlUtils.parseParams(url)
//            val h5path = stringStringMap?.get("file_url")
//            val name = stringStringMap?.get("file_name")
//            downLoadFile(webView.context, h5path, name)
//            return
//        }

        //.pdf结尾的文件,跳转到外部浏览器去下载
        if (url.endsWith(".pdf") || url.endsWith(".ppt") || url.endsWith(".pptx") || url.endsWith(".docx")) {
            val uri = Uri.parse(url) //url为你要链接的地址
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            AppGlobals.getApplication()?.startActivity(intent)
            return
        }

        //工信部调用微信支付
        if (url.startsWith("https://wx.tenpay.com/")) {
            val webviewHead: MutableMap<String, String> = HashMap()
            webviewHead["Referer"] = "https://examtesth5.junqiangzhisheng.com/"
            webView.loadUrl(url, webviewHead)
            return
        }
        //唤起中国大学mooc App
        if (url.startsWith("emaphone://")) {
            try {
                // 以下固定写法
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                AppGlobals.getApplication()?.startActivity(intent)
            } catch (e: java.lang.Exception) {
                // 防止没有安装的情况
                e.printStackTrace()
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val content_url = Uri.parse(UrlConstants.CHINA_MOOC_APK_DOWNLOAD_PAGE)
                intent.data = content_url
                AppGlobals.getApplication()?.startActivity(intent)
            }
            return
        }
        //跳转外部浏览器
        if (url.contains("http://static.learning.mil.cn/ucloud/moocnd/app/")
            || url.contains("https://static.learning.mil.cn/ucloud/moocnd/app/")
            || url.contains(UrlConstants.APK_DOWNLOAD)
        ) {
            val uri = Uri.parse(url) //url为你要链接的地址
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            AppGlobals.getApplication()?.startActivity(intent)
            return
        }
        //通过连接跳转对应的app内资源
        if (url.startsWith("customize://")
            || url.startsWith("moocnd://")
            || url.startsWith("moocndrly://")
        ) {
            loadStudyResource(url)
            return
        }
        //webview处理loadurl
        if (url.startsWith("http")) {
            //记录url调用栈
            ReLocationUrlManager.addToList(url)
            webView.loadUrl(url)
            return
        }
    }


    fun shouldOverrideUrlLoadingX5(webView: com.tencent.smtt.sdk.WebView, url: String) {
        //新学堂课程点击下载文件url
//        if (url.contains("api/v1/lms/service/download/")) {
//            val stringStringMap: Map<String, String>? = UrlUtils.parseParams(url)
//            val h5path = stringStringMap?.get("file_url")
//            val name = stringStringMap?.get("file_name")
//            downLoadFile(webView.context, h5path, name)
//            return
//        }

        //.pdf结尾的文件,跳转到外部浏览器去下载
        if (url.endsWith(".pdf") || url.endsWith(".ppt") || url.endsWith(".pptx") || url.endsWith(".docx")) {
            val uri = Uri.parse(url) //url为你要链接的地址
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            AppGlobals.getApplication()?.startActivity(intent)
            return
        }

        //微信支付
        if (url.startsWith("weixin://wap/pay?")) {
            val uri = Uri.parse(url) //url为你要链接的地址
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            AppGlobals.getApplication()?.startActivity(intent)
            return
        }

        //网页上点击了"继续支付"
        if (url.startsWith("https://mclient.alipay.com/h5Continue.htm?")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            AppGlobals.getApplication()?.startActivity(intent)
            return
        }


        //工信部调用微信支付
        if (url.startsWith("https://wx.tenpay.com/")) {
            val webviewHead: MutableMap<String, String> = HashMap()
            webviewHead["Referer"] = "https://examtesth5.junqiangzhisheng.com/"
            webView.loadUrl(url, webviewHead)
            return
        }

        //        if (url.contains("moocnd://resource_type")) {
//            return Utils.toCourseDetailOrLearnPlanUi(WebViewActivity.this, url);
//        }
        //唤起中国大学mooc App
        if (url.startsWith("emaphone://")) {
            try {
                // 以下固定写法
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                AppGlobals.getApplication()?.startActivity(intent)
            } catch (e: java.lang.Exception) {
                // 防止没有安装的情况
                e.printStackTrace()
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val content_url = Uri.parse(UrlConstants.CHINA_MOOC_APK_DOWNLOAD_PAGE)
                intent.data = content_url
                AppGlobals.getApplication()?.startActivity(intent)
            }
            return
        }
        //跳转外部浏览器
        if (url.contains("http://static.learning.mil.cn/ucloud/moocnd/app/")
            || url.contains("https://static.learning.mil.cn/ucloud/moocnd/app/")
            || url.contains(UrlConstants.APK_DOWNLOAD)
        ) {
            val uri = Uri.parse(url) //url为你要链接的地址
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            AppGlobals.getApplication()?.startActivity(intent)
            return
        }
        //通过连接跳转对应的app内资源
        if (url.startsWith("customize://")
            || url.startsWith("moocnd://")
            || url.startsWith("moocndrly://")
        ) {
            loadStudyResource(url)
            return
        }
        //webview处理loadurl
        if (url.startsWith("http")) {
            webView.loadUrl(url)
            return
        }
    }

    private fun downLoadFile(context: Context, downloadPath: String?, name: String?) {
        //检测权限
        if (!PermissionUtil.hasPermissionRequest(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            PermissionApplyActivity.launchActivity(
                context,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                0,
                object : PermissionRequestCallback {
                    override fun permissionSuccess() {

                    }

                    override fun permissionCanceled() {
                    }

                    override fun permissionDenied() {

                    }
                })
            return
        }
        if (downloadPath?.isNotEmpty() == true && name?.isNotEmpty() == true) {
            val fileDir = File(DownloadConfig.h5DownloadLocation)
            if (!fileDir.exists()) { //文件夹不存在先创建
                fileDir.mkdirs()
            }
            //创建文件
            val downloadFileName = name
            val downloadFile = File(fileDir, downloadFileName)

            if (downloadFile.exists()) {
                toast("文件已保存到${downloadFile.path}")
                return
            }

            //真正开始在子线中中下载apk
            val okHttpClient = OkHttpClient()
            val url: String = downloadPath
            val request: Request = Request.Builder().get().url(url).build()

            val response = okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    toastMain("文件下载失败")
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    var start = false
                    writeResponseToDisk(downloadFile, response) {
                        if (it > 0 && !start) {
                            start = true
                            toastMain("开始下载...")
                        } else if (it == 100) {
                            toastMain("文件已保存到${downloadFile.path}")
                        } else if (it == -1) {
                            toastMain("文件下载失败")
                        }
                    }
                }
            })
        }
    }

    /**
     * 将流写入文体
     */
    private fun writeResponseToDisk(
        file: File,
        response: okhttp3.Response,
        downloadListener: ((progress: Int) -> Unit)? = null
    ) {
        val inputStream = response.body?.byteStream()
        val totalLength = response.body?.contentLength() ?: 0
        //从response获取输入流以及总大小
        if (response.body != null) {
            var os: OutputStream? = null
            var currentLength: Long = 0
            try {
                os = BufferedOutputStream(FileOutputStream(file))
                val data = ByteArray(8192)
                var len: Int
                while (inputStream?.read(data, 0, 8192).also { len = it!! } != -1) {
                    os.write(data, 0, len)
                    currentLength += len.toLong()
                    //计算当前下载进度
                    downloadListener?.invoke((100 * currentLength / totalLength).toInt())
                }
                //下载完成，并返回保存的文件路径
            } catch (e: IOException) {
                e.printStackTrace()
                //如果下载错误传-1进度
                downloadListener?.invoke(-1)
            } finally {
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     *  跟前端约定好的格式
     *
     *  customize:// 协议由三部分构成
     *  app端的地址为app@moocnd://resource_type=1&resource_id=1&resource_link=http://moocnd.com/
     *  小程序为app@moocnd://resource_type=100&resource_id=&resource_link=pages/index
     *  wechat端的服务号地址为wechat@http://www.arcut.com，
     *  小程序为xcx://pages/index
     *  web端的地址为web@http://www.baidu.com
     *  三者之间用管道符“|”线连接起来
     *  每一个协议分俩部分，格式如：“客户端@真正的地址”
     *  在【app】端，resource_type代表唤醒的资源类型，resource_id代表唤醒的资源ID，resource_link代表如果是h5的话，h5的链接
     *  resource_id需要自己去资源列表中查看
     *  resource_type 类型如下：
     *  resource_type: 2 => '课程'
     *  resource_type: 5 => '电子书'
     *  resource_type: 15 => '专题'
     *  resource_type: 100 => '小程序'
     *  resource_type: 20 => '学习项目'
     *  这是一个错误的示例（由于一些原因不应该出现customize://app@）：customize://app@moocnd://resource_type=14&resource_id=1120&resource_link=http://moocnd.com/|wechat@http://www.arcut.com|web@http://www.baidu.com
     *  正常的示例是：moocnd://resource_type=14&resource_id=1120&resource_link=http://moocnd.com/
     */
    private fun loadStudyResource(url: String) {
        var androidUse = url
        //先通过管道分割符号 " ｜ " 截取安卓客户端的内容
        if (!TextUtils.isEmpty(url) && url.contains("|")) {
            val split = url.split("|")
            androidUse = split.find {
                it.contains("moocnd://")
            }.toString()
        }
        //去掉customize://
        var replace = androidUse.replace("customize://", "")
        //去掉app@moocnd://
        replace = replace.replace("app@moocnd://", "")
        //去掉moocnd://
        replace = replace.replace("moocnd://", "")
        //去掉moocndrly://
        replace = replace.replace("moocndrly://", "")
        //获取键值对
        val split = replace.split("&")
        if (split.isNotEmpty()) {
            val hashMap = HashMap<String, String>()
            split.forEach {
                if (it.contains("=")) {
                    val kv = it.split("=")
                    hashMap.put(kv.get(0), kv.get(1))
                }
            }

            //获取resourId,resourceType
            val resourceType = hashMap.get("resource_type") ?: ""
            val resourceId = hashMap.get("resource_id") ?: ""
            val resourceLink = hashMap.get("resource_link") ?: ""

            //微信小程序,直接跳转不用判断id
            if (ResourceTypeConstans.TYPE_WX_PROGRAM.toString().equals(resourceType)) {
                ARouter.getInstance().build(Paths.PAGE_TO_WX_PROGRAM_DIALOG).navigation()
                return
            }
            //对战,直接跳转不用判断id
            if (ResourceTypeConstans.TYPE_BATTLE.toString().equals(resourceType)) {
                ARouter.getInstance().build(Paths.PAGE_BATTLE_MAIN).navigation()
                return
            }
            //其他资源
            if (resourceType.isNotEmpty() && resourceId.isNotEmpty()) {
                try {
                    ResourceTurnManager.turnToResourcePage(object : BaseResourceInterface {
                        override val _resourceId: String
                            get() = resourceId
                        override val _resourceType: Int
                            get() = resourceType.toInt()
                        override val _other: Map<String, String>?
                            get() {
                                val hashMapOf =
                                    hashMapOf(IntentParamsConstants.WEB_PARAMS_URL to resourceLink)
                                if (_resourceType == ResourceTypeConstans.TYPE_PERIODICAL) {
                                    //如果是期刊资源，需要传递baseurl
                                    hashMapOf.put(
                                        IntentParamsConstants.PERIODICAL_PARAMS_BASICURL,
                                        resourceLink
                                    )
                                }

                                if (_resourceType == ResourceTypeConstans.TYPE_SOURCE_FOLDER) {
                                    hashMapOf.put(
                                        IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND,
                                        "true"
                                    )
                                }
                                return hashMapOf
                            }
                    })
                } catch (e: Exception) {

                }

            }
        }


    }


}