package com.mooc.common.ktextends
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import kotlin.reflect.KProperty


/**
 * intent 委托
 * 直接在成员变量上获取intent传递的数据
 */
class ExtraDelegate<out T>(private val extraName: String, private val defaultValue: T) {

    private var extra: T? = null

    operator fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        extra = getExtra(extra, extraName, thisRef)
        return extra ?: defaultValue
    }

    operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        extra = getExtra(extra, extraName, thisRef)
        return extra ?: defaultValue
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getExtra(oldExtra: T?, extraName: String, thisRef: AppCompatActivity): T? {
        return if(oldExtra != thisRef.intent?.extras?.get(extraName)){
            thisRef.intent?.extras?.get(extraName) as T?
        }else{
            oldExtra
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getExtra(oldExtra: T?, extraName: String, thisRef: Fragment): T? {
        return if(oldExtra != thisRef.arguments?.get(extraName)){
            oldExtra ?: thisRef.arguments?.get(extraName) as T?
        }else{
            oldExtra
        }
    }
}


