package com.mooc.commonbusiness.route.interceptor

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.put
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.studyproject.JoinStudyState
import com.mooc.commonbusiness.route.Paths
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


/**
 * 资源页面跳转前的拦截
 */
@Interceptor(priority = InterceptorPriorityConstants.ResourcePagePriority,name = "rousourcePageInterceptor")
class ResourcePageInterceptor : IInterceptor {

    //需要进行拦截的页面
    val needIntercepPagePath: Array<String>
        get() = arrayOf(Paths.PAGE_COURSE_DETAIL,Paths.PAGE_EBOOK_DETAIL,Paths.PAGE_WEB_RESOURCE)

    var mContext : Context? = null
    override fun process(postcard: Postcard, callback: InterceptorCallback?) {
        //如果路由地址在需要拦截的地址里，则进行学习计划查询后再判断是拦截还是拒绝
        if(postcard.path in needIntercepPagePath){
            getResourceShow(postcard,callback)
        }else{
            callback?.onContinue(postcard)
        }
    }

    override fun init(context: Context?) {
        mContext = context
    }


    /**
     * 查询是否显示
     */
    fun getResourceShow(postcard: Postcard, callback: InterceptorCallback?) {
        val resourceType = postcard.extras.getInt(IntentParamsConstants.PARAMS_RESOURCE_TYPE)
        val resourceUrl = postcard.extras.getString(IntentParamsConstants.PARAMS_RESOURCE_URL)
//        loge("当前线程 ${Thread.currentThread()}")

        //测试卷,问卷,微专业不需要查，直接进
        if(resourceType == ResourceTypeConstans.TYPE_TEST_VOLUME
            || resourceType == ResourceTypeConstans.TYPE_QUESTIONNAIRE
            || resourceType == ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK
            || resourceType == ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL
        ){
            callback?.onContinue(postcard)
            return
        }

        //缺少参数也不需要查
        if(TextUtils.isEmpty(resourceUrl)){
            callback?.onContinue(postcard)
            return
        }

        val requestData = JSONObject()
        requestData.put("resource_type", resourceType)
        requestData.put("resource_url", resourceUrl)
        val toRequestBody = requestData.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())



        HttpService.commonApi.getResourceShow(toRequestBody)
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<HttpResponse<JoinStudyState>>(mContext){
                    override fun onSuccess(t: HttpResponse<JoinStudyState>?) {
                        t?.data?.apply {
                            if(this.is_join_studyplan == 0 && resource_show_status == 0){
                                //如果没加入学习项目，并且不显示资源页面，需跳转到该资源属于的学习项目简介页面
                                val put = Bundle().put("simpleIntro", this).put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, resourceType)
                                ARouter.getInstance().build(Paths.PAGE_RES_SIMPLE_INTRO).with(put).navigation()
                                callback?.onInterrupt(Exception("需要先调用是否显示资源接口"))
                                return@apply
                            }
                            //执行原本操作
                            callback?.onContinue(postcard)
                        }
                    }

                    override fun onFailure(code: Int, message: String?) {
                        super.onFailure(code, message)

                        //查询失败执行原本操作
                        callback?.onContinue(postcard)
                    }

                })
    }

}