package com.mooc.battle.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.battle.R
import com.mooc.battle.model.RankDeatilsBean
import com.mooc.battle.ui.adapter.GameRankListAdapter
import com.mooc.battle.viewModel.GameRankListViewModel
import com.mooc.commonbusiness.base.BaseListFragment

/**

 * @Author limeng
 * @Date 2022/12/28-10:21 上午
 */
class GameRankListFragment :
    BaseListFragment<RankDeatilsBean.RankListBean, GameRankListViewModel>() {
    var tournament_id: String? = null

    //改变背景颜色
    fun changeBack(backRes: Int) {
        flRoot.setBackgroundResource(backRes)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flRoot.setBackgroundResource(R.drawable.shape_rank_bottom)
        mViewModel?.getPageData()?.observe(viewLifecycleOwner, Observer {
            if (it.size > 0 && mViewModel?.getRankOffset() != null && mViewModel?.getRankOffset()!! - (it.size) == 0) {
                if (it.size >= 3) {
                    it.removeAt(0)
                    it.removeAt(0)
                    it.removeAt(0)
                } else if (it.size == 2) {
                    it.removeAt(0)
                    it.removeAt(0)
                } else if (it.size == 1) {
                    it.removeAt(0)
                }
                mAdapter?.notifyDataSetChanged()
//                mAdapter?.setNewInstance(it)
            }

        })
    }

    override fun initAdapter(): BaseQuickAdapter<RankDeatilsBean.RankListBean, BaseViewHolder>? {
        mViewModel?.tournament_id = tournament_id
        return (mViewModel as GameRankListViewModel).getPageData()?.value?.let {
            val mAdapter = GameRankListAdapter(it)
//            mAdapter.setOnItemClickListener { adapter, view, position ->
//
//            }
            mAdapter
        }
    }
}