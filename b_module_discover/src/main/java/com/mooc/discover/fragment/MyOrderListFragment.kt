package com.mooc.discover.fragment

import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.discover.adapter.MyOrderAdapter
import com.mooc.discover.model.MyOrderBean
import com.mooc.resource.widget.Space120LoadMoreView
import com.mooc.discover.viewmodel.MyOrderViewModel
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.manager.ResourceUtil
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.discover.view.DiscoverAcitivtyFloatView
import com.mooc.statistics.LogUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MyOrderListFragment : BaseListFragment2<MyOrderBean.ResultsBean, MyOrderViewModel>(),
    DiscoverAcitivtyFloatView.FloatViewVisibale {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachToScroll(recycler_view,requireActivity())
    }

    override fun initAdapter(): BaseQuickAdapter<MyOrderBean.ResultsBean, BaseViewHolder>{
        return mViewModel?.getPageData()?.value.let {
            val myOrderAdapter = MyOrderAdapter(it?: arrayListOf())
            myOrderAdapter.setOnItemClickListener { adapter, view, position ->
                it?.get(position)?.let { it1 ->  //跳转对应资源页面

                    LogUtil.addClickLogNew(
                        LogEventConstants2.P_DISCOVER,
                        it1._resourceId,
                        "${it1._resourceType}",
                        "",
                        "${LogEventConstants2.typeLogPointMap[it1._resourceType]}#${it1._resourceId}"
                    )
                    ResourceTurnManager.turnToResourcePage(it1)

                    ResourceUtil.updateResourceRead(it1.id)
                }
            }
            myOrderAdapter
        }
    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
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