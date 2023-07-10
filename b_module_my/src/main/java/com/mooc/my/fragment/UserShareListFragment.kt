package com.mooc.my.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.utils.ShowDialogUtils
import com.mooc.my.R
import com.mooc.my.adapter.UserShareInfoAdapter
import com.mooc.my.constants.MyInFoConstants
import com.mooc.my.model.SchoolSourceBean
import com.mooc.my.viewmodel.ShareListViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject


class UserShareListFragment : BaseListFragment<SchoolSourceBean, ShareListViewModel>() {

    val userId by extraDelegate(MyInFoConstants.USER_PARAMS_ID, "")


    var mUserShareInfoAdapter: UserShareInfoAdapter? = null
    var curentSourceBean: SchoolSourceBean? = null

    companion object {
        fun getInstence(userId: String): UserShareListFragment {
            val userShareListFragment = UserShareListFragment()
            userShareListFragment.arguments = Bundle().put(MyInFoConstants.USER_PARAMS_ID, userId)
            return userShareListFragment
        }
    }


    override fun initAdapter(): BaseQuickAdapter<SchoolSourceBean, BaseViewHolder>? {

        val model = mViewModel as ShareListViewModel
        model.userId = userId
        mUserShareInfoAdapter = model.getPageData()?.value?.let {
            UserShareInfoAdapter(it)
        }
        mUserShareInfoAdapter?.setOnItemClickListener { adapter, view, position ->
            curentSourceBean = mUserShareInfoAdapter?.data?.get(position)
            curentSourceBean?.let {
                if (it.resource_type == ResourceTypeConstans.TYPE_COURSE
                    && ResourceTypeConstans.allCourseDialogId.contains(it.resource_id)
                ) {//工信部点击不进入课程详情页面，需要弹框的课程id
                    activity?.let { it1 -> ShowDialogUtils.showDialog(it1) }
                } else {
                    ResourceTurnManager.turnToResourcePage(it)
                }
            }
        }
        initAdapterDataListerner()

        return mUserShareInfoAdapter

    }


    private fun initAdapterDataListerner() {
        mUserShareInfoAdapter?.addChildClickViewIds(R.id.llPrise, R.id.ibBottomDelete)
        mUserShareInfoAdapter?.setOnItemChildClickListener { adapter, view, position ->
            curentSourceBean = mUserShareInfoAdapter?.data?.get(position)
            if (view.id == R.id.llPrise) {//动态点赞
                val requestData = JSONObject()
                val status = if (curentSourceBean?.is_like == true) 0 else 1

                try {
                    requestData.put("share_id", curentSourceBean?.id)
                    requestData.put("share_status", status.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val stringBody = requestData.toString()
                    .toRequestBody("application/json;charset=utf-8".toMediaType())
                mViewModel?.postLikeSchoolResource(stringBody)

            } else if (view.id == R.id.ibBottomDelete) {//删除
                delUserResourceDialog()
            }
        }

        //动态点赞
        mViewModel?.mParserStatusBean?.observe(this, Observer {
            if (it != null) {
                if (it.message != null) {
                    toast(it.message!!)
                }
                if (!it.code.isNullOrEmpty() && curentSourceBean?.like_count != null) {
                    val code: Int = it.code!!.toInt()
                    when (code) {
                        2 -> {
                            curentSourceBean?.is_like = true
                            curentSourceBean?.like_count = (curentSourceBean?.like_count)!! + 1
                        }

                        3 -> {
                            curentSourceBean?.is_like = false
                            curentSourceBean?.like_count = (curentSourceBean?.like_count)!! - 1
                        }
                    }
                }
                mUserShareInfoAdapter?.notifyDataSetChanged()

            }
        })

        //删除学友圈分享
        mViewModel?.mDeleteStatusBean?.observe(this, Observer {
            if (it != null) {
                if (it.message != null) {
                    toast(it.message!!)
                }
                if (!it.code.isNullOrEmpty() && curentSourceBean?.like_count != null) {
                    var code: Int = 0
                    if (it.code != null) code = it.code!!.toInt()
                    when (code) {
                        1 -> {
                            curentSourceBean?.let {
                                mUserShareInfoAdapter?.data?.remove(curentSourceBean!!)
                            }
                            mUserShareInfoAdapter?.notifyDataSetChanged()
                        }
                    }
                }

            }
        })

    }

    /**
     * 删除学友圈分享
     */
    private fun delUserResourceDialog() {

        context?.let { context ->
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = resources.getString(R.string.text_is_del_resource_school)
            publicDialogBean.strLeft = resources.getString(R.string.text_cancel)
            publicDialogBean.strRight = resources.getString(R.string.text_ok)
            publicDialogBean.isLeftGreen = 0
            XPopup.Builder(context)
                .asCustom(PublicDialog(context, publicDialogBean) {
                    if (it == 1) {
                        val requestData = JSONObject()
                        try {
                            requestData.put("share_id", curentSourceBean?.id.toString())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        val stringBody = requestData.toString()
                            .toRequestBody("application/json;charset=utf-8".toMediaType())

                        mViewModel?.delSchoolResource(stringBody)
                    }
                })
                .show()
        }
    }


    override fun initEmptyView() {
        emptyView.setGravityTop(40.dp2px())
    }

}