package com.mooc.common.ktextends

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import com.mooc.common.utils.GsonManager
import com.mooc.common.global.AppGlobals
import com.mooc.common.utils.DebugUtil
import java.io.Serializable

/***runonMainDelayer About***/
@JvmField
val msMainLooperHandler = Handler(Looper.getMainLooper())

@JvmField
val gson = GsonManager.getInstance()

fun Any.runOnMainDelayed(delay: Long, run: () -> Unit) {
    msMainLooperHandler.postDelayed(run, delay)
}


fun Any.runOnMain(run: () -> Unit) {
    msMainLooperHandler.post(run)
}

/***runonMainDelayer About END***/


/***LOG About START***/
fun logWithLevel(obj: Any, level: Int, param: List<Any?>) {
    if (DebugUtil.debugMode) {
        val builder = StringBuilder()
        param.forEach {
            builder.append(it.toString() + " | ")
        }

        when (level) {
            LOG_LEVEL.DEBUG -> {
                Log.d(obj.javaClass.name, builder.toString())
            }
            LOG_LEVEL.INFO -> {
                Log.i(obj.javaClass.name, builder.toString())
            }

            LOG_LEVEL.ERR -> {
                if (obj is String) {
                    Log.e(obj, builder.toString())
                } else {
                    Log.e(obj.javaClass.name, builder.toString())
                }
            }
        }
    }
}

interface LOG_LEVEL {
    companion object {
        const val DEBUG = 0
        const val INFO = 1
        const val ERR = 2
    }
}

fun Any.logd(vararg param: Any) {
    logWithLevel(this, LOG_LEVEL.DEBUG, param.asList())
}

fun Any.logi(vararg param: Any) {
    logWithLevel(this, LOG_LEVEL.INFO, param.asList())
}

@JvmOverloads
fun Any.loge(vararg param: Any) {
    logWithLevel(this, LOG_LEVEL.ERR, param.asList())
}

/***LOG About END***/

/***Toast START***/

fun Any.toast(str: String?) {
    Toast.makeText(AppGlobals.getApplication(), str, Toast.LENGTH_SHORT).show()
}

var lastTime = 0L
fun Any.toastOnce(str: String) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastTime > 1000) {
        lastTime = currentTime
        toast(str)
    }
}

fun Any.toastMain(str: String?) {
    runOnMain {
        Toast.makeText(AppGlobals.getApplication(), str, Toast.LENGTH_SHORT).show()
    }
}

fun Any.toastLong(str: String?) {
    Toast.makeText(AppGlobals.getApplication(), str, Toast.LENGTH_LONG).show()
}

/***Toast END ***/

/***Bundle START***/
fun Bundle.put(key: String, value: Any?): Bundle {
    if (TextUtils.isEmpty(key) || value == null) {
        return this
    }
    var store = true
    // 根据value的类型，选择合适的api进行存储
    @Suppress("UNCHECKED_CAST")
    when (value) {
        is Int -> this.putInt(key, value)
        is Long -> this.putLong(key, value)
        is CharSequence -> this.putCharSequence(key, value)
        is String -> this.putString(key, value)
        is Float -> this.putFloat(key, value)
        is Double -> this.putDouble(key, value)
        is Char -> this.putChar(key, value)
        is Short -> this.putShort(key, value)
        is Boolean -> this.putBoolean(key, value)
        is Parcelable -> this.putParcelable(key, value)
        is SparseArray<*> -> this.putSparseParcelableArray(
            key,
            value as SparseArray<out Parcelable>
        )
        is Array<*> -> when {
            value.isArrayOf<CharSequence>() -> this.putCharSequenceArray(
                key,
                value as Array<out CharSequence>
            )
            value.isArrayOf<String>() -> this.putStringArray(key, value as Array<out String>?)
            value.isArrayOf<Parcelable>() -> this.putParcelableArray(
                key,
                value as Array<out Parcelable>?
            )
            else -> store = false
        }
        is IntArray -> this.putIntArray(key, value)
        is LongArray -> this.putLongArray(key, value)
        is FloatArray -> this.putFloatArray(key, value)
        is DoubleArray -> this.putDoubleArray(key, value)
        is CharArray -> this.putCharArray(key, value)
        is ShortArray -> this.putShortArray(key, value)
        is BooleanArray -> this.putBooleanArray(key, value)
        is Serializable -> when (value) {
            is Collection<*>, is Map<*, *> -> store = false
            else -> this.putSerializable(key, value)
        }
        else -> store = false
    }

    if (store.not()) {
        this.putString(key, value.toJSON())
    }
    return this
}
/***Bundle END***/

//var Intent.id by BaseActivity.IntentExtra()
//var Intent.value by BaseActivity.IntentExtra()
//var Intent.status by BaseActivity.IntentExtra()
//var Intent.url by BaseActivity.IntentExtra()
//var Intent.type by BaseActivity.IntentExtra()
//var Intent.module by BaseActivity.IntentExtra()

/***GSON START***/
fun Any.toJSON(): String {
    return gson.toJson(this)
}

fun <T> String.toObject(clazz: Class<T>): T {
    return gson.convert(this, clazz)
}
/***GSON END***/


/**
 * 委托获取
 * fragment arguments
 * activity extra
 */
fun <T> extraDelegate(extra: String, default: T) = ExtraDelegate(extra, default)



