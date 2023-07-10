package com.mooc.my.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.MY_USER_ID
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.R
import com.mooc.commonbusiness.adapter.UserFollowAdapter
import com.mooc.commonbusiness.model.FollowMemberBean
import com.mooc.my.viewmodel.FollowViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject


/**
关注和粉丝的具体页面内容
 * @author limeng
 * @date 2021/2/20
 */
class UserFollowFragment : BaseListFragment<FollowMemberBean, FollowViewModel>() {
    var type: Int = 0//0 粉丝 1 关注的人
    var user_id: String? = null
    var currentBean: FollowMemberBean? = null
    override fun initAdapter(): BaseQuickAdapter<FollowMemberBean, BaseViewHolder>? {
        val model = mViewModel as FollowViewModel
        model.type = type
        model.id = user_id
        return model.getPageData().value?.let {
            val adapter = UserFollowAdapter(it)
            adapter.addChildClickViewIds(R.id.tv_follow)
            adapter.setOnItemChildClickListener { adapter, view, position ->//关注和取消关注内容
                val bean = adapter.data.get(position) as FollowMemberBean
                followUserDialog(bean)
            }
            adapter.setOnItemClickListener { adapter, view, position ->//0 粉丝 1 关注的人
                //跳转到UserInfoActivity
                val bean = adapter.data.get(position) as FollowMemberBean

                ARouter.getInstance().build(Paths.PAGE_USER_INFO)
                        .withString(MY_USER_ID, bean.user_id)
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
        currentBean = bean;
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

        if(mViewModel!=null && mViewModel is FollowViewModel){
            (mViewModel as FollowViewModel).postFollowUser(stringBody)
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = mViewModel as FollowViewModel
        //关注或者取消关注的监听
        model.mFollowStatusBean.observe(viewLifecycleOwner, Observer {
            toast(it.message)
            if (!TextUtils.isEmpty(it.code)) {
                when (it.code) {
                    "2" -> {
                        currentBean?.state = 2
                        mAdapter?.notifyDataSetChanged()

                    }
                    "3" -> {
                        currentBean?.state = 0
                        mAdapter?.notifyDataSetChanged()
                    }
                    "6" -> {
                        currentBean?.state = 1
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }


}