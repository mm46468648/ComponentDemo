package com.mooc.discover.fragment

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.adapter.MutualTaskMustAdapter
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.model.Choice
import com.mooc.discover.model.TaskDetailsBean
import com.mooc.discover.ui.TaskDetailsActivity
import com.mooc.discover.viewmodel.MutualTaskViewModle
import com.mooc.resource.ktextention.dp2px

/**
 * 任务详情页
 * 互斥任务中必选任务列表
 */
class MutualTaskMustListFragment : BaseListFragment2<TaskDetailsBean,MutualTaskViewModle>(){

    companion object{
//        const val MUTUAL_TAKS_TYPE = "mutual_taks_type"     //互斥任务必做类型
//        const val MUTUAL_TYPE_MUST = 0      //互斥任务必做类型
//        const val MUTUAL_TYPE_CHOOSE = 1    //互斥任务选做类型

        fun getInstance(choice:Choice) : MutualTaskMustListFragment{
            val mutualTaskListFragment = MutualTaskMustListFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(TaskConstants.INTENT_MUTUAL_TAKS_LIST,choice.necessary)
            mutualTaskListFragment.arguments = bundle
            return mutualTaskListFragment
        }
    }

    override fun initAdapter(): BaseQuickAdapter<TaskDetailsBean, BaseViewHolder>? {

        mViewModel?.taskList = arguments?.getParcelableArrayList<TaskDetailsBean>(TaskConstants.INTENT_MUTUAL_TAKS_LIST)

        return mViewModel?.getPageData()?.value?.let {
            val mutualTaskMustAdapter = MutualTaskMustAdapter(it)
            val str = "完成以下任务"
            mutualTaskMustAdapter.setHeaderView(createHeadView(str))
            mutualTaskMustAdapter.setOnItemClickListener { adapter, view, position ->
                val bean = adapter.data[position] as TaskDetailsBean
                //跳转任务详情
                ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS)
                    .withString(IntentParamsConstants.PARAMS_TASK_ID, bean.id)
                    .withBoolean(TaskDetailsActivity.FROM_MUTUAL_CHILD_TASK,true)
                    .navigation()
            }
            mutualTaskMustAdapter
        }
    }

    fun createHeadView(text:String) : TextView{
        val textView = TextView(requireContext())
        textView.text = text
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        textView.setPadding(25.dp2px(), 10.dp2px(),0, 0)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        return textView
    }

    override fun needPullToRefresh(): Boolean {
        return false
    }
}