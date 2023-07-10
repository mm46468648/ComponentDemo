package com.mooc.commonbusiness.manager

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * activity基类观察者
 */
abstract class BaseActivityLifeObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onActivityResumse(){
        onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onActivityDestory(){
        onDestory()
    }

    abstract fun onDestory()

    abstract fun onResume()
}