package com.mooc.home.ui.todaystudy.special

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.home.R
import com.mooc.home.model.todaystudy.TodaySuscribe
import com.mooc.home.ui.todaystudy.column.SubscribeColumnAdapter
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.manager.ResourceUtil
import com.mooc.resource.widget.Space120LoadMoreView
import com.mooc.statistics.LogUtil

/**
 * 订阅专题
 * （资源类型包括，文章。。。）
 */
class TodayStudySpecialFragment : BaseUserLogListenFragment<TodaySuscribe, SpecialViewModel>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel?.todaySuscribeData?.observe(viewLifecycleOwner, Observer {
            setEmptyView(it)
        })
    }


    override fun initAdapter(): BaseQuickAdapter<TodaySuscribe, BaseViewHolder>? {
        return (mViewModel as SpecialViewModel).getPageData().value?.let {
            val discoverMoocAdapter = SubscribeColumnAdapter(it)
            discoverMoocAdapter.addChildClickViewIds(R.id.tvTitle)
            discoverMoocAdapter.setOnItemClickListener { adapter, _, position ->
                if(position !in adapter.data.indices) return@setOnItemClickListener
                val todaySuscribe = it[position]

//                toast("跳转到对应资源页面 ${todaySuscribe.title}")
                ResourceTurnManager.turnToResourcePage(todaySuscribe)

                LogUtil.addClickLogNew(
                    LogEventConstants2.P_TODAY, todaySuscribe._resourceId,
                    "${todaySuscribe._resourceType}",
                    todaySuscribe.title,
                    "${LogEventConstants2.typeLogPointMap[todaySuscribe._resourceType]}#${todaySuscribe._resourceId}"
                )
                ResourceUtil.updateResourceRead(todaySuscribe.id)
                //点击上报任务完成，并进入相应资源
                mViewModel?.postTaskComplete(todaySuscribe)
            }

            discoverMoocAdapter.setOnItemChildClickListener { _, view, position ->
                if (view.id == R.id.tvTitle) {
                    if(position in it.indices){
                        val todaySuscribe = it[position]


                        ARouter.getInstance().build(Paths.PAGE_RECOMMEND_SPECIAL)
                            .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, todaySuscribe.subject_id)
                            .navigation()
                    }
                }
            }
            discoverMoocAdapter
        }
    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }

    /**
     * 设置空布局
     */
    override fun initEmptyView() {
        emptyView.setTitle("没有找到学习资源\r\n订阅一个专题")
        emptyView.setEmptyIcon(R.mipmap.common_ic_empty)
        emptyView.setGravityTop(20.dp2px())
        emptyView.setButton("+添加学习资源", View.OnClickListener {
            if(!GlobalsUserManager.isLogin()){
                ResourceTurnManager.turnToLogin()
                return@OnClickListener
            }
//            toast("跳转专题列表") 实际跳转到了全部专栏
            ARouter.getInstance().build(Paths.PAGE_COLUMN_ALL).navigation()
        })

        mViewModel?.todaySuscribeData?.value?.let {
            if(!it.is_subscribe) return@let
            setEmptyView(it)
        }
    }

    private fun setEmptyView(it: TodaySuscribe) {
        //如果专题不为空，没必要显示空布局了
        if(it.special_status?.isNotEmpty() == true){
            return
        }
//        //如果没订阅
//        if(!it.is_subscribe){
//            initEmptyView()
//            return
//        }
        //如果订阅，并且完成，并且不隐藏
        if (it.is_complete && !it.is_hide) {
            emptyView.setEmptyIcon(R.mipmap.home_bg_susbcribe_complete)
            emptyView.setTitle("恭喜您完成今日内容学习\r\n又有所精进哦")
            emptyView.setButton("") {}
        }
        //订阅，完成了，但是隐藏了
        if (it.is_complete && it.is_hide) {
            emptyView.setEmptyIcon(R.mipmap.common_ic_empty)
            emptyView.setTitle("没有数据")
            emptyView.setButton("") {}
        }


    }
}