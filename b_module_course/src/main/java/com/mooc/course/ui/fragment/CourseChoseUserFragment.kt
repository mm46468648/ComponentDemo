package com.mooc.course.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.adapter.UserFollowAdapter
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.course.R
import com.mooc.commonbusiness.model.FollowMemberBean
import com.mooc.course.viewmodel.CourseSubMemberViewModel

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject


/**
 *
 * @author limeng
 * @date 2022/5/5
 */
class CourseChoseUserFragment : BaseListFragment<FollowMemberBean, CourseSubMemberViewModel>() {
    var sub_users: String? = null// 从课程带过来的三个人ids
    var course_id: String? = null//课程id
    var currentBean: FollowMemberBean? = null
    override fun initAdapter(): BaseQuickAdapter<FollowMemberBean, BaseViewHolder>? {
        val model = mViewModel as CourseSubMemberViewModel
        model.course_id = course_id
        model.sub_users = sub_users
        return model.getPageData().value?.let {
            val adapter = UserFollowAdapter(it)
            adapter.addChildClickViewIds(R.id.tv_follow)
            adapter.setOnItemChildClickListener { adapter, view, position ->//关注 取消关注
                val bean = adapter.data.get(position) as FollowMemberBean
                followUserDialog(bean)
            }
            adapter.setOnItemClickListener { adapter, view, position ->
                //跳转到UserInfoActivity
                val bean = adapter.data.get(position) as FollowMemberBean

                ARouter.getInstance().build(Paths.PAGE_USER_INFO)
                        .withString(IntentParamsConstants.MY_USER_ID, bean.user_id)
                        .navigation()

            }
            adapter
        }
    }

    /**
     * 关注或者取消关注学友
     */
    private fun followUserDialog(bean: FollowMemberBean?) {
        context?.let { context ->
            val message = if (bean?.state == 0) {
                resources.getString(R.string.text_follow_user)
            } else {
                resources.getString(R.string.text_cancel_follow_user)
            }
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = message
            publicDialogBean.strLeft = resources.getString(R.string.text_cancel)
            publicDialogBean.strRight = resources.getString(R.string.text_ok)
            publicDialogBean.isLeftGreen = 0
            XPopup.Builder(context)
                    .asCustom(PublicDialog(context, publicDialogBean) {
                        if (it == 1) {
                            followUser(bean)
                        }
                    })
                    .show()
        }
    }

    private fun followUser(bean: FollowMemberBean?) {
        currentBean = bean
        val status: Int = if (bean?.state == 0) {
            1
        } else {
            0
        }

        val requestData = JSONObject()
        try {
            requestData.put("follow_user_id", bean?.user_id)
            requestData.put("follow_status", status)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        if (mViewModel != null && mViewModel is CourseSubMemberViewModel) {
            (mViewModel as CourseSubMemberViewModel).postFollowUser(stringBody)
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = mViewModel as CourseSubMemberViewModel
        //关注或者取消关注的监听
        model.mFollowStatusBean.observe(viewLifecycleOwner, Observer {
            toast(it.message)
            if (!TextUtils.isEmpty(it.code)) {
                when (it.code) {
                    "2" -> {
                        currentBean?.state = 2
                        mAdapter?.notifyDataSetChanged()
//                        updateFollowNum(true)

                    }
                    "3" -> {
                        currentBean?.state = 0
                        mAdapter?.notifyDataSetChanged()
//                        updateFollowNum(false)
                    }
                    "6" -> {
                        currentBean?.state = 1
                        mAdapter?.notifyDataSetChanged()
//                        updateFollowNum(true)
                    }
                }
            }
        })
    }


}