package com.mooc.my.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.my.UploadFileBean
import com.mooc.my.repository.MyModelRepository
import com.mooc.commonbusiness.model.HttpResponse
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class UserInfoEditViewModel : BaseViewModel() {

    val mRepository = MyModelRepository()

    /**
     * 上传改变的信息
     * @param userName 用户名
     */
    fun postUserInfo(userName: String) : LiveData<HttpResponse<Any>>{
        val uploadFileBeanLiveData = MutableLiveData<HttpResponse<Any>>()
        val requestData = JSONObject()
        requestData.put("name", userName)
        val toRequestBody =  requestData.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        launchUI {
            val postUserInfo = mRepository.postUserInfo(toRequestBody)
            uploadFileBeanLiveData.postValue(postUserInfo)
        }
        return uploadFileBeanLiveData
    }

    /**
     * 上传用户头像
     */
    fun postUserHead(fileUri: String): LiveData<UploadFileBean?> {
        val uploadFileBeanLiveData = MutableLiveData<UploadFileBean>()
        launchUI ({
            val file = File(fileUri)
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            val toRequestBody = file.asRequestBody("image/*; charset=utf-8".toMediaTypeOrNull())
            builder.addPart(Headers.headersOf("Content-Disposition", "form-data; name=\"" + "imgupload" + "\"; filename =\"" + file.name), toRequestBody)
            val postUserHead = mRepository.postUserHead(builder.build())

            if(postUserHead.code == 200){
                uploadFileBeanLiveData.postValue(postUserHead.data)
            }else{
                uploadFileBeanLiveData.postValue(null)
                toast(postUserHead.msg)
            }
        },{
          toast("网络异常")
          uploadFileBeanLiveData.postValue(null)
        })
        return uploadFileBeanLiveData
    }
}