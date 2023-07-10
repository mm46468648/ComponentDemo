package com.mooc.commonbusiness.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * 构建ViewModel工厂
 * @param varargs 在构造方法中传递的参数
 * !!!!慎用，可能会在realse模式中，通过反射创建跨模块的类，产生无法找到构造方法的异常
 * 最好直接在创建的地方直接调用，不要用此类了
 */
class BaseViewModelFactory (vararg var varargs: Any) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val toTypedArray = varargs.map {
            it::class.java
        }.toTypedArray()

        return modelClass.getConstructor(*toTypedArray).newInstance(*varargs)
    }
}

