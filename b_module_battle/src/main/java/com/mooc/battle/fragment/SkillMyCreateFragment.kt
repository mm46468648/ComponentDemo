package com.mooc.battle.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.battle.R
import com.mooc.battle.adapter.SkillMyCreateAdapter
import com.mooc.battle.model.SkillInfo
import com.mooc.battle.viewModel.SkillMyCreateViewModel
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths

/**
 * 我发起的比武列表
 */
class SkillMyCreateFragment : BaseListFragment2<SkillInfo,SkillMyCreateViewModel>(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flRoot.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun initAdapter(): BaseQuickAdapter<SkillInfo, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let {
            val skillMyCreateAdapter = SkillMyCreateAdapter(it)

//            skillMyCreateAdapter.setOnItemClickListener { adapter, view, position ->
//                //跳转到查看成绩页面
//                ARouter.getInstance().build(Paths.PAGE_SKILL_RESULT).navigation()
//            }
            skillMyCreateAdapter.addChildClickViewIds(R.id.tvRank)
            skillMyCreateAdapter.setOnItemChildClickListener { adapter, view, position ->
                //跳转到排行榜页面
                val skillInfo = adapter.data[position] as SkillInfo
                ARouter.getInstance().build(Paths.PAGE_SKILL_RANK)
                    .withString(IntentParamsConstants.TOURNAMENT_ID, skillInfo.id)
                    .navigation()

            }
            skillMyCreateAdapter
        }
    }

    override fun initEmptyView() {
        emptyView.setEmptyIcon(R.mipmap.ic_skill_my_create_empty)
        emptyView.setTitle("当前没有已发起的比武")
    }
}