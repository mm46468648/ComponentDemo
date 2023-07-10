package com.mooc.studyroom.ui.fragment.record

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
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.R
import com.mooc.studyroom.model.StudyProject
import com.mooc.studyroom.ui.adapter.StudyProjectAdapter
import com.mooc.studyroom.viewmodel.StudyProjectViewModel
import com.mooc.studyroom.window.StudyPlanChekDialog

/**
 * 学习项目
 */
class StudyProjectFragment : BaseListFragment<StudyProject, StudyProjectViewModel>() {

    companion object {
        fun getInstance(bundle: Bundle? = null): StudyProjectFragment {
            val fragment = StudyProjectFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataListener()
    }

    private fun initDataListener() {
        (mViewModel as StudyProjectViewModel).outPlanBean.observe(this, Observer {
            if (it.code == 10002) {
                toast(it.message)
            } else {
                if (it.join_status == 0) {//删除并刷新列表
                    if (currentOutPlanBean != null) {
                        mAdapter?.remove(currentOutPlanBean!!)
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    var currentOutPlanBean: StudyProject? = null
    override fun initAdapter(): BaseQuickAdapter<StudyProject, BaseViewHolder>? {
        return (mViewModel as StudyProjectViewModel).getPageData().value?.let {
            val studyProjectAdapter = StudyProjectAdapter(it)
            studyProjectAdapter.addChildClickViewIds(R.id.tvEixt)
            studyProjectAdapter.setOnItemClickListener { _, _, position ->
                ARouter.getInstance()
                        .build(Paths.PAGE_STUDYPROJECT)
                        .withString(IntentParamsConstants.STUDYPROJECT_PARAMS_ID, studyProjectAdapter.data[position].id.toString()
                        ).navigation()
            }
            studyProjectAdapter.setOnItemChildClickListener { _, view, position ->
                if (view.id == R.id.tvEixt) {
                    //点击退出学习项目
                    currentOutPlanBean = studyProjectAdapter.data.get(position)
                    val studyProject = it[position]
                    val planEndtime = studyProject.plan_endtime_ts * 1000
                    val computeTime = studyProject.compute_time * 1000
                    if (System.currentTimeMillis() > planEndtime) {
                        toast("当前时间学习项目已经结束不能退出")
                        return@setOnItemChildClickListener
                    }

                    if (System.currentTimeMillis() > computeTime) {
                        toast("项目积分结算期，不能退出项目")
                        return@setOnItemChildClickListener
                    }

                    //
                    showProjectExitPop(studyProject)
                }
            }
            studyProjectAdapter
        }
    }

    /**
     * 学习项目退出弹窗
     */
    private fun showProjectExitPop(studyProject: StudyProject) {
        //学习项目退出弹窗
        var studyPlanCheckDialog = activity?.let { StudyPlanChekDialog(it) }
        studyPlanCheckDialog?.onOkClickListener = {
            //退出项目
            (mViewModel as StudyProjectViewModel).postOutStudyPlan(studyProject.id.toString())
        }
        XPopup.Builder(requireContext())
                .asCustom(studyPlanCheckDialog)
                .show()

    }

    override fun neadLoadMore(): Boolean = false
}