package com.mooc.download

import java.util.concurrent.BlockingQueue

class DownloadProduct(var queue: BlockingQueue<DownloadModel>) {


    fun addProduct(downloadModel : DownloadModel){
        if(queue.offer(downloadModel)){

        }
    }

}