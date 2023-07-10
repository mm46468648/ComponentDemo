package com.mooc.commonbusiness.interfacewrapper

import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback

abstract class ARouterNavigationCallbackWrapper : NavigationCallback{
    override fun onLost(postcard: Postcard?) {

    }

    override fun onFound(postcard: Postcard?) {
    }

    override fun onInterrupt(postcard: Postcard?) {
    }

    abstract override fun onArrival(postcard: Postcard?)
}