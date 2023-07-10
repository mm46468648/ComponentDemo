package com.mooc.commonbusiness.base

import androidx.fragment.app.Fragment


open class BaseFragment : Fragment() {


    override fun onDestroy() {
        super.onDestroy()

        //统一处理EventBus解注册逻辑
//        if(EventBus.getDefault().isRegistered(this)){
//            EventBus.getDefault().unregister(this)
//        }
    }
}