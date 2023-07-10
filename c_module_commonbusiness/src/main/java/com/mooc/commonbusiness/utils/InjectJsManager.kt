package com.mooc.commonbusiness.utils

import android.webkit.WebView
import com.mooc.common.ktextends.runOnMain
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.commonbusiness.constants.NormalConstants

object InjectJsManager {

    var lastTime = 0L
    var closeInject = false      //h5决定是否需要打开大图
    fun injectJs(view: WebView?) {
        if(closeInject) return
        val temp = System.currentTimeMillis();
        if(temp - lastTime < 500){
            return
        }
        runOnMainDelayed(500) {
            view?.loadUrl(NormalConstants.JS_FUNCTION)
        }
        lastTime = temp;
    }
}