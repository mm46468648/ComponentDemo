package com.mooc.discover.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.commonbusiness.base.EmptyFragment
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.fragment.MutualTaskChooseListFragment
import com.mooc.discover.model.Choice
import com.mooc.discover.model.TaskDetailsBean

class TaskMutualViewPagerAdatper(
    var list: List<String>,
    var choice: Choice,
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    //    var type = 0
    override fun getItemCount(): Int {
        return list.size
    }

    var hastMust = false     //第一个是不是必做任务(因为有可能没有必做任务)

    init {
        if (list[0] == TaskConstants.STR_MUST_TASK) {
            hastMust = true
        }
    }

//    override fun createFragment(position: Int): Fragment {
//        val choicePosition = if (hastMust) position - 1 else position
//
//        return if (position == 0 && list[position] == TaskConstants.STR_MUST_TASK) {
//            MutualTaskMustListFragment.getInstance(choice)
//        } else if (choicePosition <= choice.choice?.size ?: 0 && choicePosition >= 0) {
//            val choiceTask = choice.choice?.get(choicePosition)
//            MutualTaskChooseListFragment.getInstance(choiceTask?.choice_list ?: arrayListOf())
//        } else {
//            EmptyFragment()
//        }
//    }

    override fun createFragment(position: Int): Fragment {
        val choicePosition = if (hastMust) position - 1 else position

        return if (position == 0 && list[position] == TaskConstants.STR_MUST_TASK) {
//            MutualTaskMustListFragment.getInstance(choice)
            val taskDetailsBean = TaskDetailsBean()
            taskDetailsBean.children_list = choice.necessary ?: arrayListOf()
            val childList = arrayListOf(taskDetailsBean)
            MutualTaskChooseListFragment.getInstance(childList,true)
            
        } else if (choicePosition <= choice.choice?.size ?: 0 && choicePosition >= 0) {
            val choiceTask = choice.choice?.get(choicePosition)
            MutualTaskChooseListFragment.getInstance(choiceTask?.choice_list ?: arrayListOf())
        } else {
            EmptyFragment()
        }
    }
}