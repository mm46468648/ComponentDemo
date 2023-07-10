package com.mooc.studyroom.ui.fragment.mymsg

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.R
import com.mooc.studyroom.model.SystemMsgBean
import com.mooc.studyroom.ui.adapter.SystemMsgAdapter
import com.mooc.studyroom.viewmodel.SystemMsgViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *系统公告消息列表
 * @author limeng
 * @date 2021/3/4
 */
class SystemMsgFragment : BaseListFragment<SystemMsgBean, SystemMsgViewModel>() {
    private var param1: String? = null
    private var param2: String? = null
    var systemMsgBean: SystemMsgBean? = null

    override fun initAdapter(): BaseQuickAdapter<SystemMsgBean, BaseViewHolder>? {
        return (mViewModel as SystemMsgViewModel).getPageData()?.value?.let {
            val adapter = SystemMsgAdapter(it)
            // 长按删除功能
            adapter.setOnItemLongClickListener { adapter, view, position ->
                systemMsgBean = adapter.data.get(position) as SystemMsgBean
                if (systemMsgBean != null) {
                    deleteDialog(systemMsgBean!!)

                }
                return@setOnItemLongClickListener true
            }
            adapter.addChildClickViewIds(R.id.tvResource)
            adapter.setOnItemChildClickListener { adapter, view, position ->
                val msgBean = adapter.data.get(position) as SystemMsgBean
                val type = msgBean.others?.type
                if (type.isNullOrEmpty()) {// 链接 直接跳到网页？？
                    ARouter.getInstance()
                            .build(Paths.PAGE_WEB)
                            .withString(IntentParamsConstants.WEB_PARAMS_URL, msgBean.others?.link)
                            .withString(IntentParamsConstants.WEB_PARAMS_TITLE, msgBean.others?.resource_title)
                            .navigation()
                }
                // 跳转资源
                msgBean.others?.let { it1 -> ResourceTurnManager.turnToResourcePage(it1) }

            }
            adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.resources?.getColor(R.color.color_0A0)?.let { flRoot.setBackgroundColor(it) }
        initDataListener()
    }

    private fun initDataListener() {
        //删除数据的回调监听
        mViewModel?.deleteRespose?.observe(viewLifecycleOwner, {
            if (it.status == 202) {
                toast("删除成功")
                val adapter = mAdapter as SystemMsgAdapter
                systemMsgBean?.let { it1 -> adapter.remove(it1) }
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun deleteDialog(data: SystemMsgBean) {
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

    private fun delMessage(data: SystemMsgBean) {
        mViewModel?.deleteMessage(data.id.toString())
    }

}