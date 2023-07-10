package com.mooc.studyroom.ui.fragment.integral

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.adapter.IntegralExchangeAdapter
import com.mooc.studyroom.Constans
import com.mooc.studyroom.model.IntegralListBean

class IntegralExchangeFragment: BaseListFragment2<Any, com.mooc.studyroom.viewmodel.IntegraViewModel>() {

    private var totalScore = 0

    companion object {
        fun getInstance(bundle: Bundle? = null): IntegralExchangeFragment {
            val fragment = IntegralExchangeFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        totalScore= arguments?.getInt(Constans.INTENT_TOTAL_SCORE)!!;

    }

    override fun initAdapter(): BaseQuickAdapter<Any, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value.let {
            val integralExchangeAdapter = IntegralExchangeAdapter(it)
            integralExchangeAdapter.setOnItemClickListener { adapter, view, position ->
                var  bean:IntegralListBean= adapter.data.get(position)  as IntegralListBean;

                ARouter.getInstance().build(Paths.PAGE_EXCHANGE_POINT).withSerializable(Constans.INTENT_EXCHANGE_POINT,bean)
                    .withBoolean(Constans.INTENT_EXCHANGE_PAGE,false).withInt(Constans.INTENT_TOTAL_SCORE,totalScore).navigation();
            }
            integralExchangeAdapter
        }
    }


}