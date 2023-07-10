package com.mooc.home.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.mooc.commonbusiness.model.eventbus.ScoreChangeNeedRefreshEvent
import com.mooc.home.ui.home.HomeActivity
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

/**
 *Activity 调用栈
 */
class ActivityStackObserver : Application.ActivityLifecycleCallbacks{
    val activityStack = ArrayList<WeakReference<Activity>>()

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityDestroyed(p0: Activity) {

        val iter = activityStack.iterator()
        while (iter.hasNext()) {

            val item: WeakReference<Activity> = iter.next()
//            loge("activityStack : " + item.get()?.localClassName)

            if (item.get() == p0) {
                iter.remove()
                break
            }
        }
//        loge("activityStackSize : " + activityStack.size)
        //当页面关闭的时候，如果最后一个是home页，就刷新一下，积分详情
        //todo 后期添加，资源页面打开后再回到主页的判断，这样可以减少请求
        if(activityStack.size == 1 && activityStack.get(0).get()?.localClassName == HomeActivity::class.java.name){
            //通知积分详情刷新
            EventBus.getDefault().post(ScoreChangeNeedRefreshEvent())
        }

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        activityStack.add(WeakReference(p0))

    }

    override fun onActivityResumed(p0: Activity) {

    }

}