package com.mooc.studyroom.ui.fragment.mymsg

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.studyroom.R
import com.mooc.studyroom.model.CourseMsgDetailBean
import com.mooc.studyroom.ui.adapter.CourseMsgDetailAdapter
import com.mooc.studyroom.viewmodel.CourseMsgDetailsViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *课程消息详情
 * @author limeng
 * @date 2021/3/11
 */
class CourseMsgDetailsFragment : BaseListFragment<CourseMsgDetailBean, CourseMsgDetailsViewModel>() {
    private var param1: String? = null
    private var param2: String? = null
    var courseMsgDetailBean: CourseMsgDetailBean? = null

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
        inintDataListener()
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CourseMsgDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun initAdapter(): BaseQuickAdapter<CourseMsgDetailBean, BaseViewHolder>? {
        mViewModel?.course_id = param1
        return (mViewModel as CourseMsgDetailsViewModel).getPageData()?.value?.let {
            val adapter = CourseMsgDetailAdapter(it)
            adapter.setOnItemLongClickListener { adapter, view, position ->
                courseMsgDetailBean = adapter.data.get(position) as CourseMsgDetailBean
                if (courseMsgDetailBean != null) {
                    deleteDialog(courseMsgDetailBean!!)
                }
                return@setOnItemLongClickListener true
            }

            adapter
        }
    }

    private fun deleteDialog(data: CourseMsgDetailBean) {
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

    private fun delMessage(data: CourseMsgDetailBean) {
        mViewModel?.deleteMessage(data.id.toString())
    }

    private fun inintDataListener() {
        //删除数据的回调监听
        mViewModel?.deleteRespose?.observe(viewLifecycleOwner, Observer {
            if (it.status == 200) {
                val adapter = mAdapter as CourseMsgDetailAdapter
                courseMsgDetailBean?.let { it1 -> adapter.remove(it1) }
                adapter.notifyDataSetChanged()
            }
        })
    }
}