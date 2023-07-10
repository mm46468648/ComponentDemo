package com.mooc.commonbusiness.net

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import android.util.Log
import kotlin.Throws
import com.mooc.common.utils.DebugUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mooc.commonbusiness.net.EncryptBodyBean.DataBeanX
import com.mooc.common.utils.GsonManager
import com.mooc.common.utils.aesencry.AesEncryptUtil
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.common.utils.Md5Util
import com.mooc.common.utils.SystemUtils
import com.mooc.commonbusiness.global.GlobalsUserManager
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.internal.http2.ConnectionShutdownException
import okio.Buffer
import java.io.IOException
import java.math.BigInteger
import java.net.SocketTimeoutException
import java.net.URLDecoder
import java.net.UnknownHostException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.Exception

class RequestEncryptInterceptor : Interceptor {
    @SuppressLint("LongLogTag")
    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest: Request = chain.request()

        try {
            var token = GlobalsUserManager.appToken
            val ts = TimeUtil.uniqueCurrentTimeMS()
            val pubHeaderParamete = getPubHeaderParamete(ts)
            //如果是xt的接口，host示例:https://v1-www.xuetangx.com，header中添加xt的token
            if (ApiService.getXtRootUrl().contains(originRequest.url.host)) {
                val authorisedRequest = originRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", userAgent)
                    .header("Authorization", "Bearer " + GlobalsUserManager.xuetangToken)
                    .header("X-BULK-OPERATION", "true")
                    .build()
                Log.e(TAG, originRequest.url.host + "学堂接口不拦截")
                return chain.proceed(authorisedRequest)
            }
            //如果不加密,cancel-jiami设置为1
            if (DebugUtil.isNoEncrypt) {
                token = if (!TextUtils.isEmpty(token)) "JWT $token" else ""
                val authorisedRequest = originRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", userAgent)
                    .header("Authorization", token) //如果不加密，需要用jwt token的形式返回
                    .header("cancel-jiami", " 1") //如果不加密，传递cancel-jiami 为 1
                    .header("ts", ts.toString())
                    .header("mark", pubHeaderParamete)
                    .header("X-BULK-OPERATION", "true")
                    .build()
                return chain.proceed(authorisedRequest)
            }

            //正式环境的拦截处理
            var charset = StandardCharsets.UTF_8
            val method = originRequest.method.lowercase(Locale.getDefault()).trim { it <= ' ' }


            //获取api路径
            val aesPath = originRequest.url.encodedPath
            //根据路径使用加密方法加密Authrozation
//        String aesToken = (!TextUtils.isEmpty(token)) ?buildAesToken(token, aesPath, l) : "";
            val aesToken = buildAesToken(token, aesPath, ts)
            var request: Request
            //        List<Object> list = getPubHeaderParamete(ts);
            request = originRequest.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", userAgent)
                .header("Authorization", aesToken)
                .header("uuid", GlobalsUserManager.uuid)
                .header("uid", GlobalsUserManager.uid)
                .header("ts", ts.toString())
                .header("mark", pubHeaderParamete)
                .header("X-BULK-OPERATION", "true")
                .build()
            val requestBody = request.body
            if ("post" == method && requestBody != null) {
                val contentType = requestBody.contentType() ?: return chain.proceed(request)
                val jsonType: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
                charset = contentType.charset(charset)
                if (contentType.type.lowercase(Locale.getDefault()) == "multipart") {     /*如果是二进制上传(文件) 则不进行加密*/
                    return chain.proceed(request)
                }
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                assert(charset != null)
                var trim = buffer.readString(charset!!).trim { it <= ' ' }
                // 解决URLDecoder遇到%解析时会产生异常，将%后两位补充25
                trim = trim.replace("%(?![0-9a-fA-F]{2})".toRegex(), "%25")
                var requestData = URLDecoder.decode(trim, "utf-8")

                //获取参数
                val gson = Gson()
                var valueMap = HashMap<String, Any>()
                if (contentType.toString().lowercase(Locale.getDefault())
                        .contains("application/json")
                ) {
                    val type = object : TypeToken<HashMap<String, Any>>() {}.getType()
//                    valueMap = gson.fromJson(requestData, HashMap::class.java) as HashMap<String, String>
                    valueMap = GsonManager.getInstance().fromJson(requestData,type)
                } else {
                    strRequest(valueMap, requestData)
                }
                val encryptBodyBean = EncryptBodyBean()
                val dataBeanX = DataBeanX()
                //加密数据赋值
                dataBeanX.data = valueMap
                dataBeanX.client_type = 1 //APP是1  h5是2
                dataBeanX.token_check = signByMd5(aesToken)
                dataBeanX.ts = ts
                requestData = gson.toJson(dataBeanX)
                val encodeStr: String
                //数据加密
                encodeStr = buildEncryptWithMap(aesPath, requestData)
                encryptBodyBean.data = encodeStr
                //转成json
                val encode = gson.toJson(encryptBodyBean) //生成新的请求body->json
                val newRequestBody: RequestBody = RequestBody.create(jsonType, encode)
                val newRequestBuilder = request.newBuilder()
                newRequestBuilder.post(newRequestBody)
                request = newRequestBuilder.build()
            }
            return chain.proceed(request)
        }catch (e:Exception){
            e.printStackTrace()
            var msg = ""
            when (e) {
                is SocketTimeoutException -> {
                    msg = "Timeout - Please check your internet connection"
                }
                is UnknownHostException -> {
                    msg = "Unable to make a connection. Please check your internet"
                }
                is ConnectionShutdownException -> {
                    msg = "Connection shutdown. Please check your internet"
                }
                is IOException -> {
                    msg = "Server is unreachable, please try again later."
                }
                is IllegalStateException -> {
                    msg = "${e.message}"
                }
                else -> {
                    msg = "${e.message}"
                }
            }

            return Response.Builder()
                .request(originRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(999)
                .message(msg)
                .body(ResponseBody.create(null, "{${e}}")).build()
        }
    }

    fun strRequest(map: MutableMap<String, Any>, s: String): String?{
        val index1 = s.indexOf("=")
        val parm1 = s.substring(0, index1)
        val index2 = s.indexOf("&")
        if (index2 == -1) {
            val parm2 = s.substring(index1 + 1)
            map[parm1] = parm2
            return null
        }
        val parm2 = s.substring(index1 + 1, index2)
        map[parm1] = parm2
        return strRequest(map, s.substring(index2 + 1))
    }

    fun signByMd5(vararg str: String): String {
        val md: MessageDigest
        md = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return ""
        }
        for (temp in str) {
            md.update(temp.toByteArray())
        }
        return String.format("%032x", BigInteger(1, md.digest()))
    }

    fun buildAesToken(token: String, path: String, ts: Long): String {
        val hashMap = HashMap<String, String>()
        hashMap["jwt"] = token
        hashMap["ts"] = ts.toString()
        return buildEncryptWithMap(path, GsonManager.getInstance().toJson(hashMap))
    }

    var userAesArray = arrayOf("/api/mobile/version/", "/api/web/message/alert_message/")
    fun buildEncryptWithMap(path: String, enData: String): String {
        var useAes = false
        for (s in userAesArray) {
            if (s == path) {
                useAes = true
                break
            }
        }
        return if (useAes) {
            try {
                AesEncryptUtil.aesEncrypt(enData)
            } catch (e: Exception) {
                enData
            }
        } else {
            try {
                AesEncryptUtil.encrypt(enData)
            } catch (e: Exception) {
                enData
            }
        }
    }

    fun getPubHeaderParamete(currentTime: Long): String {
        val list: MutableList<Any> = ArrayList()
        //        String time = TimeUtils.getFormatTime(currentTime, "MM-dd-HH-mm-ss");
        val time = TimeFormatUtil.formatDate(currentTime, "MM-dd-HH-mm-ss")
        val strs = time.split("-").toTypedArray()
        val M = strs[0].toInt()
        val D = strs[1].toInt()
        val H = strs[2].toInt()
        val mm = strs[3].toInt()
        val S = strs[4].toInt()
        var split = MARK_KEY.substring(M, M + 1) + MARK_KEY.substring(D, D + 1) +
                MARK_KEY.substring(H, H + 1) + MARK_KEY.substring(
            mm,
            mm + 1
        ) + MARK_KEY.substring(S, S + 1)
        split = Md5Util.getMD5Str(split + currentTime)
        split = split.substring(split.length - 8, split.length)
        list.add(currentTime)
        list.add(split)
        return split
    }// default to "1.0"//TODO:替换当前app版本号
    //如果是调试模式，将版本改为2.8.5不加密模式
    //测试环境通过请求头加参数，这样能享受当前版本最新功能，正式环境如果抓包还是需要改为，2.8.5
    //不知道什么时候需要变为这个
//        webUserAgent = "moocnd android/" + strVersionName + " is_master_talk" + " (Linux; U; Android %s)";
//        Locale locale = Locale.getDefault();
    // Add version
    /**
     * 获取useragent
     *
     * @return
     */
    val userAgent: String
        get() {
            var strVersionName = SystemUtils.getVersionName()
            if (TextUtils.isEmpty(strVersionName)) {
                //TODO:替换当前app版本号
                strVersionName = "3.4.3.1"
            }
            //如果是调试模式，将版本改为2.8.5不加密模式
            //测试环境通过请求头加参数，这样能享受当前版本最新功能，正式环境如果抓包还是需要改为，2.8.5
            if (DebugUtil.isNoEncrypt && ApiService.BASE_URL == ApiService.NORMAL_BASE_URL) {
                strVersionName = "2.8.5"
            }
            val webUserAgent = "moocnd android/$strVersionName (Linux; U; Android %s)"
            //不知道什么时候需要变为这个
//        webUserAgent = "moocnd android/" + strVersionName + " is_master_talk" + " (Linux; U; Android %s)";
//        Locale locale = Locale.getDefault();
            val buffer = StringBuffer()
            // Add version
            val version = Build.VERSION.RELEASE
            if (version.length > 0) {
                buffer.append(version)
            } else {
                // default to "1.0"
                buffer.append(strVersionName)
            }
            if ("REL" == Build.VERSION.CODENAME) {
                val model = Build.MODEL
                if (model.length > 0) {
                    buffer.append("; ")
                    buffer.append(model)
                }
            }
            val id = Build.ID
            if (id.length > 0) {
                buffer.append(" Build/")
                buffer.append(id)
            }
            return String.format(webUserAgent, buffer, "Mobile ")
        }

    companion object {
        const val TAG = "RequestEncryptInterceptor"
        const val MARK_KEY =
            "BwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQU"
    }
}