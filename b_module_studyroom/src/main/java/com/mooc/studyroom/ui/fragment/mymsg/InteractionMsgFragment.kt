package com.mooc.studyroom.ui.fragment.mymsg

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
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.R
import com.mooc.studyroom.model.InteractionMsgBean
import com.mooc.studyroom.ui.adapter.InteractionMsgAdapter
import com.mooc.studyroom.viewmodel.InteractionMsgViewModel

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 *互动消息
 * @author limeng
 * @date 2021/3/4
 */
class InteractionMsgFragment : BaseListFragment<InteractionMsgBean, InteractionMsgViewModel>() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.resources?.getColor(R.color.color_0A0)?.let { flRoot.setBackgroundColor(it) }
        initDataListener()
    }

    private fun initDataListener() {
        //删除数据的回调监听
        mViewModel?.deleteRespose?.observe(viewLifecycleOwner, Observer {
            if (it.status == 200) {
                toast("删除成功")
                val adapter = mAdapter as InteractionMsgAdapter
                interactionMsgBean?.let { it1 -> adapter.remove(it1) }
                adapter.notifyDataSetChanged()
            }
        })
    }

    var interactionMsgBean: InteractionMsgBean? = null

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                InteractionMsgFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun initAdapter(): BaseQuickAdapter<InteractionMsgBean, BaseViewHolder>? {
        return (mViewModel as InteractionMsgViewModel).getPageData()?.value?.let {
            val adapter = InteractionMsgAdapter(it)
            // 长按删除功能
            adapter.setOnItemLongClickListener { adapter, view, position ->
                interactionMsgBean = adapter.data.get(position) as InteractionMsgBean
                if (interactionMsgBean != null) {
                    deleteDialog(interactionMsgBean!!)

                }
                return@setOnItemLongClickListener true
            }
            //  进入评论列表
            adapter.setOnItemClickListener { adapter, view, position ->
                val bean = adapter.data.get(position) as InteractionMsgBean
                when (bean.like_type) {
                    1 -> {
                        if (bean.like_type > 0) {
                            ARouter.getInstance().build(Paths.PAGE_COMMENTLIST)
                                    .withString(IntentParamsConstants.INTENT_PLAN_DYNAMIC_ID, bean.activity_id)
                                    .withBoolean(IntentParamsConstants.INTENT_PLAN_DYNAMIC_IS_INITIATOR, bean.is_start_user)
                                    .withBoolean(IntentParamsConstants.INTENT_IS_CAN_COMMENT, true)
                                    .navigation()
                        } else {
                            toast(resources.getString(R.string.text_str_dynamic_is_del))
                        }
                    }
                    2, 3 -> {
                        if (bean.like_type > 0) {
                            ARouter.getInstance().build(Paths.PAGE_COMMENTLIST)
                                    .withString(IntentParamsConstants.INTENT_PLAN_DYNAMIC_ID, bean.activity_id)
                                    .withBoolean(IntentParamsConstants.INTENT_PLAN_DYNAMIC_IS_INITIATOR, bean.is_start_user)
                                    .withInt(IntentParamsConstants.INTENT_COMMENT_ID, bean.message_index_id)
                                    .withBoolean(IntentParamsConstants.INTENT_IS_CAN_COMMENT, true)
                                    .navigation()
                        } else {
                            toast(resources.getString(R.string.text_str_dynamic_is_del))
                        }
                    }
                    8 -> {//进入个人信息页面
                        ARouter.getInstance()
                                .build(Paths.PAGE_USER_INFO)
                                .withString(IntentParamsConstants.MY_USER_ID, GlobalsUserManager.uid)
                                .navigation()
                    }
                    else -> {
                    }
                }

            }
            adapter
        }
    }

    private fun deleteDialog(data: InteractionMsgBean) {
        context?.let { context ->
            val publicDialogBean = PublicDialogBean()
            publicDialogBean.strMsg = resources.getString(R.string.del_message)
            publicDialogBean.strLeft = resources.getString(R.string.text_cancel)
            publicDialogBean.strRight = resources.getString(R.string.text_ok)
            publicDialogBean.isLeftGreen = 0
            XPopup.Builder(context)
                    .asCustom(PublicDialog(context, publicDialogBean) {
                        if (it == 1) {
                            delMessage(data)
                        }
                    })
                    .show()
        }
    }

    private fun delMessage(data: InteractionMsgBean) {
        mViewModel?.deleteMessage(data.id.toString())
    }

}