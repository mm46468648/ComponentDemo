package com.mooc.my.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.my.ParserStatusBean
import com.mooc.my.model.SchoolCircleBean
import com.mooc.my.repository.MyModelRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject


/**

 * @Author limeng
 * @Date 2020/11/5-2:39 PM
 */
class MyInfoViewModelL : BaseViewModel() {
    private val repository = MyModelRepository()
    val mParserStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }
    val mFollowStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }
    val mDeleteStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }
    val mSchoolCircleBean: MutableLiveData<SchoolCircleBean> by lazy {
        MutableLiveData<SchoolCircleBean>()
    }
    val mUserParserStatusBean: MutableLiveData<ParserStatusBean> by lazy {
        MutableLiveData<ParserStatusBean>()
    }
    //对单个人主页点赞和取消
    fun postUserLikeAndDis(userId: String?,type:Int) {
        val requestData = JSONObject()
        try {
            requestData.put("other_user_id", userId)
            requestData.put("type", type.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

        launchUI {
            var bean = repository.postUserLikeAndDis(stringBody)
            mUserParserStatusBean.postValue(bean)
        }
    }
    fun getUserSchoolCircle(userId: String) {
        launchUI {
            var bean = repository.getUserSchoolCircle(userId)
            mSchoolCircleBean.postValue(bean)
        }
    }

    fun postLikeSchoolResource(body: RequestBody) {
        launchUI {
            var bean = repository.postLikeSchoolResource(body)
            mParserStatusBean.postValue(bean)
        }
    }

    fun postFollowUser(body: RequestBody) {
        launchUI {
            var bean = repository.postFollowUser(body)
            mFollowStatusBean.postValue(bean)
        }
    }

    fun delSchoolResource(body: RequestBody) {
        launchUI {
            var bean = repository.delSchoolResource(body)
            mDeleteStatusBean.postValue(bean)
        }
    }
}