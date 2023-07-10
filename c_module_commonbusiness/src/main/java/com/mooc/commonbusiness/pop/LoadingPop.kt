package com.mooc.commonbusiness.pop

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.commonbusiness.R
import pl.droidsonroids.gif.GifDrawable

class LoadingPop(var mContext: Context) : CenterPopupView(mContext) {

    override fun getImplLayoutId(): Int {
        return R.layout.common_dialog_loading
    }

    var backPress : (()->Unit)? = null
    override fun onBackPressed(): Boolean {
        //通知外面的Activity关闭
        backPress?.invoke()
        return true
    }
}