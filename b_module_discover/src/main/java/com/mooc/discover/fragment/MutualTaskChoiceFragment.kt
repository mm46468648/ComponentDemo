package com.mooc.discover.fragment

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.discover.R
import com.mooc.discover.adapter.MutualTaskChoiceAdapter
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.model.TaskDetailsBean
import com.mooc.discover.viewmodel.MutualTaskChooseViewModle
import com.mooc.discover.viewmodel.TaskMutualChoiceViewModel
import com.mooc.resource.ktextention.dp2px

/**
 * 互斥任务选择Fragment
 */
class MutualTaskChoiceFragment : BaseListFragment2<TaskDetailsBean,MutualTaskChooseViewModle>(){

    val mViewModle : TaskMutualChoiceViewModel by viewModels<TaskMutualChoiceViewModel>(ownerProducer = {
        requireActivity()
    })

    companion object{
        fun getInstance(taskList : ArrayList<TaskDetailsBean>,title:String,tabId:String = "") : MutualTaskChoiceFragment{
            val mutualTaskListFragment = MutualTaskChoiceFragment()

            val bundle = Bundle()
            bundle.putParcelableArrayList(TaskConstants.INTENT_MUTUAL_TAKS_LIST,taskList)
            bundle.putString(TaskConstants.INTENT_MUTUAL_TAKS_TITLE,title)
            bundle.putString(TaskConstants.INTENT_MUTUAL_TAKS_TAB_ID,tabId)
            mutualTaskListFragment.arguments =bundle
            return mutualTaskListFragment
        }
    }

    override fun initAdapter(): BaseQuickAdapter<TaskDetailsBean, BaseViewHolder>? {


        val taskList = arguments?.getParcelableArrayList<TaskDetailsBean>(TaskConstants.INTENT_MUTUAL_TAKS_LIST)
        val title = arguments?.getString(TaskConstants.INTENT_MUTUAL_TAKS_TITLE,"")
        val tabId = arguments?.getString(TaskConstants.INTENT_MUTUAL_TAKS_TAB_ID,"") ?: ""

        if(title == TaskConstants.STR_MUST_TASK){
            val taskDetailsBean = TaskDetailsBean()
            taskDetailsBean.children_list = taskList
            mViewModel?.taskList = arrayListOf(taskDetailsBean)
        }else{
            mViewModel?.taskList = taskList
        }

        return mViewModel?.getPageData()?.value?.let {
            val mutualTaskMustAdapter = MutualTaskChoiceAdapter(it,title?:"")
            val str = if(title == TaskConstants.STR_MUST_TASK) "完成以下任务" else "在以下任务中任意选择一组完成"
            mutualTaskMustAdapter.setHeaderView(createHeadView(str))

            mutualTaskMustAdapter.onItemClick = { position ->
                val taskDetailsBean = it[position]
                updateSelectData(tabId,taskDetailsBean.id)
                mutualTaskMustAdapter.selectPosition = position
            }

            mutualTaskMustAdapter
        }
    }

    /**
     * 更新选择数据
     */
    fun updateSelectData(tabId:String,taskId:String){
        mViewModle.updateChoice(tabId,taskId)
    }

    fun createHeadView(text:String) : TextView{
        val textView = TextView(requireContext())
        textView.text = text
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        textView.setPadding(25.dp2px(), 10.dp2px(),0, 10.dp2px())
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        return textView
    }
}