package com.mooc.studyroom.ui.fragment.contribute

import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.model.ContributionTaskBean
import com.mooc.studyroom.ui.adapter.ContributionTasksAdapter
import com.mooc.studyroom.viewmodel.ContributeTaskViewModel

class ContributeTaskFragment : BaseListFragment<ContributionTaskBean, ContributeTaskViewModel>() {

    companion object {
//        const val taskCheckInStr = "签到打卡"
//        const val taskStudyStr = "坚持每天学习"
//        const val taskPublishDynamicStr = "发布优质动态并被平台置顶"
//        const val taskReadBookStr = "请朋友读好书"
//        const val taskParticipationStr = "参与平台治理"

        const val taskCheckInStr = "J1"
        const val taskStudyStr = "J2"
        const val taskPublishDynamicStr = "C1"
        const val taskReadBookStr = "C2"
        const val taskParticipationStr = "C3"
    }
    override fun initAdapter(): BaseQuickAdapter<ContributionTaskBean, BaseViewHolder>? {
        return (mViewModel as ContributeTaskViewModel).getPageData()?.value?.let {
            val contributionTasksAdapter = ContributionTasksAdapter(it)
            contributionTasksAdapter.setOnItemClickListener { _, _, position ->
                goTask(it[position])
            }
            contributionTasksAdapter
        }
    }

    override fun neadLoadMore(): Boolean {
        return false
    }


    /**
     * 点击去做任务
     */
    private fun goTask(contributionTaskBean: ContributionTaskBean) {
        when(contributionTaskBean.short_name){
            taskCheckInStr ->{ //签到页面
                ARouter.getInstance().build(Paths.PAGE_CHECKIN).navigation()
            }
            taskStudyStr ->{ //去学习，跳转home发现页
                ARouter.getInstance().build(Paths.PAGE_HOME)
                        .withInt(IntentParamsConstants.HOME_SELECT_POSITION,0)
                        .withInt(IntentParamsConstants.HOME_SELECT_CHILD_POSITION,0)
                        .navigation()
            }
            taskPublishDynamicStr ->{//发动态
                ARouter.getInstance().build(Paths.PAGE_STUDY_RECORD)
                        .withBoolean("selectStudyProject",true)
                        .navigation()
            }
            taskReadBookStr ->{
                ARouter.getInstance().build(Paths.PAGE_INVITE_READ_BOOK).navigation()
            }
            taskParticipationStr ->{ //参与平台治理
                ARouter.getInstance().build(Paths.PAGE_UNDERSTAND_CONTRIBUTION).withString(IntentParamsConstants.HOME_UNDERSTAND_TYPE, "1").navigation()
            }
            else->{ //根据资源id和资源类型去跳转
//                val dataBean = RecommendContentBean.DataBean()
//                dataBean.resource_id = contributionTaskBean.resource_id
//                dataBean.id = contributionTaskBean.resource_id.toInt()
//                dataBean.type = contributionTaskBean.resource_type
//                if(contributionTaskBean.resource_type == ConstantUtils.TYPE_RECOMMEND_OUT_LINK){
//                    dataBean.link = contributionTaskBean.link
//                }else{
//                    dataBean.link = contributionTaskBean.resource_url
//                }
//                dataBean.other_type = 1   //为了电子书能跳转到掌阅阅读页面
//                Utils.toPageWithTypeId(requireContext(),dataBean,contributionTaskBean.resource_type,false)

                ResourceTurnManager.turnToResourcePage(contributionTaskBean)
            }
        }
    }
}