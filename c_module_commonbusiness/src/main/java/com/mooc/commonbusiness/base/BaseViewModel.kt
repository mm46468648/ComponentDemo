package com.mooc.commonbusiness.base

import androidx.lifecycle.*
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.net.CustomNetCoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *ViewModel基类
 */
open class BaseViewModel : ViewModel() {

    //c
    private val error by lazy { MutableLiveData<Exception>() }
    private val finally by lazy { MutableLiveData<Int>() }

    val coroutineExceptionHandler =CustomNetCoroutineExceptionHandler(error)

    /**
     * 开启ui线程环境的协程
     * viewModelScope可以自动处理内存泄漏问题
     * 如果是普通的开启模式，需要用过作用域在ViewModel销毁时取消
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit) {
//        viewModelScope.launch(coroutineExceptionHandler) {
        viewModelScope.launch(coroutineExceptionHandler) {
            block()
        }


    }

    /**
     * 带错误回调协程
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit, errorBlock: ((e: Exception) -> Unit)? = null) {
        val coroutineExceptionHandler =CustomNetCoroutineExceptionHandler(errorBlock)
        viewModelScope.launch(coroutineExceptionHandler) {
            block()
        }
    }

    /**
     * 请求失败，出现异常
     */
    fun getError(): LiveData<Exception> {
        return error
    }

    /**
     * 请求完成，在此处做一些关闭操作
     */
    fun getFinally(): LiveData<Int> {
        return finally
    }
}