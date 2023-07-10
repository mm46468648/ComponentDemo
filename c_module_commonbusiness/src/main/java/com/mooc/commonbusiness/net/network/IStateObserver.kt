package com.mooc.commonbusiness.net.network

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.common.ktextends.toast

private const val TAG = "IStateObserver"

/**
 * @date：2021/5/20
 * @author xym
 * @instruction：LiveData Observer的一个类，
 * 可以结合LoadSir，根据BaseResp里面的State分别加载不同的UI，如Loading，Error
 * 同时重写onChanged回调，分为onDataChange，onDataEmpty，onError，
 * 开发者可以在UI层，每个接口请求时，直接创建IStateObserver，重写相应callback。
 */
abstract class IStateObserver<T>(view: View? = null) : Observer<HttpResponse<T>> {



    override fun onChanged(t: HttpResponse<T>) {
        Log.d(TAG, "onChanged: ${t.dataState}")

        when (t.dataState) {
            DataState.STATE_SUCCESS -> {
                //请求成功，数据不为null
                onDataChange(t.data)
            }

            DataState.STATE_EMPTY -> {
                //数据为空
                onDataEmpty()
            }

            DataState.STATE_FAILED, DataState.STATE_ERROR -> {
                //请求错误
                t.error?.let {
                    onError(it)
                }
            }
            else -> {
            }
        }

        //加载不同状态界面
//        Log.d(TAG, "onChanged: mLoadService $mLoadService")
//        mLoadService?.showWithConvertor(t)

    }

    /**
     * 请求数据且数据不为空
     */
    open fun onDataChange(data: T?) {

    }

    /**
     * 请求成功，但数据为空
     */
    open fun onDataEmpty() {

    }

    /**
     * 请求错误
     */
    open fun onError(e: Throwable?) {

    }

    /**
     * 弹Toast
     */
    private fun showToast(msg: String) {
        toast(msg)
    }
}