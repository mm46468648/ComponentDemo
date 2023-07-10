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
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.R
import com.mooc.studyroom.model.CourseMsgBean
import com.mooc.studyroom.ui.adapter.CourseMsgAdapter
import com.mooc.studyroom.viewmodel.CourseMsgViewModel

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *课程公告消息列表
 * @author limeng
 * @date 2021/3/4
 */
class CourseMsgFragment : BaseListFragment<CourseMsgBean, CourseMsgViewModel>() {
    private var param1: String? = null
    private var param2: String? = null
    var courseMsgBean: CourseMsgBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.initData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataListener()
    }

    private fun initDataListener() {
        //删除数据的回调监听
        mViewModel?.deleteRespose?.observe(viewLifecycleOwner, Observer {
            if (it.status == 202) {
                toast("删除成功")
                val adapter = mAdapter as CourseMsgAdapter
                courseMsgBean?.let { it1 -> adapter.remove(it1) }
                adapter.notifyDataSetChanged()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CourseMsgFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun initAdapter(): BaseQuickAdapter<CourseMsgBean, BaseViewHolder>? {
        return (mViewModel as CourseMsgViewModel).getPageData()?.value?.let {
            val adapter = CourseMsgAdapter(it)
            //  删除
            adapter.setOnItemLongClickListener { adapter, view, position ->
                courseMsgBean = adapter.data.get(position) as CourseMsgBean
                if (courseMsgBean != null) {
                    deleteDialog(courseMsgBean!!)
                }
                return@setOnItemLongClickListener true
            }
            //  跳转到课程详情页面 CourseMsgDetailActivity
            adapter.setOnItemClickListener { adapter, view, position ->
                val bean = adapter.data.get(position) as CourseMsgBean
                ARouter.getInstance()
                        .build(Paths.PAGE_COURSE_MSG_DETAIL)
                        .withString(IntentParamsConstants.COURSE_PARAMS_ID, bean.sender_id.toString())
                        .withString(IntentParamsConstants.COURSE_PARAMS_TITLE, bean.title)
                        .navigation()

            }
            adapter
        }
    }

    private fun deleteDialog(data: CourseMsgBean) {

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

    private fun delMessage(data: CourseMsgBean) {
        mViewModel?.deleteMessage(data.id.toString())
    }
}