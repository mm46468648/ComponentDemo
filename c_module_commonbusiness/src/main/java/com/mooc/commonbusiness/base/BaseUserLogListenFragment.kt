package com.mooc.commonbusiness.base

import android.os.Bundle
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.common.ktextends.loge
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 监听用户登录事件的列表fragment
 */
abstract class BaseUserLogListenFragment<T,M:BaseListViewModel<T>> : BaseListFragment<T,M>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUserEvent(userInfo: UserLoginStateEvent){
        loge("${this::class.java.simpleName}收到了登录事件")
        if(userInfo.userInfo == null){
            //退出登录，直接清空列表数据
            mViewModel?.getPageData()?.value?.clear()
            mViewModel?.getPageData()?.postValue(mViewModel?.getPageData()?.value)
            mAdapter?.notifyDataSetChanged()
        }else{
            loadDataWithRrefresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }
}