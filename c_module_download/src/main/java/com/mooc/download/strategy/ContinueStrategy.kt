package com.mooc.download.strategy

import com.mooc.download.DownloadManager
import com.mooc.download.DownloadModel
import com.mooc.download.util.DownloadConstants
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * 继续写入
 */
class ContinueStrategy : WriteStrategy() {

    override fun writeResponseToDisk(downloadInfo: DownloadModel, response: Response) {
        //1.创建文件夹
        val fileDir = File(downloadInfo.downloadFilePath)
        if(!fileDir.exists()){
            fileDir.mkdirs()
        }

        val inputStream = response.body?.byteStream()
        val totalLength = response.body?.contentLength()?:0
        downloadInfo.fileSize = totalLength
        //2。检测剩余可用空间
        if (!checkAvailableSize(totalLength,downloadInfo.downloadFilePath)){
            //todo 提示用户空间已满
            //todo 终止所有等待线程？
           return
        }


        //从response获取输入流以及总大小
        if (response.body != null) {

            //随机访问文件，支持断点续传
            val raf = RandomAccessFile(downloadInfo.downloadFilePath+"/"+downloadInfo.downloadFileName, "rwd")

            if(downloadInfo.progress >= totalLength){
                //下载完成，并返回保存的文件路径
                downloadInfo.status = DownloadModel.STATUS_COMPLETED
                downloadInfo.postListenerState(DownloadModel.STATUS_COMPLETED)
                return
            }
            raf.seek(downloadInfo.progress)

//            var os: OutputStream? = null
            var currentLength: Long = downloadInfo.progress
            try {
//                os = BufferedOutputStream(FileOutputStream(file))
                val data = ByteArray(DownloadManager.bufferSize)
                var len: Int

                while (true) {
                    checkPause(downloadInfo)
                    len = inputStream?.read(data)?:-1
                    if (len == -1) {
                        break
                    }
                    raf.write(data, 0, len)
                    currentLength += len

                    //同步记录下载进度
                    downloadInfo.progress = currentLength
                    //回调下载进度
                    downloadInfo.postProgress((100 * (downloadInfo.progress) / totalLength).toInt())

                }
                //下载完成，并返回保存的文件路径
                downloadInfo.status = DownloadModel.STATUS_COMPLETED
                downloadInfo.postListenerState(DownloadModel.STATUS_COMPLETED)
//

            } catch (e: IOException) {
                e.printStackTrace()

                downloadInfo.status = DownloadModel.STATUS_ERROR
                downloadInfo.postError(DownloadConstants.ERROR_UNKNOWN,e.toString())


            } finally {
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    raf?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }



}