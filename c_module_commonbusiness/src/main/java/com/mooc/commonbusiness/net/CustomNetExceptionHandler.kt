package com.mooc.commonbusiness.net

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mooc.common.global.AppGlobals
import com.mooc.common.utils.aesencry.AesEncryptUtil
import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.model.ProhibitUserLoginBean
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.common.utils.DebugUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.net.UnknownHostException
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

const val CODE_NO_TOAST = -267
const val CODE_HAS_TOAST = -277
const val CODE_AUTHENTICATION = 401 //登录认证
const val CODE_RESOURCE_NOT_FOUND = 404 //资源未找到

/**
 * 自定义的网络异常处理类
 *
 */
val gson = Gson()

public fun CustomNetCoroutineExceptionHandler(error: MutableLiveData<Exception>): CoroutineExceptionHandler =
    object : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
        override fun handleException(context: CoroutineContext, exception: Throwable) {
            if (exception is HttpException) {
                val errorCode = exception.code()
                resolveErrcode(errorCode, exception)
                error.postValue(exception)
            } else if (exception is UnknownHostException) {  //主机无法解析
                changeRootUrl()
                error.postValue(exception)
            } else if (exception is java.lang.Exception) {
                error.postValue(exception)
            } else {
                throw exception
            }
        }


    }

public fun CustomNetCoroutineExceptionHandler(errorBlock: ((e: Exception) -> Unit)? = null): CoroutineExceptionHandler =
    object : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
        override fun handleException(context: CoroutineContext, exception: Throwable) {
            if (exception is HttpException) {
                val errorCode = exception.code()
                resolveErrcode(errorCode, exception)
                errorBlock?.invoke(exception)
            } else if (exception is UnknownHostException) {//主机无法解析
                changeRootUrl()
                errorBlock?.invoke(exception)
            } else if (exception is java.lang.Exception) {
                errorBlock?.invoke(exception)
            } else {
                throw exception
            }
        }
    }

/**
 * 切换根域名
 */
private fun changeRootUrl() {
    ApiService.setUserNewUrl(!ApiService.userNewUrl)
}
/**
 * 解析错误码
 */
public fun resolveErrcode(code: Int, exception: HttpException) {
    when (code) {
        CODE_AUTHENTICATION -> {
            val responseStr = getResponseStr(exception)

            if (responseStr.contains("10011") || responseStr.contains("10001") || responseStr.contains("10013")) {
                val bean = Gson().fromJson(responseStr, ProhibitUserLoginBean::class.java)
                runOnMain {
                    Toast.makeText(
                        AppGlobals.getApplication(),
                        bean.message?.detail ?: "请重新登录",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            if (!BaseRepository.loginActivityStart) {
                BaseRepository.loginActivityStart = true
                GlobalsUserManager.clearUserInfo()
                ResourceTurnManager.turnToLogin()
            }
        }
        CODE_RESOURCE_NOT_FOUND -> {
            val responseStr = getResponseStr(exception)
            if (!TextUtils.isEmpty(responseStr) && responseStr.contains("10001")) {
                runOnMain {
                    Toast.makeText(AppGlobals.getApplication(), "资源已下线", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@JvmField
val msMainLooperHandler = Handler(Looper.getMainLooper())

fun runOnMain(run: () -> Unit) {
    msMainLooperHandler.post(run)
}

/**
 * 登录过期
 */
fun onLoginbeOverdue() {
    Toast.makeText(AppGlobals.getApplication(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show()
    GlobalsUserManager.clearUserInfo()
//    ARouter.getInstance().build(Paths.PAGE_LOGIN).withFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).navigation()
    ResourceTurnManager.turnToLogin()
}

///**
// * 账号禁用
// */
//fun accountsDisabled(){
//    Toast.makeText(AppGlobals.getApplication(),"账号已禁用，请联系客服!", Toast.LENGTH_SHORT).show()
//}
/**
 * 获取详细的错误的信息 errorCode,errorMsg
 * 以登录的时候的Grant_type 故意写错为例子,这个时候的http 应该是直接的返回401=httpException.code()
 * 但是是怎么导致401的？我们的服务器会在respose.errorBody 中的content 中说明
 */
private fun getErrorMsg(httpException: HttpException): String {
    var errorMsg = ""
    var errorBodyStr = ""
    var body: ResponseBody? = null

    try {
        body = httpException.response()!!.errorBody()
        errorBodyStr = body!!.string()

        val errorResponse = gson.fromJson(errorBodyStr, HttpResponse::class.java)
        if (null != errorResponse) {
            if (!TextUtils.isEmpty(errorResponse.getMsg())) {
                errorMsg = errorResponse.getMsg()
            } else if (!TextUtils.isEmpty(errorResponse.getMessage())) {
                errorMsg = errorResponse.getMessage()
            }
        }
    } catch (e: Exception) {

    } finally {
        if (errorMsg.isEmpty()) {
            errorMsg = httpException.message()
        }
    }
    return errorMsg
}

/**
 * 获取响应的字符串
 */
fun getResponseStr(httpException: HttpException): String {
    var responseStr = ""
    var errorBodyStr = ""

    //我们的项目需要的UniCode转码 ,!!!!!!!!!!!!!!
    var body: ResponseBody? = null
    try {
        body = httpException.response()?.errorBody()
        errorBodyStr = body?.string() ?: ""
//        logi(TAG, errorBodyStr)

        //不需要解密的，直接解析
        if (DebugUtil.isNoEncrypt) {
            responseStr = errorBodyStr
            return responseStr
        }

        val parseData: EncryptParseData = gson.fromJson(errorBodyStr, EncryptParseData::class.java)
        val data = parseData.getData()
        responseStr = AesEncryptUtil.decrypt(data)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        responseStr = errorBodyStr
    } finally {
        body?.close()
    }

    return responseStr
}
