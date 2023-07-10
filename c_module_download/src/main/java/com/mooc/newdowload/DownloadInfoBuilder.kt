package com.mooc.newdowload

import org.jetbrains.annotations.NotNull
import java.lang.IllegalArgumentException


class DownloadInfoBuilder {

    var id = 0L
    var downloadUrl = ""
    var filePath = ""
    var fileName = ""



    fun setDownloadID(@NotNull id: Long): DownloadInfoBuilder {
        this.id = id
        return this
    }

    fun setDownloadUrl(@NotNull url: String): DownloadInfoBuilder {
        this.downloadUrl = url
        return this
    }

    fun setFilePath(@NotNull path: String): DownloadInfoBuilder {
        this.filePath = path
        return this
    }

    fun setFileName(@NotNull name: String): DownloadInfoBuilder {
        this.fileName = name
        return this
    }

    fun build(): DownloadInfo {
        return this.let {
            val downloadModel = DownloadInfo()
            downloadModel.id = it.id
            downloadModel.downloadUrl = it.downloadUrl
            downloadModel.downloadPath = it.filePath
            downloadModel.fileName = it.fileName

            checkParams(it.id,it.downloadUrl,it.filePath,it.fileName)
            downloadModel
        }
    }

    private fun checkParams(id: Long, downloadUrl: String, filePath: String, fileName: String) {
        if(id == 0L){
            throw IllegalArgumentException("download id should be init")
        }

        if(downloadUrl.isEmpty()){
            throw IllegalArgumentException("download Url can not be null")
        }

        if(filePath.isEmpty()){
            throw IllegalArgumentException("download filePath can not be null")
        }

        if(fileName.isEmpty()){
            throw IllegalArgumentException("download fileName can not be null")
        }
    }
}