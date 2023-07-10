package com.mooc.my.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.ShowDialogUtils
import com.mooc.my.R
import com.mooc.my.adapter.StudyFriendCircleAdapter
import com.mooc.my.model.SchoolSourceBean
import com.mooc.my.viewmodel.SchoolCircleViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject


/**
 *学友圈fragment
 * @author limeng
 * @date 2021/4/9
 */
class SchoolCircleFragment : BaseListFragment<SchoolSourceBean, SchoolCircleViewModel>() {
    var curentSourceBean: SchoolSourceBean? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()

        EventBus.getDefault().register(this)
    }

    private fun initListener() {
        (mViewModel as SchoolCircleViewModel).mDeleteStatusBean.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null) {
                    if (it.message != null) {
                        toast(it.message!!)
                    }
                    if (!it.code.isNullOrEmpty()) {
                        val code: Int = it.code!!.toInt()

                        when (code) {
                            1 -> {
                                curentSourceBean?.let {
                                    mAdapter?.data?.remove(curentSourceBean!!)
                                }
                                mAdapter?.notifyDataSetChanged()
                            }
                        }
                    }

                }
            })
        //关注或者取消关注
        (mViewModel as SchoolCircleViewModel).mParserStatusBean.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null) {
                    if (it.message != null) {
                        toast(it.message!!)
                    }

                    if (!it.code.isNullOrEmpty()) {
                        var code: Int = it.code!!.toInt()
                        when (code) {
                            2 -> {//点赞成功
                                curentSourceBean?.is_like = true
                                if (curentSourceBean?.like_count != null) {
                                    curentSourceBean?.like_count =
                                        curentSourceBean?.like_count!! + 1
                                    mAdapter?.notifyDataSetChanged()
                                }
                            }
                            3 -> {
                                curentSourceBean?.is_like = false
                                if (curentSourceBean?.like_count != null) {
                                    curentSourceBean?.like_count =
                                        curentSourceBean?.like_count!! - 1
                                    mAdapter?.notifyDataSetChanged()
                                }
                            }

                        }

                    }
                }
            })
    }

    override fun initAdapter(): BaseQuickAdapter<SchoolSourceBean, BaseViewHolder>? {
        return (mViewModel as SchoolCircleViewModel).getPageData().value?.let {
            val adapter = StudyFriendCircleAdapter(it)
            adapter.addChildClickViewIds(R.id.ibDelete, R.id.tvPriseNum, R.id.ivHead)
            adapter.setOnItemClickListener { adapter, view, position ->
                val bean: SchoolSourceBean = adapter.data[position] as SchoolSourceBean
                if (bean.resource_type == ResourceTypeConstans.TYPE_COURSE
                    && ResourceTypeConstans.allCourseDialogId.contains(bean.resource_id)
                ) {//工信部点击不进入课程详情页面，需要弹框的课程id
                    activity?.let { it1 -> ShowDialogUtils.showDialog(it1) }
                } else {
                    ResourceTurnManager.turnToResourcePage(bean)
                }
            }
            adapter.setOnItemChildClickListener { adapter, view, position ->
                curentSourceBean = adapter.data.get(position) as SchoolSourceBean?
                when (view.id) {
                    R.id.ibDelete -> {
                        delUserResourceDialog()

                    }
                    R.id.tvPriseNum -> {
                        prise()

                    }
                    R.id.ivHead -> {
                        ARouter.getInstance()
                            .build(Paths.PAGE_USER_INFO)
                            .withString(IntentParamsConstants.MY_USER_ID, curentSourceBean?.user)
                            .navigation()
                    }
                    else -> {
                    }
                }
            }
            adapter
        }

    }

    /**
     * 点赞
     */
    private fun prise() {
        val requestData = JSONObject()
        val status = if (curentSourceBean?.is_like == true) 0 else 1

        try {
            requestData.put("share_id", curentSourceBean?.id)
            requestData.put("share_status", status.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val stringBody =
            requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        mViewModel?.postLikeSchoolResource(stringBody)
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
                .asCustom(
                    PublicDialog(context, publicDialogBean) {
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
                    }
                )
                .show()
        }

    }

    /***
     * 笔记详情页点击了删除
     * 刷新一下接口
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResourceChangeEvent(resourceChange: RefreshStudyRoomEvent) {
        if (resourceChange.resourceType == ResourceTypeConstans.TYPE_NOTE) {
            loadDataWithRrefresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        EventBus.getDefault().unregister(this)
    }
}