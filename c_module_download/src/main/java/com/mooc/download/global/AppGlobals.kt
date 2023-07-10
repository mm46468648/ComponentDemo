package com.mooc.download.global

import android.app.Application
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * 这种方式获取全局的Application 是一种拓展思路。
 *
 *
 * 对于组件化项目,不可能把项目实际的Application下沉到Base,而且各个module也不需要知道Application真实名字
 *
 *
 * 这种一次反射就能获取全局Application对象的方式相比于在Application#OnCreate保存一份的方式显示更加通用了
 */
object AppGlobals {
    private var sApplication: Application? = null

    fun getApplication(): Application? {
        if (sApplication == null) {
            try {
                var atClass = Class.forName("android.app.ActivityThread")
                var currentApplicationMethod: Method = atClass.getDeclaredMethod("currentApplication")
                currentApplicationMethod.setAccessible(true)
                sApplication = currentApplicationMethod.invoke(null) as Application


                if (sApplication != null)
                    return sApplication;

                //防止获取不到
                atClass = Class.forName("android.app.AppGlobals");
                currentApplicationMethod = atClass.getDeclaredMethod("getInitialApplication");
                currentApplicationMethod.setAccessible(true);
                sApplication = currentApplicationMethod.invoke(null) as Application
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return sApplication
    }
}