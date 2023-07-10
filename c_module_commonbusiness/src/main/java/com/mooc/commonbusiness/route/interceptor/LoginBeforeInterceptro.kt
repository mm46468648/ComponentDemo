  package com.mooc.commonbusiness.route.interceptor

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths

/**
 * 登录拦截
 * 跳转之前需要先登录的页面
 */
@Interceptor(priority = InterceptorPriorityConstants.LoginBeforePriority, name = "loginInterceptor")
class LoginBeforeInterceptro : IInterceptor {

    //需要进行拦截的页面
    val needLoginBeforePagePath: Array<String>
        get() = arrayOf(Paths.PAGE_USER_INFO,Paths.PAGE_COURSE_DETAIL, Paths.PAGE_STUDYPROJECT
                , Paths.PAGE_COLUMN_SUBSCRIBE_ALL,Paths.PAGE_EBOOK_DETAIL
        , Paths.PAGE_STUDY_RECORD, Paths.PAGE_DATA_BOARD, Paths.PAGE_USERINFO_EDIT, Paths.PAGE_DATA_BOARD,Paths.PAGE_ALBUM,Paths.PAGE_PUBLICATION_DETAIL
        ,Paths.PAGE_CHECKIN,Paths.PAGE_SCHOOL_CIRCLE,Paths.PAGE_EVERYDAY_READ,Paths.PAGE_CONTRIBUTE_TASK,Paths.PAGE_AUDIO_PLAY
        ,Paths.PAGE_MY_MSG,Paths.PAGE_DISCOVER_TASK,Paths.PAGE_FRIEND_SCORE_RANK)

    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        //如果路由地址在需要拦截的地址里，则进行学习计划查询后再判断是拦截还是跳转登录页
        if (postcard?.path in needLoginBeforePagePath) {
            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                callback?.onInterrupt(Exception("未登录先登录"))
                return
            }
        }
        //如果登录,或者不需要判断登录了则继续
        callback?.onContinue(postcard)
    }

    override fun init(context: Context?) {
    }
}