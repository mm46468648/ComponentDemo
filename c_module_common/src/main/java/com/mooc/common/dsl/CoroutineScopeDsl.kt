package com.mooc.common.dsl

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <T> CoroutineScope.rxLaunch(init: CoroutineBuilder<T>.() -> Unit) {
    val result = CoroutineBuilder<T>().apply(init)
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        result.onError?.invoke(exception)
    }
    launch(coroutineExceptionHandler) {
        val res: T? = result.onRequest?.invoke()
        res?.let {
            result.onSuccess?.invoke(it)
        }
    }
}

class CoroutineBuilder<T> {
    var onRequest: (suspend () -> T)? = null
    var onSuccess: ((T) -> Unit)? = null
    var onError: ((Throwable) -> Unit)? = null
}
