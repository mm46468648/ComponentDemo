package com.mooc.studyroom.ui.fragment.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.utils.ShowDialogUtils
import com.mooc.studyroom.model.StudyRecordBean
import com.mooc.studyroom.ui.adapter.StudyRecordAdapter
import com.mooc.studyroom.viewmodel.StudyRecordActivityViewModel
import com.mooc.studyroom.viewmodel.StudyRecordViewModel

/**
 * 学习历史
 */
class StudyHistoryFragment : BaseListFragment<StudyRecordBean, StudyRecordViewModel>() {

    private val parentViewModel by viewModels<StudyRecordActivityViewModel>(ownerProducer = {
        requireActivity()
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //监听学习记录删除
        parentViewModel.onDeleteSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {   //删除成功，清空学习记录
                mViewModel?.cleatStudyRecordData()
                mAdapter?.notifyDataSetChanged()
            }
        })
//        LogUtil.addLoadLog(LogPageConstants.PID_HISTORY)
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun initAdapter(): BaseQuickAdapter<StudyRecordBean, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let {
            val studyRecordAdapter = StudyRecordAdapter(it)
            studyRecordAdapter.setOnItemClickListener { _, _, position ->
                val studyRecordBean = it[position]
                if (studyRecordBean.type == ResourceTypeConstans.TYPE_COURSE
                    && ResourceTypeConstans.allCourseDialogId.contains(studyRecordBean.resource_id.toString())
                ) {//工信部点击不进入课程详情页面，需要弹框的课程id
                    activity?.let { it1 -> ShowDialogUtils.showDialog(it1) }
                } else {
                    ResourceTurnManager.turnToResourcePage(studyRecordBean)
                }
            }
            studyRecordAdapter
        }
    }
}