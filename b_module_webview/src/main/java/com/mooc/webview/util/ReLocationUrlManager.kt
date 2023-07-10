package com.mooc.webview.util

/**
 * 重定向地址管理类
 */
object ReLocationUrlManager {

    val historyUrl = ArrayList<String>();

    //需要过滤的网络列表
    val filterList = arrayListOf("gjwlaqxcz.cn","http://www.81.cn/")

    fun clearList(){
        historyUrl.clear();
    }
    fun addToList(url : String){
        historyUrl.add(url)
    }


}