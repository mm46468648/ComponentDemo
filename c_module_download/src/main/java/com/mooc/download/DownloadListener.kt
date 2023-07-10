package com.mooc.download

interface DownloadListener {

    /**
     * 准备，暂停，完成
     * 统一回调
     */
    fun downloadState(status:Int,statusMessage:String = "")
    fun onDownloading(progress : Int)
//    fun downloadComplete()
    fun onDownloadError(errorCode:Int,errorMessage :String = "")
//    fun onPause()


}