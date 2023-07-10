package com.mooc.video.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.net.Uri
import android.view.ContextThemeWrapper
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Util

class VideoPlayUtils {

    companion object{
        /***
         * 获取当前手机横屏状态
         *
         * @param context
         * @return int boolean
         */
        @JvmOverloads
        fun isLand(@NonNull context: Context): Boolean {
            val resources = context.resources
            val configuration = resources.configuration
            Assertions.checkState(configuration != null)
            return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }

        /***
         * 得到活动对象
         *
         * @param context 上下文
         * @return Activity activity
         */
        @JvmOverloads
        fun scanForActivity(context: Context): Activity? {
            if (context is Activity) {
                return context
            } else if (context is ContextWrapper) {
                return scanForActivity(context.baseContext)
            }
            return null
        }

        /***
         * 得到活动对象
         *
         * @param context 上下文
         * @return AppCompatActivity app comp activity
         */
        private fun getAppCompActivity(context: Context): AppCompatActivity? {
            if (context is AppCompatActivity) {
                return context
            } else if (context is ContextThemeWrapper) {
                return getAppCompActivity(context.baseContext)
            }
            return null
        }

        /***
         * 显示ActionBar
         * @param context 上下文
         */
        fun showActionBar(context: Context) {
            val appCompActivity: AppCompatActivity? = getAppCompActivity(context)
            if (appCompActivity != null) {
                val ab = appCompActivity.supportActionBar
                ab?.show()
            }
            scanForActivity(context)!!.window
                    .clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        /***
         * 隐藏标题栏
         *
         * @param context 上下文
         */
        fun hideActionBar(context: Context) {
            val appCompActivity = getAppCompActivity(context)
            if (appCompActivity != null) {
                val ab = appCompActivity.supportActionBar
                ab?.hide()
            }
            scanForActivity(context)
                    ?.getWindow()
                    ?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }


        /**
         * Makes a best guess to infer the type from a file name.
         *
         * @param fileName Name of the file. It can include the path of the file.
         * @return The content type.
         */
        /**
         * Makes a best guess to infer the type from a file name.
         *
         * @param fileName Name of the file. It can include the path of the file.
         * @return The content type.
         */
        /**
         * Makes a best guess to infer the type from a [Uri].
         *
         * @param uri The [Uri].
         * @return The content type.
         */
        @C.ContentType
        fun inferContentType(uri: Uri): Int {
            val path = uri.path
            return path?.let { inferContentType(it) } ?: C.TYPE_OTHER
        }

        /**
         * Infer content type int.
         *
         * @param fileName the file name
         * @return the int
         */
        @C.ContentType
        private fun inferContentType(fileName: String): Int {
            val fileName = Util.toLowerInvariant(fileName)
            return if (fileName.matches(Regex(".*m3u8.*"))) {
                C.TYPE_HLS
            } else if (fileName.matches(Regex(".*mpd.*"))) {
                C.TYPE_DASH
            } else if (fileName.matches(Regex(".*\\.ism(l)?(/manifest(\\(.+\\))?)?"))) {
                C.TYPE_SS
            } else {
                C.TYPE_OTHER
            }
        }
    }
}