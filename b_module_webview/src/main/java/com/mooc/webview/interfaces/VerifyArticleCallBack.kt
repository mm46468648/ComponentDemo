package com.mooc.webview.interfaces

import com.mooc.webview.model.Point

interface VerifyArticleCallBack {

//    fun getVerifyCodeInfo(id:String);

//    fun postVerifyCode(list: MutableList<Point>);

    fun postVerifyCode(id : Int ,list: MutableList<Point>);
//    fun refreshVerifyImg();

//    fun closeActivity();
}