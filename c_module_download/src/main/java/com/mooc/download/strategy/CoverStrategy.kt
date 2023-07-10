package com.mooc.download.strategy

import com.mooc.download.DownloadManager
import com.mooc.download.DownloadModel
import com.mooc.download.util.DownloadConstants
import okhttp3.Response
import java.io.*

/**
 * 覆盖策略
 */
class CoverStrategy : WriteStrategy(){
    override fun writeResponseToDisk(downloadInfo: DownloadModel, response: Response) {

        val fileDir = File(downloadInfo.downloadFilePath)
        if(!fileDir.exists()){
            fileDir.mkdirs()
        }
        val file = File(downloadInfo.downloadFilePath,downloadInfo.downloadFileName)
        val inputStream = response.body?.byteStream()
        val totalLength = response.body?.contentLength()?:0
        val downloadListener = downloadInfo.downloadListener

        //2。检测剩余可用空间
        if (!checkAvailableSize(totalLength,downloadInfo.downloadFilePath)){
            //todo 提示用户空间已满

            //todo 终止所有等待线程？
            return
        }

        //从response获取输入流以及总大小
        if (response.body != null) {
            var os: OutputStream? = null
            var currentLength: Long = 0
            try {
                os = BufferedOutputStream(FileOutputStream(file))
                val data = ByteArray(DownloadManager.bufferSize)
                var len: Int

                while (true) {
                    checkPause(downloadInfo)
                    len = inputStream?.read(data)?:-1
                    if (len == -1) {
                        break
                    }
                    os.write(data, 0, len)
                    currentLength += len

                    //同步记录下载进度
                    downloadInfo.progress = currentLength
                    downloadInfo.postProgress((100 * (currentLength) / totalLength).toInt())
//                    runOnMain{
//                        //回调下载进度
//                        downloadListener?.onDownloading((100 * (currentLength) / totalLength).toInt())
//                    }

                }
                //下载完成，并返回保存的文件路径
                downloadInfo.status = DownloadModel.STATUS_COMPLETED
                downloadInfo.postListenerState(DownloadModel.STATUS_COMPLETED)
//                runOnMain {
//                    downloadListener?.downloadComplete()
//                }
            } catch (e: IOException) {
                e.printStackTrace()
                //如果下载错误传-1进度
                downloadInfo.status = DownloadModel.STATUS_ERROR
                downloadInfo.postError(DownloadConstants.ERROR_UNKNOWN,e.toString())
//                runOnMain {
//                    downloadListener?.onDownloadError()
//                }
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
}