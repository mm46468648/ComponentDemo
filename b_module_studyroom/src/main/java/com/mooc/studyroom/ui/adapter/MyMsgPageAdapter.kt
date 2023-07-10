package com.mooc.studyroom.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mooc.resource.widget.SimpleTabLayout
import com.mooc.studyroom.model.MyMsgBean
//import com.mooc.commonbusiness.model.studyroom.MyMsgBean
import com.mooc.studyroom.ui.fragment.mymsg.CourseMsgFragment
import com.mooc.studyroom.ui.fragment.mymsg.InteractionMsgFragment
import com.mooc.studyroom.ui.fragment.mymsg.SystemMsgFragment

/**
 *
 */
class MyMsgPageAdapter(list:ArrayList<MyMsgBean>, activity: FragmentActivity) : FragmentStateAdapter(activity) {
//    private val fragments: SparseArray<Fragment> = SparseArray()
    private var fragmentsList=ArrayList<Fragment>()
    companion object {
        const val PAGE_SYSTEM = 0
        const val PAGE_COURSE = 1
        const val PAGE_INTERACTION = 2
    }

    init {
        list.forEach {
            if (it.message_type == 0) {
                fragmentsList.add(SystemMsgFragment())
            } else if (it.message_type ==  6) {
                fragmentsList.add(CourseMsgFragment())
            } else {
                fragmentsList.add(InteractionMsgFragment())
            }
        }


    }


    override fun createFragment(position: Int): Fragment {
        return fragmentsList[position]
    }

    override fun getItemCount(): Int {
        return fragmentsList.size
    }

}