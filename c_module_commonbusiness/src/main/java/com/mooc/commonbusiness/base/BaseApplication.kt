package com.mooc.commonbusiness.base

//import com.github.moduth.blockcanary.BlockCanary
//import com.github.moduth.blockcanary.BlockCanaryContext
//import com.mooc.commonbusiness.manager.AppBlockContext
import android.app.Application
import android.content.Context
import android.content.MutableContextWrapper
import android.os.SystemClock
import android.util.Log
import android.webkit.WebView
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.DebugUtil
import com.mooc.common.webview.WebPools
import com.mooc.commonbusiness.config.ModuleConfig
import com.mooc.commonbusiness.manager.CrashManager
import com.mooc.commonbusiness.utils.WebViewMutiProcessUtil
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins


abstract class BaseApplication : MultiDexApplication(), IBaseApplication {


    companion object {
        var instance: Application? = null

    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        //为了测试是不是这个引起的电子书无法打开的问题，先注释
        WebViewMutiProcessUtil.handleWebviewDir(base)
        instance = this
        attachComponent(base)


    }

    override fun onCreate() {
        super.onCreate()
        val start = SystemClock.elapsedRealtime()

        initComponent(this)
        initARouter()
        initFloat()

        Log.d("APPLICATION", "onCreate()[" + (SystemClock.elapsedRealtime() - start) + "ms]")
        initRx()
        initKoom()


    }

    private fun initKoom() {
//        val config = CommonConfig.Builder()
//            .setApplication(this) // Set application
//            .setVersionNameInvoker { "1.0.0" } // Set version name, java leak feature use it
//            .build()
//        MonitorManager.initCommonConfig(config)
//            .apply { onApplicationCreate() }
    }

    private fun initRx() {
        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
                // Merely log undeliverable exceptions
                loge(e.message ?: "")
            } else {
                // Forward all others to current thread's uncaught exception handler
                if (DebugUtil.debugMode) {
                    Thread.currentThread().also { thread ->
                        thread.uncaughtExceptionHandler.uncaughtException(thread, e)
                    }
                } else {
                    CrashReport.postCatchedException(e)
                }
            }
        }
    }


    private fun initBlockNary() {
//        BlockCanary.install(this, BlockCanaryContext()).start()
    }

    fun initCrashHandler() {
        //初始化 错误日子系统
        CrashManager.getInstance(this);
    }


    /**
     * 初始化Webview
     * 并设置到缓存池
     * 可利用Handler空闲的时候执行
     */
    private fun initWebview() {
        WebPools.getInstance().recycle(WebView(MutableContextWrapper(this)))
    }

    /**
     * 初始化全局浮窗
     * 目的是为了检测生命周期
     */
    private fun initFloat() {
//        EasyFloat.init(this, DebugUtil.debugMode)
    }

    /**
     * 初始化ARuter
     */
    private fun initARouter() {
        if (DebugUtil.debugMode) {
            //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(this)
    }

    /**
     * 初始化各组件
     */
    private fun attachComponent(base: Context) {
        for (module in ModuleConfig.attachModules) {
            try {
                val clazz = Class.forName(module)
                val baseApplication = clazz.newInstance()
                if (baseApplication is IBaseApplication) {
//                    val method = clazz.getMethod("onAttach", Application::class.java,Context::class.java)
//                    method.invoke(baseApplication,this,base)
                    baseApplication.onAttach(this, base)
                }
            } catch (e: Exception) {
                loge(e.toString())
            }
        }
    }

    /**
     * 初始化各组件
     */
    private fun initComponent(app: Application) {
        for (module in ModuleConfig.modules) {
            try {
                val clazz = Class.forName(module)
                val baseApplication = clazz.newInstance() as IBaseApplication
                baseApplication.init()
                baseApplication.initWithContext(app)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
        }
    }
}