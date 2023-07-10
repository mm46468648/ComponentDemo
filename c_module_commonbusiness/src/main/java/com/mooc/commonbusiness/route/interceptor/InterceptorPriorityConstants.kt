package com.mooc.commonbusiness.route.interceptor

/**
 * 拦截器优先级排序常量
 * 数值越低优先级就越高
 * 创建时按需求调整顺序
 */
class InterceptorPriorityConstants {

    companion object{
        const val LoginBeforePriority = 1      //先校验登录
        const val ResourcePagePriority = 2      //资源页面跳转
    }
}