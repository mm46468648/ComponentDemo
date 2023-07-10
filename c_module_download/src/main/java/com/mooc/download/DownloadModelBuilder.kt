package com.mooc.download

class DownloadModelBuilder {

    var downloadUrl = ""
    var filePath = ""
    var fileName = ""
    var downloadListener: DownloadListener? = null

    fun setDownloadUrl(url: String): DownloadModelBuilder {
        downloadUrl = url
        return this
    }

    fun setFilePath(path: String): DownloadModelBuilder {
        filePath = path
        return this
    }

    fun setFileName(name: String): DownloadModelBuilder {
        fileName = name
        return this
    }

    fun setDownloadListener(listener: DownloadListener): DownloadModelBuilder {
        downloadListener = listener
        return this
    }

    fun build(): DownloadModel {
        return this.let {
            val downloadModel = DownloadModel()
            downloadModel.downloadUrl = it.downloadUrl
            downloadModel.downloadFilePath = it.filePath
            downloadModel.downloadFileName = it.fileName
            downloadModel.downloadListener = it.downloadListener
            downloadModel
        }
    }


}