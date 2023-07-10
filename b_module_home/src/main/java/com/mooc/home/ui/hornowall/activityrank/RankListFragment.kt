package com.mooc.home.ui.hornowall.activityrank

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.route.Paths
import com.mooc.home.model.ActivityRank

/**
 * 活动排行榜
 */
class RankListFragment : BaseListFragment<ActivityRank, ActivityRankViewModel>() {

    companion object{
        fun getInstance(bundle: Bundle? = null): RankListFragment {
            val fragment = RankListFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }


    override fun initAdapter(): BaseQuickAdapter<ActivityRank, BaseViewHolder>? {
        val datas = (mViewModel as ActivityRankViewModel).getPageData()?.value
        return datas?.let {
            val honorActivityRankListAdapter = HonorActivityRankListAdapter(it)
            honorActivityRankListAdapter.setOnItemClickListener { _, _, position ->
                ARouter.getInstance().build(Paths.PAGE_WEB)
                        .with(Bundle()
                            .put(IntentParamsConstants.WEB_PARAMS_URL,it[position].activity_url)
                            .put(IntentParamsConstants.WEB_PARAMS_TITLE,it[position].name))
                        .navigation()
            }
            honorActivityRankListAdapter
        }
    }

    override fun neadLoadMore(): Boolean  = false


}