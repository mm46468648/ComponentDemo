package com.mooc.commonbusiness.interfaces

/**
 * 资源适配器接口
 * 定义跳转进相应资源页面的方法
 */
interface ResourseAdapterInterface {

    /**
     * 跳转相应的资源页面
     */
    fun <T>turnToResoursePage(resourse: BaseResource<T>){
//        val routPath = ResourceTypeConstans.typeRoutePath[resourse.resourseType]
//        val bundle = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_OBJ, resourse.resourseObj)
//        ARouter.getInstance().build(routPath).with(bundle).navigation()
    }
}