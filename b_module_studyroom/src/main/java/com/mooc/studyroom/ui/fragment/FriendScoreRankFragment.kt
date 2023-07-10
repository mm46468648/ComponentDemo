package com.mooc.studyroom.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.util.set
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.studyroom.model.FriendRank
import com.mooc.studyroom.model.ScoreDetail
import com.mooc.studyroom.ui.adapter.FriendScoreAdapter
import com.mooc.studyroom.viewmodel.FriendScoreRankAvtivityViewModel
import com.mooc.studyroom.viewmodel.FriendScoreRankViewModel

/**
 * 好友积分排行
 */
class FriendScoreRankFragment :BaseListFragment2<FriendRank, FriendScoreRankViewModel>(){

    companion object{
        const val PARAMS_RANK_TYPE = "params_rank_type"     //排行榜类型
        const val RANK_TYPE_ALL = 0    //总榜
        const val RANK_TYPE_DAY = 1     //日榜
        const val RANK_TYPE_WEEK = 2     //周榜
        const val RANK_TYPE_MONTH = 3     //月榜

        fun getInstence(type : Int) : FriendScoreRankFragment{
            val friendScoreRankFragment = FriendScoreRankFragment()
            friendScoreRankFragment.arguments = Bundle().put(PARAMS_RANK_TYPE,type)
            return friendScoreRankFragment
        }
    }

    val parentViewModel by viewModels<FriendScoreRankAvtivityViewModel>(ownerProducer = {
        requireActivity()
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = arguments?.getInt(PARAMS_RANK_TYPE)?:0
        mViewModel?.myRank?.observe(viewLifecycleOwner, Observer {
            val value = parentViewModel.myRankList.value
            value?.set(type,it)
            parentViewModel.myRankList.value = value
        })
    }
    override fun initAdapter(): BaseQuickAdapter<FriendRank, BaseViewHolder>? {
        mViewModel?.type = arguments?.getInt(PARAMS_RANK_TYPE)?:0
        return mViewModel?.getPageData()?.value?.let {
            FriendScoreAdapter(it)
        }
    }
}