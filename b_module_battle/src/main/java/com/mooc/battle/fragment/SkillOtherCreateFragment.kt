package com.mooc.battle.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.battle.R
import com.mooc.battle.adapter.SkillMyCreateAdapter
import com.mooc.battle.adapter.SkillOtherCreateAdapter
import com.mooc.battle.model.SkillInfo
import com.mooc.battle.viewModel.SkillMyCreateViewModel
import com.mooc.battle.viewModel.SkillOtherCreateViewModel
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths

/**
 * 我发起的比武列表
 */
class SkillOtherCreateFragment : BaseListFragment2<SkillInfo, SkillOtherCreateViewModel>(){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flRoot.setBackgroundColor(Color.TRANSPARENT)
    }
    override fun initAdapter(): BaseQuickAdapter<SkillInfo, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let {
            val skillOtherCreateAdapter = SkillOtherCreateAdapter(it)
            skillOtherCreateAdapter.setOnItemClickListener { adapter, view, position ->
                //如果没参与过的话,点击参与比武
                val skillInfo = adapter.data[position] as SkillInfo
                if (skillInfo.is_enroll == 0) {
                    ARouter.getInstance().build(Paths.PAGE_BEGIN_SKILL)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, skillInfo.id)
                        .navigation()
                }
            }
            skillOtherCreateAdapter.addChildClickViewIds(R.id.tvRank,R.id.tvLook)
            skillOtherCreateAdapter.setOnItemChildClickListener { adapter, view, position ->
                val skillInfo = adapter.data[position] as SkillInfo
                if(view.id == R.id.tvRank){
                    //跳转到排行榜页面
                    ARouter.getInstance().build(Paths.PAGE_SKILL_RANK)
                        .withString(IntentParamsConstants.TOURNAMENT_ID, skillInfo.id)
                        .navigation()
                }else if(view.id == R.id.tvLook){
                    //跳转到查看成绩页面
                    ARouter.getInstance().build(Paths.PAGE_SKILL_RESULT)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID,skillInfo.id)
                        .navigation()
                }
            }
            skillOtherCreateAdapter
        }
    }

    override fun initEmptyView() {
        emptyView.setEmptyIcon(R.mipmap.ic_skill_being_empty)
        emptyView.setTitle("当前没有正在进行的比武")
    }
}