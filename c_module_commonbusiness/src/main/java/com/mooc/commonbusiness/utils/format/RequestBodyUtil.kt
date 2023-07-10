package com.mooc.commonbusiness.utils.format

import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder

class RequestBodyUtil {

    companion object{
        fun fromJson(jsonObject : JSONObject) : RequestBody{
            return jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        }

        fun fromJsonStr(jsonStr : String) : RequestBody{
            return jsonStr.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        }

        fun fromImageFilePath(filePath:String): RequestBody{
            val file = File(filePath)
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
//            val toRequestBody = file.asRequestBody("image/*; charset=utf-8".toMediaTypeOrNull())
            val toRequestBody = file.asRequestBody("image*//*".toMediaTypeOrNull())
            builder.addPart(Headers.headersOf("Content-Disposition", "form-data; name=\"" + "err_img" + "\"; filename =\"" + URLEncoder.encode(file.name,"UTF-8")), toRequestBody)
            return builder.build()
        }
    }
}