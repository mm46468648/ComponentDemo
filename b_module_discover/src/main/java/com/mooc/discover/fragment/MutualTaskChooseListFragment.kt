package com.mooc.discover.fragment

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.discover.R
import com.mooc.discover.adapter.MutualTaskChooseAdapter
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.model.TaskDetailsBean
import com.mooc.discover.viewmodel.MutualTaskChooseViewModle
import com.mooc.resource.ktextention.dp2px

/**
 * 互斥任务中可选任务列表
 * 新版本兼容必选任务样式,必须任务也通过此Fragment加载
 */
class MutualTaskChooseListFragment : BaseListFragment2<TaskDetailsBean,MutualTaskChooseViewModle>(){

    companion object{
        fun getInstance(taskList : ArrayList<TaskDetailsBean>,mustTask:Boolean = false) : MutualTaskChooseListFragment{
            val mutualTaskListFragment = MutualTaskChooseListFragment()

            val bundle = Bundle()
            bundle.putParcelableArrayList(TaskConstants.INTENT_MUTUAL_TAKS_LIST,taskList)
            bundle.putBoolean(TaskConstants.STR_MUST_TASK,mustTask)
            mutualTaskListFragment.arguments =bundle
            return mutualTaskListFragment
        }
    }

    override fun initAdapter(): BaseQuickAdapter<TaskDetailsBean, BaseViewHolder>? {

        mViewModel?.taskList = arguments?.getParcelableArrayList<TaskDetailsBean>(TaskConstants.INTENT_MUTUAL_TAKS_LIST)
        val mustTask = arguments?.getBoolean(TaskConstants.STR_MUST_TASK,false)?:false
        return mViewModel?.getPageData()?.value?.let {
            val mutualTaskMustAdapter = MutualTaskChooseAdapter(it,mustTask)
            val str = if(mustTask)"完成以下任务" else "在以下任务中任意选择一组完成"
            mutualTaskMustAdapter.setHeaderView(createHeadView(str))

//            mutualTaskMustAdapter.onItemClick = { position->
//                val bean = it[position] as TaskDetailsBean
//                //跳转任务详情
//                ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS)
//                    .withString(IntentParamsConstants.PARAMS_TASK_ID, bean.id)
//                    .withBoolean(TaskDetailsActivity.FROM_MUTUAL_CHILD_TASK,true)
//                    .navigation()
//            }

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