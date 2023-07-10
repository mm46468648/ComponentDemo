package com.mooc.commonbusiness.base

import android.util.Log
import com.google.gson.Gson
import com.mooc.common.ktextends.toastOnce
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.EncryptParseData
import com.mooc.commonbusiness.net.getResponseStr
import com.mooc.commonbusiness.net.network.DataState
import com.mooc.commonbusiness.net.network.StateLiveData
import com.mooc.common.utils.aesencry.AesEncryptUtil
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.ProhibitUserLoginBean
import com.mooc.common.utils.DebugUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    仓库基类，统一处理协程环境以及错误回调
 * @Author:         xym
 * @CreateDate:     2020/8/21 1:38 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/21 1:38 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
open class BaseRepository {
    val gson = Gson()

    companion object {
        const val ERROR_CODE_UNAUTHORIZED = 401 //token过期
        const val UNAUTHORIZED_CODE_10011 = "10011" //token过期
        const val UNAUTHORIZED_CODE_10001 = "10001" //token过期

        var loginActivityStart = false //登录页面是否启动

        @JvmStatic
        fun onErrorHandle(errorCode:Int,errorMsg:String) {

                if (errorCode == ERROR_CODE_UNAUTHORIZED) {
                    if (errorMsg.contains(UNAUTHORIZED_CODE_10011) || errorMsg.contains(
                                    UNAUTHORIZED_CODE_10001)||errorMsg.contains("10013")) {
                        val bean = Gson().fromJson(errorMsg, ProhibitUserLoginBean::class.java)
                        if (!bean.message?.detail.isNullOrEmpty()) {
                            toastOnce(bean.message?.detail!!)
                        }
                        if (!loginActivityStart) {
                            loginActivityStart = true
                            GlobalsUserManager.clearUserInfo()
                            ResourceTurnManager.turnToLogin()
                        }
                        return
                    }
                }

        }
    }

    suspend fun <T : Any> request(call: suspend () -> T): T {
        return withContext(Dispatchers.IO) { call.invoke() }
    }


    /**
     * 带有统一格式的网络请求
     */
    suspend fun <T : Any> requestHttp(
            block: suspend () -> HttpResponse<T>,
            stateLiveData: StateLiveData<T>
    ) {

        var baseResp = HttpResponse<T>()
        try {
            baseResp.dataState = DataState.STATE_LOADING
            //开始请求数据
            val invoke = block.invoke()
            //将结果复制给baseResp
            baseResp = invoke
            if (baseResp.code != 200) {
                //请求成功，判断数据是否为空，
                //因为数据有多种类型，需要自己设置类型进行判断
                if (baseResp.data == null || baseResp.data is List<*> && (baseResp.data as List<*>).size == 0) {
                    //TODO: 数据为空,结构变化时需要修改判空条件
                    baseResp.dataState = DataState.STATE_EMPTY
                } else {
                    //请求成功并且数据为空的情况下，为STATE_SUCCESS
                    baseResp.dataState = DataState.STATE_SUCCESS
                }

            } else {
                //服务器请求错误
                baseResp.dataState = DataState.STATE_FAILED
            }
        } catch (e: Exception) {
            //非后台返回错误，捕获到的异常
            baseResp.dataState = DataState.STATE_ERROR
            baseResp.error = e
            if (e is HttpException) {
                val errorMsg = getResponseStr(e)
                val errorCode = e.code()
                onErrorHandle(errorCode,errorMsg)

            }
        } finally {
            stateLiveData.postValue(baseResp)
        }
    }


    /**
     * 获取详细的错误的信息 errorCode,errorMsg
     * 以登录的时候的Grant_type 故意写错为例子,这个时候的http 应该是直接的返回401=httpException.code()
     * 但是是怎么导致401的？我们的服务器会在respose.errorBody 中的content 中说明
     */
    private fun getErrorMsg(httpException: HttpException): String {
        var errorBodyStr = ""
        //我们的项目需要的UniCode转码 ,!!!!!!!!!!!!!!
        var body: ResponseBody? = null
        try {
            body = httpException.response()?.errorBody()
            val parseData: EncryptParseData =
                    gson.fromJson(body?.string() ?: "", EncryptParseData::class.java)
            val data: String = parseData.data
            errorBodyStr = data
            if (!DebugUtil.isNoEncrypt) {    //加密的接口
                errorBodyStr = AesEncryptUtil.decrypt(data)
            }


            Log.e("BaseRepositoryencrypty:", errorBodyStr)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            body?.close()
        }
        return errorBodyStr
    }


    /**
     * 没有统一格式的网络请求
     */
    suspend fun <T : Any> requestNoFormatHttp(
            block: suspend () -> T,
            stateLiveData: StateLiveData<T>
    ) {
        val baseResp = HttpResponse<T>()
        try {
            baseResp.dataState = DataState.STATE_LOADING
            //开始请求数据
            val invoke = block.invoke()
            //将结果复制给baseResp
            baseResp.data = invoke
//            if (baseResp.code != 200) {
            //请求成功，判断数据是否为空，
            //因为数据有多种类型，需要自己设置类型进行判断
            if (baseResp.data == null || baseResp.data is List<*> && (baseResp.data as List<*>).size == 0) {
                //TODO: 数据为空,结构变化时需要修改判空条件
                baseResp.dataState = DataState.STATE_EMPTY
            } else {
                //请求成功并且数据为空的情况下，为STATE_SUCCESS
                baseResp.dataState = DataState.STATE_SUCCESS
            }

//            } else {
//                //服务器请求错误
//                baseResp.dataState = DataState.STATE_FAILED
//            }
        } catch (e: Exception) {
            //非后台返回错误，捕获到的异常
            baseResp.dataState = DataState.STATE_ERROR
            baseResp.error = e
        } finally {
            stateLiveData.postValue(baseResp)
        }
    }

}