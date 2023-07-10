package com.mooc.home.ui.todaystudy.adjusttarget

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.lxj.xpopup.XPopup
import com.mooc.home.R
import com.mooc.home.model.todaystudy.TargetDetial

/**
 * 调整目标fragment
 */
class AdjustTargetFrgment : BaseListFragment<TargetDetial,TargetAdjustViewModel>() {
    override fun initAdapter(): BaseQuickAdapter<TargetDetial, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let {
            val adjustTargetAdapter = AdjustTargetAdapter(it)
            adjustTargetAdapter.addChildClickViewIds(R.id.btAdjust,R.id.ivSwitch)
            adjustTargetAdapter.setOnItemChildClickListener { _, view, position ->
                setItemChildClick(view,it[position])
            }
            adjustTargetAdapter
        }
    }

    private fun setItemChildClick(view: View, targetDetial: TargetDetial) {
        if (view.id == R.id.btAdjust) { //点击调整目标
            showAdajustDialog(targetDetial)
            return
        }
        if (view.id == R.id.ivSwitch) { //点击开关
            //同步样式和上报
            targetDetial.is_open = if(targetDetial.is_open == 1) 0 else 1
            mAdapter?.notifyDataSetChanged()
            mViewModel?.postTargetAdjust(targetDetial)
            return
        }
    }

    /**
     * 展示调整目标弹窗
     */
    private fun showAdajustDialog(targetDetial: TargetDetial) {
        val courseChoosePop = AdjustTargetPop(requireContext())
        courseChoosePop.targetDetial = targetDetial
        courseChoosePop.onConfirm = {
            //点击确定上报调整，更新列表
            mAdapter?.notifyDataSetChanged()
            mViewModel?.postTargetAdjust(it)
        }
        XPopup.Builder(requireContext())
                .asCustom(courseChoosePop)
                .show()
    }

    override fun neadLoadMore(): Boolean  = false

}