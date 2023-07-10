package com.mooc.my.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.adapter.UserLearningAdapter
import com.mooc.my.constants.MyInFoConstants
import com.mooc.my.model.LearingListBean
import com.mooc.my.viewmodel.LearingListViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [UserLearningListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserLearningListFragment : BaseListFragment<LearingListBean, LearingListViewModel>() {

    val userId by extraDelegate(MyInFoConstants.USER_PARAMS_ID, "")


    companion object {
        fun getInstence(userId: String): UserLearningListFragment {
            val userLearningListFragment = UserLearningListFragment()
            userLearningListFragment.arguments = Bundle().put(MyInFoConstants.USER_PARAMS_ID, userId)
            return userLearningListFragment
        }
    }

    override fun initAdapter(): BaseQuickAdapter<LearingListBean, BaseViewHolder>? {

        val model = mViewModel as LearingListViewModel
        if (userId != GlobalsUserManager.userInfo?.user_id ?: "") {
            model.userId = userId
        }
        return model.getPageData().value?.let {
            val adapter = UserLearningAdapter(it)
            adapter.setOnItemClickListener { adapter, view, position ->
                ARouter.getInstance().build(Paths.PAGE_PUBLIC_STUDY_LIST)
                        .withString(IntentParamsConstants.STUDYROOM_FROM_FOLDER_ID, it[position].folder_id.toString())
                        .withString(IntentParamsConstants.STUDYROOM_FOLDER_ID, it[position].id.toString())
                        .withString(IntentParamsConstants.STUDY_ROOM_CHILD_FOLDER_ID, it[position].folder_id.toString())
                        .withString(IntentParamsConstants.STUDYROOM_FOLDER_NAME, it[position].name.toString())
                        .withString(IntentParamsConstants.MY_USER_ID, userId)
                        .navigation()
            }
            adapter
        }


    }

    override fun initEmptyView() {
        emptyView.setGravityTop(40.dp2px())
    }

}