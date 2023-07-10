package com.mooc.commonbusiness.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference


/**
 * 摇一摇反馈生命周期观察者
 */
class ShakeFeedbackObserver : Application.ActivityLifecycleCallbacks{
    val activityStack = ArrayList<WeakReference<Activity>>()

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStarted(p0: Activity) {
        activityStack.add(WeakReference(p0))
        ShakeManager.getInstance().registerShakeDetector(p0)
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(p0: Activity) {
        val iter = activityStack.iterator()
        while (iter.hasNext()) {

            val item: WeakReference<Activity> = iter.next()
//            this.loge("activityStack : " + item.get()?.localClassName)

            if (item.get() == p0) {
                iter.remove()
            }
        }

        if(activityStack.isEmpty()){
            ShakeManager.getInstance().unregisterShakeDetector()
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityResumed(p0: Activity) {

    }

}