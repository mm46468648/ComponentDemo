package com.mooc.discover.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.commonbusiness.base.EmptyFragment
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.fragment.MutualTaskChoiceFragment
import com.mooc.discover.model.Choice

class TaskMutualChoiceViewPagerAdatper(var list:List<String>, var choice : Choice, activity:FragmentActivity) : FragmentStateAdapter(activity){

    var hastMust = false     //第一个是不是必做任务(因为有可能没有必做任务)

    init {
        if (list[0] == TaskConstants.STR_MUST_TASK) {
            hastMust = true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {

        val choicePosition = if (hastMust) position - 1 else position

        return if (position == 0 && list[position] == TaskConstants.STR_MUST_TASK) {
            MutualTaskChoiceFragment.getInstance(choice.necessary?: arrayListOf(), TaskConstants.STR_MUST_TASK)
        } else if (choicePosition <= choice.choice?.size ?: 0 && choicePosition >= 0) {
            val choiceTask = choice.choice?.get(choicePosition)
            MutualTaskChoiceFragment.getInstance(choiceTask?.choice_list ?: arrayListOf(),choiceTask?.title?:"",choiceTask?.id?:"")
        } else {
            EmptyFragment()
        }
    }
}