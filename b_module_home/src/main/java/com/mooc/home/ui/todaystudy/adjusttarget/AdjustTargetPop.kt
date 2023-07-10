package com.mooc.home.ui.todaystudy.adjusttarget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.lxj.xpopup.core.BottomPopupView
import com.mooc.home.R
import com.mooc.home.databinding.HomeDialogTargetAdjustBinding
import com.mooc.home.model.todaystudy.TargetDetial
//import kotlinx.android.synthetic.main.home_dialog_target_adjust.view.*

/**
 * 调整目标弹窗
 */
class AdjustTargetPop(context: Context) : BottomPopupView(context) {

    var onConfirm: ((detail: TargetDetial) -> Unit)? = null
    var targetDetial: TargetDetial? = null

    val minStudy = 1      //最小学习
    var maxStudy = 5     //最大学习
    var currentStudy = 0
    var isOpen = true //是否打开提醒

    private lateinit var inflater: HomeDialogTargetAdjustBinding
    override fun getImplLayoutId(): Int {
        return R.layout.home_dialog_target_adjust
    }

    override fun onCreate() {
        super.onCreate()

        inflater = HomeDialogTargetAdjustBinding.bind(popupImplView)
        inflater.ivClose.setOnClickListener {
            dismiss()
        }
        //点击调整目标，将调整参数回传
        inflater.tvOk.setOnClickListener {

            targetDetial?.let { detail ->
                detail.is_open = if (isOpen) 0 else 1
                detail.resource_num = currentStudy
                onConfirm?.invoke(detail)
            }
            dismiss()
        }

        //初始化数据
        targetDetial?.apply {
            inflater.tvHeadName.setText(AdjustTargetAdapter.resourceTitleMap[this.resource_type])
            inflater.tvTitle.setText(this.colum_name)
            inflater.tvTitle.visibility = if (this.colum_name.isEmpty()) View.GONE else View.VISIBLE
            inflater.tvContent.setText("每日学习${this.resource_num}条")

            currentStudy = this.resource_num
            isOpen = this.is_open == 1

            if (limit_num != -1) {
                maxStudy = limit_num
            }
            setToggleButtonRes()
            setAddOrReduceRes()

            //设置开关
            inflater.toggleButton.setOnClickListener {
                isOpen = !isOpen
                setToggleButtonRes()
            }


            //点击确定同步设置的数据
            inflater.tvOk.setOnClickListener {
                this.resource_num = currentStudy
                this.is_open = if (isOpen) 1 else 0
                onConfirm?.invoke(this)
                dismiss()
            }


            //点击加减
            inflater.ivAdd.setOnClickListener {
                if (currentStudy < maxStudy) {
                    currentStudy++
                }
                setAddOrReduceRes()
            }

            inflater.ivReduce.setOnClickListener {
                if (currentStudy > minStudy) {
                    currentStudy--
                }
                setAddOrReduceRes()
            }
        }

    }


    private fun setAddOrReduceRes() {
        if (!isOpen) {
            inflater.tvCount.setText(currentStudy.toString())
            return
        }
        if (currentStudy >= maxStudy) {
            inflater.ivAdd.setImageResource(R.mipmap.home_ic_gray_add)
            inflater.ivAdd.isEnabled = false
        } else if (currentStudy <= minStudy) {
            inflater.ivReduce.setImageResource(R.mipmap.home_ic_gray_reduce)
            inflater.ivReduce.isEnabled = false
        } else {
            inflater.ivAdd.setImageResource(R.mipmap.home_ic_green_add)
            inflater.ivReduce.setImageResource(R.mipmap.home_ic_green_reduce)
            inflater.ivReduce.isEnabled = true
            inflater.ivAdd.isEnabled = true

        }
        inflater.tvCount.setText(currentStudy.toString())
    }

    /**
     * 设置开关资源
     */
    private fun setToggleButtonRes() {
        val switchRes = if (isOpen) R.mipmap.home_ic_switch_close else R.mipmap.home_ic_switch_open
        inflater.toggleButton.setImageResource(switchRes)

        //设置消息不提醒，将+，-按钮置灰，并不可点击
        if (isOpen) {
            inflater.ivAdd.setImageResource(R.mipmap.home_ic_green_add)
            inflater.ivReduce.setImageResource(R.mipmap.home_ic_green_reduce)
            inflater.ivReduce.isEnabled = true
            inflater.ivAdd.isEnabled = true
        } else {
            inflater.ivAdd.setImageResource(R.mipmap.home_ic_gray_add)
            inflater.ivAdd.isEnabled = false
            inflater.ivReduce.setImageResource(R.mipmap.home_ic_gray_reduce)
            inflater.ivReduce.isEnabled = false
        }
    }
}